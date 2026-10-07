package com.maan.eway.payment.service.impl;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public final class SmartPayEncryptionUtil {

	private static final int IV_LENGTH = 16;       
    private static final int TAG_LENGTH = 128;
    
    private SmartPayEncryptionUtil() {
        
    }
    
    public static String encrypt(String plainText, String workingKey) throws Exception {

        
        byte[] iv = new byte[IV_LENGTH];

        SecureRandom secureRandom = new SecureRandom();
        secureRandom.nextBytes(iv);

        
        byte[] keyBytes = workingKey.getBytes(StandardCharsets.UTF_8);

        
        SecretKeySpec secretKey =
                new SecretKeySpec(keyBytes, "AES");

        
        Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");

        
        GCMParameterSpec gcmParameterSpec =
                new GCMParameterSpec(TAG_LENGTH, iv);

        
        cipher.init(
                Cipher.ENCRYPT_MODE,
                secretKey,
                gcmParameterSpec
        );

        
        byte[] encryptedBytes =
                cipher.doFinal(
                        plainText.getBytes(StandardCharsets.UTF_8)
                );

        
        String ivHex = bytesToHex(iv);

        
        String encryptedHex = bytesToHex(encryptedBytes);

        
        return ivHex + encryptedHex;
    }
    
    private static String bytesToHex(byte[] bytes) {

        StringBuilder hexString = new StringBuilder(bytes.length * 2);

        for (byte b : bytes) {
            hexString.append(String.format("%02x", b & 0xff));
        }

        return hexString.toString();
    }
    
    public static String decrypt(
            String encryptedText,
            String workingKey) throws Exception {
    	
    	try {
    		encryptedText = encryptedText.trim();

    	    byte[] keyBytes = workingKey.getBytes(StandardCharsets.UTF_8);

    	    SecretKeySpec secretKey =
    	            new SecretKeySpec(keyBytes, "AES");

    	    // IV_LENGTH is in BYTES, but encryptedText is HEX.
    	    int ivHexLength = IV_LENGTH * 2;

    	    String ivHex = encryptedText.substring(0, ivHexLength);
    	    String encryptedHex = encryptedText.substring(ivHexLength);

    	    byte[] iv = hexToBytes(ivHex);
    	    byte[] encryptedBytes = hexToBytes(encryptedHex);

    	    Cipher cipher =
    	            Cipher.getInstance("AES/GCM/NoPadding");

    	    GCMParameterSpec gcmParameterSpec =
    	            new GCMParameterSpec(TAG_LENGTH, iv);

    	    cipher.init(
    	            Cipher.DECRYPT_MODE,
    	            secretKey,
    	            gcmParameterSpec
    	    );

    	    byte[] decryptedBytes =
    	            cipher.doFinal(encryptedBytes);

    	    return new String(
    	            decryptedBytes,
    	            StandardCharsets.UTF_8
    	    );
    	}catch(Exception e) {
    		e.printStackTrace();
    	}

    	return null;
    }
    
    private static byte[] hexToBytes(String hex) {

    	int len = hex.length();

        if (len % 2 != 0) {
            throw new IllegalArgumentException(
                    "Invalid hex string length: " + len
            );
        }

        byte[] bytes = new byte[len / 2];

        for (int i = 0; i < len; i += 2) {
            bytes[i / 2] = (byte) Integer.parseInt(
                    hex.substring(i, i + 2),
                    16
            );
        }

        return bytes;
    }

}
