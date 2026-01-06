/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.commons.utils;

import java.util.Locale;

/**
 * Utility class for string normalization.
 */
public final class StringNormalizer {

    private StringNormalizer() {
        // Empty constructor
    }

    /**
     * Converts a string to lowercase using Root locale.
     *
     * @param str a string to convert
     * @return a converted string
     */
    public static String toLowerCase(String str) {
        if (str == null) {
            return null;
        }

        return str.toLowerCase(Locale.ROOT);
    }

    /**
     * Converts a string to uppercase using Root locale.
     *
     * @param str a string to convert
     * @return a converted string
     */
    public static String toUpperCase(String str) {
        if (str == null) {
            return null;
        }

        return str.toUpperCase(Locale.ROOT);
    }

    /**
     * Converts a string to lowercase using Root locale and trims it.
     *
     * @param str a string to convert
     * @return a converted string
     */
    public static String normalize(String str) {
        if (str == null) {
            return null;
        }

        return toLowerCase(str).trim();
    }
}
