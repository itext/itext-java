package com.itextpdf.svg.processors;

import com.itextpdf.styledxmlparser.css.ICssResolver;
import com.itextpdf.svg.converter.SvgConverter;
import com.itextpdf.svg.renderers.factories.ISvgNodeRendererFactory;
import java.nio.charset.Charset;

/**
 * Interface for the configuration classes used by {@link ISvgProcessor}
 */
public interface ISvgConverterProperties {

    /**
     * Retrieve the CSS Resolver that the {@link ISvgProcessor} should use for
     * resolving and assigning CSS.
     *
     * @return A {@link ICssResolver} implementation
     */
    ICssResolver getCssResolver();

    /**
     * Retrieve the factory responsible for creating
     * {@link com.itextpdf.svg.renderers.ISvgNodeRenderer}
     *
     * @return A {@link ISvgNodeRendererFactory} implementation
     */
    ISvgNodeRendererFactory getRendererFactory();

    /**
     * Get the name of the Charset to be used when decoding an InputStream. This
     * method is allowed to return null, in which case {@code UTF-8} will
     * be used (by JSoup).
     *
     * Please be aware that this method is NOT used when handling a
     * {@code String} variable in the {@link SvgConverter}.
     *
     * @return the String name of the {@link Charset} used for decoding
     */
    String getCharset();
}
