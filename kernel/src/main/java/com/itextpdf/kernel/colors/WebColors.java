/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

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
package com.itextpdf.kernel.colors;

import com.itextpdf.io.LogMessageConstant;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.StringTokenizer;

/**
 * This class is a HashMap that contains the names of colors as a key and the
 * corresponding RGB color as value. (Source: Wikipedia
 * http://en.wikipedia.org/wiki/Web_colors )
 */
public class WebColors extends HashMap<String, int[]> {

    private static final long serialVersionUID = 3542523100813372896L;
    /**
     * HashMap containing all the names and corresponding color values.
     */
    public static final WebColors NAMES = new WebColors();

    private static final double RGB_MAX_VAL = 255.0;
    
    static {
        NAMES.put("aliceblue", new int[]{0xf0, 0xf8, 0xff, 0xff});
        NAMES.put("antiquewhite", new int[]{0xfa, 0xeb, 0xd7, 0xff});
        NAMES.put("aqua", new int[]{0x00, 0xff, 0xff, 0xff});
        NAMES.put("aquamarine", new int[]{0x7f, 0xff, 0xd4, 0xff});
        NAMES.put("azure", new int[]{0xf0, 0xff, 0xff, 0xff});
        NAMES.put("beige", new int[]{0xf5, 0xf5, 0xdc, 0xff});
        NAMES.put("bisque", new int[]{0xff, 0xe4, 0xc4, 0xff});
        NAMES.put("black", new int[]{0x00, 0x00, 0x00, 0xff});
        NAMES.put("blanchedalmond", new int[]{0xff, 0xeb, 0xcd, 0xff});
        NAMES.put("blue", new int[]{0x00, 0x00, 0xff, 0xff});
        NAMES.put("blueviolet", new int[]{0x8a, 0x2b, 0xe2, 0xff});
        NAMES.put("brown", new int[]{0xa5, 0x2a, 0x2a, 0xff});
        NAMES.put("burlywood", new int[]{0xde, 0xb8, 0x87, 0xff});
        NAMES.put("cadetblue", new int[]{0x5f, 0x9e, 0xa0, 0xff});
        NAMES.put("chartreuse", new int[]{0x7f, 0xff, 0x00, 0xff});
        NAMES.put("chocolate", new int[]{0xd2, 0x69, 0x1e, 0xff});
        NAMES.put("coral", new int[]{0xff, 0x7f, 0x50, 0xff});
        NAMES.put("cornflowerblue", new int[]{0x64, 0x95, 0xed, 0xff});
        NAMES.put("cornsilk", new int[]{0xff, 0xf8, 0xdc, 0xff});
        NAMES.put("crimson", new int[]{0xdc, 0x14, 0x3c, 0xff});
        NAMES.put("cyan", new int[]{0x00, 0xff, 0xff, 0xff});
        NAMES.put("darkblue", new int[]{0x00, 0x00, 0x8b, 0xff});
        NAMES.put("darkcyan", new int[]{0x00, 0x8b, 0x8b, 0xff});
        NAMES.put("darkgoldenrod", new int[]{0xb8, 0x86, 0x0b, 0xff});
        NAMES.put("darkgray", new int[]{0xa9, 0xa9, 0xa9, 0xff});
        NAMES.put("darkgrey", new int[]{0xa9, 0xa9, 0xa9, 0xff});
        NAMES.put("darkgreen", new int[]{0x00, 0x64, 0x00, 0xff});
        NAMES.put("darkkhaki", new int[]{0xbd, 0xb7, 0x6b, 0xff});
        NAMES.put("darkmagenta", new int[]{0x8b, 0x00, 0x8b, 0xff});
        NAMES.put("darkolivegreen", new int[]{0x55, 0x6b, 0x2f, 0xff});
        NAMES.put("darkorange", new int[]{0xff, 0x8c, 0x00, 0xff});
        NAMES.put("darkorchid", new int[]{0x99, 0x32, 0xcc, 0xff});
        NAMES.put("darkred", new int[]{0x8b, 0x00, 0x00, 0xff});
        NAMES.put("darksalmon", new int[]{0xe9, 0x96, 0x7a, 0xff});
        NAMES.put("darkseagreen", new int[]{0x8f, 0xbc, 0x8f, 0xff});
        NAMES.put("darkslateblue", new int[]{0x48, 0x3d, 0x8b, 0xff});
        NAMES.put("darkslategray", new int[]{0x2f, 0x4f, 0x4f, 0xff});
        NAMES.put("darkslategrey", new int[]{0x2f, 0x4f, 0x4f, 0xff});
        NAMES.put("darkturquoise", new int[]{0x00, 0xce, 0xd1, 0xff});
        NAMES.put("darkviolet", new int[]{0x94, 0x00, 0xd3, 0xff});
        NAMES.put("deeppink", new int[]{0xff, 0x14, 0x93, 0xff});
        NAMES.put("deepskyblue", new int[]{0x00, 0xbf, 0xff, 0xff});
        NAMES.put("dimgray", new int[]{0x69, 0x69, 0x69, 0xff});
        NAMES.put("dimgrey", new int[]{0x69, 0x69, 0x69, 0xff});
        NAMES.put("dodgerblue", new int[]{0x1e, 0x90, 0xff, 0xff});
        NAMES.put("firebrick", new int[]{0xb2, 0x22, 0x22, 0xff});
        NAMES.put("floralwhite", new int[]{0xff, 0xfa, 0xf0, 0xff});
        NAMES.put("forestgreen", new int[]{0x22, 0x8b, 0x22, 0xff});
        NAMES.put("fuchsia", new int[]{0xff, 0x00, 0xff, 0xff});
        NAMES.put("gainsboro", new int[]{0xdc, 0xdc, 0xdc, 0xff});
        NAMES.put("ghostwhite", new int[]{0xf8, 0xf8, 0xff, 0xff});
        NAMES.put("gold", new int[]{0xff, 0xd7, 0x00, 0xff});
        NAMES.put("goldenrod", new int[]{0xda, 0xa5, 0x20, 0xff});
        NAMES.put("gray", new int[]{0x80, 0x80, 0x80, 0xff});
        NAMES.put("grey", new int[]{0x80, 0x80, 0x80, 0xff});
        NAMES.put("green", new int[]{0x00, 0x80, 0x00, 0xff});
        NAMES.put("greenyellow", new int[]{0xad, 0xff, 0x2f, 0xff});
        NAMES.put("honeydew", new int[]{0xf0, 0xff, 0xf0, 0xff});
        NAMES.put("hotpink", new int[]{0xff, 0x69, 0xb4, 0xff});
        NAMES.put("indianred", new int[]{0xcd, 0x5c, 0x5c, 0xff});
        NAMES.put("indigo", new int[]{0x4b, 0x00, 0x82, 0xff});
        NAMES.put("ivory", new int[]{0xff, 0xff, 0xf0, 0xff});
        NAMES.put("khaki", new int[]{0xf0, 0xe6, 0x8c, 0xff});
        NAMES.put("lavender", new int[]{0xe6, 0xe6, 0xfa, 0xff});
        NAMES.put("lavenderblush", new int[]{0xff, 0xf0, 0xf5, 0xff});
        NAMES.put("lawngreen", new int[]{0x7c, 0xfc, 0x00, 0xff});
        NAMES.put("lemonchiffon", new int[]{0xff, 0xfa, 0xcd, 0xff});
        NAMES.put("lightblue", new int[]{0xad, 0xd8, 0xe6, 0xff});
        NAMES.put("lightcoral", new int[]{0xf0, 0x80, 0x80, 0xff});
        NAMES.put("lightcyan", new int[]{0xe0, 0xff, 0xff, 0xff});
        NAMES.put("lightgoldenrodyellow", new int[]{0xfa, 0xfa, 0xd2, 0xff});
        NAMES.put("lightgreen", new int[]{0x90, 0xee, 0x90, 0xff});
        NAMES.put("lightgray", new int[]{0xd3, 0xd3, 0xd3, 0xff});
        NAMES.put("lightgrey", new int[]{0xd3, 0xd3, 0xd3, 0xff});
        NAMES.put("lightpink", new int[]{0xff, 0xb6, 0xc1, 0xff});
        NAMES.put("lightsalmon", new int[]{0xff, 0xa0, 0x7a, 0xff});
        NAMES.put("lightseagreen", new int[]{0x20, 0xb2, 0xaa, 0xff});
        NAMES.put("lightskyblue", new int[]{0x87, 0xce, 0xfa, 0xff});
        NAMES.put("lightslategray", new int[]{0x77, 0x88, 0x99, 0xff});
        NAMES.put("lightslategrey", new int[]{0x77, 0x88, 0x99, 0xff});
        NAMES.put("lightsteelblue", new int[]{0xb0, 0xc4, 0xde, 0xff});
        NAMES.put("lightyellow", new int[]{0xff, 0xff, 0xe0, 0xff});
        NAMES.put("lime", new int[]{0x00, 0xff, 0x00, 0xff});
        NAMES.put("limegreen", new int[]{0x32, 0xcd, 0x32, 0xff});
        NAMES.put("linen", new int[]{0xfa, 0xf0, 0xe6, 0xff});
        NAMES.put("magenta", new int[]{0xff, 0x00, 0xff, 0xff});
        NAMES.put("maroon", new int[]{0x80, 0x00, 0x00, 0xff});
        NAMES.put("mediumaquamarine", new int[]{0x66, 0xcd, 0xaa, 0xff});
        NAMES.put("mediumblue", new int[]{0x00, 0x00, 0xcd, 0xff});
        NAMES.put("mediumorchid", new int[]{0xba, 0x55, 0xd3, 0xff});
        NAMES.put("mediumpurple", new int[]{0x93, 0x70, 0xdb, 0xff});
        NAMES.put("mediumseagreen", new int[]{0x3c, 0xb3, 0x71, 0xff});
        NAMES.put("mediumslateblue", new int[]{0x7b, 0x68, 0xee, 0xff});
        NAMES.put("mediumspringgreen", new int[]{0x00, 0xfa, 0x9a, 0xff});
        NAMES.put("mediumturquoise", new int[]{0x48, 0xd1, 0xcc, 0xff});
        NAMES.put("mediumvioletred", new int[]{0xc7, 0x15, 0x85, 0xff});
        NAMES.put("midnightblue", new int[]{0x19, 0x19, 0x70, 0xff});
        NAMES.put("mintcream", new int[]{0xf5, 0xff, 0xfa, 0xff});
        NAMES.put("mistyrose", new int[]{0xff, 0xe4, 0xe1, 0xff});
        NAMES.put("moccasin", new int[]{0xff, 0xe4, 0xb5, 0xff});
        NAMES.put("navajowhite", new int[]{0xff, 0xde, 0xad, 0xff});
        NAMES.put("navy", new int[]{0x00, 0x00, 0x80, 0xff});
        NAMES.put("oldlace", new int[]{0xfd, 0xf5, 0xe6, 0xff});
        NAMES.put("olive", new int[]{0x80, 0x80, 0x00, 0xff});
        NAMES.put("olivedrab", new int[]{0x6b, 0x8e, 0x23, 0xff});
        NAMES.put("orange", new int[]{0xff, 0xa5, 0x00, 0xff});
        NAMES.put("orangered", new int[]{0xff, 0x45, 0x00, 0xff});
        NAMES.put("orchid", new int[]{0xda, 0x70, 0xd6, 0xff});
        NAMES.put("palegoldenrod", new int[]{0xee, 0xe8, 0xaa, 0xff});
        NAMES.put("palegreen", new int[]{0x98, 0xfb, 0x98, 0xff});
        NAMES.put("paleturquoise", new int[]{0xaf, 0xee, 0xee, 0xff});
        NAMES.put("palevioletred", new int[]{0xdb, 0x70, 0x93, 0xff});
        NAMES.put("papayawhip", new int[]{0xff, 0xef, 0xd5, 0xff});
        NAMES.put("peachpuff", new int[]{0xff, 0xda, 0xb9, 0xff});
        NAMES.put("peru", new int[]{0xcd, 0x85, 0x3f, 0xff});
        NAMES.put("pink", new int[]{0xff, 0xc0, 0xcb, 0xff});
        NAMES.put("plum", new int[]{0xdd, 0xa0, 0xdd, 0xff});
        NAMES.put("powderblue", new int[]{0xb0, 0xe0, 0xe6, 0xff});
        NAMES.put("purple", new int[]{0x80, 0x00, 0x80, 0xff});
        NAMES.put("red", new int[]{0xff, 0x00, 0x00, 0xff});
        NAMES.put("rosybrown", new int[]{0xbc, 0x8f, 0x8f, 0xff});
        NAMES.put("royalblue", new int[]{0x41, 0x69, 0xe1, 0xff});
        NAMES.put("saddlebrown", new int[]{0x8b, 0x45, 0x13, 0xff});
        NAMES.put("salmon", new int[]{0xfa, 0x80, 0x72, 0xff});
        NAMES.put("sandybrown", new int[]{0xf4, 0xa4, 0x60, 0xff});
        NAMES.put("seagreen", new int[]{0x2e, 0x8b, 0x57, 0xff});
        NAMES.put("seashell", new int[]{0xff, 0xf5, 0xee, 0xff});
        NAMES.put("sienna", new int[]{0xa0, 0x52, 0x2d, 0xff});
        NAMES.put("silver", new int[]{0xc0, 0xc0, 0xc0, 0xff});
        NAMES.put("skyblue", new int[]{0x87, 0xce, 0xeb, 0xff});
        NAMES.put("slateblue", new int[]{0x6a, 0x5a, 0xcd, 0xff});
        NAMES.put("slategray", new int[]{0x70, 0x80, 0x90, 0xff});
        NAMES.put("slategrey", new int[]{0x70, 0x80, 0x90, 0xff});
        NAMES.put("snow", new int[]{0xff, 0xfa, 0xfa, 0xff});
        NAMES.put("springgreen", new int[]{0x00, 0xff, 0x7f, 0xff});
        NAMES.put("steelblue", new int[]{0x46, 0x82, 0xb4, 0xff});
        NAMES.put("tan", new int[]{0xd2, 0xb4, 0x8c, 0xff});
        NAMES.put("teal", new int[]{0x00, 0x80, 0x80, 0xff});
        NAMES.put("thistle", new int[]{0xd8, 0xbf, 0xd8, 0xff});
        NAMES.put("tomato", new int[]{0xff, 0x63, 0x47, 0xff});
        NAMES.put("transparent", new int[]{0xff, 0xff, 0xff, 0x00});
        NAMES.put("turquoise", new int[]{0x40, 0xe0, 0xd0, 0xff});
        NAMES.put("violet", new int[]{0xee, 0x82, 0xee, 0xff});
        NAMES.put("wheat", new int[]{0xf5, 0xde, 0xb3, 0xff});
        NAMES.put("white", new int[]{0xff, 0xff, 0xff, 0xff});
        NAMES.put("whitesmoke", new int[]{0xf5, 0xf5, 0xf5, 0xff});
        NAMES.put("yellow", new int[]{0xff, 0xff, 0x00, 0xff});
        NAMES.put("yellowgreen", new int[]{0x9a, 0xcd, 0x32, 0xff});
    }

