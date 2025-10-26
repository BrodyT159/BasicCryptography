import java.math.BigInteger;
import java.util.HexFormat;

// https://cryptopals.com/sets/1/challenges/2
public class Set1_Ch_2 {
    public static void main(String[] args) {
        // Spacing
        System.out.println();

        // Starting String
        String s = "1c0111001f010100061a024b53535009181c";
        String xorValue = "686974207468652062756c6c277320657965";

        System.out.println("Converted Value: " + fixedXOR(s, xorValue));
        
        // Should Produce:
        String answer = "746865206b696420646f6e277420706c6179";
        System.out.println("Should be: " + answer);
        // Spacing
        System.out.println();
    }

    public static String fixedXOR(String string, String xorAgainst) {
        // BigInteger(String val, int radix)
        // Translates the String representation of a BigInteger in the specified radix into a BigInteger.
        // "radix" refers to the base of a numeral system
        BigInteger hexValue = new BigInteger(string, 16);
        // Value to be XOR against converting to hex
        BigInteger xorValue = new BigInteger(xorAgainst, 16);
        
        hexValue = hexValue.xor(xorValue);
        byte[] rawByte = hexValue.toByteArray();
        return HexFormat.of().formatHex(rawByte);

    }
}