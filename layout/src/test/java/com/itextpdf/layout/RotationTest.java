package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.List;
import com.itextpdf.layout.element.ListItem;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class RotationTest extends ExtendedITextTest{
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/RotationTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/RotationTest/";
    public static final String cmpPrefix = "cmp_";

    private static final String para1Text = "The first published account of what would evolve into the Mafia in the United States came in the spring of 1869. " +
            "The New Orleans Times reported that the city's Second District had become overrun by \"well-known and notorious Sicilian murderers, " +
            "counterfeiters and burglars, who, in the last month, have formed a sort of general co-partnership or stock company for the plunder " +
            "and disturbance of the city.\" Emigration from southern Italy to the Americas was primarily to Brazil and Argentina, and New Orleans " +
            "had a heavy volume of port traffic to and from both locales.";
    private static final String para2Text = "Mafia groups in the United States first became influential in the New York City area, gradually progressing from small neighborhood" +
            " operations in Italian ghettos to citywide and eventually national organizations. The Black Hand was a name given to an extortion method used " +
            "in Italian neighborhoods at the turn of the 20th century. It has been sometimes mistaken for the Mafia itself, which it is not. Although the Black" +
            " Hand was a criminal society, there were many small Black Hand gangs.";
    private static final String para3Text = "From the 1890s to the 1900s (decade) in New York City, the Sicilian Mafia developed into the Five Points Gang and were very powerful in the" +
            " Little Italy of the Lower East Side. They were often in conflict with the Jewish Eastmans of the same area.";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void fixedTextRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedTextRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "fixedTextRotationTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        SolidBorder border = new SolidBorder(0.5f);
        int x1 = 350;
        int y1 = 600;
        int width1 = 100;
        document.add(new Paragraph("text to be rotatedg").setRotationAngle((Math.PI / 6)).setFixedPosition(x1, y1, width1)
                .setBorder(border));
        document.add(new Paragraph("text to be rotatedg").setFixedPosition(x1, y1, width1)
                .setBorder(border));

        String longText = "loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooong text";
        int x2 = 50;
        int y2 = 300;
        int width2 = 450;
        document.add(new Paragraph(longText).setRotationAngle((Math.PI / 6)).setFixedPosition(x2, y2, width2));
        document.add(new Paragraph(longText).setFixedPosition(x2, y2, width2));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fixedTextRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedTextRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "fixedTextRotationTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        String longText = "loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooo" +
                "ooooooooooooooooooooooooooooooooooooooooooooooooooooooooong text";
        document.add(new Paragraph(longText).setRotationAngle(-(Math.PI / 6)).setFixedPosition(50, 50, 450));
        document.add(new Paragraph(longText).setFixedPosition(50, 50, 450));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fixedTextRotationTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedTextRotationTest03.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "fixedTextRotationTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        String simpleText = "text simple text";
        float x = 50;
        float y = 380;
        float width = 200;
        document.add(new Paragraph(simpleText).setRotationAngle((Math.PI / 2)).setFixedPosition(x, y, width));
        document.add(new Paragraph(simpleText).setFixedPosition(x, y, width));

        PdfCanvas canvas = new PdfCanvas(pdfDocument.getFirstPage());
        drawCross(canvas, x, y);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fixedTextRotationTest04() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedTextRotationTest04.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "fixedTextRotationTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        String simpleText = "text simple text";
        float x = 50;
        float y = 380;
        float width = 100;
        document.add(new Paragraph(simpleText).setRotationAngle(-(Math.PI / 4)).setBackgroundColor(Color.RED).setFixedPosition(x, y, width));

        PdfCanvas canvas = new PdfCanvas(pdfDocument.getFirstPage());
        drawCross(canvas, x, y);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    @Test
    public void staticTextRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "staticTextRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "staticTextRotationTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph();
        for (int i = 0; i < 7; ++i)
            p.add(para2Text);
        document.add(p.setRotationAngle((68 * Math.PI / 180)).setBackgroundColor(Color.BLUE));
        document.add(new Paragraph("text line text line text line text line text line text line text line text line text line text line text line"));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)
    })
    @Test
    public void staticTextRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "staticTextRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "staticTextRotationTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para2Text).setRotationAngle((Math.PI / 12)));
        document.add(new Paragraph(new Text(para2Text).setBackgroundColor(Color.GREEN)).
                setRotationAngle((-Math.PI / 12)).setBackgroundColor(Color.BLUE));
        document.add(new Paragraph(para3Text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void staticTextRotationTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "staticTextRotationTest03.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "staticTextRotationTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para2Text).setRotationAngle((Math.PI / 6)).setBackgroundColor(Color.RED));
        document.add(new Paragraph(para2Text).setRotationAngle((-Math.PI / 3)));
        document.add(new Paragraph(para3Text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void staticTextRotationTest04() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "staticTextRotationTest04.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "staticTextRotationTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text));
        document.add(new Paragraph("short text string").setRotationAngle((Math.PI / 6)).setBackgroundColor(Color.RED));
        document.add(new Paragraph(para3Text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void splitTextRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "splitTextRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "splitTextRotationTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para1Text).setRotationAngle((Math.PI / 4)));
        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para2Text).setRotationAngle((-Math.PI / 3)));
        document.add(new Paragraph(para3Text));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)
    })
    @Test
    public void splitTextRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "splitTextRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "splitTextRotationTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para1Text));
        document.add(new Paragraph(para1Text));

        String extremelyLongText = "";
        for (int i = 0; i < 300; ++i) {
            extremelyLongText += para2Text;
        }

        document.add(new Paragraph(extremelyLongText).setRotationAngle(Math.PI / 2));
        document.add(new Paragraph(extremelyLongText).setRotationAngle(Math.PI / 4));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    @Test
    public void rotationInfiniteLoopTest01() throws IOException, InterruptedException {
        String fileName = "rotationInfiniteLoopTest01.pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + cmpPrefix + fileName;
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        pdfDocument.setDefaultPageSize(PageSize.A5.rotate());

        Document document = new Document(pdfDocument);

        document.add(new Paragraph(para1Text).setRotationAngle((Math.PI / 2)));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    @Test
    public void rotationInfiniteLoopTest02() throws IOException, InterruptedException {
        String fileName = "rotationInfiniteLoopTest02.pdf";
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + cmpPrefix + fileName;
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));
        pdfDocument.setDefaultPageSize(PageSize.A5.rotate());

        Document document = new Document(pdfDocument);

        document.add(new List().add(para1Text).setRotationAngle((Math.PI / 2)));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void tableRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tableRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "tableRotationTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{50, 50})
                .addCell(new Cell().add(new Paragraph("cell 1, 1").setRotationAngle((Math.PI / 2))))
                .addCell(new Cell().add(new Paragraph("cell 1, 2").setRotationAngle((Math.PI / 3))))
                .addCell(new Cell().add(new Paragraph("cell 2, 1").setRotationAngle((Math.PI / 3))))
                .addCell(new Cell().add(new Paragraph("cell 2, 2").setRotationAngle((Math.PI))));
        doc.add(table);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)
    })
    @Test
    public void tableRotationTest03() throws IOException,InterruptedException {
        String outFileName = destinationFolder + "tableRotationTest03.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "tableRotationTest03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{25, 50})
                .addCell(new Cell().add(new Paragraph("cell 1, 1").setRotationAngle((Math.PI / 2))))
                .addCell(new Cell().add(new Paragraph("cell 1, 2").setRotationAngle((Math.PI / 3))))
                .addCell(new Cell().add(new Paragraph("cell 2, 1")))
                .addCell(new Cell().add(new Paragraph("cell 2, 2")))
                .addCell(new Cell().add(new Paragraph("cell 3, 1").setRotationAngle(-(Math.PI / 2))))
                .addCell(new Cell().add(new Paragraph("cell 3, 2").setRotationAngle((Math.PI))));
        doc.add(table);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void cellRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "cellRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "cellRotationTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(1)
                .setWidth(50)
                .addCell(new Cell()
                        .add(new Paragraph("Hello"))
                        .setRotationAngle(Math.PI * 70 / 180.0)
                        .setBackgroundColor(Color.GREEN));
        doc.add(table);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
    
    @Test
    public void divRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "divRotationTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Div div = new Div().setBackgroundColor(Color.GREEN);
        div.add(new Paragraph(para1Text).setBackgroundColor(Color.RED)).setRotationAngle(Math.PI / 4);
        doc.add(div);

        div = new Div();
        div.add(new Paragraph(para1Text)).setRotationAngle(Math.PI / 2);
        doc.add(div);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 2)
    })
    @Test
    public void divRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "divRotationTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph(para1Text));
        doc.add(new Paragraph(para1Text));
        doc.add(new Paragraph(para1Text));

        String extremelyLongText = "";
        for (int i = 0; i < 300; ++i) {
            extremelyLongText += para2Text;
        }

        doc.add(new Div().add(new Paragraph(extremelyLongText)).setRotationAngle(Math.PI / 2));
        doc.add(new Div().add(new Paragraph(extremelyLongText)).setRotationAngle(Math.PI / 4));
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void listRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "listRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "listRotationTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph(para1Text));

        List list = new List().setRotationAngle(3 * Math.PI / 4).setBackgroundColor(Color.GREEN);
        list.add(new ListItem("text of first list item"));
        list.add("text of second list item");
        list.add("text of third list item");
        doc.add(list);

        doc.add(new Paragraph(para2Text));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void listRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "listRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "listRotationTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph(para1Text));
        doc.add(new Paragraph(para1Text));
        doc.add(new Paragraph(para1Text));

        List list = new List().setRotationAngle(Math.PI / 2).setBackgroundColor(Color.GREEN);
        String itemText = "list item text long item txt list item text long item txt list item text long item txt list item text long item txt list item text long item txt END";
        for (int i = 0; i < 10; ++i) {
            list.add(itemText);
        }
        doc.add(list);

        doc.add(new Paragraph(para2Text));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void alignedTextRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "alignedTextRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "alignedTextRotationTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph(para1Text));

        Paragraph p = new Paragraph();
        p.add("texttext").setTextAlignment(TextAlignment.CENTER).setHorizontalAlignment(HorizontalAlignment.CENTER);
        p.setRotationAngle(Math.PI / 4);
        doc.add(p);

        doc.add(new Paragraph(para3Text));

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void innerRotationTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "innerRotationTest01.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "innerRotationTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Div().
                setBackgroundColor(Color.GREEN).
                setHeight(300).setWidth(300).
                add(new Div().
                        setBackgroundColor(Color.RED).
                        setHeight(100).
                        setWidth(100).
                        setRotationAngle(Math.PI / 4)).
                setRotationAngle(Math.PI / 8)
        );

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)
    })
    @Test
    public void innerRotationTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "innerRotationTest02.pdf";
        String cmpFileName = sourceFolder + cmpPrefix + "innerRotationTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(6400, 6400));

        String longText = para1Text + para2Text + para3Text;
        String extremeLongText = longText + longText + longText;
        doc.add(new Div().
                setBackgroundColor(Color.GREEN).
                setMinHeight(300).setWidth(300).
                add(new Div().
                        setBackgroundColor(Color.RED).
                        setWidth(30).
                        setRotationAngle(5 * Math.PI / 16).
                        add(new Paragraph(extremeLongText))).
                add(new Paragraph("smaaaaaaaaaaaaaaaaaaaall taaaaaaaaaaaaaaaaaaalk")).
                add(new Paragraph("smaaaaaaaaaaaaaaaaaaaall taaaaaaaaaaaaaaaaaaalk")).
                setRotationAngle(Math.PI / 8)
        );

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private void drawCross(PdfCanvas canvas, float x, float y) {
        drawLine(canvas, x - 50, y, x + 50, y);
        drawLine(canvas, x, y - 50, x, y + 50);
    }

    private void drawLine(PdfCanvas canvas, float x1, float y1, float x2, float y2) {
        canvas.saveState().setLineWidth(0.5f).setLineDash(3).moveTo(x1, y1).lineTo(x2,y2).stroke().restoreState();
    }
}
