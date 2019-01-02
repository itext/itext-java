/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.numbering;

/**
 * This class can produce String combinations representing an armenian numeral.
 * See https://en.wikipedia.org/wiki/Armenian_numerals
 */
public class ArmenianNumbering {

    private static final ArmenianDigit[] DIGITS = {
            new ArmenianDigit('\u0531', 1),
            new ArmenianDigit('\u0532', 2),
            new ArmenianDigit('\u0533', 3),
            new ArmenianDigit('\u0534', 4),
            new ArmenianDigit('\u0535', 5),
            new ArmenianDigit('\u0536', 6),
            new ArmenianDigit('\u0537', 7),
            new ArmenianDigit('\u0538', 8),
            new ArmenianDigit('\u0539', 9),
            new ArmenianDigit('\u053A', 10),
            new ArmenianDigit('\u053B', 20),
            new ArmenianDigit('\u053C', 30),
            new ArmenianDigit('\u053D', 40),
            new ArmenianDigit('\u053E', 50),
            new ArmenianDigit('\u053F', 60),
            new ArmenianDigit('\u0540', 70),
            new ArmenianDigit('\u0541', 80),
            new ArmenianDigit('\u0542', 90),
            new ArmenianDigit('\u0543', 100),
            new ArmenianDigit('\u0544', 200),
            new ArmenianDigit('\u0545', 300),
            new ArmenianDigit('\u0546', 400),
            new ArmenianDigit('\u0547', 500),
            new ArmenianDigit('\u0548', 600),
            new ArmenianDigit('\u0549', 700),
            new ArmenianDigit('\u054A', 800),
            new ArmenianDigit('\u054B', 900),
            new ArmenianDigit('\u054C', 1000),
            new ArmenianDigit('\u054D', 2000),
            new ArmenianDigit('\u054E', 3000),
            new ArmenianDigit('\u054F', 4000),
            new ArmenianDigit('\u0550', 5000),
            new ArmenianDigit('\u0551', 6000),
            new ArmenianDigit('\u0552', 7000),
            new ArmenianDigit('\u0553', 8000),
            new ArmenianDigit('\u0554', 9000)
    };

    private ArmenianNumbering() {
    }

    /**
     * Returns an armenian numeral representation of an integer.
     *
     * @param number a number greater than zero to be converted to armenian notation
     */
    public static String toArmenian(int number) {
        StringBuilder result = new StringBuilder();
        for (int i = DIGITS.length - 1; i >= 0; i--) {
            ArmenianDigit curDigit = DIGITS[i];
            while (number >= curDigit.value) {
                result.append(curDigit.digit);
                number -= curDigit.value;
            }
        }
        return result.toString();
    }

    private static class ArmenianDigit {
        char digit;
        int value;

        ArmenianDigit(char digit, int value) {
            this.digit = digit;
            this.value = value;
        }
    }

}
