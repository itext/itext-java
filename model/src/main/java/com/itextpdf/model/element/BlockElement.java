package com.itextpdf.model.element;

import com.itextpdf.model.renderer.BlockRenderer;
import com.itextpdf.model.renderer.IRenderer;

public abstract class BlockElement extends AbstractElement implements IAccessibleElement {

    public BlockElement() {
    }

    // TODO avoid copying implementations for convenient builder
    public BlockElement add(BlockElement element) {
        renderer.addChild(element.makeRenderer());
        return this;
    }

    public BlockElement add(InlineElement element) {
        makeRenderer().addChild(element.makeRenderer());
        return this;
    }

    public BlockElement add(ILeafElement element) {
        makeRenderer().addChild(element.makeRenderer());
        return this;
    }

    @Override
    public IRenderer makeRenderer() {
        if (renderer == null)
            renderer = new BlockRenderer(this);
        return renderer;
    }
}
