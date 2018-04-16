package com.itextpdf.svg;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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
    public static final class Tags{
        public static final String CIRCLE = "circle";
        public static final String DEFS = "defs";
        public static final String ELLIPSE = "ellipse";
        public static final String FOREIGN_OBJECT = "foreignObject";
        public static final String D = "d";
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
        public static final String STYLE = "style";
    }

    /**
     * Class containing the constant property names for the attributes of tags in the SVG spec
     */
    public static final class Attributes{
        //Viewbox, Position & Dimension
        public static final String X = "x";
        public static final String Y = "y";
        public static final String CX = "cx";
        public static final String CY = "cy";
        public static final String R = "r";
        public static final String RX = "rx";
        public static final String RY = "ry";
        public static final String WIDTH = "width";
        public static final String HEIGHT = "height";
        public static final String TRANSFORM = "transform";
        public static final String VIEWBOX = "viewbox";
        public static final String X1="x1";
        public static final String X2="x2";
        public static final String Y1="y1";
        public static final String Y2="y2";
        public static final String POINTS = "points";
        public static final String PRESERVE_ASPECT_RATIO = "preserveaspectratio";

        //Stroke and fill
        public static final String STROKE = "stroke";
        public static final String FILL = "fill";
        public static final String STROKE_WIDTH = "stroke-width";
        public static final String FILL_RULE_EVEN_ODD = "evenodd";
        public static final String FILL_RULE = "fill-rule";

        //Text and font
        public static final String FONT_SIZE = "font-size";
        public static final String ID = "id";
        public static final String TEXT_CONTENT = "text_content";

        //Svg path element commands
        public static final String PATH_DATA_ELIP_ARC="A";
        public static final String PATH_DATA_ELIP_ARC_RELATIVE="a";
        public static final String PATH_DATA_LINE_TO="L";
        public static final String PATH_DATA_LINE_RELATIVE_TO="l";

        public static final String PATH_DATA_MOVE_TO="M";
        public static final String PATH_DATA_MOVE_RELATIVE_TO="m";

        public static final String PATH_DATA_HORIZNTL_TO="H";
        public static final String PATH_DATA_HORIZNTL_RELATIVE_TO="h";

        public static final String PATH_DATA_VERTICL_TO="V";
        public static final String PATH_DATA_VERTICL_RELATIVE_TO="v";

        public static final String PATH_DATA_CLOSE_PATH="z";
        public static final String PATH_DATA_CURVE_TO="C";
        public static final String PATH_DATA_CURVE_RELATIVE_TO="c";

        public static final String PATH_DATA_CURVE_TO_S="S";
        public static final String PATH_DATA_CURVE_TO_RELATIVE_S="s";

        public static final String PATH_DATA_QUARD_CURVE_TO="Q";
        public static final String PATH_DATA_QUARD_CURVE_RELATIVE_TO="q";

        public static final String PATH_DATA_QUARD_CURVE_TO_T="T";
        public static final String PATH_DATA_QUARD_CURVE_TO_RELATIVE_T="t";

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

    }

    /**
     * Class containing the constants for values appearing in SVG tags and attributes
     */
    public static final class Values{
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




}
