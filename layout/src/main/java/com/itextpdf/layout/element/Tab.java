package com.itextpdf.layout.element;

import com.itextpdf.layout.renderer.TabRenderer;

public class Tab extends AbstractElement<Tab> implements ILeafElement<Tab>, IElement<Tab> {

    @Override
    protected TabRenderer makeNewRenderer() {
        return new TabRenderer(this);
    }
}
