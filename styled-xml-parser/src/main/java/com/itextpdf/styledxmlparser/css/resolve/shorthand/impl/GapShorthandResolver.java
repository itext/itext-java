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
import com.itextpdf.styledxmlparser.css.validate.CssDeclarationValidationMaster;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shorthand resolver for gap shorthand properties, can be used for
 * different gap properties like {@code gap} or {@code grid-gap}.
 */
public class GapShorthandResolver implements IShorthandResolver {
    private final String gapShorthandProperty;

    /**
     * Instantiates default {@link GapShorthandResolver} for {@code gap} shorthand.
     */
    public GapShorthandResolver() {
        this(CommonCssConstants.GAP);
    }

    /**
     * Instantiates default {@link GapShorthandResolver} for passed gap shorthand.
     *
     * @param gapShorthandProperty the name of the gap shorthand property
     */
    public GapShorthandResolver(String gapShorthandProperty) {
        this.gapShorthandProperty = gapShorthandProperty;
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GapShorthandResolver.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        shorthandExpression = shorthandExpression.trim();
        if (CssTypesValidationUtils.isInitialOrInheritOrUnset(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(CommonCssConstants.ROW_GAP, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.COLUMN_GAP, shorthandExpression)
            );
        }
        if (CssTypesValidationUtils.containsInitialOrInheritOrUnset(shorthandExpression)) {
            return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    gapShorthandProperty, shorthandExpression);
        }
        if (shorthandExpression.isEmpty()) {
            return handleExpressionError(StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY,
                    gapShorthandProperty, shorthandExpression);
        }

        final String[] gapProps = shorthandExpression.split(" ");

        if (gapProps.length == 1) {
            return resolveGapWithTwoProperties(gapProps[0], gapProps[0]);
        } else if (gapProps.length == 2) {
            return resolveGapWithTwoProperties(gapProps[0], gapProps[1]);
        } else {
            return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    gapShorthandProperty, shorthandExpression);

        }
    }

    private List<CssDeclaration> resolveGapWithTwoProperties(String row, String column) {
        CssDeclaration rowGapDeclaration = new CssDeclaration(CommonCssConstants.ROW_GAP, row);
        if (!CssDeclarationValidationMaster.checkDeclaration(rowGapDeclaration)) {
            return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    CommonCssConstants.ROW_GAP, row);
        }
        CssDeclaration columnGapDeclaration = new CssDeclaration(CommonCssConstants.COLUMN_GAP, column);
        if (!CssDeclarationValidationMaster.checkDeclaration(columnGapDeclaration)) {
            return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    CommonCssConstants.COLUMN_GAP, column);
        }
        return Arrays.asList(rowGapDeclaration, columnGapDeclaration);
    }

    private static List<CssDeclaration> handleExpressionError(String logMessage, String attribute,
            String shorthandExpression) {
        LOGGER.warn(MessageFormatUtil.format(logMessage, attribute, shorthandExpression));
        return Collections.<CssDeclaration>emptyList();
    }
}
