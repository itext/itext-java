package com.itextpdf.pdfa;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.io.image.ImageFactory;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ITextTest;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.kernel.xmp.XMPException;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class PdfAFlushingTest extends ITextTest{
    static final public String sourceFolder = "./src/test/resources/com/itextpdf/pdfa/";
    static final public String destinationFolder = "./target/test/PdfAFlushingTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void flushingTest01() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA1b_flushingTest01.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFlushingTest/cmp_pdfA1b_flushingTest01.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        PdfImageXObject imageXObject = new PdfImageXObject(ImageFactory.getImage(sourceFolder + "Desert.jpg"));
        imageXObject.makeIndirect(doc);
        canvas.addXObject(imageXObject, new Rectangle(30, 300, 300, 300));

        imageXObject.flush();
        if (imageXObject.isFlushed()) {
            fail("Flushing of unchecked objects shall be forbidden.");
        }

        doc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void flushingTest02() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA2b_flushingTest02.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFlushingTest/cmp_pdfA2b_flushingTest02.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_2B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        PdfImageXObject imageXObject = new PdfImageXObject(ImageFactory.getImage(sourceFolder + "Desert.jpg"));
        imageXObject.makeIndirect(doc);
        canvas.addXObject(imageXObject, new Rectangle(30, 300, 300, 300));

        PdfPage lastPage = doc.getLastPage();
        lastPage.flush();
        if (lastPage.isFlushed()) {
            fail("Flushing of unchecked objects shall be forbidden.");
        }

        doc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void flushingTest03() throws IOException, XMPException, InterruptedException {
        String outPdf = destinationFolder + "pdfA3b_flushingTest03.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFlushingTest/cmp_pdfA3b_flushingTest03.pdf";

        PdfWriter writer = new PdfWriter(outPdf);
        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument doc = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_3B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        doc.createXmpMetadata();

        PdfCanvas canvas = new PdfCanvas(doc.addNewPage());
        PdfImageXObject imageXObject = new PdfImageXObject(ImageFactory.getImage(sourceFolder + "Desert.jpg"));
        canvas.addXObject(imageXObject, new Rectangle(30, 300, 300, 300));

        PdfPage lastPage = doc.getLastPage();
        lastPage.flush(true);
        if (!imageXObject.isFlushed()) {
            fail("When flushing the page along with it's resources, page check should be performed also page and all resources should be flushed.");
        }

        doc.close();

        compareResult(outPdf, cmpPdf);
    }

    @Test
    public void addUnusedStreamObjectsTest() throws IOException, InterruptedException, XMPException {
        String outPdf = destinationFolder + "pdfA1b_docWithUnusedObjects_3.pdf";
        String cmpPdf = sourceFolder + "cmp/PdfAFlushingTest/cmp_pdfA1b_docWithUnusedObjects_3.pdf";

        PdfWriter writer = new PdfWriter(outPdf);

        InputStream is = new FileInputStream(sourceFolder + "sRGB Color Space Profile.icm");
        PdfADocument pdfDocument = new PdfADocument(writer, PdfAConformanceLevel.PDF_A_1B, new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is));
        pdfDocument.createXmpMetadata();
        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        PdfStream stream = new PdfStream(new byte[]{1, 2, 34, 45}, 0);
        unusedArray.add(stream);
        unusedDictionary.put(new PdfName("testName"), unusedArray);
        unusedDictionary.makeIndirect(pdfDocument).flush();
        unusedDictionary.flush();
        pdfDocument.close();

        PdfReader testerReader = new PdfReader(outPdf);
        PdfDocument testerDocument = new PdfDocument(testerReader);

        assertEquals(testerDocument.listIndirectReferences().size(), 11);

        testerDocument.close();

        compareResult(outPdf, cmpPdf);
    }

    private void compareResult(String outFile, String cmpFile) throws IOException, InterruptedException {
        String differences = new CompareTool().compareByContent(outFile, cmpFile, destinationFolder, "diff_");
        if (differences != null) {
            fail(differences);
        }
    }
}
