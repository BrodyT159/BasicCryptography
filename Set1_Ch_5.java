import java.util.HexFormat;

// https://cryptopals.com/sets/1/challenges/5
public class Set1_Ch_5 {
    public static void main(String[] args) {
        System.out.println();

        String s = "Burning 'em, if you ain't quick and nimble\nI go crazy when I hear a cymbal";

        System.out.println("Converted Hex Value With Key: " + repeatXOR(s, "ICE"));
        System.out.println("""
                           Answer: 0b3637272a2b2e63622c2e69692a23693a2a3c6324202d623d63343c2a26226324272765272
                           a282b2f20430a652e2c652a3124333a653e2b2027630c692b20283165286326302e27282f""" //
        );

        System.out.println();
    }

    public static String repeatXOR(String string, String xorAgainst) {
        
        byte[] byteHex = string.getBytes();

        byte[] byteInt = xorAgainst.getBytes();
        
        int count = 0;
        byte[] convertedByte = new byte[byteHex.length];
        for (int i = 0; i < byteHex.length; i++) {
            convertedByte[i] = (byte)(byteHex[i] ^ byteInt[count]);
            if (count >= 2) {
                count = 0;
            } else {
                count++;
            }
        }

        String hexText = HexFormat.of().formatHex(convertedByte);
        return hexText;
    }
}