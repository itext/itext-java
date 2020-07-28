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

import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class SymbolTest extends SvgIntegrationTest {
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/renderers/impl/SymbolTest/";
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/renderers/impl/SymbolTest/";

    private ISvgConverterProperties properties;

    @BeforeClass
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Before
    public void before() {
        properties = new SvgConverterProperties()
                .setBaseUri(SOURCE_FOLDER);
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void simpleSymbolTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "simpleSymbolTest");
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void useTagFirstSymbolAfterTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "useTagFirstSymbolAfterTest");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    // TODO Check cmp after feature implementation. DEVSIX-2257
    public void heightPxAttrTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "heightPxAttrTest");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    // TODO Check cmp after feature implementation. DEVSIX-2257
    public void heightPercentsAttrTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "heightPercentsAttrTest");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    // TODO Check cmp after feature implementation. DEVSIX-2257
    public void widthPxAttrTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthPxAttrTest");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    // TODO Check cmp after feature implementation. DEVSIX-2257
    public void widthPercentsAttrTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthPercentsAttrTest");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    // TODO Check cmp after feature implementation. DEVSIX-2257
    public void widthHeightAttrPxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightAttrPxTest");
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 4),
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG, count = 2),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES, count = 2)
    })
    public void widthHeightAttrPercentsPxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "widthHeightAttrPercentsPxTest");
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.MISSING_HEIGHT),
            @LogMessage(messageTemplate = SvgLogMessageConstant.MISSING_WIDTH),
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG, count = 3),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES, count = 3)
    })
    public void preserveAspectRatioViewBoxTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "preserveAspectRatioViewBoxTest");
    }

    @Test
    @Ignore("DEVSIX-2257")
    // TODO Check cmp after feature implementation. DEVSIX-2257
    public void xYInUseWithDefsTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "xYInUseWithDefsTest");
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void classAttributeTestWithCssTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "classAttrTest");
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void styleAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "styleAttrTest");
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void styleAttrInUseTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "styleAttrInUseTest");
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void bothStyleAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "bothStyleAttrTest");
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void opacityAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "opacityAttrTest");
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void visibilityAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "visibilityAttrTest");
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void displayNoneAttributeTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "displayNoneAttrTest");
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void displayAttributeWithNoUseTagTest() throws IOException, InterruptedException {

        //Expects that nothing will be displayed on the page as it's done in Chrome browser
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "displayAttrWithNoUseTagTest");
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void simpleImageTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "simpleImageTest", properties);
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void linearGradientSymbolTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "linearGradientSymbolTest", properties);
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void useHeightWidthAllUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "useHeightWidthAllUnitsTest", properties);
    }

    @Test
    // TODO Check cmp after feature implementation. DEVSIX-2257
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES),
    })
    public void useSymbolHeightWidthAllUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "useSymbolHeightWidthAllUnitsTest",
                properties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = SvgLogMessageConstant.UNMAPPEDTAG),
            @LogMessage(messageTemplate = LogMessageConstant.ERROR_RESOLVING_PARENT_STYLES)
    })
    // TODO Check cmp after feature implementation. DEVSIX-2257
    public void useSymbolXYContrudictionAllUnitsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "useSymbolXYContrudictionAllUnitsTest",
                properties);
    }
}
