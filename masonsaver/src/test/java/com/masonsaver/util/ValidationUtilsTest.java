package com.masonsaver.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * ValidationUtilsTest
 *
 * Unit tests for isValidEmail() and isPasswordStrong() in ValidationUtils.
 * No mocking needed since these are pure static utility methods
 * with no external dependencies.
 * Run with: mvn test
 */
public class ValidationUtilsTest {

    /* ── isValidEmail() tests ── */

    /**
     * A standard well-formed email address should return true.
     */
    @Test
    void isValidEmail_withValidEmail_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("jane@example.com"));
    }

    /**
     * An email missing the at-sign should return false.
     */
    @Test
    void isValidEmail_withMissingAtSign_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail("janeexample.com"));
    }

    /**
     * An email missing the domain after the at-sign should return false.
     */
    @Test
    void isValidEmail_withMissingDomain_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail("jane@"));
    }

    /**
     * A null input should return false without throwing an exception.
     */
    @Test
    void isValidEmail_withNullInput_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail(null));
    }

    /**
     * An empty string should return false.
     */
    @Test
    void isValidEmail_withEmptyString_returnsFalse() {
        assertFalse(ValidationUtils.isValidEmail(""));
    }

    /**
     * An email with a subdomain should be considered valid.
     */
    @Test
    void isValidEmail_withSubdomain_returnsTrue() {
        assertTrue(ValidationUtils.isValidEmail("jane@mail.example.com"));
    }

    /* ── isPasswordStrong() tests ── */

    /**
     * A password of exactly 8 characters should return true.
     */
    @Test
    void isPasswordStrong_withEightCharacters_returnsTrue() {
        assertTrue(ValidationUtils.isPasswordStrong("abcde123"));
    }

    /**
     * A password of 7 characters should return false.
     */
    @Test
    void isPasswordStrong_withSevenCharacters_returnsFalse() {
        assertFalse(ValidationUtils.isPasswordStrong("abcde12"));
    }

    /**
     * A null input should return false without throwing an exception.
     */
    @Test
    void isPasswordStrong_withNullInput_returnsFalse() {
        assertFalse(ValidationUtils.isPasswordStrong(null));
    }

    /**
     * An empty string should return false.
     */
    @Test
    void isPasswordStrong_withEmptyString_returnsFalse() {
        assertFalse(ValidationUtils.isPasswordStrong(""));
    }

    /**
     * A long password well above the minimum should return true.
     */
    @Test
    void isPasswordStrong_withLongPassword_returnsTrue() {
        assertTrue(ValidationUtils.isPasswordStrong("thisIsAVeryLongPasswordThatShouldPass"));
    }

    /* ── isValidISBN() tests ── */

    /**
     * A well-formed 10-digit ISBN with a correct checksum should return true.
     */
    @Test
    void isValidISBN_withValidISBN10_returnsTrue() {
        assertTrue(ValidationUtils.isValidISBN("0306406152"));
    }

    /**
     * A 10-digit ISBN whose check digit is 'X' (representing 10) should
     * return true.
     */
    @Test
    void isValidISBN_withISBN10CheckDigitX_returnsTrue() {
        assertTrue(ValidationUtils.isValidISBN("080442957X"));
    }

    /**
     * A well-formed 13-digit ISBN with a correct checksum should return true.
     */
    @Test
    void isValidISBN_withValidISBN13_returnsTrue() {
        assertTrue(ValidationUtils.isValidISBN("9780306406157"));
    }

    /**
     * A valid ISBN-13 formatted with hyphens should still return true,
     * since hyphens are treated as formatting and ignored.
     */
    @Test
    void isValidISBN_withHyphenatedISBN13_returnsTrue() {
        assertTrue(ValidationUtils.isValidISBN("978-0-306-40615-7"));
    }

    /**
     * A 10-digit string with an incorrect final check digit should return
     * false, even though its length is correct.
     */
    @Test
    void isValidISBN_withBadISBN10Checksum_returnsFalse() {
        assertFalse(ValidationUtils.isValidISBN("0306406153"));
    }

    /**
     * A 13-digit string with an incorrect final check digit should return
     * false, even though its length is correct.
     */
    @Test
    void isValidISBN_withBadISBN13Checksum_returnsFalse() {
        assertFalse(ValidationUtils.isValidISBN("9780306406158"));
    }

    /**
     * An 'X' appearing anywhere other than the final check digit position
     * should return false.
     */
    @Test
    void isValidISBN_withMisplacedX_returnsFalse() {
        assertFalse(ValidationUtils.isValidISBN("030X406152"));
    }

    /**
     * A string of the wrong length (neither 10 nor 13 digits) should return
     * false.
     */
    @Test
    void isValidISBN_withWrongLength_returnsFalse() {
        assertFalse(ValidationUtils.isValidISBN("12345"));
    }

    /**
     * A string containing non-digit characters should return false.
     */
    @Test
    void isValidISBN_withLetters_returnsFalse() {
        assertFalse(ValidationUtils.isValidISBN("97803064061AB"));
    }

    /**
     * A null input should return false without throwing an exception.
     */
    @Test
    void isValidISBN_withNullInput_returnsFalse() {
        assertFalse(ValidationUtils.isValidISBN(null));
    }

    /**
     * An empty string should return false.
     */
    @Test
    void isValidISBN_withEmptyString_returnsFalse() {
        assertFalse(ValidationUtils.isValidISBN(""));
    }
}