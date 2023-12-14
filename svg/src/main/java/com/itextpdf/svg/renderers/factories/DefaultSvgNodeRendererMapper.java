/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.svg.renderers.factories;

import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.CircleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.ClipPathSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.DefsSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.EllipseSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.GroupSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.ImageSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.LineSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.LinearGradientSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.MarkerSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.PathSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.PatternSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.PolygonSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.PolylineSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.RectangleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.StopSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.SvgTagSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.SymbolSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.TextLeafSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.TextSvgBranchRenderer;
import com.itextpdf.svg.renderers.impl.TextSvgTSpanBranchRenderer;
import com.itextpdf.svg.renderers.impl.UseSvgNodeRenderer;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * The implementation of {@link ISvgNodeRendererMapper} that will be used by
 * default in the {@link DefaultSvgNodeRendererFactory}. It contains the mapping
 * of the default implementations, provided by this project for the standard SVG
 * tags as defined in the SVG Specification.
 *
 * @deprecated The public access to this class will be removed in 7.2. The class itself can become
 * either package private or the inner private static class for
 * the {@link DefaultSvgNodeRendererFactory}. Users should override {@link ISvgNodeRendererFactory}
 * (or at least {@link DefaultSvgNodeRendererFactory}) and should not deal with the mapping class
 * as it's more of an implementation detail.
 */
@Deprecated
public class DefaultSvgNodeRendererMapper implements ISvgNodeRendererMapper {

    @Override
    public Map<String, Class<? extends ISvgNodeRenderer>> getMapping() {
        Map<String, Class<? extends ISvgNodeRenderer>> result = new HashMap<>();

        result.put(SvgConstants.Tags.CIRCLE, CircleSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.CLIP_PATH, ClipPathSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.DEFS, DefsSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.ELLIPSE, EllipseSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.G, GroupSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.IMAGE, ImageSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.LINE, LineSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.LINEAR_GRADIENT, LinearGradientSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.MARKER, MarkerSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.PATTERN, PatternSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.PATH, PathSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.POLYGON, PolygonSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.POLYLINE, PolylineSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.RECT, RectangleSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.STOP, StopSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.SVG, SvgTagSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.SYMBOL, SymbolSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.TEXT, TextSvgBranchRenderer.class);
        result.put(SvgConstants.Tags.TSPAN, TextSvgTSpanBranchRenderer.class);
        result.put(SvgConstants.Tags.USE, UseSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.TEXT_LEAF, TextLeafSvgNodeRenderer.class);

        return result;
    }

    @Override
    public Collection<String> getIgnoredTags() {
        Collection<String> ignored = new HashSet<>();

        // Not supported tags as of yet
        ignored.add(SvgConstants.Tags.A);
        ignored.add(SvgConstants.Tags.ALT_GLYPH);
        ignored.add(SvgConstants.Tags.ALT_GLYPH_DEF);
        ignored.add(SvgConstants.Tags.ALT_GLYPH_ITEM);

        ignored.add(SvgConstants.Tags.COLOR_PROFILE);

        ignored.add(SvgConstants.Tags.DESC);

        ignored.add(SvgConstants.Tags.FE_BLEND);
        ignored.add(SvgConstants.Tags.FE_COLOR_MATRIX);
        ignored.add(SvgConstants.Tags.FE_COMPONENT_TRANSFER);
        ignored.add(SvgConstants.Tags.FE_COMPOSITE);
        ignored.add(SvgConstants.Tags.FE_COMVOLVE_MATRIX);
        ignored.add(SvgConstants.Tags.FE_DIFFUSE_LIGHTING);
        ignored.add(SvgConstants.Tags.FE_DISPLACEMENT_MAP);
        ignored.add(SvgConstants.Tags.FE_DISTANT_LIGHT);
        ignored.add(SvgConstants.Tags.FE_FLOOD);
        ignored.add(SvgConstants.Tags.FE_FUNC_A);
        ignored.add(SvgConstants.Tags.FE_FUNC_B);
        ignored.add(SvgConstants.Tags.FE_FUNC_G);
        ignored.add(SvgConstants.Tags.FE_FUNC_R);
        ignored.add(SvgConstants.Tags.FE_GAUSSIAN_BLUR);
        ignored.add(SvgConstants.Tags.FE_IMAGE);
        ignored.add(SvgConstants.Tags.FE_MERGE);
        ignored.add(SvgConstants.Tags.FE_MERGE_NODE);
        ignored.add(SvgConstants.Tags.FE_MORPHOLOGY);
        ignored.add(SvgConstants.Tags.FE_OFFSET);
        ignored.add(SvgConstants.Tags.FE_POINT_LIGHT);
        ignored.add(SvgConstants.Tags.FE_SPECULAR_LIGHTING);
        ignored.add(SvgConstants.Tags.FE_SPOTLIGHT);
        ignored.add(SvgConstants.Tags.FE_TILE);
        ignored.add(SvgConstants.Tags.FE_TURBULENCE);
        ignored.add(SvgConstants.Tags.FILTER);
        ignored.add(SvgConstants.Tags.FONT);
        ignored.add(SvgConstants.Tags.FONT_FACE);
        ignored.add(SvgConstants.Tags.FONT_FACE_FORMAT);
        ignored.add(SvgConstants.Tags.FONT_FACE_NAME);
        ignored.add(SvgConstants.Tags.FONT_FACE_SRC);
        ignored.add(SvgConstants.Tags.FONT_FACE_URI);
        ignored.add(SvgConstants.Tags.FOREIGN_OBJECT);

        ignored.add(SvgConstants.Tags.GLYPH);
        ignored.add(SvgConstants.Tags.GLYPH_REF);

        ignored.add(SvgConstants.Tags.HKERN);

        ignored.add(SvgConstants.Tags.MASK);
        ignored.add(SvgConstants.Tags.METADATA);
        ignored.add(SvgConstants.Tags.MISSING_GLYPH);

        ignored.add(SvgConstants.Tags.RADIAL_GRADIENT);

        ignored.add(SvgConstants.Tags.STYLE);

        ignored.add(SvgConstants.Tags.TITLE);

        return ignored;
    }
}
