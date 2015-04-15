package com.itextpdf.model.element;

import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.ImageRenderer;

public class Image extends AbstractElement implements ILeafElement {
    @Override
    public IRenderer makeRenderer() {
        if (nextRenderer != null) {
            IRenderer renderer = nextRenderer;
            nextRenderer = null;
            return renderer;
        }
        return new ImageRenderer();
    }
}
