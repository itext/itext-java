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
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Color space to specify colors according to CMYK color model.
 */
public class DeviceCmyk extends Color {

    /**
     * Predefined cyan DeviceCmyk color
     */
    public static final DeviceCmyk CYAN = new DeviceCmyk(100, 0, 0, 0);
    /**
     * Predefined magenta DeviceCmyk color
     */
    public static final DeviceCmyk MAGENTA = new DeviceCmyk(0, 100, 0, 0);
    /**
     * Predefined yellow DeviceCmyk color
     */
    public static final DeviceCmyk YELLOW = new DeviceCmyk(0, 0, 100, 0);
    /**
     * Predefined black DeviceCmyk color
     */
    public static final DeviceCmyk BLACK = new DeviceCmyk(0, 0, 0, 100);

    private static final long serialVersionUID = 5466518014595706050L;

    /**
     * Creates DeviceCmyk color with all colorants intensities initialised as zeroes.
     */
    public DeviceCmyk() {
        this(0f, 0f, 0f, 1f);
    }

    /**
     * Creates DeviceCmyk color by intensities of cyan, magenta, yellow and black colorants.
     * The intensities are considered to be in [0, 100] gap, if not,
     * the intensity will be considered as 100 (when colorant's value is bigger than 100)
     * or 0 (when colorant's value is less than 0).
     *
     * @param c the intensity of cyan colorant
     * @param m the intensity of magenta colorant
     * @param y the intensity of yellow colorant
     * @param k the intensity of black colorant
     */
    public DeviceCmyk(int c, int m, int y, int k) {
        this(c / 100f, m / 100f, y / 100f, k / 100f);
    }

    /**
     * Creates DeviceCmyk color by intensities of cyan, magenta, yellow and black colorants.
     * The intensities are considered to be in [0, 1] interval, if not,
     * the intensity will be considered as 1 (when colorant's value is bigger than 1)
     * or 0 (when colorant's value is less than 0).
     *
     * @param c the intensity of cyan colorant
     * @param m the intensity of magenta colorant
     * @param y the intensity of yellow colorant
     * @param k the intensity of black colorant
     */
    public DeviceCmyk(float c, float m, float y, float k) {
        super(new PdfDeviceCs.Cmyk(), new float[]{
                c > 1 ? 1 : (c > 0 ? c : 0),
                m > 1 ? 1 : (m > 0 ? m : 0),
                y > 1 ? 1 : (y > 0 ? y : 0),
                k > 1 ? 1 : (k > 0 ? k : 0)
        });
        if (c > 1 || c < 0 || m > 1 || m < 0 || y > 1 || y < 0 || k > 1 || k < 0) {
            Logger LOGGER = LoggerFactory.getLogger(DeviceCmyk.class);
            LOGGER.warn(LogMessageConstant.COLORANT_INTENSITIES_INVALID);
        }
    }

    /**
     * Returns {@link DeviceCmyk DeviceCmyk} color which is lighter than given one
     * @param cmykColor the DeviceCmyk color to be made lighter
     *
     * @return lighter color
     */
    public static DeviceCmyk makeLighter(DeviceCmyk cmykColor) {
        DeviceRgb rgbEquivalent = convertCmykToRgb(cmykColor);
        DeviceRgb lighterRgb = DeviceRgb.makeLighter((rgbEquivalent));
        return convertRgbToCmyk(lighterRgb);
    }

    /**
     * Returns {@link DeviceCmyk DeviceCmyk} color which is darker than given one
     * @param cmykColor the DeviceCmyk color to be made darker
     *
     * @return darker color
     */
    public static DeviceCmyk makeDarker(DeviceCmyk cmykColor) {
        DeviceRgb rgbEquivalent = convertCmykToRgb(cmykColor);
        DeviceRgb darkerRgb = DeviceRgb.makeDarker(rgbEquivalent);
        return convertRgbToCmyk(darkerRgb);
    }
}
