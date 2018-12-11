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
package com.itextpdf.svg;

import com.itextpdf.styledxmlparser.CommonAttributeConstants;

/**
 * A class containing constant values signifying the proeprty names of tags, attribute, CSS-style
 * and certain values in SVG XML.
 */
public final class SvgConstants {

    private SvgConstants() {
    }

    /**
     * Class containing the constant property names for the tags in the SVG spec
     */
    public static final class Tags {
        /**
         * Tag defining a Hyperlink.
         */
        public static final String A = "a";

        /**
         * Alternate glyphs to be used instead of regular grlyphs, e.g. ligatures, Asian scripts, ...
         */
        public static final String ALT_GLYPH = "altGlyph";

        /**
         * Defines a set of glyph substitions.
         */
        public static final String ALT_GLYPH_DEF = "altGlyphDef";

        /**
         * Defines a candidate set of glyph substitutions.
         */
        public static final String ALT_GLYPH_ITEM = "altGlyphItem";

        /**
         * Not supported in PDF.
         */
        public static final String ANIMATE = "animate";

        /**
         * Not supported in PDF.
         */
        public static final String ANIMATE_MOTION = "animateMotion";

        /**
         * Not supported in PDF.
         */
        public static final String ANIMATE_COLOR = "animateColor";

        /**
         * Not supported in PDF.
         */
        public static final String ANIMATE_TRANSFORM = "animateTransform";

        /**
         * Tag defining a {@link com.itextpdf.svg.renderers.impl.CircleSvgNodeRenderer circle}.
         *
         * @since 7.1.2
         */
        public static final String CIRCLE = "circle";

        /**
         * Tag defining a clipping path. A clipping path defines the region where can be drawn. Anything outside the path won't be drawn.
         */
        public static final String CLIP_PATH = "clippath";

        /**
         * Tag defining the color profile to be used.
         */
        public static final String COLOR_PROFILE = "color-profile";

        /**
         * Not supported in PDF
         */
        public static final String CURSOR = "cursor";

        /**
         * Tag defining objects that can be reused from another context
         */
        public static final String DEFS = "defs";


        /**
         * Tag defining the description of its parent element
         */
        public static final String DESC = "desc";

        /**
         * Tag defining an {@link com.itextpdf.svg.renderers.impl.EllipseSvgNodeRenderer ellipse}.
         *
         * @since 7.1.2
         */
        public static final String ELLIPSE = "ellipse";

        /**
         * Tag defining how to blend two objects together.
         */
        public static final String FE_BLEND = "feBlend";

        /**
         * Tag defining the color matrix transformations that can be performed.
         */
        public static final String FE_COLOR_MATRIX = "feColorMatrix";

        /**
         * Tag defining color component remapping.
         */
        public static final String FE_COMPONENT_TRANSFER = "feComponentTransfer";

        /**
         * Tag defining the combination of two input images.
         */
        public static final String FE_COMPOSITE = "feComposite";

        /**
         * Tag defining a matrix convolution filter
         */
        public static final String FE_COMVOLVE_MATRIX = "feConvolveMatrix";

        /**
         * Tag defining the lighting map.
         */
        public static final String FE_DIFFUSE_LIGHTING = "feDiffuseLighting";

        /**
         * Tag defining the values to displace an image.
         */
        public static final String FE_DISPLACEMENT_MAP = "feDisplacementMap";

        /**
         * Tag defining a distant light source.
         */
        public static final String FE_DISTANT_LIGHT = "feDistantLight";

        /**
         * Tag defining the fill of a subregion.
         */
        public static final String FE_FLOOD = "feFlood";

        /**
         * Tag defining the transfer function for the Alpha component.
         */
        public static final String FE_FUNC_A = "feFuncA";

        /**
         * Tag defining the transfer function for the Blue component.
         */
        public static final String FE_FUNC_B = "feFuncB";

        /**
         * Tag defining the transfer function for the Green component.
         */
        public static final String FE_FUNC_G = "feFuncG";

        /**
         * Tag defining the transfer function for the Red component.
         */
        public static final String FE_FUNC_R = "feFuncR";

