package com.itextpdf.model;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.geom.PageSize;
import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.core.color.Color;
import com.itextpdf.core.color.DeviceGray;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.font.PdfFontFactory;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.model.element.Cell;
import com.itextpdf.model.element.Div;
import com.itextpdf.model.element.Image;
import com.itextpdf.model.element.List;
import com.itextpdf.model.element.Paragraph;
import com.itextpdf.model.element.Table;
import com.itextpdf.model.element.Text;
import com.itextpdf.test.ExtendedITextTest;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.xml.sax.SAXException;

import static org.junit.Assert.assertNull;

@Category(IntegrationTest.class)
public class AutoTaggingTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/AutoTaggingTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/AutoTaggingTest/";
    static final public String imageName = "Desert.jpg";

    @BeforeClass
    static public void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void textInParagraphTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "textInParagraphTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_textInParagraphTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Paragraph p = createParagraph1();
        document.add(p);

        for (int i = 0; i < 26; ++i) {
            document.add(createParagraph2());
        }

        document.close();

        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "imageTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Image image = new Image(ImageFactory.getImage(sourceFolder + imageName));
        document.add(image);

        document.close();

        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void divTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "divTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_divTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Div div = new Div();

        div.add(createParagraph1());
        Image image = new Image(ImageFactory.getImage(sourceFolder + imageName));
        image.setAutoScale(true);
        div.add(image);
        div.add(createParagraph2());
        div.add(image);
        div.add(createParagraph2());

        document.add(div);

        document.close();

        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void tableTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "tableTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_tableTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Table table = new Table(3);

        table.addCell(createParagraph1());
        Image image = new Image(ImageFactory.getImage(sourceFolder + imageName));
        image.setAutoScale(true);
        table.addCell(image);
        table.addCell(createParagraph2());
        table.addCell(image);
        table.addCell(new Paragraph("abcdefghijklkmnopqrstuvwxyz").setFontColor(Color.GREEN));
        table.addCell("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        document.add(table);

        document.close();

        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void tableTest02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "tableTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_tableTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Table table = new Table(3);

        for (int i = 0; i < 5; ++i) {
            table.addCell(createParagraph2());
        }
        table.addCell("little text");

        document.add(table);

        document.close();

        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void tableTest03() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "tableTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_tableTest03.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Table table = new Table(3);

        for (int i = 0; i < 3; ++i) {
            table.addHeaderCell("header " + i);
        }

        for (int i = 0; i < 3; ++i) {
            table.addFooterCell("footer " + i);
        }

        for (int i = 0; i < 5; ++i) {
            table.addCell(createParagraph2());
        }

        table.addCell(new Paragraph("little text"));

        document.add(table);

        document.close();

        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void tableTest04() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "tableTest04.pdf";
        String cmpFileName = sourceFolder + "cmp_tableTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        Table table = new Table(5, true);

        doc.add(table);
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 4; j++) {
                table.addCell(new Cell().add(new Paragraph(String.format("Cell %s, %s", i + 1, j + 1))));
            }

            if (i % 10 == 0) {
                table.flush();

                // This is a deliberate additional flush.
                table.flush();
            }
        }

        table.complete();

        doc.close();
        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void tableTest05() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "tableTest05.pdf";
        String cmpFileName = sourceFolder + "cmp_tableTest05.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        Table table = new Table(5, true);
        doc.add(table);

        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)"));
        table.addHeaderCell(cell);
        for (int i = 0; i < 5; ++i) {
            table.addHeaderCell(new Cell().add("Header " + (i + 1)));
        }
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page"));
        table.addFooterCell(cell);
        table.setSkipFirstHeader(true);
        table.setSkipLastFooter(true);

        for (int i = 0; i < 350; i++) {
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i+1))));
            table.flush();
        }

        table.complete();

        doc.close();
        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void tableTest06() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "tableTest06.pdf";
        String cmpFileName = sourceFolder + "cmp_tableTest06.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

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

        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void listTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "listTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_listTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        List list = new List();
        list.add("item 1");
        list.add("item 2");
        list.add("item 3");

        doc.add(list);
        doc.close();

        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void artifactTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "artifactTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_artifactTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();
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

        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void artifactTest02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "artifactTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_artifactTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();
        Document document = new Document(pdfDocument);

        document.add(new Paragraph("Hello world"));

        Table table = new Table(5);
        for (int i = 0; i < 25; ++i) {
            table.addCell(String.valueOf(i));
        }
        table.setRole(PdfName.Artifact);
        document.add(table);

        document.close();

        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    /**
     * Document generation and result is the same in this test as in the textInParagraphTest01, except the partial flushing of
     * tag structure. So you can check the result by comparing resultant document with the one in textInParagraphTest01.
     */
    @Test
    public void flushingTest01() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        String outFileName = destinationFolder + "flushingTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_flushingTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Paragraph p = createParagraph1();
        document.add(p);

        int pageToFlush = 1;
        for (int i = 0; i < 26; ++i) {
            if (i % 6 == 5) {
                pdfDocument.getPage(pageToFlush++).flush();
            }
            document.add(createParagraph2());
        }

        document.close();

        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    /**
     * Document generation and result is the same in this test as in the tableTest05, except the partial flushing of
     * tag structure. So you can check the result by comparing resultant document with the one in tableTest05.
     */
    @Test
    public void flushingTest02() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        String outFileName = destinationFolder + "flushingTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_flushingTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        Table table = new Table(5, true);
        doc.add(table);

