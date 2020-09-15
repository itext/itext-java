package com.itextpdf.kernel.font;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfTrueTypeFontTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/kernel/font/PdfTrueTypeFontTest/";

    @Test
    public void testReadingPdfTrueTypeFontWithType1StandardFontProgram() throws IOException {
        // We deliberately use an existing PDF in this test and not simplify the test to create the
        // PDF object structure on the fly to be able to easily inspect the PDF with other processors
        String filePath = SOURCE_FOLDER + "trueTypeFontWithStandardFontProgram.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(filePath));

        PdfDictionary fontDict = pdfDocument.getPage(1).getResources().getResource(PdfName.Font).getAsDictionary(new PdfName("F1"));
        PdfFont pdfFont = PdfFontFactory.createFont(fontDict);

        Assert.assertEquals(542, pdfFont.getFontProgram().getAvgWidth());
        Assert.assertEquals(556, pdfFont.getGlyph('a').getWidth());
    }

    @Test
    public void isBuiltInTest() {
        PdfFont font = PdfFontFactory.createFont(createTrueTypeFontDictionaryWithStandardHelveticaFont());
        Assert.assertTrue(font instanceof PdfTrueTypeFont);
        Assert.assertTrue(((PdfTrueTypeFont) font).isBuiltInFont());
    }

    @Test
    public void isNotBuiltInTest() throws IOException {
        PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "NotoSans-Regular_v.1.8.2.ttf");
        Assert.assertTrue(font instanceof PdfTrueTypeFont);
        Assert.assertFalse(((PdfTrueTypeFont) font).isBuiltInFont());
    }

    private static PdfDictionary createTrueTypeFontDictionaryWithStandardHelveticaFont() {
        PdfDictionary fontDictionary = new PdfDictionary();
        fontDictionary.put(PdfName.Type, PdfName.Font);
        fontDictionary.put(PdfName.Subtype, PdfName.TrueType);
        fontDictionary.put(PdfName.Encoding, PdfName.WinAnsiEncoding);
        fontDictionary.put(PdfName.BaseFont, new PdfName(StandardFonts.HELVETICA));
        return fontDictionary;
    }

}
