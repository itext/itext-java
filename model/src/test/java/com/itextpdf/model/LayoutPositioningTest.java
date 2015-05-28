package com.itextpdf.model;

import com.itextpdf.basics.PdfException;
import com.itextpdf.canvas.color.DeviceGray;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Text;
import com.itextpdf.text.DocumentException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LayoutPositioningTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/LayoutPositioningTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/LayoutPositioningTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void relativePositioningTest01() throws IOException, PdfException, InterruptedException, DocumentException {
        String outFileName = destinationFolder + "relativePositioningTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_relativePositioningTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph().
                setBorder(new Property.BorderConfig(new DeviceGray(0), 5, Property.BorderConfig.BorderStyle.SOLID)).
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
    public void relativePositioningTest02() throws IOException, PdfException, InterruptedException, DocumentException {
        String outFileName = destinationFolder + "relativePositioningTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_relativePositioningTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));

        Document document = new Document(pdfDocument);

        Paragraph p = new Paragraph().
                setBorder(new Property.BorderConfig(new DeviceGray(0), 5, Property.BorderConfig.BorderStyle.SOLID)).
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

}
