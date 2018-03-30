package com.itextpdf.svg.dummy.factories;

import com.itextpdf.styledxmlparser.node.IElementNode;
import com.itextpdf.svg.dummy.renderers.impl.DummyBranchSvgNodeRenderer;
import com.itextpdf.svg.dummy.renderers.impl.DummySvgNodeRenderer;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;

/**
 * A dummy implementation of {@link ISvgNodeRendererFactory}for testing purposes
 */
public class DummySvgNodeFactory implements ISvgNodeRendererFactory{

    @Override
    public ISvgNodeRenderer createSvgNodeRendererForTag(IElementNode tag, ISvgNodeRenderer parent) {
        ISvgNodeRenderer result;
        if ("svg".equals(tag.name())) {
            result = new DummyBranchSvgNodeRenderer(tag.name());
        }
        else {
            result = new DummySvgNodeRenderer(tag.name());
        }
        result.setParent(parent);
        return result;
    }

    @Override
    public boolean isTagIgnored(IElementNode tag) {
        return false;
    }
}
