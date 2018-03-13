package com.itextpdf.svg.renderers.factories;

import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.CircleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.EllipseSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.PathSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.RectangleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.SvgSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.LineSvgNodeRenderer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * The implementation of {@link ISvgNodeRendererMapper} that will be used by
 * default in the {@link DefaultSvgNodeRendererFactory}. It contains the mapping
 * of the default implementations, provided by this project for the standard SVG
 * tags as defined in the SVG Specification.
 */
public class DefaultSvgNodeRendererMapper implements ISvgNodeRendererMapper {

    @Override
    public Map<String, Class<? extends ISvgNodeRenderer>> getMapping() {
        Map<String, Class<? extends ISvgNodeRenderer>> result = new HashMap<>();
        result.put(SvgTagConstants.LINE, LineSvgNodeRenderer.class);
        result.put(SvgTagConstants.SVG, SvgSvgNodeRenderer.class);
        result.put(SvgTagConstants.CIRCLE, CircleSvgNodeRenderer.class);
        result.put(SvgTagConstants.RECT, RectangleSvgNodeRenderer.class);
        result.put(SvgTagConstants.PATH, PathSvgNodeRenderer.class);
        result.put(SvgTagConstants.ELLIPSE, EllipseSvgNodeRenderer.class);
        return result;
    }

    @Override
    public Collection<String> getIgnoredTags() {
        Collection<String> ignored = new HashSet<>();
        ignored.add(SvgTagConstants.STYLE);
        return ignored;
    }

}
