package com.itextpdf.svg.dummy.processors.impl;

import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.svg.dummy.css.impl.DummyCssResolver;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.dummy.factories.DummySvgNodeFactory;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;

public class DummySvgConverterProperties implements ISvgConverterProperties {
    ICssResolver cssResolver;
    ISvgNodeRendererFactory rendererFactory;

    public DummySvgConverterProperties(){
        cssResolver = new DummyCssResolver();
        rendererFactory = new DummySvgNodeFactory();
    }

    @Override
    public ICssResolver getCssResolver() {
        return cssResolver;
    }

    @Override
    public ISvgNodeRendererFactory getRendererFactory() {
        return rendererFactory;
    }

    @Override
    public String getCharset() {
        return null;
    }
}
