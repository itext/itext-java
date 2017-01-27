package com.itextpdf.layout.renderer;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.BlockElement;
import com.itextpdf.layout.element.Cell;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Image;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Table;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.minmaxwidth.MinMaxWidth;
import com.itextpdf.layout.minmaxwidth.MinMaxWidthUtils;
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
public class MinWidthTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/MinWidthTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/MinWidthTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void paragraphTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "paragraphTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_paragraphTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str).setBorder(new SolidBorder(Color.BLACK, 5))).setBorder(new SolidBorder(Color.BLUE, 5));
        MinMaxWidth result = ((AbstractRenderer)p.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth(doc.getPageEffectiveArea(PageSize.A4).getWidth());
        p.setWidth(toEffectiveWidth(p, result.getMinWidth()));
        doc.add(p);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void divTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_divTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str)).setPadding(1f).setBorder(new SolidBorder(Color.BLACK, 2)).setMargin(3).setBackgroundColor(Color.LIGHT_GRAY);
        Div d = new Div().setPadding(4f).setBorder(new SolidBorder(Color.GREEN, 5)).setMargin(6);
        d.add(p);
        MinMaxWidth result = ((AbstractRenderer)d.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth(doc.getPageEffectiveArea(PageSize.A4).getWidth());
        d.setWidth(toEffectiveWidth(d, result.getMinWidth()));
        doc.add(d);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void divWithSmallRotatedParagraph() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divSmallRotatedParagraphTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_divSmallRotatedParagraphTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str)).setPadding(1f).setBorder(new SolidBorder(Color.BLACK, 2)).setMargin(3).setBackgroundColor(Color.LIGHT_GRAY);
        Div d = new Div().setPadding(4f).setBorder(new SolidBorder(Color.GREEN, 5)).setMargin(6);
        d.add(new Paragraph(("iText")).setRotationAngle(Math.PI/8).setBorder(new SolidBorder(Color.BLUE, 2f)));
        d.add(p);
        MinMaxWidth result = ((AbstractRenderer)d.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth(doc.getPageEffectiveArea(PageSize.A4).getWidth());
        d.setWidth(toEffectiveWidth(d, result.getMinWidth()));
        doc.add(d);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void divWithBigRotatedParagraph() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divBigRotatedParagraphTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_divBigRotatedParagraphTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str)).setPadding(1f).setBorder(new SolidBorder(Color.BLACK, 2)).setMargin(3).setBackgroundColor(Color.LIGHT_GRAY).setRotationAngle(Math.PI/8);
        Div d = new Div().setPadding(4f).setBorder(new SolidBorder(Color.GREEN, 5)).setMargin(6);
        d.add(p);
        d.add(new Paragraph(("iText")));
        MinMaxWidth result = ((AbstractRenderer)d.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth(doc.getPageEffectiveArea(PageSize.A4).getWidth());
        d.setWidth(toEffectiveWidth(d, result.getMinWidth()));
        doc.add(d);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = LogMessageConstant.ELEMENT_DOES_NOT_FIT_AREA)})
    public void divWithSmallRotatedDiv() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divSmallRotatedDivTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_divSmallRotatedDivTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str)).setPadding(1f).setBorder(new SolidBorder(Color.BLACK, 2)).setMargin(3).setBackgroundColor(Color.LIGHT_GRAY);
        Div d = new Div().setPadding(4f).setBorder(new SolidBorder(Color.GREEN, 5)).setMargin(6);
        d.add(p);
        Div dRotated = new Div().setRotationAngle(Math.PI/8).setBorder(new SolidBorder(Color.BLUE, 2f));
        d.add(dRotated.add(new Paragraph(("iText"))));
        MinMaxWidth result = ((AbstractRenderer)d.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth(doc.getPageEffectiveArea(PageSize.A4).getWidth());
        d.setWidth(toEffectiveWidth(d, result.getMinWidth()));
        doc.add(d);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Test
    public void divWithBigRotatedDiv() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divBigRotatedDivTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_divBigRotatedDivTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str)).setPadding(1f).setBorder(new SolidBorder(Color.BLACK, 2)).setMargin(3).setBackgroundColor(Color.LIGHT_GRAY);
        Div dRotated = new Div().setPadding(4f).setBorder(new SolidBorder(Color.GREEN, 5)).setMargin(6);
        dRotated.add(p).setRotationAngle(Math.PI * 3 / 8);
        Div d = new Div().add(new Paragraph(("iText"))).add(dRotated).setBorder(new SolidBorder(Color.BLUE, 2f));
        MinMaxWidth result = ((AbstractRenderer)d.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth(doc.getPageEffectiveArea(PageSize.A4).getWidth());
        d.setWidth(toEffectiveWidth(d, result.getMinWidth()));
        doc.add(d);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void multipleDivTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "multipleDivTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_multipleDivTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);
        Border[] borders = {new SolidBorder(Color.BLUE, 2f), new SolidBorder(Color.RED, 2f), new SolidBorder(Color.GREEN, 2f)};

        Div externalDiv = new Div().setPadding(2f).setBorder(borders[2]);
        Div curr = externalDiv;
        for (int i = 0; i < 100; ++i) {
            Div d = new Div().setBorder(borders[i%3]);
            curr.add(d);
            curr = d;
        }

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str)).setPadding(1f).setBorder(new SolidBorder(Color.BLACK, 2)).setMargin(3).setBackgroundColor(Color.LIGHT_GRAY);
        curr.add(p);
        MinMaxWidth result = ((AbstractRenderer)externalDiv.createRendererSubTree().setParent(doc.getRenderer())).getMinMaxWidth(doc.getPageEffectiveArea(PageSize.A4).getWidth());
        externalDiv.setWidth(toEffectiveWidth(externalDiv, result.getMinWidth()));
        doc.add(externalDiv);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void simpleTableTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "simpleTableTest.pdf";
        String cmpFileName = sourceFolder + "cmp_simpleTableTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));
        Cell cell1 = new Cell().add("I am table")
                .setBorder(new SolidBorder(Color.RED, 60))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);
        Cell cell2 = new Cell().add("I am table")
                .setBorder(new SolidBorder(Color.YELLOW, 10))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);

        Table table = new Table(2).setBorder(new SolidBorder(Color.BLUE, 20))
                .addCell(cell1.clone(true)).addCell(cell2.clone(true))
                .addCell(cell1.clone(true)).addCell(cell2.clone(true));

        TableRenderer renderer = (TableRenderer) table.createRendererSubTree().setParent(doc.getRenderer());
        MinMaxWidth minMaxWidth = renderer.getMinMaxWidth(doc.getPageEffectiveArea(PageSize.A4).getWidth());

        Table minTable = new Table(toEffectiveTableColumnWidth(renderer.getMinColumnWidth()))
                .setWidth(toEffectiveWidth(table, minMaxWidth.getMinWidth()))
                .setMarginTop(10).setBorder(new SolidBorder(Color.BLUE, 20))
                .addCell(cell1.clone(true)).addCell(cell2.clone(true))
                .addCell(cell1.clone(true)).addCell(cell2.clone(true));

        Table maxTable = new Table(toEffectiveTableColumnWidth(renderer.getMaxColumnWidth()))
                .setWidth(toEffectiveWidth(table, minMaxWidth.getMaxWidth()))
                .setMarginTop(10).setBorder(new SolidBorder(Color.BLUE, 20))
                .addCell(cell1.clone(true)).addCell(cell2.clone(true))
                .addCell(cell1.clone(true)).addCell(cell2.clone(true));

        doc.add(table);
        doc.add(minTable);
        doc.add(maxTable);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void colspanTableTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "colspanTableTest.pdf";
        String cmpFileName = sourceFolder + "cmp_colspanTableTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));
        Cell bigCell = new Cell(1, 2).add("I am veryveryvery big cell")
                .setBorder(new SolidBorder(Color.RED, 60))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);
        Cell cell = new Cell().add("I am cell")
                .setBorder(new SolidBorder(Color.YELLOW, 10))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);

        Table table = new Table(3).setBorder(new SolidBorder(Color.BLUE, 20))
                .addCell(cell.clone(true)).addCell(bigCell.clone(true))
                .addCell(cell.clone(true)).addCell(cell.clone(true)).addCell(cell.clone(true));

        TableRenderer renderer = (TableRenderer) table.createRendererSubTree().setParent(doc.getRenderer());
        MinMaxWidth minMaxWidth = renderer.getMinMaxWidth(doc.getPageEffectiveArea(PageSize.A4).getWidth());

        Table minTable = new Table(toEffectiveTableColumnWidth(renderer.getMinColumnWidth()))
                .setWidth(toEffectiveWidth(table, minMaxWidth.getMinWidth()))
                .setMarginTop(10).setBorder(new SolidBorder(Color.BLUE, 20))
                .addCell(cell.clone(true)).addCell(bigCell.clone(true))
                .addCell(cell.clone(true)).addCell(cell.clone(true)).addCell(cell.clone(true));

        Table maxTable = new Table(toEffectiveTableColumnWidth(renderer.getMaxColumnWidth()))
                .setWidth(toEffectiveWidth(table, minMaxWidth.getMaxWidth()))
                .setMarginTop(10).setBorder(new SolidBorder(Color.BLUE, 20))
                .addCell(cell.clone(true)).addCell(bigCell.clone(true))
                .addCell(cell.clone(true)).addCell(cell.clone(true)).addCell(cell.clone(true));

        doc.add(table);
        doc.add(minTable);
        doc.add(maxTable);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void colspanRowspanTableTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "colspanRowspanTableTest.pdf";
        String cmpFileName = sourceFolder + "cmp_colspanRowspanTableTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));
        Cell colspanCell = new Cell(1, 2).add("I am veryveryvery big cell")
                .setBorder(new SolidBorder(Color.RED, 60))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);
        Cell rowspanCell = new Cell(2, 1).add("I am very very very long cell")
                .setBorder(new SolidBorder(Color.GREEN, 60))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);
        Cell cell = new Cell().add("I am cell")
                .setBorder(new SolidBorder(Color.BLUE, 10))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);

        Table table = new Table(3).setBorder(new SolidBorder(Color.BLACK, 20))
                .addCell(cell.clone(true)).addCell(cell.clone(true)).addCell(rowspanCell.clone(true))
                .addCell(colspanCell.clone(true));

        TableRenderer renderer = (TableRenderer) table.createRendererSubTree().setParent(doc.getRenderer());
        MinMaxWidth minMaxWidth = renderer.getMinMaxWidth(doc.getPageEffectiveArea(PageSize.A4).getWidth());

        Table minTable = new Table(toEffectiveTableColumnWidth(renderer.getMinColumnWidth()))
                .setWidth(toEffectiveWidth(table, minMaxWidth.getMinWidth()))
                .setMarginTop(10).setBorder(new SolidBorder(Color.BLACK, 20))
                .addCell(cell.clone(true)).addCell(cell.clone(true)).addCell(rowspanCell.clone(true))
                .addCell(colspanCell.clone(true));

        Table maxTable = new Table(toEffectiveTableColumnWidth(renderer.getMaxColumnWidth()))
                .setWidth(toEffectiveWidth(table, minMaxWidth.getMaxWidth()))
                .setMarginTop(10).setBorder(new SolidBorder(Color.BLACK, 20))
                .addCell(cell.clone(true)).addCell(cell.clone(true)).addCell(rowspanCell.clone(true))
                .addCell(colspanCell.clone(true));

        doc.add(table);
        doc.add(minTable);
        doc.add(maxTable);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void headerFooterTableTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "headerFooterTableTest.pdf";
        String cmpFileName = sourceFolder + "cmp_headerFooterTableTest.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(new PdfDocument(new PdfWriter(outFileName)));
        Cell bigCell = new Cell().add("veryveryveryvery big cell")
                .setBorder(new SolidBorder(Color.RED, 40))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);
        Cell mediumCell = new Cell().add("mediumsize cell")
                .setBorder(new SolidBorder(Color.GREEN, 30))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);
        Cell cell = new Cell().add("cell")
                .setBorder(new SolidBorder(Color.BLUE, 10))
                .setBorderBottom(Border.NO_BORDER)
                .setBorderTop(Border.NO_BORDER)
                .setPadding(0);

        Table table = new Table(3)
                .setBorder(new SolidBorder(Color.BLACK, 20))
                .addCell(mediumCell.clone(true)).addCell(mediumCell.clone(true)).addCell(mediumCell.clone(true))
                .addFooterCell(cell.clone(true)).addFooterCell(cell.clone(true)).addFooterCell(bigCell.clone(true))
                .addHeaderCell(bigCell.clone(true)).addHeaderCell(cell.clone(true)).addHeaderCell(cell.clone(true));

        TableRenderer renderer = (TableRenderer) table.createRendererSubTree().setParent(doc.getRenderer());
        MinMaxWidth minMaxWidth = renderer.getMinMaxWidth(doc.getPageEffectiveArea(PageSize.A4).getWidth());

        Table minTable = new Table(toEffectiveTableColumnWidth(renderer.getMinColumnWidth()))
                .setWidth(toEffectiveWidth(table, minMaxWidth.getMinWidth()))
                .setBorder(new SolidBorder(Color.BLACK, 20)).setMarginTop(20)
                .addCell(mediumCell.clone(true)).addCell(mediumCell.clone(true)).addCell(mediumCell.clone(true))
                .addFooterCell(cell.clone(true)).addFooterCell(cell.clone(true)).addFooterCell(bigCell.clone(true))
                .addHeaderCell(bigCell.clone(true)).addHeaderCell(cell.clone(true)).addHeaderCell(cell.clone(true));

        Table maxTable = new Table(toEffectiveTableColumnWidth(renderer.getMaxColumnWidth()))
                .setWidth(toEffectiveWidth(table, minMaxWidth.getMaxWidth()))
                .setBorder(new SolidBorder(Color.BLACK, 20)).setMarginTop(20)
                .addCell(mediumCell.clone(true)).addCell(mediumCell.clone(true)).addCell(mediumCell.clone(true))
                .addFooterCell(cell.clone(true)).addFooterCell(cell.clone(true)).addFooterCell(bigCell.clone(true))
                .addHeaderCell(bigCell.clone(true)).addHeaderCell(cell.clone(true)).addHeaderCell(cell.clone(true));

        doc.add(table);
        doc.add(minTable);
        doc.add(maxTable);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    private static float toEffectiveWidth(BlockElement b, float fullWidth) {
        if (b instanceof Table) {
            return fullWidth + ((Table) b).getNumberOfColumns() * MinMaxWidthUtils.getEps();
        } else {
            return fullWidth - MinMaxWidthUtils.getBorderWidth(b) - MinMaxWidthUtils.getMarginsWidth(b)
                             - MinMaxWidthUtils.getPaddingWidth(b) + MinMaxWidthUtils.getEps();
        }
    }

    private static float[] toEffectiveTableColumnWidth(float[] tableColumnWidth) {
        float[] result = tableColumnWidth.clone();
        for (int i = 0; i < result.length; ++i) {
            result[i] += MinMaxWidthUtils.getEps();
        }
        return result;
    }
}
