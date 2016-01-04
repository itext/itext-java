package com.itextpdf.model;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.image.ImageFactory;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;
import com.itextpdf.core.testutils.CompareTool;
import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.model.Document;
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
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.assertNull;

@Category(IntegrationTest.class)
public class AutoTaggingTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/model/AutoTaggingTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/model/AutoTaggingTest/";
    static final public String imageName = "Desert.jpg";

    @BeforeClass
    static public void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void textInParagraphTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "textInParagraphTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_textInParagraphTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Paragraph p = createParagraph1(pdfDocument);
        document.add(p);

        for (int i = 0; i < 26; ++i) {
            document.add(createParagraph2());
        }

        document.close();

        assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void imageTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "imageTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_imageTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Image image = new Image(ImageFactory.getImage(sourceFolder + imageName));
        document.add(image);

        document.close();

        assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void divTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_divTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Div div = new Div();

        div.add(createParagraph1(pdfDocument));
        Image image = new Image(ImageFactory.getImage(sourceFolder + imageName));
        image.setAutoScale(true);
        div.add(image);
        div.add(createParagraph2());
        div.add(image);
        div.add(createParagraph2());

        document.add(div);

        document.close();

        assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void tableTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "tableTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_tableTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new FileOutputStream(outFileName)));
        pdfDocument.setTagged();

        Document document = new Document(pdfDocument);

        Table table = new Table(3);

        table.addCell(createParagraph1(pdfDocument));
        Image image = new Image(ImageFactory.getImage(sourceFolder + imageName));
        image.setAutoScale(true);
        table.addCell(image);
        table.addCell(createParagraph2());
        table.addCell(image);
        table.addCell("abcdefghijklkmnopqrstuvwxyz");
        table.addCell("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        document.add(table);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void tableTest02() throws IOException, InterruptedException {
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @Ignore("bug with footer when the last row is split")
    public void tableTest03() throws IOException, InterruptedException {
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

        table.addCell("little text");

        document.add(table);

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void tableTest04() throws IOException, InterruptedException {
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    /* TODO: incorrect header/footer tag structure
        1. If first header is skipped - header appears after TBody or TFoot
        2. THead/TFoot row cells are all contained in single row, even when header/footer is repeated on every page.
     */
    public void tableTest05() throws IOException, InterruptedException {
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
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    /* TODO: incorrect cell tags order when rowspan is used
        1. The tag of cell with rowspan is always the last in the TR
     */
    public void tableTest06() throws IOException, InterruptedException {
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void listTest01() throws IOException, InterruptedException {
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

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    /* TODO
        1. compare by tag structure
        5. lists tests
     */

    private Paragraph createParagraph1(PdfDocument pdfDocument) throws IOException {
        PdfFont font = PdfFont.createStandardFont(pdfDocument, FontConstants.HELVETICA_BOLD);
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
