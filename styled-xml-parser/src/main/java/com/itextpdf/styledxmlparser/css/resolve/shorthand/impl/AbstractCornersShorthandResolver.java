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
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract {@link IShorthandResolver} implementation for corners definitions.
 */
public abstract class AbstractCornersShorthandResolver implements IShorthandResolver {

    /**
     * The template for -bottom-left properties.
     */
    private static final String _0_BOTTOM_LEFT_1 = "{0}-bottom-left{1}";

    /**
     * The template for -bottom-right properties.
     */
    private static final String _0_BOTTOM_RIGHT_1 = "{0}-bottom-right{1}";

    /**
     * The template for -top-left properties.
     */
    private static final String _0_TOP_LEFT_1 = "{0}-top-left{1}";

    /**
     * The template for -top-right properties.
     */
    private static final String _0_TOP_RIGHT_1 = "{0}-top-right{1}";

    /**
     * Gets the prefix of a property.
     *
     * @return the prefix
     */
    protected abstract String getPrefix();

    /**
     * Gets the postfix of a property.
     *
     * @return the postfix
     */
    protected abstract String getPostfix();

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver#resolveShorthand(java.lang.String)
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        String[] props = shorthandExpression.split("\\/");
        String[][] properties = new String[props.length][];
        for (int i = 0; i < props.length; i++) {
            properties[i] = props[i].trim().split("\\s+");
        }

        String[] resultExpressions = new String[4];
        for (int i = 0; i < resultExpressions.length; i++) {
            resultExpressions[i] = "";
        }

        List<CssDeclaration> resolvedDecl = new ArrayList<>();
        String topLeftProperty = MessageFormatUtil.format(_0_TOP_LEFT_1, getPrefix(), getPostfix());
        String topRightProperty = MessageFormatUtil.format(_0_TOP_RIGHT_1, getPrefix(), getPostfix());
        String bottomRightProperty = MessageFormatUtil.format(_0_BOTTOM_RIGHT_1, getPrefix(), getPostfix());
        String bottomLeftProperty = MessageFormatUtil.format(_0_BOTTOM_LEFT_1, getPrefix(), getPostfix());

        for (int i = 0; i < properties.length; i++) {
            if (properties[i].length == 1) {
                resultExpressions[0] += properties[i][0] + " ";
                resultExpressions[1] += properties[i][0] + " ";
                resultExpressions[2] += properties[i][0] + " ";
                resultExpressions[3] += properties[i][0] + " ";
            } else if (properties[i].length == 2) {
                resultExpressions[0] += properties[i][0] + " ";
                resultExpressions[1] += properties[i][1] + " ";
                resultExpressions[2] += properties[i][0] + " ";
                resultExpressions[3] += properties[i][1] + " ";
            } else if (properties[i].length == 3) {
                resultExpressions[0] += properties[i][0] + " ";
                resultExpressions[1] += properties[i][1] + " ";
                resultExpressions[2] += properties[i][2] + " ";
                resultExpressions[3] += properties[i][1] + " ";
            } else if (properties[i].length == 4) {
                resultExpressions[0] += properties[i][0] + " ";
                resultExpressions[1] += properties[i][1] + " ";
                resultExpressions[2] += properties[i][2] + " ";
                resultExpressions[3] += properties[i][3] + " ";
            }
        }

        resolvedDecl.add(new CssDeclaration(topLeftProperty, resultExpressions[0]));
        resolvedDecl.add(new CssDeclaration(topRightProperty, resultExpressions[1]));
        resolvedDecl.add(new CssDeclaration(bottomRightProperty, resultExpressions[2]));
        resolvedDecl.add(new CssDeclaration(bottomLeftProperty, resultExpressions[3]));
        return resolvedDecl;
    }
}
