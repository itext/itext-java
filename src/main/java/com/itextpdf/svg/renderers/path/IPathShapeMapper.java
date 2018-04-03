package com.itextpdf.svg.renderers.path;

import java.util.Map;

/**
 * Maps {@link IPathShape} on their names.
 */
public interface IPathShapeMapper {

    /**
     * Provides a mapping of Path-data instructions' names to path shape classes.
     *
     * @return a {@link Map} with Strings as keys and {link @{@link IPathShape}
     * implementations as values
     */
    Map<String, IPathShape> getMapping();

}
