package com.itextpdf.svg.renderers.path;

import com.itextpdf.svg.renderers.path.impl.PathShapeMapper;

/**
 * A factory for creating {@link IPathShape} objects.
 */
public class DefaultSvgPathShapeFactory {

    public static IPathShape createPathShape(String name){
        return new PathShapeMapper().getMapping().get( name);
    }
}
