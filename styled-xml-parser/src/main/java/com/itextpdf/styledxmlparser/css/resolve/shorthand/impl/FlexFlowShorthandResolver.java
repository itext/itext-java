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

public class FlexFlowShorthandResolver implements IShorthandResolver {

    private static final Logger LOGGER = LoggerFactory.getLogger(FlexFlowShorthandResolver.class);

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
            return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    CommonCssConstants.FLEX_FLOW,
                    shorthandExpression);
        }
        if (shorthandExpression.isEmpty()) {
            return handleExpressionError(LogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY,
                    CommonCssConstants.FLEX_FLOW, shorthandExpression);
        }

        final String[] flexFlowProps = shorthandExpression.split(" ");
        final List<CssDeclaration> resolvedProperties = new ArrayList<>();

        if (1 == flexFlowProps.length) {
            final CssDeclaration flexDirectionDeclaration = new CssDeclaration(CommonCssConstants.FLEX_DIRECTION,
                    flexFlowProps[0]);
            if (CssDeclarationValidationMaster.checkDeclaration(flexDirectionDeclaration)) {
                resolvedProperties.add(flexDirectionDeclaration);
            } else {
                final CssDeclaration flexWrapDeclaration = new CssDeclaration(CommonCssConstants.FLEX_WRAP,
                        flexFlowProps[0]);
                if (CssDeclarationValidationMaster.checkDeclaration(flexWrapDeclaration)) {
                    resolvedProperties.add(flexWrapDeclaration);
                } else {
                    return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                            CommonCssConstants.FLEX_FLOW,
                            shorthandExpression);
                }
            }
        } else if (2 == flexFlowProps.length) {
            CssDeclaration flexDirectionDeclaration = new CssDeclaration(CommonCssConstants.FLEX_DIRECTION,
                    flexFlowProps[0]);
            CssDeclaration flexWrapDeclaration = new CssDeclaration(CommonCssConstants.FLEX_WRAP,
                    flexFlowProps[1]);

            if (CssDeclarationValidationMaster.checkDeclaration(flexDirectionDeclaration)) {
                resolvedProperties.add(flexDirectionDeclaration);
            } else {
                // for some reasons browsers do support flex-wrap flex-direction order
                flexDirectionDeclaration = new CssDeclaration(CommonCssConstants.FLEX_DIRECTION,
                        flexFlowProps[1]);
                flexWrapDeclaration = new CssDeclaration(CommonCssConstants.FLEX_WRAP,
                        flexFlowProps[0]);

                if (CssDeclarationValidationMaster.checkDeclaration(flexDirectionDeclaration)) {
                    resolvedProperties.add(flexDirectionDeclaration);
                } else {
                    return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                            CommonCssConstants.FLEX_DIRECTION,
                            shorthandExpression);
                }
            }
            if (CssDeclarationValidationMaster.checkDeclaration(flexWrapDeclaration)) {
                resolvedProperties.add(flexWrapDeclaration);
            } else {
                return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                        CommonCssConstants.FLEX_WRAP,
                        shorthandExpression);
            }
        } else {
            return handleExpressionError(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                    CommonCssConstants.FLEX_FLOW,
                    shorthandExpression);
        }

        fillUnresolvedPropertiesWithDefaultValues(resolvedProperties);
        return resolvedProperties;
    }

    private static List<CssDeclaration> handleExpressionError(String logMessage, String attribute,
            String shorthandExpression) {
        LOGGER.warn(MessageFormatUtil.format(logMessage, attribute, shorthandExpression));
        return Collections.<CssDeclaration>emptyList();
    }

    private void fillUnresolvedPropertiesWithDefaultValues(List<CssDeclaration> resolvedProperties) {
        if (!resolvedProperties.stream()
                .anyMatch(property -> property.getProperty().equals(CommonCssConstants.FLEX_DIRECTION))) {
            resolvedProperties.add(new CssDeclaration(CommonCssConstants.FLEX_DIRECTION,
                    CssDefaults.getDefaultValue(CommonCssConstants.FLEX_DIRECTION)));
        }
        if (!resolvedProperties.stream()
                .anyMatch(property -> property.getProperty().equals(CommonCssConstants.FLEX_WRAP))) {
            resolvedProperties.add(new CssDeclaration(CommonCssConstants.FLEX_WRAP,
                    CssDefaults.getDefaultValue(CommonCssConstants.FLEX_WRAP)));
        }
    }
}
