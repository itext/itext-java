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

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfUAConformance;
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
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Tag("IntegrationTest")
public class WellTaggedPdfAccessibilityTest extends ExtendedITextTest {
    private static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/pdfua/wtpdf/WellTaggedPdfAccessibilityTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/wtpdf/WellTaggedPdfAccessibilityTest/";

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = PdfUALogMessageConstants.PDF_TO_WTPDF_CONVERSION_IS_NOT_SUPPORTED, logLevel = LogLevelConstants.WARN)
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
            @LogMessage(messageTemplate = PdfUALogMessageConstants.WRITER_PROPERTIES_PDF_VERSION_WAS_OVERRIDDEN, logLevel = LogLevelConstants.WARN)
    })
    public void settingWrongPdfVersionTest() {
        try (WellTaggedPdfDocument doc = new WellTaggedPdfDocument(new PdfWriter(new ByteArrayOutputStream(),
                new WriterProperties().setPdfVersion(PdfVersion.PDF_1_4)),
                new WellTaggedPdfConfig(WellTaggedPdfConformance.FOR_ACCESSIBILITY, "en-us", "title"))) {
            Assertions.assertEquals(PdfVersion.PDF_2_0, doc.getPdfVersion());
        }
    }

    @Test
    public void wellTaggedPdfTableTest() throws IOException {
        WellTaggedValidationFramework framework = new WellTaggedValidationFramework(DESTINATION_FOLDER);
        PdfUATableTest.TableBuilder tableBuilder = new PdfUATableTest.TableBuilder(4);
        tableBuilder.addBodyCell(new PdfUATableTest.HeaderCellSupplier(null, "Header 1", 1, 1, "Column"));
        tableBuilder.addBodyCell(new PdfUATableTest.HeaderCellSupplier(null, "Header 2", 1, 1, "Column"));
        tableBuilder.addBodyCell(new PdfUATableTest.HeaderCellSupplier(null, "Header 3", 1, 1, "Column"));
        tableBuilder.addBodyCell(new PdfUATableTest.HeaderCellSupplier(null, "Header 4", 1, 1, "Column"));
        for (int i = 0; i < 4; i++) {
            tableBuilder.addBodyCell(new PdfUATableTest.DataCellSupplier("Data 1", 1, 1, null));
        }

        framework.addSuppliers(tableBuilder);
        framework.assertBothValid("wellTaggedPdfTableTest", PdfUAConformance.PDF_UA_2);
    }

    private static class WellTaggedValidationFramework extends UaValidationTestFramework {
        public WellTaggedValidationFramework(String destinationFolder) {
            super(destinationFolder);
        }

        // Android-Conversion-Skip-Block-Start (TODO DEVSIX-7377 introduce pdf/ua validation on Android)
        @Override
        protected VeraPdfValidator getVerapdfValidator() {
            return new VeraPdfValidator("WTPDF");
        }
        // Android-Conversion-Skip-Block-End

        @Override
        protected PdfDocument createPdfDocument(String filename, PdfUAConformance pdfUAConformance) throws IOException {
            return new WellTaggedPdfDocument(new PdfWriter(filename,
                    new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                    new WellTaggedPdfConfig(
                            WellTaggedPdfConformance.FOR_ACCESSIBILITY, "English pangram", "en-US"));
        }

        @Override
        protected PdfDocument createPdfDocument(String inputFile, String outputFile, PdfUAConformance pdfUAConformance) throws IOException {
            return new WellTaggedPdfDocument(new PdfReader(inputFile),
                    new PdfWriter(outputFile, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                    new WellTaggedPdfConfig(
                            WellTaggedPdfConformance.FOR_ACCESSIBILITY, "English pangram", "en-US"));
        }
    }
}
