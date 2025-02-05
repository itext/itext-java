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
            return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    CommonCssConstants.PLACE_ITEMS,
                    shorthandExpression);
        }
        if (shorthandExpression.isEmpty()) {
            return handleExpressionError(StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY,
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
                return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.PLACE_ITEMS,
                        shorthandExpression);
        }
    }

    private List<CssDeclaration> resolveShorthandWithOneWord(String firstWord) {
        List<CssDeclaration> resolvedShorthand = resolveAlignItemsAndJustifyItems(firstWord, firstWord);
        if (resolvedShorthand.isEmpty()) {
            return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
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
                return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
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
                return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
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
            return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
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
