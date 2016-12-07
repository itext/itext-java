package com.itextpdf.layout;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.element.WidthUtils;
import com.itextpdf.layout.layout.LayoutArea;
import com.itextpdf.layout.layout.LayoutContext;
import com.itextpdf.layout.layout.LayoutResult;
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
public class MinMaxWidthTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/MinMaxWidthTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/MinMaxWidthTest/";

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
        LayoutResult result = p.createRendererSubTree().setParent(doc.getRenderer()).layout(new LayoutContext(new LayoutArea(1, doc.getPageEffectiveArea(PageSize.A4))));
        p.setWidth(WidthUtils.toEffectiveWidth(p, result.getMinFullWidth()));
        doc.add(p);
        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
