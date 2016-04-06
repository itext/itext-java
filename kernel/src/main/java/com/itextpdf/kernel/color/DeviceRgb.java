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

import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;

public class DeviceRgb extends Color {

    public DeviceRgb(int r, int g, int b) {
        this(r / 255f, g / 255f, b / 255f);
    }

    public DeviceRgb(float r, float g, float b) {
        super(new PdfDeviceCs.Rgb(), new float[]{r, g, b});
    }

    public DeviceRgb() {
        this(0, 0, 0);
    }

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
