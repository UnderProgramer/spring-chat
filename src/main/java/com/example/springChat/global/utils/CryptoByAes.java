package com.example.springChat.global.utils;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

@Component
public class CryptoByAes {
    private final String AES_ALGORITHM_GCM = "AES/GCM/NoPadding";
    private final Integer IV_LENGTH_ENCRYPT = 12;
    private  final Integer TAG_LENGTH_ENCRYPT = 16;

    @Value("${spring.crypto.secret}")
    private String LOCAL_PASSPHRASE;

    @Value("${spring.crypto.salt}")
    private String PBKDF2_SALT_STRING;

    private SecretKeySpec generateAesKeyFromPassphrase() throws Exception {
        byte[] salt = PBKDF2_SALT_STRING.getBytes();

        PBEKeySpec spec = new PBEKeySpec(
                LOCAL_PASSPHRASE.toCharArray(),
                salt,
                65536,
                256
        );
        SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
        byte[] keyBytes = factory.generateSecret(spec).getEncoded();
        String AES_ALGORITHM = "AES";
        return new SecretKeySpec(keyBytes, AES_ALGORITHM);
    }

    public String encrypt(String plainText) throws Exception {
        byte[] iv = new byte[IV_LENGTH_ENCRYPT];
        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        SecretKeySpec aesKey = generateAesKeyFromPassphrase();

        Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_ENCRYPT * 8, iv);
        cipher.init(Cipher.ENCRYPT_MODE, aesKey, gcmSpec);

        byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

        byte[] combinedIvAndCipherText = new byte[iv.length + encryptedBytes.length];
        System.arraycopy(iv, 0, combinedIvAndCipherText, 0, iv.length);
        System.arraycopy(encryptedBytes, 0, combinedIvAndCipherText, iv.length, encryptedBytes.length);

        return Base64.getEncoder().encodeToString(combinedIvAndCipherText);
    }

    public String decrypt(String cipherText) throws Exception {
        byte[] decodedCipherText = Base64.getDecoder().decode(cipherText);

        SecretKeySpec aesKey = generateAesKeyFromPassphrase();

        byte[] iv = new byte[IV_LENGTH_ENCRYPT];
        System.arraycopy(decodedCipherText, 0, iv, 0, iv.length);
        byte[] encryptedText = new byte[decodedCipherText.length - IV_LENGTH_ENCRYPT];
        System.arraycopy(decodedCipherText, IV_LENGTH_ENCRYPT, encryptedText, 0, encryptedText.length);

        GCMParameterSpec gcmSpec = new GCMParameterSpec(TAG_LENGTH_ENCRYPT * 8, iv);
        Cipher cipher = Cipher.getInstance(AES_ALGORITHM_GCM);
        cipher.init(Cipher.DECRYPT_MODE, aesKey, gcmSpec);

        byte[] decryptedBytes = cipher.doFinal(encryptedText);

        return new String(decryptedBytes, StandardCharsets.UTF_8);
    }
}
