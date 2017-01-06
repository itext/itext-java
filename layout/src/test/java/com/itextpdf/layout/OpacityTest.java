package com.itextpdf.layout;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvasConstants;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.border.DoubleBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class OpacityTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/OpacityTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/OpacityTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void backgroundOpacityTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "backgroundOpacityTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_backgroundOpacityTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        DeviceRgb darkBlue = new DeviceRgb(32, 80, 129);
        Div div = new Div().setBackgroundColor(darkBlue).setHeight(200);
        div.add(new Paragraph("Simple text inside of the div with transparent (0.0) background.").setBackgroundColor(Color.RED, 0f));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.3) background.").setBackgroundColor(Color.RED, 0.3f));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.5) background.").setBackgroundColor(Color.RED, 0.5f));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.7) background.").setBackgroundColor(Color.RED, 0.7f));
        div.add(new Paragraph("Simple text inside of the div with transparent (1.0) background.").setBackgroundColor(Color.RED, 1f));
        div.add(new Paragraph("Simple text inside of the div with background.").setBackgroundColor(Color.RED));

        document.add(div);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
    
    @Test
    public void borderOpacityTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "borderOpacityTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_borderOpacityTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        DeviceRgb darkBlue = new DeviceRgb(32, 80, 129);
        Div div = new Div().setBackgroundColor(darkBlue).setHeight(300);
        div.add(new Paragraph("Simple text inside of the div with transparent (0.0) border.").setBorder(new DoubleBorder(Color.RED, 7f, 0.0f)));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.3) border.").setBorder(new DoubleBorder(Color.RED, 7f, 0.3f)));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.5) border.").setBorder(new DoubleBorder(Color.RED, 7f, 0.5f)));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.7) border.").setBorder(new DoubleBorder(Color.RED, 7f, 0.7f)));
        div.add(new Paragraph("Simple text inside of the div with transparent (1.0) border.").setBorder(new DoubleBorder(Color.RED, 7f, 1.0f)));
        div.add(new Paragraph("Simple text inside of the div with border.").setBorder(new DoubleBorder(Color.RED, 7f)));

        document.add(div);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
    
    @Test
    public void textOpacityTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textOpacityTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_textOpacityTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        DeviceRgb darkBlue = new DeviceRgb(32, 80, 129);
        Div div = new Div().setBackgroundColor(darkBlue).setHeight(300);
        div.add(new Paragraph("Simple text inside of the div with transparent (0.0) text.").setFontColor(Color.RED, 0f));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.3) text.").setFontColor(Color.RED, 0.3f));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.5) text.").setFontColor(Color.RED, 0.5f));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.7) text.").setFontColor(Color.RED, 0.7f));
        div.add(new Paragraph("Simple text inside of the div with transparent (1.0) text.").setFontColor(Color.RED, 1f));
        div.add(new Paragraph("Simple text inside of the div with text.").setFontColor(Color.RED));
        
        document.add(div);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
    
    @Test
    public void underlineOpacityTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "underlineOpacityTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_underlineOpacityTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        DeviceRgb darkBlue = new DeviceRgb(32, 80, 129);
        Div div = new Div().setBackgroundColor(darkBlue).setHeight(300);
        div.add(new Paragraph("Simple text inside of the div with transparent (0.0) underline.").setUnderline(Color.RED, 0.0f, .75f, 0, 0, -1 / 8f, PdfCanvasConstants.LineCapStyle.BUTT));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.3) underline.").setUnderline(Color.RED, 0.3f, .75f, 0, 0, -1 / 8f, PdfCanvasConstants.LineCapStyle.BUTT));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.5) underline.").setUnderline(Color.RED, 0.5f, .75f, 0, 0, -1 / 8f, PdfCanvasConstants.LineCapStyle.BUTT));
        div.add(new Paragraph("Simple text inside of the div with transparent (0.7) underline.").setUnderline(Color.RED, 0.7f, .75f, 0, 0, -1 / 8f, PdfCanvasConstants.LineCapStyle.BUTT));
        div.add(new Paragraph("Simple text inside of the div with transparent (1.0) underline.").setUnderline(Color.RED, 1.0f, .75f, 0, 0, -1 / 8f, PdfCanvasConstants.LineCapStyle.BUTT));
        div.add(new Paragraph("Simple text inside of the div with underline.").setUnderline(Color.RED, .75f, 0, 0, -1 / 8f, PdfCanvasConstants.LineCapStyle.BUTT));
        
        document.add(div);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
