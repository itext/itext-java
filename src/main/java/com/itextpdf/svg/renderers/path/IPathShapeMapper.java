package com.itextpdf.svg.renderers.path;

import java.util.Map;

/**
 * Provides a mapping of Path-data instructions' names to path shape classes.
 *
 * @return a {@link Map} with Strings as keys and {link @{@link IPathShape}
 * implementations as values
 */
public interface IPathShapeMapper {


    Map<String, IPathShape> getMapping();

}
