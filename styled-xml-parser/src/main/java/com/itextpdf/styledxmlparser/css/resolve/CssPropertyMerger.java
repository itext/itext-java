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
package com.itextpdf.styledxmlparser.css.resolve;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;

/**
 * Utilities class to merge CSS properties.
 */
public final class CssPropertyMerger {

    /**
     * Creates a new {@link CssPropertyMerger} class.
     */
    private CssPropertyMerger() {
    }

    /**
     * Merges text decoration.
     *
     * @param firstValue the first value
     * @param secondValue the second value
     * @return the merged value
     */
    public static String mergeTextDecoration(String firstValue, String secondValue) {
        if (firstValue == null) {
            return secondValue;
        } else if (secondValue == null) {
            return firstValue;
        }

        Set<String> merged = normalizeTextDecoration(firstValue);
        merged.addAll(normalizeTextDecoration(secondValue));

        StringBuilder sb = new StringBuilder();
        for (String mergedProp : merged) {
            if (sb.length() != 0) {
                sb.append(" ");
            }
            sb.append(mergedProp);
        }
        return sb.length() != 0 ? sb.toString() : CommonCssConstants.NONE;
    }

    /**
     * Normalizes text decoration values.
     *
     * @param value the text decoration value
     * @return a set of normalized decoration values
     */
    private static Set<String> normalizeTextDecoration(String value) {
        String[] parts = value.split("\\s+");
        // LinkedHashSet to make order invariant of JVM
        Set<String> merged = new LinkedHashSet<>();
        merged.addAll(Arrays.asList(parts));
        // if none and any other decoration are used together, none is displayed
        if (merged.contains(CommonCssConstants.NONE)) {
            merged.clear();
        }
        return merged;
    }
}
