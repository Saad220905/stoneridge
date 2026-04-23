package com.stoneridge.backend.util;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

public class EncryptionUtil {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int TAG_LENGTH_BIT = 128;
    private static final int IV_LENGTH_BYTE = 12;
    private static final String ENCRYPTION_KEY = System.getenv("ENCRYPTION_KEY");

    private static SecretKey getSecretKey() {
        if (ENCRYPTION_KEY == null || ENCRYPTION_KEY.isBlank()) {
            throw new IllegalStateException("ENCRYPTION_KEY environment variable is not set");
        }
        // Ensure the key is 32 bytes for AES-256
        byte[] keyBytes = ENCRYPTION_KEY.getBytes(StandardCharsets.UTF_8);
        if (keyBytes.length != 32) {
             // If not 32 bytes, we could pad it or hash it, but better to require correct size
             throw new IllegalStateException("ENCRYPTION_KEY must be exactly 32 bytes for AES-256");
        }
        return new SecretKeySpec(keyBytes, "AES");
    }

    public static String encrypt(String strToEncrypt) throws Exception {
        if (strToEncrypt == null) return null;
        
        byte[] iv = new byte[IV_LENGTH_BYTE];
        new SecureRandom().nextBytes(iv);
        
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        cipher.init(Cipher.ENCRYPT_MODE, getSecretKey(), spec);
        
        byte[] cipherText = cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8));
        byte[] cipherTextWithIv = new byte[IV_LENGTH_BYTE + cipherText.length];
        System.arraycopy(iv, 0, cipherTextWithIv, 0, IV_LENGTH_BYTE);
        System.arraycopy(cipherText, 0, cipherTextWithIv, IV_LENGTH_BYTE, cipherText.length);
        
        return Base64.getEncoder().encodeToString(cipherTextWithIv);
    }

    public static String decrypt(String strToDecrypt) throws Exception {
        if (strToDecrypt == null) return null;
        
        byte[] decode = Base64.getDecoder().decode(strToDecrypt);
        if (decode.length < IV_LENGTH_BYTE) {
            throw new IllegalArgumentException("Invalid encrypted data");
        }
        
        byte[] iv = new byte[IV_LENGTH_BYTE];
        System.arraycopy(decode, 0, iv, 0, IV_LENGTH_BYTE);
        
        GCMParameterSpec spec = new GCMParameterSpec(TAG_LENGTH_BIT, iv);
        Cipher cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(Cipher.DECRYPT_MODE, getSecretKey(), spec);
        
        byte[] plainText = cipher.doFinal(decode, IV_LENGTH_BYTE, decode.length - IV_LENGTH_BYTE);
        return new String(plainText, StandardCharsets.UTF_8);
    }

    /**
     * Deterministic hash for lookups (e.g., email)
     */
    public static String hash(String input) throws Exception {
        if (input == null) return null;
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] encodedhash = digest.digest(input.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encodedhash);
    }
}
