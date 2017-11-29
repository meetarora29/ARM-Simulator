import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Main class is used just for serializing the Instruction class to maintain a database
 * for the supported instruction set.
 */
public class Main {
    static void deserialize(ArrayList<PresetInstruction> PresetInstructions) throws IOException, ClassNotFoundException {
        InputStream inputStream=new FileInputStream("PresetInstructions.dat");
        ObjectInputStream in=null;
        try {
            in=new ObjectInputStream(inputStream);
            try {
                while (true) {
                    PresetInstruction presetInstruction = (PresetInstruction) in.readObject();
                    PresetInstructions.add(presetInstruction);
                }
            }
            catch (EOFException e) {

            }
        }
        finally {
            if(in!=null)
                in.close();
        }
    }

    private static void serialize(ArrayList<PresetInstruction> PresetInstructions) throws IOException {
        OutputStream outputStream=new FileOutputStream("PresetInstructions.dat");
        ObjectOutputStream out=null;
        try {
            out = new ObjectOutputStream(outputStream);
            for (PresetInstruction presetInstruction : PresetInstructions)
                out.writeObject(presetInstruction);
        }
        finally {
            if(out!=null)
                out.close();
        }
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        Scanner Reader=new Scanner(System.in);
        ArrayList<PresetInstruction> presetInstructions=new ArrayList<>();

        int flag;
        System.out.println("Do you want to add PresetInstructions? Enter 1 for yes.");
        flag=Reader.nextInt();
        while(flag==1) {
            String name=Reader.next();
            int specification=Reader.nextInt();
            int opcode=Reader.nextInt();
            int condition=-1;
            if(specification==2)
                condition=Reader.nextInt();
            String operator="";
            if(specification==0)
                operator=Reader.next();
            presetInstructions.add(new PresetInstruction(name, specification, opcode, condition, operator));
            System.out.println("Do you want to continue? Enter 1 for yes.");
            flag=Reader.nextInt();
        }

        serialize(presetInstructions);
        presetInstructions=new ArrayList<>();
        deserialize(presetInstructions);
        for(PresetInstruction i:presetInstructions)
            System.out.println(i);
    }
}
