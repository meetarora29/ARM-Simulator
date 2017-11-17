import java.io.*;
import java.math.BigInteger;

import static java.lang.Math.pow;

/*
condition-31 to 28
 */
public class Simulator {
//Register file
    static  int[] R;
    //flags
    static int N,C,V,Z;
//memory
    static  char[] MEM;

//intermediate datapath and control path signals
//    static  int inst;
    static  int operand1;
    static  int operand2;
    static  int destination;
//    static  int PC;
    static int offset;
//    static  int address = 0x0;
    static  int flag;
    static  int specification;
    static  int opcode;
//    static  int result;
    static  int cond;
    static  int immediate;
    static  int[] R0,R1,R2,R3,R4,R5,R6,R7,R8,R9,R10,R11,R12,R13,R14,R15;
//    static String[][] split=new String[1000][1000];
//    static int index1=0,index2=0;
//    static int index=0;

    void reset_proc() {
        int i;
        for(i=0;i<16;i++)
            R[i] = 0;
        for(i=0;i<4000;i++)
            MEM[i] = '0';

        operand1=destination=flag=opcode=immediate=cond =0; operand2 = 0;
        N = C = V = Z = 0;
        //*(to_be) = 0;
    }
    void write_word(char[] mem, int address,int data)
    {

    }
    static void fetch() throws IOException {
        BufferedReader file=null;
        try {
            file=new BufferedReader(new FileReader("/Users/gagan/Desktop/CS112-Project/test/simple_add.mem"));
            String l;
            while ((l=file.readLine())!=null)
            {
                String[] s=l.split("[\\s]+");
                String str=hexToBin(s[1]);

                System.out.println(" Fetch instruction "+s[1]+" from address "+s[0]);
                decode(str);

            }
        }finally {
            if(file!=null)
            {
                file.close();
            }
        }
    }
    private static String hexToBin(String hex){
        String bin = "";
        String binFragment = "";
        int iHex;
        hex = hex.trim();
        hex = hex.replaceFirst("0x", "");

        for(int i = 0; i < hex.length(); i++){
            iHex = Integer.parseInt(""+hex.charAt(i),16);
            binFragment = Integer.toBinaryString(iHex);

            while(binFragment.length() < 4){
                binFragment = "0" + binFragment;
            }
            bin += binFragment;
        }
        return bin;
    }
    static Long convert(String s)
    {
        char[] arr=s.toCharArray();
        Long inst=(long)0;
        for(int i=arr.length-1;i>=0;i--) {
            inst = (long) (inst + (arr[i] * pow(2, arr.length - i)));
        }
        return  inst;
    }
    static void decode(String s)
    {

        Long instruction;
//        System.out.println(s);
        instruction=convert(s);
//        System.out.println(instruction);

        // Bits 28-31
        cond=(int)(instruction>>28)&(0xF);
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
                if(opcode==0){
                    System.out.println("DECODE: Operation is AND, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]+", R"+operand2+" = "+R[operand2]);
                }
                else if(opcode==1)
                    System.out.println("DECODE: Operation is XOR, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]+", R"+operand2+" = "+R[operand2]);
                else if(opcode==2)
                    System.out.println("DECODE: Operation is SUB, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]+", R"+operand2+" = "+R[operand2]);
                else if(opcode==4)
                    System.out.println("DECODE: Operation is ADD, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]+", R"+operand2+" = "+R[operand2]);
                else if(opcode==5)
                    System.out.println("DECODE: Operation is ADC, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]+", R"+operand2+" = "+R[operand2]);
                else if(opcode==10)
                    System.out.println("DECODE: Operation is CMP, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]+", R"+operand2+" = "+R[operand2]);
                else if(opcode==12)
                    System.out.println("DECODE: Operation is ORR, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]+", R"+operand2+" = "+R[operand2]);
                else if(opcode==13)
                    System.out.println("DECODE: Operation is MOV, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]+", R"+operand2+" = "+R[operand2]);
                else if(opcode==15)
                    System.out.println("DECODE: Operation is MNV, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]+", R"+operand2+" = "+R[operand2]);
            }
            else {
                // Bits 0-8
                operand2 =  (instruction.intValue()) & (0xFF);
                if(opcode==0){
                    System.out.println("DECODE: Operation is AND, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]);
                }
                else if(opcode==1)
                    System.out.println("DECODE: Operation is XOR, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]);
                else if(opcode==2)
                    System.out.println("DECODE: Operation is SUB, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]);
                else if(opcode==4)
                    System.out.println("DECODE: Operation is ADD, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]);
                else if(opcode==5)
                    System.out.println("DECODE: Operation is ADC, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]);
                else if(opcode==10)
                    System.out.println("DECODE: Operation is CMP, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]);
                else if(opcode==12)
                    System.out.println("DECODE: Operation is ORR, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]);
                else if(opcode==13)
                    System.out.println("DECODE: Operation is MOV, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]);
                else if(opcode==15)
                    System.out.println("DECODE: Operation is MNV, First Operand is R"+operand1+", Second Operand is R"+operand2+", Destination Register is R"+destination+".\nRead Registers: R"+operand1+" ="+R[operand1]);

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
            if(opcode==25)
            {

                System.out.println("DECODE: Operation is LDR, Base Register is R"+operand1+", Offset is "+operand2+", Destination Register is R"+destination);
            }
            else if(opcode==24)
            {

                System.out.println("DECODE: Operation is STR, Base Register is R"+operand1+", Offset is "+operand2+", Register to be stored in memory is R"+destination+".\n,Read Register: R"+destination+" = "+R[destination]);
            }
        }
        else if(specification==2) {
            // Bits 24
            opcode = (int) (instruction >> 24) & (0x1);
            // Bits 0-23
            offset = (instruction.intValue()) & (0xFFFFFF);
            if(opcode==2){
                if(cond==0){
                   System.out.println("DECODE: Operation is BEQ");
                }
                else if(cond==1) System.out.println("DECODE: Operation is BNE");
                else if(cond==11) System.out.println("DECODE: Operation is BLT");
                else if(cond==13) System.out.println("DECODE: Operation is BLE");
                else if(cond==12) System.out.println("DECODE: Operation is BGT");
                else if(cond==10) System.out.println("DECODE; Operation is BGE");
                else if(cond==14) System.out.println("DECODE: Operation is BAL");
            }
        }


    }
    static void execute()
    {

    }
    static void mem()
    {

    }
    static void write_back()
    {

    }
    public static void main(String[] args) throws IOException {
        fetch();

    }

}
