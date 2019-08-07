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

import com.itextpdf.io.util.MessageFormatUtil;
import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(IntegrationTest.class)

public class SimpleSvgTagSvgNodeRendererIntegrationTest extends SvgIntegrationTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/RootSvgNodeRendererTest/svg/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/RootSvgNodeRendererTest/svg/";

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void everythingPresentAndValidTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"everythingPresentAndValid");
    }

    @Test
    //TODO: change cmp file after DEVSIX-3123 fixed
    @LogMessages(messages = {
            @LogMessage(messageTemplate =  SvgLogMessageConstant.MISSING_HEIGHT),
    })
    public void absentHeight() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"absentHeight");
    }

    @Test
    //TODO: change cmp file after DEVSIX-3123 fixed
    @LogMessages(messages = {
            @LogMessage(messageTemplate =  SvgLogMessageConstant.MISSING_WIDTH),
    })
    public void absentWidth() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"absentWidth");
    }

    @Test
    //TODO: change cmp file after DEVSIX-3123 fixed
    @LogMessages(messages = {
            @LogMessage(messageTemplate =  SvgLogMessageConstant.MISSING_WIDTH),
            @LogMessage(messageTemplate =  SvgLogMessageConstant.MISSING_HEIGHT),
    })
    public void absentWidthAndHeight() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"absentWidthAndHeight");
    }

    @Test

    public void absentWHViewboxPresent() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"absentWHViewboxPresent");
    }

    @Test
    public void absentX() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"absentX");
    }

    @Test
    public void absentY() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"absentY");
    }

    @Test
    public void invalidHeight() throws IOException, InterruptedException {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(LogMessageConstant.NAN, "abc"));
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"invalidHeight");
    }

    @Test
    public void invalidWidth() throws IOException, InterruptedException {
        junitExpectedException.expect(StyledXMLParserException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(LogMessageConstant.NAN, "abc"));
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"invalidWidth");
    }

    @Test
    public void invalidX() throws IOException, InterruptedException {
        junitExpectedException.expect(StyledXMLParserException.class);
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"invalidX");
    }

    @Test
    public void invalidY() throws IOException, InterruptedException {
        junitExpectedException.expect(StyledXMLParserException.class);
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"invalidY");
    }

    @Test
    public void negativeEverything() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"negativeEverything");
    }

    @Test
    public void negativeHeight() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"negativeHeight");
    }

    @Test
    public void negativeWidth() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"negativeWidth");
    }

    @Test
    public void negativeWidthAndHeight() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"negativeWidthAndHeight");
    }

    @Test
    public void negativeX() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"negativeX");
    }

    @Test
    public void negativeXY() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"negativeXY");
    }

    @Test
    public void negativeY() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"negativeY");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 2),

    })
    public void percentInMeasurement() throws IOException, InterruptedException {
        //TODO: update after DEVSIX-2377
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "percentInMeasurement");
    }
}
