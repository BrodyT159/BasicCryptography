import java.math.BigInteger;
import java.nio.charset.StandardCharsets;

// https://cryptopals.com/sets/1/challenges/3
// Answer: 88
public class Set1_Ch_3 {
    public static void main(String[] args) {
        // Spacing
        System.out.println();

        // Starting String
        String s = "1b37373331363f78151b7f2b783431333d78397828372d363c78373e783a393b3736";
        
        // Int value is interated to find the Key
        // The Int value in which xor against the Hex String to find the message
        for (int i = 0; i < 256; i++) {
            System.out.println("Key(" + i + ") Converted Value: " + xorCipher(s, i));
        }

        // Spacing
        System.out.println();
    }

    public static String xorCipher(String string, int xorAgainst) {
        // Conversion of Hex String to BigInteger
        // BigInteger can be broken down to byte arrays
        BigInteger hexValue = new BigInteger(string, 16);
        byte[] byteHex = hexValue.toByteArray();

        // Conversion of int value to byte value
        // Since its a int limited to 255, it can be represented with one byte, not an array
        byte byteInt = (byte) xorAgainst;
        

        // Create a blank byte array for storing after xor operations
        // Since the int is only going to 255 then its byte Array has one element
        byte[] convertedByte = new byte[byteHex.length];
        for (int i = 0; i < byteHex.length; i++) {
            // Add the byte value to byte Array when the Hex Byte Array is xor against byteInt
            // Operator ^ means xor, and then it's all casted to a byte value
            convertedByte[i] = (byte)(byteHex[i] ^ byteInt);
        }


        // Converts the byte array to String characters using String object
        String text = new String(convertedByte, StandardCharsets.UTF_8);
        return text;
    }
}

/*
 * -----------------------------------------------------------------
 * HOW TO DO SINGLE-BYTE XOR BY HAND
 * -----------------------------------------------------------------
 *
 * GOAL: XOR the first byte '1b' with the key 'X' (which is 88)
 *
 * --- WHAT IS XOR? ---
 * XOR (Exclusive OR) is a bitwise operation.
 * It compares two bits:
 * - If the bits are DIFFERENT, the result is 1.
 * - If the bits are the SAME, the result is 0.
 *
 * 0 XOR 0 = 0
 * 1 XOR 1 = 0
 * 1 XOR 0 = 1
 * 0 XOR 1 = 1
 *
 * --- STEP 1: CONVERT TO BINARY (8 bits / 1 byte) ---
 *
 * 1. Ciphertext Byte: '1b'
 * '1' -> 0001
 * 'b' (11) -> 1011
 * '1b' = 0001 1011
 *
 * 2. Key Byte: 'X'
 * 'X' -> 88 (from ASCII table)
 * 88 = (64 + 16 + 8)
 * 'X' = 0101 1000
 *
 * --- STEP 2: PERFORM THE XOR (BIT BY BIT) ---
 *
 * 0001 1011   (This is '1b')
 * XOR
 * 0101 1000   (This is 'X' or 88)
 * -----------------
 * 0100 0011   (This is the result)
 *
 *
 * --- STEP 3: CONVERT RESULT BACK TO A CHARACTER ---
 *
 * 1. Binary -> Decimal:
 * The result is 0100 0011
 * = (0*128) + (1*64) + (0*32) + (0*16) + (0*8) + (0*4) + (1*2) + (1*1)
 * = 64 + 2 + 1
 * = 67
 *
 * 2. Decimal -> ASCII Character:
 * Decimal value 67 is the ASCII character 'C'
 *
 * This 'C' is the first letter of "Cooking MC's like a pound of bacon".
 * You repeat this process for every byte in the hex string.
 */