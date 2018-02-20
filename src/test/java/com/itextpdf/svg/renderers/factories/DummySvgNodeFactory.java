package com.itextpdf.svg.renderers.factories;

import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.DummySvgNodeRenderer;

/**
 * A dummy implementation of {@link ISvgNodeRendererFactory}for testing purposes
 */
public class DummySvgNodeFactory implements ISvgNodeRendererFactory{

    @Override
    public ISvgNodeRenderer createSvgNodeRendererForTag(IElementNode tag, ISvgNodeRenderer parent) {
        return new DummySvgNodeRenderer(tag.name(),parent);
    }
}
