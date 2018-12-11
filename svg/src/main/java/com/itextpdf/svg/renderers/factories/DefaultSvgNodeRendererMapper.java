/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.renderers.factories;

import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.ClipPathSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.GroupSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.CircleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.EllipseSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.ImageSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.LineSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.NoDrawOperationSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.PathSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.PolygonSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.PolylineSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.RectangleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.SvgTagSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.TextSvgNodeRenderer;
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
 */
public class DefaultSvgNodeRendererMapper implements ISvgNodeRendererMapper {

    @Override
    public Map<String, Class<? extends ISvgNodeRenderer>> getMapping() {
        Map<String, Class<? extends ISvgNodeRenderer>> result = new HashMap<>();

        result.put(SvgConstants.Tags.CIRCLE, CircleSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.CLIP_PATH, ClipPathSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.DEFS, NoDrawOperationSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.ELLIPSE, EllipseSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.G, GroupSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.IMAGE, ImageSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.LINE, LineSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.PATH, PathSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.POLYGON, PolygonSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.POLYLINE, PolylineSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.RECT, RectangleSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.SVG, SvgTagSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.TEXT, TextSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.USE, UseSvgNodeRenderer.class);

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

        ignored.add(SvgConstants.Tags.LINEAR_GRADIENT);

        ignored.add(SvgConstants.Tags.MARKER);
        ignored.add(SvgConstants.Tags.MASK);
        ignored.add(SvgConstants.Tags.METADATA);
        ignored.add(SvgConstants.Tags.MISSING_GLYPH);

        ignored.add(SvgConstants.Tags.PATTERN);

        ignored.add(SvgConstants.Tags.RADIAL_GRADIENT);

        ignored.add(SvgConstants.Tags.STOP);
        ignored.add(SvgConstants.Tags.STYLE);

        ignored.add(SvgConstants.Tags.TITLE);

        return ignored;
    }
}
