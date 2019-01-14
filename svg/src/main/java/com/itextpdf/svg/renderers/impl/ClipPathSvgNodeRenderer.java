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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;

/**
 * This renderer represents a collection of elements (simple shapes and paths).
 * The elements are not drawn visibly, but the union of their shapes will be used
 * to only show the parts of the drawn objects that fall within the clipping path.
 *
 * In PDF, the clipping path operators use the intersection of all its elements, not the union (as in SVG);
 * thus, we need to draw the clipped elements multiple times if the clipping path consists of multiple elements.
 */
public class ClipPathSvgNodeRenderer extends AbstractBranchSvgNodeRenderer {

    private AbstractSvgNodeRenderer clippedRenderer;

    @Override
    public ISvgNodeRenderer createDeepCopy() {
        AbstractBranchSvgNodeRenderer copy = new ClipPathSvgNodeRenderer();
        deepCopyAttributesAndStyles(copy);
        deepCopyChildren(copy);
        return copy;
    }

    @Override void preDraw(SvgDrawContext context) {}

    @Override
    protected void doDraw(SvgDrawContext context) {
        PdfCanvas currentCanvas = context.getCurrentCanvas();
        for (ISvgNodeRenderer child : getChildren()) {
            currentCanvas.saveState();

            if (child instanceof AbstractSvgNodeRenderer) {
                ((AbstractSvgNodeRenderer) child).setPartOfClipPath(true);
            }

            child.draw(context);

            if (child instanceof AbstractSvgNodeRenderer) {
                ((AbstractSvgNodeRenderer) child).setPartOfClipPath(false);
            }

            if (clippedRenderer != null) {
                clippedRenderer.preDraw(context);
                clippedRenderer.doDraw(context);
                clippedRenderer.postDraw(context);
            }

            currentCanvas.restoreState();
        }

    }

    public void setClippedRenderer(AbstractSvgNodeRenderer clippedRenderer) {
        this.clippedRenderer = clippedRenderer;
    }
}
