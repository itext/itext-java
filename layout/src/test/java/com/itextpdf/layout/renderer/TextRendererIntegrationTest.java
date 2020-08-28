package com.itextpdf.layout.renderer;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class TextRendererIntegrationTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/TextRendererIntegrationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/TextRendererIntegrationTest/";
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/layout/fonts/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }


    @Test
    public void trimFirstJapaneseCharactersTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "trimFirstJapaneseCharacters.pdf";
        String cmpFileName = sourceFolder + "cmp_trimFirstJapaneseCharacters.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDocument);

        // UTF-8 encoding table and Unicode characters
        byte[] bUtf16A = {(byte) 0xd8, (byte) 0x40, (byte) 0xdc, (byte) 0x0b};

        // This String is U+2000B
        String strUtf16A = new String(bUtf16A, "UTF-16BE");

        PdfFont font = PdfFontFactory
                .createFont(fontsFolder + "NotoSansCJKjp-Bold.otf", PdfEncodings.IDENTITY_H);

        doc.add(new Paragraph(strUtf16A).setFont(font));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder));
    }
}