//        TODO solve header/footer problems with tagging. Currently, partial flushing when header/footer is used leads to crash.
        Cell cell = new Cell(1, 5).add(new Paragraph("Table XYZ (Continued)"));
        table.addHeaderCell(cell);
        for (int i = 0; i < 5; ++i) {
            table.addHeaderCell(new Cell().add("Header " + (i + 1)));
        }
        cell = new Cell(1, 5).add(new Paragraph("Continue on next page"));
        table.addFooterCell(cell);
        table.setSkipFirstHeader(true);
        table.setSkipLastFooter(true);

        int magicalFlushingIndicator = 148;
        for (int i = 0; i < 350; i++) {
            if (i % magicalFlushingIndicator == magicalFlushingIndicator - 1) {
                pdfDocument.getPage(i / magicalFlushingIndicator + 1).flush();
            }
            table.addCell(new Cell().add(new Paragraph(String.valueOf(i+1))));
            table.flush();
        }

        table.complete();

        doc.close();
        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    /**
     * Document generation and result is the same in this test as in the tableTest04, except the partial flushing of
     * tag structure. So you can check the result by comparing resultant document with the one in tableTest04.
     */
    @Test
    public void flushingTest03() throws IOException, ParserConfigurationException, SAXException, InterruptedException {
        String outFileName = destinationFolder + "flushingTest03.pdf";
        String cmpFileName = sourceFolder + "cmp_tableTest04.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document doc = new Document(pdfDocument);

        Table table = new Table(5, true);

        doc.add(table);
        for (int i = 0; i < 20; i++) {
            for (int j = 0; j < 4; j++) {
                table.addCell(new Cell().add(new Paragraph(String.format("Cell %s, %s", i + 1, j + 1))));
            }

            if (i % 10 == 0) {
                table.flush();

                pdfDocument.getTagStructure().flushPageTags(pdfDocument.getPage(1));

                // This is a deliberate additional flush.
                table.flush();
            }
        }

        table.complete();

        doc.close();
        new CompareTool().compareTagStructures(outFileName, cmpFileName);
        assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private Paragraph createParagraph1() throws IOException {
        PdfFont font = PdfFontFactory.createStandardFont(FontConstants.HELVETICA_BOLD);
        Paragraph p = new Paragraph().add("text chunk. ").add("explicitly added separate text chunk");
        Text id = new Text("text chunk with specific font").setFont(font).setFontSize(8).setTextRise(6);
        p.add(id);
        return p;
    }

    private Paragraph createParagraph2() {
        Paragraph p;
        String alphabet = "abcdefghijklkmnopqrstuvwxyz";
        StringBuilder longTextBuilder = new StringBuilder();
        for (int i = 0; i < 26; ++i) {
            longTextBuilder.append(alphabet);
        }

        String longText = longTextBuilder.toString();
        p = new Paragraph(longText);
        return p;
    }
}
