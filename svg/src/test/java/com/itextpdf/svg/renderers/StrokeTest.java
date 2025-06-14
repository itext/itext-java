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
package com.itextpdf.svg.renderers;

import com.itextpdf.test.ITextTest;
import com.itextpdf.test.TestUtil;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class StrokeTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/StrokeTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/renderers/impl/StrokeTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void normalLineStrokeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "normalLineStroke");
    }

    @Test
    public void noLineStrokeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "noLineStroke");
    }

    @Test
    public void noLineStrokeWidthTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "noLineStrokeWidth");
    }

    @Test
    // TODO DEVSIX-8854 Draw SVG elements with transparent stroke in 2 steps
    public void strokeWithDashesTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "strokeWithDashes");
    }

    @Test
    public void strokeWithDashesAcrobatBugTest() throws IOException, InterruptedException {
        // Acrobat displays the result incorrectly, however e.g. Xodo PDF Studio displays the document exactly the same
        // as svg (in terms of stroke opacity and view box). Same issue is reproduced in the
        // DefaultStyleInheritanceIntegrationTest#usePropertiesInheritanceTest and nestedInheritanceTest,
        // ClipPathSvgNodeRendererIntegrationTest#clipPathComplexTest.
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "strokeWithDashesAcrobatBug");
    }

    @Test
    // TODO DEVSIX-8854 Draw SVG elements with transparent stroke in 2 steps
    public void strokeOpacityTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "strokeOpacity");
    }

    @Test
    public void overrideStrokeWidthTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "overrideStrokeWidth");
    }

    @Test
    //TODO: update cmp-file after DEVSIX-2258
    public void advancedStrokeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "strokeAdvanced");
    }

    @Test
    public void strokeWidthMeasureUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "strokeWidthMeasureUnitsTest");
    }

    @Test
    public void pathLengthTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "path-length");
    }

    @Test
    //TODO DEVSIX-2258: update cmp after supporting
    public void strokeAttributesTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "stroke-attributes");
    }

    @Test
    public void zeroStrokeWidthTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "zeroStrokeWidth");
    }

    @Test
    public void negativeStrokeWidthTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "negativeStrokeWidth");
    }

    @Test
    public void heightWidthZeroTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "heightWidthZero");
    }

    @Test
    public void heightWidthNegativeTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "heightWidthNegative");
    }

    @Test
    //TODO: update cmp-file after DEVSIX-2258
    public void strokeDashArrayLinesTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "strokeDashArrayLines");
    }

    //TODO DEVSIX-2507: Update cmp file after supporting
    @Test
    public void strokeTextTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "strokeText");
    }

    //TODO DEVSIX-2507: Update cmp file after supporting
    @Test
    public void strokeTspanTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "strokeTspan");
    }

    @Test
    public void strokeObjectsOverlap1Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "strokeOnGroup");
    }

    @Test
    //TODO DEVSIX-7338: SVG stroke on group applied incorrectly
    public void strokeObjectsOverlap2Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "strokeOnGroup2");
    }

    @Test
    //TODO DEVSIX-7338: SVG stroke on group applied incorrectly
    public void strokeObjectsOverlap3Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "strokeOnGroupNoInsideStroke");
    }

    @Test
    //TODO DEVSIX-7338: SVG stroke on group applied incorrectly
    public void strokeObjectsOverlap4Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "strokeOnGroupNoInsideStroke2");
    }

    @Test
    //TODO DEVSIX-7338: Update cmp file
    public void strokeObjectsOverlap5Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "strokeOnGroupNoInsideStroke3");
    }
}
