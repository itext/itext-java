package com.itextpdf.layout;


import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.FloatPropertyValue;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class FloatTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/FloatTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/FloatTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void floatParagraphTest01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatParagraphTest01.pdf";
        String outFile = destinationFolder + "floatParagraphTest01.pdf";

        PdfWriter writer = new PdfWriter(outFile);


        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);
        Paragraph p = new Paragraph();
        p.add("paragraph1");
        p.setWidth(70);
        p.setHeight(100);
        p.setBorder(new SolidBorder(1));
        p.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        Paragraph p1 = new Paragraph();
        p1.add("paragraph2");
        p1.setWidth(70);
        p1.setHeight(100);
        p1.setBorder(new SolidBorder(1));
        p1.setProperty(Property.FLOAT, FloatPropertyValue.RIGHT);

        doc.add(p);
        doc.add(p1);
        Paragraph p2 = new Paragraph();
        p2.add("paragraph3");
        p2.setBorder(new SolidBorder(1));
        doc.add(p2);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void floatParagraphTest02() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatParagraphTest02.pdf";
        String outFile = destinationFolder + "floatParagraphTest02.pdf";

        PdfWriter writer = new PdfWriter(outFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Paragraph p = new Paragraph();
        p.add("paragraph1");
        p.setWidth(70);
        p.setHeight(100);
        p.setBorder(new SolidBorder(1));
        Paragraph p1 = new Paragraph();
        p1.add("paragraph2");
        p1.setBorder(new SolidBorder(1));

        p.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        doc.add(p);
        doc.add(p1);
        Paragraph p2 = new Paragraph();
        p2.add("paragraph3");
        p2.setBorder(new SolidBorder(1));
        doc.add(p2);
        doc.add(p2);
        Paragraph p3 = new Paragraph("paragraph4aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        p3.setBorder(new SolidBorder(1));
        doc.add(p3);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void floatDivTest01() throws IOException, InterruptedException {
        String cmpFileName = sourceFolder + "cmp_floatDivTest01.pdf";
        String outFile = destinationFolder + "floatDivTest01.pdf";

        PdfWriter writer = new PdfWriter(outFile);
        PdfDocument pdfDoc = new PdfDocument(writer);
        Document doc = new Document(pdfDoc);

        Div div = new Div();
        div.setWidth(70);

        Paragraph p = new Paragraph();
        p.add("div1");
        div.setBorder(new SolidBorder(1));
        p.setBorder(new SolidBorder(1));
        div.setProperty(Property.FLOAT, FloatPropertyValue.LEFT);
        div.add(p);
        doc.add(div);
        doc.add(new Paragraph("div2"));

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFile, cmpFileName, destinationFolder, "diff"));
    }

}
