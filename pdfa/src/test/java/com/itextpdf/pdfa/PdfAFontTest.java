/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfAFontTest extends ExtendedITextTest {

    static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    static final String outputDir = "./target/test/com/itextpdf/pdfa/PdfAFontTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(outputDir);
    }

    @Test
    public void fontCheckPdfA1_01() throws IOException, InterruptedException {
        String outPdf = outputDir + "pdfA1b_fontCheckPdfA1_01.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA1b_fontCheckPdfA1_01.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-1B")
                .endText()
                .restoreState();
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void fontCheckPdfA1_02() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", PdfEncodings.WINANSI,
                EmbeddingStrategy.FORCE_NOT_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-1B")
                .endText()
                .restoreState();

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ALL_THE_FONTS_MUST_BE_EMBEDDED_THIS_ONE_IS_NOT_0, "FreeSans"),
                e.getMessage());
    }

    @Test
    public void fontCheckPdfA1_03() throws IOException, InterruptedException {
        String outPdf = outputDir + "pdfA1b_fontCheckPdfA1_03.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA1b_fontCheckPdfA1_03.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "Identity-H", EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-1B")
                .endText()
                .restoreState();
        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void fontCheckPdfA1_04() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFontFactory.createFont("Helvetica",
                "WinAnsi", EmbeddingStrategy.PREFER_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-1B")
                .endText()
                .restoreState();

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfaExceptionMessageConstant.ALL_THE_FONTS_MUST_BE_EMBEDDED_THIS_ONE_IS_NOT_0, "Helvetica"),
                e.getMessage());
    }

    @Test
    public void fontCheckPdfA1_05() throws IOException, InterruptedException {
        String outPdf = outputDir + "pdfA1b_fontCheckPdfA1_05.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA1b_fontCheckPdfA1_05.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "NotoSansCJKtc-Light.otf", "Identity-H");
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-1B")
                .endText()
                .restoreState();

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void fontCheckPdfA2_01() throws IOException, InterruptedException {
        String outPdf = outputDir + "pdfA2b_fontCheckPdfA2_01.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA2b_fontCheckPdfA2_01.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "Identity-H", EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-2B")
                .endText()
                .restoreState();

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void fontCheckPdfA3_01() throws IOException, InterruptedException {
        String outPdf = outputDir + "pdfA3b_fontCheckPdfA3_01.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA3b_fontCheckPdfA3_01.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf",
                "Identity-H", EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-3B")
                .endText()
                .restoreState();

        doc.close();
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void cidFontCheckTest1() throws IOException, InterruptedException {
        String outPdf = outputDir + "pdfA2b_cidFontCheckTest1.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA2b_cidFontCheckTest1.pdf";
        generateAndValidatePdfA2WithCidFont("FreeSans.ttf", outPdf);
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void cidFontCheckTest2() throws IOException, InterruptedException {
        String outPdf = outputDir + "pdfA2b_cidFontCheckTest2.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA2b_cidFontCheckTest2.pdf";
        String expectedVeraPdfWarning = "The following warnings and errors were logged during validation:\n"
                + "WARNING: The Top DICT does not begin with ROS operator";

        generateAndValidatePdfA2WithCidFont("Puritan2.otf", outPdf, expectedVeraPdfWarning);
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void cidFontCheckTest3() throws IOException, InterruptedException {
        String outPdf = outputDir + "pdfA2b_cidFontCheckTest3.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_pdfA2b_cidFontCheckTest3.pdf";
        generateAndValidatePdfA2WithCidFont("NotoSansCJKtc-Light.otf", outPdf);
        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void symbolicTtfCharEncodingsPdfA1Test01() throws IOException, InterruptedException {
        // encoding must not be specified
        createDocumentWithFont("symbolicTtfCharEncodingsPdfA1Test01.pdf", "Symbols1.ttf", "", PdfAConformanceLevel.PDF_A_1B);
    }

    @Test
    public void symbolicTtfCharEncodingsPdfA1Test02() throws IOException, InterruptedException {
        // if you specify encoding, symbolic font is treated as non-symbolic
        createDocumentWithFont("symbolicTtfCharEncodingsPdfA1Test02.pdf", "Symbols1.ttf", PdfEncodings.MACROMAN, PdfAConformanceLevel.PDF_A_1B);
    }

    @Test
    public void symbolicTtfCharEncodingsPdfA1Test03() throws IOException, InterruptedException {
        // if you specify encoding, symbolic font is treated as non-symbolic
        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> createDocumentWithFont("symbolicTtfCharEncodingsPdfA1Test03.pdf", "Symbols1.ttf", "ISO-8859-1", PdfAConformanceLevel.PDF_A_1B)
        );
        Assert.assertEquals(PdfaExceptionMessageConstant.ALL_NON_SYMBOLIC_TRUE_TYPE_FONT_SHALL_SPECIFY_MAC_ROMAN_OR_WIN_ANSI_ENCODING_AS_THE_ENCODING_ENTRY,
                e.getMessage());
    }

    @Test
    public void symbolicTtfCharEncodingsPdfA1Test04() throws IOException, InterruptedException {
        // emulate behaviour with default WinAnsi, which was present in 7.1
        createDocumentWithFont("symbolicTtfCharEncodingsPdfA1Test04.pdf", "Symbols1.ttf", PdfEncodings.WINANSI, PdfAConformanceLevel.PDF_A_1B);
    }

    @Test
    public void symbolicTtfCharEncodingsPdfA1Test05() throws IOException, InterruptedException {
        // Identity-H behaviour should be the same as the default one, starting from 7.2
        createDocumentWithFont("symbolicTtfCharEncodingsPdfA1Test05.pdf", "Symbols1.ttf", PdfEncodings.IDENTITY_H, PdfAConformanceLevel.PDF_A_1B);
    }

    @Test
    public void nonSymbolicTtfCharEncodingsPdfA1Test01() throws IOException, InterruptedException {
        // encoding must be either winansi or macroman, by default winansi is used
        createDocumentWithFont("nonSymbolicTtfCharEncodingsPdfA1Test01.pdf", "FreeSans.ttf", PdfEncodings.WINANSI, PdfAConformanceLevel.PDF_A_1B);
    }


    @Test
    public void nonSymbolicTtfCharEncodingsPdfA1Test02() throws IOException, InterruptedException {
        // encoding must be either winansi or macroman, by default winansi is used
        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> createDocumentWithFont("nonSymbolicTtfCharEncodingsPdfA1Test02.pdf", "FreeSans.ttf", "ISO-8859-1", PdfAConformanceLevel.PDF_A_2B)
        );
        Assert.assertEquals(PdfaExceptionMessageConstant.ALL_NON_SYMBOLIC_TRUE_TYPE_FONT_SHALL_SPECIFY_MAC_ROMAN_ENCODING_OR_WIN_ANSI_ENCODING,
                e.getMessage());
    }

    private void createDocumentWithFont(String outFileName, String fontFileName, String encoding, PdfAConformanceLevel conformanceLevel) throws IOException, InterruptedException {
        String outPdf = outputDir + outFileName;
        String cmpPdf = sourceFolder + "cmp/PdfAFontTest/cmp_" + outFileName;
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, conformanceLevel, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        PdfFont font = PdfFontFactory.createFont(sourceFolder + fontFileName,
                encoding, EmbeddingStrategy.FORCE_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 12)
                .showText("Hello World")
                .endText()
                .restoreState();


        doc.close();

        compareResult(outPdf, cmpPdf);
    }

    private void compareResult(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, outputDir, "diff_");
        if (result != null) {
            fail(result);
        }
    }

    private void generateAndValidatePdfA2WithCidFont(String fontFile, String outPdf) throws IOException {
        generateAndValidatePdfA2WithCidFont(fontFile, outPdf, null);
    }

    private void generateAndValidatePdfA2WithCidFont(String fontFile, String outPdf, String expectedVeraPdfWarning) throws IOException {
        try (PdfWriter writer = new PdfWriter(outPdf);
                InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
                PdfDocument doc = new PdfADocument(
                        writer,
                        PdfAConformanceLevel.PDF_A_2B,
                        new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is)
                )
        ) {


            PdfPage page = doc.addNewPage();
            // Identity-H must be embedded
            PdfFont font = PdfFontFactory.createFont(sourceFolder + fontFile,
                    "Identity-H", EmbeddingStrategy.FORCE_EMBEDDED);
            PdfCanvas canvas = new PdfCanvas(page);
            canvas.saveState()
                    .beginText()
                    .moveText(36, 700)
                    .setFontAndSize(font, 12)
                    .showText("Hello World")
                    .endText()
                    .restoreState();
        }
        Assert.assertEquals(expectedVeraPdfWarning, new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
    }
}
