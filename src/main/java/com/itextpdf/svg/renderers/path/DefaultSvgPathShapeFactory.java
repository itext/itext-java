package com.itextpdf.svg.renderers.path;

import com.itextpdf.svg.renderers.path.impl.PathShapeMapper;

/**
 * A factory for creating {@link IPathShape} objects.
 */
public class DefaultSvgPathShapeFactory {

    /**
     * Creates a configured {@link IPathShape} object based on the passed Svg path data instruction tag.
     * @param name svg path element's path-data instruction name.
     * @return IPathShape implementation
     */
    public static IPathShape createPathShape(String name){
        return new PathShapeMapper().getMapping().get( name);
    }
}