    /**
     * Gives you a DeviceRgb based on a name.
     *
     * @param name a name such as black, violet, cornflowerblue or #RGB or
     *             #RRGGBB or RGB or RRGGBB or rgb(R,G,B)
     * @return the corresponding DeviceRgb object. Never returns null.
     */
    public static DeviceRgb getRGBColor(String name) {
        float[] rgbaColor = getRGBAColor(name);
        if (rgbaColor == null) {
            return new DeviceRgb(0, 0, 0);
        } else {
            return new DeviceRgb(rgbaColor[0], rgbaColor[1], rgbaColor[2]);
        }
    }

    /**
     * Gives an array of four floats that contain RGBA values, each value is between 0 and 1. 
     * @param name a name such as black, violet, cornflowerblue or #RGB or
     *             #RRGGBB or RGB or RRGGBB or rgb(R,G,B) or rgb(R,G,B,A)
     * @return the corresponding array of four floats, or <code>null</code> if parsing failed.
     */
    public static float[] getRGBAColor(String name) {
        float[] color = null;
        try {
            String colorName = name.toLowerCase();
            boolean colorStrWithoutHash = missingHashColorFormat(colorName);
            if (colorName.startsWith("#") || colorStrWithoutHash) {
                if (!colorStrWithoutHash) {
                    // lop off the # to unify hex parsing.
                    colorName = colorName.substring(1);
                }
                if (colorName.length() == 3) {
                    String red = colorName.substring(0, 1);
                    color = new float[]{0, 0, 0, 1};
                    color[0] = (float) (Integer.parseInt(red + red, 16) / RGB_MAX_VAL);
                    String green = colorName.substring(1, 2);
                    color[1] = (float) (Integer.parseInt(green + green, 16) / RGB_MAX_VAL);
                    String blue = colorName.substring(2);
                    color[2] = (float) (Integer.parseInt(blue + blue, 16) / RGB_MAX_VAL);
                } else if (colorName.length() == 6) {
                    color = new float[]{0, 0, 0, 1};
                    color[0] = (float) (Integer.parseInt(colorName.substring(0, 2), 16) / RGB_MAX_VAL);
                    color[1] = (float) (Integer.parseInt(colorName.substring(2, 4), 16) / RGB_MAX_VAL);
                    color[2] = (float) (Integer.parseInt(colorName.substring(4), 16) / RGB_MAX_VAL);
                } else {
                    Logger logger = LoggerFactory.getLogger(WebColors.class);
                    logger.error(LogMessageConstant.UNKNOWN_COLOR_FORMAT_MUST_BE_RGB_OR_RRGGBB);
                }
            } else if (colorName.startsWith("rgb(")) {
                final String delim = "rgb(), \t\r\n\f";
                StringTokenizer tok = new StringTokenizer(colorName, delim);
                color = new float[]{0, 0, 0, 1};
                parseRGBColors(color, tok);
            } else if (colorName.startsWith("rgba(")) {
                final String delim = "rgba(), \t\r\n\f";
                StringTokenizer tok = new StringTokenizer(colorName, delim);
                color = new float[]{0, 0, 0, 1};
                parseRGBColors(color, tok);
                if (tok.hasMoreTokens()) {
                    color[3] = getAlphaChannelValue(tok.nextToken());
                }
            } else if (NAMES.containsKey(colorName)) {
                int[] intColor = NAMES.get(colorName);
                color = new float[]{0, 0, 0, 1};
                color[0] = (float) (intColor[0] / RGB_MAX_VAL);
                color[1] = (float) (intColor[1] / RGB_MAX_VAL);
                color[2] = (float) (intColor[2] / RGB_MAX_VAL);
            }
        } catch (Exception exc) {
            // Will just return null in this case
            color = null;
        }

        return color;
    }

