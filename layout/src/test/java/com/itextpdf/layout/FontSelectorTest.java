package com.itextpdf.layout;

import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.property.Property;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.FileOutputStream;

public class FontSelectorTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/FontSelectorTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/FontSelectorTest/";
    public static final String fontsFolder = "./src/test/resources/com/itextpdf/layout/fonts/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void cyrillicAndLatinGroup() throws Exception {
        String outFileName = destinationFolder + "cyrillicAndLatinGroup.pdf";
        String cmpFileName = sourceFolder + "cmp_cyrillicAndLatinGroup.pdf";

        FontProvider sel = new FontProvider();
        sel.addFont(PdfFontFactory.createFont(fontsFolder + "Puritan2.otf", PdfEncodings.IDENTITY_H, true));
        sel.addFont(PdfFontFactory.createFont(fontsFolder + "NotoSans-Regular.ttf", PdfEncodings.IDENTITY_H, true));


        String s = "Hello world! Здравствуй мир! Hello world! Здравствуй мир!";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);

        doc.setFontProvider(sel);
        doc.setProperty(Property.FONT, "Puritan");
        com.itextpdf.layout.element.Text text = new com.itextpdf.layout.element.Text(s).setBackgroundColor(Color.LIGHT_GRAY);
        com.itextpdf.layout.element.Paragraph paragraph = new com.itextpdf.layout.element.Paragraph(text);
        doc.add(paragraph);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
