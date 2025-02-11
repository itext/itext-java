/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Contains the mapping of the default implementations, provided by this project for the standard SVG
 * tags as defined in the SVG Specification.
 */
class DefaultSvgNodeRendererMapper {

    private static final String CLIP_PATH_LC = SvgConstants.Tags.CLIP_PATH.toLowerCase();
    private static final String LINEAR_GRADIENT_LC = SvgConstants.Tags.LINEAR_GRADIENT.toLowerCase();
    private static final String TEXT_LEAF_LC = SvgConstants.Tags.TEXT_LEAF.toLowerCase();

    /**
     * Creates a new {@link DefaultSvgNodeRendererMapper} instance.
     */
    DefaultSvgNodeRendererMapper() {
    }

    private static final Map<String, ISvgNodeRendererCreator> mapping;
    private static final Collection<String> ignored;

    static {
        Map<String, ISvgNodeRendererCreator> result = new HashMap<>();
        result.put(SvgConstants.Tags.CIRCLE, () -> new CircleSvgNodeRenderer());
        result.put(SvgConstants.Tags.CLIP_PATH, () -> new ClipPathSvgNodeRenderer());
        result.put(SvgConstants.Tags.DEFS, () -> new DefsSvgNodeRenderer());
        result.put(SvgConstants.Tags.ELLIPSE, () -> new EllipseSvgNodeRenderer());
        result.put(SvgConstants.Tags.G, () -> new GroupSvgNodeRenderer());
        result.put(SvgConstants.Tags.IMAGE, () -> new ImageSvgNodeRenderer());
        result.put(SvgConstants.Tags.LINE, () -> new LineSvgNodeRenderer());
        result.put(SvgConstants.Tags.LINEAR_GRADIENT, () -> new LinearGradientSvgNodeRenderer());
        result.put(SvgConstants.Tags.MARKER, () -> new MarkerSvgNodeRenderer());
        result.put(SvgConstants.Tags.PATTERN, () -> new PatternSvgNodeRenderer());
        result.put(SvgConstants.Tags.PATH, () -> new PathSvgNodeRenderer());
        result.put(SvgConstants.Tags.POLYGON, () -> new PolygonSvgNodeRenderer());
        result.put(SvgConstants.Tags.POLYLINE, () -> new PolylineSvgNodeRenderer());
        result.put(SvgConstants.Tags.RECT, () -> new RectangleSvgNodeRenderer());
        result.put(SvgConstants.Tags.STOP, () -> new StopSvgNodeRenderer());
        result.put(SvgConstants.Tags.SVG, () -> new SvgTagSvgNodeRenderer());
        result.put(SvgConstants.Tags.SYMBOL, () -> new SymbolSvgNodeRenderer());
        result.put(SvgConstants.Tags.TEXT, () -> new TextSvgBranchRenderer());
        result.put(SvgConstants.Tags.TSPAN, () -> new TextSvgTSpanBranchRenderer());
        result.put(SvgConstants.Tags.USE, () -> new UseSvgNodeRenderer());
        result.put(SvgConstants.Tags.TEXT_LEAF, () -> new TextLeafSvgNodeRenderer());

        // TODO: DEVSIX-3923 remove normalization (.toLowerCase)
        result.put(CLIP_PATH_LC, () -> new ClipPathSvgNodeRenderer());
        result.put(LINEAR_GRADIENT_LC, () -> new LinearGradientSvgNodeRenderer());
        result.put(TEXT_LEAF_LC, () -> new TextLeafSvgNodeRenderer());

        mapping = Collections.unmodifiableMap(result);

        // Not supported tags as of yet
        Collection<String> ignoredTags = new HashSet<>();

        ignoredTags.add(SvgConstants.Tags.A);
        ignoredTags.add(SvgConstants.Tags.ALT_GLYPH);
        ignoredTags.add(SvgConstants.Tags.ALT_GLYPH_DEF);
        ignoredTags.add(SvgConstants.Tags.ALT_GLYPH_ITEM);

        ignoredTags.add(SvgConstants.Tags.COLOR_PROFILE);

        ignoredTags.add(SvgConstants.Tags.DESC);

        ignoredTags.add(SvgConstants.Tags.FE_BLEND);
        ignoredTags.add(SvgConstants.Tags.FE_COLOR_MATRIX);
        ignoredTags.add(SvgConstants.Tags.FE_COMPONENT_TRANSFER);
        ignoredTags.add(SvgConstants.Tags.FE_COMPOSITE);
        ignoredTags.add(SvgConstants.Tags.FE_COMVOLVE_MATRIX);
        ignoredTags.add(SvgConstants.Tags.FE_DIFFUSE_LIGHTING);
        ignoredTags.add(SvgConstants.Tags.FE_DISPLACEMENT_MAP);
        ignoredTags.add(SvgConstants.Tags.FE_DISTANT_LIGHT);
        ignoredTags.add(SvgConstants.Tags.FE_FLOOD);
        ignoredTags.add(SvgConstants.Tags.FE_FUNC_A);
        ignoredTags.add(SvgConstants.Tags.FE_FUNC_B);
        ignoredTags.add(SvgConstants.Tags.FE_FUNC_G);
        ignoredTags.add(SvgConstants.Tags.FE_FUNC_R);
        ignoredTags.add(SvgConstants.Tags.FE_GAUSSIAN_BLUR);
        ignoredTags.add(SvgConstants.Tags.FE_IMAGE);
        ignoredTags.add(SvgConstants.Tags.FE_MERGE);
        ignoredTags.add(SvgConstants.Tags.FE_MERGE_NODE);
        ignoredTags.add(SvgConstants.Tags.FE_MORPHOLOGY);
        ignoredTags.add(SvgConstants.Tags.FE_OFFSET);
        ignoredTags.add(SvgConstants.Tags.FE_POINT_LIGHT);
        ignoredTags.add(SvgConstants.Tags.FE_SPECULAR_LIGHTING);
        ignoredTags.add(SvgConstants.Tags.FE_SPOTLIGHT);
        ignoredTags.add(SvgConstants.Tags.FE_TILE);
        ignoredTags.add(SvgConstants.Tags.FE_TURBULENCE);
        ignoredTags.add(SvgConstants.Tags.FILTER);
        ignoredTags.add(SvgConstants.Tags.FONT);
        ignoredTags.add(SvgConstants.Tags.FONT_FACE);
        ignoredTags.add(SvgConstants.Tags.FONT_FACE_FORMAT);
        ignoredTags.add(SvgConstants.Tags.FONT_FACE_NAME);
        ignoredTags.add(SvgConstants.Tags.FONT_FACE_SRC);
        ignoredTags.add(SvgConstants.Tags.FONT_FACE_URI);
        ignoredTags.add(SvgConstants.Tags.FOREIGN_OBJECT);

        ignoredTags.add(SvgConstants.Tags.GLYPH);
        ignoredTags.add(SvgConstants.Tags.GLYPH_REF);

        ignoredTags.add(SvgConstants.Tags.HKERN);

        ignoredTags.add(SvgConstants.Tags.MASK);
        ignoredTags.add(SvgConstants.Tags.METADATA);
        ignoredTags.add(SvgConstants.Tags.MISSING_GLYPH);

        ignoredTags.add(SvgConstants.Tags.RADIAL_GRADIENT);

        ignoredTags.add(SvgConstants.Tags.STYLE);

        ignoredTags.add(SvgConstants.Tags.TITLE);

        ignored = Collections.unmodifiableCollection(ignoredTags);
    }

    /**
     * Gets the default SVG tags mapping.
     *
     * @return the default SVG tags mapping
     */
    Map<String, ISvgNodeRendererCreator> getMapping() {
        return mapping;
    }

    /**
     * Gets the default ignored SVG tags.
     * @return default ignored SVG tags
     */
    Collection<String> getIgnoredTags() {
        return ignored;
    }

    /**
     * Represents a function, which creates {@link ISvgNodeRenderer} instance.
     */
    @FunctionalInterface
    public interface ISvgNodeRendererCreator {
        /**
         * Creates an {@link ISvgNodeRenderer} instance.
         * @return {@link ISvgNodeRenderer} instance.
         */
        ISvgNodeRenderer create();
    }
}
