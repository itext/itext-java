/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.svg.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class that facilitates parsing values from CSS.
 */
// TODO DEVSIX-2266

public final class SvgCssUtils {

    private SvgCssUtils() {}

    /**
     * Splits a given String into a list of substrings.
     * The string is split up by commas and whitespace characters (\t, \n, \r, \f).
     *
     * @param value the string to be split
     * @return a list containing the split strings, an empty list if the value is null or empty
     */
    public static List<String> splitValueList(String value) {
        List<String> result = new ArrayList<>();

        if (value != null && value.length() > 0) {
            value = value.trim();

            String[] list = value.split("[,|\\s]");
            for (String element: list) {
                if (!element.isEmpty()) {
                    result.add(element);
                }
            }
        }

        return result;
    }

    /**
     * Converts a float to a String.
     *
     * @param value to be converted float value
     * @return the value in a String representation
     */
    public static String convertFloatToString(float value) {
        return String.valueOf(value);
    }

    /**
     * Converts a double to a String.
     *
     * @param value to be converted double value
     * @return the value in a String representation
     */
    public static String convertDoubleToString(double value) {
        return String.valueOf(value);
    }
}
