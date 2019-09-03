package com.itextpdf.pdfa;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.element.Image;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

@Category(IntegrationTest.class)
public class PDFA2LayoutOcgTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    public static final String destinationFolder = "./target/test/com/itextpdf/pdfa/PDFA2LayoutOcgTest/";

    @Before
    public void configure() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void checkIfOcgForPdfA2Works() throws IOException, InterruptedException {
        String fileName = "createdOcgPdfA.pdf";
        InputStream colorStream = new FileInputStream(sourceFolder + "color/sRGB_CS_profile.icm");
        String outFileName = destinationFolder + fileName;
        String cmpFileName = sourceFolder + "cmp/PDFA2LayoutOcg/cmp_" + fileName;
        PdfDocument pdfDoc = new PdfADocument(new PdfWriter(outFileName), PdfAConformanceLevel.PDF_A_2A,
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", colorStream));
        pdfDoc.setTagged();
        pdfDoc.getCatalog().setLang(new PdfString("en-US"));

        pdfDoc.addNewPage();

        Image image1 = new Image(ImageDataFactory.create(sourceFolder + "images/manualTransparency_for_png.png"));

        PdfCanvas pdfCanvas = new PdfCanvas(pdfDoc, 1);

        Canvas canvas1 = new Canvas(pdfCanvas, pdfDoc, new Rectangle(0, 0, 590, 420));
        PdfLayer imageLayer1 = new PdfLayer("*SomeTest_image$here@.1", pdfDoc);
        imageLayer1.setOn(true);
        pdfCanvas.beginLayer(imageLayer1);
        canvas1.add(image1);
        pdfCanvas.endLayer();

        canvas1.close();

        pdfDoc.close();
        Assert.assertNull(new CompareTool().compareByContent(outFileName, cmpFileName, destinationFolder, "diff01_"));
    }

}