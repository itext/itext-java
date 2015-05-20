package com.itextpdf.model.numbering;

/**
 * This class can produce String combinations representing a roman number.
 * The first roman numbers are: I, II, III, IV, V, VI, VII, VIII, IX, X
 * See http://en.wikipedia.org/wiki/Roman_numerals
 */
public class RomanNumbering {

    /**
     * Array with Roman digits.
     */
    private static final RomanDigit[] ROMAN_DIGITS = {
            new RomanDigit('m', 1000, false),
            new RomanDigit('d', 500, false),
            new RomanDigit('c', 100, true),
            new RomanDigit('l', 50, false),
            new RomanDigit('x', 10, true),
            new RomanDigit('v', 5, false),
            new RomanDigit('i', 1, true)
    };

    /**
     * Returns a lower case roman representation of an integer.
     *
     * @param number a number to be converted to roman notation.
     */
    public static String toRomanLowerCase(int number) {
        return convert(number);
    }

    /**
     * Returns an upper case roman representation of an integer.
     *
     * @param number a number to be converted to roman notation.
     */
    public static String toRomanUpperCase(int number) {
        return convert(number).toUpperCase();
    }

    /**
     * Returns a roman representation of an integer.
     *
     * @param number     a number to be converted to roman notation.
     * @param upperCase <code>true</code> for upper case representation,
     *                  <code>false</code> for lower case one.
     */
    public static String toRoman(int number, boolean upperCase) {
        return upperCase ? toRomanUpperCase(number) : toRomanLowerCase(number);
    }

    /**
     * Returns a roman representation of an integer.
     *
     * @param index the original number
     * @return the roman number representation (lower case)
     */
    protected static String convert(int index) {
        StringBuilder buf = new StringBuilder();

        // lower than 0 ? Add minus
        if (index < 0) {
            buf.append('-');
            index = -index;
        }

        // greater than 3000
        if (index > 3000) {
            buf.append('|');
            buf.append(convert(index / 1000));
            buf.append('|');
            // remainder
            index = index - (index / 1000) * 1000;
        }

        // number between 1 and 3000
        int pos = 0;
        while (true) {
            // loop over the array with values for m-d-c-l-x-v-i
            RomanDigit dig = ROMAN_DIGITS[pos];
            // adding as many digits as we can
            while (index >= dig.value) {
                buf.append(dig.digit);
                index -= dig.value;
            }
            // we have the complete number
            if (index <= 0) {
                break;
            }
            // look for the next digit that can be used in a special way
            int j = pos;
            while (!ROMAN_DIGITS[++j].pre) ;

            // does the special notation apply?
            if (index + ROMAN_DIGITS[j].value >= dig.value) {
                buf.append(ROMAN_DIGITS[j].digit).append(dig.digit);
                index -= dig.value - ROMAN_DIGITS[j].value;
            }
            pos++;
        }
        return buf.toString();
    }

    /**
     * Helper class for Roman Digits
     */
    private static class RomanDigit {

        /**
         * part of a roman number
         */
        public char digit;

        /**
         * value of the roman digit
         */
        public int value;

        /**
         * can the digit be used as a prefix
         */
        public boolean pre;

        /**
         * Constructs a roman digit
         *
         * @param digit the roman digit
         * @param value the value
         * @param pre   can it be used as a prefix
         */
        RomanDigit(final char digit, final int value, final boolean pre) {
            this.digit = digit;
            this.value = value;
            this.pre = pre;
        }
    }
}
