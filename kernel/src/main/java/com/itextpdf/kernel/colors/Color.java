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
package com.itextpdf.kernel.colors;

import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.colorspace.PdfCieBasedCs;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs.Cmyk;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs.Gray;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs.Rgb;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;

import java.util.Arrays;

/**
 * Represents a color
 */
public class Color {


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
        if (colorValue == null) {
            this.colorValue = new float[colorSpace.getNumberOfComponents()];
        } else {
            this.colorValue = colorValue;
        }
    }

    /**
     * Makes a Color of certain color space.
     * All color value components will be initialised with zeroes.
     *
     * @param colorSpace the color space to which the returned Color object relates
     *
     * @return the created Color object.
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
     *
     * @return the created Color object.
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
                c = colorValue != null ? new DeviceCmyk(colorValue[0], colorValue[1], colorValue[2], colorValue[3])
                        : new DeviceCmyk();
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
            } else if (colorSpace instanceof PdfSpecialCs.DeviceN) {
                //NChannel goes here also
                PdfSpecialCs.DeviceN deviceN = (PdfSpecialCs.DeviceN) colorSpace;
                c = colorValue != null ? new DeviceN(deviceN, colorValue) : new DeviceN(deviceN);
            } else if (colorSpace instanceof PdfSpecialCs.Indexed) {
                c = colorValue != null ? new Indexed(colorSpace, (int) colorValue[0]) : new Indexed(colorSpace);
            } else {
                unknownColorSpace = true;
            }
        } else if (colorSpace instanceof PdfSpecialCs.Pattern) {
            c = new Color(colorSpace, colorValue);
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
     *
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
     *
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
     * Creates a color object based on the passed through values.
     * <p>
     *
     * @param colorValue the float array with the values
     *                   <p>
     *                   The number of array elements determines the colour space in which the colour shall be defined:
     *                   0 - No colour; transparent
     *                   1 - DeviceGray
     *                   3 - DeviceRGB
     *                   4 - DeviceCMYK
     *
     * @return Color the color or null if it's invalid
     */
    public static Color createColorWithColorSpace(float[] colorValue) {
        if (colorValue == null || colorValue.length == 0) {
            return null;
        }
        if (colorValue.length == 1) {
            return makeColor(new Gray(), colorValue);
        }
        if (colorValue.length == 3) {
            return makeColor(new Rgb(), colorValue);
        }
        if (colorValue.length == 4) {
            return makeColor(new Cmyk(), colorValue);
        }
        return null;
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
            throw new PdfException(KernelExceptionMessageConstant.INCORRECT_NUMBER_OF_COMPONENTS, this);
        }
        colorValue = value;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        int result = colorSpace == null ? 0 : colorSpace.getPdfObject().hashCode();
        result = 31 * result + (colorValue != null ? Arrays.hashCode(colorValue) : 0);
        return result;
    }

    /**
     * Indicates whether the color is equal to the given color.
     * The {@link Color#colorSpace color space} and {@link Color#colorValue color value} are considered during the
     * comparison.
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
        return (colorSpace != null ? colorSpace.getPdfObject().equals(color.colorSpace.getPdfObject())
                : color.colorSpace == null) && Arrays.equals(colorValue, color.colorValue);
    }
}
