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
