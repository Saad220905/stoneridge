package com.stoneridge.backend.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final String SECRET_KEY_STRING = System.getenv("ENCRYPTION_KEY") != null ? System.getenv("ENCRYPTION_KEY") : "12345678901234567890123456789012"; // 32 chars for 256-bit

    public static String encrypt(String strToEncrypt) throws Exception {
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY_STRING.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, spec);
        byte[] cipherText = cipher.doFinal(strToEncrypt.getBytes());
        byte[] cipherTextWithIv = new byte[IV_LENGTH_BYTE + cipherText.length];
        System.arraycopy(iv, 0, cipherTextWithIv, 0, IV_LENGTH_BYTE);
        System.arraycopy(cipherText, 0, cipherTextWithIv, IV_LENGTH_BYTE, cipherText.length);
        return Base64.getEncoder().encodeToString(cipherTextWithIv);
    }

    public static String decrypt(String strToDecrypt) throws Exception {
        byte[] decode = Base64.getDecoder().decode(strToDecrypt);
        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(decode, 0, iv, 0, IV_LENGTH_BYTE);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        SecretKey secretKey = new SecretKeySpec(SECRET_KEY_STRING.getBytes(), "AES");
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
        byte[] plainText = cipher.doFinal(decode, IV_LENGTH_BYTE, decode.length - IV_LENGTH_BYTE);
        return new String(plainText);
    }

    /**
     * Deterministic hash for lookups (e.g., email)
     */
    public static String hash(String input) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(input.getBytes());
        return Base64.getEncoder().encodeToString(encodedhash);
    }
}
