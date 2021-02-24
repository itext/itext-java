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
            return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, CommonCssConstants.FLEX,
                    shorthandExpression);
        }
        if (shorthandExpression.isEmpty()) {
            return handleExpressionError(LogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY, CommonCssConstants.FLEX,
                    shorthandExpression);
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
                return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
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
                return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
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
                    return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
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
                return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.FLEX_GROW, secondProperty);
            } else {
                return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
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
                return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.FLEX_SHRINK, secondProperty);
            } else {
                resolvedProperties.add(flexShrinkDeclaration);
                final CssDeclaration flexBasisDeclaration = new CssDeclaration(CommonCssConstants.FLEX_BASIS,
                        thirdProperty);
                if (!CssDeclarationValidationMaster.checkDeclaration(flexBasisDeclaration)) {
                    return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
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
                    return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                            CommonCssConstants.FLEX_SHRINK, thirdProperty);
                } else {
                    resolvedProperties.add(flexShrinkDeclaration);
                    final CssDeclaration flexBasisDeclaration = new CssDeclaration(CommonCssConstants.FLEX_BASIS,
                            firstProperty);
                    if (!CssDeclarationValidationMaster.checkDeclaration(flexBasisDeclaration)) {
                        return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                                CommonCssConstants.FLEX_BASIS, firstProperty);
                    } else {
                        resolvedProperties.add(flexBasisDeclaration);
                        return resolvedProperties;
                    }
                }
            } else {
                return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
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
