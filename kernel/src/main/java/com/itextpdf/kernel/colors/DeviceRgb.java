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
import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Color space to specify colors according to RGB color model.
 */
public class DeviceRgb extends Color {
    /**
     * Predefined black DeviceRgb color
     */
    public static final Color BLACK = new DeviceRgb(0, 0, 0);

    /**
     * Predefined white DeviceRgb color
     */
    public static final Color WHITE = new DeviceRgb(255, 255, 255);

    /**
     * Predefined red DeviceRgb color
     */
    public static final Color RED = new DeviceRgb(255, 0, 0);

    /**
     * Predefined green DeviceRgb color
     */
    public static final Color GREEN = new DeviceRgb(0, 255, 0);

    /**
     * Predefined blue  DeviceRgb color
     */
    public static final Color BLUE = new DeviceRgb(0, 0, 255);

    private static final long serialVersionUID = 7172400358137528030L;

    /**
     * Creates DeviceRgb color by intensities of red, green and blue colorants.
     * The intensities are considered to be in [0, 255] gap, if not,
     * the intensity will be considered as 255 (when colorant's value is bigger than 255)
     * or 0 (when colorant's value is less than 0).
     *
     * @param r the intensity of red colorant
     * @param g the intensity of green colorant
     * @param b the intensity of blue colorant
     */
    public DeviceRgb(int r, int g, int b) {
        this(r / 255f, g / 255f, b / 255f);
    }

    /**
     * Creates DeviceRgb color by intensities of red, green and blue colorants.
     * The intensities are considered to be in [0, 1] interval, if not,
     * the intensity will be considered as 1 (when colorant's value is bigger than 1)
     * or 0 (when colorant's value is less than 0).
     *
     * @param r the intensity of red colorant
     * @param g the intensity of green colorant
     * @param b the intensity of blue colorant
     */
    public DeviceRgb(float r, float g, float b) {
        super(new PdfDeviceCs.Rgb(), new float[]{
                r > 1 ? 1 : (r > 0 ? r : 0),
                g > 1 ? 1 : (g > 0 ? g : 0),
                b > 1 ? 1 : (b > 0 ? b : 0)
        });
        if (r > 1 || r < 0 || g > 1 || g < 0 || b > 1 || b < 0) {
            Logger LOGGER = LoggerFactory.getLogger(DeviceRgb.class);
            LOGGER.warn(LogMessageConstant.COLORANT_INTENSITIES_INVALID);
        }
    }

    /**
     * Create DeviceRGB color from R, G, B values of java.awt.Color
     * <br/>
     * Note, that alpha chanel is ignored,  but opacity still can be achieved
     * in some places by using 'setOpacity' method or 'TransparentColor' class.
     *
     * @param color the color which RGB values are used
     */
    public DeviceRgb(java.awt.Color color) {
        this(color.getRed(), color.getGreen(), color.getBlue());
        if (color.getAlpha() != 255) {
            Logger LOGGER = LoggerFactory.getLogger(DeviceRgb.class);
            LOGGER.warn(MessageFormatUtil.format(LogMessageConstant.COLOR_ALPHA_CHANNEL_IS_IGNORED, color.getAlpha()));
        }
    }

    /**
     * Creates DeviceRgb color with all colorants intensities initialised as zeroes.
     */
    public DeviceRgb() {
        this(0f, 0f, 0f);
    }

    /**
     * Returns {@link DeviceRgb DeviceRgb} color which is lighter than given one
     * @param rgbColor the DeviceRgb color to be made lighter
     *
     * @return lighter color
     */
    public static DeviceRgb makeLighter(DeviceRgb rgbColor) {
        float r = rgbColor.getColorValue()[0];
        float g = rgbColor.getColorValue()[1];
        float b = rgbColor.getColorValue()[2];

        float v = Math.max(r, Math.max(g, b));

        if (v == 0f) {
            return new DeviceRgb(0x54, 0x54, 0x54);
        }

        float multiplier = Math.min(1f, v + 0.33f) / v;

        r = multiplier * r;
        g = multiplier * g;
        b = multiplier * b;
        return new DeviceRgb(r, g, b);
    }

    /**
     * Returns {@link DeviceRgb DeviceRgb} color which is darker than given one
     * @param rgbColor the DeviceRgb color to be made darker
     *
     * @return darker color
     */
    public static DeviceRgb makeDarker(DeviceRgb rgbColor) {
        float r = rgbColor.getColorValue()[0];
        float g = rgbColor.getColorValue()[1];
        float b = rgbColor.getColorValue()[2];

        float v = Math.max(r, Math.max(g, b));

        float multiplier = Math.max(0f, (v - 0.33f) / v);

        r = multiplier * r;
        g = multiplier * g;
        b = multiplier * b;
        return new DeviceRgb(r, g, b);
    }
}
