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
package com.itextpdf.styledxmlparser.css;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.css.util.CssUtils;

/**
 * Class to store a CSS declaration.
 */
public class CssDeclaration {

    /** The property. */
    private String property;

    /** The expression. */
    private String expression;

    /**
     * Instantiates a new CSS declaration.
     *
     * @param property the property
     * @param expression the expression
     */
    public CssDeclaration(String property, String expression) {
        this.property = resolveAlias(CssUtils.normalizeCssProperty(property));
        this.expression = CssUtils.normalizeCssProperty(expression);
    }

    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return MessageFormatUtil.format("{0}: {1}", property, expression);
    }

    /**
     * Gets the property.
     *
     * @return the property
     */
    public String getProperty() {
        return property;
    }

    /**
     * Gets the expression.
     *
     * @return the expression
     */
    public String getExpression() {
        return expression;
    }

    /**
     * Sets the expression.
     *
     * @param expression the new expression
     */
    public void setExpression(String expression) {
        this.expression = expression;
    }

    /**
     * Resolves css property aliases.
     * For example, word-wrap is an alias for overflow-wrap property.
     *
     * @param normalizedCssProperty css property to be resolved as alias
     * @return resolved property if the provided property was an alias, otherwise original provided property.
     */
    String resolveAlias(String normalizedCssProperty) {
        if (CommonCssConstants.WORDWRAP.equals(normalizedCssProperty)) {
            return CommonCssConstants.OVERFLOW_WRAP;
        }
        return normalizedCssProperty;
    }

}
