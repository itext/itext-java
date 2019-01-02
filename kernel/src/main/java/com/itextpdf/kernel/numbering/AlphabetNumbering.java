/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
