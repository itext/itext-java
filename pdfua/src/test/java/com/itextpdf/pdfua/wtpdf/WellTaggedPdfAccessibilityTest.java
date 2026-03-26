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
package com.itextpdf.pdfua.wtpdf;

import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WellTaggedPdfConformance;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.pdfua.UaValidationTestFramework;
import com.itextpdf.pdfua.checkers.PdfUATableTest;
import com.itextpdf.pdfua.logs.PdfUALogMessageConstants;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.LogLevelConstants;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.pdfa.VeraPdfValidator;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static com.itextpdf.pdfua.checkers.PdfUATableTest.newDataCell;
import static com.itextpdf.pdfua.checkers.PdfUATableTest.newHeaderCell;

@Tag("IntegrationTest")
public class WellTaggedPdfAccessibilityTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER =
            TestUtil.getOutputPath() + "/pdfua/wtpdf/WellTaggedPdfAccessibilityTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/wtpdf"
            + "/WellTaggedPdfAccessibilityTest/";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }


    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = PdfUALogMessageConstants.PDF_TO_WTPDF_CONVERSION_IS_NOT_SUPPORTED,
                    logLevel = LogLevelConstants.WARN)
    })
    public void openNotWellTaggedPdfDocumentTest() {
        AssertUtil.doesNotThrow(() ->
                new WellTaggedPdfDocument(
                        new PdfReader(SOURCE_FOLDER + "usualPdf.pdf"),
                        new PdfWriter(new ByteArrayOutputStream()),
                        new WellTaggedPdfConfig(WellTaggedPdfConformance.FOR_ACCESSIBILITY, "simple doc", "eng")));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = PdfUALogMessageConstants.WRITER_PROPERTIES_PDF_VERSION_WAS_OVERRIDDEN,
                    logLevel = LogLevelConstants.WARN)
    })
    public void settingWrongPdfVersionTest() {
        try (WellTaggedPdfDocument doc = new WellTaggedPdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_1_4)),
                new WellTaggedPdfConfig(WellTaggedPdfConformance.FOR_ACCESSIBILITY, "en-us", "title"))) {
            Assertions.assertEquals(PdfVersion.PDF_2_0, doc.getPdfVersion());
        }
    }

    @Test
    public void wellTaggedPdfTableAccesibilityTest() throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER,
                PdfConformance.WELL_TAGGED_PDF_FOR_REUSE);
        PdfUATableTest.TableBuilder tableBuilder = new PdfUATableTest.TableBuilder(4);
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("wellTaggedPdfTableTest");
    }

    @Test
    public void wellTaggedPdfTableReuseTest() throws IOException {
        UaValidationTestFramework framework = new UaValidationTestFramework(DESTINATION_FOLDER,
                PdfConformance.WELL_TAGGED_PDF_FOR_ACCESSIBILITY);
        PdfUATableTest.TableBuilder tableBuilder = new PdfUATableTest.TableBuilder(4);
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(newHeaderCell(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(newDataCell("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder.generateFunc());
        framework.assertBothValid("wellTaggedPdfTableTest");
    }
}
