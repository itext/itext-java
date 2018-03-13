package com.itextpdf.svg.renderers.factories;

import com.itextpdf.svg.SvgTagConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.CircleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.EllipseSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.PathSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.RectangleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.SvgSvgNodeRenderer;
import org.apache.batik.util.SVGConstants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
        result.put(SvgTagConstants.SVG, SvgSvgNodeRenderer.class);
        result.put(SvgTagConstants.CIRCLE, CircleSvgNodeRenderer.class);
        result.put(SvgTagConstants.RECT, RectangleSvgNodeRenderer.class);
        result.put(SvgTagConstants.PATH, PathSvgNodeRenderer.class);
        result.put(SvgTagConstants.ELLIPSE, EllipseSvgNodeRenderer.class);
        return result;
    }

    @Override
    public List<String> getIgnoredTags() {
        List<String> ignored = new ArrayList<>();
        ignored.add(SvgTagConstants.STYLE);
        return ignored;
    }

}
