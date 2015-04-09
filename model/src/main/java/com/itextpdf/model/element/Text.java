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
        if (renderer == null)
            renderer = new TextRenderer(this, text);
        return renderer;
    }

    public String getText() {
        return text;
    }
}
