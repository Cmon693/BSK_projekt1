package sample;

import java.util.Random;
import java.math.BigInteger;
import  java.security.SecureRandom;

public class Generator {

    public static void generateWithTime() {

        Random rand1 = new Random(System.currentTimeMillis());
        Random rand2 = new Random(System.currentTimeMillis() * 10);

        //System.out.println(rand1);
        //System.out.println(rand2);

        BigInteger bigP = BigInteger.probablePrime(64, rand1);
        BigInteger bigQ = BigInteger.probablePrime(64, rand2);

        BigInteger bigN = bigP.multiply(bigQ);

        System.out.println("klucz sesyjny: " + bigN);

        /*
        BigInteger bigP1 = bigP.subtract(new BigInteger("1"));
        BigInteger bigQ1 = bigQ.subtract(new BigInteger("1"));
        BigInteger bigQ1P1 = bigP1.multiply(bigQ1);

        while (true){
            BigInteger bigGCD = bigQ1P1.gcd(new BigInteger(""+pubKey));

            if (bigGCD.equals(BigInteger.ONE))
                break;

            pubKey++;
        }

        BigInteger bigPubKey = new BigInteger(""+pubKey);
        BigInteger bigPrvKey = bigPubKey.modInverse(bigQ1P1);

        System.out.println("");
        System.out.println("BIG n " + bigN);
        System.out.println("Public key " + bigPubKey);
        System.out.println("Private key " + bigPrvKey);
        */


    }
}
