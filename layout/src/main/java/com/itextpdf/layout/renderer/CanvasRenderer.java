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
package com.itextpdf.layout.renderer;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.layout.RootLayoutArea;
import com.itextpdf.layout.properties.Property;
import com.itextpdf.layout.properties.Transform;

import org.slf4j.LoggerFactory;

/**
 * Represents a renderer for the {@link Canvas} layout element.
 */
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
            LoggerFactory.getLogger(CanvasRenderer.class).warn(
                    IoLogMessageConstant.CANVAS_ALREADY_FULL_ELEMENT_WILL_BE_SKIPPED);
        } else {
            super.addChild(renderer);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void flushSingleRenderer(IRenderer resultRenderer) {
        linkRenderToDocument(resultRenderer, canvas.getPdfDocument());

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
