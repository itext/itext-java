/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * {@link IShorthandResolver} implementation for grid items column/row start and end positions.
 */
public abstract class GridItemShorthandResolver implements IShorthandResolver {
    private static final Logger LOGGER = LoggerFactory.getLogger(GridItemShorthandResolver.class);
    private final String propertyTemplate;

    /**
     * Creates a new shorthand resolver for provided shorthand template
     *
     * @param shorthand shorthand from which template will be created.
     */
    protected GridItemShorthandResolver(String shorthand) {
        this.propertyTemplate = shorthand + "-{0}";
    }

    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        shorthandExpression = shorthandExpression.trim();
        if (shorthandExpression.isEmpty()) {
            LOGGER.warn(MessageFormatUtil.format(
                    StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY,
                    propertyTemplate.substring(0, propertyTemplate.length() - 4)
            ));
            return new ArrayList<>();
        }
        if (CssTypesValidationUtils.isInitialOrInheritOrUnset(shorthandExpression)
                || CommonCssConstants.AUTO.equals(shorthandExpression)) {
            return new ArrayList<>();
        }
        final String[] values = shorthandExpression.split("/");
        if (values.length == 1) {
            if (shorthandExpression.startsWith("span")) {
                return Collections.singletonList(
                        new CssDeclaration(MessageFormatUtil.format(propertyTemplate, "start"), values[0]));
            }
            return Arrays.asList(
                    new CssDeclaration(MessageFormatUtil.format(propertyTemplate, "start"), values[0]),
                    new CssDeclaration(MessageFormatUtil.format(propertyTemplate, "end"), values[0])
            );
        }
        return Arrays.asList(
                new CssDeclaration(MessageFormatUtil.format(propertyTemplate, "start"), values[0]),
                new CssDeclaration(MessageFormatUtil.format(propertyTemplate, "end"), values[1])
        );
    }
}
