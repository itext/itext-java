package com.itextpdf.kernel.pdf.canvas.parser.data;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.pdf.canvas.CanvasGraphicsState;

public class AbstractRenderInfo implements IEventData {

    protected CanvasGraphicsState gs;
    private boolean graphicsStateIsPreserved;

    public AbstractRenderInfo(CanvasGraphicsState gs) {
        this.gs = gs;
    }

    @Override
    public CanvasGraphicsState getGraphicsState() {
        checkGraphicsState();
        return graphicsStateIsPreserved ? gs : new CanvasGraphicsState(gs);
    }

    public boolean isGraphicsStatePreserved() {
        return graphicsStateIsPreserved;
    }

    public void preserveGraphicsState() {
        checkGraphicsState();
        this.graphicsStateIsPreserved = true;
        gs = new CanvasGraphicsState(gs);
    }

    public void releaseGraphicsState() {
        if (!graphicsStateIsPreserved) {
            gs = null;
        }
    }


    // check if graphics state was released
    protected void checkGraphicsState() {
        if (null == gs) {
            throw new IllegalStateException(LogMessageConstant.GRAPHICS_STATE_WAS_DELETED);
        }
    }
}
