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

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link IShorthandResolver} implementation for grid shorthand.
 */
public class GridShorthandResolver implements IShorthandResolver {

    /**
     * {@inheritDoc}
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        if (!shorthandExpression.contains(CommonCssConstants.AUTO_FLOW)) {
            return new GridTemplateShorthandResolver().resolveShorthand(shorthandExpression);
        }
        String[] values = shorthandExpression.trim().split("/");
        List<CssDeclaration> result = new ArrayList<>();
        if (values[0].contains(CommonCssConstants.AUTO_FLOW)) {
            if (values[0].contains(CommonCssConstants.DENSE)) {
                result.add(new CssDeclaration(CommonCssConstants.GRID_AUTO_FLOW, CommonCssConstants.DENSE));
            }
            String rowsTemplate =
                    values[0].substring(
                            Math.max(values[0].indexOf(CommonCssConstants.AUTO_FLOW) + CommonCssConstants.AUTO_FLOW.length(),
                                    values[0].indexOf(CommonCssConstants.DENSE) + CommonCssConstants.DENSE.length())
                    );
            if (!rowsTemplate.trim().isEmpty()) {
                result.add(new CssDeclaration(CommonCssConstants.GRID_AUTO_ROWS, rowsTemplate));
            }
            if (values.length == 2) {
                result.add(new CssDeclaration(CommonCssConstants.GRID_TEMPLATE_COLUMNS, values[1]));
            }
        } else if (values.length == 2) {
            result.add(new CssDeclaration(CommonCssConstants.GRID_TEMPLATE_ROWS, values[0]));
            if (values[1].contains(CommonCssConstants.DENSE)) {
                result.add(new CssDeclaration(CommonCssConstants.GRID_AUTO_FLOW, CommonCssConstants.COLUMN + " " + CommonCssConstants.DENSE));
            }
            String columnsTemplate =
                    values[1].substring(
                            Math.max(values[1].indexOf(CommonCssConstants.AUTO_FLOW) + CommonCssConstants.AUTO_FLOW.length(),
                                    values[1].indexOf(CommonCssConstants.DENSE) + CommonCssConstants.DENSE.length())
                    );
            if (!columnsTemplate.trim().isEmpty()) {
                result.add(new CssDeclaration(CommonCssConstants.GRID_AUTO_COLUMNS, columnsTemplate));
            }
        }
        return result;
    }
}
