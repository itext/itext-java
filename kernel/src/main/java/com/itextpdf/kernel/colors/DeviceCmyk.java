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

import com.itextpdf.io.logs.IoLogMessageConstant;
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
            LOGGER.warn(IoLogMessageConstant.COLORANT_INTENSITIES_INVALID);
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
