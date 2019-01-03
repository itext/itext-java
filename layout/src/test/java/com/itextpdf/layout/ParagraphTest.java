package com.itextpdf.layout;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.TabAlignment;
import com.itextpdf.layout.property.TextAlignment;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;

public class ParagraphTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/layout/ParagraphTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/layout/ParagraphTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void cannotPlaceABigChunkOnALineTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "cannotPlaceABigChunkOnALineTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_cannotPlaceABigChunkOnALineTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Paragraph p = new Paragraph().setBorder(new SolidBorder(ColorConstants.YELLOW, 0));

        p.add(new Text("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa").setBorder(new SolidBorder(ColorConstants.RED, 0)));
        p.add(new Text("b").setFontSize(100).setBorder(new SolidBorder(ColorConstants.BLUE, 0)));
        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void cannotPlaceABigChunkOnALineTest02() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "cannotPlaceABigChunkOnALineTest02.pdf";
        String cmpFileName = sourceFolder + "cmp_cannotPlaceABigChunkOnALineTest02.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Paragraph p = new Paragraph().setBorder(new SolidBorder(ColorConstants.YELLOW, 0));
        p.add(new Text("smaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaall").setFontSize(5).setBorder(new SolidBorder(ColorConstants.RED, 0)));
        p.add(new Text("biiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiig").setFontSize(20).setBorder(new SolidBorder(ColorConstants.BLUE, 0)));

        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }

    @Test
    public void wordWasSplitAndItWillFitOntoNextLineTest01() throws IOException, InterruptedException {
        String outFileName = destinationFolder + "wordWasSplitAndItWillFitOntoNextLineTest01.pdf";
        String cmpFileName = sourceFolder + "cmp_wordWasSplitAndItWillFitOntoNextLineTest01.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFileName));

        Document doc = new Document(pdfDocument);

        Paragraph p = new Paragraph().setBorder(new SolidBorder(ColorConstants.YELLOW, 0)).setTextAlignment(TextAlignment.RIGHT);
        for (int i = 0; i < 5; i++) {
            p.add(new Text("aaaaaaaaaaaaaaaaaaaaa" + i).setBorder(new SolidBorder(ColorConstants.BLUE, 0)));
        }

        doc.add(p);

        doc.close();

        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff"));
    }
}
