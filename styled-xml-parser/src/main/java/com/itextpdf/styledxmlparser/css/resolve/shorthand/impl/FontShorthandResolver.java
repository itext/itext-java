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
package com.itextpdf.styledxmlparser.css.resolve.shorthand.impl;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    private static final Set<String> UNSUPPORTED_VALUES_OF_FONT_SHORTHAND = new HashSet<>(Arrays.asList(
            CommonCssConstants.CAPTION, CommonCssConstants.ICON, CommonCssConstants.MENU, CommonCssConstants.MESSAGE_BOX,
            CommonCssConstants.SMALL_CAPTION, CommonCssConstants.STATUS_BAR
    ));

    /** Font weight values. */
    private static final Set<String> FONT_WEIGHT_NOT_DEFAULT_VALUES = new HashSet<>(Arrays.asList(
            CommonCssConstants.BOLD, CommonCssConstants.BOLDER, CommonCssConstants.LIGHTER,
            "100", "200", "300", "400", "500", "600", "700", "800", "900"
    ));

    /** Font size values. */
    private static final Set<String> FONT_SIZE_VALUES = new HashSet<>(Arrays.asList(
            CommonCssConstants.MEDIUM, CommonCssConstants.XX_SMALL, CommonCssConstants.X_SMALL, CommonCssConstants.SMALL, CommonCssConstants.LARGE,
            CommonCssConstants.X_LARGE, CommonCssConstants.XX_LARGE, CommonCssConstants.SMALLER, CommonCssConstants.LARGER
    ));

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

        List<String> properties = getFontProperties(shorthandExpression.replaceAll("\\s*,\\s*", ","));
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
            } else if (FONT_SIZE_VALUES.contains(value) || CssUtils.isMetricValue(value)
                    || CssUtils.isNumericValue(value) || CssUtils.isRelativeValue(value)) {
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
