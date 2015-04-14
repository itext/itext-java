package com.itextpdf.canvas;

import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.basics.font.Type1Font;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.font.PdfType1Font;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.extgstate.PdfExtGState;
import com.itextpdf.core.testutils.CompareTool;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;

public class PdfExtGStateTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/canvas/PdfExtGStateTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/canvas/PdfExtGStateTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void egsTest1() throws Exception {
        final String destinationDocument = destinationFolder + "egsTest1.pdf";
        FileOutputStream fos = new FileOutputStream(destinationDocument);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);

        //Create page and canvas
        PdfPage page = document.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);

        //Create ExtGState and fill it with line width and font
        PdfExtGState egs = new PdfExtGState(document);
        egs.getPdfObject().put(PdfName.LW, new PdfNumber(5));
        PdfArray font = new PdfArray();
        PdfFont pdfFont = new PdfType1Font(document, new Type1Font(FontConstants.COURIER, ""));
        //TODO if uncomment - exception will generated
        //pdfFont.flush();
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
