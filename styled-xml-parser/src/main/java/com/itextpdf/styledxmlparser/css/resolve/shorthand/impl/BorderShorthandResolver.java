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
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.ShorthandResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * {@link AbstractBorderShorthandResolver} implementation for borders.
 */
public class BorderShorthandResolver extends AbstractBorderShorthandResolver {

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.AbstractBorderShorthandResolver#getPrefix()
     */
    @Override
    protected String getPrefix() {
        return CommonCssConstants.BORDER;
    }

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.resolve.shorthand.impl.AbstractBorderShorthandResolver#resolveShorthand(java.lang.String)
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        List<CssDeclaration> preResolvedProps = super.resolveShorthand(shorthandExpression);
        List<CssDeclaration> resolvedProps = new ArrayList<>();
        for (CssDeclaration prop : preResolvedProps) {
            IShorthandResolver shorthandResolver = ShorthandResolverFactory.getShorthandResolver(prop.getProperty());
            if (shorthandResolver != null) {
                resolvedProps.addAll(shorthandResolver.resolveShorthand(prop.getExpression()));
            } else {
                Logger logger = LoggerFactory.getLogger(BorderShorthandResolver.class);
                logger.error(MessageFormatUtil.format("Cannot find a shorthand resolver for the \"{0}\" property. " +
                        "Expected border-width, border-style or border-color properties.", prop.getProperty()));
            }
        }
        return resolvedProps;
    }
}
