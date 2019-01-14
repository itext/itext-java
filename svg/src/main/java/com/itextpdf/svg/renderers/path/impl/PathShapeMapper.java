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
package com.itextpdf.svg.renderers.path.impl;

import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.renderers.impl.PathSvgNodeRenderer;
import com.itextpdf.svg.renderers.path.IPathShape;
import com.itextpdf.svg.renderers.path.IPathShapeMapper;

import java.util.HashMap;
import java.util.Map;

/**
 * The implementation of {@link IPathShapeMapper} that will be used by
 * sub classes of {@link PathSvgNodeRenderer} To map the path-data
 * instructions(moveto, lineto, corveto ...) to their respective implementations.
 */
public class PathShapeMapper implements IPathShapeMapper {
    @Override
    public Map<String, IPathShape> getMapping() {
        Map<String, IPathShape> result = new HashMap<>();
        result.put(SvgConstants.Attributes.PATH_DATA_LINE_TO, new LineTo());
        result.put(SvgConstants.Attributes.PATH_DATA_REL_LINE_TO, new LineTo(true));
        result.put(SvgConstants.Attributes.PATH_DATA_LINE_TO_V, new VerticalLineTo());
        result.put(SvgConstants.Attributes.PATH_DATA_REL_LINE_TO_V, new VerticalLineTo(true));
        result.put(SvgConstants.Attributes.PATH_DATA_LINE_TO_H, new HorizontalLineTo());
        result.put(SvgConstants.Attributes.PATH_DATA_REL_LINE_TO_H, new HorizontalLineTo(true));
        result.put(SvgConstants.Attributes.PATH_DATA_CLOSE_PATH, new ClosePath());
        result.put(SvgConstants.Attributes.PATH_DATA_CLOSE_PATH.toLowerCase(), new ClosePath());
        result.put(SvgConstants.Attributes.PATH_DATA_MOVE_TO, new MoveTo());
        result.put(SvgConstants.Attributes.PATH_DATA_REL_MOVE_TO, new MoveTo(true));
        result.put(SvgConstants.Attributes.PATH_DATA_CURVE_TO, new CurveTo());
        result.put(SvgConstants.Attributes.PATH_DATA_REL_CURVE_TO, new CurveTo(true));
        result.put(SvgConstants.Attributes.PATH_DATA_QUAD_CURVE_TO, new QuadraticCurveTo());
        result.put(SvgConstants.Attributes.PATH_DATA_CURVE_TO_S, new SmoothSCurveTo());
        return result;
    }
}
