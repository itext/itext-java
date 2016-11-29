package com.itextpdf.layout;

import com.itextpdf.kernel.color.DeviceRgb;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.Property;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class CollapsingMarginsTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/CollapsingMarginsTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/CollapsingMarginsTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void collapsingMarginsTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "collapsingMarginsTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_collapsingMarginsTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));


        drawPageBorders(pdfDocument, 4);

        String textByron =
                "When a man hath no freedom to fight for at home,\n" +
                        "    Let him combat for that of his neighbours;\n" +
                        "Let him think of the glories of Greece and of Rome,\n" +
                        "    And get knocked on the head for his labours.\n" +
                        "\n" +
                        "To do good to Mankind is the chivalrous plan,\n" +
                        "    And is always as nobly requited;\n" +
                        "Then battle for Freedom wherever you can,\n" +
                        "    And, if not shot or hanged, you'll get knighted.";

        Document doc = new Document(pdfDocument);
        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        doc.add(new Paragraph("marker text").setMargin(0));

        Paragraph p = new Paragraph(textByron);
        for (int i = 0; i < 5; i++) {
            p.add(textByron);
        }

        Div div1 = new Div();
        Div div2 = new Div();

        div1.add(p).setBackgroundColor(new DeviceRgb(65,151,29));
        div2.add(p).setBackgroundColor(new DeviceRgb(209,247,29));

        div1.setMarginBottom(20);
        div2.setMarginTop(150);
        div2.setMarginBottom(150);

        Div div = new Div().setMarginTop(20).setMarginBottom(10).setBackgroundColor(new DeviceRgb(78,151,205));
        div.add(div1);
        div.add(div2);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void collapsingMarginsTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "collapsingMarginsTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_collapsingMarginsTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));


        drawPageBorders(pdfDocument, 3);

        String textByron =
                "When a man hath no freedom to fight for at home,\n" +
                        "    Let him combat for that of his neighbours;\n" +
                        "Let him think of the glories of Greece and of Rome,\n" +
                        "    And get knocked on the head for his labours.\n" +
                        "\n" +
                        "To do good to Mankind is the chivalrous plan,\n" +
                        "    And is always as nobly requited;\n" +
                        "Then battle for Freedom wherever you can,\n" +
                        "    And, if not shot or hanged, you'll get knighted.";

        Document doc = new Document(pdfDocument);
        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        doc.add(new Paragraph("marker text").setMargin(0));

        Paragraph p = new Paragraph(textByron);
        for (int i = 0; i < 3; i++) {
            p.add(textByron);
        }
        p.add("When a man hath no freedom to fight for at home,\n" +
                "    Let him combat for that of his neighbours;\n" +
                "Let him think of the glories of Greece and of Rome,\n" +
                "    And get knocked on the head for his labours.\n" +
                "\n" +
                "To do good to Mankind is the chivalrous plan,\n");

        Div div1 = new Div();
        Div div2 = new Div();

        div1.add(p).setBackgroundColor(new DeviceRgb(65,151,29));
        div2.add(p).setBackgroundColor(new DeviceRgb(209,247,29));

        div1.setMarginBottom(40);
        div2.setMarginTop(20);
        div2.setMarginBottom(150);

        Div div = new Div().setMarginTop(20).setMarginBottom(10).setBackgroundColor(new DeviceRgb(78,151,205));
        div.add(div1);
        div.add(div2);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void collapsingMarginsTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "collapsingMarginsTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_collapsingMarginsTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));


        drawPageBorders(pdfDocument, 3);

        String textByron =
                "When a man hath no freedom to fight for at home,\n" +
                        "    Let him combat for that of his neighbours;\n" +
                        "Let him think of the glories of Greece and of Rome,\n" +
                        "    And get knocked on the head for his labours.\n" +
                        "\n" +
                        "To do good to Mankind is the chivalrous plan,\n" +
                        "    And is always as nobly requited;\n" +
                        "Then battle for Freedom wherever you can,\n" +
                        "    And, if not shot or hanged, you'll get knighted.";

        Document doc = new Document(pdfDocument);
        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        doc.add(new Paragraph("marker text").setMargin(0));

        Paragraph p = new Paragraph(textByron);
        for (int i = 0; i < 3; i++) {
            p.add(textByron);
        }
        p.add("When a man hath no freedom to fight for at home,\n" +
                "    Let him combat for that of his neighbours;\n" +
                "Let him think of the glories of Greece and of Rome,\n" +
                "    And get knocked on the head for his labours.\n" +
                "To do good to Mankind is the chivalrous plan,\n");

        Div div1 = new Div();
        Div div2 = new Div();

        div1.add(p).setBackgroundColor(new DeviceRgb(65,151,29));
        div2.add(p).setBackgroundColor(new DeviceRgb(209,247,29));

        div1.setMarginBottom(80);
        div2.setMarginTop(80);
        div2.setMarginBottom(150);

        doc.add(div1);
        doc.add(div2);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void collapsingMarginsTest04() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "collapsingMarginsTest04.pdf";
        String cmpFileName = sourceFolder + "cmp_collapsingMarginsTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));


        drawPageBorders(pdfDocument, 3);

        String textByron =
                "When a man hath no freedom to fight for at home,\n" +
                        "    Let him combat for that of his neighbours;\n" +
                        "Let him think of the glories of Greece and of Rome,\n" +
                        "    And get knocked on the head for his labours.\n" +
                        "\n" +
                        "To do good to Mankind is the chivalrous plan,\n" +
                        "    And is always as nobly requited;\n" +
                        "Then battle for Freedom wherever you can,\n" +
                        "    And, if not shot or hanged, you'll get knighted.";

        Document doc = new Document(pdfDocument);
        doc.setProperty(Property.COLLAPSING_MARGINS, true);

        doc.add(new Paragraph("marker text").setMargin(0));

        Paragraph p = new Paragraph(textByron);
        for (int i = 0; i < 3; i++) {
            p.add(textByron);
        }
        p.add("When a man hath no freedom to fight for at home,\n" +
                "    Let him combat for that of his neighbours;\n" +
                "Let him think of the glories of Greece and of Rome,\n" +
                "    And get knocked on the head for his labours.\n" +
                "To do good to Mankind is the chivalrous plan,\n");
        p.add(new Text("small text").setFontSize(6));
        p.add(
                "\nAnd is always as nobly requited;\n" +
                "Then battle for Freedom wherever you can,\n" +
                "And, if not shot or hanged, you'll get knighted.");

        Div div1 = new Div();
        Div div2 = new Div();

        div1.add(p).setBackgroundColor(new DeviceRgb(65,151,29));
        div2.add(p).setBackgroundColor(new DeviceRgb(209,247,29));

        div1.setMarginBottom(80);
        div2.setMarginTop(80);
        div2.setMarginBottom(150);

        doc.add(div1);
        doc.add(div2);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private void drawPageBorders(PdfDocument pdfDocument, int pageNum) {
        for (int i = 1; i <= pageNum; ++i) {
            while (pdfDocument.getNumberOfPages() < i) {
                pdfDocument.addNewPage();
            }
            PdfCanvas canvas = new PdfCanvas(pdfDocument.getPage(i));
            canvas.saveState();
            canvas.setLineDash(5, 10);
            canvas.rectangle(36, 36, 595 - 36 * 2, 842 - 36 * 2);
            canvas.stroke();
            canvas.restoreState();
        }
    }
}
