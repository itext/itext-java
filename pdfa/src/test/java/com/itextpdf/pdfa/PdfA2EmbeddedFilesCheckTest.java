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
package com.itextpdf.pdfa;

import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.filespec.PdfFileSpec;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfa.logs.PdfAConformanceLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("IntegrationTest")
public class PdfA2EmbeddedFilesCheckTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String cmpFolder = sourceFolder + "cmp/PdfA2EmbeddedFilesCheckTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PdfA2EmbeddedFilesCheckTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = PdfAConformanceLogMessageConstant.EMBEDDED_FILE_SHALL_BE_COMPLIANT_WITH_SPEC, count = 1)
    })
    // According to spec, only pdfa-1 or pdfa-2 compliant pdf document are allowed to be added to the
    // conforming pdfa-2 document. We only check they mime type, to define embedded file type, but we don't check
    // the bytes of the file. That's why this test creates invalid pdfa document.
    public void fileSpecNonConformingTest01() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_fileSpecNonConformingTest01.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_fileSpecNonConformingTest01.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        PdfPage page = pdfDocument.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World!")
                .endText()
                .restoreState();


        byte[] somePdf = new byte[25];
        pdfDocument.addAssociatedFile("some pdf file",
                PdfFileSpec.createEmbeddedFileSpec(pdfDocument, somePdf, "some pdf file", "foo.pdf",
                        PdfName.ApplicationPdf, null, new PdfName("Data")));

        pdfDocument.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = PdfAConformanceLogMessageConstant.EMBEDDED_FILE_SHALL_BE_COMPLIANT_WITH_SPEC, count = 1)
    })
    public void fileSpecCheckTest02() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_fileSpecCheckTest02.pdf";
        String cmpPdf = cmpFolder + "cmp_pdfA2b_fileSpecCheckTest02.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        PdfPage page = pdfDocument.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World!")
                .endText()
                .restoreState();

        InputStream fis = FileUtil.getInputStreamForFile(sourceFolder + "pdfs/pdfa.pdf");
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int length;
        while ((length = fis.read(buffer, 0, buffer.length)) > 0) {
            os.write(buffer, 0, length);
        }
        pdfDocument.addFileAttachment("some pdf file", PdfFileSpec.createEmbeddedFileSpec(pdfDocument, os.toByteArray(),
                "some pdf file", "foo.pdf", PdfName.ApplicationPdf, null, null));

        pdfDocument.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = PdfAConformanceLogMessageConstant.EMBEDDED_FILE_SHALL_BE_COMPLIANT_WITH_SPEC, count = 1)
    })
    public void fileSpecCheckTest03() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(sourceFolder + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent = new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, outputIntent);

        PdfPage page = pdfDocument.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World!")
                .endText()
                .restoreState();

        ByteArrayOutputStream txt = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(txt);
        out.print("<foo><foo2>Hello world</foo2></foo>");
        out.close();
        pdfDocument.addFileAttachment("foo file",
                PdfFileSpec.createEmbeddedFileSpec(pdfDocument, txt.toByteArray(), "foo file",
                        "foo.xml", PdfName.ApplicationXml, null, PdfName.Source));

        pdfDocument.close();
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
