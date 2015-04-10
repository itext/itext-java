package com.itextpdf.model.element;

import com.itextpdf.model.renderer.BlockRenderer;
import com.itextpdf.model.renderer.IRenderer;

public abstract class BlockElement<T extends BlockElement> extends AbstractElement implements IAccessibleElement {

    public BlockElement() {
    }

    // TODO All in-flow children of a block flow must be blocks, or all in-flow children of a block flow must be inlines.
    // https://www.webkit.org/blog/115/webcore-rendering-ii-blocks-and-inlines/
    public T add(BlockElement element) {
        renderer.addChild(element.makeRenderer());
        return (T) this;
    }

    public T add(InlineElement element) {
        makeRenderer().addChild(element.makeRenderer());
        return (T) this;
    }

    public T add(ILeafElement element) {
        makeRenderer().addChild(element.makeRenderer());
        return (T) this;
    }

    @Override
    public IRenderer makeRenderer() {
        if (renderer == null)
            renderer = new BlockRenderer(this);
        return renderer;
    }
}
