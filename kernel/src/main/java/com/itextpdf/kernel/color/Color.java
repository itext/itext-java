/*
    $Id$

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.kernel.color;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;

import java.util.Arrays;

public class Color {

    public static final Color BLACK = new DeviceRgb(0, 0, 0);
    public static final Color BLUE = new DeviceRgb(0, 0, 255);
    public static final Color CYAN = new DeviceRgb(0, 255, 255);
    public static final Color DARK_GRAY = new DeviceRgb(64, 64, 64);
    public static final Color GRAY = new DeviceRgb(128, 128, 128);
    public static final Color GREEN = new DeviceRgb(0, 255, 0);
    public static final Color LIGHT_GRAY = new DeviceRgb(192, 192, 192);
    public static final Color MAGENTA = new DeviceRgb(255, 0, 255);
    public static final Color ORANGE = new DeviceRgb(255, 200, 0);
    public static final Color PINK = new DeviceRgb(255, 175, 175);
    public static final Color RED = new DeviceRgb(255, 0, 0);
    public static final Color WHITE = new DeviceRgb(255, 255, 255);
    public static final Color YELLOW = new DeviceRgb(255, 255, 0);

    protected PdfColorSpace colorSpace;
    protected float[] colorValue;

    public Color(PdfObject pdfObject, float[] colorValue) {
        this(PdfColorSpace.makeColorSpace(pdfObject), colorValue);
    }

    public Color(PdfObject pdfObject) {
        this(pdfObject, null);
    }

    public Color(PdfColorSpace colorSpace, float[] colorValue) {
        this.colorSpace = colorSpace;
        if (colorValue == null)
            this.colorValue = new float[colorSpace.getNumberOfComponents()];
        else
            this.colorValue = colorValue;
    }

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

    public int getNumberOfComponents() {
        return colorValue.length;
    }

    public PdfColorSpace getColorSpace() {
        return colorSpace;
    }

    public float[] getColorValue() {
        return colorValue;
    }

    public void setColorValue(float[] value) {
        colorValue = value;
        if (colorValue.length != value.length)
            throw new PdfException(PdfException.IncorrectNumberOfComponents, this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Color)) {
            return false;
        }
        Color color = (Color) o;
        //noinspection SimplifiableIfStatement
        if (colorSpace != null ? !colorSpace.equals(color.colorSpace) : color.colorSpace != null) {
            return false;
        }
        return Arrays.equals(colorValue, color.colorValue);

    }

    @Override
    public int hashCode() {
        int result = colorSpace != null ? colorSpace.hashCode() : 0;
        result = 31 * result + (colorValue != null ? Arrays.hashCode(colorValue) : 0);
        return result;
    }
}
