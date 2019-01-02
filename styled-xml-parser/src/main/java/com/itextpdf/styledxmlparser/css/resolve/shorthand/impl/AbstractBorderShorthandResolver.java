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

import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver;
import com.itextpdf.styledxmlparser.css.util.CssUtils;

import com.itextpdf.io.util.MessageFormatUtil;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Abstract {@link IShorthandResolver} implementation for borders.
 */
public abstract class AbstractBorderShorthandResolver implements IShorthandResolver {

    /** The template for -width properties. */
    private static final String _0_WIDTH = "{0}-width";

    /** The template for -style properties. */
    private static final String _0_STYLE = "{0}-style";

    /** The template for -color properties. */
    private static final String _0_COLOR = "{0}-color";

    /**
     * Gets the prefix of a property.
     *
     * @return the prefix
     */
    protected abstract String getPrefix();

    /* (non-Javadoc)
     * @see com.itextpdf.styledxmlparser.css.resolve.shorthand.IShorthandResolver#resolveShorthand(java.lang.String)
     */
    @Override
    public List<CssDeclaration> resolveShorthand(String shorthandExpression) {
        String widthPropName = MessageFormatUtil.format(_0_WIDTH, getPrefix());
        String stylePropName = MessageFormatUtil.format(_0_STYLE, getPrefix());
        String colorPropName = MessageFormatUtil.format(_0_COLOR, getPrefix());

        if (CommonCssConstants.INITIAL.equals(shorthandExpression) || CommonCssConstants.INHERIT.equals(shorthandExpression)) {
            return Arrays.asList(
                    new CssDeclaration(widthPropName, shorthandExpression),
                    new CssDeclaration(stylePropName, shorthandExpression),
                    new CssDeclaration(colorPropName, shorthandExpression));
        }

        String[] props = shorthandExpression.split("\\s+");

        String borderColorValue = null;
        String borderStyleValue = null;
        String borderWidthValue = null;

        for (String value : props) {
            if (CommonCssConstants.INITIAL.equals(value) || CommonCssConstants.INHERIT.equals(value)) {
                Logger logger = LoggerFactory.getLogger(AbstractBorderShorthandResolver.class);
                logger.warn(MessageFormatUtil.format(LogMessageConstant.INVALID_CSS_PROPERTY_DECLARATION, shorthandExpression));
                return Collections.<CssDeclaration>emptyList();
            }
            if (CommonCssConstants.BORDER_WIDTH_VALUES.contains(value) || CssUtils.isNumericValue(value)
                    || CssUtils.isMetricValue(value) || CssUtils.isRelativeValue(value)) {
                borderWidthValue = value;
            } else if (CommonCssConstants.BORDER_STYLE_VALUES.contains(value) || value.equals(CommonCssConstants.AUTO)) { // AUTO property value is needed for outline property only
                borderStyleValue = value;
            } else if (CssUtils.isColorProperty(value)) {
                borderColorValue = value;
            }
        }

        List<CssDeclaration> resolvedDecl = new ArrayList<>();
        resolvedDecl.add(new CssDeclaration(widthPropName, borderWidthValue == null ? CommonCssConstants.INITIAL : borderWidthValue));
        resolvedDecl.add(new CssDeclaration(stylePropName, borderStyleValue == null ? CommonCssConstants.INITIAL : borderStyleValue));
        resolvedDecl.add(new CssDeclaration(colorPropName, borderColorValue == null ? CommonCssConstants.INITIAL : borderColorValue));
        return resolvedDecl;
    }
}
