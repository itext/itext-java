package com.itextpdf.model.layout;

import com.itextpdf.model.renderer.IRenderer;

public class LineLayoutResult extends LayoutResult {

    protected boolean splitForcedByNewline;

    public LineLayoutResult(int status, LayoutArea occupiedArea, IRenderer splitRenderer, IRenderer overflowRenderer) {
        super(status, occupiedArea, splitRenderer, overflowRenderer);
    }

    public boolean isSplitForcedByNewline() {
        return splitForcedByNewline;
    }

    public LineLayoutResult setSplitForcedByNewline(boolean isSplitForcedByNewline) {
        this.splitForcedByNewline = isSplitForcedByNewline;
        return this;
    }
}
