package com.itextpdf.model.renderer;

import com.itextpdf.model.element.Image;
import com.itextpdf.model.layout.LayoutContext;
import com.itextpdf.model.layout.LayoutResult;

public class ImageRenderer extends AbstractRenderer {

    public ImageRenderer(Image modelElement) {
        super(modelElement);
    }

    @Override
    public LayoutResult layout(LayoutContext layoutContext) {
        throw new UnsupportedOperationException();
    }

}
