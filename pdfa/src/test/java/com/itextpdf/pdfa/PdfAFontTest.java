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
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.font.FontEncoding;
import com.itextpdf.io.font.FontProgramFactory;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.font.PdfType1Font;
import com.itextpdf.kernel.font.PdfType3Font;
import com.itextpdf.kernel.font.Type3Glyph;
import com.itextpdf.kernel.pdf.CompressionConstants;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.pdfa.exceptions.PdfAConformanceException;
import com.itextpdf.pdfa.exceptions.PdfaExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.junit.Assert.fail;

@Category(IntegrationTest.class)
public class PdfAFontTest extends ExtendedITextTest {

    static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/pdfa/";
    static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/pdfa/PdfAFontTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void fontCheckPdfA1_01() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA1b_fontCheckPdfA1_01.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_pdfA1b_fontCheckPdfA1_01.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
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
        compareResult(outPdf, cmpPdf, null);
    }

    @Test
    public void fontCheckPdfA1_02() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf", PdfEncodings.WINANSI,
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
        String outPdf = DESTINATION_FOLDER + "pdfA1b_fontCheckPdfA1_03.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_pdfA1b_fontCheckPdfA1_03.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
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
        compareResult(outPdf, cmpPdf, null);
    }

    @Test
    public void fontCheckPdfA1_04() throws IOException {
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
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
        String outPdf = DESTINATION_FOLDER + "pdfA1b_fontCheckPdfA1_05.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_pdfA1b_fontCheckPdfA1_05.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "NotoSansCJKtc-Light.otf", "Identity-H");
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
        compareResult(outPdf, cmpPdf, null);
    }

    @Test
    public void fontCheckPdfA2_01() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA2b_fontCheckPdfA2_01.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_pdfA2b_fontCheckPdfA2_01.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
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
        compareResult(outPdf, cmpPdf, null);
    }

    @Test
    public void fontCheckPdfA3_01() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA3b_fontCheckPdfA3_01.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_pdfA3b_fontCheckPdfA3_01.pdf";
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
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
        compareResult(outPdf, cmpPdf, null);
    }

    @Test
    public void cidFontCheckTest1() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA2b_cidFontCheckTest1.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_pdfA2b_cidFontCheckTest1.pdf";
        generatePdfA2WithCidFont("FreeSans.ttf", outPdf);
        compareResult(outPdf, cmpPdf, null);
    }

    @Test
    public void cidFontCheckTest2() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA2b_cidFontCheckTest2.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_pdfA2b_cidFontCheckTest2.pdf";
        String expectedVeraPdfWarning = "The following warnings and errors were logged during validation:\n"
                + "WARNING: The Top DICT does not begin with ROS operator";

        generatePdfA2WithCidFont("Puritan2.otf", outPdf);
        compareResult(outPdf, cmpPdf, expectedVeraPdfWarning);
    }

    @Test
    public void cidFontCheckTest3() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA2b_cidFontCheckTest3.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_pdfA2b_cidFontCheckTest3.pdf";
        generatePdfA2WithCidFont("NotoSansCJKtc-Light.otf", outPdf);
        compareResult(outPdf, cmpPdf, null);
    }

    @Test
    public void symbolicTtfCharEncodingsPdfA1Test01() {
        // encoding must not be specified
        // Here we produced valid pdfa files in the past by silently removing not valid symbols
        // But right now we check for used glyphs which don't exist in the font and throw exception
        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> createDocumentWithFont("symbolicTtfCharEncodingsPdfA1Test01.pdf", "Symbols1.ttf", "", PdfAConformanceLevel.PDF_A_1B)
        );
        Assert.assertEquals(PdfaExceptionMessageConstant.EMBEDDED_FONTS_SHALL_DEFINE_ALL_REFERENCED_GLYPHS,
                e.getMessage());
    }

    @Test
    public void symbolicTtfCharEncodingsPdfA1Test02() {
        // if you specify encoding, symbolic font is treated as non-symbolic
        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> createDocumentWithFont("symbolicTtfCharEncodingsPdfA1Test02.pdf", "Symbols1.ttf", PdfEncodings.MACROMAN, PdfAConformanceLevel.PDF_A_1B)
        );
        Assert.assertEquals(PdfaExceptionMessageConstant.EMBEDDED_FONTS_SHALL_DEFINE_ALL_REFERENCED_GLYPHS,
                e.getMessage());
    }

    @Test
    public void symbolicTtfCharEncodingsPdfA1Test03() {
        // if you specify encoding, symbolic font is treated as non-symbolic
        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> createDocumentWithFont("symbolicTtfCharEncodingsPdfA1Test03.pdf", "Symbols1.ttf", "ISO-8859-1", PdfAConformanceLevel.PDF_A_1B)
        );
        Assert.assertEquals(PdfaExceptionMessageConstant.EMBEDDED_FONTS_SHALL_DEFINE_ALL_REFERENCED_GLYPHS,
                e.getMessage());
    }

    @Test
    public void symbolicTtfCharEncodingsPdfA1Test04() {
        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> createDocumentWithFont("symbolicTtfCharEncodingsPdfA1Test04.pdf", "Symbols1.ttf", PdfEncodings.WINANSI, PdfAConformanceLevel.PDF_A_1B)
        );
        Assert.assertEquals(PdfaExceptionMessageConstant.EMBEDDED_FONTS_SHALL_DEFINE_ALL_REFERENCED_GLYPHS,
                e.getMessage());
    }

    @Test
    public void symbolicTtfCharEncodingsPdfA1Test05() {
        // Identity-H behaviour should be the same as the default one, starting from 7.2
        // Here we produced valid pdfa files in the past by silently removing not valid symbols
        // But right now we check for used glyphs which don't exist in the font and throw exception
        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> createDocumentWithFont("symbolicTtfCharEncodingsPdfA1Test05.pdf", "Symbols1.ttf", PdfEncodings.IDENTITY_H, PdfAConformanceLevel.PDF_A_1B)
        );
        Assert.assertEquals(PdfaExceptionMessageConstant.EMBEDDED_FONTS_SHALL_DEFINE_ALL_REFERENCED_GLYPHS,
                e.getMessage());
    }

    @Test
    public void nonSymbolicTtfCharEncodingsPdfA1Test01() throws IOException, InterruptedException {
        // encoding must be either winansi or macroman, by default winansi is used
        createDocumentWithFont("nonSymbolicTtfCharEncodingsPdfA1Test01.pdf", "FreeSans.ttf", PdfEncodings.WINANSI, PdfAConformanceLevel.PDF_A_1B);
    }


    @Test
    public void nonSymbolicTtfCharEncodingsPdfA1Test02() {
        // encoding must be either winansi or macroman, by default winansi is used
        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> createDocumentWithFont("nonSymbolicTtfCharEncodingsPdfA1Test02.pdf", "FreeSans.ttf", "ISO-8859-1", PdfAConformanceLevel.PDF_A_2B)
        );
        Assert.assertEquals(PdfaExceptionMessageConstant.ALL_NON_SYMBOLIC_TRUE_TYPE_FONT_SHALL_SPECIFY_MAC_ROMAN_ENCODING_OR_WIN_ANSI_ENCODING,
                e.getMessage());
    }

    @Test
    public void notdefInTrueTypeFontTest() throws IOException {
        String outPdf = DESTINATION_FOLDER + "notdefInTrueTypeFont.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "# simple 32 0020 00C5 1987", EmbeddingStrategy.PREFER_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 786).
                setFontAndSize(font, 36);

        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> canvas.showText("\u00C5 \u1987")
        );
        Assert.assertEquals(PdfaExceptionMessageConstant.EMBEDDED_FONTS_SHALL_DEFINE_ALL_REFERENCED_GLYPHS,
                e.getMessage());
    }

    @Test
    public void notdefFontTest2() throws IOException {
        String outPdf = DESTINATION_FOLDER + "notdefFontTest2.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "NotoSans-Regular.ttf",
                "", EmbeddingStrategy.PREFER_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 786).
                setFontAndSize(font, 36);

        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> canvas.showText("\u898B\u7A4D\u3082\u308A")
        );
        Assert.assertEquals(PdfaExceptionMessageConstant.EMBEDDED_FONTS_SHALL_DEFINE_ALL_REFERENCED_GLYPHS,
                e.getMessage());
    }

    @Test
    public void glyphLineWithUndefinedGlyphsTest() throws Exception {
        String outPdf = DESTINATION_FOLDER + "glyphLineWithUndefinedGlyphs.pdf";

        InputStream icm = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        Document document = new Document(new PdfADocument(
                new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)),
                PdfAConformanceLevel.PDF_A_4,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB ICC preference", icm)));

        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "NotoSans-Regular.ttf",
                "", EmbeddingStrategy.PREFER_EMBEDDED);
        Paragraph p = new Paragraph("\u898B\u7A4D\u3082\u308A");
        p.setFont(font);

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> document.add(p));
        Assert.assertEquals(PdfaExceptionMessageConstant.EMBEDDED_FONTS_SHALL_DEFINE_ALL_REFERENCED_GLYPHS,
                e.getMessage());
    }

    @Test
    public void pdfArrayWithUndefinedGlyphsTest() throws Exception {
        String outPdf = DESTINATION_FOLDER + "pdfArrayWithUndefinedGlyphs.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "NotoSans-Regular.ttf",
                "", EmbeddingStrategy.PREFER_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        canvas.
                saveState().
                beginText().
                moveText(36, 786).
                setFontAndSize(font, 36);

        PdfArray pdfArray = new PdfArray();
        pdfArray.add(new PdfString("ABC"));
        pdfArray.add(new PdfNumber(1));
        pdfArray.add(new PdfString("\u898B\u7A4D\u3082\u308A"));
        Exception e = Assert.assertThrows(PdfAConformanceException.class,
                () -> canvas.showText(pdfArray)
        );
        Assert.assertEquals(PdfaExceptionMessageConstant.EMBEDDED_FONTS_SHALL_DEFINE_ALL_REFERENCED_GLYPHS,
                e.getMessage());
    }

    @Test
    public void createDocumentWithType1FontAfmTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "DocumentWithCMR10Afm.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_DocumentWithCMR10Afm.pdf";

        PdfWriter writer = new PdfWriter(outPdf, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0));
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument pdfDoc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));

        PdfFont pdfType1Font = PdfFontFactory.createFont(FontProgramFactory.createType1Font(
                        SOURCE_FOLDER + "cmr10.afm", SOURCE_FOLDER + "cmr10.pfb"),
                FontEncoding.FONT_SPECIFIC, EmbeddingStrategy.PREFER_EMBEDDED);
        Assert.assertTrue("PdfType1Font expected", pdfType1Font instanceof PdfType1Font);

        new PdfCanvas(pdfDoc.addNewPage())
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfType1Font, 72)
                .showText("\u0000\u0001\u007cHello world")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill();

        byte[] afm = StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "cmr10.afm"));
        byte[] pfb = StreamUtil.inputStreamToArray(FileUtil.getInputStreamForFile(SOURCE_FOLDER + "cmr10.pfb"));
        pdfType1Font = PdfFontFactory.createFont(FontProgramFactory.createType1Font(afm, pfb),
                FontEncoding.FONT_SPECIFIC, EmbeddingStrategy.PREFER_EMBEDDED);
        Assert.assertTrue("PdfType1Font expected", pdfType1Font instanceof PdfType1Font);

        new PdfCanvas(pdfDoc.addNewPage())
                .saveState()
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(pdfType1Font, 72)
                .showText("\u0000\u0001\u007cHello world")
                .endText()
                .restoreState()
                .rectangle(100, 500, 100, 100).fill();

        pdfDoc.close();

        compareResult(outPdf, cmpPdf, null);
    }

    @Test
    public void checkPdfA4FreeSansForceEmbeddedTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "PdfA4FreeSansForceEmbeddedTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_PdfA4FreeSansForceEmbeddedTest.pdf";
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfWriter writer = new PdfWriter(outPdf, writerProperties);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-4")
                .endText()
                .restoreState();
        doc.close();
        compareResult(outPdf, cmpPdf, null);
    }

    @Test
    public void checkPdfA4FreeSansForceNotEmbeddedTest() throws IOException {
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), writerProperties);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf", PdfEncodings.WINANSI,
                EmbeddingStrategy.FORCE_NOT_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-4")
                .endText()
                .restoreState();

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfAConformanceException.ALL_THE_FONTS_MUST_BE_EMBEDDED_THIS_ONE_IS_NOT_0, "FreeSans"),
                e.getMessage());
    }

    @Test
    public void checkPdfA4FreeSansPreferNotEmbeddedTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "PdfA4FreeSansPreferNotEmbeddedTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_PdfA4FreeSansPreferNotEmbeddedTest.pdf";
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfWriter writer = new PdfWriter(outPdf, writerProperties);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                "Identity-H", EmbeddingStrategy.PREFER_NOT_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-4")
                .endText()
                .restoreState();

        doc.close();
        compareResult(outPdf, cmpPdf, null);
    }

    @Test
    public void checkPdfA4HelveticaPreferEmbeddedTest() throws IOException {
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream(), writerProperties);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFontFactory.createFont("Helvetica",
                "WinAnsi", EmbeddingStrategy.PREFER_EMBEDDED);
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-4")
                .endText()
                .restoreState();

        Exception e = Assert.assertThrows(PdfAConformanceException.class, () -> doc.close());
        Assert.assertEquals(MessageFormatUtil.format(PdfAConformanceException.ALL_THE_FONTS_MUST_BE_EMBEDDED_THIS_ONE_IS_NOT_0, "Helvetica"),
                e.getMessage());
    }

    @Test
    public void checkPdfA4NotoSansCJKtcLightTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4NotoSansCJKtcLightTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_pdfA4NotoSansCJKtcLightTest.pdf";
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfWriter writer = new PdfWriter(outPdf, writerProperties);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "NotoSansCJKtc-Light.otf", "Identity-H");
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("你好世界! Pdf/A-4")
                .endText()
                .restoreState();

        doc.close();
        compareResult(outPdf, cmpPdf, null);
    }

    @Test
    public void checkPdfA4Puritan2Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4Puritan2Test.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_pdfA4Puritan2Test.pdf";
        String expectedVeraPdfWarning = "The following warnings and errors were logged during validation:\n"
                + "WARNING: The Top DICT does not begin with ROS operator";
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfWriter writer = new PdfWriter(outPdf, writerProperties);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        // Identity-H must be embedded
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "Puritan2.otf", "Identity-H");
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-4")
                .endText()
                .restoreState();

        doc.close();
        compareResult(outPdf, cmpPdf, expectedVeraPdfWarning);
    }

    @Test
    public void checkPdfA4Type3Test() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4Type3Test.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_pdfA4Type3Test.pdf";

        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfWriter writer = new PdfWriter(outPdf, writerProperties);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));

        // A A A A E E E ~ é
        String testString = "A A A A E E E ~ \u00E9";

        //writing type3 font characters
        writer.setCompressionLevel(CompressionConstants.NO_COMPRESSION);

        PdfType3Font type3 = PdfFontFactory.createType3Font(doc, false);
        PdfDictionary charProcs = new PdfDictionary();
        type3.getPdfObject().put(PdfName.CharProcs, charProcs);

        Type3Glyph a = type3.addGlyph('A', 600, 0, 0, 600, 700);
        a.setLineWidth(100);
        a.moveTo(5, 5);
        a.lineTo(300, 695);
        a.lineTo(595, 5);
        a.closePathFillStroke();

        Type3Glyph space = type3.addGlyph(' ', 600, 0, 0, 600, 700);
        space.setLineWidth(10);
        space.closePathFillStroke();

        Type3Glyph e = type3.addGlyph('E', 600, 0, 0, 600, 700);
        e.setLineWidth(100);
        e.moveTo(595, 5);
        e.lineTo(5, 5);
        e.lineTo(300, 350);
        e.lineTo(5, 695);
        e.lineTo(595, 695);
        e.stroke();

        Type3Glyph tilde = type3.addGlyph('~', 600, 0, 0, 600, 700);
        tilde.setLineWidth(100);
        tilde.moveTo(595, 5);
        tilde.lineTo(5, 5);
        tilde.stroke();

        Type3Glyph symbol233 = type3.addGlyph('\u00E9', 600, 0, 0, 600, 700);
        symbol233.setLineWidth(100);
        symbol233.moveTo(540, 5);
        symbol233.lineTo(5, 340);
        symbol233.stroke();

        PdfPage page = doc.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .setFontAndSize(type3, 12)
                .moveText(50, 800)
                .showText(testString)
                .endText();
        page.flush(true);

        doc.close();

        compareResult(outPdf, cmpPdf, null);
    }

    @Test
    public void checkPdfA4UmingTtcTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4UmingTtcTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_pdfA4UmingTtcTest.pdf";
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfWriter writer = new PdfWriter(outPdf, writerProperties);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFontFactory.createTtcFont(SOURCE_FOLDER + "uming.ttc", 0, "Identity-H", EmbeddingStrategy.FORCE_EMBEDDED, false);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-4")
                .endText()
                .restoreState();

        doc.close();
        compareResult(outPdf, cmpPdf, null);
    }

    @Test
    public void checkPdfA4WoffTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "pdfA4WoffTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_pdfA4WoffTest.pdf";
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfWriter writer = new PdfWriter(outPdf, writerProperties);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "SourceSerif4-Black.woff", "Identity-H", EmbeddingStrategy.FORCE_EMBEDDED);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("Hello World! Pdf/A-4")
                .endText()
                .restoreState();

        doc.close();
        compareResult(outPdf, cmpPdf, null);
    }

    @Test
    public void checkPdfA4SurrogatePairTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "PdfA4SurrogatePairTest.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_PdfA4SurrogatePairTest.pdf";
        WriterProperties writerProperties = new WriterProperties();
        writerProperties.setPdfVersion(PdfVersion.PDF_2_0);
        PdfWriter writer = new PdfWriter(outPdf, writerProperties);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_4, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "NotoEmoji-Regular.ttf", "Identity-H", EmbeddingStrategy.FORCE_EMBEDDED);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .setFillColor(ColorConstants.GREEN)
                .beginText()
                .moveText(36, 700)
                .setFontAndSize(font, 36)
                .showText("\uD83D\uDC7B \uD83D\uDE09")
                .endText()
                .restoreState();

        doc.close();
        compareResult(outPdf, cmpPdf, null);
    }

    private void createDocumentWithFont(String outFileName, String fontFileName, String encoding, PdfAConformanceLevel conformanceLevel) throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + outFileName;
        String cmpPdf = SOURCE_FOLDER + "cmp/PdfAFontTest/cmp_" + outFileName;
        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
        PdfDocument doc = new PdfADocument(writer, conformanceLevel, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        PdfPage page = doc.addNewPage();

        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + fontFileName,
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

        compareResult(outPdf, cmpPdf, null);
    }

    private void compareResult(String outPdf, String cmpPdf, String expectedVeraPdfWarning) throws IOException, InterruptedException {
        Assert.assertEquals(expectedVeraPdfWarning, new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_");
        if (result != null) {
            fail(result);
        }
    }


    private void generatePdfA2WithCidFont(String fontFile, String outPdf) throws IOException {
        try (PdfWriter writer = new PdfWriter(outPdf);
                InputStream is = FileUtil.getInputStreamForFile(SOURCE_FOLDER + "sRGB Color Space Profile.icm");
                PdfDocument doc = new PdfADocument(
                        writer,
                        PdfAConformanceLevel.PDF_A_2B,
                        new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is)
                )
        ) {


            PdfPage page = doc.addNewPage();
            // Identity-H must be embedded
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + fontFile,
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
    }
}
