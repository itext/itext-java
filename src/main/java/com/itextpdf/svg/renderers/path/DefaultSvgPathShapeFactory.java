package com.itextpdf.svg.renderers.path;

import com.itextpdf.svg.renderers.path.impl.PatheShapeMapper;

/**
 * A factory for creating {@link IPathShape} objects.
 */
public class DefaultSvgPathShapeFactory {

    public static IPathShape createPathShape(String name){
        return new PatheShapeMapper().getMapping().get( name);
    }
}
