package com.itextpdf.model;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.canvas.color.DeviceGray;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.model.border.SolidBorder;
import com.itextpdf.model.element.List;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Text;
import com.itextpdf.text.DocumentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PositioningTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/PositioningTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/PositioningTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void relativePositioningTest01() throws IOException, InterruptedException, DocumentException {
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
    public void relativePositioningTest02() throws IOException, InterruptedException, DocumentException {
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
                setBackgroundColor(Color.Blue).
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

        new PdfCanvas(document.getPdfDocument().getPage(1)).setFillColor(Color.Black).rectangle(300, 300, 100, 100).fill().release();

        Paragraph p = new Paragraph("Hello").setBackgroundColor(Color.Blue).setHeight(100).
                setFixedPosition(1, 300, 300, 100);
        document.add(p);


        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
