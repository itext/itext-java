package com.itextpdf.model.numbering;

/**
 * This class is responsible for converting integer numbers to their
 * English alphabet letter representations.
 */
public class EnglishAlphabetNumbering {

    protected static final char[] ALPHABET_LOWERCASE;
    protected static final char[] ALPHABET_UPPERCASE;
    protected static final int ALPHABET_LENGTH = 26;

    static {
        ALPHABET_LOWERCASE = new char[ALPHABET_LENGTH];
        ALPHABET_UPPERCASE = new char[ALPHABET_LENGTH];
        for (int i = 0; i < ALPHABET_LENGTH; i++) {
            ALPHABET_LOWERCASE[i] = (char) ('a' + i);
            ALPHABET_UPPERCASE[i] = (char) ('A' + i);
        }
    }

    /**
     * Converts the given number to its English alphabet lowercase string representation.
     * E.g. 1 will be converted to "a", 2 to "b", ..., 27 to "aa", and so on.
     * @param number the number to be converted
     */
    public static String toLatinAlphabetNumberLowerCase(int number) {
        return AlphabetNumbering.toAlphabetNumber(number, ALPHABET_LOWERCASE);
    }

    /**
     * Converts the given number to its English alphabet lowercase string representation.
     * E.g. 1 will be converted to "A", 2 to "B", ..., 27 to "AA", and so on.
     * @param number the number to be converted
     */
    public static String toLatinAlphabetNumberUpperCase(int number) {
        return AlphabetNumbering.toAlphabetNumber(number, ALPHABET_UPPERCASE);
    }

    /**
     * Converts the given number to its English alphabet string representation.
     * E.g. for <code>upperCase</code> set to false,
     * 1 will be converted to "a", 2 to "b", ..., 27 to "aa", and so on.
     * @param number the number to be converted
     * @param upperCase whether to use uppercase or lowercase alphabet
     */
    public static String toLatinAlphabetNumber(int number, boolean upperCase) {
        return upperCase ? toLatinAlphabetNumberUpperCase(number) : toLatinAlphabetNumberLowerCase(number);
    }

}
