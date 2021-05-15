package com.example.p2pgeocaching.RSA;

import java.math.BigInteger;
import java.util.*;

public class SecurityTest {

    public static void breakRSA (Context c) {
        String keys = RSA.generateKeys(c);
        String privateKey = RSA.getPrivateKey(keys);
        String publicKey = RSA.getPublicKey(keys);
        String[] parts = publicKey.split("_");
        String[] parts1 = privateKey.split("_");
        BigInteger dCorrect = new BigInteger(parts1[0]);
        BigInteger e = new BigInteger(parts[0]);
        BigInteger n = new BigInteger(parts[1]);
        List<BigInteger[]> relevantPrimes = new ArrayList<>();
        long start = System.currentTimeMillis();
        for (BigInteger i = new BigInteger("2"); i.compareTo(n) <= 0; i = i.add(BigInteger.ONE)) {
            for (BigInteger j = new BigInteger("2"); j.compareTo(n) <= 0; j = j.add(BigInteger.ONE)) {
                if ((i.multiply(j)).compareTo(n) == 1) {
                    break;
                }
                if ((i.multiply(j)).compareTo(n) == 0) {
                    if (!isPrime(i)) {
                        break;
                    }
                    if (!isPrime(j)) {
                        continue;
                    }
                    BigInteger[] array = new BigInteger[2];
                    array[0] = i;
                    array[1] = j;
                    relevantPrimes.add(array);
                }
            }
        }
        if (relevantPrimes.isEmpty()) {
            System.out.println("No factorization found");
            return;
        }
        for (int i = 0; i < relevantPrimes.size(); i++) {
            BigInteger[] array = relevantPrimes.get(i);
            BigInteger p = array[0];
            BigInteger q = array[1];
            BigInteger phi = (p.subtract(BigInteger.ONE)).multiply(q.subtract(BigInteger.ONE));
            if ((RSA.ggT(e, phi)).compareTo(BigInteger.ONE) != 0) {
                continue;
            }
            BigInteger d = RSA.multiplicativeInverse(e, phi);
            if (d.compareTo(dCorrect) == 0) {
                long stop = System.currentTimeMillis();
                System.out.println("Could break RSA");
                long time = (stop-start);
                System.out.println("Time: " + time);
                break;
            }
        }
    }

    private static boolean isPrime(BigInteger a) {
        for (BigInteger i = new BigInteger ("2"); i.compareTo(a) < 0; i = i.add(BigInteger.ONE)) {
            try {
                if (a.mod(i) == BigInteger.ZERO) {
                    return false;
                }
            } catch (ArithmeticException e) {}
        }
        return true;
    }
}
