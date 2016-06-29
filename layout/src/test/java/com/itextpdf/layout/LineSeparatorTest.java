package com.itextpdf.layout;

import com.itextpdf.kernel.color.Color;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.draw.DashedLine;
import com.itextpdf.kernel.pdf.canvas.draw.ILineDrawer;
import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.element.LineSeparator;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.property.HorizontalAlignment;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class LineSeparatorTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/LineSeparatorTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/layout/LineSeparatorTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void lineSeparatorWidthPercentageTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "lineSeparatorWidthPercentageTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_lineSeparatorWidthPercentageTest01.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);

        ILineDrawer line1 = new SolidLine();
        line1.setColor(Color.RED);
        ILineDrawer line2 = new SolidLine();
        document.add(new LineSeparator(line1).setWidth(50).setMarginBottom(10));
        document.add(new LineSeparator(line2).setWidthPercent(50));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void lineSeparatorBackgroundTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "lineSeparatorBackgroundTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_lineSeparatorBackgroundTest01.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);

        Style style = new Style();
        style.setBackgroundColor(Color.YELLOW);
        style.setMargin(10);
        document.add(new LineSeparator(new SolidLine()).addStyle(style));

        document.add(new LineSeparator(new DashedLine()).setBackgroundColor(Color.RED));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }


    @Test
    public void rotatedLineSeparatorTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "rotatedLineSeparatorTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_rotatedLineSeparatorTest01.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);

        document.add(new LineSeparator(new DashedLine()).setBackgroundColor(Color.RED).setRotationAngle(Math.PI / 2));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void rotatedLineSeparatorTest0() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "rotatedLineSeparatorTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_rotatedLineSeparatorTest02.pdf";
        PdfDocument pdf = new PdfDocument(new PdfWriter(outFileName));
        Document document = new Document(pdf);

        document.add(new Paragraph("Hello"));
        document.add(new LineSeparator(new DashedLine()).setWidth(100).setHorizontalAlignment(HorizontalAlignment.CENTER).
                setBackgroundColor(Color.GREEN).setRotationAngle(Math.PI / 4));
        document.add(new Paragraph("World"));

        document.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

}
