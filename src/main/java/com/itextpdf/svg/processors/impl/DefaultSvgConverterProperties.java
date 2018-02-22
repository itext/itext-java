package com.itextpdf.svg.processors.impl;

import com.itextpdf.svg.css.ICssResolver;
import com.itextpdf.svg.css.impl.DefaultCssResolver;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.renderers.factories.DefaultSvgNodeRendererFactory;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;

/**
 * Default and fallback implementation of {@link ISvgConverterProperties} for {@link DefaultSvgProcessor}
 */
public class DefaultSvgConverterProperties implements ISvgConverterProperties{

    private ICssResolver cssResolver;
    private ISvgNodeRendererFactory rendererFactory;

    public DefaultSvgConverterProperties(){
        cssResolver = new DefaultCssResolver();
        rendererFactory = new DefaultSvgNodeRendererFactory();
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
