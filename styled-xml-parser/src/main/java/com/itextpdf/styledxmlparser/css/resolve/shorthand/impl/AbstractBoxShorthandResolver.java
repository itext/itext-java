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
import com.itextpdf.styledxmlparser.css.util.CssUtils;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
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
        List<String> props = CssUtils.extractShorthandProperties(shorthandExpression).get(0);
        List<CssDeclaration> resolvedDecl = new ArrayList<>();
        String topProperty = MessageFormatUtil.format(_0_TOP_1, getPrefix(), getPostfix());
        String rightProperty = MessageFormatUtil.format(_0_RIGHT_1, getPrefix(), getPostfix());
        String bottomProperty = MessageFormatUtil.format(_0_BOTTOM_1, getPrefix(), getPostfix());
        String leftProperty = MessageFormatUtil.format(_0_LEFT_1, getPrefix(), getPostfix());
        if (props.size() == 1) {
            resolvedDecl.add(new CssDeclaration(topProperty, props.get(0)));
            resolvedDecl.add(new CssDeclaration(rightProperty, props.get(0)));
            resolvedDecl.add(new CssDeclaration(bottomProperty, props.get(0)));
            resolvedDecl.add(new CssDeclaration(leftProperty, props.get(0)));
        } else {
            for (String prop : props) {
                if (CommonCssConstants.INHERIT.equals(prop) || CommonCssConstants.INITIAL.equals(prop)) {
                    Logger logger = LoggerFactory.getLogger(AbstractBoxShorthandResolver.class);
                    logger.warn(
                            MessageFormatUtil.format(StyledXmlParserLogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION,
                                    shorthandExpression));
                    return Collections.<CssDeclaration>emptyList();
                }
            }
            if (props.size() == 2) {
                resolvedDecl.add(new CssDeclaration(topProperty, props.get(0)));
                resolvedDecl.add(new CssDeclaration(rightProperty, props.get(1)));
                resolvedDecl.add(new CssDeclaration(bottomProperty, props.get(0)));
                resolvedDecl.add(new CssDeclaration(leftProperty, props.get(1)));
            } else if (props.size() == 3) {
                resolvedDecl.add(new CssDeclaration(topProperty, props.get(0)));
                resolvedDecl.add(new CssDeclaration(rightProperty, props.get(1)));
                resolvedDecl.add(new CssDeclaration(bottomProperty, props.get(2)));
                resolvedDecl.add(new CssDeclaration(leftProperty, props.get(1)));
            } else if (props.size() == 4) {
                resolvedDecl.add(new CssDeclaration(topProperty, props.get(0)));
                resolvedDecl.add(new CssDeclaration(rightProperty, props.get(1)));
                resolvedDecl.add(new CssDeclaration(bottomProperty, props.get(2)));
                resolvedDecl.add(new CssDeclaration(leftProperty, props.get(3)));
            }
        }
        return resolvedDecl;
    }
}
