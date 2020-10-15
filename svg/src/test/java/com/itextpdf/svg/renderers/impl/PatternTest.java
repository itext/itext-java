/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static com.itextpdf.svg.SvgNodeRendererIntegrationTestUtil.convertAndCompare;

@Category(IntegrationTest.class)
public class PatternTest extends ExtendedITextTest {
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/PatternTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/PatternTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void widthHeightXYInCmUnitDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInCmUnitDiffPatternUnits");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void widthHeightXYInInchUnitDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInInchUnitDiffPatternUnits");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void widthHeightXYInEmUnitDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInEmUnitDiffPatternUnits");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void widthHeightXYInExUnitDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInExUnitDiffPatternUnits");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void widthHeightXYInPercentsDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInPercentsDiffPatternUnits");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void widthHeightXYInPxUnitDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInPxUnitDiffPatternUnits");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void widthHeightXYInMmUnitDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInMmUnitDiffPatternUnits");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void widthHeightXYInPtUnitDiffPatternUnitsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYInPtUnitDiffPatternUnits");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void widthHeightXYNoMeasureUnitTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightXYNoMeasureUnit");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void hrefAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hrefAttribute");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void patternUnitsObjectBoundingBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternUnitsObjectBoundingBox");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void patternUnitsUserSpaceOnUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternUnitsUserSpaceOnUse");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void patternContentUnitsUserSpaceOnUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternContentUnitsUserSpaceOnUse");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void patternContentUnitsObjBoundBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternContentUnitsObjBoundBox");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void patternContentUnitsObjBoundBoxAbsoluteCoordTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternContentUnitsObjBoundBoxAbsoluteCoord");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void viewBoxAndAbsoluteCoordinatesTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "viewBoxAndAbsoluteCoordinates");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void patternTransformSimpleTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternTransformSimple");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void patternTransformUnitsObjectBoundingBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternTransformUnitsObjectBoundingBox");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void patternTransformUnitsUserSpaceOnUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternTransformUnitsUserSpaceOnUse");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void preserveAspectRatioXMaxYMidMeetTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "preserveAspectRatioXMaxYMidMeet");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void preserveAspectRatioXMaxYMidSliceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "preserveAspectRatioXMaxYMidSlice");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void preserveAspectRatioXMidYMaxMeetTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "preserveAspectRatioXMidYMaxMeet");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void preserveAspectRatioXMidYMaxSliceTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "preserveAspectRatioXMidYMaxSlice");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void relativeUnitsResolveFromDefsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "relativeUnitsResolveFromDefs");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void relativeUnitsResolveFromPatternTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "relativeUnitsResolveFromPattern");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void linearGradientInsidePatternTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradientInsidePattern");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void nestedPatternsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedPatterns");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void severalComplexElementsInsidePatternTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "severalComplexElementsInsidePattern");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void nestedPatternsWithComplexElementsInsideTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedPatternsWithComplexElementsInside");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void patternUseItselfTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "patternUseItself");
    }

    @Test
    //TODO: DEVSIX-3347 pattern element isn't supported
    public void nestedPatternsLinkedToEachOtherTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "nestedPatternsLinkedToEachOther");
    }
}
