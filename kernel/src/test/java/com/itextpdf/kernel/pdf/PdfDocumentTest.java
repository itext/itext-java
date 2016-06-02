package com.itextpdf.kernel.pdf;

import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.io.source.DeflaterOutputStream;
import com.itextpdf.kernel.pdf.navigation.PdfDestination;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import static org.junit.Assert.*;

@Category(IntegrationTest.class)
public class PdfDocumentTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/PdfDocumentTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfDocumentTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void writingVersionTest01() throws IOException {
        // There is a possibility to override version in stamping mode
        String out = destinationFolder + "writing_pdf_version.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(out, new WriterProperties().setPdfVersion(PdfVersion.PDF_2_0)));

        assertEquals(PdfVersion.PDF_2_0, pdfDoc.getPdfVersion());

        pdfDoc.addNewPage();
        pdfDoc.close();

        PdfDocument assertPdfDoc = new PdfDocument(new PdfReader(out));
        assertEquals(PdfVersion.PDF_2_0, assertPdfDoc.getPdfVersion());
        assertPdfDoc.close();
    }

    @Test
    public void freeReferencesInObjectStream() throws IOException {
        PdfReader reader = new PdfReader(sourceFolder + "styledLineArts_Redacted.pdf");
        PdfWriter writer = new PdfWriter(new ByteArrayOutputStream());
        PdfDocument document = new PdfDocument(reader, writer, new StampingProperties().useAppendMode());
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
        byte[] b = ImageDataFactory.create(sourceFolder + "berlin2013.jpg").getData();
        com.itextpdf.io.source.ByteArrayOutputStream image =  new com.itextpdf.io.source.ByteArrayOutputStream();
        image.assignBytes(b, b.length);

        ByteArrayOutputStream byteArrayStream1 = new com.itextpdf.io.source.ByteArrayOutputStream();
        DeflaterOutputStream zip = new DeflaterOutputStream(byteArrayStream1, 9);
        image.writeTo(zip);

        ByteArrayOutputStream byteArrayStream2 = new com.itextpdf.io.source.ByteArrayOutputStream();
        DeflaterOutputStream zip2 = new DeflaterOutputStream(byteArrayStream2, -1);
        image.writeTo(zip2);

        Assert.assertTrue(byteArrayStream1.size() == byteArrayStream2.size());
        zip.close();
        zip2.close();
    }

    @Test
    public void testFreeReference() throws IOException, InterruptedException {
        PdfWriter writer = new PdfWriter(destinationFolder + "freeReference.pdf", new WriterProperties().setFullCompressionMode(false));
        PdfDocument pdfDocument = new PdfDocument(new PdfReader(sourceFolder + "baseFreeReference.pdf"), writer);
        pdfDocument.getPage(1).getResources().getPdfObject().getAsArray(new PdfName("d")).get(0).getIndirectReference().setFree();
        PdfStream pdfStream = new PdfStream();
        pdfStream.setData(new byte[]{24, 23, 67});
        pdfStream.makeIndirect(pdfDocument);
        pdfDocument.getPage(1).getResources().getPdfObject().getAsArray(new PdfName("d")).add(pdfStream);
        pdfDocument.close();
        assertNull(new CompareTool().compareByContent(destinationFolder + "freeReference.pdf", sourceFolder + "cmp_freeReference.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void checkAndResolveCircularReferences() throws IOException, InterruptedException {
        PdfReader pdfReader = new PdfReader(sourceFolder + "datasheet.pdf");
        PdfDocument pdfDocument = new PdfDocument(pdfReader, new PdfWriter(destinationFolder + "datasheet_mode.pdf"));
        PdfDictionary pdfObject = (PdfDictionary)pdfDocument.getPdfObject(53);
        pdfDocument.getPage(1).getResources().addForm(pdfObject);
        pdfDocument.close();
        assertNull(new CompareTool().compareByContent(destinationFolder + "datasheet_mode.pdf", sourceFolder + "cmp_datasheet_mode.pdf", "d:/", "diff_"));
    }

    @Test
    public void readEncryptedDocumentWithFullCompression() throws IOException {
        PdfReader reader = new PdfReader(new FileInputStream(sourceFolder + "source.pdf"), new ReaderProperties().setPassword("123".getBytes()));
        PdfDocument pdfDocument = new PdfDocument(reader);

        PdfDictionary form = pdfDocument.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);

        PdfDictionary field = form.getAsArray(PdfName.Fields).getAsDictionary(0);

        assertEquals("ch", field.getAsString(PdfName.T).toUnicodeString());
        assertEquals("SomeStringValueInDictionary", field.getAsDictionary(new PdfName("TestDic")).getAsString(new PdfName("TestString")).toUnicodeString());
        assertEquals("SomeStringValueInArray", field.getAsArray(new PdfName("TestArray")).getAsString(0).toUnicodeString());
        pdfDocument.close();
    }
}
