package com.example.p2pgeocaching.RSA;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.Scanner;

public class RSA {
    /*
    public static void main (String [] args) {
        String key = generateKeys();
        System.out.println("Generated Keys.");
        String privateKey = getPrivateKey(key);
        System.out.println("Private Key (d_n): "+privateKey);
        String publicKey = getPublicKey(key);
        System.out.println("Public Key (e_n): "+publicKey);
        String encodedMessage = encode("M", privateKey);
        System.out.println("Encoded Message: "+encodedMessage);
        String decodedMessage = decode(encodedMessage, publicKey);
        System.out.println("Decoded Message: "+decodedMessage);
    }
    */
    private final static String TAG = "RSA";
    private final static String FILE_PATH = "raw/primeNumbers.txt";

    private static int getRandomPrime(Context c) {
        Random random = new Random();
        AssetManager assetManager = c.getAssets();
        InputStream inputStream = null;
        while (inputStream == null) {
            try {
                inputStream = assetManager.open(FILE_PATH);
            } catch (IOException e) {
                Log.d(TAG, "IOException when generating primes, could not open primesFile.");
                try {
                    Log.d(TAG, "List of assets: " + Arrays.toString(assetManager.list("")));
                } catch (IOException i) {}
            }
        }
        Scanner scanner = new Scanner(inputStream);
        int randomRow = random.nextInt(numOfLines(scanner));
        String line = "";
        try {
            scanner = new Scanner(assetManager.open(FILE_PATH));
        } catch (IOException e) {}
        for (int i = 0; i < randomRow; i++) {
            scanner.nextLine();
        }
        line = scanner.nextLine();

        return Integer.parseInt(line);
    }

    /**
     * This method is used to get the total amount of lines of the file
     * "raw/primeNumbers.txt".
     * @param sc Scanner which is used to scan over the lines of file "raw/primeNumbers.txt"
     * @return total amount of lines as int
     */
    private static int numOfLines(Scanner sc) {
        int lines = 0;
        // go through all lines of the file and count the amount of lines
        while (sc.hasNextLine()) {
            sc.nextLine();
            lines++;
        }
        sc.close();
        return lines;
    }

    /**
     * This method is used to calculate the greatest common divisor of two BigInteger values
     * @param a first BigInteger value
     * @param b second BigInteger value
     * @return greatest common divisor of the two values a and b
     */
    private static BigInteger ggT(BigInteger a, BigInteger b) {
        BigInteger min = a;
        // get the smaller number to avoid longer for-loop
        if (a.compareTo(b) > 0) {
            min = b;
        }
        for (BigInteger i = min; i.compareTo(BigInteger.ONE) > 0; i = i.subtract(BigInteger.ONE)) {
            // try to get a common divisor
            if ((a.mod(i)).compareTo(BigInteger.ZERO) == 0 && (b.mod(i)).compareTo(BigInteger.ZERO) == 0) {
                // return greatest common divisor
                return i;
            }
        }
        // if no divisor was found, return 1
        return BigInteger.ONE;
    }

    /**
     * This method is used to get the multiplicative inverse of two BigInteger values
     * @param a first BigInteger value
     * @param b second BigInteger value
     * @return multiplicative inverse as BigInteger
     */
    private static BigInteger multiplicativeInverse(BigInteger a, BigInteger b) {
        if (a.compareTo(BigInteger.ZERO) == 0) {
            return BigInteger.ZERO;
        }
        BigInteger b0 = b;
        BigInteger c, d, e, f;
        e = BigInteger.ZERO;
        f = BigInteger.ONE;
        while (a.compareTo(BigInteger.ONE) > 0) {
            c = a.divide(b);
            d = b;
            b = a.mod(b);
            a = d;
            d = e;
            e = f.subtract(c.multiply(e));
            f = d;
        }
        if (f.compareTo(BigInteger.ZERO) == 0) {
            f = f.add(b0);
        }
        return f;
    }

    /**
     * This method is used to get a random BigInteger value with the condition
     * that it must be smaller than the given parameter phi
     * @param phi represents the limit of the random value
     * @return random BigInteger value
     */
    private static BigInteger getRandomBigInteger(BigInteger phi) {
        Random random = new Random();
        BigInteger e;
        // e must be strictly lower than phi
        do {
            e = new BigInteger(phi.bitLength(), random);
        } while (e.compareTo(phi) >= 0);
        return e;
    }

