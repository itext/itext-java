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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
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
public class PathParsingIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/PathParsingIntegrationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/PathParsingIntegrationTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(destinationFolder);
    }

    @Test
    public void normalTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "normal");
    }

    @Test
    public void mixTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "mix");
    }

    @Test
    public void noWhitespace() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "noWhitespace");
    }

    @Test
    public void zOperator() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "zOperator");
    }

    @Test
    public void missingOperandArgument() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "missingOperandArgument");
    }

    @Test
    public void decimalPointHandlingTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "decimalPointHandling");
    }

    @Test
    public void invalidOperatorTest() throws IOException, InterruptedException {
        Assertions.assertThrows(SvgProcessingException.class,
                () -> convertAndCompare(sourceFolder, destinationFolder, "invalidOperator")
        );
    }

    @Test
    public void invalidOperatorCSensTest() throws IOException, InterruptedException {
        Assertions.assertThrows(SvgProcessingException.class,
                () -> convertAndCompare(sourceFolder, destinationFolder, "invalidOperatorCSens")
        );
    }

    @Test
    public void moreThanOneHParam() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "moreThanOneHParam");
    }

    @Test
    public void negativeAfterPositiveHandlingTest01() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "negativeAfterPositiveHandling");
    }

    @Test
    public void negativeAfterPositiveHandlingTest02() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "negativeAfterPositiveHandlingExtendedViewbox");
    }

    @Test
    public void insignificantSpacesTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "insignificantSpaces");
    }

    @Test
    public void precedingSpacesTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "precedingSpaces");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG),
    })
    //TODO: update cmp-file after DEVSIX-2255
    public void textPathTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "textpath");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG),
    })
    public void textPathExample() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-2255 implemented
        convertAndCompare(sourceFolder, destinationFolder, "textPathExample");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPED_TAG),
    })
    public void tspanInTextPathTest() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-2255 implemented
        convertAndCompare(sourceFolder, destinationFolder, "tspanInTextPath");
    }

    @Test
    public void pathH() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "pathH");
    }

    @Test
    public void pathV() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder,  "pathV");
    }

    @Test
    public void pathHV() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "pathHV");
    }

    @Test
    public void pathRelativeAbsoluteCombinedTest() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "pathRelativeAbsoluteCombined");
    }

    @Test
    public void pathHVExponential() throws IOException, InterruptedException {
        // TODO DEVSIX-2906 This file has large numbers (2e+10) in it. At the moment we do not post-process such big numbers
        // and simply print them to the output PDF. Not all the viewers are able to process such large numbers
        // and hence different results in different viewers. Acrobat is not able to process the numbers
        // and the result is garbled visual representation. GhostScript, however, renders the PDF just fine
        convertAndCompare(sourceFolder, destinationFolder, "pathHVExponential");
    }

    @Test
    public void pathABasic() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "pathABasic");
    }

    @Test
    public void pathAFlags() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "pathAFlags");
    }

    @Test
    public void pathAAxisRotation() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "pathAAxisRotation");
    }

    @Test
    //TODO: update cmp when DEVSIX-3010 and DEVSIX-3011 fixed
    public void pathAOutOfRange() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "pathAOutOfRange");
    }

    @Test
    //TODO: update cmp when DEVSIX-3010 fixed
    public void arcs_end_point() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "arcsEndPoint");
    }

    @Test
    //TODO: update cmp when DEVSIX-3011 fixed
    public void flags_out_of_range() throws IOException, InterruptedException {
        convertAndCompareSinglePage(sourceFolder, destinationFolder, "flags_out_of_range");
    }
}

