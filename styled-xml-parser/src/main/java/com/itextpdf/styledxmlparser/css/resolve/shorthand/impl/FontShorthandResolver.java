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

import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Collections;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


/**
 * {@link IShorthandResolver} implementation for fonts.
 */
public class FontShorthandResolver implements IShorthandResolver {

    /** Unsupported shorthand values. */
    private static final Set<String> UNSUPPORTED_VALUES_OF_FONT_SHORTHAND = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    CommonCssConstants.CAPTION,
                    CommonCssConstants.ICON,
                    CommonCssConstants.MENU,
                    CommonCssConstants.MESSAGE_BOX,
                    CommonCssConstants.SMALL_CAPTION,
                    CommonCssConstants.STATUS_BAR
    )));

    /** Font weight values. */
    private static final Set<String> FONT_WEIGHT_NOT_DEFAULT_VALUES = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    CommonCssConstants.BOLD,
                    CommonCssConstants.BOLDER,
                    CommonCssConstants.LIGHTER,
            "100", "200", "300", "400", "500", "600", "700", "800", "900"
    )));

    /** Font size values. */
    private static final Set<String> FONT_SIZE_VALUES = Collections
            .unmodifiableSet(new HashSet<>(Arrays.asList(
                    CommonCssConstants.MEDIUM,
                    CommonCssConstants.XX_SMALL,
                    CommonCssConstants.X_SMALL,
                    CommonCssConstants.SMALL,
                    CommonCssConstants.LARGE,
                    CommonCssConstants.X_LARGE,
                    CommonCssConstants.XX_LARGE,
                    CommonCssConstants.SMALLER,
                    CommonCssConstants.LARGER
    )));

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver#resolveShorthand(java.lang.String)
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        if (UNSUPPORTED_VALUES_OF_FONT_SHORTHAND.contains(shorthandExpression)) {
            Logger logger = LoggerFactory.getLogger(FontShorthandResolver.class);
            logger.error(MessageFormatUtil.format("The \"{0}\" value of CSS shorthand property \"font\" is not supported", shorthandExpression));
        }
        if (CommonCssConstants.INITIAL.equals(shorthandExpression) || CommonCssConstants.INHERIT.equals(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(CommonCssConstants.FONT_STYLE, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.FONT_VARIANT, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.FONT_WEIGHT, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.FONT_SIZE, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.LINE_HEIGHT, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.FONT_FAMILY, shorthandExpression)
            );
        }

        String fontStyleValue = null;
        String fontVariantValue = null;
        String fontWeightValue = null;
        String fontSizeValue = null;
        String lineHeightValue = null;
        String fontFamilyValue = null;

        final String[] props = shorthandExpression.split(",");
        final String shExprFixed = String.join(",",
                Arrays.stream(props).map(str -> str.trim()).collect(Collectors.toList()));
        List<String> properties = getFontProperties(shExprFixed);
        for (String value : properties) {
            int slashSymbolIndex = value.indexOf('/');
            if (CommonCssConstants.ITALIC.equals(value) || CommonCssConstants.OBLIQUE.equals(value)) {
                fontStyleValue = value;
            } else if (CommonCssConstants.SMALL_CAPS.equals(value)) {
                fontVariantValue = value;
            } else if (FONT_WEIGHT_NOT_DEFAULT_VALUES.contains(value)) {
                fontWeightValue = value;
            } else if (slashSymbolIndex > 0) {
                fontSizeValue = value.substring(0, slashSymbolIndex);
                lineHeightValue = value.substring(slashSymbolIndex + 1, value.length());
            } else if (FONT_SIZE_VALUES.contains(value) || CssTypesValidationUtils.isMetricValue(value)
                    || CssTypesValidationUtils.isNumber(value) || CssTypesValidationUtils.isRelativeValue(value)) {
                fontSizeValue = value;
            } else {
                fontFamilyValue = value;
            }
        }

        List<CssDeclaration> cssDeclarations = Arrays.asList(
                new CssDeclaration(CommonCssConstants.FONT_STYLE, fontStyleValue == null ? CommonCssConstants.INITIAL : fontStyleValue),
                new CssDeclaration(CommonCssConstants.FONT_VARIANT, fontVariantValue == null ? CommonCssConstants.INITIAL : fontVariantValue),
                new CssDeclaration(CommonCssConstants.FONT_WEIGHT, fontWeightValue == null ? CommonCssConstants.INITIAL : fontWeightValue),
                new CssDeclaration(CommonCssConstants.FONT_SIZE, fontSizeValue == null ? CommonCssConstants.INITIAL : fontSizeValue),
                new CssDeclaration(CommonCssConstants.LINE_HEIGHT, lineHeightValue == null ? CommonCssConstants.INITIAL : lineHeightValue),
                new CssDeclaration(CommonCssConstants.FONT_FAMILY, fontFamilyValue == null ? CommonCssConstants.INITIAL : fontFamilyValue)
        );

        return cssDeclarations;
    }

    /**
     * Gets the font properties.
     *
     * @param shorthandExpression the shorthand expression
     * @return the font properties
     */
    private List<String> getFontProperties(String shorthandExpression) {
        boolean doubleQuotesAreSpotted = false;
        boolean singleQuoteIsSpotted = false;
        List<String> properties = new ArrayList<>();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < shorthandExpression.length(); i++) {
            char currentChar = shorthandExpression.charAt(i);
            if (currentChar == '\"') {
                doubleQuotesAreSpotted = !doubleQuotesAreSpotted;
                sb.append(currentChar);
            } else if (currentChar == '\'' ) {
                singleQuoteIsSpotted = !singleQuoteIsSpotted;
                sb.append(currentChar);
            } else if (!doubleQuotesAreSpotted && !singleQuoteIsSpotted && Character.isWhitespace(currentChar)) {
                if (sb.length() > 0) {
                    properties.add(sb.toString());
                    sb = new StringBuilder();
                }
            } else {
                sb.append(currentChar);
            }
        }
        if (sb.length() > 0) {
            properties.add(sb.toString());
        }
        return properties;
    }
}
