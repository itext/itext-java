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
package com.itextpdf.svg.css;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

public class AttributesRelativeUnitTest extends SvgIntegrationTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/css/AttributesRelativeUnitTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/css/AttributesRelativeUnitTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void rectangleAttributesEmUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectangleAttributesEmUnits");
    }

    @Test
    public void rectangleAttributesExUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectangleAttributesExUnits");
    }

    @Test
    public void rectangleAttributesPercentUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "rectangleAttributesPercentUnits");
    }

    @Test
    public void imageAttributesEmUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "imageAttributesEmUnits");
    }

    @Test
    public void imageAttributesExUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "imageAttributesExUnits");
    }

    @Test
    public void imageAttributesPercentUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "imageAttributesPercentUnits");
    }

//-------------- Nested svg
    @Test
    public void nestedSvgWidthPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedSvgWidthPercentTest");
    }

    @Test
    public void nestedSvgXPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedSvgXPercentTest");
    }

    @Test
    public void nestedSvgHeightPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedSvgHeightPercentTest");
    }

    @Test
    public void nestedSvgYPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedSvgYPercentTest");
    }

    @Test
    public void nestedSvgWidthEmTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedSvgWidthEmTest");
    }

    @Test
    public void nestedSvgAllPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedSvgAllPercentTest");
    }

//-------------- Top level svg
    @Test
    public void svgWidthPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "svgWidthPercentTest");
    }

    @Test
    public void svgViewboxWidthPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "svgViewboxWidthPercentTest");
    }

    @Test
    public void svgHeightPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "svgHeightPercentTest");
    }

    @Test
    public void svgWidthAndHeightEmAndRemTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "svgWidthAndHeightEmAndRemTest");
    }

    @Test
    public void svgRelativeSizeWithViewBox1() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "svgRelativeSizeWithViewBox1");
    }
//-------------- use
    @Test
    public void useXPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "useXPercentTest");
    }

    @Test
    public void useYPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "useYPercentTest");
    }

    @Test
    public void useXAndYEmAndRemTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "useXAndYEmAndRemTest");
    }

    @Test
    public void useWidthPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "useWidthPercentTest");
    }

//-------------- symbol

    @Test
    public void symbolXPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "symbolXPercentTest");
    }

    @Test
    public void symbolYPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "symbolYPercentTest");
    }

    @Test
    public void symbolXAndYEmAndRemTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "symbolXAndYEmAndRemTest");
    }

    @Test
    public void symbolWidthAndHeightPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "symbolWidthAndHeightPercentTest");
    }

    @Test
    public void symbolWidthAndHeightEmAndRemTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "symbolWidthAndHeightEmAndRemTest");
    }

//-------------- SVGs with absolute or missing size, but with viewBox and or preserveAspectRatio="none"
    @Test
    public void absoluteWidthHeightViewBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "absoluteWidthHeightViewBoxTest");
    }

    @Test
    public void absoluteHeightViewBoxMissWidthTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "absoluteHeightViewBoxMissWidthTest");
    }

    @Test
    public void absoluteWidthNoHeightNoViewBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "absoluteWidthNoHeightNoViewBoxTest");
    }

    @Test
    public void absoluteWidthViewBoxNoHeightTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "absoluteWidthViewBoxNoHeightTest");
    }

    @Test
    public void absWidthViewBoxNoneRatioTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "absWidthViewBoxNoneRatioTest");
    }

//-------------- misc
    @Test
    public void linePercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linePercentTest");
    }

    @Test
    public void diffViewBoxAndPortPercent1Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "diffViewBoxAndPortPercent1Test");
    }

    @Test
    public void diffViewBoxAndPortPercent2Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "diffViewBoxAndPortPercent2Test");
    }

    @Test
    public void noViewBoxAndViewPortPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "noViewBoxAndViewPortPercentTest");
    }

    @Test
    public void noViewBoxPercentTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "noViewBoxPercentTest");
    }

    @Test
    public void viewportFromConverterPropertiesTest() throws IOException, InterruptedException {
        SvgConverterProperties properties = new SvgConverterProperties();
        properties.setCustomViewport(new Rectangle(500, 500));
        // It is expected that the result is different with browser. In
        // browsers the result should be bigger but with the same proportions
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "viewportFromConverterPropertiesTest", properties);
    }

    @Test
    public void viewBoxMissWidthHeightTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "viewBoxMissWidthHeightTest");
    }

    @Test
    public void percentHeightMissWidthViewBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "percentHeightMissWidthViewBoxTest");
    }
}
