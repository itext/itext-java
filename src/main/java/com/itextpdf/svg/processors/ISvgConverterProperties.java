package com.itextpdf.svg.processors;

import com.itextpdf.svg.css.ICssResolver;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;

/**
 * Interface for the configuration classes used by {@link ISvgProcessor}
 */
public interface ISvgConverterProperties {

    /**
     * Retrieve the Css Resolver that the {@link ISvgProcessor} should use for resolving and assigning Css
     * @return A {@link ICssResolver} implementation
     */
    ICssResolver getCssResolver();

    /**
     * Retrieve the factory responsible for creating {@link com.itextpdf.svg.renderers.ISvgNodeRenderer}
     * @return A {@link ISvgNodeRendererFactory} implementation
     */
    ISvgNodeRendererFactory getRendererFactory();
}
