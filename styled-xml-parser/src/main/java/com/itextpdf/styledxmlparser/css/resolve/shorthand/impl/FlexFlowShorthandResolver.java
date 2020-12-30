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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class FlexFlowShorthandResolver implements IShorthandResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlexFlowShorthandResolver.class);

    private static final String DEFAULT_FLEX_DIRECTION = CommonCssConstants.ROW;
    private static final String DEFAULT_FLEX_WRAP = CommonCssConstants.NOWRAP;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        shorthandExpression = shorthandExpression.trim();
        if (CssTypesValidationUtils.isInitialOrInheritOrUnset(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(CommonCssConstants.FLEX_DIRECTION, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.FLEX_WRAP, shorthandExpression)
            );
        }
        if (CssTypesValidationUtils.containsInitialOrInheritOrUnset(shorthandExpression)) {
            LOGGER.error(MessageFormatUtil.format(
                    LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.FLEX_FLOW, shorthandExpression));
            return Collections.emptyList();
        }
        if (shorthandExpression.isEmpty()) {
            LOGGER.error(MessageFormatUtil.format(
                    LogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, CommonCssConstants.FLEX_FLOW));
            return Collections.emptyList();
        }

        final String[] flexFlowProps = shorthandExpression.split(" ");
        boolean isDirectionResolved = false;
        boolean isWrapResolved = false;
        final List<CssDeclaration> resolvedProperties = new ArrayList<>();

        for (String flexFlowProp : flexFlowProps) {
            final CssDeclaration flexDirectionDeclaration = new CssDeclaration(CommonCssConstants.FLEX_DIRECTION, flexFlowProp);
            final CssDeclaration flexWrapDeclaration = new CssDeclaration(CommonCssConstants.FLEX_WRAP, flexFlowProp);

            if (CssDeclarationValidationMaster.checkDeclaration(flexDirectionDeclaration)) {
                if (isDirectionResolved) {
                    LOGGER.error(MessageFormatUtil.format(
                            LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.FLEX_FLOW, shorthandExpression));
                    return Collections.emptyList();
                }
                isDirectionResolved = true;
                resolvedProperties.add(flexDirectionDeclaration);
            } else if (CssDeclarationValidationMaster.checkDeclaration(flexWrapDeclaration)) {
                if (isWrapResolved) {
                    LOGGER.error(MessageFormatUtil.format(
                            LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.FLEX_FLOW, shorthandExpression));
                    return Collections.emptyList();
                }
                isWrapResolved = true;
                resolvedProperties.add(flexWrapDeclaration);
            } else {
                LOGGER.error(MessageFormatUtil.format(
                        LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.FLEX_FLOW, shorthandExpression));
                return Collections.emptyList();
            }
        }

        fillUnresolvedPropertiesWithDefaultValues(resolvedProperties);
        return resolvedProperties;
    }

    private void fillUnresolvedPropertiesWithDefaultValues(List<CssDeclaration> resolvedProperties) {
        if (!resolvedProperties.stream().anyMatch(property -> property.getProperty().equals(CommonCssConstants.FLEX_DIRECTION))) {
            resolvedProperties.add(new CssDeclaration(CommonCssConstants.FLEX_DIRECTION, DEFAULT_FLEX_DIRECTION));
        }
        if (!resolvedProperties.stream().anyMatch(property -> property.getProperty().equals(CommonCssConstants.FLEX_WRAP))) {
            resolvedProperties.add(new CssDeclaration(CommonCssConstants.FLEX_WRAP, DEFAULT_FLEX_WRAP));
        }
    }
}
