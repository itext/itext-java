/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2018 iText Group NV
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
import com.itextpdf.svg.renderers.impl.CircleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.EllipseSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.LineSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.BranchSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.PathSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.PolygonSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.PolylineSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.RectangleSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.SvgSvgNodeRenderer;
import com.itextpdf.svg.renderers.impl.TextSvgNodeRenderer;

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
        result.put(SvgConstants.Tags.LINE, LineSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.SVG, SvgSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.CIRCLE, CircleSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.RECT, RectangleSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.PATH, PathSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.POLYGON, PolygonSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.POLYLINE, PolylineSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.ELLIPSE, EllipseSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.G, BranchSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.CIRCLE,CircleSvgNodeRenderer.class);
        result.put(SvgConstants.Tags.TEXT, TextSvgNodeRenderer.class);
        return result;
    }

    @Override
    public Collection<String> getIgnoredTags() {
        Collection<String> ignored = new HashSet<>();
        ignored.add(SvgConstants.Tags.STYLE);
        return ignored;
    }

}