        /**
         * Tag defining the blur values.
         */
        public static final String FE_GAUSSIAN_BLUR = "feGaussianBlur";

        /**
         * Tag defining a image data from a source.
         */
        public static final String FE_IMAGE = "feImage";

        /**
         * Tag defining that filters will be applied concurrently instead of sequentially.
         */
        public static final String FE_MERGE = "feMerge";

        /**
         * Tag defining a node in a merge.
         */
        public static final String FE_MERGE_NODE = "feMergeNode";

        /**
         * Tag defining the erosion or dilation of an image.
         */
        public static final String FE_MORPHOLOGY = "feMorphology";

        /**
         * Tag defining the offset of an image.
         */
        public static final String FE_OFFSET = "feOffset";

        /**
         * Tag defining a point light effect.
         */
        public static final String FE_POINT_LIGHT = "fePointLight";

        /**
         * Tag defining a lighting map.
         */
        public static final String FE_SPECULAR_LIGHTING = "feSpecularLighting";

        /**
         * Tag defining a spotlight.
         */
        public static final String FE_SPOTLIGHT = "feSpotLight";

        /**
         * Tag defining a fill that can be repeated. Similar to PATTERN.
         */
        public static final String FE_TILE = "feTile";

        /**
         * Tag defining values for the perlin turbulence function.
         */
        public static final String FE_TURBULENCE = "feTurbulence";

        /**
         * Tag defining a collection of filter operations.
         */
        public static final String FILTER = "filter";

        /**
         * Tag defining a font.
         */
        public static final String FONT = "font";

        /**
         * Tag defining a font-face.
         */
        public static final String FONT_FACE = "font-face";

        /**
         * Tag defining the formats of the font.
         */
        public static final String FONT_FACE_FORMAT = "font-face-format";

        /**
         * Tag defining the name of the font.
         */
        public static final String FONT_FACE_NAME = "font-face-name";

        /**
         * Tag defining the source file of the font.
         */
        public static final String FONT_FACE_SRC = "font-face-src";

        /**
         * Tag defining the URI of a font.
         */
        public static final String FONT_FACE_URI = "font-face-uri";

        /**
         * Tag definign a foreign XML standard to be inserted. E.g. MathML
         */
        public static final String FOREIGN_OBJECT = "foreignObject";

        /**
         * Tag defining a group of elements.
         */
        public static final String G = "g";

        /**
         * Tag defining a single glyph.
         */
        public static final String GLYPH = "glyph";

        /**
         * Tag defining a sigle glyph for altGlyph.
         */
        public static final String GLYPH_REF = "glyphRef";

        /**
         * Tag defining the horizontal kerning values in between two glyphs.
         */
        public static final String HKERN = "hkern";

        /**
         * Tag defining an image.
         */
        public static final String IMAGE = "image";

        /**
         * Tag defining a {@link com.itextpdf.svg.renderers.impl.LineSvgNodeRenderer line}.
         *
         * @since 7.1.2
         */
        public static final String LINE = "line";

        /**
         * Tag defining a linear gradient
         */
        public static final String LINEAR_GRADIENT = "linearGradient";

        /**
         * Tag defining a link
         */
        public static final String LINK = "link";

        /**
         * Tag defining the graphics (arrowheads or polymarkers) to be drawn at the end of paths, lines, etc.
         */
        public static final String MARKER = "marker";

        /**
         * Tag defining a mask.
         */
        public static final String MASK = "mask";

        /**
         * Tag defining metadata.
         */
        public static final String METADATA = "metadata";

        /**
         * Tag defining content to be rendered if a glyph is missing from the font.
         */
        public static final String MISSING_GLYPH = "missing-glyph";

        /**
         * Not supported in PDF
         */
        public static final String MPATH = "mpath";

        /**
         * Tag defining a {@link com.itextpdf.svg.renderers.impl.PathSvgNodeRenderer path}.
         *
         * @since 7.1.2
         */
        public static final String PATH = "path";

        /**
         * Tag defining a graphical object that can be repeated.
         */
        public static final String PATTERN = "pattern";

