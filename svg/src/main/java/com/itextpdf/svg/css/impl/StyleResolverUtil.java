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
package com.itextpdf.svg.css.impl;

import com.itextpdf.io.util.DecimalFormatUtil;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.resolve.CssInheritance;
import com.itextpdf.styledxmlparser.css.resolve.CssPropertyMerger;
import com.itextpdf.styledxmlparser.css.resolve.IStyleInheritance;
import com.itextpdf.styledxmlparser.css.util.CssUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Utility class for resolving parent-inheritance of style and attribute declarations
 */
public class StyleResolverUtil {

    private Set<IStyleInheritance> inheritanceRules;

    /**
     * List to store the properties whose value can depend on parent or element font-size
     */
    private static final List<String> fontSizeDependentPercentage = new ArrayList<String>(2);

    static {
        fontSizeDependentPercentage.add(CommonCssConstants.FONT_SIZE);
        fontSizeDependentPercentage.add(CommonCssConstants.LINE_HEIGHT);
    }

    public StyleResolverUtil(){
        this.inheritanceRules = new HashSet<>();
        inheritanceRules.add(new CssInheritance());
        inheritanceRules.add(new SvgAttributeInheritance());
    }

    /**
     * Merge parent style declarations for passed styleProperty into passed style map
     *
     * @param styles          the styles map
     * @param styleProperty   the style property
     * @param parentPropValue the parent properties value
     * @param parentFontSizeString the parent font-size for resolving relative, font-dependent attributes
     */
    public void mergeParentStyleDeclaration(Map<String, String> styles, String styleProperty, String parentPropValue,String parentFontSizeString) {
        String childPropValue = styles.get(styleProperty);

        if ((childPropValue == null && checkInheritance(styleProperty)) || CommonCssConstants.INHERIT.equals(childPropValue)) {
            if (    valueIsOfMeasurement(parentPropValue, CommonCssConstants.EM)
                    || valueIsOfMeasurement(parentPropValue, CommonCssConstants.EX)
                    || (valueIsOfMeasurement(parentPropValue, CommonCssConstants.PERCENTAGE) && fontSizeDependentPercentage.contains(styleProperty))
                    ) {
                float absoluteParentFontSize = CssUtils.parseAbsoluteLength(parentFontSizeString);
                // Format to 4 decimal places to prevent differences between Java and C#
                styles.put(styleProperty, DecimalFormatUtil.formatNumber(CssUtils.parseRelativeValue(parentPropValue, absoluteParentFontSize),
                        "0.####") + CommonCssConstants.PT);
            } else {
                //Property is inherited, add to element style declarations
                styles.put(styleProperty, parentPropValue);
            }
        } else if (CommonCssConstants.TEXT_DECORATION.equals(styleProperty) && !CommonCssConstants.INLINE_BLOCK.equals(styles.get(CommonCssConstants.DISPLAY))) {
            // TODO Note! This property is formally not inherited, but the browsers behave very similar to inheritance here.
                        /* Text decorations on inline boxes are drawn across the entire element,
                            going across any descendant elements without paying any attention to their presence. */
            // Also, when, for example, parent element has text-decoration:underline, and the child text-decoration:overline,
            // then the text in the child will be both overline and underline. This is why the declarations are merged
            // See TextDecorationTest#textDecoration01Test
            styles.put(styleProperty, CssPropertyMerger.mergeTextDecoration(childPropValue, parentPropValue));
        }
    }

    /**
     * Check all inheritance rule-sets to see if the passed property is inheritable
     *
     * @param styleProperty property identifier to check
     * @return True if the property is inheritable by one of the rule-sets,
     * false if it is not marked as inheritable in all rule-sets
     */
    private boolean checkInheritance(String styleProperty) {
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
     * @return True if the value is numerical and ends with the measurement symbol, false otherwise
     */
    private static boolean valueIsOfMeasurement(String value, String measurement) {
        if (value == null)
            return false;
        if (value.endsWith(measurement) && CssUtils.isNumericValue(value.substring(0, value.length() - measurement.length()).trim()))
            return true;
        return false;
    }
}
