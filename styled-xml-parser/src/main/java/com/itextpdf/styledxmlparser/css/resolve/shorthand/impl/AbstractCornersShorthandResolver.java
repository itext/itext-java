/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
        String[] props = shorthandExpression.split("\\s*\\/\\s*");
        String[][] properties = new String[props.length][];
        for (int i = 0; i < props.length; i++) {
            properties[i] = props[i].split("\\s+");
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
