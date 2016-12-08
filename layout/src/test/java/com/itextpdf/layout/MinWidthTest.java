package com.itextpdf.layout;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.border.Border;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.element.MinMaxWidthUtils;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
import com.itextpdf.layout.renderer.AbstractRenderer;
import com.itextpdf.test.ExtendedITextTest;
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
        LayoutResult result = MinMaxWidthUtils.tryLayoutWithInfHeight(p.createRendererSubTree().setParent(doc.getRenderer()), doc.getPageEffectiveArea(PageSize.A4).getWidth());
        p.setWidth(MinMaxWidthUtils.toEffectiveWidth(p, result.getMinFullWidth()));
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
        LayoutResult result = MinMaxWidthUtils.tryLayoutWithInfHeight(d.createRendererSubTree().setParent(doc.getRenderer()), doc.getPageEffectiveArea(PageSize.A4).getWidth());
        d.setWidth(MinMaxWidthUtils.toEffectiveWidth(d, result.getMinFullWidth()));
        doc.add(d);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void divWithSmallRotatedParagraph() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "divSmallRotatedParagraphTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_divSmallRotatedParagraphTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        String str = "Hello. I am a fairly long paragraph. I really want you to process me correctly. You heard that? Correctly!!! Even if you will have to wrap me.";
        Paragraph p = new Paragraph(new Text(str)).setPadding(1f).setBorder(new SolidBorder(Color.BLACK, 2)).setMargin(3).setBackgroundColor(Color.LIGHT_GRAY);
        Div d = new Div().setPadding(4f).setBorder(new SolidBorder(Color.GREEN, 5)).setMargin(6);
        d.add(p);
        d.add(new Paragraph(("iText")).setRotationAngle(Math.PI/8).setBorder(new SolidBorder(Color.BLUE, 2f)));
        LayoutResult result = MinMaxWidthUtils.tryLayoutWithInfHeight(d.createRendererSubTree().setParent(doc.getRenderer()), doc.getPageEffectiveArea(PageSize.A4).getWidth());
        d.setWidth(MinMaxWidthUtils.toEffectiveWidth(d, result.getMinFullWidth()));
        doc.add(d);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Test
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
        LayoutResult result = MinMaxWidthUtils.tryLayoutWithInfHeight(d.createRendererSubTree().setParent(doc.getRenderer()), doc.getPageEffectiveArea(PageSize.A4).getWidth());
        d.setWidth(MinMaxWidthUtils.toEffectiveWidth(d, result.getMinFullWidth()));
        doc.add(d);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
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
        LayoutResult result = MinMaxWidthUtils.tryLayoutWithInfHeight(d.createRendererSubTree().setParent(doc.getRenderer()), doc.getPageEffectiveArea(PageSize.A4).getWidth());
        d.setWidth(MinMaxWidthUtils.toEffectiveWidth(d, result.getMinFullWidth()));
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
        LayoutResult result = MinMaxWidthUtils.tryLayoutWithInfHeight(d.createRendererSubTree().setParent(doc.getRenderer()), doc.getPageEffectiveArea(PageSize.A4).getWidth());
        d.setWidth(MinMaxWidthUtils.toEffectiveWidth(d, result.getMinFullWidth()));
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
        LayoutResult result = MinMaxWidthUtils.tryLayoutWithInfHeight(externalDiv.createRendererSubTree().setParent(doc.getRenderer()), doc.getPageEffectiveArea(PageSize.A4).getWidth());
        externalDiv.setWidth(MinMaxWidthUtils.toEffectiveWidth(externalDiv, result.getMinFullWidth()));
        doc.add(externalDiv);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
