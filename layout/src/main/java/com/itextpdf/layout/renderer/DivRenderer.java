package com.itextpdf.layout.renderer;

import com.itextpdf.layout.element.Div;

public class DivRenderer extends BlockRenderer {

    public DivRenderer(Div modelElement) {
        super(modelElement);
    }

    @Override
    public IRenderer getNextRenderer() {
        return new DivRenderer((Div) modelElement);
    }
}
