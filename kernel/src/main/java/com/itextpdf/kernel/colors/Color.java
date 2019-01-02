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

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;

import java.io.Serializable;
import java.util.Arrays;

/**
 * Represents a color
 */
public class Color implements Serializable {

    private static final long serialVersionUID = -6639782922289701126L;

    /**
     * The color space of the color
     */
    protected PdfColorSpace colorSpace;

    /**
     * The color value of the color
     */
    protected float[] colorValue;

    /**
     * Creates a Color of certain color space and color value.
     * If color value is set in null, all value components will be initialised with zeroes.
     *
     * @param colorSpace the color space to which the created Color object relates
     * @param colorValue the color value of the created Color object
     */
    protected Color(PdfColorSpace colorSpace, float[] colorValue) {
        this.colorSpace = colorSpace;
        if (colorValue == null)
            this.colorValue = new float[colorSpace.getNumberOfComponents()];
        else
            this.colorValue = colorValue;
    }

    /**
     * Makes a Color of certain color space.
     * All color value components will be initialised with zeroes.
     *
     * @param colorSpace the color space to which the returned Color object relates
     */
    public static Color makeColor(PdfColorSpace colorSpace) {
        return makeColor(colorSpace, null);
    }

    /**
     * Makes a Color of certain color space and color value.
     * If color value is set in null, all value components will be initialised with zeroes.
     *
     * @param colorSpace the color space to which the returned Color object relates
     * @param colorValue the color value of the returned Color object
     */
    public static Color makeColor(PdfColorSpace colorSpace, float[] colorValue) {
        Color c = null;
        boolean unknownColorSpace = false;
        if (colorSpace instanceof PdfDeviceCs) {
            if (colorSpace instanceof PdfDeviceCs.Gray) {
                c = colorValue != null ? new DeviceGray(colorValue[0]) : new DeviceGray();
            } else if (colorSpace instanceof PdfDeviceCs.Rgb) {
                c = colorValue != null ? new DeviceRgb(colorValue[0], colorValue[1], colorValue[2]) : new DeviceRgb();
            } else if (colorSpace instanceof PdfDeviceCs.Cmyk) {
                c = colorValue != null ? new DeviceCmyk(colorValue[0], colorValue[1], colorValue[2], colorValue[3]) : new DeviceCmyk();
            } else {
                unknownColorSpace = true;
            }
        } else if (colorSpace instanceof PdfCieBasedCs) {
            if (colorSpace instanceof PdfCieBasedCs.CalGray) {
                PdfCieBasedCs.CalGray calGray = (PdfCieBasedCs.CalGray) colorSpace;
                c = colorValue != null ? new CalGray(calGray, colorValue[0]) : new CalGray(calGray);
            } else if (colorSpace instanceof PdfCieBasedCs.CalRgb) {
                PdfCieBasedCs.CalRgb calRgb = (PdfCieBasedCs.CalRgb) colorSpace;
                c = colorValue != null ? new CalRgb(calRgb, colorValue) : new CalRgb(calRgb);
            } else if (colorSpace instanceof PdfCieBasedCs.IccBased) {
                PdfCieBasedCs.IccBased iccBased = (PdfCieBasedCs.IccBased) colorSpace;
                c = colorValue != null ? new IccBased(iccBased, colorValue) : new IccBased(iccBased);
            } else if (colorSpace instanceof PdfCieBasedCs.Lab) {
                PdfCieBasedCs.Lab lab = (PdfCieBasedCs.Lab) colorSpace;
                c = colorValue != null ? new Lab(lab, colorValue) : new Lab(lab);
            } else {
                unknownColorSpace = true;
            }
        } else if (colorSpace instanceof PdfSpecialCs) {
            if (colorSpace instanceof PdfSpecialCs.Separation) {
                PdfSpecialCs.Separation separation = (PdfSpecialCs.Separation) colorSpace;
                c = colorValue != null ? new Separation(separation, colorValue[0]) : new Separation(separation);
            } else if (colorSpace instanceof PdfSpecialCs.DeviceN) { //NChannel goes here also
                PdfSpecialCs.DeviceN deviceN = (PdfSpecialCs.DeviceN) colorSpace;
                c = colorValue != null ? new DeviceN(deviceN, colorValue) : new DeviceN(deviceN);
            } else if (colorSpace instanceof PdfSpecialCs.Indexed) {
                c = colorValue != null ? new Indexed(colorSpace, (int) colorValue[0]) : new Indexed(colorSpace);
            } else {
                unknownColorSpace = true;
            }
        } else if (colorSpace instanceof PdfSpecialCs.Pattern) {
            c = new Color(colorSpace, colorValue); // TODO review this. at least log a warning
        } else {
            unknownColorSpace = true;
        }
        if (unknownColorSpace) {
            throw new PdfException("Unknown color space.");
        }
        return c;
    }