    private static void parseRGBColors(float[] color, StringTokenizer tok) {
        for (int k = 0; k < 3; ++k) {
            if (tok.hasMoreTokens()) {
                color[k] = getRGBChannelValue(tok.nextToken());
                color[k] = Math.max(0, color[k]);
                color[k] = Math.min(1f, color[k]);
            }
        }
    }

    /**
     * A web color string without the leading # will be 3 or 6 characters long
     * and all those characters will be hex digits. NOTE: colStr must be all
     * lower case or the current hex letter test will fail.
     *
     * @param colStr A non-null, lower case string that might describe an RGB color
     *               in hex.
     * @return Is this a web color hex string without the leading #?
     */
    private static boolean missingHashColorFormat(String colStr) {
        int len = colStr.length();
        if (len == 3 || len == 6) {
            // and it just contains hex chars 0-9, a-f, A-F
            String match = "[0-9a-f]{" + len + "}";
            return colStr.matches(match);
        }
        return false;
    }

    private static float getRGBChannelValue(String rgbChannel) {
        if (rgbChannel.endsWith("%")) {
            return parsePercentValue(rgbChannel);
        } else {
            return (float) (Integer.parseInt(rgbChannel) / RGB_MAX_VAL);
        }

    }

    private static float getAlphaChannelValue(String rgbChannel) {
        float alpha;
        if (rgbChannel.endsWith("%")) {
            alpha = parsePercentValue(rgbChannel);
        } else {
            alpha = Float.parseFloat(rgbChannel);
        }
        alpha = Math.max(0, alpha);
        alpha = Math.min(1f, alpha);
        return alpha;
    }

    private static float parsePercentValue(String rgbChannel) {
        return (float) (Float.parseFloat(rgbChannel.substring(0,
                rgbChannel.length() - 1)) / 100.0);
    }
}

