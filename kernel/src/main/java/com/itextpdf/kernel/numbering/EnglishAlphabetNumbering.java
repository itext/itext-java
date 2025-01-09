/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
     *
     * @param number the number greater than zero to be converted
     * @return English alphabet lowercase string representation of an integer
     */
    public static String toLatinAlphabetNumberLowerCase(int number) {
        return AlphabetNumbering.toAlphabetNumber(number, ALPHABET_LOWERCASE);
    }

    /**
     * Converts the given number to its English alphabet uppercase string representation.
     * E.g. 1 will be converted to "A", 2 to "B", ..., 27 to "AA", and so on.
     *
     * @param number the number greater than zero to be converted
     * @return English alphabet uppercase string representation of an integer
     */
    public static String toLatinAlphabetNumberUpperCase(int number) {
        return AlphabetNumbering.toAlphabetNumber(number, ALPHABET_UPPERCASE);
    }

    /**
     * Converts the given number to its English alphabet string representation.
     * E.g. for <code>upperCase</code> set to false,
     * 1 will be converted to "a", 2 to "b", ..., 27 to "aa", and so on.
     *
     * @param number    the number greater than zero to be converted
     * @param upperCase whether to use uppercase or lowercase alphabet
     * @return English alphabet string representation of an integer
     */
    public static String toLatinAlphabetNumber(int number, boolean upperCase) {
        return upperCase ? toLatinAlphabetNumberUpperCase(number) : toLatinAlphabetNumberLowerCase(number);
    }

}
