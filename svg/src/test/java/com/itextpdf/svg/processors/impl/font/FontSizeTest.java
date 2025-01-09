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
package com.itextpdf.svg.processors.impl.font;

import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.test.ITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class FontSizeTest extends SvgIntegrationTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/processors/impl/font/FontSizeTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/processors/impl/font/FontSizeTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void fontSize01Test() throws IOException, InterruptedException {
        String name = "fontSizeTest01";
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,name);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED),
    })
    public void fontSize02Test() throws IOException, InterruptedException {
        String name = "fontSizeTest02";
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,name);
    }

    @Test
    public void fontSize03Test() throws IOException, InterruptedException {
        String name = "fontSizeTest03";
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,name);
    }
    @Test
    public void fontAbsoluteKeywords() throws IOException, InterruptedException {
        String name = "fontAbsoluteKeywords";
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,name);
    }

    @Test
    public void fontRelativeKeywords() throws IOException, InterruptedException {
        String name = "fontRelativeKeywords";
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER,name);
    }

    @Test
    public void diffUnitsOfMeasure() throws IOException, InterruptedException {
        String name = "diff_units_of_measure";
        convertAndCompare(SOURCE_FOLDER, DESTINATION_FOLDER, name);
    }
}
