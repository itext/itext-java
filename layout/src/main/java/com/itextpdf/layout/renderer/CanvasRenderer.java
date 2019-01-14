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
package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.borders.Border;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.property.Property;

import com.itextpdf.layout.property.Transform;
import org.slf4j.LoggerFactory;

public class CanvasRenderer extends RootRenderer {

    protected Canvas canvas;

    /**
     * Creates a CanvasRenderer from its corresponding layout object.
     * Sets {@link #immediateFlush} to true.
     *
     * @param canvas the {@link com.itextpdf.layout.Canvas} which this object should manage
     */
    public CanvasRenderer(Canvas canvas) {
        this(canvas, true);
    }

    /**
     * Creates a CanvasRenderer from its corresponding layout object.
     * Defines whether the content should be flushed immediately after addition {@link #addChild(IRenderer)} or not
     *
     * @param canvas         the {@link com.itextpdf.layout.Canvas} which this object should manage
     * @param immediateFlush the value which stands for immediate flushing
     */
    public CanvasRenderer(Canvas canvas, boolean immediateFlush) {
        this.canvas = canvas;
        this.modelElement = canvas;
        this.immediateFlush = immediateFlush;
    }

    @Override
    public void addChild(IRenderer renderer) {
        if (Boolean.TRUE.equals(getPropertyAsBoolean(Property.FULL))) {
            LoggerFactory.getLogger(CanvasRenderer.class).warn("Canvas is already full. Element will be skipped.");
        } else {
            super.addChild(renderer);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void flushSingleRenderer(IRenderer resultRenderer) {
        Transform transformProp = resultRenderer.<Transform>getProperty(Property.TRANSFORM);
        if (!waitingDrawingElements.contains(resultRenderer)) {
            processWaitingDrawing(resultRenderer, transformProp, waitingDrawingElements);
            if (FloatingHelper.isRendererFloating(resultRenderer) || transformProp != null)
                return;
        }

        if (!resultRenderer.isFlushed()) {
            boolean toTag = canvas.getPdfDocument().isTagged() && canvas.isAutoTaggingEnabled();
            TagTreePointer tagPointer = null;
            if (toTag) {
                tagPointer = canvas.getPdfDocument().getTagStructureContext().getAutoTaggingPointer();
                tagPointer.setPageForTagging(canvas.getPage());

                boolean pageStream = false;
                for (int i = canvas.getPage().getContentStreamCount() - 1; i >= 0; --i) {
                    if (canvas.getPage().getContentStream(i).equals(canvas.getPdfCanvas().getContentStream())) {
                        pageStream = true;
                        break;
                    }
                }
                if (!pageStream) {
                    tagPointer.setContentStreamForTagging(canvas.getPdfCanvas().getContentStream());
                }
            }
            resultRenderer.draw(new DrawContext(canvas.getPdfDocument(), canvas.getPdfCanvas(), toTag));
            if (toTag) {
                tagPointer.setContentStreamForTagging(null);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
        if (currentArea == null) {
            int pageNumber = canvas.isCanvasOfPage() ? canvas.getPdfDocument().getPageNumber(canvas.getPage()) : 0;
            currentArea = new RootLayoutArea(pageNumber, canvas.getRootArea().clone());
        } else {
            setProperty(Property.FULL, true);
            currentArea = null;
        }
        return currentArea;
    }

    /**
     * For {@link CanvasRenderer}, this has a meaning of the renderer that will be used for relayout.
     *
     * @return relayout renderer.
     */
    @Override
    public IRenderer getNextRenderer() {
        return new CanvasRenderer(canvas, immediateFlush);
    }
}
