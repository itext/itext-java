package com.itextpdf.svg.renderers.factories;

import com.itextpdf.svg.renderers.ISvgNodeRenderer;

import java.util.List;
import java.util.Map;

/**
 * Interface that will provide a mapping from SVG tag names to Renderers that
 * will be able to draw them. It's used in {@link DefaultSvgNodeRendererFactory}
 * to allow customizability in client code, and dependency injection in tests.
 */
public interface ISvgNodeRendererMapper {

    /**
     * Gets the map from tag names to Renderer classes.
     *
     * @return a {@link Map} with Strings as keys and {link @ISvgNodeRenderer}
     * implementations as values
     */
    Map<String, Class<? extends ISvgNodeRenderer>> getMapping();

    /**
     * Get the list of tags that do not map to any Renderer and should be ignored
     * @return
     */
    List<String> getIgnoredTags();
}
