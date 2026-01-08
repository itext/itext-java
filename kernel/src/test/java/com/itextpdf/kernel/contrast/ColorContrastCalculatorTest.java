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
package com.itextpdf.kernel.contrast;

import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

@Tag("UnitTest")
public class ColorContrastCalculatorTest extends ExtendedITextTest {

    public static Object[][] colors() {
        // As per https://webaim.org/resources/contrastchecker/
        return new Object[][] {
                {new DeviceRgb(0, 0, 0), new DeviceRgb(255, 255, 255), 21.0},
                {new DeviceRgb(255, 0, 0), new DeviceRgb(0, 0, 255), 2.15},
                {new DeviceRgb(255, 255, 0), new DeviceRgb(0, 0, 255), 8},
                {new DeviceRgb(128, 128, 128), new DeviceRgb(0, 0, 255), 2.17},
                {new DeviceRgb(128, 128, 128), new DeviceRgb(192, 192, 192), 2.17},
                {new DeviceRgb(0, 128, 0), new DeviceRgb(255, 255, 255), 5.13},
                {new DeviceRgb(255, 165, 0), new DeviceRgb(0, 0, 0), 10.63},
        };
    }

    @ParameterizedTest(name = "Contrast between RGB({0}) and RGB({1}) should be {2}")
    @MethodSource("colors")
    public void calculateContrastShouldBeTheSameAsWebAimContrastTool(DeviceRgb color1, DeviceRgb color2,
            double expectedContrast) {
        double calculatedContrast = ColorContrastCalculator.contrastRatio(color1, color2);
        assertEquals(expectedContrast, calculatedContrast, 0.01);
    }

    @Test
    public void contrastColor1NullShouldThrowException() {
        DeviceRgb color2 = new DeviceRgb(255, 255, 255);
        assertThrows(IllegalArgumentException.class, () -> {
            ColorContrastCalculator.contrastRatio(null, color2);
        });
    }

    @Test
    public void contrastColor2NullShouldThrowException() {
        DeviceRgb color1 = new DeviceRgb(0, 0, 0);
        assertThrows(IllegalArgumentException.class, () -> {
            ColorContrastCalculator.contrastRatio(color1, null);
        });
    }
}