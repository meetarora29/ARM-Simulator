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
        return (this.specification == specification && (this.opcode==opcode || this.condition==condition));
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

    private Instruction(Long instruction) {
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
        if(name.equals(""))
            System.out.println("Instruction Not Supported\n\n");
        System.out.println(this);
    }

    void decode(int[] register_file) {
        String one="DECODE: Operation is %s, First Operand is R%d, Second Operand is R%d, Destination Register is R%d \nDECODE: Read Registers- R%d = %d, R%d = %d\n";
        String two="DECODE: Operation is %s, First Operand is R%d, Second Immediate Operand is %d, Destination Register is R%d \nDECODE: Read Registers- R%d = %d, R%d = %d\n";
        String ldr="DECODE: Operation is %s, Base Register is R%d, Destination Register is R%d, Offset is %d \nDECODE: Read Register- R%d = %d\n";
        String str="DECODE: Operation is %s, Base Register is R%d, Register whose value is to be stored in memory is R%d, Offset is %d \nDECODE: Read Registers- R%d = %d, R%d = %d\n";
        String branch="DECODE: Operation is %s\n";

        if(specification==0) {
            if(immediate==0)
                System.out.printf(one, name, operand1, operand2, destination, operand1, register_file[operand1], operand2, register_file[operand2]);
            else
                System.out.printf(two, name, operand1, operand2, destination, operand1, register_file[operand1], operand2, register_file[operand2]);
        }
        else if(specification==1) {
            if(name.equals("LDR"))
                System.out.printf(ldr, name, operand1, destination, operand2, operand1, register_file[operand1]);
            else if(name.equals("STR"))
                System.out.printf(str, name, operand1, destination, operand2, operand1, register_file[operand1], destination, register_file[destination]);
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
                    // TODO: Set Z = 1
                    return 0;
                }
                if (a < b) {
                    // TODO: Set N = 1
                    return -1;
                }
                return 1;
            case "|":
                return a | b;
            case "~":
                return ~b;
        }
        return -1;
    }

    int execute(int[] register_file) {
        String one="EXECUTE: %s %d and %d\n";
        String mov="EXECUTE: %s value of R%d to R%d\n";
        if(specification==0) {
            if(opcode==13 || opcode==15)
                System.out.printf(mov, operand2, destination);
            else {
                if (immediate == 0)
                    System.out.printf(one, name, register_file[operand1], register_file[operand2]);
                else
                    System.out.printf(one, name, register_file[operand1], operand2);
            }
            return compute(register_file);
        }
        else if(specification==1) {

        }
        return -1;
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

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner Reader=new Scanner(System.in);

        Main.deserialize(presetInstructions);

        Instruction instruction=new Instruction(Long.decode(Reader.next()));
        instruction.decode(new int[16]);
    }
}
