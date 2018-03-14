package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.svg.renderers.path.IPathShape;

import java.util.Map;
/****
 * This class handles common behaviour in IPathShape implementations
 */

public abstract class AbstractPathShape implements IPathShape{

    public float getCoordinate(Map<String, String> attributes, String key) {
        String value = "";
        if (attributes != null) {
            value = attributes.get( key );
        }
        if (value != null && !value.isEmpty()) {
            return Float.valueOf( attributes.get( key ) );
        }
        return 0;
    }
}
