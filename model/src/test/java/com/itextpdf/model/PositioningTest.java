package com.itextpdf.model;

import com.itextpdf.basics.geom.PageSize;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.color.Color;
import com.itextpdf.core.color.DeviceGray;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.model.border.SolidBorder;
import com.itextpdf.model.element.List;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Text;
import com.itextpdf.test.ExtendedITextTest;


import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PositioningTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/PositioningTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/PositioningTest/";

    @BeforeClass
    static public void beforeClass() {
       createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void relativePositioningTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "relativePositioningTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_relativePositioningTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph().
                setBorder(new SolidBorder(new DeviceGray(0), 5)).
                setWidth(300).
                setPaddings(20, 20, 20, 20).
                add("Here is a line of text.").
                add(new Text("This part is shifted\n up a bit,").setRelativePosition(0, -10, 0, 0).setBackgroundColor(new DeviceGray(0.8f))).
                add("but the rest of the line is in its original position.");

        document.add(p);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void relativePositioningTest02() throws IOException, InterruptedException{
        String outFileName = destinationFolder + "relativePositioningTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_relativePositioningTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph().
                setBorder(new SolidBorder(new DeviceGray(0), 5)).
                        setWidth(180).
                        setPaddings(20, 20, 20, 20).
                        add("Here is a line of text.").
                        add(new Text("This part is shifted\n up a bit,").setRelativePosition(0, -10, 0, 0).setBackgroundColor(new DeviceGray(0.8f))).
                        add("but the rest of the line is in its original position.").
                        setRelativePosition(50, 0, 0, 0);

        document.add(p);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fixedPositioningTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedPositioningTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_fixedPositioningTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        List list = new List(Property.ListNumberingType.ROMAN_UPPER).
                setFixedPosition(2, 300, 300, 50).
                setBackgroundColor(Color.BLUE).
                setHeight(100);
        list.add("Hello").
            add("World").
            add("!!!");
        document.add(list);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void fixedPositioningTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "fixedPositioningTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_fixedPositioningTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);
        document.getPdfDocument().addNewPage();

        new PdfCanvas(document.getPdfDocument().getPage(1)).setFillColor(Color.BLACK).rectangle(300, 300, 100, 100).fill().release();

        Paragraph p = new Paragraph("Hello").setBackgroundColor(Color.BLUE).setHeight(100).
                setFixedPosition(1, 300, 300, 100);
        document.add(p);


        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void showTextAlignedTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "showTextAlignedTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_showTextAlignedTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document document = new Document(pdfDocument);

        pdfDocument.addNewPage();
        PdfCanvas canvas = new PdfCanvas(pdfDocument.getLastPage());

        String text = "textapqgaPQGatext";
        float width = 200;
        float x, y;

        y = 700;
        x = 115;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, Property.TextAlignment.LEFT, Property.VerticalAlignment.BOTTOM, 0);
        document.showTextAligned(text, x, y, Property.TextAlignment.LEFT, Property.VerticalAlignment.BOTTOM, (float) (Math.PI / 6 * 1));
        x = 300;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, Property.TextAlignment.LEFT, Property.VerticalAlignment.MIDDLE, 0);
        document.showTextAligned(text, x, y, Property.TextAlignment.LEFT, Property.VerticalAlignment.MIDDLE, (float) (Math.PI/6 * 3));
        x = 485;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, Property.TextAlignment.LEFT, Property.VerticalAlignment.TOP, 0);
        document.showTextAligned(text, x, y, Property.TextAlignment.LEFT, Property.VerticalAlignment.TOP, (float) (Math.PI/6 * 5));

        y = 400;
        x = 115;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, Property.TextAlignment.CENTER, Property.VerticalAlignment.BOTTOM, 0);
        document.showTextAligned(text, x, y, Property.TextAlignment.CENTER, Property.VerticalAlignment.BOTTOM, (float) (Math.PI/6 * 2));
        x = 300;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, Property.TextAlignment.CENTER, Property.VerticalAlignment.MIDDLE, 0);
        document.showTextAligned(text, x, y, Property.TextAlignment.CENTER, Property.VerticalAlignment.MIDDLE, (float) (Math.PI / 6 * 4));
        x = 485;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, Property.TextAlignment.CENTER, Property.VerticalAlignment.TOP, 0);
        document.showTextAligned(text, x, y, Property.TextAlignment.CENTER, Property.VerticalAlignment.TOP, (float) (Math.PI/6 * 8));

        y = 100;
        x = 115;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, Property.TextAlignment.RIGHT, Property.VerticalAlignment.BOTTOM, 0);
        document.showTextAligned(text, x, y, Property.TextAlignment.RIGHT, Property.VerticalAlignment.BOTTOM, (float) (Math.PI/6 * 9));
        x = 300;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, Property.TextAlignment.RIGHT, Property.VerticalAlignment.MIDDLE, 0);
        document.showTextAligned(text, x, y, Property.TextAlignment.RIGHT, Property.VerticalAlignment.MIDDLE, (float) (Math.PI/6 * 7));
        x = 485;
        drawCross(canvas, x, y);
        document.showTextAligned(text, x, y, Property.TextAlignment.RIGHT, Property.VerticalAlignment.TOP, 0);
        document.showTextAligned(text, x, y, Property.TextAlignment.RIGHT, Property.VerticalAlignment.TOP, (float) (Math.PI/6 * 6));

        document.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));

    }

    @Test
    public void showTextAlignedTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "showTextAlignedTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_showTextAlignedTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        Document document = new Document(pdfDocument);

        String watermarkText = "WATERMARK";
        Paragraph watermark = new Paragraph(watermarkText);
        watermark.setFontColor(new DeviceGray(0.75f)).setFontSize(72);
        document.showTextAligned(watermark, PageSize.A4.getWidth() / 2, PageSize.A4.getHeight() / 2, 1, Property.TextAlignment.CENTER, Property.VerticalAlignment.MIDDLE, (float) (Math.PI / 4));

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        document.add(new Paragraph(textContent + textContent + textContent));
        document.add(new Paragraph(textContent + textContent + textContent));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));

    }

    private void drawCross(PdfCanvas canvas, float x, float y) {
        drawLine(canvas, x - 50, y, x + 50, y);
        drawLine(canvas, x, y - 50, x, y + 50);
    }

    private void drawLine(PdfCanvas canvas, float x1, float y1, float x2, float y2) {
        canvas.saveState().moveTo(x1, y1).lineTo(x2,y2).setLineWidth(0.5f).setLineDash(3).stroke().restoreState();
    }
}
