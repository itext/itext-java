package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.util.UrlUtil;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.layout.property.Property;
import com.itextpdf.layout.property.UnitValue;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class TableTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/TableTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/TableTest/";

    static final String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
            "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
            "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
    static final String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
    static final String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
            "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";


    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void simpleTableTest01() throws IOException, InterruptedException {
        String testName = "tableTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{50, 50})
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("cell 1, 2")));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest02() throws IOException, InterruptedException {
        String testName = "tableTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{50, 50})
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("cell 1, 2")))
                .addCell(new Cell().add(new Paragraph("cell 2, 1")))
                .addCell(new Cell().add(new Paragraph("cell 2, 2")))
                .addCell(new Cell().add(new Paragraph("cell 3, 1")))
                .addCell(new Cell());
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest03() throws IOException, InterruptedException {
        String testName = "tableTest03.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent1 = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n";

        String textContent2 = "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n" +
                "Aenean nec lorem. In porttitor. Donec laoreet nonummy augue.\n" +
                "Suspendisse dui purus, scelerisque at, vulputate vitae, pretium mattis, nunc. Mauris eget neque at sem venenatis eleifend. Ut nonummy.\n";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent1)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + textContent1 + textContent2)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + textContent2 + textContent1)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + textContent2)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest04() throws IOException, InterruptedException {
        String testName = "tableTest04.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent)));
        table.addCell(new Cell(3, 1).add(new Paragraph("cell 1, 2:3\n" + textContent + textContent + textContent)));
        table.addCell(new Cell().add(new Paragraph("cell 2, 1\n" + textContent)));
        table.addCell(new Cell().add(new Paragraph("cell 3, 1\n" + textContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest05() throws IOException, InterruptedException {
        String testName = "tableTest05.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell(3, 1).add(new Paragraph("cell 1, 1:3\n" + textContent + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 2\n" + textContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest06() throws IOException, InterruptedException {
        String testName = "tableTest06.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent)))
                .addCell(new Cell(3, 1).add(new Paragraph("cell 1, 2:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + textContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest07() throws IOException, InterruptedException {
        String testName = "tableTest07.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell(3, 1).add(new Paragraph("cell 1, 1:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 2\n" + textContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest08() throws IOException, InterruptedException {
        String testName = "tableTest08.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell(3, 2).add(new Paragraph("cell 1:2, 1:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest09() throws IOException, InterruptedException {
        String testName = "tableTest09.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + middleTextContent)))
                .addCell(new Cell(3, 2).add(new Paragraph("cell 2:2, 1:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest10() throws IOException, InterruptedException {
        String testName = "tableTest10.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph("Table 1"));
        Table table = new Table(new float[]{100, 100})
                .addCell(new Cell().add(new Paragraph("1, 1")))
                .addCell(new Cell().add(new Paragraph("1, 2")))
                .addCell(new Cell().add(new Paragraph("2, 1")))
                .addCell(new Cell().add(new Paragraph("2, 2")));
        doc.add(table);

        doc.add(new Paragraph("Table 2"));

        Table table2 = new Table(new float[]{50, 50})
                .addCell(new Cell().add(new Paragraph("1, 1")))
                .addCell(new Cell().add(new Paragraph("1, 2")))
                .addCell(new Cell().add(new Paragraph("2, 1")))
                .addCell(new Cell().add(new Paragraph("2, 2")));
        doc.add(table2);

        doc.add(new Paragraph("Table 3"));

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.createPng(UrlUtil.toURL(sourceFolder + "itext.png")));
        Image image = new Image(xObject, 50);

        Table table3 = new Table(new float[]{100, 100})
                .addCell(new Cell().add(new Paragraph("1, 1")))
                .addCell(new Cell().add(image))
                .addCell(new Cell().add(new Paragraph("2, 1")))
                .addCell(new Cell().add(new Paragraph("2, 2")));
        doc.add(table3);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest11() throws IOException, InterruptedException {
        String testName = "tableTest11.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 5, 1\n" + middleTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 6, 1\n" + middleTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 6, 2\n" + shortTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 7, 1\n" + middleTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 7, 2\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest12() throws IOException, InterruptedException {
        String testName = "tableTest12.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 5, 1\n" + middleTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 6, 1\n" + middleTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 6, 2\n" + shortTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 7, 1\n" + middleTextContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 7, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 8, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 8, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 9, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 9, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 10, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 10, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 11, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 11, 2\n" + shortTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest13() throws IOException, InterruptedException {
        String testName = "tableTest13.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);
        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 2\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest14() throws IOException, InterruptedException {
        String testName = "tableTest14.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell(3, 2).add(new Paragraph("cell 1:2, 1:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest15() throws IOException, InterruptedException {
        String testName = "tableTest15.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + middleTextContent)))
                .addCell(new Cell(3, 2).add(new Paragraph("cell 3:2, 1:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 7, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest16() throws IOException, InterruptedException {
        String testName = "tableTest16.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        String longTextContent = "1. " + textContent + "2. " + textContent + "3. " + textContent + "4. " + textContent
                + "5. " + textContent + "6. " + textContent + "7. " + textContent + "8. " + textContent + "9. " + textContent;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + middleTextContent)).setBorder(new SolidBorder(Color.RED, 2)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + middleTextContent + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + longTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void simpleTableTest17() throws IOException, InterruptedException {
        String testName = "tableTest17.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{50, 50, 50})
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("cell 1, 2")))
                .addCell(new Cell().add(new Paragraph("cell 1, 3")));

        String longText = "Long text, very long text. ";
        for (int i = 0; i < 4; i++) {
            longText += longText;
        }
        table.addCell(new Cell().add(new Paragraph("cell 2.1\n" + longText).setKeepTogether(true)));
        table.addCell("cell 2.2\nShort text.");
        table.addCell(new Cell().add(new Paragraph("cell 2.3\n" + longText)));

        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void simpleTableTest18() throws IOException, InterruptedException {
        String testName = "tableTest18.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        doc.add(new Paragraph(textContent));

        Table table = new Table(new float[]{50, 50, 50})
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("cell 1, 2")))
                .addCell(new Cell().add(new Paragraph("cell 1, 3")));

        String longText = "Long text, very long text. ";
        for (int i = 0; i < 4; i++) {
            longText += longText;
        }
        table.addCell(new Cell().add(new Paragraph("cell 2.1\n" + longText).setKeepTogether(true)));
        table.addCell("cell 2.2\nShort text.");
        table.addCell(new Cell().add(new Paragraph("cell 2.3\n" + longText)));

        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void simpleTableTest19() throws IOException, InterruptedException {
        String testName = "tableTest19.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell(3, 2).add(new Paragraph("cell 1:2, 1:3\n" + textContent + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 1, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 3\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 3\n" + textContent)))
                .addCell(new Cell().add(new Image(ImageDataFactory.create(sourceFolder + "red.png"))))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void simpleTableTest20() throws IOException, InterruptedException {
        String testName = "tableTest20.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell().add(new Image(ImageDataFactory.create(sourceFolder + "red.png"))))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    public void simpleTableTest21() throws IOException, InterruptedException {
        String testName = "tableTest21.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String shortTextContent = "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        doc.add(new Paragraph(textContent));

        Table table = new Table(new float[]{130, 130, 260})
                .addCell(new Cell().add(new Image(ImageDataFactory.create(sourceFolder + "red.png"))))
                .addCell(new Cell().add(new Paragraph("cell 4, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 2\n" + shortTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 3\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 3\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void simpleTableTest22() throws IOException, InterruptedException {
        String testName = "tableTest22.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new UnitValue[]{UnitValue.createPointValue(30), UnitValue.createPointValue(30), UnitValue.createPercentValue(30), UnitValue.createPercentValue(30)})
                .addCell(new Cell().add(new Paragraph("cell 1, 1")))
                .addCell(new Cell().add(new Paragraph("cell 1, 2")))
                .addCell(new Cell().add(new Paragraph("cell 1, 3")))
                .addCell(new Cell().add(new Paragraph("cell 1, 4")));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest01() throws IOException, InterruptedException {
        String testName = "bigRowspanTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        String longTextContent = "1. " + textContent + "2. " + textContent + "3. " + textContent + "4. " + textContent
                + "5. " + textContent + "6. " + textContent + "7. " + textContent + "8. " + textContent + "9. " + textContent;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent)))
                .addCell(new Cell(5, 1).add(new Paragraph("cell 1, 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + middleTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest02() throws IOException, InterruptedException {
        String testName = "bigRowspanTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        String longTextContent = "1. " + textContent + "2. " + textContent + "3. " + textContent + "4. " + textContent
                + "5. " + textContent + "6. " + textContent + "7. " + textContent + "8. " + textContent + "9. " + textContent;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent)))
                .addCell(new Cell(5, 1).add(new Paragraph("cell 1, 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + textContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest03() throws IOException, InterruptedException {
        String testName = "bigRowspanTest03.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent)))
                .addCell(new Cell(5, 1).add(new Paragraph("cell 1, 2\n" + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + textContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest04() throws IOException, InterruptedException {
        String testName = "bigRowspanTest04.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String middleTextContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.";

        String longTextContent = "1. " + textContent + "2. " + textContent + "3. " + textContent + "4. " + textContent
                + "5. " + textContent + "6. " + textContent + "7. " + textContent + "8. " + textContent + "9. " + textContent;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 1, 1\n" + textContent)))
                .addCell(new Cell(5, 1).add(new Paragraph("cell 1, 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 3, 1\n" + textContent)))
                .addCell(new Cell().setKeepTogether(true).add(new Paragraph("cell 4, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + textContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    @Ignore
    public void bigRowspanTest05() throws IOException, InterruptedException {
        String testName = "bigRowspanTest05.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Maecenas porttitor congue massa. Fusce posuere, magna sed pulvinar ultricies, purus lectus malesuada libero, sit amet commodo magna eros quis urna.\n" +
                "Nunc viverra imperdiet enim. Fusce est. Vivamus a tellus.\n" +
                "Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Proin pharetra nonummy pede. Mauris et orci.\n";
        String longTextContent = "1. " + textContent + "2. " + textContent + "3. " + textContent + "4. " + textContent
                + "5. " + textContent + "6. " + textContent + "7. " + textContent + "8. " + textContent + "9. " + textContent;

        Table table = new Table(new float[]{250, 250})
                .addCell(new Cell().add(new Paragraph("cell 4, 1\n" + textContent)))
                .addCell(new Cell(2, 1).add(new Paragraph("cell 4, 2\n" + longTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 5, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 6, 1\n" + textContent)))
                .addCell(new Cell().add(new Paragraph("cell 9, 2\n" + textContent)));

        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void differentPageOrientationTest01() throws IOException, InterruptedException {
        String testName = "differentPageOrientationTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        final PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        String textContent1 = "Video provides a powerful way to help you prove your point. When you click Online Video, you can paste in the embed code for the video you want to add. You can also type a keyword to search online for the video that best fits your document.";
        String textContent2 = "To make your document look professionally produced, Word provides header, footer, cover page, and text box designs that complement each other. For example, you can add a matching cover page, header, and sidebar. Click Insert and then choose the elements you want from the different galleries.";
        String textContent3 = "Themes and styles also help keep your document coordinated. When you click Design and choose a new Theme, the pictures, charts, and SmartArt graphics change to match your new theme. When you apply styles, your headings change to match the new theme.";

        Table table = new Table(3);
        for (int i = 0; i < 20; i++) {
            table.addCell(new Cell().add(new Paragraph(textContent1)))
                    .addCell(new Cell().add(new Paragraph(textContent3)))
                    .addCell(new Cell().add(new Paragraph(textContent2)))

                    .addCell(new Cell().add(new Paragraph(textContent3)))
                    .addCell(new Cell().add(new Paragraph(textContent2)))
                    .addCell(new Cell().add(new Paragraph(textContent1)))

                    .addCell(new Cell().add(new Paragraph(textContent2)))
                    .addCell(new Cell().add(new Paragraph(textContent1)))
                    .addCell(new Cell().add(new Paragraph(textContent3)));
        }
        doc.setRenderer(new RotatedDocumentRenderer(doc, pdfDoc));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    private static class RotatedDocumentRenderer extends DocumentRenderer {
        private final PdfDocument pdfDoc;

        public RotatedDocumentRenderer(Document doc, PdfDocument pdfDoc) {
            super(doc);
            this.pdfDoc = pdfDoc;
        }

        @Override
        protected PageSize addNewPage(PageSize customPageSize) {
            PageSize pageSize = currentPageNumber % 2 == 1 ? PageSize.A4 : PageSize.A4.rotate();
            pdfDoc.addNewPage(pageSize);
            return pageSize;
        }
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    @Test
    public void toLargeElementWithKeepTogetherPropertyInTableTest01() throws IOException, InterruptedException {
        String testName = "toLargeElementWithKeepTogetherPropertyInTableTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        Table table = new Table(1);
        Cell cell = new Cell();
        String str = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String result = "";
        for (int i = 0; i < 53; i++) {
            result += str;
        }
        Paragraph p = new Paragraph(new Text(result));
        p.setProperty(Property.KEEP_TOGETHER, true);
        cell.add(p);
        table.addCell(cell);
        doc.add(table);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 1)
    })
    @Test
    public void toLargeElementInTableTest01() throws IOException, InterruptedException {
        String testName = "toLargeElementInTableTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + "toLargeElementInTableTest01.pdf"));
        Document doc = new Document(pdfDoc);

        Table table = new Table(new float[]{5});
        Cell cell = new Cell();
        Paragraph p = new Paragraph(new Text("a"));
        cell.add(p);
        table.addCell(cell);
        doc.add(table);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void nestedTableSkipHeaderFooterTest() throws IOException, InterruptedException {
        String testName = "nestedTableSkipHeaderFooter.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, PageSize.A4.rotate());

        Table table = new Table(5);
        table.addHeaderCell(new Cell(1, 5).
                add(new Paragraph("Table XYZ (Continued)")));
        table.addFooterCell(new Cell(1, 5).
                add(new Paragraph("Continue on next page")));
        table.setSkipFirstHeader(true);
        table.setSkipLastFooter(true);
        for (int i = 0; i < 350; i++) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i + 1))));
        }

        Table t = new Table(1);
        t.addCell(new Cell().
                setBorder(new SolidBorder(Color.RED, 1)).
                setPaddings(3, 3, 3, 3).
                add(table));

        doc.add(t);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @LogMessages(messages = {
            @LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA, count = 9)
    })
    @Test
    public void splitTableOnShortPage() throws IOException, InterruptedException {
        String testName = "splitTableOnShortPage.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc, new PageSize(300, 90));

        doc.add(new Paragraph("Table with setKeepTogether(true):"));
        Table table = new Table(3);
        table.setKeepTogether(true);
        Cell cell = new Cell(3, 1);
        cell.add("G");
        cell.add("R");
        cell.add("P");
        table.addCell(cell);
        table.addCell("middle row 1");
        cell = new Cell(3, 1);
        cell.add("A");
        cell.add("B");
        cell.add("C");
        table.addCell(cell);
        table.addCell("middle row 2");
        table.addCell("middle row 3");
        doc.add(table);

        doc.add(new AreaBreak());

        doc.add(new Paragraph("Table with setKeepTogether(false):"));
        table.setKeepTogether(false);
        doc.add(table);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void imageInTableTest_HA() throws IOException, InterruptedException {
        String testName = "imageInTableTest_HA.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        Document doc = new Document(pdfDoc);

        PdfImageXObject xObject = new PdfImageXObject(ImageDataFactory.createPng(UrlUtil.toURL(sourceFolder + "itext.png")));
        Image imageL = new Image(xObject);
        imageL.setHorizontalAlignment(HorizontalAlignment.LEFT);
        Image imageC = new Image(xObject);
        imageC.setHorizontalAlignment(HorizontalAlignment.CENTER);
        Image imageR = new Image(xObject);
        imageR.setHorizontalAlignment(HorizontalAlignment.RIGHT);

        doc.add(new Paragraph("Table"));
        Table table = new Table(1)
                .addCell(new Cell().add(imageL))
                .addCell(new Cell().add(imageC))
                .addCell(new Cell().add(imageR));
        doc.add(table);

        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }
}
