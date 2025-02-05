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
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.css.validate.CssDeclarationValidationMaster;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * {@link IShorthandResolver} implementation for background-position.
 */
public class BackgroundPositionShorthandResolver implements IShorthandResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(BackgroundPositionShorthandResolver.class);
    private static final int POSITION_VALUES_MAX_COUNT = 2;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        if (CssTypesValidationUtils.isInitialOrInheritOrUnset(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_X, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_Y, shorthandExpression)
            );
        }
        if (shorthandExpression.trim().isEmpty()) {
            LOGGER.warn(MessageFormatUtil.format(StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY,
                    CommonCssConstants.BACKGROUND_POSITION));
            return new ArrayList<>();
        }

        final List<List<String>> propsList = CssUtils.extractShorthandProperties(shorthandExpression);
        final Map<String, String> resolvedProps = new HashMap<>();

        final Map<String, String> values = new HashMap<>();
        for (final List<String> props : propsList) {
            if (props.isEmpty()) {
                LOGGER.warn(
                        MessageFormatUtil.format(StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY,
                                CommonCssConstants.BACKGROUND_POSITION));
                return new ArrayList<>();
            }
            if (!parsePositionShorthand(props, values)) {
                LOGGER.warn(MessageFormatUtil.format(
                        StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, shorthandExpression));
                return new ArrayList<>();
            }

            updateValue(resolvedProps, values, CommonCssConstants.BACKGROUND_POSITION_X);
            updateValue(resolvedProps, values, CommonCssConstants.BACKGROUND_POSITION_Y);
            values.clear();
        }
        if (!checkProperty(resolvedProps, CommonCssConstants.BACKGROUND_POSITION_X) ||
                !checkProperty(resolvedProps, CommonCssConstants.BACKGROUND_POSITION_Y)) {
            return new ArrayList<>();
        }

        return Arrays.asList(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_X,
                        resolvedProps.get(CommonCssConstants.BACKGROUND_POSITION_X)),
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_Y,
                        resolvedProps.get(CommonCssConstants.BACKGROUND_POSITION_Y))
        );
    }

    private static boolean checkProperty(Map<String, String> resolvedProps, String key) {
        if (!CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(key, resolvedProps.get(key)))) {
            LOGGER.warn(MessageFormatUtil.format(
                    StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, resolvedProps.get(key)));
            return false;
        }
        return true;
    }

    private static void updateValue(Map<String, String> resolvedProps, Map<String, String> values, String key) {
        if (values.get(key) == null) {
            if (resolvedProps.get(key) == null) {
                resolvedProps.put(key, CommonCssConstants.CENTER);
            } else {
                resolvedProps.put(key, resolvedProps.get(key) + "," + CommonCssConstants.CENTER);
            }
        } else {
            if (resolvedProps.get(key) == null) {
                resolvedProps.put(key, values.get(key));
            } else {
                resolvedProps.put(key, resolvedProps.get(key) + "," + values.get(key));
            }
        }
    }

    private static boolean parsePositionShorthand(List<String> valuesToParse, Map<String, String> parsedValues) {
        for (final String positionValue : valuesToParse) {
            if (!parseNonNumericValue(positionValue, parsedValues)) {
                return false;
            }
        }
        for (int i = 0; i < valuesToParse.size(); i++) {
            if (typeOfValue(valuesToParse.get(i)) == BackgroundPositionType.NUMERIC &&
                    !parseNumericValue(i, valuesToParse, parsedValues)) {
                return false;
            }
        }
        return true;
    }

    private static boolean parseNumericValue(int i, List<String> positionValues, Map<String, String> values) {
        if (values.get(CommonCssConstants.BACKGROUND_POSITION_X) == null
                || values.get(CommonCssConstants.BACKGROUND_POSITION_Y) == null) {
            return parseShortNumericValue(i, positionValues, values, positionValues.get(i));
        }
        if (i == 0) {
            return false;
        }
        return parseLargeNumericValue(positionValues.get(i - 1), values, positionValues.get(i));
    }

    // Parses shorthand with one or less background-position keywords.
    private static boolean parseShortNumericValue(int i, List<String> positionValues,
                                                  Map<String, String> values, String value) {
        if (positionValues.size() > POSITION_VALUES_MAX_COUNT) {
            return false;
        }
        if (values.get(CommonCssConstants.BACKGROUND_POSITION_X) == null) {
            if (i != 0) {
                return false;
            }
            values.put(CommonCssConstants.BACKGROUND_POSITION_X, value);
            return true;
        }
        if (i == 0) {
            if (typeOfValue(positionValues.get(i + 1)) == BackgroundPositionType.CENTER) {
                values.put(CommonCssConstants.BACKGROUND_POSITION_X, value);
                values.put(CommonCssConstants.BACKGROUND_POSITION_Y, CommonCssConstants.CENTER);
                return true;
            }
            return false;
        }
        values.put(CommonCssConstants.BACKGROUND_POSITION_Y, value);
        return true;
    }

    // Parses shorthand with two background-position keywords.
    private static boolean parseLargeNumericValue(String prevValue, Map<String, String> values, String value) {
        if (typeOfValue(prevValue) == BackgroundPositionType.HORIZONTAL_POSITION) {
            values.put(CommonCssConstants.BACKGROUND_POSITION_X,
                    values.get(CommonCssConstants.BACKGROUND_POSITION_X) + " " + value);
            return true;
        }
        if (typeOfValue(prevValue) == BackgroundPositionType.VERTICAL_POSITION) {
            values.put(CommonCssConstants.BACKGROUND_POSITION_Y,
                    values.get(CommonCssConstants.BACKGROUND_POSITION_Y) + " " + value);
            return true;
        }
        return false;
    }

    private static boolean parseNonNumericValue(String positionValue, Map<String, String> values) {
        switch (typeOfValue(positionValue)) {
            case HORIZONTAL_POSITION:
                return parseHorizontal(positionValue, values);
            case VERTICAL_POSITION:
                return parseVertical(positionValue, values);
            case CENTER:
                return parseCenter(positionValue, values);
            default:
                return true;
        }
    }

    private static boolean parseHorizontal(String positionValue, Map<String, String> values) {
        if (values.get(CommonCssConstants.BACKGROUND_POSITION_X) == null) {
            values.put(CommonCssConstants.BACKGROUND_POSITION_X, positionValue);
            return true;
        }
        if (CommonCssConstants.CENTER.equals(values.get(CommonCssConstants.BACKGROUND_POSITION_X))
                && values.get(CommonCssConstants.BACKGROUND_POSITION_Y) == null) {
            values.put(CommonCssConstants.BACKGROUND_POSITION_X, positionValue);
            values.put(CommonCssConstants.BACKGROUND_POSITION_Y, CommonCssConstants.CENTER);
            return true;
        }
        return false;
    }

    private static boolean parseVertical(String positionValue, Map<String, String> values) {
        if (values.get(CommonCssConstants.BACKGROUND_POSITION_Y) == null) {
            values.put(CommonCssConstants.BACKGROUND_POSITION_Y, positionValue);
            return true;
        }
        return false;
    }

    private static boolean parseCenter(final String positionValue, Map<String, String> values) {
        if (values.get(CommonCssConstants.BACKGROUND_POSITION_X) == null) {
            values.put(CommonCssConstants.BACKGROUND_POSITION_X, positionValue);
            return true;
        }
        if (values.get(CommonCssConstants.BACKGROUND_POSITION_Y) == null) {
            values.put(CommonCssConstants.BACKGROUND_POSITION_Y, positionValue);
            return true;
        }
        return false;
    }

    private static BackgroundPositionType typeOfValue(final String value) {
        if (CommonCssConstants.LEFT.equals(value) || CommonCssConstants.RIGHT.equals(value)) {
            return BackgroundPositionType.HORIZONTAL_POSITION;
        }
        if (CommonCssConstants.TOP.equals(value) || CommonCssConstants.BOTTOM.equals(value)) {
            return BackgroundPositionType.VERTICAL_POSITION;
        }
        if (CommonCssConstants.CENTER.equals(value)) {
            return BackgroundPositionType.CENTER;
        }
        return BackgroundPositionType.NUMERIC;
    }

    private static enum BackgroundPositionType {
        NUMERIC,
        HORIZONTAL_POSITION,
        VERTICAL_POSITION,
        CENTER
    }
}
