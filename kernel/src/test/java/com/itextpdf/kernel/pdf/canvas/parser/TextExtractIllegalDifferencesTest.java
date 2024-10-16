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
package com.itextpdf.kernel.pdf.canvas.parser;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class TextExtractIllegalDifferencesTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/parser/TextExtractIllegalDifferencesTest/";

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.DOCFONT_HAS_ILLEGAL_DIFFERENCES))
    public void illegalDifference() throws IOException {
        try (PdfDocument pdf = new PdfDocument(new PdfReader(sourceFolder + "illegalDifference.pdf"))) {
            AssertUtil.doesNotThrow(() -> PdfTextExtractor.getTextFromPage(pdf.getFirstPage()));
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = IoLogMessageConstant.DOCFONT_HAS_ILLEGAL_DIFFERENCES, count = 2))
    public void illegalDifferenceType3Font() throws IOException {
        try (PdfDocument pdf = new PdfDocument(new PdfReader(sourceFolder + "illegalDifferenceType3Font.pdf"))) {
            AssertUtil.doesNotThrow(() -> PdfTextExtractor.getTextFromPage(pdf.getFirstPage()));
        }
    }
}