    /**
     * This method is used to generate private and public keys
     * @param c Context which is used to reach file "raw/primeNumbers.txt"
     * @return String which contains private and public Key in the format:
     * d_n:e_n
     */
    public static String generateKeys(Context c) {
        int p = 0;
        int q = 0;
        BigInteger bigP = BigInteger.ZERO;
        BigInteger bigQ = BigInteger.ZERO;
        BigInteger n = BigInteger.ZERO;
        // search after two prime values p and q which are not equall to each other
        // and produce n with a bit length between 12 and 18 bits.
        // If the bit length exceeds this limit, the calculation would take much longer
        while (p == q || n.bitLength() < 12 || n.bitLength() > 18) {
            // get p and q through the method getRandomPrime
            p = getRandomPrime(c);
            q = getRandomPrime(c);
            // transform p and q to BigInteger values
            bigP = BigInteger.valueOf(p);
            bigQ = BigInteger.valueOf(q);
            // calculate n with n = p * q
            n = bigP.multiply(bigQ);
        }
        // calculate phi with phi = (p-1) * (q-1)
        BigInteger phi = (bigP.subtract(BigInteger.ONE)).multiply(bigQ.subtract(BigInteger.ONE));

        BigInteger e = BigInteger.ZERO;
        BigInteger ggt = BigInteger.ZERO;
        // Try to calculate a which is bigger than zero and smaller than phi with the condition, that e and phi
        // don't share any other greatest common divisor than 1
        while (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0 || ggt.compareTo(BigInteger.ONE) != 0) {
            // get e with the method getRandomBigInteger
            e = getRandomBigInteger(phi);
            // calculate greates common divisor of e and phi with the method ggT
            ggt = ggT(e, phi);
        }
        // Calculate the multiplicative inverse d of e and phi so that e * d == 1 mod phi
        // d is calculated with the method multiplicativeInverse
        BigInteger d = multiplicativeInverse(e, phi);
        // check whether all conditions are correct: d, e and n must be bigger than 0 (safety check)
        if (d.compareTo(BigInteger.ZERO) <= 0 || e.compareTo(BigInteger.ZERO) <= 0 || n.compareTo(BigInteger.ZERO) <= 0) {
            return generateKeys(c);
        }
        // return keys in the format "d_n:e_n"
        return d.toString() + "_" + n.toString() + ":" + e.toString() + "_" + n.toString();
    }

    /**
     * This method is used to calculate the pow between base and exponent
     * which are both BigInteger values
     * @param base base value as BigInteger
     * @param exponent exponent values as BigInteger
     * @return BigInteger value base^exponent
     */
    private static BigInteger pow(BigInteger base, BigInteger exponent) {
        BigInteger result = BigInteger.ONE;
        while (exponent.signum() > 0) {
            if (exponent.testBit(0)) {
                result = result.multiply(base);
            }
            base = base.multiply(base);
            exponent = exponent.shiftRight(1);
        }
        return result;
    }