        /**
         * Tag defining a {@link com.itextpdf.svg.renderers.impl.PolygonSvgNodeRenderer polygon} shape.
         *
         * @since 7.1.2
         */
        public static final String POLYGON = "polygon";

        /**
         * Tag defining a {@link com.itextpdf.svg.renderers.impl.PolylineSvgNodeRenderer polyline} shape.
         *
         * @since 7.1.2
         */
        public static final String POLYLINE = "polyline";

        /**
         * Tag defining a radial gradient
         */
        public static final String RADIAL_GRADIENT = "radialGradient";

        /**
         * Tag defining a {@link com.itextpdf.svg.renderers.impl.RectangleSvgNodeRenderer rectangle}.
         *
         * @since 7.1.2
         */
        public static final String RECT = "rect";

        /**
         * Not supported in PDF.
         */
        public static final String SCRIPT = "script";

        /**
         * Not supported in PDF.
         */
        public static final String SET = "set";

        /**
         * Tag defining the ramp of colors in a gradient.
         */
        public static final String STOP = "stop";

        /**
         * Tag defining the style to be.
         */
        public static final String STYLE = "style";

        /**
         * Tag defining an {@link com.itextpdf.svg.renderers.impl.SvgTagSvgNodeRenderer SVG} element.
         *
         * @since 7.1.2
         */
        public static final String SVG = "svg";

        /**
         * Tag defining a switch element.
         */
        public static final String SWITCH = "switch";

        /**
         * Tag defining graphical templates that can be reused by the use tag.
         */
        public static final String SYMBOL = "symbol";

        /**
         * Tag defining text to be drawn on a page/screen.
         *
         * @since 7.1.2
         */
        public static final String TEXT = "text";

        /**
         * Tag defining a path on which text can be drawn.
         */
        public static final String TEXT_PATH = "textPath";

        /**
         * Tag defining the description of an element. Is not rendered.
         */
        public static final String TITLE = "title";

        /**
         * Deprecated in SVG. Tag defining text that was defined in an SVG document.
         */
        public static final String TREF = "tref";

        /**
         * Tag defining a span within a text element.
         */
        public static final String TSPAN = "tspan";

        /**
         * Tag defining the use of a named object.
         */
        public static final String USE = "use";

        /**
         * Tag defining how to view the image.
         */
        public static final String VIEW = "view";

        /**
         * Tag defining the vertical kerning values in between two glyphs.
         */
        public static final String VKERN = "vkern";
    }

    /**
     * Class containing the constant property names for the attributes of tags in the SVG spec
     */
    public static final class Attributes extends CommonAttributeConstants {

        /**
         * Attribute defining the clipping path to be applied to a specific shape or group of shapes.
         */
        public static final String CLIP_PATH = "clip-path";

        /**
         * Attribute defining the clipping rule in a clipping path (or element thereof).
         */
        public static final String CLIP_RULE = "clip-rule";

        /**
         * Attribute defining the x value of the center of a circle or ellipse.
         */
        public static final String CX = "cx";

        /**
         * Attribute defining the y value of the center of a circle or ellipse.
         */
        public static final String CY = "cy";

        /**
         * Attribute defining the outline of a shape.
         */
        public static final String D = "d";

        /**
         * Attribute defining the fill color.
         */
        public static final String FILL = "fill";

        /**
         * Attribute defining the fill opacity.
         */
        public static final String FILL_OPACITY = "fill-opacity";

        /**
         * Attribute defining the fill rule.
         */
        public static final String FILL_RULE = "fill-rule";

        /**
         * Attribute defining the font family.
         */
        public static final String FONT_FAMILY = "font-family";

        /**
         * Attribute defining the font weight.
         */
        public static final String FONT_WEIGHT = "font-weight";

        /**
         * Attribute defining the font style.
         */
        public static final String FONT_STYLE = "font-style";

        /**
         * Attribute defining the font size.
         */
        public static final String FONT_SIZE = "font-size";

        /**
         * The Constant ITALIC.
         */
        public static final String ITALIC = "italic";

        /**
         * The Constant BOLD.
         */
        public static final String BOLD = "bold";

