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
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssEnumValidator;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Shorthand resolver for the column-rule property.
 * This property is a shorthand for the column-rule-width, column-rule-style, and column-rule-color  properties.
 */
public class ColumnRuleShortHandResolver implements IShorthandResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(ColumnRuleShortHandResolver.class);
    private final CssEnumValidator borderStyleValidators = new CssEnumValidator(CommonCssConstants.BORDER_STYLE_VALUES);
    private final CssEnumValidator borderWithValidators = new CssEnumValidator(CommonCssConstants.BORDER_WIDTH_VALUES);

    /**
     * Creates a new {@link ColumnsShorthandResolver} instance.
     */
    public ColumnRuleShortHandResolver() {
        //empty constructor
    }

    /**
     * Resolves a shorthand expression.
     *
     * @param shorthandExpression the shorthand expression
     *
     * @return a list of CSS declaration
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        shorthandExpression = shorthandExpression.trim();
        if (CssTypesValidationUtils.isInitialOrInheritOrUnset(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(CommonCssConstants.COLUMN_RULE_COLOR, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.COLUMN_RULE_WIDTH, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.COLUMN_RULE_STYLE, shorthandExpression)
            );
        }
        if (CssTypesValidationUtils.containsInitialOrInheritOrUnset(shorthandExpression)) {
            return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    CommonCssConstants.COLUMN_RULE, shorthandExpression);
        }
        if (shorthandExpression.isEmpty()) {
            return handleExpressionError(StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY,
                    CommonCssConstants.COLUMN_RULE, shorthandExpression);
        }

        final int maxProperties = 3;
        List<String> properties = CssUtils.extractShorthandProperties(shorthandExpression).get(0);
        if (properties.size() > maxProperties) {
            return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    CommonCssConstants.COLUMN_RULE, shorthandExpression);
        }
        List<CssDeclaration> result = new ArrayList<>(maxProperties);
        for (String property : properties) {
            String cleanProperty = property.trim();
            CssDeclaration declaration = processProperty(cleanProperty);
            if (declaration != null) {
                result.add(declaration);
            }
            if (declaration == null) {
                return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.COLUMN_RULE_STYLE, shorthandExpression);
            }
        }

        return result;
    }

    private CssDeclaration processProperty(String value) {
        if (CssTypesValidationUtils.isMetricValue(value) || CssTypesValidationUtils.isRelativeValue(value)
                || borderWithValidators.isValid(value)) {
            return new CssDeclaration(CommonCssConstants.COLUMN_RULE_WIDTH, value);
        }
        if (CssTypesValidationUtils.isColorProperty(value)) {
            return new CssDeclaration(CommonCssConstants.COLUMN_RULE_COLOR, value);
        }
        if (borderStyleValidators.isValid(value)) {
            return new CssDeclaration(CommonCssConstants.COLUMN_RULE_STYLE, value);
        }
        return null;
    }

    private static List<CssDeclaration> handleExpressionError(String logMessage, String attribute,
            String shorthandExpression) {
        LOGGER.warn(MessageFormatUtil.format(logMessage, attribute, shorthandExpression));
        return Collections.<CssDeclaration>emptyList();
    }
}
