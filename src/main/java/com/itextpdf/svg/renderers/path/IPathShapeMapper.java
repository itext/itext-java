package com.itextpdf.svg.renderers.path;

import java.util.Map;

/**
 * Interface that will provide a mapping from path element-data  instruction names to
 * {@link IPathShape}.
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
