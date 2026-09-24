/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class ColorUtilsTest extends ExtendedITextTest {

    private static final float EPSILON = 0.000001f;

    @Test
    public void calculateSvgLuminanceTest() {
        assertSvgLuminance(0f, 0f, 0f, 0f);
        assertSvgLuminance(1f, 1f, 1f, 1f);
        assertSvgLuminance(1f, 0f, 0f, 0.2125f);
        assertSvgLuminance(0f, 1f, 0f, 0.7154f);
        assertSvgLuminance(0f, 0f, 1f, 0.0721f);
        assertSvgLuminance(0.5f, 0.5f, 0.5f, 0.5f);
        assertSvgLuminance(0.2f, 0.4f, 0.6f, 0.37192f);
    }

    @Test
    public void rgbPrimariesAreConvertedUsingSvgLuminanceCoefficientsTest() {
        assertGray(0.2125f, new DeviceRgb(1f, 0f, 0f));
        assertGray(0.7154f, new DeviceRgb(0f, 1f, 0f));
        assertGray(0.0721f, new DeviceRgb(0f, 0f, 1f));
        assertGray(1, new DeviceRgb(1f, 1f, 1f));
    }

    @Test
    public void deviceGrayColorIsPreservedTest() {
        DeviceGray gray = new DeviceGray(0.4f);

        Assertions.assertSame(gray, ColorUtils.toDeviceGrayForSvgLuminanceMode(gray));
    }

    @Test
    public void deviceCmykColorIsPreservedTest() {
        DeviceCmyk cmyk = new DeviceCmyk(0.1f, 0.2f, 0.3f, 0.4f);

        Assertions.assertSame(cmyk, ColorUtils.toDeviceGrayForSvgLuminanceMode(cmyk));
    }

    @Test
    public void nullColorIsPreservedTest() {
        Assertions.assertNull(ColorUtils.toDeviceGrayForSvgLuminanceMode(null));
    }

    private static void assertGray(float expected, DeviceRgb rgb) {
        Color converted = ColorUtils.toDeviceGrayForSvgLuminanceMode(rgb);

        Assertions.assertEquals(1, converted.getNumberOfComponents());
        Assertions.assertEquals(expected, converted.getColorValue()[0], EPSILON);
    }


    private static void assertSvgLuminance(float red, float green, float blue, float expected) {
        float[] rgb = new float[] {red, green, blue};

        Assertions.assertEquals(expected, ColorUtils.calculateSvgLuminance(rgb), EPSILON);
        Assertions.assertArrayEquals(new float[] {red, green, blue}, rgb);
    }
}

