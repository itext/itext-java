package com.itextpdf.model.renderer;

import com.itextpdf.core.pdf.tagutils.PdfTagStructure;
import com.itextpdf.model.Canvas;
import com.itextpdf.model.layout.LayoutArea;
import com.itextpdf.model.layout.LayoutResult;

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
            if (toTag) {
                PdfTagStructure tagStructure = canvas.getPdfDocument().getTagStructure();
                tagStructure.setPage(canvas.getPage());
                tagStructure.setContentStream(canvas.getPdfCanvas().getContentStream());
            }
            resultRenderer.draw(new DrawContext(canvas.getPdfDocument(), canvas.getPdfCanvas(), toTag));
        }
    }

    @Override
    protected LayoutArea updateCurrentArea(LayoutResult overflowResult) {
        if (currentArea == null) {
            currentArea = new LayoutArea(0, canvas.getRootArea());
        } else {
            currentArea = null;
        }
        return currentArea;
    }

    @Override
    public CanvasRenderer getNextRenderer() {
        return null;
    }
}
