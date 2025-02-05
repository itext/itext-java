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
package com.itextpdf.styledxmlparser.css.resolve.shorthand;

import com.itextpdf.styledxmlparser.css.CssDeclaration;

import java.util.List;

/**
 * Interface for shorthand resolvers.
 * <p>
 * CSS shorthand is a group of CSS properties that allow values of multiple properties to be set simultaneously. These
 * values are separated by spaces. For example, the border property is shorthand for the border-width, border-style, and
 * border-color properties. So in CSS, border: 5px solid red; would specify a border that’s five px wide, solid, and
 * red.
 */
public interface IShorthandResolver {

    /**
     * Resolves a shorthand expression.
     *
     * @param shorthandExpression the shorthand expression
     *
     * @return a list of CSS declaration
     */
    List<CssDeclaration> resolveShorthand(String shorthandExpression);
}
