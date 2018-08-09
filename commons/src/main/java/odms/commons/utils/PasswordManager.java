package odms.commons.utils;


import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.util.Arrays;
import java.util.Base64;
import java.util.Random;

/**
 * Takes the password and securely hashes, salts and stores them.
 * Based on the thread https://stackoverflow.com/questions/18142745/how-do-i-generate-a-salt-in-java-for-salted-hash
 */

public final class PasswordManager {

    private static final Random RANDOM = new SecureRandom();
    private static final int ITERATIONS = 10000;
    private static final int KEY_LENGTH = 256;

    private PasswordManager() {
    }

    /**
     * Makes the next salt to be used
     *
     * @return salt
     */
    public static String getNextSalt() {
        byte[] salt = new byte[16];
        RANDOM.nextBytes(salt);
        Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
        return enc.encodeToString(salt);
    }

    /**
     * Takes the password and salt value and hashes and salts the password and returns the hash
     *
     * @param password String password to be hashed in plain text
     * @param salt     byte[] salt to be used should be created by getNextSalt()
     * @return the hashed and salted password
     */
    public static String hash(String password, String salt) {
        char[] passwordChar = password.toCharArray();
        byte[] saltB = salt.getBytes();
        PBEKeySpec spec = new PBEKeySpec(passwordChar, saltB, ITERATIONS, KEY_LENGTH);
        Arrays.fill(passwordChar, Character.MIN_VALUE);
        try {
            SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
            byte[] hash = skf.generateSecret(spec).getEncoded();
            Base64.Encoder enc = Base64.getUrlEncoder().withoutPadding();
            return enc.encodeToString(hash);
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new AssertionError("Error while hashing a password: " + e.getMessage());
        } finally {
            spec.clearPassword();
        }
    }

    /**
     * Takes a password as a string and checks it hash and salt against the known hash and salt to allow logging in.
     *
     * @param password     String password as plain text
     * @param salt         byte[] salt used on password stored in users account
     * @param expectedHash expected value for the hash
     * @return true if matches false otherwise
     */
    public static boolean isExpectedPassword(String password, String salt, String expectedHash) {
        String passwordHash = hash(password, salt);
        return passwordHash.equals(expectedHash);
    }
}

