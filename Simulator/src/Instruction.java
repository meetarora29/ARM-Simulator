import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Scanner;

class PresetInstruction implements Serializable {
    String name;
    private int opcode, specification, condition;
    String operator;

    static final long serialVersionUID=1L;

    PresetInstruction(String name, int specification, int opcode, int condition, String operator) {
        this.name=name;
        this.opcode=opcode;
        this.specification=specification;
        this.condition=condition;
        this.operator=operator;
    }

    boolean equals(int specification, int condition, int opcode) {
        if (this.specification == specification && this.opcode == opcode) {
            if(specification==2 && this.condition==condition) // Branch
                return true;
            else if(specification!=2)
                return true;
        }
        return false;
    }

    @Override
    public String toString() {
        return name;
    }
}

public class Instruction {
    private static ArrayList<PresetInstruction> presetInstructions=new ArrayList<>();

    private Long instruction;
    private int condition, specification, opcode, immediate, operand1, operand2, destination, offset;
    private String name, operator;

    String getName() {
        return name;
    }

    Instruction(Long instruction) {
        condition=-1;
        specification=-1;
        opcode=-1;
        immediate=-1;
        operand1=-1;
        operand2=-1;
        destination=-1;
        offset=-1;

        this.instruction=instruction;

        // Bits 28-31
        condition=(int)(instruction>>28)&(0xF);
        // Bits 26-27
        specification=(int)(instruction>>26)&(0x3);
        // Bit 25
        immediate=(int)(instruction>>25)&(0x1);

        if(specification==0) {
            // Bits 21-25
            opcode = (int) (instruction >> 21) & (0xF);
            // Bits 16-19
            operand1 = (int) (instruction>>16) & (0xF);
            // Bits 12-15
            destination = (int) (instruction>>12) & (0xF);

            if(immediate==0) {
                // Bits 0-3
                operand2 = (instruction.intValue()) & (0xF);
            }
            else {
                // Bits 0-8
                operand2 =  (instruction.intValue()) & (0xFF);
            }
        }
        else if(specification==1) {
            // Bits 20-25
            opcode = (int) (instruction >> 20) & (0x1F);
            // Bits 16-19
            operand1 = (int) (instruction >> 16) & (0xF);
            // Bits 0-12
            operand2 = (instruction.intValue()) & (0xFFF);
            // Bits 12-15
            destination = (int) (instruction>>12) & (0xF);
        }
        else if(specification==2) {
            // Bits 24
            opcode = (int) (instruction >> 24) & (0x1);
            // Bits 0-23
            offset = (instruction.intValue()) & (0xFFFFFF);
        }

        for(PresetInstruction presetInstruction:presetInstructions) {
            if(presetInstruction.equals(specification, condition, opcode)) {
                name=presetInstruction.name;
                operator=presetInstruction.operator;
                break;
            }
        }
        if(name==null)
            System.out.println("Instruction Not Supported\n\n");
//        System.out.println(this);
    }

    void decode(int[] register_file) {
        String one="DECODE: Operation is %s, First Operand is R%d, Second Operand is R%d, Destination Register is R%d \nDECODE: Read Registers- R%d = %d, R%d = %d\n";
        String two="DECODE: Operation is %s, First Operand is R%d, Second Immediate Operand is %d, Destination Register is R%d \nDECODE: Read Registers- R%d = %d\n";
        String ldr="DECODE: Operation is %s, Base Register is R%d, Destination Register is R%d, Offset is %d \nDECODE: Read Register- R%d = %d\n";
        String str="DECODE: Operation is %s, Base Register is R%d, Register whose value is to be stored in memory is R%d, Offset is %d \nDECODE: Read Registers- R%d = %d, R%d = %d\n";
        String branch="DECODE: Operation is %s\n";

        if(specification==0) {
            if(immediate==0)
                System.out.printf(one, name, operand1, operand2, destination, operand1, register_file[operand1], operand2, register_file[operand2]);
            else
                System.out.printf(two, name, operand1, operand2, destination, operand1, register_file[operand1]);
        }
        else if(specification==1) {
            if(name.equals("LDR")) {
                if(immediate==0)
                    System.out.printf(ldr, name, operand1, destination, operand2, operand1, register_file[operand1]);
                else if(immediate==1)
                    System.out.printf(ldr, name, operand1, destination, register_file[operand2], operand1, register_file[operand1]);
            } else if(name.equals("STR")) {
                if(immediate==0)
                    System.out.printf(str, name, operand1, destination, operand2, operand1, register_file[operand1], destination, register_file[destination]);
                else if(immediate==1)
                    System.out.printf(str, name, operand1, destination, register_file[operand2], operand1, register_file[operand1], destination, register_file[destination]);
            }
        }
        else if(specification==2)
            System.out.printf(branch, name);
    }

    private int compute(int[] register_file) {
        int a=register_file[operand1];
        int b=operand2;
        if(immediate==0)
            b=register_file[operand2];
        switch (operator) {
            case "&":
                return a & b;
            case "^":
                return a ^ b;
            case "-":
                return a - b;
            case "+":
                return a + b;
            case "==":
                if (a == b) {
                    Simulator.Z=1;
                    Simulator.N=0;
                    return 0;
                }
                if (a < b) {
                    Simulator.N=1;
                    Simulator.Z=0;
                    return -1;
                }
                Simulator.Z=0;
                Simulator.N=0;
                return 1;
            case "|":
                return a | b;
            case "~":
                return ~b;
            case ".":
                return b;
        }
        return -1;
    }

