import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class temp {

    static byte[] MEM=new byte[4000];

    public static void main(String[] args ) {

        ByteBuffer b=ByteBuffer.allocate(4);
        b.putInt(500);
        byte[] result=b.array();

        for(int i=0;i<4;i++)
            System.out.println(result[i]);

        ByteBuffer w=ByteBuffer.wrap(result);
        System.out.println(w.getInt());

        Long s=Long.decode("0xE3A0200A");
        System.out.println(s);

//        byte[] string=Long.toHexString(s).getBytes(StandardCharsets.US_ASCII);
//        System.out.println(string.length);
//        System.out.println(Long.toHexString(s));
//        char[] chars=Long.toHexString(s).toCharArray();
//        System.out.println(chars.length);
//        String hex=Long.toBinaryString(s);
//        System.out.println(hex);
//        for(int i=0;i<string.length;i++)
//            System.out.println(string[i]);
//        byte[] half=new byte[4];
//        for(int i=0;i<4;i++)
//            half[i]=string[2*i];
//        String h=new String(half);
//        System.out.println(h);
//        String t=Long.toHexString(s);
//        Long temp=Long.parseLong(s, 8);
//        System.out.println(temp);
//        ByteBuffer byteBuffer=ByteBuffer.allocate(4);
//        byteBuffer.putLong(3818921994L);
//        byteBuffer.flip();
//        System.out.println(byteBuffer.getLong());

//        byte[] bytes=new byte[4];
//        bytes[0]=(byte)(s>>>32);
//        bytes[1]=(byte)(s>>>16);
//        bytes[2]=(byte)(s>>>8);
//        bytes[3]=(byte)(s>>>0);

//        ByteBuffer byteBuffer=ByteBuffer.allocate(4);
//        byteBuffer.putInt(Integer.parseUnsignedInt(s.toString()));
//        byte[] r=byteBuffer.array();
//        ByteBuffer wrap=ByteBuffer.wrap(r);
//        System.out.println(wrap.getInt());
//        Integer x=wrap.getInt();
//        System.out.println(x);

        Long one= 16777209L;
        Long two= (long) (16777209 << 2);
        System.out.println(Long.toBinaryString(one));
        System.out.println(Long.toBinaryString(two));
    }
}