    /**
     * Converts {@link DeviceCmyk DeviceCmyk} color to
     * {@link DeviceRgb DeviceRgb} color
     *
     * @param cmykColor the DeviceCmyk color which will be converted to DeviceRgb color
     * @return converted color
     */
    public static DeviceRgb convertCmykToRgb(DeviceCmyk cmykColor) {
        float cyanComp = 1 - cmykColor.getColorValue()[0];
        float magentaComp = 1 - cmykColor.getColorValue()[1];
        float yellowComp = 1 - cmykColor.getColorValue()[2];
        float blackComp = 1 - cmykColor.getColorValue()[3];

        float r = cyanComp * blackComp;
        float g = magentaComp * blackComp;
        float b = yellowComp * blackComp;
        return new DeviceRgb(r, g, b);
    }

    /**
     * Converts {@link DeviceRgb DeviceRgb} color to
     * {@link DeviceCmyk DeviceCmyk} color
     *
     * @param rgbColor the DeviceRgb color which will be converted to DeviceCmyk color
     * @return converted color
     */
    public static DeviceCmyk convertRgbToCmyk(DeviceRgb rgbColor) {
        float redComp = rgbColor.getColorValue()[0];
        float greenComp = rgbColor.getColorValue()[1];
        float blueComp = rgbColor.getColorValue()[2];

        float k = 1 - Math.max(Math.max(redComp, greenComp), blueComp);
        float c = (1 - redComp - k) / (1 - k);
        float m = (1 - greenComp - k) / (1 - k);
        float y = (1 - blueComp - k) / (1 - k);
        return new DeviceCmyk(c, m, y, k);
    }

    /**
     * Returns the number of color value components
     *
     * @return the number of color value components
     */
    public int getNumberOfComponents() {
        return colorValue.length;
    }

    /**
     * Returns the {@link com.itextpdf.kernel.pdf.colorspace.PdfColorSpace color space}
     * to which the color is related.
     *
     * @return the color space of the color
     */
    public PdfColorSpace getColorSpace() {
        return colorSpace;
    }

    /**
     * Returns the color value of the color
     *
     * @return the color value
     */
    public float[] getColorValue() {
        return colorValue;
    }

    /**
     * Sets the color value of the color
     *
     * @param value new color value
     */
    public void setColorValue(float[] value) {
        if (colorValue.length != value.length) {
            throw new PdfException(PdfException.IncorrectNumberOfComponents, this);
        }
        colorValue = value;
    }

    /**
     * Indicates whether the color is equal to the given color.
     * The {@link Color#colorSpace color space} and {@link Color#colorValue color value} are considered during the comparison.
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Color color = (Color) o;
        return (colorSpace != null ? colorSpace.getPdfObject().equals(color.colorSpace.getPdfObject()) : color.colorSpace == null)
                && Arrays.equals(colorValue, color.colorValue);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = colorSpace != null ? colorSpace.hashCode() : 0;
        result = 31 * result + (colorValue != null ? Arrays.hashCode(colorValue) : 0);
        return result;
    }
}
