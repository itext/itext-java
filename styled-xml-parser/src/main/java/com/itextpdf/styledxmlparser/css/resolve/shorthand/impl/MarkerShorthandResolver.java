/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MarkerShorthandResolver implements IShorthandResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(MarkerShorthandResolver.class);

    /**
     * Creates a new {@link MarkerShorthandResolver} instance.
     */
    public MarkerShorthandResolver() {
        //empty constructor
    }

    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        if (CssTypesValidationUtils.isInitialOrInheritOrUnset(shorthandExpression)) {
            return Arrays.asList(new CssDeclaration(CommonCssConstants.MARKER_START, shorthandExpression),
                                 new CssDeclaration(CommonCssConstants.MARKER_MID, shorthandExpression),
                                 new CssDeclaration(CommonCssConstants.MARKER_END, shorthandExpression));
        }
        String expression = shorthandExpression.trim();
        if (expression.isEmpty()) {
            LOGGER.warn(MessageFormatUtil.format(StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY,
                    CommonCssConstants.MARKER));
            return new ArrayList<>();
        }
        if (!expression.startsWith(CommonCssConstants.URL + "(") || !expression.endsWith(")")) {
            LOGGER.warn(MessageFormatUtil.format(
                    StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, shorthandExpression));
            return new ArrayList<>();
        }
        return Arrays.asList(new CssDeclaration(CommonCssConstants.MARKER_START, shorthandExpression),
                new CssDeclaration(CommonCssConstants.MARKER_MID, shorthandExpression),
                new CssDeclaration(CommonCssConstants.MARKER_END, shorthandExpression));
    }
}
