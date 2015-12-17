package com.itextpdf.model.renderer;

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
            resultRenderer.draw(canvas.getPdfDocument(), canvas.getPdfCanvas());
        }
    }

    @Override
    protected LayoutArea getNextArea(LayoutResult overflowResult) {
        if (currentArea == null) {
            currentArea = new LayoutArea(0, canvas.getRootArea());
        }
        return currentArea;
    }

    @Override
    public CanvasRenderer getNextRenderer() {
        return null;
    }
}
