import java.io.*;

public class Simulator {
    private static int[] register_file=new int[16];
    static int N, C, V, Z;
    private static String[] ins_MEM=new String[1000];
    private static int[][] data_MEM=new int[16][4096];
    private static Instruction ins;

    private static void reset_proc() {
        for(int i=0;i<16;i++)
            register_file[i]=0;
        for(int i=0;i<1000;i++)
            ins_MEM[i]=null;
//        for(int i=0;i<16;i++)
//            for (int j = 0; j < 4096; j++)
//                data_MEM[i][j] = 0;
        N=C=V=Z=0;
    }

    private static void write_word(int address, String instruction) {
        int index=address/4;
        ins_MEM[index]=instruction;
    }

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

    private static String read_word(int address) {
        int index=address/4;
        return ins_MEM[index];
    }

    private static void write_data_memory() {
        try {
            PrintWriter writer=new PrintWriter("data_out.mem");
            for(int i=0;i<1000;i++) {
                writer.printf("%s %s\n", Integer.toHexString(i), ins_MEM[i]);
            }
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    static void swi_exit() {
        System.out.println("EXIT");
        write_data_memory();
        System.exit(0);
    }

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

    private static void fetch() {
        String instruction=read_word(register_file[15]);
        ins=new Instruction(Long.decode(instruction));

        if(ins.getName()==null)
            return;

        System.out.printf("FETCH: Instruction %s from address 0x%s\n", instruction, Integer.toHexString(register_file[15]));
        register_file[15]+=4;
    }

    private static void decode() {
        ins.decode(register_file);
    }

    private static int execute() {
        return ins.execute(register_file, data_MEM);
    }

    private static int mem(int result) {
        return ins.memory(data_MEM, result);
    }

    private static void write_back(int result) {
        ins.write_back(register_file, result);
    }

    public static void main(String[] args) {
        String file=args[0];

        Instruction.deserialize();

        reset_proc();

        load_program_memory(file);

        run_armsim();
    }
}
