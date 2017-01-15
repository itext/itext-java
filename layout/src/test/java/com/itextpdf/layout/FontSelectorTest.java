package com.itextpdf.layout;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.layout.property.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileOutputStream;

@Category(IntegrationTest.class)
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
        sel.addFont(fontsFolder + "Puritan2.otf");
        sel.addFont(fontsFolder + "NotoSans-Regular.ttf");
        sel.addFont(fontsFolder + "FreeSans.ttf");


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

    @Test
    public void cyrillicAndLatinGroup2() throws Exception {
        String outFileName = destinationFolder + "cyrillicAndLatinGroup2.pdf";
        String cmpFileName = sourceFolder + "cmp_cyrillicAndLatinGroup2.pdf";

        FontProvider sel = new FontProvider();
        sel.addFont(fontsFolder + "Puritan2.otf");
        sel.addFont(fontsFolder + "NotoSans-Regular.ttf");
        sel.addFont(fontsFolder + "FreeSans.ttf");


        String s = "Hello world! Здравствуй мир! Hello world! Здравствуй мир!";
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document doc = new Document(pdfDoc);

        doc.setFontProvider(sel);
        doc.setProperty(Property.FONT, "'Puritan', \"FreeSans\"");
        com.itextpdf.layout.element.Text text = new com.itextpdf.layout.element.Text(s).setBackgroundColor(Color.LIGHT_GRAY);
        com.itextpdf.layout.element.Paragraph paragraph = new com.itextpdf.layout.element.Paragraph(text);
        doc.add(paragraph);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void latinAndNotdefGroup() throws Exception {
        String outFileName = destinationFolder + "latinAndNotdefGroup.pdf";
        String cmpFileName = sourceFolder + "cmp_latinAndNotdefGroup.pdf";

        FontProvider sel = new FontProvider();
        sel.addFont(fontsFolder + "Puritan2.otf");

        String s = "Hello мир!";
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
