package com.itextpdf.model.element;

import com.itextpdf.model.Property;
import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.TabRenderer;

public class Tab extends AbstractElement<Tab> implements ILeafElement<Tab>, IAccessibleElement<Tab> {

    @Override
    public IRenderer makeRenderer() {
        if (nextRenderer != null) {
            IRenderer renderer = nextRenderer;
            nextRenderer = null;
            return renderer;
        }
        return new TabRenderer(this);
    }
}
