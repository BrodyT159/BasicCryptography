
import java.math.BigInteger;
import java.util.Base64;

// https://cryptopals.com/sets/1/challenges/1
public class Set1_Ch_1 {
    public static void main(String[] args) {
        // Spacing
        System.out.println();

        // Starting String
        String s = "49276d206b696c6c696e6720796f757220627261696e206c696b65206120706f69736f6e6f7573206d757368726f6f6d";

        System.out.println("Converted Value: " + conversionHexBase64(s));
        // Should Produce:
        String answer = "SSdtIGtpbGxpbmcgeW91ciBicmFpbiBsaWtlIGEgcG9pc29ub3VzIG11c2hyb29t";
        System.out.println("Should be: " + answer);
        // Spacing
        System.out.println();
    }

    public static String conversionHexBase64(String string) {
        // BigInteger(String val, int radix)
        // Translates the String representation of a BigInteger in the specified radix into a BigInteger.
        // "radix" refers to the base of a numeral system
        BigInteger hexValue = new BigInteger(string, 16);

        // Takes the BigInteger object in hex and converts it to raw byte arrays
        byte[] rawByte = hexValue.toByteArray();

        // Using Base64, an encoder can be obtained which has a method to convert byte arrays to 64 base String
        return Base64.getEncoder().encodeToString(rawByte);

    }
}