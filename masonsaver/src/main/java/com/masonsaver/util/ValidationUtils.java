package com.masonsaver.util;

import java.util.regex.Pattern;

/**
 * ValidationUtils
 *
 * A collection of static utility methods for validating user input
 * before it is processed by the application layer.
 *
 * All methods in this class are stateless and have no external dependencies.
 * They can be called directly without instantiating the class.
 *
 * Used by AuthController to validate email and password fields
 * during user registration before any database operations occur.
 */
public class ValidationUtils {

    /**
     * Regular expression pattern for validating email addresses.
     * Checks for a local part, an at-sign, a domain name, and a
     * top-level domain of at least two characters.
     * Example matches: jane@example.com, jane@mail.gmu.edu
     */
    private static final Pattern EMAIL_PATTERN =
        Pattern.compile("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$");

    /**
     * Minimum number of characters required for a valid password.
     */
    private static final int MIN_PASSWORD_LENGTH = 8;

    /** Number of characters in an ISBN-10. */
    private static final int ISBN_10_LENGTH = 10;

    /** Number of characters in an ISBN-13. */
    private static final int ISBN_13_LENGTH = 13;

    /** The weighted sum of a valid ISBN-10 is evenly divisible by this value. */
    private static final int ISBN_10_MODULUS = 11;

    /** The weighted sum of a valid ISBN-13 is evenly divisible by this value. */
    private static final int ISBN_13_MODULUS = 10;

    /** Character that may stand in for the value 10 as an ISBN-10 check digit. */
    private static final char ISBN_10_CHECK_DIGIT_X = 'X';

    /** Numeric value represented by the ISBN-10 'X' check digit. */
    private static final int ISBN_10_X_VALUE = 10;

    /** Weight applied to the even-indexed digits of an ISBN-13. */
    private static final int ISBN_13_EVEN_WEIGHT = 1;

    /** Weight applied to the odd-indexed digits of an ISBN-13. */
    private static final int ISBN_13_ODD_WEIGHT = 3;

    /**
     * isValidEmail
     *
     * Validates whether a given string is a properly formatted email address.
     *
     * Uses a regular expression to check for the presence of a local part,
     * an at-sign, a domain name, and a top-level domain of at least two
     * characters. Does not verify whether the address actually exists or
     * whether the domain is reachable.
     *
     * Called by AuthController.register() to guard against malformed email
     * inputs before passing data to the database layer.
     *
     * @param email the string to validate as an email address
     * @return true if the string matches a valid email format,
     *         false if it is null, empty, or does not match the pattern
     */
    public static boolean isValidEmail(String email) {
        /* Null or empty input is never a valid email */
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        return EMAIL_PATTERN.matcher(email.trim()).matches();
    }

    /**
     * isPasswordStrong
     *
     * Validates whether a given password meets the minimum strength
     * requirements for a MasonSaver account.
     *
     * A password is considered strong if it is at least 8 characters long.
     * This method is called by AuthController.register() before hashing
     * and persisting the password, ensuring weak passwords are rejected
     * at the application layer before reaching the database.
     *
     * @param password the plain text password string to validate
     * @return true if the password is at least 8 characters long,
     *         false if it is null, empty, or shorter than 8 characters
     */
    public static boolean isPasswordStrong(String password) {
        /* Null or empty input fails immediately */
        if (password == null || password.isEmpty()) {
            return false;
        }
        return password.length() >= MIN_PASSWORD_LENGTH;
    }

