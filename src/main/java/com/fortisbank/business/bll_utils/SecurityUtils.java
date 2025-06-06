package com.fortisbank.business.bll_utils;

 import javax.crypto.SecretKeyFactory;
 import javax.crypto.spec.PBEKeySpec;
 import java.security.NoSuchAlgorithmException;
 import java.security.SecureRandom;
 import java.security.spec.InvalidKeySpecException;
 import java.util.Arrays;
 import java.util.Base64;

 /**
  * SecurityUtils
  * --------------------------
  * This utility class provides methods for securely handling sensitive user inputs,
  * such as passwords and PINs, using strong cryptographic hashing techniques.
  *
  * Key Features:
  * - Uses PBKDF2WithHmacSHA256 for password-based key derivation (a strong hashing algorithm).
  * - Automatically generates a cryptographically secure random salt for each input.
  * - Appends the salt to the hash and stores both as a single Base64-encoded string
  *   in the format: "salt:hash".
  * - Accepts sensitive input as char[] to allow for explicit memory cleanup after use.
  *
  * Passwords and PINs are never stored in plaintext or as String in memory,
  * and all verification is done using re-derived hashes from the original salt.
  *
  * Workflow:
  * - During registration:
  *     → Accept password or PIN as char[]
  *     → Generate a salt
  *     → Derive a hash using PBKDF2
  *     → Clear the input char[] from memory
  *     → Return "salt:hash" string to store in the database
  *
  * - During login:
  *     → Accept password or PIN as char[]
  *     → Extract salt from stored value
  *     → Hash input using same salt
  *     → Compare with stored hash
  *     → Clear input char[] from memory
  *
  * Constants define secure defaults:
  * - HASH_ITERATIONS: Number of PBKDF2 iterations (10,000 by default)
  * - SALT_LENGTH: Length of random salt in bytes
  * - HASH_LENGTH / PIN_HASH_LENGTH / PASSWORD_HASH_LENGTH: Desired output length (in bytes)
  *
  * This approach follows security best practices for storing and verifying credentials.
  */
 public class SecurityUtils {

     public static final String HASHING_ALGORITHM = "PBKDF2WithHmacSHA256";
     public static final int HASH_ITERATIONS = 10000;
     public static final int SALT_LENGTH = 16;
     public static final int PIN_HASH_LENGTH = 32;
     public static final int PASSWORD_HASH_LENGTH = 32;

     /**
      * Generates a random salt.
      *
      * @return a byte array containing the generated salt
      */
     public static byte[] generateSalt() {
         SecureRandom random = new SecureRandom();
         byte[] salt = new byte[SALT_LENGTH];
         random.nextBytes(salt);
         return salt;
     }

     /**
      * Hashes the given char[] input with a specified salt and desired length.
      *
      * @param input the input char[] to hash
      * @param salt the salt to use for hashing
      * @param length the desired length of the hash
      * @return the hashed value as a Base64-encoded string
      * @throws NoSuchAlgorithmException if the specified algorithm is not available
      * @throws InvalidKeySpecException if the specified key specification is invalid
      */
     public static String hash(char[] input, byte[] salt, int length) throws NoSuchAlgorithmException, InvalidKeySpecException {
         PBEKeySpec spec = new PBEKeySpec(input, salt, HASH_ITERATIONS, length * 8);
         SecretKeyFactory factory = SecretKeyFactory.getInstance(HASHING_ALGORITHM);
         byte[] hash = factory.generateSecret(spec).getEncoded();
         // Clear the input char[] after use
         spec.clearPassword();
         return Base64.getEncoder().encodeToString(hash);
     }

     /**
      * Hashes a password (char[]) and returns the salt:hash as a Base64-encoded string.
      *
      * @param password the password to hash
      * @return the salt:hash string
      * @throws Exception if an error occurs during hashing
      */
     public static String hashPassword(char[] password) throws Exception {
         byte[] salt = generateSalt();
         String hash = hash(password, salt, PASSWORD_HASH_LENGTH);
         // Clear the input password array
         Arrays.fill(password, '\0');
         return Base64.getEncoder().encodeToString(salt) + ":" + hash;
     }

     /**
      * Verifies a password against a stored salt:hash value.
      *
      * @param inputPassword the input password to verify
      * @param stored the stored salt:hash value
      * @return true if the password is verified, false otherwise
      * @throws Exception if an error occurs during verification
      */
     public static boolean verifyPassword(char[] inputPassword, String stored) throws Exception {
         String[] parts = stored.split(":");
         byte[] salt = Base64.getDecoder().decode(parts[0]);
         String hashOfInput = hash(inputPassword, salt, PASSWORD_HASH_LENGTH);
         Arrays.fill(inputPassword, '\0'); // Clear after hashing
         return hashOfInput.equals(parts[1]);
     }

     /**
      * Hashes a PIN (char[]) and returns the salt:hash as a Base64-encoded string.
      *
      * @param pin the PIN to hash
      * @return the salt:hash string
      * @throws Exception if an error occurs during hashing
      */
     public static String hashPIN(char[] pin) throws Exception {
         byte[] salt = generateSalt();
         String hash = hash(pin, salt, PIN_HASH_LENGTH);
         Arrays.fill(pin, '\0');
         return Base64.getEncoder().encodeToString(salt) + ":" + hash;
     }

     /**
      * Verifies a PIN against a stored salt:hash value.
      *
      * @param inputPin the input PIN to verify
      * @param stored the stored salt:hash value
      * @return true if the PIN is verified, false otherwise
      * @throws Exception if an error occurs during verification
      */
     public static boolean verifyPIN(char[] inputPin, String stored) throws Exception {
         String[] parts = stored.split(":");
         byte[] salt = Base64.getDecoder().decode(parts[0]);
         String hashOfInput = hash(inputPin, salt, PIN_HASH_LENGTH);
         Arrays.fill(inputPin, '\0');
         return hashOfInput.equals(parts[1]);
     }
 }