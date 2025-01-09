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
 * Color space to specify shades of gray color.
 */
public class DeviceGray extends Color {

    /**
     * Predefined white DeviceGray color.
     */
    public static final DeviceGray WHITE = new DeviceGray(1f);
    /**
     * Predefined gray DeviceGray color.
     */
    public static final DeviceGray GRAY = new DeviceGray(.5f);
    /**
     * Predefined black DeviceGray color.
     */
    public static final DeviceGray BLACK = new DeviceGray();


    /**
     * Creates DeviceGray color by given grayscale.
     * The grayscale is considered to be in [0, 1] interval, if not,
     * the grayscale will be considered as 1 (when grayscale's value is bigger than 1)
     * or 0 (when grayscale's value is less than 0).
     *
     * @param value the grayscale value
     */
    public DeviceGray(float value) {
        super(new PdfDeviceCs.Gray(), new float[] {value > 1 ? 1 : (value > 0 ? value : 0)});
        if (value > 1 || value < 0) {
            Logger LOGGER = LoggerFactory.getLogger(DeviceGray.class);
            LOGGER.warn(IoLogMessageConstant.COLORANT_INTENSITIES_INVALID);
        }
    }

    /**
     * Creates DeviceGray color with grayscale value initialised as zero.
     */
    public DeviceGray() {
        this(0f);
    }

    /**
     * Returns {@link DeviceGray DeviceGray} color which is lighter than given one
     * @param grayColor the DeviceGray color to be made lighter
     *
     * @return lighter color
     */
    public static DeviceGray makeLighter(DeviceGray grayColor) {
        float v = grayColor.getColorValue()[0];

        if (v == 0f)
            return new DeviceGray(0.3f);

        float multiplier = Math.min(1f, v + 0.33f) / v;

        return new DeviceGray(v * multiplier);
    }

    /**
     * Returns {@link DeviceGray DeviceGray} color which is darker than given one
     * @param grayColor the DeviceGray color to be made darker
     *
     * @return darker color
     */
    public static DeviceGray makeDarker(DeviceGray grayColor) {
        float v = grayColor.getColorValue()[0];
        float multiplier = Math.max(0f, (v - 0.33f) / v);

        return new DeviceGray(v * multiplier);
    }
}
