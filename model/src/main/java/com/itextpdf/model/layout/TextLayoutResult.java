package com.itextpdf.model.layout;

import com.itextpdf.model.renderer.IRenderer;

public class TextLayoutResult extends LayoutResult {

    protected boolean wordHasBeenSplit;

    public TextLayoutResult(int status, LayoutArea occupiedArea, IRenderer splitRenderer, IRenderer overflowRenderer) {
        super(status, occupiedArea, splitRenderer, overflowRenderer);
    }

    public boolean isWordHasBeenSplit() {
        return wordHasBeenSplit;
    }

    public TextLayoutResult setWordHasBeenSplit(boolean wordHasBeenSplit) {
        this.wordHasBeenSplit = wordHasBeenSplit;
        return this;
    }
}
