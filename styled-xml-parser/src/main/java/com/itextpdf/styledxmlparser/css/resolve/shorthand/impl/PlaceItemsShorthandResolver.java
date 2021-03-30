/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.validate.CssDeclarationValidationMaster;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PlaceItemsShorthandResolver implements IShorthandResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(PlaceItemsShorthandResolver.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        shorthandExpression = shorthandExpression.trim();
        if (CssTypesValidationUtils.isInitialOrInheritOrUnset(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(CommonCssConstants.ALIGN_ITEMS, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.JUSTIFY_ITEMS, shorthandExpression)
            );
        }
        if (CssTypesValidationUtils.containsInitialOrInheritOrUnset(shorthandExpression)) {
            return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    CommonCssConstants.PLACE_ITEMS,
                    shorthandExpression);
        }
        if (shorthandExpression.isEmpty()) {
            return handleExpressionError(LogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY,
                    CommonCssConstants.PLACE_ITEMS, shorthandExpression);
        }

        final String[] placeItemsProps = shorthandExpression.split(" ");
        switch (placeItemsProps.length) {
            case 1:
                return resolveShorthandWithOneWord(placeItemsProps[0]);
            case 2:
                return resolveShorthandWithTwoWords(placeItemsProps[0], placeItemsProps[1]);
            case 3:
                return resolveShorthandWithThreeWords(placeItemsProps[0], placeItemsProps[1], placeItemsProps[2]);
            case 4:
                return resolveShorthandWithFourWords(placeItemsProps[0],
                        placeItemsProps[1], placeItemsProps[2], placeItemsProps[3]);
            default:
                return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.PLACE_ITEMS,
                        shorthandExpression);
        }
    }

    private List<CssDeclaration> resolveShorthandWithOneWord(String firstWord) {
        List<CssDeclaration> resolvedShorthand = resolveAlignItemsAndJustifyItems(firstWord, firstWord);
        if (resolvedShorthand.isEmpty()) {
            return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    CommonCssConstants.PLACE_ITEMS,
                    firstWord);
        }
        return resolvedShorthand;
    }

    private List<CssDeclaration> resolveShorthandWithTwoWords(String firstWord, String secondWord) {
        List<CssDeclaration> resolvedShorthand = resolveAlignItemsAndJustifyItems(firstWord, secondWord);
        if (resolvedShorthand.isEmpty()) {
            resolvedShorthand =
                    resolveAlignItemsAndJustifyItems(firstWord + " " + secondWord, firstWord + " " + secondWord);
            if (resolvedShorthand.isEmpty()) {
                return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.PLACE_ITEMS,
                        firstWord + " " + secondWord);
            }
        }
        return resolvedShorthand;
    }

    private List<CssDeclaration> resolveShorthandWithThreeWords(String firstWord, String secondWord, String thirdWord) {
        List<CssDeclaration> resolvedShorthand = resolveAlignItemsAndJustifyItems(firstWord,
                secondWord + " " + thirdWord);
        if (resolvedShorthand.isEmpty()) {
            resolvedShorthand = resolveAlignItemsAndJustifyItems(firstWord + " " + secondWord, thirdWord);
            if (resolvedShorthand.isEmpty()) {
                return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.PLACE_ITEMS,
                        firstWord + " " + secondWord + " " + thirdWord);
            }
        }
        return resolvedShorthand;
    }

    private List<CssDeclaration> resolveShorthandWithFourWords(String firstWord,
            String secondWord, String thirdWord, String fourthWord) {
        List<CssDeclaration> resolvedShorthand =
                resolveAlignItemsAndJustifyItems(firstWord + " " + secondWord, thirdWord + " " + fourthWord);
        if (resolvedShorthand.isEmpty()) {
            return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    CommonCssConstants.PLACE_ITEMS,
                    firstWord + " " + secondWord + " " + thirdWord + " " + fourthWord);
        }
        return resolvedShorthand;
    }

    private List<CssDeclaration> resolveAlignItemsAndJustifyItems(String alignItems, String justifyItems) {
        CssDeclaration alignItemsDeclaration = new CssDeclaration(CommonCssConstants.ALIGN_ITEMS, alignItems);

        if (CssDeclarationValidationMaster.checkDeclaration(alignItemsDeclaration)) {
            CssDeclaration justifyItemsDeclaration = new CssDeclaration(CommonCssConstants.JUSTIFY_ITEMS, justifyItems);

            if (CssDeclarationValidationMaster.checkDeclaration(justifyItemsDeclaration)) {
                return Arrays.asList(alignItemsDeclaration, justifyItemsDeclaration);
            }

            return Collections.<CssDeclaration>emptyList();
        } else {
            return Collections.<CssDeclaration>emptyList();
        }
    }

    private static List<CssDeclaration> handleExpressionError(String logMessage, String attribute,
            String shorthandExpression) {
        LOGGER.warn(MessageFormatUtil.format(logMessage, attribute, shorthandExpression));
        return Collections.<CssDeclaration>emptyList();
    }
}
