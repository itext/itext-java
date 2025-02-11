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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Abstract {@link IShorthandResolver} implementation for borders.
 */
public abstract class AbstractBorderShorthandResolver implements IShorthandResolver {

    /** The template for -width properties. */
    private static final String _0_WIDTH = "{0}-width";

    /** The template for -style properties. */
    private static final String _0_STYLE = "{0}-style";

    /** The template for -color properties. */
    private static final String _0_COLOR = "{0}-color";

    /**
     * Gets the prefix of a property.
     *
     * @return the prefix
     */
    protected abstract String getPrefix();

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver#resolveShorthand(java.lang.String)
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        String widthPropName = MessageFormatUtil.format(_0_WIDTH, getPrefix());
        String stylePropName = MessageFormatUtil.format(_0_STYLE, getPrefix());
        String colorPropName = MessageFormatUtil.format(_0_COLOR, getPrefix());

        if (CommonCssConstants.INITIAL.equals(shorthandExpression) || CommonCssConstants.INHERIT.equals(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(widthPropName, shorthandExpression),
                    new CssDeclaration(stylePropName, shorthandExpression),
                    new CssDeclaration(colorPropName, shorthandExpression));
        }

        List<String> props = CssUtils.extractShorthandProperties(shorthandExpression).get(0);

        String borderColorValue = null;
        String borderStyleValue = null;
        String borderWidthValue = null;

        for (String value : props) {
            if (CommonCssConstants.INITIAL.equals(value) || CommonCssConstants.INHERIT.equals(value)) {
                Logger logger = LoggerFactory.getLogger(AbstractBorderShorthandResolver.class);
                logger.warn(MessageFormatUtil.format(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        shorthandExpression));
                return Collections.<CssDeclaration>emptyList();
            }
            if (CommonCssConstants.BORDER_WIDTH_VALUES.contains(value) || CssTypesValidationUtils.isNumber(value)
                    || CssTypesValidationUtils.isMetricValue(value) || CssTypesValidationUtils.isRelativeValue(value)) {
                borderWidthValue = value;
            } else if (CommonCssConstants.BORDER_STYLE_VALUES.contains(value) || value.equals(CommonCssConstants.AUTO)) { // AUTO property value is needed for outline property only
                borderStyleValue = value;
            } else if (CssTypesValidationUtils.isColorProperty(value)) {
                borderColorValue = value;
            }
        }

        List<CssDeclaration> resolvedDecl = new ArrayList<>();
        resolvedDecl.add(new CssDeclaration(widthPropName, borderWidthValue == null ? CommonCssConstants.INITIAL : borderWidthValue));
        resolvedDecl.add(new CssDeclaration(stylePropName, borderStyleValue == null ? CommonCssConstants.INITIAL : borderStyleValue));
        resolvedDecl.add(new CssDeclaration(colorPropName, borderColorValue == null ? CommonCssConstants.INITIAL : borderColorValue));
        return resolvedDecl;
    }
}
