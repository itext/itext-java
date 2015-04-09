package com.itextpdf.model.element;

import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.ImageRenderer;

public class Image extends AbstractElement implements ILeafElement {
    @Override
    public IRenderer makeRenderer() {
        if (renderer == null)
            renderer = new ImageRenderer();
        return renderer;
    }
}
