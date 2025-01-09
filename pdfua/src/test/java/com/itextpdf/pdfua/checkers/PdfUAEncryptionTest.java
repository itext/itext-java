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
package com.itextpdf.pdfua.checkers;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.CanvasTag;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.tagging.PdfMcr;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfua.PdfUATestPdfDocument;
import com.itextpdf.pdfua.exceptions.PdfUAConformanceException;
import com.itextpdf.pdfua.exceptions.PdfUAExceptionMessageConstants;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Tag("IntegrationTest")
public class PdfUAEncryptionTest extends ExtendedITextTest {

    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfua/PdfUAEncryptionTest/";
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfua/PdfUAEncryptionTest/";
    private static final String FONT = "./src/test/resources/com/itextpdf/pdfua/font/FreeSans.ttf";
    private static final byte[] USER_PASSWORD = "user".getBytes(StandardCharsets.UTF_8);
    private static final byte[] OWNER_PASSWORD = "owner".getBytes(StandardCharsets.UTF_8);

    @BeforeAll
    public static void before() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void encryptWithPassword()
            throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "encryptWithPassword.pdf";
        WriterProperties writerProperties = new WriterProperties()
                .setStandardEncryption(USER_PASSWORD, OWNER_PASSWORD, EncryptionConstants.ALLOW_SCREENREADERS, 3);
        try (PdfWriter writer = new PdfWriter(outPdf,
                writerProperties);
             PdfUATestPdfDocument document = new PdfUATestPdfDocument(writer)) {
            writeTextToDocument(document);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf,
                SOURCE_FOLDER + "cmp_" + "encryptWithPassword.pdf", DESTINATION_FOLDER, "diff", USER_PASSWORD, USER_PASSWORD));
    }

    @Test
    public void encryptWithPasswordWithInvalidPermissionsTest()
            throws IOException {
        String outPdf = DESTINATION_FOLDER + "encryptWithPassword2.pdf";
        WriterProperties writerProperties = new WriterProperties()
                .setStandardEncryption(USER_PASSWORD, OWNER_PASSWORD,  ~EncryptionConstants.ALLOW_SCREENREADERS, 3);
        PdfUATestPdfDocument document = new PdfUATestPdfDocument(new PdfWriter(outPdf, writerProperties));
        writeTextToDocument(document);
        Exception e = Assertions.assertThrows(PdfUAConformanceException.class, () -> document.close());
        Assertions.assertEquals(PdfUAExceptionMessageConstants.TENTH_BIT_OF_P_VALUE_IN_ENCRYPTION_SHOULD_BE_NON_ZERO,
                e.getMessage());
    }

    private void writeTextToDocument(PdfDocument document) throws IOException {
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        PdfFont font = PdfFontFactory.createFont(FONT, PdfEncodings.WINANSI, PdfFontFactory.EmbeddingStrategy.PREFER_EMBEDDED);
        PdfStructElem doc = document.getStructTreeRoot().addKid(new PdfStructElem(document, PdfName.Document));
        PdfStructElem paragraph = doc.addKid(new PdfStructElem(document, PdfName.P, page));
        PdfMcr mcr = paragraph.addKid(new PdfMcrNumber(page, paragraph));
        canvas
                .openTag(new CanvasTag(mcr))
                .saveState()
                .beginText()
                .setFontAndSize(font, 12)
                .moveText(200, 200)
                .showText("Hello World!")
                .endText()
                .restoreState()
                .closeTag();
    }
}
