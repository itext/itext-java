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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.TestUtil;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class CircleNodeRendererIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/CircleSvgNodeRendererTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/renderers/impl/CircleSvgNodeRendererTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicCircleTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "basicCircle");
    }

    @Test
    public void relativeCircleTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "relativeCircle");
    }

    @Test
    public void circleCxCyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleCxCyAbsent");
    }

    @Test
    public void circleCxAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleCxAbsent");
    }

    @Test
    public void circleCxNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleCxNegative");
    }

    @Test
    public void circleCyAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleCyAbsent");
    }

    @Test
    public void circleCyNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleCyNegative");
    }

    @Test
    public void circleRAbsentTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleRAbsent");
    }

    @Test
    public void circleRNegativeTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleRNegative");
    }

    @Test
    public void circleTranslatedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleTranslated");
    }

    @Test
    public void circleRotatedTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleRotated");
    }

    @Test
    public void circleScaledUpTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleScaledUp");
    }

    @Test
    public void circleScaledDownTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleScaledDown");
    }

    @Test
    public void circleScaledXYTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleScaledXY");
    }

    @Test
    public void circleSkewXTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleSkewX");
    }

    @Test
    public void circleSkewYTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleSkewY");
    }

    @Test
    public void circleWithBigStrokeWidthTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "circleWithBigStrokeWidth");
    }

    @Test
    public void circleShapeRenderingTest() throws IOException, InterruptedException, java.io.IOException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "shape-rendering");
    }
}
