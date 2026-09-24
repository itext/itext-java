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

import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class PathParsingIntegrationTest extends SvgIntegrationTest {

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/PathParsingIntegrationTest/";
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/svg/renderers/impl/PathParsingIntegrationTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void normalTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "normal");
    }

    @Test
    public void mixTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "mix");
    }

    @Test
    public void noWhitespace() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "noWhitespace");
    }

    @Test
    public void zOperator() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "zOperator");
    }

    @Test
    public void missingOperandArgument() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "missingOperandArgument");
    }

    @Test
    public void decimalPointHandlingTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "decimalPointHandling");
    }

    @Test
    public void invalidOperatorTest() throws IOException, InterruptedException {
        Assertions.assertThrows(SvgProcessingException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "invalidOperator")
        );
    }

    @Test
    public void invalidOperatorCSensTest() throws IOException, InterruptedException {
        Assertions.assertThrows(SvgProcessingException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "invalidOperatorCSens")
        );
    }

    @Test
    public void moreThanOneHParam() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "moreThanOneHParam");
    }

    @Test
    public void negativeAfterPositiveHandlingTest01() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "negativeAfterPositiveHandling");
    }

    @Test
    public void negativeAfterPositiveHandlingTest02() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "negativeAfterPositiveHandlingExtendedViewbox");
    }

    @Test
    public void insignificantSpacesTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "insignificantSpaces");
    }

    @Test
    public void precedingSpacesTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "precedingSpaces");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG),
    })
    //TODO: update cmp-file after DEVSIX-2255
    public void textPathTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textpath");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG),
    })
    public void textPathExample() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-2255 implemented
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "textPathExample");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG),
    })
    public void tspanInTextPathTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-2255 implemented
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "tspanInTextPath");
    }

    @Test
    public void pathH() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathH");
    }

    @Test
    public void pathV() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,  "pathV");
    }

    @Test
    public void pathHV() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathHV");
    }

    @Test
    public void pathRelativeAbsoluteCombinedTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathRelativeAbsoluteCombined");
    }

    @Test
    public void pathHVExponential() throws IOException, InterruptedException {
        // TODO DEVSIX-2906 This file has large numbers (2e+10) in it. At the moment we do not post-process such big numbers
        // and simply print them to the output PDF. Not all the viewers are able to process such large numbers
        // and hence different results in different viewers. Acrobat is not able to process the numbers
        // and the result is garbled visual representation. GhostScript, however, renders the PDF just fine
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "pathHVExponential");
    }

    @Test
    public void pathABasic() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "pathABasic");
    }

    @Test
    public void pathAFlags() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "pathAFlags");
    }

    @Test
    public void pathAAxisRotation() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "pathAAxisRotation");
    }

    @Test
    //TODO: update cmp when DEVSIX-3010 and DEVSIX-3011 fixed
    public void pathAOutOfRange() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "pathAOutOfRange");
    }

    @Test
    //TODO: update cmp when DEVSIX-3010 fixed
    public void arcs_end_point() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "arcsEndPoint");
    }

    @Test
    //TODO: update cmp when DEVSIX-3011 fixed
    public void flags_out_of_range() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "flags_out_of_range");
    }
}

