package com.itextpdf.svg.processors;

import com.itextpdf.svg.renderers.ISvgNodeRenderer;

import java.util.Map;


/**
 * Interface for SVG processors results.
 */

public interface ISvgProcessorResult {

    /**
     * Obtains a map of named-objects with their id's as keys and the objects as values
     *
     * @return Map of Strings as keys and {@link ISvgNodeRenderer} as values
     */
    Map<String, ISvgNodeRenderer> getNamedObjects();

    /**
     * Obtains the wrapped {@link ISvgNodeRenderer} root renderer.
     *
     * @return ISvgNodeRenderer
     */
    ISvgNodeRenderer getRootRenderer();
}
