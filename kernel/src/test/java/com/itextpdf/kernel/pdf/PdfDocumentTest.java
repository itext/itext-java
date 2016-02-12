package com.itextpdf.kernel.pdf;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.io.image.ImageFactory;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;


import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.zip.Deflater;
import java.util.zip.DeflaterOutputStream;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.experimental.categories.Category;


import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@Category(IntegrationTest.class)
public class PdfDocumentTest extends ExtendedITextTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDocumentTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfDocumentTest/";

    @BeforeClass
    static public void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void writingVersionTest01() throws IOException {
        // There is a possibility to override version in stamping mode
        String out = destinationFolder + "writing_pdf_version.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out), PdfVersion.PDF_2_0);

        assertEquals(PdfVersion.PDF_2_0, pdfDoc.getPdfVersion());

        pdfDoc.addNewPage();
        pdfDoc.close();

        PdfDocument assertPdfDoc = new PdfDocument(new PdfReader(out));
        assertEquals(PdfVersion.PDF_2_0, assertPdfDoc.getPdfVersion());
        assertPdfDoc.close();
    }

    @Test
    public void addOutlinesWithNamedDestinations01() throws IOException, InterruptedException {
        PdfReader reader = new PdfReader(new FileInputStream(sourceFolder + "iphone_user_guide.pdf"));
        String filename = destinationFolder + "outlinesWithNamedDestinations01.pdf";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);

        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        PdfArray array1 = new PdfArray();
        array1.add(pdfDoc.getPage(2).getPdfObject());
        array1.add(PdfName.XYZ);
        array1.add(new PdfNumber(36));
        array1.add(new PdfNumber(806));
        array1.add(new PdfNumber(0));

        PdfArray array2 = new PdfArray();
        array2.add(pdfDoc.getPage(3).getPdfObject());
        array2.add(PdfName.XYZ);
        array2.add(new PdfNumber(36));
        array2.add(new PdfNumber(806));
        array2.add(new PdfNumber(1.25));

        PdfArray array3 = new PdfArray();
        array3.add(pdfDoc.getPage(4).getPdfObject());
        array3.add(PdfName.XYZ);
        array3.add(new PdfNumber(36));
        array3.add(new PdfNumber(806));
        array3.add(new PdfNumber(1));

        pdfDoc.addNewName(new PdfString("test1"), array2);
        pdfDoc.addNewName(new PdfString("test2"), array3);
        pdfDoc.addNewName(new PdfString("test3"), array1);

        PdfOutline root = pdfDoc.getOutlines(false);
        if (root == null)
            root = new PdfOutline(pdfDoc);

        PdfOutline firstOutline = root.addOutline("Test1");
        firstOutline.addDestination(PdfDestination.makeDestination(new PdfString("test1")));
        PdfOutline secondOutline = root.addOutline("Test2");
        secondOutline.addDestination(PdfDestination.makeDestination(new PdfString("test2")));
        PdfOutline thirdOutline = root.addOutline("Test3");
        thirdOutline.addDestination(PdfDestination.makeDestination(new PdfString("test3")));
        pdfDoc.close();

        assertNull(new CompareTool().compareByContent(filename, sourceFolder + "cmp_outlinesWithNamedDestinations01.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void freeReferencesInObjectStream() throws IOException {
        PdfDocument document = new PdfDocument(new PdfReader(sourceFolder + "styledLineArts_Redacted.pdf"), new PdfWriter(new ByteArrayOutputStream()), true);
        PdfDictionary dict = new PdfDictionary();
        dict.makeIndirect(document);
        assertTrue(dict.getIndirectReference().getObjNumber() > 0);
    }

    @Test
    public void removeUnusedObjectsInWriterModeTest() throws IOException, InterruptedException {
        String filename = "removeUnusedObjectsInWriter.pdf";

        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filename));
        PdfDocument pdfDocument = new PdfDocument(writer);

        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        unusedDictionary.put(new PdfName("testName"), unusedArray);

        unusedDictionary.makeIndirect(pdfDocument);

        assertEquals(pdfDocument.getXref().size(), 8);
        //on closing, all unused objects shall not be written to resultant document
        pdfDocument.close();


        PdfReader testerReader = new PdfReader(destinationFolder + filename);
        PdfDocument testerDocument = new PdfDocument(testerReader);
        assertEquals(testerDocument.getXref().size(), 6);
        testerDocument.close();
    }

    @Test
    public void removeUnusedObjectsInStampingModeTest() throws IOException, InterruptedException {
        String filenameIn = "docWithUnusedObjects_1.pdf";
        String filenameOut = "removeUnusedObjectsInStamping.pdf";

        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filenameIn));
        PdfDocument pdfDocument = new PdfDocument(writer);

        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        unusedDictionary.put(new PdfName("testName"), unusedArray);

        unusedDictionary.makeIndirect(pdfDocument).flush();
        pdfDocument.close();


        PdfReader reader = new PdfReader(destinationFolder + filenameIn);
        PdfDocument doc = new PdfDocument(reader, new PdfWriter(new FileOutputStream(destinationFolder + filenameOut)));
        assertEquals(doc.getXref().size(), 8);
        //on closing, all unused objects shall not be written to resultant document
        doc.close();


        PdfReader testerReader = new PdfReader(destinationFolder + filenameOut);
        PdfDocument testerDocument = new PdfDocument(testerReader);
        assertEquals(testerDocument.getXref().size(), 6);
        testerDocument.close();
    }


    @Test
    public void addUnusedObjectsInWriterModeTest() throws IOException, InterruptedException {
        String filename = "addUnusedObjectsInWriter.pdf";

        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filename));
        PdfDocument pdfDocument = new PdfDocument(writer);

        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        unusedDictionary.put(new PdfName("testName"), unusedArray);

        unusedDictionary.makeIndirect(pdfDocument);

        assertEquals(pdfDocument.getXref().size(), 8);
        pdfDocument.setFlushUnusedObjects(true);
        pdfDocument.close();


        PdfReader testerReader = new PdfReader(destinationFolder + filename);
        PdfDocument testerDocument = new PdfDocument(testerReader);
        assertEquals(testerDocument.getXref().size(), 8);
        testerDocument.close();
    }

    @Test
    public void addUnusedObjectsInStampingModeTest() throws IOException, InterruptedException {
        String filenameIn = "docWithUnusedObjects_2.pdf";
        String filenameOut = "addUnusedObjectsInStamping.pdf";

        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filenameIn));
        PdfDocument pdfDocument = new PdfDocument(writer);

        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        unusedDictionary.put(new PdfName("testName"), unusedArray);

        unusedDictionary.makeIndirect(pdfDocument).flush();
        pdfDocument.close();


        PdfReader reader = new PdfReader(destinationFolder + filenameIn);
        PdfDocument doc = new PdfDocument(reader, new PdfWriter(new FileOutputStream(destinationFolder + filenameOut)));
        assertEquals(doc.getXref().size(), 8);
        doc.setFlushUnusedObjects(true);
        doc.close();


        PdfReader testerReader = new PdfReader(destinationFolder + filenameOut);
        PdfDocument testerDocument = new PdfDocument(testerReader);
        assertEquals(testerDocument.getXref().size(), 8);
        testerDocument.close();
    }

    @Test
    public void addUnusedStreamObjectsTest() throws IOException, InterruptedException {
        String filenameIn = "docWithUnusedObjects_3.pdf";


        PdfWriter writer = new PdfWriter(new FileOutputStream(destinationFolder + filenameIn));
        PdfDocument pdfDocument = new PdfDocument(writer);

        pdfDocument.addNewPage();

        PdfDictionary unusedDictionary = new PdfDictionary();
        PdfArray unusedArray = new PdfArray().makeIndirect(pdfDocument);
        unusedArray.add(new PdfNumber(42));
        PdfStream stream = new PdfStream(new byte[]{1, 2, 34, 45}, 0);
        unusedArray.add(stream);
        unusedDictionary.put(new PdfName("testName"), unusedArray);
        unusedDictionary.makeIndirect(pdfDocument).flush();
        pdfDocument.setFlushUnusedObjects(true);
        pdfDocument.close();

        PdfReader testerReader = new PdfReader(destinationFolder + filenameIn);
        PdfDocument testerDocument = new PdfDocument(testerReader);
        assertEquals(testerDocument.getXref().size(), 9);
        testerDocument.close();
    }

    @Test
    public void testImageCompressLevel() throws IOException {
        byte[] b = ImageFactory.getImage(sourceFolder+"berlin2013.jpg").getData();
        com.itextpdf.io.source.ByteArrayOutputStream image =  new com.itextpdf.io.source.ByteArrayOutputStream();
        image.assignBytes(b,b.length);

        ByteArrayOutputStream byteArrayStream1 = new com.itextpdf.io.source.ByteArrayOutputStream();
        Deflater deflater = new Deflater(9);
        DeflaterOutputStream zip = new DeflaterOutputStream(byteArrayStream1, deflater);
        image.writeTo(zip);
        zip.close();

        ByteArrayOutputStream byteArrayStream2 = new com.itextpdf.io.source.ByteArrayOutputStream();
        Deflater deflater2 = new Deflater(-1);
        DeflaterOutputStream zip2 = new DeflaterOutputStream(byteArrayStream2, deflater2);
        image.writeTo(zip2);
        zip2.close();
        deflater2.end();

        Assert.assertTrue(byteArrayStream1.size() == byteArrayStream2.size());
    }

    @Test
    public void testFreeReference() throws IOException, InterruptedException {
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder+ "baseFreeReference.pdf"), new PdfWriter(destinationFolder + "freeReference.pdf"));
        pdfDocument.getWriter().setFullCompression(false);
        pdfDocument.getPage(1).getResources().getPdfObject().getAsArray(new PdfName("d")).get(0).getIndirectReference().setFree();
        PdfStream pdfStream = new PdfStream();
        pdfStream.setData(new byte[]{24, 23, 67});
        pdfStream.makeIndirect(pdfDocument);
        pdfDocument.getPage(1).getResources().getPdfObject().getAsArray(new PdfName("d")).add(pdfStream);
        pdfDocument.close();
        assertNull(new CompareTool().compareByContent(destinationFolder + "freeReference.pdf", sourceFolder + "cmp_freeReference.pdf", destinationFolder, "diff_"));
    }
}
