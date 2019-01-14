/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.layout.layout;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.layout.margincollapse.MarginsCollapseInfo;
import java.util.List;

/**
 * Represents the context for content of a line {@link com.itextpdf.layout.renderer.IRenderer#layout(LayoutContext) layouting}.
 */
public class LineLayoutContext extends LayoutContext {
    private boolean floatOverflowedToNextPageWithNothing = false;
    private float textIndent;

    public LineLayoutContext(LayoutArea area, MarginsCollapseInfo marginsCollapseInfo, List<Rectangle> floatedRendererAreas, boolean clippedHeight) {
        super(area, marginsCollapseInfo, floatedRendererAreas, clippedHeight);
    }

    public LineLayoutContext(LayoutContext layoutContext) {
        super(layoutContext.area, layoutContext.marginsCollapseInfo, layoutContext.floatRendererAreas, layoutContext.clippedHeight);
    }

    /**
     * Specifies whether some floating element within the same paragraph has already completely overflowed to the next
     * page.
     * @return true if floating element has already overflowed to the next page, false otherwise.
     */
    public boolean isFloatOverflowedToNextPageWithNothing() {
        return floatOverflowedToNextPageWithNothing;
    }

    /**
     * Changes the value of property specified by {@link #isFloatOverflowedToNextPageWithNothing()}.
     * @param floatOverflowedToNextPageWithNothing true if some floating element already completely overflowed.
     * @return this {@link LineLayoutContext} instance.
     */
    public LineLayoutContext setFloatOverflowedToNextPageWithNothing(boolean floatOverflowedToNextPageWithNothing) {
        this.floatOverflowedToNextPageWithNothing = floatOverflowedToNextPageWithNothing;
        return this;
    }

    /**
     * Gets the indent of text in the beginning of the current line.
     * @return the indent of text in this line.
     */
    public float getTextIndent() {
        return textIndent;
    }

    /**
     * Sets the indent of text in the beginning of the current line.
     * @param textIndent the indent of text in this line.
     * @return this {@link LineLayoutContext} instance.
     */
    public LineLayoutContext setTextIndent(float textIndent) {
        this.textIndent = textIndent;
        return this;
    }
}
