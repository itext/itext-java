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
package com.itextpdf.styledxmlparser.css.media;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class MediaExpressionTest extends ExtendedITextTest {
    @Test
    public void mediaExpressionTestTest01() {
        MediaExpression minWidth = new MediaExpression("min-width", "600px");
        MediaExpression minHeight = new MediaExpression("min-height", "600px");

        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        deviceDescription.setWidth(600);
        deviceDescription.setHeight(600);

        Assertions.assertTrue(minHeight.matches(deviceDescription));
        Assertions.assertTrue(minWidth.matches(deviceDescription));
    }

    @Test
    public void mediaExpressionTestTest02() {
        MediaExpression maxWidth = new MediaExpression("max-width", "600px");
        MediaExpression maxHeight = new MediaExpression("max-height", "600px");

        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        deviceDescription.setWidth(450);
        deviceDescription.setHeight(450);

        Assertions.assertTrue(maxHeight.matches(deviceDescription));
        Assertions.assertTrue(maxWidth.matches(deviceDescription));
    }

    @Test
    public void mediaExpressionTestTest03() {
        MediaExpression orientation = new MediaExpression("orientation", "landscape");
        MediaExpression resolution = new MediaExpression("resolution", "150dpi");
        MediaExpression grid = new MediaExpression("grid", "0");
        MediaExpression colorIndex = new MediaExpression("max-color-index", "15000");
        MediaExpression monochrome = new MediaExpression("monochrome", "0");
        MediaExpression scan = new MediaExpression("scan", "interlace");
        MediaExpression color = new MediaExpression("color", "15000");
        MediaExpression minAspectRatio = new MediaExpression("max-aspect-ratio", "8/5");

        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        deviceDescription.setOrientation("landscape");
        deviceDescription.setResolution(150);
        deviceDescription.setGrid(false);
        deviceDescription.setColorIndex(15000);
        deviceDescription.setMonochrome(0);
        deviceDescription.setScan("interlace");
        deviceDescription.setBitsPerComponent(15000);
        deviceDescription.setWidth(32);
        deviceDescription.setHeight(20);

        Assertions.assertTrue(resolution.matches(deviceDescription));
        Assertions.assertTrue(orientation.matches(deviceDescription));
        Assertions.assertTrue(grid.matches(deviceDescription));
        Assertions.assertTrue(colorIndex.matches(deviceDescription));
        Assertions.assertTrue(monochrome.matches(deviceDescription));
        Assertions.assertTrue(scan.matches(deviceDescription));
        Assertions.assertTrue(color.matches(deviceDescription));
        Assertions.assertTrue(minAspectRatio.matches(deviceDescription));
    }

    @Test
    public void mediaExpressionTestTest04() {
        MediaExpression minColorIndex = new MediaExpression("min-color-index", "15000");
        MediaExpression minResolution = new MediaExpression("min-resolution", "150dpi");
        MediaExpression minColor = new MediaExpression("min-color", "8");
        MediaExpression minAspectRatio = new MediaExpression("min-aspect-ratio", "8/5");

        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        deviceDescription.setColorIndex(15000);
        deviceDescription.setBitsPerComponent(8);
        deviceDescription.setResolution(150);
        deviceDescription.setWidth(32);
        deviceDescription.setHeight(20);

        Assertions.assertTrue(minAspectRatio.matches(deviceDescription));
        Assertions.assertTrue(minColorIndex.matches(deviceDescription));
        Assertions.assertTrue(minColor.matches(deviceDescription));
        Assertions.assertTrue(minResolution.matches(deviceDescription));
    }

    @Test
    public void mediaExpressionTestTest05() {
        MediaExpression maxColorIndex = new MediaExpression("max-color-index", null);
        MediaExpression maxColor = new MediaExpression("max-color", null);
        MediaExpression maxWidth = new MediaExpression("width", "600ex");
        MediaExpression maxHeight = new MediaExpression("height", "600ex");
        MediaExpression maxMonochrome = new MediaExpression("max-monochrome", "0");
        MediaExpression maxResolution = new MediaExpression("max-resolution", "150dpi");

        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        deviceDescription.setHeight(450);
        deviceDescription.setWidth(450);
        deviceDescription.setColorIndex(15000);
        deviceDescription.setBitsPerComponent(8);
        deviceDescription.setMonochrome(0);
        deviceDescription.setResolution(150);

        Assertions.assertTrue(maxMonochrome.matches(deviceDescription));
        Assertions.assertTrue(maxHeight.matches(deviceDescription));
        Assertions.assertTrue(maxWidth.matches(deviceDescription));
        Assertions.assertFalse(maxColorIndex.matches(deviceDescription));
        Assertions.assertFalse(maxColor.matches(deviceDescription));
        Assertions.assertTrue(maxResolution.matches(deviceDescription));
    }

    @Test
    public void mediaExpressionTestTest06() {

        MediaExpression minColorIndex = new MediaExpression("min-color-index", null);
        MediaExpression minColor = new MediaExpression("min-color", null);
        MediaExpression colorIndex = new MediaExpression("color-index", "1500");
        MediaExpression minMonochrome = new MediaExpression("min-monochrome", "0");
        MediaExpression resolution = new MediaExpression("resolution", "150dpi");
        MediaExpression defaultExpression = new MediaExpression("none", "none");
        MediaExpression aspectRatio = new MediaExpression("aspect-ratio", "8/8");

        MediaDeviceDescription deviceDescription = new MediaDeviceDescription("all");
        deviceDescription.setColorIndex(15000);
        deviceDescription.setBitsPerComponent(8);
        deviceDescription.setMonochrome(0);
        deviceDescription.setWidth(1.99999999f);
        deviceDescription.setHeight(2.00000000f);
        deviceDescription.setColorIndex(15000);

        Assertions.assertTrue(aspectRatio.matches(deviceDescription));
        Assertions.assertTrue(minMonochrome.matches(deviceDescription));
        Assertions.assertFalse(minColorIndex.matches(deviceDescription));
        Assertions.assertFalse(minColor.matches(deviceDescription));
        Assertions.assertFalse(resolution.matches(deviceDescription));
        Assertions.assertFalse(defaultExpression.matches(deviceDescription));
        Assertions.assertFalse(colorIndex.matches(deviceDescription));
    }
}


