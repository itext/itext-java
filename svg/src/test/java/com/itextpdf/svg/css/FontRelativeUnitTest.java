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
public class FontRelativeUnitTest extends SvgIntegrationTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/svg/css/FontRelativeUnitTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/svg/css/FontRelativeUnitTest/";

    @BeforeAll
    public static void beforeClass() {
        ITextTest.createDestinationFolder(DESTINATION_FOLDER);
    }

    // Text tests block

    @Test
    public void textFontSizeRemUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textFontSizeRemUnitTest");
    }

    @Test
    public void textFontSizeEmUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textFontSizeEmUnitTest");
    }

    @Test
    public void textNegativeFontSizeRemUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textNegativeFontSizeRemUnitTest");
    }

    @Test
    public void textNegativeFontSizeEmUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textNegativeFontSizeEmUnitTest");
    }

    @Test
    public void textFontSizeFromParentTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textFontSizeFromParentTest");
    }

    @Test
    public void textFontSizeHierarchyEmAndRemUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textFontSizeHierarchyEmAndRemUnitTest");
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 4)
    })
    // TODO DEVSIX-2607 relative font-size value is not supported for tspan element
    public void textFontSizeInheritanceFromUseTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textFontSizeInheritanceFromUseTest");
    }

    @Test
    public void textFontSizeFromUseTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"textFontSizeFromUseTest");
    }

    // Linear gradient tests block

    @Test
    public void lnrGrdntObjectBoundingBoxEmUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"lnrGrdntObjectBoundingBoxEmUnitTest");
    }

    @Test
    public void lnrGrdntUserSpaceOnUseEmUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"lnrGrdntUserSpaceOnUseEmUnitTest");
    }

    @Test
    public void lnrGrdntObjectBoundingBoxRemUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"lnrGrdntObjectBoundingBoxRemUnitTest");
    }

    @Test
    public void lnrGrdntUserSpaceOnUseRemUnitTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"lnrGrdntUserSpaceOnUseRemUnitTest");
    }

    @Test
    public void lnrGrdntObjectBoundingBoxEmUnitFromDirectParentTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"lnrGrdntObjectBoundingBoxEmUnitFromDirectParentTest");
    }

    @Test
    public void lnrGrdntUserSpaceOnUseEmUnitFromDirectParentTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"lnrGrdntUserSpaceOnUseEmUnitFromDirectParentTest");
    }

    @Test
    public void lnrGrdntFontSizeFromDefsFillTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"lnrGrdntFontSizeFromDefsFillTest");
    }

    // Symbol tests block

    @Test
    public void symbolFontSizeInheritanceFromUseTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"symbolFontSizeInheritanceFromUseTest");
    }

    // Marker tests block

    @Test
    public void markerFontSizeInheritanceFromDifsTest() throws IOException, InterruptedException {
        convertAndCompareSinglePage(SOURCE_FOLDER, DESTINATION_FOLDER,"markerFontSizeInheritanceFromDifsTest");
    }
}
