/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.io.IOException;

@Category(IntegrationTest.class)
public class PathParsingIntegrationTest extends SvgIntegrationTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/svg/renderers/impl/PathParsingIntegrationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/svg/renderers/impl/PathParsingIntegrationTest/";

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @BeforeClass
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
        junitExpectedException.expect(SvgProcessingException.class);
        convertAndCompare(sourceFolder, destinationFolder, "invalidOperator");
    }

    @Test
    public void invalidOperatorCSensTest() throws IOException, InterruptedException {
        junitExpectedException.expect(SvgProcessingException.class);
        convertAndCompare(sourceFolder, destinationFolder, "invalidOperatorCSens");
    }

    @Test
    // TODO-2331 Update the cmp after the issue is resolved
    // UPD: Seems to be fixed now, but leaving the TODO and issue open because the scope of the issue might be bigger than
    // this test
    public void moreThanOneHParam() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "moreThanOneHParam");
    }

    @Test
    //TODO update after DEVSIX-2331 - several (negative) line operators
    public void negativeAfterPositiveHandlingTest01() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "negativeAfterPositiveHandling");
    }

    @Test
    //TODO update after DEVSIX-2333 (negative viewbox) fix
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
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
    })
    //TODO: update cmp-file after DEVSIX-2255
    public void text_path_Test() throws IOException, InterruptedException {
        convertAndCompare(sourceFolder, destinationFolder, "textpath");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
    })
    public void textPathExample() throws IOException, InterruptedException {
        //TODO: update when DEVSIX-2255 implemented
        convertAndCompare(sourceFolder, destinationFolder, "textPathExample");
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

