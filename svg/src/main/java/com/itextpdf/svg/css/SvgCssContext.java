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
package com.itextpdf.svg.css;

import com.itextpdf.styledxmlparser.css.resolve.AbstractCssContext;
import com.itextpdf.styledxmlparser.css.util.CssDimensionParsingUtils;
import com.itextpdf.svg.css.impl.SvgStyleResolver;

/**
 * Context necessary for evaluating certain Css statements whose final values depends on other statements
 * e.g. relative font-size statements.
 */
public class SvgCssContext extends AbstractCssContext {
    /** The root font size value in pt. */
    private float rootFontSize = SvgStyleResolver.DEFAULT_FONT_SIZE;

    /**
     * Gets the root font size.
     *
     * @return the root font size in pt
     */
    public float getRootFontSize() {
        return rootFontSize;
    }

    /**
     * Sets the root font size.
     *
     * @param fontSizeStr the new root font size
     */
    public void setRootFontSize(String fontSizeStr) {
        this.rootFontSize = CssDimensionParsingUtils.parseAbsoluteFontSize(fontSizeStr);
    }

    /**
     * Sets the root font size.
     *
     * @param rootFontSize the new root font size
     */
    public void setRootFontSize(float rootFontSize) {
        this.rootFontSize = rootFontSize;
    }
}