        /**
         * Attribute defining the height. Used in several elements.
         */
        public static final String HEIGHT = "height";

        /**
         * Attribute defining the href value.
         */
        public static final String HREF = "href";

        /**
         * Attribute defining the unique id of an element.
         */
        public static final String ID = "id";

        /**
         * Attribute defining the radius of a circle.
         */
        public static final String R = "r";

        /**
         * Attribute defining the x-axis of an ellipse or the x-axis radius of rounded rectangles.
         */
        public static final String RX = "rx";

        /**
         * Attribute defining the y-axis of an ellipse or the y-axis radius of rounded rectangles.
         */
        public static final String RY = "ry";

        /**
         * Close Path Operator.
         */
        public static final String PATH_DATA_CLOSE_PATH = "Z";

        /**
         * CurveTo Path Operator.
         */
        public static final String PATH_DATA_CURVE_TO = "C";

        /**
         * Relative CurveTo Path Operator.
         */
        public static final String PATH_DATA_REL_CURVE_TO = "c";

        /**
         * Attribute defining Elliptical arc path operator.
         */
        public static final String PATH_DATA_ELLIPTICAL_ARC_A = "A";

        /**
         * Attribute defining Elliptical arc path operator.
         */
        public static final String PATH_DATA_REL_ELLIPTICAL_ARC_A = "a";
        /**
         * Smooth CurveTo Path Operator.
         */
        public static final String PATH_DATA_CURVE_TO_S = "S";

        /**
         * Relative Smooth CurveTo Path Operator.
         */
        public static final String PATH_DATA_REL_CURVE_TO_S = "s";
        /**
         * Absolute LineTo Path Operator.
         */
        public static final String PATH_DATA_LINE_TO = "L";

        /**
         * Absolute hrizontal LineTo Path Operator.
         */
        public static final String PATH_DATA_LINE_TO_H = "H";

        /**
         * Relative horizontal LineTo Path Operator.
         */
        public static final String PATH_DATA_REL_LINE_TO_H = "h";

        /**
         * Absolute vertical LineTo Path operator.
         */
        public static final String PATH_DATA_LINE_TO_V = "V";

        /**
         * Relative vertical LineTo Path operator.
         */
        public static final String PATH_DATA_REL_LINE_TO_V = "v";

        /**
         * Relative LineTo Path Operator.
         */
        public static final String PATH_DATA_REL_LINE_TO = "l";

        /**
         * MoveTo Path Operator.
         */
        public static final String PATH_DATA_MOVE_TO = "M";

        /**
         * Relative MoveTo Path Operator.
         */
        public static final String PATH_DATA_REL_MOVE_TO = "m";

        /**
         * Shorthand/smooth quadratic Bézier curveto.
         */
        public static final String PATH_DATA_SHORTHAND_CURVE_TO = "T";

        /**
         * Relative Shorthand/smooth quadratic Bézier curveto.
         */
        public static final String PATH_DATA_REL_SHORTHAND_CURVE_TO = "t";

        /**
         * Catmull-Rom curve command.
         */
        public static final String PATH_DATA_CATMULL_CURVE = "R";

        /**
         * Relative Catmull-Rom curve command.
         */
        public static final String PATH_DATA_REL_CATMULL_CURVE = "r";

        /**
         * Bearing command.
         */
        public static final String PATH_DATA_BEARING = "B";

        /**
         * Relative Bearing command.
         */
        public static final String PATH_DATA_REL_BEARING = "b";

        /**
         * Quadratic CurveTo Path Operator.
         */
        public static final String PATH_DATA_QUAD_CURVE_TO = "Q";

        /**
         * Relative Quadratic CurveTo Path Operator.
         */
        public static final String PATH_DATA_REL_QUAD_CURVE_TO = "q";

        /**
         * Attribute defining the points of a polyline or polygon.
         */
        public static final String POINTS = "points";

        /**
         * Attribute defining how to preserve the aspect ratio when scaling.
         */
        public static final String PRESERVE_ASPECT_RATIO = "preserveaspectratio";

        /**
         * Attribute defining the stroke color.
         */
        public static final String STROKE = "stroke";