    /**
     * This method is used to generate a random binary value with length of 12 bits
     * @return String containing a binary value with length 12 bits
     */
    private static String getRandomBinValue() {
        StringBuilder randomBin = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            // append either 0 or 1 to the string
            randomBin.append(random.nextInt(2));
        }
        return randomBin.toString();
    }

    /**
     * This method is used to encode a message with the given privateKey
     * @param message message which should be encoded with private Key in RSA
     * @param privateKey String which contains d an n in the format d_n
     * @return String containing the given message encoded in rsa
     */
    public static String encode(String message, String privateKey) {
        // split private key at "_". This is due to the fact that the privateKey
        // is in the format "d_n".
        String[] parts = privateKey.split("_");
        // transform the parts into BigInteger values
        BigInteger d = new BigInteger(parts[0]);
        BigInteger n = new BigInteger(parts[1]);
        String[] letters;
        BigInteger[] block;
        // if message length even then we don't have to pad
        if (message.length() % 2 == 0) {
            letters = new String[message.length()];
        } else {
            // padding necessary. Last array element is reserved for the binary
            // value 111111 (later). This serves as indicator for the decoding method,
            // that this element only served for padding
            letters = new String[message.length() + 1];
        }
        // get all characters in the message and save them in array letters
        for (int i = 0; i < message.length(); i++) {
            letters[i] = Character.toString(message.charAt(i));
            //System.out.println(letters[i]);
        }
        // If last array element is null then message is not even. This element
        // was reserved for the padding indicator
        if (letters[letters.length - 1] == null) {
            letters[letters.length - 1] = "";
        }
        // We transform all letters into base64 encoding and save them as 2-letter-blocks
        // That is the reasen, why block has only half of the length of array letters
        block = new BigInteger[letters.length / 2];
        for (int i = 0; i < block.length; i++) {
            // get base64 encoding of letter through class BaseTable
            String binaryBlock1 = BaseTable.getBinValue(letters[i * 2]);
            String binaryBlock2 = BaseTable.getBinValue(letters[i * 2 + 1]);
            // if second block is a empty string this means that this block is only
            // used for padding. Save there the binary value 111111
            if (binaryBlock2.equals("")) {
                binaryBlock2 = "111111";
            }
            // save 2-block-binary values in array
            block[i] = new BigInteger(binaryBlock1 + binaryBlock2, 2);
        }
        BigInteger[] encodedValues;
        // If we only have one block: use RSA directly
        if (block.length == 1) {
            encodedValues = new BigInteger[1];
            encodedValues[0] = pow(block[0], d).mod(n);
        } else {
            // If we have multiple block, than apply CBC. Generate an initalizing
            // vector with the length of 12 bits. Save the RSA encoding of it in the
            // first position of the array. Take this value and calculate XOR between it and
            // the first block. Apply RSA and save the value in array. Take that value and
            // calculate XOR with the second block etc.
            encodedValues = new BigInteger[block.length + 1];
            // calculate a random binary value of length of 12 bits and get the BigInteger value
            // of it. This servers as initializing vector
            BigInteger randomBigInt = new BigInteger(getRandomBinValue(), 2);
            // save RSA encoding of the initializing vector
            encodedValues[0] = pow(randomBigInt, d).mod(n);
            for (int i = 1; i < encodedValues.length; i++) {
                // get encoded block and calculate XOR with next block, get the RSA
                // encoding of XOR and save it in the array
                encodedValues[i] = pow(encodedValues[i - 1].xor(block[i - 1]), d).mod(n);
            }
        }
        StringBuilder encodedMessage = new StringBuilder(encodedValues[0].toString());
        // append all RSA encodings in a string
        for (int i = 1; i < encodedValues.length; i++) {
            encodedMessage.append(" ").append(encodedValues[i].toString());
        }
        return encodedMessage.toString();
    }

    public static String decode(String encodedMessageAsText, String publicKey) {
        String[] parts = publicKey.split("_");
        BigInteger e = new BigInteger(parts[0]);
        BigInteger n = new BigInteger(parts[1]);
        String[] partsOfText = encodedMessageAsText.split(" ");
        BigInteger[] encodedMessage = new BigInteger[partsOfText.length];
        for (int i = 0; i < encodedMessage.length; i++) {
            encodedMessage[i] = new BigInteger(partsOfText[i]);
        }
        StringBuilder message = new StringBuilder();
        BigInteger support;
        String binaryValue;
        String m, t, s;
        if (encodedMessage.length == 1) {
            support = pow(encodedMessage[0], e).mod(n);
            binaryValue = String.format("%12s", support.toString(2)).replace(' ', '0');
            m = binaryValue.substring(0, 6);
            t = binaryValue.substring(6, 12);
            m = BaseTable.getLetter(m);
            t = BaseTable.getLetter(t);
            message.append(m);
            if (t.compareTo("") != 0) {
                message.append(t);
            }
        } else {
            BigInteger[] decodedInt = new BigInteger[encodedMessage.length - 1];
            for (int i = encodedMessage.length - 1; i >= 1; i--) {
                support = pow(encodedMessage[i], e).mod(n);
                decodedInt[i - 1] = encodedMessage[i - 1].xor(support);
            }

            for (BigInteger bigInteger : decodedInt) {
                binaryValue = String.format("%12s", bigInteger.toString(2)).replace(' ', '0');
                m = binaryValue.substring(0, 6);
                t = binaryValue.substring(6, 12);
                m = BaseTable.getLetter(m);
                t = BaseTable.getLetter(t);
                message.append(m);
                if (t.compareTo("") != 0) {
                    message.append(t);
                }
            }
        }
        return message.toString();
    }

    /**
     * This method takes the keys and returns the private key
     * @param key key in the format "d_n:e_n"
     * @return private key in the format "d_n"
     */
    public static String getPrivateKey(String key) {
        // split key at the position ":"
        String[] keys = key.split(":");
        // return first part "d_n"
        return keys[0];
    }

    /**
     * This method takes the keys and returns the public key
     * @param key key in the format "d_n:e_n"
     * @return private key in the format "e_n"
     */
    public static String getPublicKey(String key) {
        // split key at the position ":"
        String[] keys = key.split(":");
        // return second part "e_n"
        return keys[1];
    }

    /**
     * This method is used to create private key String
     * @param privateKey d value of private key
     * @param publicKey format: "e_n"
     * @return privateKey as String in the format "d_n"
     */
    public static String createPrivateKey(String privateKey, String publicKey) {
        // split publicKey at the position "_" to get n
        String[] parts = publicKey.split("_");
        // return "d_n"
        return privateKey + "_" + parts[1];
    }

    /**
     * This method returns a random binary String with length n
     * @param n length of string
     * @return the binary value as String
     */
    private static String getRandomBinWithLengthN (int n) {
        String binaryString = "";
        int j;
        Random random = new Random();
        for (int i = 0; i < n; i++) {
            // append n-times 0 or 1 to the string
            binaryString = binaryString + random.nextInt(2);
        }
        return binaryString;
    }

    /**
     * This method is used to transform a binary String into BigInteger
     * @param binaryString binary value as String which should be transformed into BigInteger
     * @return BigInteger value of binaryString
     */
    private static BigInteger binToBigInteger (String binaryString) {
        /*BigInteger intValue = BigInteger.ZERO;
        BigInteger support = BigInteger.ZERO;
        BigInteger bit = BigInteger.ZERO;
        String[] binaryArray = binaryString.split("");
        BigInteger potenz = BigInteger.ZERO;
        BigInteger two = new BigInteger("2");
        for (int i = binaryArray.length - 1; i >= 0; i--) {
            bit = new BigInteger(binaryArray[i]);
            support = bit.multiply(pow(two, potenz));
            intValue = intValue.add(support);
            potenz = potenz.add(BigInteger.ONE);
        }*/
        return new BigInteger(binaryString, 2);
    }

    /**
     * This method is used to get the length of the BigInteger value
     * in binary format
     * @param keyValue BigInteger value
     * @return length of BigInteger value in binary format as int
     */
    private static int getLengthOfBigIntegerAsBin(BigInteger keyValue) {
        int count = 0;
        BigInteger two = new BigInteger("2");
        while (keyValue.compareTo(BigInteger.ZERO) >= 1) {
            keyValue = keyValue.divide(two);
            count++;
        }
        return count;
    }

    public static String keySplitting (int n, String privateKey) {
        BigInteger[] keyList = new BigInteger[n];
        String binaryString = "";
        BigInteger intValue, xorValue;
        String[] parts = privateKey.split("_");
        BigInteger d = new BigInteger(parts[0]);
        int binLength = getLengthOfBigIntegerAsBin(d);
        if (n == 1) {
            keyList[0] = d;
        } else {
            for (int i = 0; i < n-1; i++) {
                binaryString = getRandomBinWithLengthN(binLength);
                intValue = binToBigInteger(binaryString);
                xorValue = intValue.xor(d);
                keyList[i] = xorValue;
                d = intValue;
            }
            keyList[n-1] = d;
            for (int i = 0; i < keyList.length; i++) {
                for (int j = i; j < keyList.length; j++) {
                    if (keyList[j].compareTo(keyList[i]) == 0 && i != j) {
                        return keySplitting(n, privateKey);
                    }
                }
            }
        }
        String splittedKeys = "";
        for (int i = 0; i < keyList.length; i++) {
            if (i != keyList.length-1) {
                splittedKeys = splittedKeys + keyList[i].toString() + "_";
            } else {
                splittedKeys = splittedKeys + keyList[i].toString();
            }
        }
        return splittedKeys;
    }

    public static String keyRecreating (String keyList) {
        String[] keyParts = keyList.split("_");
        int len = keyParts.length;
        BigInteger d = new BigInteger(keyParts[len-1]);
        if (keyParts.length == 1) {
            return d.toString();
        }
        for (int i = len - 2; i >= 0; i--) {
            d = d.xor(new BigInteger(keyParts[i]));
        }
        return d.toString();
    }
}