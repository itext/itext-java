package com.itextpdf.svg.processors.impl;

import com.itextpdf.svg.css.ICssResolver;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;

public class DefaultSvgProperties  implements ISvgConverterProperties{
    @Override
    public ICssResolver getCssResolver() {
        return null;
    }

    @Override
    public ISvgNodeRendererFactory getRendererFactory() {
        return null;
    }
}
