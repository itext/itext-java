/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class TextSvgBranchRendererIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/TextSvgBranchRendererIntegrationTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/TextSvgBranchRendererIntegrationTest/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void helloWorldTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world");
    }

    @Test
    public void tooLongTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "too_long");
    }

    @Test
    public void twoLinesTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "two_lines");
    }

    @Test
    public void twoLinesNewlineTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "two_lines_newline");
    }

    @Test
    public void helloWorldScaleUpXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_scaleUpX");
    }

    @Test
    public void helloWorldScaleUpYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_scaleUpY");
    }

    @Test
    public void helloWorldScaleDownXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_scaleDownX");
    }

    @Test
    public void helloWorldScaleDownYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_scaleDownY");
    }

    @Test
    public void helloWorldTranslateTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_translate");
    }

    @Test
    public void helloWorldRotateTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_rotate");
    }

    @Test
    public void helloWorldSkewXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_skewX");
    }

    @Test
    public void helloWorldSkewYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_skewY");
    }

    @Test
    public void helloWorldCombinedTransformationsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_combinedTransformations");
    }

    @Test
    public void helloWorldFontSizeMissingTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "hello_world_fontSizeMissing");
    }

    //Absolute position
    //X
    @Test
    public void textAbsolutePositionpositiveXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-positiveX");
    }

    @Test
    public void textAbsolutePositionnegativeXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-negativeX");
    }

    @Test
    public void textAbsolutePositionzeroXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-zeroX");
    }

    @Test
    public void textAbsolutePositionInvalidXTest() throws IOException, InterruptedException {
        Assert.assertThrows(StyledXMLParserException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-invalidX")
        );
    }

    //Y
    @Test
    public void textAbsolutePositionPositiveYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-positiveY");
    }

    @Test
    public void textAbsolutePositionNegativeYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-negativeY");
    }

    @Test
    public void textAbsolutePositionZeroYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-zeroY");
    }

    @Test
    public void textAbsolutePositionInvalidYTest() throws IOException, InterruptedException {
        Assert.assertThrows(StyledXMLParserException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-absolutePosition-invalidY")
        );
    }

    //Relative move
    //X
    @Test
    public void textRelativeMovePositiveXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-positiveX");
    }

    @Test
    public void textRelativeMoveNegativeXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-negativeX");
    }

    @Test
    public void textRelativeMoveZeroXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-zeroX");
    }

    @Test
    public void textRelativeMoveInvalidXTest() throws IOException, InterruptedException {
        Assert.assertThrows(StyledXMLParserException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-invalidX")
        );
    }

    //Y
    @Test
    public void textRelativeMovePositiveYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-positiveY");
    }

    @Test
    public void textRelativeMoveNegativeYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-negativeY");
    }

    @Test
    public void textRelativeMoveZeroYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-zeroY");
    }

    @Test
    public void textRelativeMoveInvalidYTest() throws IOException, InterruptedException {
        Assert.assertThrows(StyledXMLParserException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "text-relativeMove-invalidY")
        );
    }

    @Test
    public void textFontSizeEmUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "textFontSizeEmUnitsTest");
    }

    @Test
    public void textFontSizeRemUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "textFontSizeRemUnitsTest");
    }

    @Test
    public void textFontSizeExUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "textFontSizeExUnitsTest");
    }

    @Test
    // TODO change cmp after DEVSIX-4143 is fixed
    public void tspanWithOneAbsoluteCoordinateTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "tspanWithOneAbsoluteCoordinateTest");
    }
}
