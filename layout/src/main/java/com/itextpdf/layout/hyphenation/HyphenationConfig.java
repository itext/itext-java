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
