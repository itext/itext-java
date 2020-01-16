/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.awt.Color;

@Category(UnitTest.class)
public class DeviceRgbTest extends ExtendedITextTest {

    @Test
    public void makeDarkerTest() {
        DeviceRgb rgbColor = new DeviceRgb(50, 100, 150);

        DeviceRgb darkerRgbColor = DeviceRgb.makeDarker(rgbColor);
        // check the resultant darkness of RGB items with using this multiplier
        float multiplier = Math.max(0f, (150f / 255 - 0.33f) / (150f / 255));

        Assert.assertEquals(multiplier * (50f / 255), darkerRgbColor.getColorValue()[0], 0.0001);
        Assert.assertEquals(multiplier * (100f / 255), darkerRgbColor.getColorValue()[1], 0.0001);
        Assert.assertEquals(multiplier * (150f / 255), darkerRgbColor.getColorValue()[2], 0.0001);
    }

    @Test
    public void makeLighterTest() {
        DeviceRgb rgbColor = new DeviceRgb(50, 100, 150);

        DeviceRgb darkerRgbColor = DeviceRgb.makeLighter(rgbColor);
        // check the resultant darkness of RGB items with using this multiplier
        float multiplier = Math.min(1f, 150f / 255 + 0.33f) / (150f / 255);

        Assert.assertEquals(multiplier * (50f / 255), darkerRgbColor.getColorValue()[0], 0.0001);
        Assert.assertEquals(multiplier * (100f / 255), darkerRgbColor.getColorValue()[1], 0.0001);
        Assert.assertEquals(multiplier * (150f / 255), darkerRgbColor.getColorValue()[2], 0.0001);
    }

    @Test
    public void colorByAWTColorConstantTest() {
        // RED
        DeviceRgb rgbColor = new DeviceRgb(Color.RED);
        float[] rgbColorValue = rgbColor.getColorValue();

        Assert.assertEquals(1, rgbColorValue[0], 0.0001);
        Assert.assertEquals(0, rgbColorValue[1], 0.0001);
        Assert.assertEquals(0, rgbColorValue[2], 0.0001);

        // GREEN
        rgbColor = new DeviceRgb(Color.GREEN);
        rgbColorValue = rgbColor.getColorValue();

        Assert.assertEquals(0, rgbColorValue[0], 0.0001);
        Assert.assertEquals(1, rgbColorValue[1], 0.0001);
        Assert.assertEquals(0, rgbColorValue[2], 0.0001);

        // BLUE
        rgbColor = new DeviceRgb(Color.BLUE);
        rgbColorValue = rgbColor.getColorValue();

        Assert.assertEquals(0, rgbColorValue[0], 0.0001);
        Assert.assertEquals(0, rgbColorValue[1], 0.0001);
        Assert.assertEquals(1, rgbColorValue[2], 0.0001);
    }

    @Test
    public void colorByAWTColorTest() {
        Color color = new Color(50, 100, 150);
        DeviceRgb rgbColor = new DeviceRgb(color);
        float[] rgbColorValue = rgbColor.getColorValue();
        Assert.assertEquals(50f / 255, rgbColorValue[0], 0.0001);
        Assert.assertEquals(100f / 255, rgbColorValue[1], 0.0001);
        Assert.assertEquals(150f / 255, rgbColorValue[2], 0.0001);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.COLORANT_INTENSITIES_INVALID, count = 14)
    })
    public void invalidConstructorArgumentsTest() {
        Assert.assertEquals(0, getSumOfColorValues(new DeviceRgb(-2f, 0f, 0f)), 0.001f);
        Assert.assertEquals(0, getSumOfColorValues(new DeviceRgb(0f, -2f, 0f)), 0.001f);
        Assert.assertEquals(0, getSumOfColorValues(new DeviceRgb(0f, 0f, -2f)), 0.001f);

        Assert.assertEquals(1, getSumOfColorValues(new DeviceRgb(2f, 0f, 0f)), 0.001f);
        Assert.assertEquals(1, getSumOfColorValues(new DeviceRgb(0f, 2f, 0f)), 0.001f);
        Assert.assertEquals(1, getSumOfColorValues(new DeviceRgb(0f, 0f, 2f)), 0.001f);

        Assert.assertEquals(0, getSumOfColorValues(new DeviceRgb(-2f, -2f, 0f)), 0.001f);
        Assert.assertEquals(0, getSumOfColorValues(new DeviceRgb(-2f, 0f, -2f)), 0.001f);
        Assert.assertEquals(0, getSumOfColorValues(new DeviceRgb(0f, -2f, -2f)), 0.001f);

        Assert.assertEquals(2, getSumOfColorValues(new DeviceRgb(2f, 2f, 0f)), 0.001f);
        Assert.assertEquals(2, getSumOfColorValues(new DeviceRgb(2f, 0f, 2f)), 0.001f);
        Assert.assertEquals(2, getSumOfColorValues(new DeviceRgb(0f, 2f, 2f)), 0.001f);

        Assert.assertEquals(0, getSumOfColorValues(new DeviceRgb(-2f, -2f, -2f)), 0.001f);
        Assert.assertEquals(3, getSumOfColorValues(new DeviceRgb(2f, 2f, 2f)), 0.001f);
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
