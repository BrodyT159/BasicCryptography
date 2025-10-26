import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Scanner;

// Had to break a weak cipher (repeating-key XOR) by finding the key yourself.
// XOR (Stream Cipher): You operated on one byte at a time.
// https://cryptopals.com/sets/1/challenges/6 (Cyptoanalysis)
public class Set1_Ch_6 {
    public static void main(String[] args) throws IOException {
        System.out.println();

        // Step 1: Read the Base64-encoded file and get the raw ciphertext bytes.
        // (This function is not in this code block, but we assume it's elsewhere)
        byte[] byteText = readFileAsBytes();
        
        // Step 2: Find the most likely keysize (from 2 to 40).
        // (This function is not in this code block, but we assume it's elsewhere)
        int keySize = findBestKeysize(byteText);
        System.out.println("Found best keysize: " + keySize);

        // Step 3: Transpose the ciphertext into 'keySize' number of blocks.
        // All bytes at index 0, 29, 58, etc., go into block 0.
        // All bytes at index 1, 30, 59, etc., go into block 1.
        ArrayList<ArrayList<Byte>> transposedBlocks = keySizeBlocking(keySize, byteText);

        // This array will store the 29 bytes of the final, decrypted key.
        byte[] finalKey = new byte[keySize];

        // Step 4: Solve each transposed block as a single-byte XOR cipher.
        // Loop through each of our 29 blocks.
        for (int i = 0; i < transposedBlocks.size(); i++) {
            
            // These variables will track the best key for *this specific block*.
            int bestScore = -1;
            byte bestKeyByte = 0;

            // This is the Challenge 3 logic: brute-force all 256 possible single-byte keys.
            for (int j = 0; j < 256; j++) {

                // Try decrypting the block with the current key 'j'.
                String unreadableString = singleCharXOR(transposedBlocks.get(i), j);

                // Score the resulting plaintext to see how "English-like" it is.
                int score = scoreText(unreadableString);

                // If this key ('j') gives a better score, save it as the new best.
                if (score > bestScore) {
                    bestScore = score;
                    bestKeyByte = (byte) j;
                }
            }
            // After checking all 256 keys, add the winning key byte to our final key.
            finalKey[i] = bestKeyByte;
        }

        // Convert the final key (e.g., [84, 101, 114, ...]) into a readable string.
        String keyString = new String(finalKey, StandardCharsets.US_ASCII);
        System.out.println("Found Key: " + keyString);

        // Step 5: Decrypt the entire original ciphertext with the key we just found.
        String decryptedString = repeatingKeyXOR(new String(byteText), keyString);
        System.out.println("Decrypted Message: " + decryptedString);

        System.out.println();
    }



    /**
     * Encrypts or decrypts a String with a repeating key.
     * This is the solution to Challenge 5.
     * @param string The plaintext (or ciphertext) String.
     * @param keyXOR The key to use.
     * @return The resulting encrypted or decrypted String.
     */
    public static String repeatingKeyXOR(String string, String keyXOR) {
            
        // Get the raw bytes of the input string.
        byte[] byteHex = string.getBytes();
        // Get the raw bytes of the key.
        byte[] byteKey = keyXOR.getBytes();
        
        // This 'count' will be our index for the key, to make it wrap around.
        int count = 0;
        byte[] convertedByte = new byte[byteHex.length];
        
        // Loop through every byte of the input text.
        for (int i = 0; i < byteHex.length; i++) {
            // XOR the text byte with the *current* key byte.
            convertedByte[i] = (byte)(byteHex[i] ^ byteKey[count]);
            
            // This is the key-wrapping logic.
            // BUG: This is "hard-coded" for a 29-byte key (index 0-28).
            // A more robust solution would be 'count = (count + 1) % byteKey.length;'
            // or just use 'byteKey[i % byteKey.length]' inside the loop.
            if (count >= 28) {
                count = 0; // Reset key index to the beginning
            } else {
                count++; // Move to the next key byte
            }
        }

        // Convert the final array of decrypted bytes back into a String.
        String finalText = new String(convertedByte);
        return finalText;
    }



