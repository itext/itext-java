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
package com.itextpdf.styledxmlparser.util;

import com.itextpdf.io.util.DecimalFormatUtil;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.resolve.IStyleInheritance;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for resolving parent-inheritance of style and attribute declarations.
 */
public final class StyleUtil {

    private StyleUtil() {

    }

    /**
     * List to store the properties whose value can depend on parent or element font-size
     */
    private static final List<String> fontSizeDependentPercentage = new ArrayList<String>(2);

    static {
        fontSizeDependentPercentage.add(CommonCssConstants.FONT_SIZE);
        fontSizeDependentPercentage.add(CommonCssConstants.LINE_HEIGHT);
    }

    /**
     * Merge parent CSS declarations.
     *
     * @param styles               the styles map
     * @param styleProperty        the CSS property
     * @param parentPropValue      the parent properties value
     * @param parentFontSizeString is a font size of parent element
     * @param inheritanceRules     set of inheritance rules
     *
     * @return a map of updated styles after merging parent and child style declarations
     */
    public static Map<String, String> mergeParentStyleDeclaration(Map<String, String> styles,
            String styleProperty, String parentPropValue, String parentFontSizeString,
            Set<IStyleInheritance> inheritanceRules) {
        String childPropValue = styles.get(styleProperty);
        if ((childPropValue == null && checkInheritance(styleProperty, inheritanceRules))
                || CommonCssConstants.INHERIT.equals(childPropValue)) {
            if (valueIsOfMeasurement(parentPropValue, CommonCssConstants.EM)
                    || valueIsOfMeasurement(parentPropValue, CommonCssConstants.EX)
                    || valueIsOfMeasurement(parentPropValue, CommonCssConstants.PERCENTAGE)
                    && fontSizeDependentPercentage.contains(styleProperty)) {
                float absoluteParentFontSize = CssDimensionParsingUtils.parseAbsoluteLength(parentFontSizeString);
                // Format to 4 decimal places to prevent differences between Java and C#
                styles.put(styleProperty, DecimalFormatUtil
                        .formatNumber(
                                CssDimensionParsingUtils.parseRelativeValue(parentPropValue, absoluteParentFontSize),
                                "0.####") + CommonCssConstants.PT);
            } else {
                styles.put(styleProperty, parentPropValue);
            }
        }
        return styles;
    }

    /**
     * Check all inheritance rule-sets to see if the passed property is inheritable
     *
     * @param styleProperty    property identifier to check
     * @param inheritanceRules a set of inheritance rules
     *
     * @return True if the property is inheritable by one of the rule-sets,
     * false if it is not marked as inheritable in all rule-sets
     */
    private static boolean checkInheritance(String styleProperty, Set<IStyleInheritance> inheritanceRules) {
        for (IStyleInheritance inheritanceRule : inheritanceRules) {
            if (inheritanceRule.isInheritable(styleProperty)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check to see if the passed value is a measurement of the type based on the passed measurement symbol string
     *
     * @param value       string containing value to check
     * @param measurement measurement symbol (e.g. % for relative, px for pixels)
     *
     * @return True if the value is numerical and ends with the measurement symbol, false otherwise
     */
    private static boolean valueIsOfMeasurement(String value, String measurement) {
        if (value == null) {
            return false;
        }
        return value.endsWith(measurement) && CssTypesValidationUtils
                .isNumber(value.substring(0, value.length() - measurement.length()).trim());
    }
}
