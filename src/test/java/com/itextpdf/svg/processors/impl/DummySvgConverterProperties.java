package com.itextpdf.svg.processors.impl;

import com.itextpdf.svg.css.ICssResolver;
import com.itextpdf.svg.css.impl.DummyCssResolver;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.renderers.factories.DummySvgNodeFactory;
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
}
