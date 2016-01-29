package com.itextpdf.layout.layout;

import com.itextpdf.layout.renderer.IRenderer;

public class TextLayoutResult extends LayoutResult {

    protected boolean wordHasBeenSplit;
    protected boolean splitForcedByNewline;

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

    public boolean isSplitForcedByNewline() {
        return splitForcedByNewline;
    }

    public TextLayoutResult setSplitForcedByNewline(boolean isSplitForcedByNewline) {
        this.splitForcedByNewline = isSplitForcedByNewline;
        return this;
    }
}
