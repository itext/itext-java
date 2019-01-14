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
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Abstract {@link IShorthandResolver} implementation for box definitions.
 */
public abstract class AbstractBoxShorthandResolver implements IShorthandResolver {

    /** The template for -left properties. */
    private static final String _0_LEFT_1 = "{0}-left{1}";

    /** The template for -right properties. */
    private static final String _0_RIGHT_1 = "{0}-right{1}";

    /** The template for -bottom properties. */
    private static final String _0_BOTTOM_1 = "{0}-bottom{1}";

    /** The template for -top properties. */
    private static final String _0_TOP_1 = "{0}-top{1}";

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
        String[] props = shorthandExpression.split("\\s+");
        List<CssDeclaration> resolvedDecl = new ArrayList<>();
        String topProperty = MessageFormatUtil.format(_0_TOP_1, getPrefix(), getPostfix());
        String rightProperty = MessageFormatUtil.format(_0_RIGHT_1, getPrefix(), getPostfix());
        String bottomProperty = MessageFormatUtil.format(_0_BOTTOM_1, getPrefix(), getPostfix());
        String leftProperty = MessageFormatUtil.format(_0_LEFT_1, getPrefix(), getPostfix());
        if (props.length == 1) {
            resolvedDecl.add(new CssDeclaration(topProperty, props[0]));
            resolvedDecl.add(new CssDeclaration(rightProperty, props[0]));
            resolvedDecl.add(new CssDeclaration(bottomProperty, props[0]));
            resolvedDecl.add(new CssDeclaration(leftProperty, props[0]));
        } else {
            for (String prop : props) {
                if (CommonCssConstants.INHERIT.equals(prop) || CommonCssConstants.INITIAL.equals(prop)) {
                    Logger logger = LoggerFactory.getLogger(AbstractBoxShorthandResolver.class);
                    logger.warn(MessageFormatUtil.format(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, shorthandExpression));
                    return Collections.<CssDeclaration>emptyList();
                }
            }
            if (props.length == 2) {
                resolvedDecl.add(new CssDeclaration(topProperty, props[0]));
                resolvedDecl.add(new CssDeclaration(rightProperty, props[1]));
                resolvedDecl.add(new CssDeclaration(bottomProperty, props[0]));
                resolvedDecl.add(new CssDeclaration(leftProperty, props[1]));
            } else if (props.length == 3) {
                resolvedDecl.add(new CssDeclaration(topProperty, props[0]));
                resolvedDecl.add(new CssDeclaration(rightProperty, props[1]));
                resolvedDecl.add(new CssDeclaration(bottomProperty, props[2]));
                resolvedDecl.add(new CssDeclaration(leftProperty, props[1]));
            } else if (props.length == 4) {
                resolvedDecl.add(new CssDeclaration(topProperty, props[0]));
                resolvedDecl.add(new CssDeclaration(rightProperty, props[1]));
                resolvedDecl.add(new CssDeclaration(bottomProperty, props[2]));
                resolvedDecl.add(new CssDeclaration(leftProperty, props[3]));
            }
        }
        return resolvedDecl;
    }
}
