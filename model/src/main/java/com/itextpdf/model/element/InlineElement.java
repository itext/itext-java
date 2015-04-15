package com.itextpdf.model.element;

import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.InlineRenderer;

public abstract class InlineElement extends AbstractElement implements IAccessibleElement {

    protected IRenderer renderer;

    public InlineElement() {
    }

    InlineElement add(InlineElement element) {
        childElements.add(element);
        return this;
    }

    InlineElement add(ILeafElement element) {
        childElements.add(element);
        return this;
    }

    @Override
    public IRenderer makeRenderer() {
        if (renderer == null)
            renderer = new InlineRenderer(this);
        return renderer;
    }
}
