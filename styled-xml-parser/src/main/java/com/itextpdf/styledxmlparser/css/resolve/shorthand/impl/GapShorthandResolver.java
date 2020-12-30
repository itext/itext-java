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

public class GapShorthandResolver implements IShorthandResolver {

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
            LOGGER.error(MessageFormatUtil.format(
                    LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.GAP, shorthandExpression));
            return Collections.emptyList();
        }
        if (shorthandExpression.isEmpty()) {
            LOGGER.error(MessageFormatUtil.format(
                    LogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, CommonCssConstants.GAP));
            return Collections.emptyList();
        }

        final String[] gapProps = shorthandExpression.split(" ");

        if (gapProps.length == 1) {
            return resolveGapWithOneProperty(gapProps[0]);
        }
        if (gapProps.length == 2) {
            return resolveGapWithTwoProperties(gapProps[0], gapProps[1]);
        }

        LOGGER.error(MessageFormatUtil.format(
                LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.GAP, shorthandExpression));
        return Collections.emptyList();
    }

    private List<CssDeclaration> resolveGapWithOneProperty(String rowAndColumn) {
        CssDeclaration rowGapDeclaration = new CssDeclaration(CommonCssConstants.ROW_GAP, rowAndColumn);
        if (CssDeclarationValidationMaster.checkDeclaration(rowGapDeclaration)) {
            CssDeclaration columnGapDeclaration = new CssDeclaration(CommonCssConstants.COLUMN_GAP, rowAndColumn);
            return Arrays.asList(rowGapDeclaration, columnGapDeclaration);
        }
        LOGGER.error(MessageFormatUtil.format(
                LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.ROW_GAP, rowAndColumn));
        return Collections.emptyList();
    }

    private List<CssDeclaration> resolveGapWithTwoProperties(String row, String column) {
        CssDeclaration rowGapDeclaration = new CssDeclaration(CommonCssConstants.ROW_GAP, row);
        if (!CssDeclarationValidationMaster.checkDeclaration(rowGapDeclaration)) {
            LOGGER.error(MessageFormatUtil.format(
                    LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.ROW_GAP, row));
            return Collections.emptyList();
        }
        CssDeclaration columnGapDeclaration = new CssDeclaration(CommonCssConstants.COLUMN_GAP, column);
        if (!CssDeclarationValidationMaster.checkDeclaration(columnGapDeclaration)) {
            LOGGER.error(MessageFormatUtil.format(
                    LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.COLUMN_GAP, column));
            return Collections.emptyList();
        }
        return Arrays.asList(rowGapDeclaration, columnGapDeclaration);
    }
}
