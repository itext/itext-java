/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2017 iText Group NV
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
package com.itextpdf.svg;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class containing constants to represent all the SVG-tags.
 */
public final class SvgTagConstants {

    // TODO RND-874 make inner Tag and Attribute classes

    public static final String CSS_STROKE_WIDTH_PROPERTY ="stroke-width" ;

    private SvgTagConstants(){};

    // tags
    public static final String CIRCLE = "circle";
    public static final String DEFS = "defs";
    public static final String ELLIPSE = "ellipse";
    public static final String FOREIGN_OBJECT = "foreignObject";
    public static final String G = "g";
    public static final String IMAGE = "image";
    public static final String LINE = "line";
    public static final String LINEAR_GRADIENT = "linearGradient";
    public static final String PATH = "path";
    public static final String PATTERN = "pattern";
    public static final String POLYLINE = "polyline";
    public static final String POLYGON = "polygon";
    public static final String RADIAL_GRADIENT = "radialGradient";
    public static final String RECT = "rect";
    public static final String SVG = "svg";
    public static final String SYMBOL = "symbol";
    public static final String TEXT = "text";
    public static final String TSPAN = "tspan";
    public static final String TEXTPATH = "textpath";
    public static final String USE = "use";

    // attributes
    public static final String HEIGHT= "height";
    public static final String ID = "id";
    public static final String FILL = "fill";
    public static final String FILL_RULE_EVEN_ODD = "evenodd";
    public static final String FILL_RULE = "fill-rule";
    public static final String PRESERVE_ASPECT_RATIO = "preserveaspectratio";
    public static final String STROKE = "stroke";
    public static final String STROKE_WIDTH = "stroke-width";
    public static final String TRANSFORM = "transform";
    public static final String VIEWBOX = "viewbox";
    public static final String WIDTH = "width";
    public static final String X = "x";
    public static final String X1="x1";
    public static final String X2="x2";
    public static final String Y = "y";
    public static final String Y1="y1";
    public static final String Y2="y2";

    //Animation
    public static final String ANIMATE = "animate";
    public static final String ANIMATE_MOTION = "animateMotion";
    public static final String ANIMATE_TRANSFORM = "animateTransform";
    public static final String DISCARD = "discard";
    public static final String SET = "set";
    public static final Set<String> ANIMATION_ELEMENTS = Collections.unmodifiableSet(new HashSet<>(Arrays.asList(
            ANIMATE,
            ANIMATE_MOTION,
            ANIMATE_TRANSFORM,
            DISCARD,
            SET)));

    //CSS
    public static final String STYLE = "style";

    // values
    public static final String DEFAULT_ASPECT_RATIO = "xmidymid";
    public static final String DEFER = "defer";
    public static final String MEET = "meet";
    public static final String MEET_OR_SLICE_DEFAULT = "meet";
    public static final String NONE = "none";
    public static final String SLICE = "slice";
    public static final String XMIN_YMIN = "xminymin";
    public static final String XMIN_YMID = "xminymid";
    public static final String XMIN_YMAX = "xminymax";
    public static final String XMID_YMIN = "xmidymin";
    public static final String XMID_YMID = "xmidymid";
    public static final String XMID_YMAX = "xmidymax";
    public static final String XMAX_YMIN = "xmaxymin";
    public static final String XMAX_YMID = "xmaxymid";
    public static final String XMAX_YMAX = "xmaxymax";
}
