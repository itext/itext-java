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

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class WebColorsTest extends ExtendedITextTest {

    private static final double RGB_MAX_VAL = 255.0;


    @Test
    public void getRGBColorBySupportedNameTest() {
        String colorName = "violet";
        DeviceRgb cmpRgb = new DeviceRgb(0xee, 0x82, 0xee);

        DeviceRgb resultRgb = WebColors.getRGBColor(colorName);

        Assertions.assertEquals(cmpRgb, resultRgb);
    }

    @Test
    public void getRGBColorByUnsupportedNameTest() {
        String colorName = "tangerine";
        DeviceRgb cmpRgb = new DeviceRgb(0,0,0);

        DeviceRgb resultRgb = WebColors.getRGBColor(colorName);

        Assertions.assertEquals(cmpRgb, resultRgb);
    }

    @Test
    public void getRGBAColorByNameTest() {
        String colorName = "violet";
        float[] cmpRgba = new float[]{(float)(238/RGB_MAX_VAL), (float)(130/RGB_MAX_VAL), (float)(238/RGB_MAX_VAL), (float)(1.0)};
        float delta = (float)(0.0001);

        float[] resultRgba = WebColors.getRGBAColor(colorName);

        Assertions.assertArrayEquals(cmpRgba, resultRgba, delta);
    }

    @Test
    public void getRGBAColorByCodeWithHashTest() {
        //corresponding color name = "violet"
        String hashHex = "#EE82EE";
        float[] cmpRgba = new float[]{(float)(238/RGB_MAX_VAL), (float)(130/RGB_MAX_VAL), (float)(238/RGB_MAX_VAL), (float)(1.0)};
        float delta = (float)(0.0001);

        float[] resultRgba = WebColors.getRGBAColor(hashHex);

        Assertions.assertArrayEquals(cmpRgba, resultRgba, delta);

    }
    @Test
    public void getRGBAColorByCode6DigitsTest() {
        //corresponding color name = "violet"
        String hexString = "EE82EE";
        float[] cmpRgba = new float[]{(float)(238/RGB_MAX_VAL), (float)(130/RGB_MAX_VAL), (float)(238/RGB_MAX_VAL), (float)(1.0)};
        float delta = (float)(0.0001);

        float[] resultRgba = WebColors.getRGBAColor(hexString);

        Assertions.assertArrayEquals(cmpRgba, resultRgba, delta);
    }

    @Test
    public void getRGBAColorByCode3DigitsTest() {
        //corresponding full hex = #990000, rgb(153,0,0)
        String hexString = "900";
        float[] cmpRgba = new float[]{(float)(153/RGB_MAX_VAL), (float)(0.0), (float)(0.0), (float)(1.0)};
        float delta = (float)(0.0001);

        float[] resultRgba = WebColors.getRGBAColor(hexString);

        Assertions.assertArrayEquals(cmpRgba, resultRgba, delta);
    }

    @Test
    public void getRGBAColorByCodeWrongDigitsNumberTest() {
        String hexString = "9000";

        float[] resultRgba = WebColors.getRGBAColor(hexString);

        Assertions.assertNull(resultRgba);
    }

    @Test
    public void getRGBAColorByRgbObjectTest() {
        //corresponding color name = "violet"
        String rgbString = "rgb(238,130,238)";
        float[] cmpRgba = new float[]{(float)(238/RGB_MAX_VAL), (float)(130/RGB_MAX_VAL), (float)(238/RGB_MAX_VAL), (float)(1.0)};
        float delta = (float)(0.0001);

        float[] resultRgba = WebColors.getRGBAColor(rgbString);

        Assertions.assertArrayEquals(cmpRgba, resultRgba, delta);
    }


    @Test
    public void getRGBAColorByRgbaObjectTest() {
        //corresponding color name = "violet"
        String rgbaString = "rgba(238,130,238,255)";
        float[] cmpRgba = new float[]{(float)(238/RGB_MAX_VAL), (float)(130/RGB_MAX_VAL), (float)(238/RGB_MAX_VAL), (float)(1.0)};
        float delta = (float)(0.0001);

        float[] resultRgba = WebColors.getRGBAColor(rgbaString);

        Assertions.assertArrayEquals(cmpRgba, resultRgba, delta);
    }

    @Test
    public void getCMYKColorByDeviceCmykTest() {
        //corresponding color name = "violet"
        String cmykString = "device-cmyk(44% 100% 0% 0%)";
        float[] cmpCmyk = new float[]{0.44f, 1f, 0f, 0f, 1f};
        float delta = (float)(0.0001);

        float[] resultCmyk = WebColors.getCMYKArray(cmykString);

        Assertions.assertArrayEquals(cmpCmyk, resultCmyk, delta);
    }

    @Test
    public void getCMYKColorByDeviceCmykWithOpacityTest() {
        //corresponding color name = "violet"
        String cmykString = "device-cmyk(44% 100% 0% 0% / .8)";
        float[] cmpCmyk = new float[]{0.44f, 1f, 0f, 0f, 0.8f};
        float delta = (float)(0.0001);

        float[] resultCmyk = WebColors.getCMYKArray(cmykString);

        Assertions.assertArrayEquals(cmpCmyk, resultCmyk, delta);
    }


    @Test
    public void getCMYKColorByDeviceCmykWithOpacityAndFallbackTest() {
        //corresponding color name = "violet"
        String cmykString = "device-cmyk(44% 100% 0% 0% / .8 rgb(238,130,238))";
        float[] cmpCmyk = new float[]{0.44f, 1f, 0f, 0f, 0.8f};
        float delta = (float)(0.0001);

        float[] resultCmyk = WebColors.getCMYKArray(cmykString);

        Assertions.assertArrayEquals(cmpCmyk, resultCmyk, delta);
    }

    @Test
    public void getCMYKColorWithNoBlack() {
        String cmykString = "device-cmyk(44% 100% 0%))";
        float[] cmpCmyk = new float[]{0.44f, 1f, 0f, 1f, 1f};
        float delta = (float)(0.0001);

        float[] resultCmyk = WebColors.getCMYKArray(cmykString);

        Assertions.assertArrayEquals(cmpCmyk, resultCmyk, delta);
    }

    @Test
    public void getCMYKColorWithInvalidDeviceCmykDefinition() {
        String cmykString = "cmyk(44% 100% 0% 1%))";

        float[] resultCmyk = WebColors.getCMYKArray(cmykString);

        Assertions.assertNull(resultCmyk);
    }

    @Test
    public void getCMYKColorWithExceptionDuringParsing() {
        float[] resultCmyk = WebColors.getCMYKArray(null);
        Assertions.assertNull(resultCmyk);
    }


    @Test
    public void getRGBAColorWithExceptionDuringParsing() {
        float[] resultCmyk = WebColors.getRGBAColor(null);
        Assertions.assertNull(resultCmyk);
    }



    @Test
    public void getCMYKColorTest() {
        //corresponding color name = "violet"
        String cmykString = "device-cmyk(44% 100% 0% 0%)";
        float[] cmpCmyk = new float[]{0.44f, 1f, 0f, 0f};
        float delta = (float)(0.0001);

        DeviceCmyk resultCmyk = WebColors.getCMYKColor(cmykString);

        Assertions.assertArrayEquals(cmpCmyk, resultCmyk.colorValue, delta);
    }

    @Test
    public void getCMYKColorFloatTest() {
        String cmykString = "device-cmyk(0.44 1 0.2 0.2)";
        float[] cmpCmyk = new float[]{0.44f, 1f, 0.2f, 0.2f};
        float delta = (float)(0.0001);

        DeviceCmyk resultCmyk = WebColors.getCMYKColor(cmykString);

        Assertions.assertArrayEquals(cmpCmyk, resultCmyk.colorValue, delta);
    }

    @Test
    public void getCMYKColorNullTest() {
        String cmykString = "null";
        float[] cmpCmyk = new float[]{0f, 0f, 0f, 1f};
        float delta = (float)(0.0001);

        DeviceCmyk resultCmyk = WebColors.getCMYKColor(cmykString);

        Assertions.assertArrayEquals(cmpCmyk, resultCmyk.colorValue, delta);
    }
}
