package com.itextpdf.model.element;

import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.InlineRenderer;

public abstract class InlineElement extends AbstractElement implements IAccessibleElement {

    protected IRenderer renderer;

    public InlineElement() {
    }

    InlineElement add(InlineElement element) {
        makeRenderer().addChild(element.makeRenderer());
        return this;
    }

    InlineElement add(ILeafElement element) {
        makeRenderer().addChild(element.makeRenderer());
        return this;
    }

    @Override
    public void setRenderer(IRenderer renderer) {
        this.renderer = renderer;
    }

    @Override
    public IRenderer makeRenderer() {
        if (renderer == null)
            renderer = new InlineRenderer();
        return renderer;
    }
}
