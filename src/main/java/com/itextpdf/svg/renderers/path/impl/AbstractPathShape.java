package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.styledxmlparser.css.util.CssUtils;
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
            return CssUtils.parseAbsoluteLength(value);
        }
        return 0;
    }
}
