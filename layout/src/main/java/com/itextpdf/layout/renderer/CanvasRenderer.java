package com.itextpdf.layout.renderer;

import com.itextpdf.kernel.pdf.tagutils.PdfTagStructure;
import com.itextpdf.layout.Canvas;
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
