package com.itextpdf.model.element;

import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.TextRenderer;

public class Text extends AbstractElement implements ILeafElement {

    String text;
    boolean isRTL = false;

    public Text(String text) {
        this.text = text;
    }

    @Override
    public IRenderer makeRenderer() {
        if (nextRenderer != null) {
            IRenderer renderer = nextRenderer;
            nextRenderer = null;
            return renderer;
        }
        return new TextRenderer(this, text);
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }
}
