package com.itextpdf.model;

import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.model.border.SolidBorder;
import com.itextpdf.model.element.Cell;
import com.itextpdf.model.element.Image;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Table;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class TableTest {
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/TableTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/TableTest/";

    @BeforeClass
    static public void beforeClass() {
        File dest = new File(destinationFolder);
        dest.mkdirs();
        File[] files = dest.listFiles();
        if (files != null) {
            for (File file : files) {
                file.delete();
            }
        }
    }

    @Test
    public void simpleTableTest01() throws IOException, InterruptedException {
        String testName = "tableTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        PdfImageXObject xObject = new PdfImageXObject(ImageFactory.getPngImage(new File(sourceFolder + "itext.png").toURI().toURL()));
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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
                .addCell(new Cell().add(new Paragraph("cell 1, 2\n" + middleTextContent)).setBorder(new SolidBorder(Color.Red, 2)))
                .addCell(new Cell().add(new Paragraph("cell 2, 1\n" + middleTextContent + middleTextContent)))
                .addCell(new Cell().add(new Paragraph("cell 2, 2\n" + longTextContent)));
        doc.add(table);
        doc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, testName + "_diff"));
    }

    @Test
    public void bigRowspanTest01() throws IOException, InterruptedException {
        String testName = "bigRowspanTest01.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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
    @Ignore("DEVSIX-225. A cell has a big rowspan and we don't even try to fit it in the first available area.")
    public void bigRowspanTest02() throws IOException, InterruptedException {
        String testName = "bigRowspanTest02.pdf";
        String outFileName = destinationFolder + testName;
        String cmpFileName = sourceFolder + "cmp_" + testName;

        FileOutputStream file = new FileOutputStream(outFileName);
        PdfWriter writer = new PdfWriter(file);
        PdfDocument pdfDoc = new PdfDocument(writer);
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
}
