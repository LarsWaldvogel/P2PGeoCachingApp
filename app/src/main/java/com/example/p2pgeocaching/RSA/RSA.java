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

    private static int numOfLines(Scanner sc) {
        int lines = 0;
        while (sc.hasNextLine()) {
            sc.nextLine();
            lines++;
        }
        sc.close();
        return lines;
    }

    private static BigInteger ggT(BigInteger a, BigInteger b) {
        BigInteger min = a;
        if (a.compareTo(b) > 0) {
            min = b;
        }
        for (BigInteger i = min; i.compareTo(BigInteger.ONE) > 0; i = i.subtract(BigInteger.ONE)) {
            if ((a.mod(i)).compareTo(BigInteger.ZERO) == 0 && (b.mod(i)).compareTo(BigInteger.ZERO) == 0) {
                return i;
            }
        }
        return BigInteger.ONE;
    }

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

    private static BigInteger getRandomBigInteger(BigInteger phi) {
        Random random = new Random();
        BigInteger e;
        do {
            e = new BigInteger(phi.bitLength(), random);
        } while (e.compareTo(phi) >= 0);
        return e;
    }

    public static String generateKeys(Context c) {
        int p = 0;
        int q = 0;
        BigInteger bigP = BigInteger.ZERO;
        BigInteger bigQ = BigInteger.ZERO;
        BigInteger n = BigInteger.ZERO;
        while (p == q || n.bitLength() < 12 || n.bitLength() > 18) {
            p = getRandomPrime(c);
            q = getRandomPrime(c);
            bigP = BigInteger.valueOf(p);
            bigQ = BigInteger.valueOf(q);
            n = bigP.multiply(bigQ);
        }
        Integer intP = p;
        Integer intQ = q;

        BigInteger phi = (bigP.subtract(BigInteger.ONE)).multiply(bigQ.subtract(BigInteger.ONE));

        BigInteger e = BigInteger.ZERO;
        BigInteger ggt = BigInteger.ZERO;
        while (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0 || ggt.compareTo(BigInteger.ONE) != 0) {
            e = getRandomBigInteger(phi);
            ggt = ggT(e, phi);
        }
        BigInteger d = multiplicativeInverse(e, phi);
        if (d.compareTo(BigInteger.ZERO) <= 0 || e.compareTo(BigInteger.ZERO) <= 0 || n.compareTo(BigInteger.ZERO) <= 0) {
            return generateKeys(c);
        }
        return d.toString() + "_" + n.toString() + ":" + e.toString() + "_" + n.toString();
    }

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

    private static String getRandomBinValue() {
        StringBuilder randomBin = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            randomBin.append(random.nextInt(2));
        }
        return randomBin.toString();
    }

    public static String encode(String message, String privateKey) {
        String[] parts = privateKey.split("_");
        BigInteger d = new BigInteger(parts[0]);
        BigInteger n = new BigInteger(parts[1]);
        String[] letters;
        BigInteger[] block;
        if (message.length() % 2 == 0) {
            letters = new String[message.length()];
        } else {
            letters = new String[message.length() + 1];
        }
        for (int i = 0; i < message.length(); i++) {
            letters[i] = Character.toString(message.charAt(i));
            System.out.println(letters[i]);
        }
        if (letters[letters.length - 1] == null) {
            letters[letters.length - 1] = "";
        }
        block = new BigInteger[letters.length / 2];
        for (int i = 0; i < block.length; i++) {
            String binaryBlock1 = BaseTable.getBinValue(letters[i * 2]);
            String binaryBlock2 = BaseTable.getBinValue(letters[i * 2 + 1]);
            if (binaryBlock2.equals("")) {
                binaryBlock2 = "111111";
            }
            block[i] = new BigInteger(binaryBlock1 + binaryBlock2, 2);
        }
        BigInteger[] encodedValues;
        if (block.length == 1) {
            encodedValues = new BigInteger[1];
            encodedValues[0] = pow(block[0], d).mod(n);
        } else {
            encodedValues = new BigInteger[block.length + 1];
            BigInteger randomBigInt = new BigInteger(getRandomBinValue(), 2);
            encodedValues[0] = pow(randomBigInt, d).mod(n);
            for (int i = 1; i < encodedValues.length; i++) {
                encodedValues[i] = pow(encodedValues[i - 1].xor(block[i - 1]), d).mod(n);
            }
        }
        StringBuilder encodedMessage = new StringBuilder(encodedValues[0].toString());
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

    public static String getPrivateKey(String key) {
        String[] keys = key.split(":");
        return keys[0];
    }

    public static String getPublicKey(String key) {
        String[] keys = key.split(":");
        return keys[1];
    }

    public static String createPrivateKey(String privateKey, String publicKey) {
        String[] parts = publicKey.split("_");
        return privateKey + parts[1];
    }
}