    /**
     * A simple scoring function to guess how "English-like" a string is.
     * This is a simplified version of the logic from Challenge 3/4.
     * @param s The plaintext string to score.
     * @return An integer score (higher is better).
     */
    public static int scoreText(String s) {
        // This is a simple "allow-list" of characters we expect to see.
        String acceptableLetters = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ ,.'`;!?\n|{}~-_+=\"";
        int scoring = 0;
        
        // Loop through each character of the potential plaintext.
        for (char letter : s.toCharArray()) {
            // Check if the character is in our "good" list.
            if (acceptableLetters.contains(String.valueOf(letter))) {
                scoring++; // Add a point if it's a good character
            }
            // Check if the character is *not* in our "good" list.
            if (acceptableLetters.indexOf(letter) == -1) {
                scoring--; // Subtract a point if it's a "bad" (e.g., control) character
            }
        }
        return scoring;
    }



    /**
     * Decrypts a list of bytes using a single-byte XOR key.
     * @param byteData The block of ciphertext bytes.
     * @param xor The integer key (0-255) to try.
     * @return The decrypted string.
     */
    public static String singleCharXOR(ArrayList<Byte> byteData, int xor) {

        // Convert the integer key (0-255) into its byte representation.
        byte byteChar = (byte) xor;
        
        // Create a new byte array to hold the decrypted bytes.
        byte[] convertedByte = new byte[byteData.size()];
        
        // Loop through every byte in this block.
        for (int i = 0; i < byteData.size(); i++) {
            // XOR the ciphertext byte with the single key byte.
            convertedByte[i] = (byte)(byteData.get(i) ^ byteChar);
        }

        // Convert the decrypted bytes into a String and return it.
        String text = new String(convertedByte, StandardCharsets.UTF_8);
        return text;
    }



    /**
     * Transposes the ciphertext into a number of blocks equal to the keysize.
     * This is Step 6 of the challenge.
     * @param keySize The key length (e.g., 29).
     * @param ciphertext The full, raw ciphertext.
     * @return A 2D ArrayList where each inner list is a "pile" of bytes.
     */
    public static ArrayList<ArrayList<Byte>> keySizeBlocking(int keySize, byte[] ciphertext) {
        // This is our list of "piles". It's a list that will hold other lists.
        ArrayList<ArrayList<Byte>> masterBlockArrays = new ArrayList<>();

        // Initialize the master list with 'keySize' (e.g., 29) new, empty "piles".
        for (int i = 0; i < keySize; i++) {
            masterBlockArrays.add(new ArrayList<>());
        }

        // Loop through every single byte of the original ciphertext.
        for (int i = 0; i < ciphertext.length; i++) {

            // This is the "modulo trick" to "deal" the bytes.
            // 'i % keySize' gives the "pile index" (0-28) this byte belongs to.
            int pileIndex = i % keySize;
            
            // Add the current byte to its correct pile.
            masterBlockArrays.get(pileIndex).add(ciphertext[i]);
        }

        // Return the list of 29 transposed blocks.
        return masterBlockArrays;
    }



    /**
     * Reads through a txt file and storing its data in a byte array.
     * @return The file in byte[] data
     */
    public static byte[] readFileAsBytes() throws IOException{
        
        // Initilizes a Scanner object that uses a Path to obtain the file
        Scanner fileScanner = new Scanner(Paths.get("Set1_Ch_6.txt"));
        ArrayList<String> fileStrings = new ArrayList<String>();
        
        // Iterates through the scanner adding each line to ArrayList
        while (fileScanner.hasNextLine()) {
            fileStrings.add(fileScanner.nextLine());
        }

        // Using Stringbuilder each string in Arraylist is appending to each other and stored in stringBuilder
        StringBuilder stringBuilder = new StringBuilder();
        for (String lines : fileStrings) {
            stringBuilder.append(lines);
        }

        // Converts the stringBuilder object to string values instead of its memory address
        String fullTextString = stringBuilder.toString();

        // Returns the byte array of the whole string, using Base64 as its encoded with that, so it needs to be decoded
        return Base64.getDecoder().decode(fullTextString);
    }



