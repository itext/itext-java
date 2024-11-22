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

import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;

@Tag("IntegrationTest")
public class TSpanNodeRendererIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/TSpanNodeRendererIntegrationTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/TSpanNodeRendererIntegrationTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    //Relative Move tests
    @Test
    public void TSpanRelativeMovePositiveXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-positiveX");
    }

    @Test
    public void TSpanRelativeMoveNegativeXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-negativeX");
    }

    @Test
    public void TSpanRelativeMoveZeroXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-zeroX");
    }

    @Test
    public void TSpanRelativeMoveInvalidXTest() throws IOException, InterruptedException {
        Assertions.assertThrows(StyledXMLParserException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-invalidX")
        );
    }

    @Test
    public void TSpanRelativeMovePositiveYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-positiveY");
    }

    @Test
    public void TSpanRelativeMoveNegativeYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-negativeY");
    }

    @Test
    public void TSpanRelativeMoveZeroYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-zeroY");
    }

    @Test
    public void TSpanRelativeMoveInvalidYTest() throws IOException, InterruptedException {
        Assertions.assertThrows(StyledXMLParserException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-invalidY")
        );
    }

    @Test
    public void TSpanRelativeMoveXandYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-relativeMove-XandY");
    }
    
    //Absolute Position tests
    @Test
    public void TSpanAbsolutePositionPositiveXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-positiveX");
    }

    @Test
    public void TSpanAbsolutePositionNegativeXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-negativeX");
    }

    @Test
    public void TSpanAbsolutePositionZeroXTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-zeroX");
    }
    
    @Test
    public void TSpanAbsolutePositionInvalidXTest() throws IOException, InterruptedException {
        Assertions.assertThrows(StyledXMLParserException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-invalidX")
        );
    }

    @Test
    public void TSpanAbsolutePositionPositiveYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-positiveY");
    }

    @Test
    public void TSpanAbsolutePositionNegativeYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-negativeY");
    }

    @Test
    public void TSpanAbsolutePositionZeroYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-zeroY");
    }

    @Test
    public void TSpanAbsolutePositionInvalidYTest() throws IOException, InterruptedException {
        Assertions.assertThrows(StyledXMLParserException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-invalidY")
        );
    }

    @Test
    public void TSpanAbsolutePositionXandYTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-XandY");
    }

    @Test
    public void TSpanAbsolutePositionNestedTSpanTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePosition-nestedTSpan");
    }

    //Whitespace
    @Test
    public void TSpanWhiteSpaceFunctionalTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-whitespace");
    }

    //Relative move and absolute position interplay
    @Test
    public void TSpanAbsolutePositionAndRelativeMoveFunctionalTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-absolutePositionAndRelativeMove");
    }

    //Text-anchor test
    @Test
    public void TSpanTextAnchorFunctionalTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textspan-textAnchor");
    }

    @Test
    //TODO: update after DEVSIX-2507 and DEVSIX-3005 fix
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED),
    })
    public void tspanBasicExample() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "tspanBasicExample");
    }

    @Test
    //TODO: update after DEVSIX-2507 and DEVSIX-3005 fix
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED),
    })
    public void tspanNestedExample() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "tspanNestedExample");
    }

    @Test
    //TODO: update cmp-file after DEVSIX-2270 fixed
    public void text_decoration_Test() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "text_decoration");
    }

    @Test
    public void tspanDefaultFontSizeTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "tspanDefaultFontSize");
    }

    @Test
    public void tspanInheritTextFontSizeTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "tspanInheritTextFontSize");
    }

    @Test
    public void tspanInheritAncestorsTspanFontSizeTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "tspanInheritAncestorsTspanFontSize");
    }

    @Test
    public void tspanNestedWithOffsets() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "tspanNestedWithOffsets");
    }

    @Test
    public void tspanNestedRelativeOffsets() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "tspanNestedRelativeOffsets");
    }

    @Test
    public void simpleNestedTspanTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "simpleNestedTspan");
    }

    @Test
    public void xWithoutYTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "xWithoutY");
    }

    @Test
    public void noXNoYTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "noXNoY");
    }

    @Test
    public void yWithoutXTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "yWithoutX");
    }

    @Test
    // TODO DEVSIX-2507 support x, y, dx, dy attributes
    public void negativeAbsoluteAndRelativePositionTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "negativeAbsoluteAndRelativePosition");
    }

    @Test
    // TODO DEVSIX-2507 support x, y, dx, dy attributes
    public void noPositionAfterRelativeTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "noPositionAfterRelative");
    }
}
