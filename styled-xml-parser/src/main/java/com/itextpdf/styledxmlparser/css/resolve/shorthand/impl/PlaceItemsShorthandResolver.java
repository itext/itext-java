package com.itextpdf.styledxmlparser.css.resolve.shorthand.impl;

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.validate.CssDeclarationValidationMaster;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

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
            LOGGER.error(MessageFormatUtil.format(
                    LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.PLACE_ITEMS, shorthandExpression));
            return Collections.emptyList();
        }
        if (shorthandExpression.isEmpty()) {
            LOGGER.error(MessageFormatUtil.format(
                    LogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, CommonCssConstants.PLACE_ITEMS));
            return Collections.emptyList();
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
                LOGGER.error(MessageFormatUtil.format(
                        LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.PLACE_ITEMS, shorthandExpression));
                return Collections.emptyList();
        }
    }

    private List<CssDeclaration> resolveShorthandWithOneWord(String firstWord) {
        List<CssDeclaration> resolvedShorthand = resolveAlignItemsAndJustifyItems(firstWord, firstWord);
        if (resolvedShorthand == null) {
            LOGGER.error(MessageFormatUtil.format(
                    LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.ALIGN_ITEMS, firstWord));
            return Collections.emptyList();
        }

        return resolvedShorthand;
    }

    private List<CssDeclaration> resolveShorthandWithTwoWords(String firstWord, String secondWord) {
        List<CssDeclaration> resolvedShorthand = resolveAlignItemsAndJustifyItems(firstWord, secondWord);
        if (resolvedShorthand != null) {
            return resolvedShorthand;
        }

        resolvedShorthand =
                resolveAlignItemsAndJustifyItems(firstWord + " " + secondWord, firstWord + " " + secondWord);
        if (resolvedShorthand == null) {
            LOGGER.error(MessageFormatUtil.format(LogMessageConstant.UNKNOWN_PROPERTY,
                    CommonCssConstants.ALIGN_ITEMS, firstWord + " " + secondWord));
            return Collections.emptyList();
        }

        return resolvedShorthand;
    }

    private List<CssDeclaration> resolveShorthandWithThreeWords(String firstWord, String secondWord, String thirdWord) {
        List<CssDeclaration> resolvedShorthand = resolveAlignItemsAndJustifyItems(firstWord, secondWord + " " + thirdWord);
        if (resolvedShorthand != null) {
            return resolvedShorthand;
        }

        resolvedShorthand = resolveAlignItemsAndJustifyItems(firstWord + " " + secondWord, thirdWord);
        if (resolvedShorthand == null) {
            LOGGER.error(MessageFormatUtil.format(LogMessageConstant.UNKNOWN_PROPERTY,
                    CommonCssConstants.ALIGN_ITEMS, firstWord + " " + secondWord));
            return Collections.emptyList();
        }

        return resolvedShorthand;
    }

    private List<CssDeclaration> resolveShorthandWithFourWords(String firstWord,
                                                               String secondWord, String thirdWord, String fourthWord) {
        List<CssDeclaration> resolvedShorthand =
                resolveAlignItemsAndJustifyItems(firstWord + " " + secondWord, thirdWord + " " + fourthWord);
        if (resolvedShorthand == null) {
            LOGGER.error(MessageFormatUtil.format(LogMessageConstant.UNKNOWN_PROPERTY,
                    CommonCssConstants.ALIGN_ITEMS, firstWord + " " + secondWord));
            return Collections.emptyList();
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

            LOGGER.error(MessageFormatUtil.format(LogMessageConstant.UNKNOWN_PROPERTY,
                    CommonCssConstants.JUSTIFY_ITEMS, justifyItemsDeclaration.getExpression()));
            return Collections.emptyList();
        }
        return null;
    }
}
