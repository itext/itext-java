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
package com.itextpdf.layout.hyphenation;

/**
 * This is the class used to configure hyphenation on layout level
 */
public class HyphenationConfig {

    /**
     * The Hyphenator object.
     */
    protected Hyphenator hyphenator;

    /**
     * The hyphenation symbol used when hyphenating.
     */
    protected char hyphenSymbol = '-';

    /**
     * Constructs a new {@link HyphenationConfig}. No language hyphenation files will be used.
     * Only soft hyphen symbols ('\u00ad') will be taken into account.
     * @param leftMin the minimum number of characters before the hyphenation point
     * @param rightMin the minimum number of characters after the hyphenation point
     */
    public HyphenationConfig(int leftMin, int rightMin) {
        this.hyphenator = new Hyphenator(null, null, leftMin, rightMin);
    }

    /**
     * Constructs a new {@link HyphenationConfig} by a {@link Hyphenator} which will be used to
     * find hyphenation points.
     * @param hyphenator the {@link Hyphenator} instance
     */
    public HyphenationConfig(Hyphenator hyphenator) {
        this.hyphenator = hyphenator;
    }

    /**
     * Constructs a new {@link HyphenationConfig} instance.
     * @param lang the language
     * @param country the optional country code (may be null or "none")
     * @param leftMin the minimum number of characters before the hyphenation point
     * @param rightMin the minimum number of characters after the hyphenation point
     */
    public HyphenationConfig(String lang, String country, int leftMin, int rightMin) {
        this.hyphenator = new Hyphenator(lang, country, leftMin, rightMin);
    }

    /**
     * Hyphenates a given word.
     *
     * @return {@link Hyphenation} object representing possible hyphenation points
     * or {@code null} if no hyphenation points are found.
     *
     * @param word Tee word to hyphenate
     */
    public Hyphenation hyphenate(String word) {
        return hyphenator != null ? hyphenator.hyphenate(word) : null;
    }

    /**
     * Gets the hyphenation symbol.
     *
     * @return the hyphenation symbol
     */
    public char getHyphenSymbol() {
        return hyphenSymbol;
    }

    /**
     * Sets the hyphenation symbol to the specified value.
     *
     * @param hyphenSymbol the new hyphenation symbol
     */
    public void setHyphenSymbol(char hyphenSymbol) {
        this.hyphenSymbol = hyphenSymbol;
    }
}
