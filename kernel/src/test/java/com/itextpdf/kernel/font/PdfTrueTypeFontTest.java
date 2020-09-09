package com.itextpdf.kernel.font;

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
        String filePath = SOURCE_FOLDER + "trueTypeFontWithStandardFontProgram.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(filePath));

        PdfDictionary fontDict = pdfDocument.getPage(1).getResources().getResource(PdfName.Font).getAsDictionary(new PdfName("F1"));
        PdfFont pdfFont = PdfFontFactory.createFont(fontDict);

        Assert.assertEquals(542, pdfFont.getFontProgram().getAvgWidth());
        Assert.assertEquals(556, pdfFont.getGlyph('a').getWidth());
    }

}
