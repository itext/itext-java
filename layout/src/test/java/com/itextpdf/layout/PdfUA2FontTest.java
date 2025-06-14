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
package com.itextpdf.layout;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.TestUtil;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf/ua validation on Android)

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("IntegrationTest")
public class PdfUA2FontTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/layout/PdfUA2FontTest/";
    public static final String DESTINATION_FOLDER = TestUtil.getOutputPath() + "/layout/PdfUA2FontTest/";
    public static final String FONT_FOLDER = "./src/test/resources/com/itextpdf/layout/fonts/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void checkPuritan2WithUTF8Test() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "puritan2WithUTF8Test.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_puritan2WithUTF8Test.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "Puritan2.otf",
                    PdfEncodings.UTF8, EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            Paragraph paragraph = new Paragraph("Simple paragraph");
            document.add(paragraph);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkFreeSansWithMacromanTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "freeSansWithMacromanTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_freeSansWithMacromanTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    PdfEncodings.MACROMAN, EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            Paragraph paragraph = new Paragraph("Simple paragraph");
            document.add(paragraph);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkNotoSansRegularTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "notoSansRegularTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_notoSansRegularTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "NotoSans-Regular.ttf",
                    PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            Paragraph paragraph = new Paragraph("Simple paragraph");
            document.add(paragraph);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkOpenSansRegularTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "openSansRegularTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_openSansRegularTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "Open_Sans/OpenSans-Regular.ttf",
                    PdfEncodings.WINANSI, EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            Paragraph paragraph = new Paragraph("Simple paragraph");
            document.add(paragraph);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkType0FontTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "type0FontTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_type0FontTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(FONT_FOLDER + "FreeSans.ttf",
                    PdfEncodings.IDENTITY_H, EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            Paragraph paragraph = new Paragraph("Simple paragraph");
            document.add(paragraph);
        }
        compareAndValidate(outFile, cmpFile);
    }

    private void createSimplePdfUA2Document(PdfDocument pdfDocument) throws IOException, XMPException {
        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "simplePdfUA2.xmp"));
        XMPMeta xmpMeta = XMPMetaFactory.parse(new ByteArrayInputStream(bytes));
        pdfDocument.setXmpMetadata(xmpMeta);
        pdfDocument.setTagged();
        pdfDocument.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        pdfDocument.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDocument.getDocumentInfo();
        info.setTitle("PdfUA2 Title");
    }

    private void compareAndValidate(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
