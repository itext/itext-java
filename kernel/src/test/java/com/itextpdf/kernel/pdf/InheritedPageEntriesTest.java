package com.itextpdf.kernel.pdf;

import com.itextpdf.io.font.constants.StandardFonts;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class InheritedPageEntriesTest extends ExtendedITextTest {
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/InheritedPageEntriesTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/InheritedPageEntriesTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    //TODO: update cmp-files when DEVSIX-3635 will be fixed
    public void addNewPageToDocumentWithInheritedPageRotationTest() throws InterruptedException, IOException {
        String inputFileName = sourceFolder + "srcFileTestRotationInheritance.pdf";
        String outputFileName = destinationFolder + "addNewPageToDocumentWithInheritedPageRotation.pdf";
        String cmpFileName = sourceFolder + "cmp_addNewPageToDocumentWithInheritedPageRotation.pdf";

        PdfDocument outFile = new PdfDocument(new PdfReader(inputFileName), new PdfWriter(outputFileName));

        PdfPage page = outFile.addNewPage();
        PdfCanvas canvas = new PdfCanvas(page);
        canvas
                .beginText()
                .moveText(36, 750)
                .setFontAndSize(PdfFontFactory.createFont(StandardFonts.HELVETICA), 16)
                .showText("Hello Helvetica!")
                .endText()
                .saveState();

        outFile.close();

        Assert.assertNull(new CompareTool().compareByContent(outputFileName, cmpFileName, destinationFolder));
    }

    @Test
    public void mediaBoxInheritance() throws IOException {
        String inputFileName = sourceFolder + "mediaBoxInheritanceTestSource.pdf";

        PdfDocument outFile = new PdfDocument(new PdfReader(inputFileName));

        PdfObject mediaBox = outFile.getPage(1).getPdfObject().get(PdfName.MediaBox);
        //Check if MediaBox in Page is absent
        Assert.assertNull(mediaBox);

        PdfArray array = outFile.getCatalog().getPdfObject().getAsDictionary(PdfName.Pages).getAsArray(PdfName.MediaBox);
        Rectangle rectangle = array.toRectangle();

        Rectangle pageRect = outFile.getPage(1).getMediaBox();

        outFile.close();

        Assert.assertTrue(rectangle.equalsWithEpsilon(pageRect));
    }
    
    @Test
    public void cropBoxInheritance() throws IOException {
        String inputFileName = sourceFolder + "cropBoxInheritanceTestSource.pdf";

        PdfDocument outFile = new PdfDocument(new PdfReader(inputFileName));

        PdfObject cropBox = outFile.getPage(1).getPdfObject().get(PdfName.CropBox);
        //Check if CropBox in Page is absent
        Assert.assertNull(cropBox);

        PdfArray array = outFile.getCatalog().getPdfObject().getAsDictionary(PdfName.Pages).getAsArray(PdfName.CropBox);
        Rectangle rectangle = array.toRectangle();

        Rectangle pageRect = outFile.getPage(1).getCropBox();

        outFile.close();

        Assert.assertTrue(rectangle.equalsWithEpsilon(pageRect));
    }
}