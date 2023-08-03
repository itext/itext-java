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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)

public class SimpleSvgTagSvgNodeRendererIntegrationTest extends SvgIntegrationTest {

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
        Exception e = Assert.assertThrows(StyledXMLParserException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"invalidHeight")
        );
        Assert.assertEquals(MessageFormatUtil.format(StyledXMLParserException.NAN, "abc"), e.getMessage());
    }

    @Test
    public void invalidWidth() throws IOException, InterruptedException {
        Exception e = Assert.assertThrows(StyledXMLParserException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"invalidWidth")
        );
        Assert.assertEquals(MessageFormatUtil.format(StyledXMLParserException.NAN, "abc"), e.getMessage());
    }

    @Test
    public void invalidX() throws IOException, InterruptedException {
        Assert.assertThrows(StyledXMLParserException.class,
                () ->  convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"invalidX")
        );
    }

    @Test
    public void invalidY() throws IOException, InterruptedException {
        Assert.assertThrows(StyledXMLParserException.class,
                () -> convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,"invalidY")
        );
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
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 2),

    })
    public void percentInMeasurement() throws IOException, InterruptedException {
        //TODO: update after DEVSIX-2377
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "percentInMeasurement");
    }
}
