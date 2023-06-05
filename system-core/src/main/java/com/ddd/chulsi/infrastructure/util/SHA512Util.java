package com.ddd.chulsi.infrastructure.util;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * SHA-512 암호화
 */
public class SHA512Util {

    private static byte[] getSHA512Digest(String input) {
        MessageDigest digest;
        try {
            digest = MessageDigest.getInstance("SHA-512");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("NoSuchAlgorithmException");
        }
        digest.update(input.getBytes(StandardCharsets.UTF_8));
        return digest.digest();
    }

    public static String getSHA512(String input) {
        return String.format("%0128x", new BigInteger(1, getSHA512Digest(input)));
    }

    public static String getSHA512AndBase64Encoding(String input) {
        return Base64.getEncoder().encodeToString(getSHA512Digest(input));
    }
}