    /**
     * isValidISBN
     *
     * Validates whether a given string is a structurally valid ISBN,
     * supporting both the older 10-digit and the current 13-digit formats.
     *
     * The check goes beyond simple length: it verifies the ISBN's checksum
     * (the final "check digit"), which is mathematically derived from the
     * preceding digits. This catches common entry mistakes such as a single
     * mistyped or transposed digit that a length-only check would miss.
     *
     * Hyphens and surrounding whitespace are treated as formatting and are
     * ignored, so both "978-0-306-40615-7" and "9780306406157" are accepted.
     *
     * Called before an ISBN is used to search the local catalog or any
     * external price source, ensuring malformed input is rejected up front.
     *
     * @param isbn the ISBN string to validate, in 10- or 13-digit form,
     *             optionally containing hyphens or spaces
     * @return true if the string is a valid ISBN-10 or ISBN-13,
     *         false if it is null, the wrong length, contains illegal
     *         characters, or fails its checksum
     */
    public static boolean isValidISBN(String isbn) {
        /* Null input is never a valid ISBN */
        if (isbn == null) {
            return false;
        }
        /* Strip hyphens and whitespace, which are only formatting */
        String normalized = isbn.replaceAll("[\\s-]", "");
        if (normalized.length() == ISBN_10_LENGTH) {
            return isValidISBN10(normalized);
        }
        if (normalized.length() == ISBN_13_LENGTH) {
            return isValidISBN13(normalized);
        }
        /* Any other length cannot be a valid ISBN */
        return false;
    }

    /**
     * isValidISBN10
     *
     * Verifies the checksum of a candidate ISBN-10 string that has already
     * been stripped of hyphens and whitespace.
     *
     * An ISBN-10 is exactly {@code ISBN_10_LENGTH} characters long. Each
     * character carries a weight equal to its distance from the end of the
     * string: the first character is weighted by {@code ISBN_10_LENGTH}, the
     * next by one less, and so on down to a weight of one for the final
     * check digit. The first characters must be digits; only the final check
     * digit may instead be {@code ISBN_10_CHECK_DIGIT_X}, which stands for
     * {@code ISBN_10_X_VALUE}. The number is valid when the sum of every
     * character's value times its weight is evenly divisible by
     * {@code ISBN_10_MODULUS}.
     *
     * Any non-digit character, or an 'X' anywhere other than the final
     * position, makes the candidate invalid.
     *
     * @param isbn a normalized candidate ISBN of length {@code ISBN_10_LENGTH}
     * @return true if the checksum is valid, false otherwise
     */
    private static boolean isValidISBN10(String isbn) {
        int weightedSum = 0;
        for (int position = 0; position < ISBN_10_LENGTH; position++) {
            char character = isbn.charAt(position);
            int weight = ISBN_10_LENGTH - position;
            int finalPosition = ISBN_10_LENGTH - 1;

            int value;
            if (character >= '0' && character <= '9') {
                value = character - '0';
            } else if (character == ISBN_10_CHECK_DIGIT_X && position == finalPosition) {
                value = ISBN_10_X_VALUE;
            } else {
                return false;
            }

            weightedSum += value * weight;
        }
        return weightedSum % ISBN_10_MODULUS == 0;
    }

    /**
     * isValidISBN13
     *
     * Verifies the checksum of a candidate ISBN-13 string that has already
     * been stripped of hyphens and whitespace.
     *
     * An ISBN-13 is exactly {@code ISBN_13_LENGTH} characters and, unlike an
     * ISBN-10, every character must be a digit. Characters at even indices
     * carry a weight of {@code ISBN_13_EVEN_WEIGHT} and characters at odd
     * indices a weight of {@code ISBN_13_ODD_WEIGHT}. The number is valid
     * when the sum of every digit times its weight is evenly divisible by
     * {@code ISBN_13_MODULUS}.
     *
     * @param isbn a normalized candidate ISBN of length {@code ISBN_13_LENGTH}
     * @return true if the checksum is valid, false otherwise
     */
    private static boolean isValidISBN13(String isbn) {
        int weightedSum = 0;
        for (int position = 0; position < ISBN_13_LENGTH; position++) {
            char character = isbn.charAt(position);
            if (character < '0' || character > '9') {
                return false;
            }

            int value = character - '0';
            boolean evenIndex = position % 2 == 0;
            int weight = evenIndex ? ISBN_13_EVEN_WEIGHT : ISBN_13_ODD_WEIGHT;

            weightedSum += value * weight;
        }
        return weightedSum % ISBN_13_MODULUS == 0;
    }
}