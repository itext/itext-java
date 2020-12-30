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

public class FlexShorthandResolver implements IShorthandResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlexShorthandResolver.class);

    private static final String DEFAULT_FLEX_GROW = "0";
    private static final String DEFAULT_FLEX_SHRINK = "1";
    private static final String DEFAULT_FLEX_BASIS = CommonCssConstants.AUTO;

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        shorthandExpression = shorthandExpression.trim();
        if (CssTypesValidationUtils.isInitialOrInheritOrUnset(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(CommonCssConstants.FLEX_GROW, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.FLEX_SHRINK, shorthandExpression),
                    new CssDeclaration(CommonCssConstants.FLEX_BASIS, shorthandExpression)
            );
        }
        if (CommonCssConstants.AUTO.equals(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(CommonCssConstants.FLEX_GROW, "1"),
                    new CssDeclaration(CommonCssConstants.FLEX_SHRINK, "1"),
                    new CssDeclaration(CommonCssConstants.FLEX_BASIS, CommonCssConstants.AUTO)
            );
        }
        if (CommonCssConstants.NONE.equals(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(CommonCssConstants.FLEX_GROW, "0"),
                    new CssDeclaration(CommonCssConstants.FLEX_SHRINK, "0"),
                    new CssDeclaration(CommonCssConstants.FLEX_BASIS, CommonCssConstants.AUTO)
            );
        }
        if (CssTypesValidationUtils.containsInitialOrInheritOrUnset(shorthandExpression)) {
            LOGGER.error(MessageFormatUtil.format(
                    LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.FLEX, shorthandExpression));
            return Collections.emptyList();
        }
        if (shorthandExpression.isEmpty()) {
            LOGGER.error(MessageFormatUtil.format(
                    LogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, CommonCssConstants.FLEX));
            return Collections.emptyList();
        }

        final String[] flexProps = shorthandExpression.split(" ");

        final List<CssDeclaration> resolvedProperties;
        switch (flexProps.length) {
            case 1:
                resolvedProperties = resolveShorthandWithOneValue(flexProps[0]);
                break;
            case 2:
                resolvedProperties = resolveShorthandWithTwoValues(flexProps[0], flexProps[1]);
                break;
            case 3:
                resolvedProperties = resolveShorthandWithThreeValues(flexProps[0],
                        flexProps[1], flexProps[2]);
                break;
            default:
                LOGGER.error(MessageFormatUtil.format(
                        LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.FLEX, shorthandExpression));
                return Collections.emptyList();
        }

        if (!resolvedProperties.isEmpty()) {
            fillUnresolvedPropertiesWithDefaultValues(resolvedProperties);
        }
        return resolvedProperties;
    }

    private List<CssDeclaration> resolveShorthandWithOneValue(String firstProperty) {
        final List<CssDeclaration> resolvedProperties = new ArrayList<>();

        CssDeclaration flexGrowDeclaration = new CssDeclaration(CommonCssConstants.FLEX_GROW, firstProperty);
        if (CssDeclarationValidationMaster.checkDeclaration(flexGrowDeclaration)) {
            resolvedProperties.add(flexGrowDeclaration);
            return resolvedProperties;
        }

        CssDeclaration flexBasisDeclaration = new CssDeclaration(CommonCssConstants.FLEX_BASIS, firstProperty);
        if (CssDeclarationValidationMaster.checkDeclaration(flexBasisDeclaration)) {
            resolvedProperties.add(flexBasisDeclaration);
            return resolvedProperties;
        }

        LOGGER.error(MessageFormatUtil.format(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, firstProperty));
        return Collections.emptyList();
    }

    private List<CssDeclaration> resolveShorthandWithTwoValues(String firstProperty, String secondProperty) {
        final List<CssDeclaration> resolvedProperties = new ArrayList<>();

        final CssDeclaration flexGrowDeclaration = new CssDeclaration(CommonCssConstants.FLEX_GROW, firstProperty);
        if (CssDeclarationValidationMaster.checkDeclaration(flexGrowDeclaration)) {
            resolvedProperties.add(flexGrowDeclaration);
            final CssDeclaration flexShrinkDeclaration = new CssDeclaration(CommonCssConstants.FLEX_SHRINK, secondProperty);
            if (CssDeclarationValidationMaster.checkDeclaration(flexShrinkDeclaration)) {
                resolvedProperties.add(flexShrinkDeclaration);
                return resolvedProperties;
            }

            final CssDeclaration flexBasisDeclaration = new CssDeclaration(CommonCssConstants.FLEX_BASIS, secondProperty);
            if (CssDeclarationValidationMaster.checkDeclaration(flexBasisDeclaration)) {
                resolvedProperties.add(flexBasisDeclaration);
                return resolvedProperties;
            }
            LOGGER.error(MessageFormatUtil.format(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, secondProperty));
            return Collections.emptyList();
        }

        final CssDeclaration flexBasisDeclaration = new CssDeclaration(CommonCssConstants.FLEX_BASIS, firstProperty);
        if (CssDeclarationValidationMaster.checkDeclaration(flexBasisDeclaration)) {
            resolvedProperties.add(flexBasisDeclaration);
            flexGrowDeclaration.setExpression(secondProperty);
            if (CssDeclarationValidationMaster.checkDeclaration(flexGrowDeclaration)) {
                resolvedProperties.add(flexGrowDeclaration);
                return resolvedProperties;
            }
            LOGGER.error(MessageFormatUtil.format(
                    LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.FLEX_GROW, secondProperty));
            return Collections.emptyList();
        }

        LOGGER.error(MessageFormatUtil.format(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, firstProperty));
        return Collections.emptyList();
    }

    private List<CssDeclaration> resolveShorthandWithThreeValues(String firstProperty,
                                                                 String secondProperty, String thirdProperty) {
        final List<CssDeclaration> resolvedProperties = new ArrayList<>();

        final CssDeclaration flexGrowDeclaration = new CssDeclaration(CommonCssConstants.FLEX_GROW, firstProperty);
        if (CssDeclarationValidationMaster.checkDeclaration(flexGrowDeclaration)) {
            resolvedProperties.add(flexGrowDeclaration);
            final CssDeclaration flexShrinkDeclaration = new CssDeclaration(CommonCssConstants.FLEX_SHRINK, secondProperty);
            if (!CssDeclarationValidationMaster.checkDeclaration(flexShrinkDeclaration)) {
                LOGGER.error(MessageFormatUtil.format(
                        LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.FLEX_SHRINK, secondProperty));
                return Collections.emptyList();
            }
            resolvedProperties.add(flexShrinkDeclaration);

            final CssDeclaration flexBasisDeclaration = new CssDeclaration(CommonCssConstants.FLEX_BASIS, thirdProperty);
            if (!CssDeclarationValidationMaster.checkDeclaration(flexBasisDeclaration)) {
                LOGGER.error(MessageFormatUtil.format(
                        LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.FLEX_BASIS, thirdProperty));
                return Collections.emptyList();
            }
            resolvedProperties.add(flexBasisDeclaration);
            return resolvedProperties;
        }

        final CssDeclaration flexBasisDeclaration = new CssDeclaration(CommonCssConstants.FLEX_BASIS, firstProperty);
        if (CssDeclarationValidationMaster.checkDeclaration(flexBasisDeclaration)) {
            resolvedProperties.add(flexBasisDeclaration);
            flexGrowDeclaration.setExpression(secondProperty);
            if (!CssDeclarationValidationMaster.checkDeclaration(flexGrowDeclaration)) {
                LOGGER.error(MessageFormatUtil.format(
                        LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.FLEX_GROW, secondProperty));
                return Collections.emptyList();
            }
            resolvedProperties.add(flexGrowDeclaration);

            final CssDeclaration flexShrinkDeclaration = new CssDeclaration(CommonCssConstants.FLEX_SHRINK, thirdProperty);
            if (!CssDeclarationValidationMaster.checkDeclaration(flexShrinkDeclaration)) {
                LOGGER.error(MessageFormatUtil.format(
                        LogMessageConstant.UNKNOWN_PROPERTY, CommonCssConstants.FLEX_SHRINK, thirdProperty));
                return Collections.emptyList();
            }
            resolvedProperties.add(flexShrinkDeclaration);
            return resolvedProperties;
        }

        LOGGER.error(MessageFormatUtil.format(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, firstProperty));
        return Collections.emptyList();
    }

    private void fillUnresolvedPropertiesWithDefaultValues(List<CssDeclaration> resolvedProperties) {
        if (!resolvedProperties.stream().anyMatch(property -> property.getProperty().equals(CommonCssConstants.FLEX_GROW))) {
            resolvedProperties.add(new CssDeclaration(CommonCssConstants.FLEX_GROW, DEFAULT_FLEX_GROW));
        }
        if (!resolvedProperties.stream().anyMatch(property -> property.getProperty().equals(CommonCssConstants.FLEX_SHRINK))) {
            resolvedProperties.add(new CssDeclaration(CommonCssConstants.FLEX_SHRINK, DEFAULT_FLEX_SHRINK));
        }
        if (!resolvedProperties.stream().anyMatch(property -> property.getProperty().equals(CommonCssConstants.FLEX_BASIS))) {
            resolvedProperties.add(new CssDeclaration(CommonCssConstants.FLEX_BASIS, DEFAULT_FLEX_BASIS));
        }
    }
}
