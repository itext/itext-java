package com.itextpdf.model.element;

import com.itextpdf.model.renderer.IRenderer;
import com.itextpdf.model.renderer.InlineRenderer;

public abstract class InlineElement implements IAccessibleElement {

    protected IRenderer renderer;

    public InlineElement() {
    }

    InlineElement add(InlineElement element) {
        makeRenderer().addChild(element.makeRenderer());
        return this;
    }

    // TODO All in-flow children of a block flow must be blocks, or all in-flow children of a block flow must be inlines.
    // https://www.webkit.org/blog/115/webcore-rendering-ii-blocks-and-inlines/
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
