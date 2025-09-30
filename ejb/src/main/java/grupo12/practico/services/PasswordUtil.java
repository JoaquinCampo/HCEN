package grupo12.practico.services;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * Utility class for password hashing and verification.
 * Uses SHA-256 with salt for secure password storage.
 */
public final class PasswordUtil {

    private static final String HASH_ALGORITHM = "SHA-256";
    private static final int SALT_LENGTH = 16; // 128 bits

    private PasswordUtil() {
        // Utility class
    }

    /**
     * Generates a random salt for password hashing.
     *
     * @return Base64 encoded salt
     */
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] salt = new byte[SALT_LENGTH];
        random.nextBytes(salt);
        return Base64.getEncoder().encodeToString(salt);
    }

    /**
     * Hashes a password with the provided salt.
     *
     * @param password Plain text password
     * @param salt     Salt for hashing
     * @return Base64 encoded hash
     */
    public static String hashPassword(String password, String salt) {
        try {
            MessageDigest md = MessageDigest.getInstance(HASH_ALGORITHM);
            md.update(Base64.getDecoder().decode(salt));
            byte[] hashedPassword = md.digest(password.getBytes(StandardCharsets.UTF_8));
            return Base64.getEncoder().encodeToString(hashedPassword);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error hashing password", e);
        }
    }

    /**
     * Verifies a password against a hash and salt.
     *
     * @param password   Plain text password to verify
     * @param storedHash Stored hash
     * @param storedSalt Stored salt
     * @return true if password matches, false otherwise
     */
    public static boolean verifyPassword(String password, String storedHash, String storedSalt) {
        String computedHash = hashPassword(password, storedSalt);
        return MessageDigest.isEqual(
                Base64.getDecoder().decode(computedHash),
                Base64.getDecoder().decode(storedHash));
    }
}
