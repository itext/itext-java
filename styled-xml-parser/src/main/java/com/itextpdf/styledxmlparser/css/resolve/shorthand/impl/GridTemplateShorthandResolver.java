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
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.parse.CssDeclarationValueTokenizer;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssTypesValidationUtils;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link IShorthandResolver} implementation for grid-template shorthand.
 */
public class GridTemplateShorthandResolver implements IShorthandResolver {
    /**
     * Creates grid template shorthand resolver.
     */
    public GridTemplateShorthandResolver() {
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(GridTemplateShorthandResolver.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        shorthandExpression = shorthandExpression.trim();
        if (shorthandExpression.isEmpty()) {
            LOGGER.warn(MessageFormatUtil.format(
                    StyledXmlParserLogMessageConstant.SHORTHAND_PROPERTY_CANNOT_BE_EMPTY,
                    CommonCssConstants.GRID_TEMPLATE
            ));
            return new ArrayList<>();
        }
        if (CssTypesValidationUtils.isInitialOrInheritOrUnset(shorthandExpression)
            || CommonCssConstants.AUTO.equals(shorthandExpression)
            || CommonCssConstants.NONE.equals(shorthandExpression)) {
            return new ArrayList<>();
        }

        StringBuilder rowsTemplateBuilder = new StringBuilder();
        StringBuilder areasBuilder = new StringBuilder();
        String columnsTemplate = "";
        String[] values = shorthandExpression.split("/");
        if (values.length == 2) {
            columnsTemplate = values[1];
        }

        CssDeclarationValueTokenizer tokenizer = new CssDeclarationValueTokenizer(values[0]);
        CssDeclarationValueTokenizer.Token token;
        boolean templateRowsEncountered = false;
        boolean previousTokenIsArea = false;
        for (int i = 0; ((token = tokenizer.getNextValidToken()) != null); ++i) {
            if (token.isString() && !token.getValue().startsWith("[")) {
                if (previousTokenIsArea) {
                    rowsTemplateBuilder.append(CommonCssConstants.AUTO).append(" ");
                }
                areasBuilder.append("'").append(token.getValue()).append("'").append(" ");
                previousTokenIsArea = true;
            } else {
                rowsTemplateBuilder.append(token.getValue()).append(" ");
                templateRowsEncountered = true;
                previousTokenIsArea = false;
            }
        }
        if (previousTokenIsArea) {
            rowsTemplateBuilder.append(CommonCssConstants.AUTO).append(" ");
        }
        if (!templateRowsEncountered) {
            rowsTemplateBuilder.setLength(0);
        }
        String rowsTemplate = rowsTemplateBuilder.toString();
        String areas = areasBuilder.toString();

        List<CssDeclaration> result = new ArrayList<>(3);
        if (!rowsTemplate.isEmpty()) {
            result.add(new CssDeclaration(CommonCssConstants.GRID_TEMPLATE_ROWS, rowsTemplate));
        }
        if (!columnsTemplate.isEmpty()) {
            result.add(new CssDeclaration(CommonCssConstants.GRID_TEMPLATE_COLUMNS, columnsTemplate));
        }
        if (!areas.isEmpty()) {
            result.add(new CssDeclaration(CommonCssConstants.GRID_TEMPLATE_AREAS, areas));
        }
        return result;
    }
}
