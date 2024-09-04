/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.numbering;

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
     * @param number a number to be converted to roman notation
     * @return a lower case roman representation of an integer
     */
    public static String toRomanLowerCase(int number) {
        return convert(number);
    }

    /**
     * Returns an upper case roman representation of an integer.
     *
     * @param number a number to be converted to roman notation
     * @return an upper case roman representation of an integer
     */
    public static String toRomanUpperCase(int number) {
        return convert(number).toUpperCase();
    }

    /**
     * Returns a roman representation of an integer.
     *
     * @param number     a number to be converted to roman notation
     * @param upperCase <code>true</code> for upper case representation,
     *                  <code>false</code> for lower case one
     * @return a roman representation of an integer
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

        if (index >= 4000) {
            buf.append('|');
            buf.append(convert(index / 1000));
            buf.append('|');
            // remainder
            index = index - (index / 1000) * 1000;
        }

        // number between 1 and 3999
        int pos = 0;
        while (true) {
            // loop over the array with values for m-d-c-l-x-v-i
            RomanDigit dig = ROMAN_DIGITS[pos];
            // adding as many digits as we can
            while (index >= dig.getValue()) {
                buf.append(dig.getDigit());
                index -= dig.getValue();
            }
            // we have the complete number
            if (index <= 0) {
                break;
            }
            // look for the next digit that can be used in a special way
            int j = pos;
            while (!ROMAN_DIGITS[++j].isPre()) ;

            // does the special notation apply?
            if (index + ROMAN_DIGITS[j].getValue() >= dig.getValue()) {
                buf.append(ROMAN_DIGITS[j].getDigit()).append(dig.getDigit());
                index -= dig.getValue() - ROMAN_DIGITS[j].getValue();
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
        private final char digit;

        /**
         * value of the roman digit
         */
        private final int value;

        /**
         * can the digit be used as a prefix
         */
        private final boolean pre;

        /**
         * Constructs a roman digit
         *
         * @param digit the roman digit
         * @param value the value
         * @param pre   can it be used as a prefix
         */
        RomanDigit(char digit, int value, boolean pre) {
            this.digit = digit;
            this.value = value;
            this.pre = pre;
        }

        /**
         * Retrieves the roman digit.
         *
         * @return roman digit
         */
        public char getDigit() {
            return digit;
        }

        /**
         * Retrieves the value of the roman digit.
         *
         * @return value
         */
        public int getValue() {
            return value;
        }

        /**
         * Retrieves whether the roman digit can be used as prefix.
         *
         * @return true if it can, false otherwise
         */
        public boolean isPre() {
            return pre;
        }
    }
}
