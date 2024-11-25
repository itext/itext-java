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
package com.itextpdf.svg.css;

import com.itextpdf.styledxmlparser.css.media.MediaDeviceDescription;
import com.itextpdf.styledxmlparser.css.media.MediaType;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.svg.processors.ISvgConverterProperties;
import com.itextpdf.svg.processors.impl.SvgConverterProperties;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("IntegrationTest")
public class ImportRuleTest extends SvgIntegrationTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/css/ImportRuleTest/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/css/ImportRuleTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void externalCssLoopTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "externalCssLoop");
    }

    @Test
    public void recursiveImports1Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "recursiveImports1");
    }

    @Test
    public void recursiveImports2Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "recursiveImports2");
    }

    @Test
    public void recursiveImports3Test() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "recursiveImports3");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.IMPORT_MUST_COME_BEFORE, logLevel = LogLevelConstants.WARN)
    })
    public void styleBeforeImportTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "styleBeforeImport");
    }

    @Test
    public void twoExternalCssTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "twoExternalCss");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, logLevel = LogLevelConstants.ERROR)
    })
    public void wrongNestedCssTest() throws IOException, InterruptedException {
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "wrongNestedCss");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, logLevel = LogLevelConstants.ERROR)
    })
    // TODO DEVSIX-2263 SVG: CSS: Media query processing
    public void mediaQueryTest() throws IOException, InterruptedException {
        ISvgConverterProperties properties = new SvgConverterProperties().setMediaDeviceDescription(new MediaDeviceDescription(
                MediaType.SCREEN));
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER, "mediaQuery", properties);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNABLE_TO_RETRIEVE_STREAM_WITH_GIVEN_BASE_URI, logLevel = LogLevelConstants.ERROR)
    })
    public void srcInImportTest() throws IOException, InterruptedException {
        // Spec says that import can contain src, but no one browser doesn't support it as well as iText
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, "srcInImport");
    }
}