    /**
     * Analyzes the ciphertext to find the most likely keysize.
     * @param ciphertext The raw bytes of the Base64-decoded file.
     * @return The most likely keysize (e.g., 25).
     */
    public static int findBestKeysize(byte[] ciphertext) {
        
        // Initialize variables to store the best versions
        double smallestNormalizedDistance = Double.MAX_VALUE;
        int bestKeysize = 0;

        // Loop through all possible keysizes 2 to 40
        for (int keysize = 2; keysize <= 40; keysize++) {

            // Get multiple chunks of 'keysize'
            byte[] chunk1 = Arrays.copyOfRange(ciphertext, 0, keysize);
            byte[] chunk2 = Arrays.copyOfRange(ciphertext, keysize, keysize * 2);
            byte[] chunk3 = Arrays.copyOfRange(ciphertext, keysize * 2, keysize * 3);
            byte[] chunk4 = Arrays.copyOfRange(ciphertext, keysize * 3, keysize * 4);

            // Calculate normalized Hamming distances between them and average those distances, cast keysize for non int division
            // Calculate the normalized distance for all 6 pairs / combinations
            double normalizeDist1 = hammingDist(chunk1, chunk2) / (double) keysize;
            double normalizeDist2 = hammingDist(chunk1, chunk3) / (double) keysize;
            double normalizeDist3 = hammingDist(chunk1, chunk4) / (double) keysize;
            double normalizeDist4 = hammingDist(chunk2, chunk3) / (double) keysize;
            double normalizeDist5 = hammingDist(chunk2, chunk4) / (double) keysize;
            double normalizeDist6 = hammingDist(chunk3, chunk4) / (double) keysize;

            // Get the average between both normalized distances
            double averageDist = ((normalizeDist1) + (normalizeDist2) + (normalizeDist3) + (normalizeDist4) + (normalizeDist5) + (normalizeDist6)) / 6;

            // Check if this average is the new smallestNormalizedDistance
            if (averageDist < smallestNormalizedDistance) {
                smallestNormalizedDistance = averageDist;
                bestKeysize = keysize;
            }
        }
        // After the loop finishes, return the winner
        return bestKeysize;
    }



    /**
     * Calculates the Hamming distance (number of differing bits) between two byte arrays.
     * @param a The first byte array.
     * @param b The second byte array.
     * @return The total number of bits that are different, or 0 if lengths mismatch.
     */
    public static int hammingDist(byte[] a, byte[] b) {
        
        if (a.length == b.length) {
            int distance = 0; // This will store our total count of differing bits.

            // Loop through each byte of the arrays.
            for (int i = 0; i < a.length; i++) {
                
                // Step 1: Use XOR to find the differences between the two bytes.
                // A '1' in the result means the bits at that position were different.
                // A '0' means they were the same.
                byte xorResult = (byte)(a[i] ^ b[i]);

                // Step 2: Count the '1's (set bits) in the 8-bit result.
                // (e.g., bitCount of 0101 0101 is 4)
                distance += Integer.bitCount(xorResult & 0xff);
            }
            
            // Return the total number of differing bits we counted.
            return distance;
        }
        
        // If lengths don't match, the distance is 0.
        return 0;  
    }
}










/*
 * -----------------------------------------------------------------
 * THE 'xorResult & 0xff' MASK (A CRITICAL JAVA "GOTCHA")
 * -----------------------------------------------------------------
 *
 * 1. THE PROBLEM:
 * In Java, a 'byte' is SIGNED (from -128 to 127). The
 * 'Integer.bitCount()' function, however, takes an 'int'.
 *
 * 2. THE "SIGN EXTENSION":
 * When Java converts a 'byte' to an 'int', it tries to
 * preserve the sign (the value).
 *
 * - If the byte is POSITIVE (e.g., 0x7f, or 127):
 * It becomes the int 0x0000007f.
 * bitCount(0x0000007f) is 7. This is CORRECT.
 *
 * - If the byte is NEGATIVE (e.g., 0xff, or -1):
 * It is "sign-extended" to become the int 0xffffffff (also -1).
 * bitCount(0xffffffff) is 32. This is WRONG.
 *
 * We wanted the bit count of just '0xff' (which is 8), not the
 * 32-bit integer representation of -1.
 *
 * 3. THE SOLUTION:
 * The '& 0xff' is a "bitwise AND mask". It zeroes out all the
 * extra '1's that were added by the sign extension.
 *
 * 11111111 11111111 11111111 11111111  (The sign-extended -1 int)
 * & 00000000 00000000 00000000 11111111  (The 0xff mask)
 * ---------------------------------------
 * = 00000000 00000000 00000000 11111111  (The int 255)
 *
 * bitCount(255) is 8. This is CORRECT.
 *
 * This operation ensures we are only ever counting the bits
 * from the original 8-bit byte.
 */
// ===> distance += Integer.bitCount(xorResult & 0xff); <===