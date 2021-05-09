/*import java.math.BigInteger;
import java.util.*;
import java.com.example.p2pgeocaching.BaseTable;
import java.io.*;
import java.util.Scanner;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.*;

public class RSA {
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
    private static int getRandomPrime() {
        Random random = new Random();
        int randomRow = random.nextInt(filelen());
        String line = "";
        int prime = 0;
        try (Stream<String> lines = Files.lines(Paths.get("primeNumbers.txt"))) {
            line = lines.skip(randomRow).findFirst().get();
            prime = Integer.parseInt(line);
        } catch(IOException e){
            System.out.println("Problem");
        }
        return prime;
    }
    private static int filelen() {
        int count = 0;
        try {
            File file=new File("primeNumbers.txt");
            Scanner sc = new Scanner(file);
            while(sc.hasNextLine()) {
                sc.nextLine();
                count++;
            }
            sc.close();
        } catch (FileNotFoundException e) {
            return -1;
        }
        return count;
    }
    private static BigInteger ggT(BigInteger a, BigInteger b) {
        BigInteger min = a;
        if (a.compareTo(b) == 1) {
            min = b;
        }
        for (BigInteger i = min; i.compareTo(BigInteger.ONE) == 1; i = i.subtract(BigInteger.ONE)) {
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
        while (a.compareTo(BigInteger.ONE) == 1) {
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
    public static String generateKeys() {
        int p = 0;
        int q = 0;
        BigInteger bigP = BigInteger.ZERO;
        BigInteger bigQ = BigInteger.ZERO;
        BigInteger n = BigInteger.ZERO;
        while (p == q || n.bitLength() < 12 || n.bitLength() > 18) {
            p = getRandomPrime();
            q = getRandomPrime();
            bigP = BigInteger.valueOf(p);
            bigQ = BigInteger.valueOf(q);
            n = bigP.multiply(bigQ);
        }
        Integer intP = Integer.valueOf(p);
        Integer intQ = Integer.valueOf(q);

        BigInteger phi = (bigP.subtract(BigInteger.ONE)).multiply(bigQ.subtract(BigInteger.ONE));

        BigInteger e = BigInteger.ZERO;
        BigInteger ggt = BigInteger.ZERO;
        while (e.compareTo(BigInteger.ONE) <= 0 || e.compareTo(phi) >= 0 || ggt.compareTo(BigInteger.ONE) != 0) {
            e = getRandomBigInteger(phi);
            ggt = ggT(e, phi);
        }
        BigInteger d = multiplicativeInverse(e, phi);
        if (d.compareTo(BigInteger.ZERO) <= 0 || e.compareTo(BigInteger.ZERO) <= 0 || n.compareTo(BigInteger.ZERO) <= 0) {
            return generateKeys();
        }
        String keyAsString = d.toString() + "_" + n.toString() + ":" + e.toString() + "_" + n.toString();
        return keyAsString;
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
        String randomBin = "";
        Random random = new Random();
        for (int i = 0; i < 12; i++) {
            randomBin = randomBin + random.nextInt(2);
        }
        return randomBin;
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
            String binaryBlock1 = BaseTable.getBinValue(letters[i*2]);
            String binaryBlock2 = BaseTable.getBinValue(letters[i*2 + 1]);
            if (binaryBlock2 == "") {
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
            BigInteger randomBigInt = new BigInteger(getRandomBinValue(),2);
            encodedValues[0] = pow(randomBigInt, d).mod(n);
            for (int i = 1; i < encodedValues.length; i++) {
                encodedValues[i] = pow(encodedValues[i-1].xor(block[i-1]), d).mod(n);
            }
        }
        String encodedMessage = encodedValues[0].toString();
        for (int i = 1; i < encodedValues.length; i++) {
            encodedMessage = encodedMessage + " " + encodedValues[i].toString();
        }
        return encodedMessage;
    }
    public static String decode(String encodedMessageAsText, String publicKey){
        String[] parts = publicKey.split("_");
        BigInteger e = new BigInteger(parts[0]);
        BigInteger n = new BigInteger(parts[1]);
        String[] partsOfText = encodedMessageAsText.split(" ");
        BigInteger[] encodedMessage = new BigInteger[partsOfText.length];
        for (int i = 0; i < encodedMessage.length; i++) {
            encodedMessage[i] = new BigInteger(partsOfText[i]);
        }
        String message = "";
        BigInteger support;
        String binaryValue;
        String m, t, s;
        if (encodedMessage.length == 1) {
            support = pow(encodedMessage[0], e).mod(n);
            binaryValue = String.format("%12s",support.toString(2)).replace(' ', '0');
            m = binaryValue.substring(0,6);
            t = binaryValue.substring(6,12);
            m = BaseTable.getLetter(m);
            t = BaseTable.getLetter(t);
            message = message + m;
            if (t.compareTo("") != 0) {
                message = message + t;
            }
        } else {
            BigInteger[] decodedInt = new BigInteger[encodedMessage.length-1];
            for (int i = encodedMessage.length-1; i >= 1; i--) {
                support = pow(encodedMessage[i], e).mod(n);
                decodedInt[i-1] = encodedMessage[i-1].xor(support);
            }

            for (int i = 0; i < decodedInt.length; i++) {
                binaryValue = String.format("%12s", decodedInt[i].toString(2)).replace(' ', '0');
                m = binaryValue.substring(0,6);
                t = binaryValue.substring(6,12);
                m = BaseTable.getLetter(m);
                t = BaseTable.getLetter(t);
                message = message + m;
                if (t.compareTo("") != 0) {
                    message = message + t;
                }
            }
        }
        return message;
    }
    public static String getPrivateKey(String key) {
        String[] keys = key.split(":");
        return keys[0];
    }
    public static String getPublicKey(String key) {
        String[] keys = key.split(":");
        return keys[1];
    }
    public static String createPrivateKey (String privateKey, String publicKey) {
        String[] parts = publicKey.split("_");
        String key = privateKey + parts[1];
        return key;
    }
}*/