    int execute(int[] register_file, int[][] data_MEM) {
        String one="EXECUTE: %s %d and %d\n";
        String mov_one="EXECUTE: %s value of R%d i.e. %d to R%d\n";
        String mov_two="EXECUTE: %s %d to R%d\n";

        if(specification==0) {
            if (immediate == 0) {
                if(opcode==13 || opcode==15)
                    System.out.printf(mov_one, name, operand2, register_file[operand2], destination);
                else
                    System.out.printf(one, name, register_file[operand1], register_file[operand2]);
            }
            else {
                if(opcode==13 || opcode==15)
                    System.out.printf(mov_two, name, operand2, destination);
                else
                    System.out.printf(one, name, register_file[operand1], operand2);
            }

            return compute(register_file);
        }
        else if(specification==1) {
            int index=operand2/4;
            if(opcode==25) {
                if(immediate==0) {
                    System.out.printf("EXECUTE: Load from Data Memory value of %d element from base %d to register R%d\n", index + 1, operand1, destination);
                    return data_MEM[operand1][index];
                }
                else if(immediate==1) {
                    System.out.printf("EXECUTE: Load from Data Memory value of %d element from base %d to register R%d\n", register_file[operand2], operand1, destination);
                    return data_MEM[operand1][register_file[operand2]];
                }
            }
            if(opcode==24) {
                if(immediate==0)
                    System.out.printf("EXECUTE: Store value in register R%d to the %d element from base %d in Data Memory\n", destination, index+1, operand1);
                else if(immediate==1)
                    System.out.printf("EXECUTE: Store value in register R%d to the %d element from base %d in Data Memory\n", destination, register_file[operand2], operand1);
                return -2;
            }
        }
        else if(specification==2) {
            if(opcode==0) {
                int rel;
                int bit=(offset>>23) & (0x1);
                if(bit==1) {
                    rel = (0xFF000000) | (offset * 4);
                } else
                    rel=offset*4;

                System.out.printf("EXECUTE: %s with offset = %d\n", name, offset);

                if(condition==0 && Simulator.Z==1)
                    register_file[15]+=4+rel;
                else if(condition==1 && Simulator.Z!=1)
                    register_file[15]+=4+rel;
                else if(condition==11 && Simulator.N==1 && Simulator.Z==0)
                    register_file[15]+=4+rel;
                else if(condition==12 && Simulator.N==0 && Simulator.Z==0)
                    register_file[15]+=4+rel;
                else if(condition==13 && (Simulator.N==1 || Simulator.Z==1))
                    register_file[15]+=4+rel;
                else if(condition==10 && (Simulator.N==0 || Simulator.Z==1))
                    register_file[15]+=4+rel;
                else if(condition==14)
                    register_file[15]+=4+rel;
            }
        }
        return -1;
    }

    int memory(int[][] data_MEM, int result, int[] register_file) {
        if(specification==3)
            return result;

        if(condition==14) {
            if(opcode==25) {
                if(immediate==0) {
                    System.out.printf("MEMORY: Load value %d from memory\n", data_MEM[operand1][operand2 / 4]);
                    result = data_MEM[operand1][operand2 / 4];
                }
                else if(immediate==1) {
                    System.out.printf("MEMORY: Load value %d from memory\n", data_MEM[operand1][register_file[operand2]]);
                    result = data_MEM[operand1][register_file[operand2]];
                }
            }
            else if(opcode==24) {
                if(result==-2) {
                    result=register_file[destination];
                    System.out.printf("MEMORY: Store value %d in memory\n", result);
                    if(immediate==0)
                        data_MEM[operand1][operand2/4]=result;
                    else if(immediate==1)
                        data_MEM[operand1][register_file[operand2]]=result;
                }
            }
            else
                System.out.println("MEMORY: No memory operation");
        }
        else
            System.out.println("MEMORY: No memory operation");
        return result;
    }

    void write_back(int[] register_file, int result) {
        String write="WRITE-BACK: Write %d to R%d\n";
        String no="WRITE-BACK: No write-back operation";

        if(specification==0) {
            if(opcode==10)
                System.out.println(no);
            else {
                register_file[destination]=result;
                System.out.printf(write, result, destination);
            }
        }
        else if(specification==1) {
            if(opcode==25) {
                register_file[destination]=result;
                System.out.printf(write, result, destination);
            }
            else
                System.out.println(no);
        }
        else if(specification==2)
            System.out.println(no);
        else
            Simulator.swi_exit();
    }

    @Override
    public String toString() {
        String s="Instruction: "+Long.toBinaryString(instruction)+"\n";
        s+="Name: "+name+"\n";
        s+="Operator: "+operator+"\n";
        s+="Condition: "+Integer.toBinaryString(condition)+"\n";
        s+="Specification: "+Integer.toBinaryString(specification)+"\n";
        s+="Opcode: "+Integer.toBinaryString(opcode)+"\n";
        s+="Immediate: "+Integer.toBinaryString(immediate)+"\n";
        s+="Operand 1: "+Integer.toBinaryString(operand1)+"\n";
        s+="Operand 2: "+Integer.toBinaryString(operand2)+"\n";
        s+="Destination Register: "+Integer.toBinaryString(destination)+"\n";
        s+="Offset: "+Integer.toBinaryString(offset);

        return s;
    }

    static void deserialize() {
        try {
            Main.deserialize(presetInstructions);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner Reader=new Scanner(System.in);

        Main.deserialize(presetInstructions);

        Instruction instruction=new Instruction(Long.decode(Reader.next()));
        instruction.decode(new int[16]);
    }
}
