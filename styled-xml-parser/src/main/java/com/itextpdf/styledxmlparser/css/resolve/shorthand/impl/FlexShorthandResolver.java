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
import com.itextpdf.styledxmlparser.css.resolve.CssDefaults;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.css.validate.CssDeclarationValidationMaster;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FlexShorthandResolver implements IShorthandResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlexShorthandResolver.class);

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
            return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    CommonCssConstants.FLEX, shorthandExpression);
        }
        if (shorthandExpression.isEmpty()) {
            return handleExpressionError(StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY,
                    CommonCssConstants.FLEX, shorthandExpression);
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
                return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.FLEX, shorthandExpression);
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
        } else {
            CssDeclaration flexBasisDeclaration = new CssDeclaration(CommonCssConstants.FLEX_BASIS, firstProperty);
            if (CssDeclarationValidationMaster.checkDeclaration(flexBasisDeclaration)) {
                resolvedProperties.add(flexBasisDeclaration);
                return resolvedProperties;
            } else {
                return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.FLEX_GROW, firstProperty);
            }
        }
    }

    private List<CssDeclaration> resolveShorthandWithTwoValues(String firstProperty, String secondProperty) {
        final List<CssDeclaration> resolvedProperties = new ArrayList<>();

        final CssDeclaration flexGrowDeclaration = new CssDeclaration(CommonCssConstants.FLEX_GROW, firstProperty);
        if (CssDeclarationValidationMaster.checkDeclaration(flexGrowDeclaration)) {
            resolvedProperties.add(flexGrowDeclaration);
            final CssDeclaration flexShrinkDeclaration = new CssDeclaration(CommonCssConstants.FLEX_SHRINK,
                    secondProperty);
            if (CssDeclarationValidationMaster.checkDeclaration(flexShrinkDeclaration)) {
                resolvedProperties.add(flexShrinkDeclaration);
                return resolvedProperties;
            } else {
                final CssDeclaration flexBasisDeclaration = new CssDeclaration(CommonCssConstants.FLEX_BASIS,
                        secondProperty);
                if (CssDeclarationValidationMaster.checkDeclaration(flexBasisDeclaration)) {
                    resolvedProperties.add(flexBasisDeclaration);
                    return resolvedProperties;
                } else {
                    return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                            CommonCssConstants.FLEX_BASIS, secondProperty);
                }
            }
        } else {
            final CssDeclaration flexBasisDeclaration = new CssDeclaration(CommonCssConstants.FLEX_BASIS,
                    firstProperty);
            if (CssDeclarationValidationMaster.checkDeclaration(flexBasisDeclaration)) {
                resolvedProperties.add(flexBasisDeclaration);
                flexGrowDeclaration.setExpression(secondProperty);
                if (CssDeclarationValidationMaster.checkDeclaration(flexGrowDeclaration)) {
                    resolvedProperties.add(flexGrowDeclaration);
                    return resolvedProperties;
                }
                return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.FLEX_GROW, secondProperty);
            } else {
                return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.FLEX_SHRINK, secondProperty);
            }
        }
    }

    private List<CssDeclaration> resolveShorthandWithThreeValues(String firstProperty,
            String secondProperty, String thirdProperty) {
        final List<CssDeclaration> resolvedProperties = new ArrayList<>();

        CssDeclaration flexGrowDeclaration = new CssDeclaration(CommonCssConstants.FLEX_GROW, firstProperty);
        if (CssDeclarationValidationMaster.checkDeclaration(flexGrowDeclaration)) {
            resolvedProperties.add(flexGrowDeclaration);
            final CssDeclaration flexShrinkDeclaration = new CssDeclaration(CommonCssConstants.FLEX_SHRINK,
                    secondProperty);
            if (!CssDeclarationValidationMaster.checkDeclaration(flexShrinkDeclaration)) {
                return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.FLEX_SHRINK, secondProperty);
            } else {
                resolvedProperties.add(flexShrinkDeclaration);
                final CssDeclaration flexBasisDeclaration = new CssDeclaration(CommonCssConstants.FLEX_BASIS,
                        thirdProperty);
                if (!CssDeclarationValidationMaster.checkDeclaration(flexBasisDeclaration)) {
                    return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                            CommonCssConstants.FLEX_BASIS, thirdProperty);
                } else {
                    resolvedProperties.add(flexBasisDeclaration);
                    return resolvedProperties;
                }
            }
        } else {
            // For some reason browsers support flex-basis, flex-grow, flex-shrink order as well
            flexGrowDeclaration = new CssDeclaration(CommonCssConstants.FLEX_GROW, secondProperty);
            if (CssDeclarationValidationMaster.checkDeclaration(flexGrowDeclaration)) {
                resolvedProperties.add(flexGrowDeclaration);
                final CssDeclaration flexShrinkDeclaration = new CssDeclaration(CommonCssConstants.FLEX_SHRINK,
                        thirdProperty);
                if (!CssDeclarationValidationMaster.checkDeclaration(flexShrinkDeclaration)) {
                    return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                            CommonCssConstants.FLEX_SHRINK, thirdProperty);
                } else {
                    resolvedProperties.add(flexShrinkDeclaration);
                    final CssDeclaration flexBasisDeclaration = new CssDeclaration(CommonCssConstants.FLEX_BASIS,
                            firstProperty);
                    if (!CssDeclarationValidationMaster.checkDeclaration(flexBasisDeclaration)) {
                        return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                                CommonCssConstants.FLEX_BASIS, firstProperty);
                    } else {
                        resolvedProperties.add(flexBasisDeclaration);
                        return resolvedProperties;
                    }
                }
            } else {
                return handleExpressionError(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.FLEX_GROW, secondProperty);
            }
        }
    }

    private void fillUnresolvedPropertiesWithDefaultValues(List<CssDeclaration> resolvedProperties) {
        if (!resolvedProperties.stream()
                .anyMatch(property -> property.getProperty().equals(CommonCssConstants.FLEX_GROW))) {
            resolvedProperties.add(new CssDeclaration(CommonCssConstants.FLEX_GROW,
                    CssDefaults.getDefaultValue(CommonCssConstants.FLEX_GROW)));
        }
        if (!resolvedProperties.stream()
                .anyMatch(property -> property.getProperty().equals(CommonCssConstants.FLEX_SHRINK))) {
            resolvedProperties.add(new CssDeclaration(CommonCssConstants.FLEX_SHRINK,
                    CssDefaults.getDefaultValue(CommonCssConstants.FLEX_SHRINK)));
        }
        if (!resolvedProperties.stream()
                .anyMatch(property -> property.getProperty().equals(CommonCssConstants.FLEX_BASIS))) {
            // When flex-basis is omitted from the flex shorthand, its specified value is 0.
            resolvedProperties.add(new CssDeclaration(CommonCssConstants.FLEX_BASIS, "0"));
        }
    }

    private static List<CssDeclaration> handleExpressionError(String logMessage, String attribute,
            String shorthandExpression) {
        LOGGER.warn(MessageFormatUtil.format(logMessage, attribute, shorthandExpression));
        return Collections.<CssDeclaration>emptyList();
    }
}
