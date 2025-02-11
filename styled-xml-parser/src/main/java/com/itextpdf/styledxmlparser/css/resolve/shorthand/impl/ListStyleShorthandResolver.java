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
package com.itextpdf.styledxmlparser.css.resolve.shorthand.impl;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssGradientUtil;
import com.itextpdf.styledxmlparser.css.util.CssUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * {@link IShorthandResolver} implementation for list styles.
 */
public class ListStyleShorthandResolver implements IShorthandResolver {
    
    /** The list style types (disc, decimal,...). */
    private static final Set<String> LIST_STYLE_TYPE_VALUES = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    CommonCssConstants.DISC,
                    CommonCssConstants.ARMENIAN,
                    CommonCssConstants.CIRCLE,
                    CommonCssConstants.CJK_IDEOGRAPHIC,
                    CommonCssConstants.DECIMAL,
                    CommonCssConstants.DECIMAL_LEADING_ZERO,
                    CommonCssConstants.GEORGIAN,
                    CommonCssConstants.HEBREW,
                    CommonCssConstants.HIRAGANA,
                    CommonCssConstants.HIRAGANA_IROHA,
                    CommonCssConstants.LOWER_ALPHA,
                    CommonCssConstants.LOWER_GREEK,
                    CommonCssConstants.LOWER_LATIN,
                    CommonCssConstants.LOWER_ROMAN,
                    CommonCssConstants.NONE,
                    CommonCssConstants.SQUARE,
                    CommonCssConstants.UPPER_ALPHA,
                    CommonCssConstants.UPPER_LATIN,
                    CommonCssConstants.UPPER_ROMAN
    )));
    
    /** The list style positions (inside, outside). */
    private static final Set<String> LIST_STYLE_POSITION_VALUES = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    CommonCssConstants.INSIDE,
                    CommonCssConstants.OUTSIDE
    )));

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver#resolveShorthand(java.lang.String)
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        if (CommonCssConstants.INITIAL.equals(shorthandExpression) || CommonCssConstants.INHERIT.equals(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(CommonCssConstants.LIST_STYLE_TYPE, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.LIST_STYLE_POSITION, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.LIST_STYLE_IMAGE, shorthandExpression));
        }

        List<String> props = CssUtils.extractShorthandProperties(shorthandExpression).get(0);

        String listStyleTypeValue = null;
        String listStylePositionValue = null;
        String listStyleImageValue = null;

        for (String value : props) {
            if (value.contains("url(") || CssGradientUtil.isCssLinearGradientValue(value) ||
                    (CommonCssConstants.NONE.equals(value) && listStyleTypeValue != null)) {
                listStyleImageValue = value;
            } else if (LIST_STYLE_TYPE_VALUES.contains(value)) {
                listStyleTypeValue = value;
            } else if (LIST_STYLE_POSITION_VALUES.contains(value)) {
                listStylePositionValue = value;
            }
        }

        List<CssDeclaration> resolvedDecl = new ArrayList<>();
        resolvedDecl.add(new CssDeclaration(CommonCssConstants.LIST_STYLE_TYPE, listStyleTypeValue == null ? CommonCssConstants.INITIAL : listStyleTypeValue));
        resolvedDecl.add(new CssDeclaration(CommonCssConstants.LIST_STYLE_POSITION, listStylePositionValue == null ? CommonCssConstants.INITIAL : listStylePositionValue));
        resolvedDecl.add(new CssDeclaration(CommonCssConstants.LIST_STYLE_IMAGE, listStyleImageValue == null ? CommonCssConstants.INITIAL : listStyleImageValue));
        return resolvedDecl;
    }
}