        /**
         * Attribute defining the stroke dash offset.
         */
        public static final String STROKE_DASHARRAY = "stroke-dasharray";

        /**
         * Attribute defining the stroke dash offset.
         */
        public static final String STROKE_DASHOFFSET = "stroke-dashoffset";

        /**
         * Attribute defining the stroke linecap.
         */
        public static final String STROKE_LINECAP = "stroke-linecap";

        /**
         * Attribute defining the stroke miterlimit.
         */
        public static final String STROKE_MITERLIMIT = "stroke-miterlimit";

        /**
         * Attribute defingin the stroke opacity.
         */
        public static final String STROKE_OPACITY = "stroke-opacity";

        /**
         * Attribute defining the stroke width.
         */
        public static final String STROKE_WIDTH = "stroke-width";

        /**
         * Attribute defining the style of an element.
         */
        public static final String STYLE = "style";

        /**
         * Attribute defining the text content of a text node.
         */
        public static final String TEXT_CONTENT = "text_content";

        /**
         * Attribute defining a transformation that needs to be applied.
         */
        public static final String TRANSFORM = "transform";

        /**
         * Attribute defining the viewbox of an element.
         */
        public static final String VIEWBOX = "viewbox";

        /**
         * Attribute defining the width of an element.
         */
        public static final String WIDTH = "width";

        /**
         * Attribute defining the x value of an element.
         */
        public static final String X = "x";

        /**
         * Attribute defining the first x coordinate value of a line.
         */
        public static final String X1 = "x1";

        /**
         * Attribute defining the second x coordinate value of a line.
         */
        public static final String X2 = "x2";

        /**
         * Attribute defining image source.
         */
        public static final String XLINK_HREF = "xlink:href";

        /**
         * Attribute defining XML namespace
         */
        public static final String XMLNS = "xmlns";

        /**
         * Attribute defining the y value of an element.
         */
        public static final String Y = "y";

        /**
         * Attribute defining the first y coordinate value of a line.
         */
        public static final String Y1 = "y1";

        /**
         * Attribute defining the second y coordinate value of a line.
         */
        public static final String Y2 = "y2";

        /**
         * Attribute defining version
         */
        public static final String VERSION = "version";
    }

    /**
     * Class containing the constants for values appearing in SVG tags and attributes
     */
    public static final class Values {

        /**
         * Value representing the default value for the stroke linecap.
         */
        public static final String BUTT = "butt";

        /**
         * Value representing the default aspect ratio: xmidymid.
         */
        public static final String DEFAULT_ASPECT_RATIO = "xmidymid";

        /**
         * Value representing how to preserve the aspect ratio when dealing with images.
         */
        public static final String DEFER = "defer";

        /**
         * Value representing the fill rule "even odd".
         */
        public static final String FILL_RULE_EVEN_ODD = "evenodd";

        /**
         * Value representing the fill rule "nonzero".
         */
        public static final String FILL_RULE_NONZERO = "nonzero";

        /**
         * Value representing the "none" value".
         */
        public static final String NONE = "none";

        /**
         * The value corresponding with the namespace url for SVG
         */
        public static final String SVGNAMESPACEURL = "http://www.w3.org/2000/svg";

        /**
         * Value representing how to align when scaling.
         */
        public static final String XMIN_YMIN = "xminymin";

        /**
         * Value representing how to align when scaling.
         */
        public static final String XMIN_YMID = "xminymid";

        /**
         * Value representing how to align when scaling.
         */
        public static final String XMIN_YMAX = "xminymax";

        /**
         * Value representing how to align when scaling.
         */
        public static final String XMID_YMIN = "xmidymin";

        /**
         * Value representing how to align when scaling.
         */
        public static final String XMID_YMAX = "xmidymax";

        /**
         * Value representing how to align when scaling.
         */
        public static final String XMAX_YMIN = "xmaxymin";

        /**
         * Value representing how to align when scaling.
         */
        public static final String XMAX_YMID = "xmaxymid";

        /**
         * Value representing how to align when scaling.
         */
        public static final String XMAX_YMAX = "xmaxymax";

        public static final String VERSION1_1 = "1.1";
    }
}
