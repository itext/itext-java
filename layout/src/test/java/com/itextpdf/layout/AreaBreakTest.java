package com.itextpdf.layout;

import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.layout.renderer.DocumentRenderer;
import com.itextpdf.layout.renderer.IRenderer;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;

@Category(IntegrationTest.class)
public class AreaBreakTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/AreaBreakTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/AreaBreakTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void pageBreakTest1() throws IOException,  InterruptedException {
        String outFileName = destinationFolder + "pageBreak1.pdf";
        String cmpFileName = sourceFolder + "cmp_pageBreak1.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        document.add(new AreaBreak());

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void pageBreakTest2() throws IOException,  InterruptedException {
        String outFileName = destinationFolder + "pageBreak2.pdf";
        String cmpFileName = sourceFolder + "cmp_pageBreak2.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        document.add(new Paragraph("Hello World!")).add(new AreaBreak(new PageSize(200, 200)));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void pageBreakTest03() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "pageBreak3.pdf";
        String cmpFileName = sourceFolder + "cmp_pageBreak3.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);
        document.setRenderer(new ColumnDocumentRenderer(document, new Rectangle[] {new Rectangle(30, 30, 200, 600), new Rectangle(300, 30, 200, 600)}));

        document.add(new Paragraph("Hello World!")).add(new AreaBreak(AreaBreakType.NEXT_PAGE)).add(new Paragraph("New page hello world"));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void pageBreakTest04() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "pageBreak4.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        AreaBreak areaBreak = new AreaBreak(AreaBreakType.NEXT_PAGE);
        document.add(new Paragraph("Hello World!")).add(areaBreak);
        IRenderer areaBreakRenderer = areaBreak.getRenderer();
        Assert.assertNull(areaBreakRenderer.getParent());
        Assert.assertEquals(areaBreakRenderer, areaBreakRenderer.getRoot());
    }

    @Test
    public void lastPageAreaBreakTest() throws IOException, InterruptedException {
        String inputFileName = sourceFolder + "input.pdf";
        String cmpFileName = sourceFolder + "cmp_lastPageAreaBreakTest.pdf";
        String outFileName = destinationFolder + "lastPageAreaBreakTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(inputFileName), new PdfWriter(outFileName));

        Document document = new Document(pdfDocument);

        document.add(new AreaBreak(AreaBreakType.LAST_PAGE)).add(new Paragraph("Hello there on the last page!").setFontSize(30).setWidth(200).setMarginTop(250));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}
