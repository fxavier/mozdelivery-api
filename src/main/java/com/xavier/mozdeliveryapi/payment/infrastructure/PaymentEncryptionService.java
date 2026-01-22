package com.xavier.mozdeliveryapi.payment.infrastructure;

import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

/**
 * Service for encrypting and decrypting sensitive payment data.
 * Implements PCI DSS compliant encryption for card data and other sensitive information.
 */
@Service
public class PaymentEncryptionService {
    
    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES/ECB/PKCS5Padding";
    
    private final SecretKey encryptionKey;
    private final SecureRandom secureRandom;
    
    public PaymentEncryptionService(@Value("${payment.encryption.key:}") String encryptionKeyString) {
        this.secureRandom = new SecureRandom();
        
        if (encryptionKeyString != null && !encryptionKeyString.isEmpty()) {
            // Use provided key
            byte[] keyBytes = Base64.getDecoder().decode(encryptionKeyString);
            this.encryptionKey = new SecretKeySpec(keyBytes, ALGORITHM);
        } else {
            // Generate a new key (for development/testing only)
            this.encryptionKey = generateKey();
        }
    }
    
    /**
     * Encrypt sensitive payment data.
     */
    public String encrypt(String plainText) {
        try {
            if (plainText == null || plainText.isEmpty()) {
                return plainText;
            }
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.ENCRYPT_MODE, encryptionKey);
            
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(encryptedBytes);
        } catch (Exception e) {
            throw new PaymentEncryptionException("Failed to encrypt payment data", e);
        }
    }
    
    /**
     * Decrypt sensitive payment data.
     */
    public String decrypt(String encryptedText) {
        try {
            if (encryptedText == null || encryptedText.isEmpty()) {
                return encryptedText;
            }
            
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(Cipher.DECRYPT_MODE, encryptionKey);
            
            byte[] encryptedBytes = Base64.getDecoder().decode(encryptedText);
            byte[] decryptedBytes = cipher.doFinal(encryptedBytes);
            
            return new String(decryptedBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new PaymentEncryptionException("Failed to decrypt payment data", e);
        }
    }
    
    /**
     * Encrypt card number with PAN masking.
     */
    public EncryptedCardData encryptCardNumber(String cardNumber) {
        if (cardNumber == null || cardNumber.length() < 6) {
            throw new IllegalArgumentException("Invalid card number");
        }
        
        // Mask the card number (show first 6 and last 4 digits)
        String maskedNumber = maskCardNumber(cardNumber);
        
        // Encrypt the full card number
        String encryptedNumber = encrypt(cardNumber);
        
        return new EncryptedCardData(encryptedNumber, maskedNumber);
    }
    
    /**
     * Generate a secure token for card storage.
     */
    public String generateCardToken() {
        byte[] tokenBytes = new byte[32];
        secureRandom.nextBytes(tokenBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(tokenBytes);
    }
    
    /**
     * Hash sensitive data for comparison purposes.
     */
    public String hashSensitiveData(String data) {
        try {
            java.security.MessageDigest digest = java.security.MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(data.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashBytes);
        } catch (Exception e) {
            throw new PaymentEncryptionException("Failed to hash sensitive data", e);
        }
    }
    
    private SecretKey generateKey() {
        try {
            KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
            keyGenerator.init(256); // Use 256-bit AES key
            return keyGenerator.generateKey();
        } catch (Exception e) {
            throw new PaymentEncryptionException("Failed to generate encryption key", e);
        }
    }
    
    private String maskCardNumber(String cardNumber) {
        if (cardNumber.length() <= 10) {
            return "*".repeat(cardNumber.length());
        }
        
        String first6 = cardNumber.substring(0, 6);
        String last4 = cardNumber.substring(cardNumber.length() - 4);
        String middle = "*".repeat(cardNumber.length() - 10);
        
        return first6 + middle + last4;
    }
    
    /**
     * Record for encrypted card data with masked display.
     */
    public record EncryptedCardData(String encryptedNumber, String maskedNumber) {}
    
    /**
     * Exception for encryption/decryption errors.
     */
    public static class PaymentEncryptionException extends RuntimeException {
        public PaymentEncryptionException(String message, Throwable cause) {
            super(message, cause);
        }
    }
}