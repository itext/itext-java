/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.condition.DisabledInNativeImage;

@Tag("UnitTest")
public class DeviceRgbTest extends ExtendedITextTest {

    @Test
    public void makeDarkerTest() {
        DeviceRgb rgbColor = new DeviceRgb(50, 100, 150);

        DeviceRgb darkerRgbColor = DeviceRgb.makeDarker(rgbColor);
        // check the resultant darkness of RGB items with using this multiplier
        float multiplier = Math.max(0f, (150f / 255 - 0.33f) / (150f / 255));

        Assertions.assertEquals(multiplier * (50f / 255), darkerRgbColor.getColorValue()[0], 0.0001);
        Assertions.assertEquals(multiplier * (100f / 255), darkerRgbColor.getColorValue()[1], 0.0001);
        Assertions.assertEquals(multiplier * (150f / 255), darkerRgbColor.getColorValue()[2], 0.0001);
    }

    @Test
    public void makeLighterTest() {
        DeviceRgb rgbColor = new DeviceRgb(50, 100, 150);

        DeviceRgb darkerRgbColor = DeviceRgb.makeLighter(rgbColor);
        // check the resultant darkness of RGB items with using this multiplier
        float multiplier = Math.min(1f, 150f / 255 + 0.33f) / (150f / 255);

        Assertions.assertEquals(multiplier * (50f / 255), darkerRgbColor.getColorValue()[0], 0.0001);
        Assertions.assertEquals(multiplier * (100f / 255), darkerRgbColor.getColorValue()[1], 0.0001);
        Assertions.assertEquals(multiplier * (150f / 255), darkerRgbColor.getColorValue()[2], 0.0001);
    }

    // Android-Conversion-Skip-Block-Start (java.awt library isn't available on Android)
    @DisabledInNativeImage // java.awt is not compatible with graalvm
    @Test
    public void colorByAWTColorConstantTest() {
        // RED
        DeviceRgb rgbColor = new DeviceRgb(java.awt.Color.RED);
        float[] rgbColorValue = rgbColor.getColorValue();

        Assertions.assertEquals(1, rgbColorValue[0], 0.0001);
        Assertions.assertEquals(0, rgbColorValue[1], 0.0001);
        Assertions.assertEquals(0, rgbColorValue[2], 0.0001);

        // GREEN
        rgbColor = new DeviceRgb(java.awt.Color.GREEN);
        rgbColorValue = rgbColor.getColorValue();

        Assertions.assertEquals(0, rgbColorValue[0], 0.0001);
        Assertions.assertEquals(1, rgbColorValue[1], 0.0001);
        Assertions.assertEquals(0, rgbColorValue[2], 0.0001);

        // BLUE
        rgbColor = new DeviceRgb(java.awt.Color.BLUE);
        rgbColorValue = rgbColor.getColorValue();

        Assertions.assertEquals(0, rgbColorValue[0], 0.0001);
        Assertions.assertEquals(0, rgbColorValue[1], 0.0001);
        Assertions.assertEquals(1, rgbColorValue[2], 0.0001);
    }

    @DisabledInNativeImage // java.awt is not compatible with graalvm
    @Test
    public void colorByAWTColorTest() {
        java.awt.Color color = new java.awt.Color(50, 100, 150);
        DeviceRgb rgbColor = new DeviceRgb(color);
        float[] rgbColorValue = rgbColor.getColorValue();
        Assertions.assertEquals(50f / 255, rgbColorValue[0], 0.0001);
        Assertions.assertEquals(100f / 255, rgbColorValue[1], 0.0001);
        Assertions.assertEquals(150f / 255, rgbColorValue[2], 0.0001);
    }
    // Android-Conversion-Skip-Block-End

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.COLORANT_INTENSITIES_INVALID, count = 14)
    })
    public void invalidConstructorArgumentsTest() {
        Assertions.assertEquals(0, getSumOfColorValues(new DeviceRgb(-2f, 0f, 0f)), 0.001f);
        Assertions.assertEquals(0, getSumOfColorValues(new DeviceRgb(0f, -2f, 0f)), 0.001f);
        Assertions.assertEquals(0, getSumOfColorValues(new DeviceRgb(0f, 0f, -2f)), 0.001f);

        Assertions.assertEquals(1, getSumOfColorValues(new DeviceRgb(2f, 0f, 0f)), 0.001f);
        Assertions.assertEquals(1, getSumOfColorValues(new DeviceRgb(0f, 2f, 0f)), 0.001f);
        Assertions.assertEquals(1, getSumOfColorValues(new DeviceRgb(0f, 0f, 2f)), 0.001f);

        Assertions.assertEquals(0, getSumOfColorValues(new DeviceRgb(-2f, -2f, 0f)), 0.001f);
        Assertions.assertEquals(0, getSumOfColorValues(new DeviceRgb(-2f, 0f, -2f)), 0.001f);
        Assertions.assertEquals(0, getSumOfColorValues(new DeviceRgb(0f, -2f, -2f)), 0.001f);

        Assertions.assertEquals(2, getSumOfColorValues(new DeviceRgb(2f, 2f, 0f)), 0.001f);
        Assertions.assertEquals(2, getSumOfColorValues(new DeviceRgb(2f, 0f, 2f)), 0.001f);
        Assertions.assertEquals(2, getSumOfColorValues(new DeviceRgb(0f, 2f, 2f)), 0.001f);

        Assertions.assertEquals(0, getSumOfColorValues(new DeviceRgb(-2f, -2f, -2f)), 0.001f);
        Assertions.assertEquals(3, getSumOfColorValues(new DeviceRgb(2f, 2f, 2f)), 0.001f);
    }

    private float getSumOfColorValues(DeviceRgb deviceRgb) {
        float sum = 0;
        float[] values = deviceRgb.getColorValue();
        for (int i = 0; i < values.length; i++) {
            sum += values[i];
        }
        return sum;
    }
}
