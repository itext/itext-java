package com.itextpdf.model.numbering;

/**
 * This is a general class for alphabet numbering.
 * You can specify an alphabet and convert an integer into the corresponding
 * alphabet number representation.
 * E.g.: if the alphabet is English letters 'a' to 'z', then
 * 1 is represented as "a", ..., 26 is represented as "z",
 * 27 is represented as "aa" and so on.
 */
public class AlphabetNumbering {
    /**
     * Translates a positive integer (not equal to zero)
     * into an alphabet number using the letters from the specified alphabet.
     *
     * @param number the number
     * @param alphabet the array containing all possible letters from the alphabet
     * @return a translated number representation
     */
    public static String toAlphabetNumber(int number, char[] alphabet) {
        if (number < 1) {
            throw new IllegalArgumentException("The parameter must be a positive integer");
        }

        int cardinality = alphabet.length;

        number--;
        int bytes = 1;
        int start = 0;
        int symbols = cardinality;

        while (number >= symbols + start) {
            bytes++;
            start += symbols;
            symbols *= cardinality;
        }

        int c = number - start;
        char[] value = new char[bytes];
        while (bytes > 0) {
            value[--bytes] = alphabet[c % cardinality];
            c /= cardinality;
        }

        return new String(value);
    }
}
