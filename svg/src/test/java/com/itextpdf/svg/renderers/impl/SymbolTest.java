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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class SymbolTest extends SvgIntegrationTest {
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/SymbolTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/SymbolTest/";

    private ISvgConverterProperties properties;

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void before() {
        properties = new SvgConverterProperties().setBaseUri(SOURCE_FOLDER);
    }

    @Test
    public void simpleSymbolTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "simpleSymbolTest");
    }

    @Test
    public void useTagFirstSymbolAfterTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "useTagFirstSymbolAfterTest");
    }

    @Test
    public void heightPxAttrTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "heightPxAttrTest");
    }

    @Test
    public void heightPercentsAttrTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "heightPercentsAttrTest");
    }

    @Test
    public void heightEmTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "heightEmTest");
    }

    @Test
    public void widthPxAttrTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthPxAttrTest");
    }

    @Test
    public void widthPercentsAttrTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthPercentsAttrTest");
    }

    @Test
    public void widthHeightAttrPxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightAttrPxTest");
    }

    @Test
    public void widthHeightAttrPercentsPxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightAttrPercentsPxTest");
    }

    @Test
    // TODO DEVSIX-3537 Processing of preserveAspectRatio attribute with offsets x and y is not currently supported
    public void preserveAspectRatioViewBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "preserveAspectRatioViewBoxTest");
    }

    @Test
    public void xYInUseWithDefsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "xYInUseWithDefsTest");
    }

    @Test
    public void classAttributeTestWithCssTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "classAttrTest");
    }

    @Test
    public void styleAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "styleAttrTest");
    }

    @Test
    public void styleAttrInUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "styleAttrInUseTest");
    }

    @Test
    public void bothStyleAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "bothStyleAttrTest");
    }

    @Test
    // TODO DEVSIX-2258 Processing of stroke attribute is not currently correct supported
    public void opacityAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "opacityAttrTest");
    }

    @Test
    // TODO DEVSIX-2254 Processing of visibility attribute is not currently supported
    public void visibilityAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "visibilityAttrTest");
    }

    @Test
    // TODO DEVSIX-4564 Processing of display attribute is not currently supported
    public void displayNoneAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "displayNoneAttrTest");
    }

    @Test
    // TODO DEVSIX-4564 Processing of display attribute is not currently supported
    public void displayAttributeWithNoUseTagTest() throws IOException, InterruptedException {
        //Expects that nothing will be displayed on the page as it's done in Chrome browser
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "displayAttrWithNoUseTagTest");
    }

    @Test
    public void simpleImageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "simpleImageTest", properties);
    }

    @Test
    public void linearGradientSymbolTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradientSymbolTest", properties);
    }

    @Test
    public void useHeightWidthAllUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "useHeightWidthAllUnitsTest", properties);
    }

    @Test
    public void useSymbolHeightWidthAllUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "useSymbolHeightWidthAllUnitsTest",
                properties);
    }

    @Test
    public void useSymbolXYContrudictionAllUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "useSymbolXYContrudictionAllUnitsTest",
                properties);
    }

    @Test
    public void useSymbolCoordinatesContrudictionTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "useSymbolCoordinatesContrudiction",
                properties);
    }

    @Test
    public void widthHeightAttrInteractionTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightAttrInteraction",
                properties);
    }
}
