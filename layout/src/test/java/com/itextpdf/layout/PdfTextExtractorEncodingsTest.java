package com.itextpdf.layout;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.parser.PdfTextExtractor;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfTextExtractorEncodingsTest extends ExtendedITextTest {

    private static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/PdfTextExtractorEncodingsTest/";

    /**
     * Basic Latin characters, with Unicode values less than 128
     */
    private static final String TEXT1 = "AZaz09*!";
    /**
     * Latin-1 characters
     */
    private static final String TEXT2 = "\u0027\u0060\u00a4\u00a6";

    /**
     * Test parsing a document which uses a standard non-embedded font.
     *
     * @throws Exception any exception will cause the test to fail
     */
    @Test
    public void testStandardFont() throws Exception {
        PdfFont font = PdfFontFactory.createFont(FontConstants.TIMES_ROMAN);
        byte[] pdfBytes = createPdf(font);
        checkPdf(pdfBytes);
    }

    /**
     * Test parsing a document which uses a font encoding which creates a /Differences
     * PdfArray in the PDF.
     *
     * @throws Exception any exception will cause the test to fail
     */
    @Test
    public void testEncodedFont() throws Exception {
        PdfFont font = getTTFont("ISO-8859-1", true);
        byte[] pdfBytes = createPdf(font);
        checkPdf(pdfBytes);
    }

    /**
     * Test parsing a document which uses a Unicode font encoding which creates a /ToUnicode
     * PdfArray.
     *
     * @throws Exception any exception will cause the test to fail
     */
    @Test
    public void testUnicodeFont() throws Exception {
        PdfFont font = getTTFont(PdfEncodings.IDENTITY_H, true);
        byte[] pdfBytes = createPdf(font);
        checkPdf(pdfBytes);
    }

    private void checkPdf(byte[] pdfBytes) throws Exception {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(pdfBytes)));
        // Characters from http://unicode.org/charts/PDF/U0000.pdf
        Assert.assertEquals(TEXT1, PdfTextExtractor.getTextFromPage(pdfDocument.getPage(1)));
        // Characters from http://unicode.org/charts/PDF/U0080.pdf
        Assert.assertEquals(TEXT2, PdfTextExtractor.getTextFromPage(pdfDocument.getPage(2)));
    }

    protected static PdfFont getTTFont(String encoding, boolean embedded) throws IOException {
        return PdfFontFactory.createFont(sourceFolder + "FreeSans.ttf", encoding, embedded);
    }

    private static byte[] createPdf(PdfFont font)
            throws Exception {
        ByteArrayOutputStream byteStream = new ByteArrayOutputStream();

        Document document = new Document(new PdfDocument(new PdfWriter(byteStream)));
        document.add(new Paragraph(TEXT1).setFont(font));
        document.add(new AreaBreak());
        document.add(new Paragraph(TEXT2).setFont(font));
        document.close();

        return byteStream.toByteArray();
    }
}
