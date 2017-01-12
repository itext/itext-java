package com.itextpdf.layout;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.border.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class NonBreakableSpaceTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/NonBreakableSpaceTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/NonBreakableSpaceTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void simpleParagraphTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "simpleParagraphTest.pdf";
        String cmpFileName = sourceFolder + "cmp_simpleParagraphTest.pdf";
        String diffPrefix = "diff_simpleParagraphTest_";

        Document document = new Document(new PdfDocument(new PdfWriter(outFileName)));
        document.add(new Paragraph("aaa bbb\u00a0ccccccccccc").setWidth(100).setBorder(new SolidBorder(Color.RED, 10)));
        document.add(new Paragraph("aaa bbb ccccccccccc").setWidth(100).setBorder(new SolidBorder(Color.GREEN, 10)));
        document.add(new Paragraph("aaaaaaa\u00a0bbbbbbbbbbb").setWidth(100).setBorder(new SolidBorder(Color.BLUE, 10)));
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, diffPrefix));
    }
    
    @Test
    public void consecutiveSpacesTest() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "consecutiveSpacesTest.pdf";
        String cmpFileName = sourceFolder + "cmp_consecutiveSpacesTest.pdf";
        String diffPrefix = "diff_consecutiveSpacesTest_";

        Document document = new Document(new PdfDocument(new PdfWriter(outFileName)));
        document.add(new Paragraph("aaa\u00a0\u00a0\u00a0bbb").setWidth(100).setBorder(new SolidBorder(Color.RED, 10)));
        document.add(new Paragraph("aaa\u00a0bbb").setWidth(100).setBorder(new SolidBorder(Color.GREEN, 10)));
        document.add(new Paragraph("aaa   bbb").setWidth(100).setBorder(new SolidBorder(Color.BLUE, 10)));
        document.add(new Paragraph("aaa bbb").setWidth(100).setBorder(new SolidBorder(Color.BLACK, 10)));
        Paragraph p = new Paragraph();
        p.add("aaa\u00a0\u00a0\u00a0bbb").add("ccc   ddd");
        document.add(p);
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, diffPrefix));
    }
}
