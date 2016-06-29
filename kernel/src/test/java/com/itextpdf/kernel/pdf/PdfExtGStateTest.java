package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfExtGStateTest extends ExtendedITextTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfExtGStateTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/PdfExtGStateTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void egsTest1() throws Exception {
        String destinationDocument = destinationFolder + "egsTest1.pdf";
        PdfDocument document = new PdfDocument(new PdfWriter(destinationDocument));

        //Create page and canvas
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        //Create ExtGState and fill it with line width and font
        PdfExtGState egs = new PdfExtGState();
        egs.getPdfObject().put(PdfName.LW, new PdfNumber(5));
        PdfArray font = new PdfArray();
        PdfFont pdfFont = PdfFontFactory.createFont(FontConstants.COURIER);
        pdfFont.flush();
        font.add(pdfFont.getPdfObject());
        font.add(new PdfNumber(24));
        egs.getPdfObject().put(PdfName.Font, font);

        //Write ExtGState
        canvas.setExtGState(egs);

        //Write text to check that font from ExtGState is applied
        canvas.beginText();
        canvas.moveText(50, 600);
        canvas.showText("Courier, 24pt");
        canvas.endText();

        //Draw line to check if ine width is applied
        canvas.moveTo(50, 500);
        canvas.lineTo(300, 500);
        canvas.stroke();

        //Write text again to check that font from page resources and font from ExtGState is the same.
        canvas.beginText();
        canvas.setFontAndSize(pdfFont, 36);
        canvas.moveText(50, 400);
        canvas.showText("Courier, 36pt");
        canvas.endText();
        canvas.release();

        page.flush();
        document.close();

        Assert.assertNull(new CompareTool().compareByContent(destinationDocument, sourceFolder + "cmp_egsTest1.pdf", destinationFolder, "diff_"));
    }

}
