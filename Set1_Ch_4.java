import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Scanner;

// https://cryptopals.com/sets/1/challenges/4
// Answer: String(170) Key(53) Converted Value: Now that the party is jumping
public class Set1_Ch_4 {
    // Throws IOException added for getting File, and it having a chance of error
    public static void main(String[] args) throws IOException {
        // Spacing
        System.out.println();

        // Starting String obtained from text file containing 60-character Strings
        Scanner fileScanner = new Scanner(Paths.get("Set1_Ch_4.txt"));
        ArrayList<String> fileStrings = new ArrayList<String>();

        // Goes through the File until no lines exist
        // Adds each string found to ArrayList using Scanner
        int count = 0;
        while (fileScanner.hasNextLine()) {
            fileStrings.add(fileScanner.nextLine());
            count++;
        }

        String foundString = "";
        
        // Iterate through the amt of Strings in the file and change the String value from ArrayList
        // Int value is interated to find the Key
        // The Int value in which xor against the Hex String to find the message
        for (int i = 0; i < fileStrings.size(); i++) {
            String s = fileStrings.get(i);
            for (int j = 0; j < 256; j++) {
                foundString = xorCipher(s, j);
                if (!"".equals(foundString)) {
                    System.out.println("String(" + i + ") Key(" + j + ") Converted Value: " + foundString);
                }
            }
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

        // Checks if the characters printing are standard to eliminate wrong keys
        // Loop through each character in the text
        // indexOf() returns -1 if the character is NOT FOUND
        // checks if each letter from the text is found in the string of allowed characters
        String acceptableLetters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ,.'`;:!?@#$%^&*()<>/\\\n|{}~-_+=\"";
        for (char letter : text.toCharArray()) {
            if (acceptableLetters.indexOf(letter) == -1) {
                return "";
            }
        }
        return text;
    }
}