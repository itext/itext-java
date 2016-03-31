package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Property;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutResult;

public class CanvasRenderer extends RootRenderer {

    protected Canvas canvas;

    public CanvasRenderer(Canvas canvas) {
        this(canvas, true);
    }

    public CanvasRenderer(Canvas canvas, boolean immediateFlush) {
        this.canvas = canvas;
        this.modelElement = canvas;
        this.immediateFlush = immediateFlush;
    }

    @Override
    protected void flushSingleRenderer(IRenderer resultRenderer) {
        if (!resultRenderer.isFlushed()) {
            boolean toTag = canvas.getPdfDocument().isTagged() && canvas.isAutoTaggingEnabled();
            TagTreePointer tagPointer = null;
            if (toTag) {
                tagPointer = canvas.getPdfDocument().getTagStructureContext().getAutoTaggingPointer();
                tagPointer.setPageForTagging(canvas.getPage());
                tagPointer.setContentStreamForTagging(canvas.getPdfCanvas().getContentStream());
            }
            resultRenderer.draw(new DrawContext(canvas.getPdfDocument(), canvas.getPdfCanvas(), toTag));
            if (toTag) {
                tagPointer.setContentStreamForTagging(null);
            }
        }
    }

    @Override
    protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
        if (currentArea == null) {
            currentArea = new LayoutArea(0, canvas.getRootArea().clone());
        } else {
            setProperty(Property.FULL, true);
            currentArea = null;
        }
        return currentArea;
    }

    @Override
    public CanvasRenderer getNextRenderer() {
        return null;
    }
}
