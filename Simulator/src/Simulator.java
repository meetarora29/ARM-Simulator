import java.io.*;

/**
 * A simulator for the ARM Instruction Set Architecture.
 * All the basic commands are supported.
 *
 * @author Meet
 * @author Gagan
 */
public class Simulator {
    private static int[] register_file=new int[16];
    static int N, C, V, Z;
    private static String[] ins_MEM=new String[4000];
    private static int[][] data_MEM=new int[16][4096];
    private static Instruction ins;

    /**
     * Resets the simulator.
     * Resets all values to 0 or null.
     */
    private static void reset_proc() {
        for(int i=0;i<16;i++)
            register_file[i]=0;
        for(int i=0;i<4000;i++)
            ins_MEM[i]=null;
//        for(int i=0;i<16;i++)
//            for (int j = 0; j < 4096; j++)
//                data_MEM[i][j] = 0;
        N=C=V=Z=0;
    }

    /**
     * Writes the instruction to the instruction memory.
     * @param address
     * @param instruction
     */
    private static void write_word(int address, String instruction) {
        ins_MEM[address]=instruction;
    }

    /**
     * Reads the input memory and populates the instruction memory.
     * @param file
     */
    private static void load_program_memory(String file) {
        try {
            BufferedReader bufferedReader=new BufferedReader(new FileReader(file));
            String line;
            while((line=bufferedReader.readLine()) != null) {
                String[] input=line.split(" ");
                int address=Integer.decode(input[0]);
                String instruction=input[1];

                write_word(address, instruction);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Reads the instruction from the instruction memory.
     * @param address
     * @return instruction
     */
    private static String read_word(int address) {
        return ins_MEM[address];
    }

    /**
     * Writes the data memory in "data_out.mem" file.
     */
    private static void write_data_memory() {
        try {
            PrintWriter writer=new PrintWriter("data_out.mem");
            for(int i=0;i<16;i+=1) {
                for(int j=0;j<1024;j++)
                    writer.printf("%s %s\n", Integer.toHexString(i+4*j), data_MEM[i][j]);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Writes the ins memory in "ins_out.mem" file.
     */
    private static void write_ins_memory() {
        try {
            PrintWriter writer=new PrintWriter("ins_out.mem");
            for(int i=0;i<4000;i+=4) {
                if(ins_MEM[i]==null)
                    ins_MEM[i]="0x00000000";
                writer.printf("%s %s\n", Integer.toHexString(i), ins_MEM[i]);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * Exits the program.
     */
    static void swi_exit() {
        System.out.println("EXIT");
        write_data_memory();
        write_ins_memory();
        System.exit(0);
    }

    /**
     * Runs the simulator.
     */
    private static void run_armsim() {
        while(true) {
            fetch();
            if(ins.getName()==null)
                break;

            decode();

            int result=execute();

            result=mem(result);

            write_back(result);

            System.out.println();
        }
    }

    /**
     * Reads the instruction memory and updates the instruction register.
     */
    private static void fetch() {
        String instruction=read_word(register_file[15]);
        ins=new Instruction(Long.decode(instruction));

        if(ins.getName()==null)
            return;

        System.out.printf("FETCH: Instruction %s from address 0x%s\n", instruction, Integer.toHexString(register_file[15]));
        register_file[15]+=4;
    }

    /**
     * Reads the instruction register, reads operand1, operand2 from register file,
     * decides the operation to be performed in execute stage.
     */
    private static void decode() {
        ins.decode(register_file);
    }

    /**
     * Executes the ALU operation based on ALUop and other fields.
     * @return result
     */
    private static int execute() {
        return ins.execute(register_file, data_MEM);
    }

    /**
     * Performs the memory operation.
     * @param result
     * @return result
     */
    private static int mem(int result) {
        return ins.memory(data_MEM, result, register_file);
    }

    /**
     * Writes the results back to the register file.
     * @param result
     */
    private static void write_back(int result) {
        ins.write_back(register_file, result);
    }

    public static void main(String[] args) {
        String file=args[0];

        if (file == null) {
            System.out.printf("Invoke Correctly.");
            System.exit(0);
        }

        Instruction.deserialize();

        reset_proc();

        load_program_memory(file);

        run_armsim();
    }
}
