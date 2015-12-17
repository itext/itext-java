package com.itextpdf.model.element;

import com.itextpdf.model.renderer.TabRenderer;

public class Tab extends AbstractElement<Tab> implements ILeafElement<Tab>, IAccessibleElement<Tab> {

    @Override
    protected TabRenderer makeNewRenderer() {
        return new TabRenderer(this);
    }
}
