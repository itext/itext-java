package com.itextpdf.model.element;

import com.itextpdf.model.renderer.BlockRenderer;
import com.itextpdf.model.renderer.IRenderer;

public abstract class BlockElement<T extends BlockElement> extends AbstractElement implements IAccessibleElement {

    public BlockElement() {
    }

    // TODO All in-flow children of a block flow must be blocks, or all in-flow children of a block flow must be inlines.
    // We will probably define layout strategy considering this.
    // https://www.webkit.org/blog/115/webcore-rendering-ii-blocks-and-inlines/
    public T add(BlockElement element) {
        childElements.add(element);
        return (T) this;
    }

    public T add(InlineElement element) {
        childElements.add(element);
        return (T) this;
    }

    public T add(ILeafElement element) {
        childElements.add(element);
        return (T) this;
    }

    @Override
    public IRenderer makeRenderer() {
        if (nextRenderer != null) {
            IRenderer renderer = nextRenderer;
            nextRenderer = null;
            return renderer;
        }
        return new BlockRenderer(this);
    }
}
