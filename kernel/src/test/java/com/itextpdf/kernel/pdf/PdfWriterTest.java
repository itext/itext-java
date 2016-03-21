package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.io.source.OutputStream;
import com.itextpdf.test.annotations.type.IntegrationTest;
import com.itextpdf.test.ExtendedITextTest;


import java.io.ByteArrayInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfWriterTest extends ExtendedITextTest {

    static final public String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/PdfWriterTest/";

    @BeforeClass
    static public void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void createEmptyDocument() throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "emptyDocument.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.close();

        PdfReader reader = new PdfReader(destinationFolder + "emptyDocument.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        Assert.assertNotNull(pdfDocument.getPage(1));
        String date = pdfDocument.getDocumentInfo().getPdfObject().getAsString(PdfName.CreationDate).getValue();
        Calendar cl = PdfDate.decode(date);
        long diff = new GregorianCalendar().getTimeInMillis() - cl.getTimeInMillis();
        String message = "Unexpected creation date. Different from now is " + (float)diff/1000 + "s";
        Assert.assertTrue(message, diff < 5000);
        reader.close();

    }

    @Test
    public void useObjectForMultipleTimes1() throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "useObjectForMultipleTimes1.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfDictionary helloWorld = new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        pdfDoc.getCatalog().getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes1.pdf");
    }

    @Test
    public void useObjectForMultipleTimes2() throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "useObjectForMultipleTimes2.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfDictionary helloWorld = new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        helloWorld.flush();
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        pdfDoc.getCatalog().getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes2.pdf");
    }

    @Test
    public void useObjectForMultipleTimes3() throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "useObjectForMultipleTimes3.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfDictionary helloWorld = new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        helloWorld.flush();
        pdfDoc.getCatalog().getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes3.pdf");
    }

    @Test
    public void useObjectForMultipleTimes4() throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "useObjectForMultipleTimes4.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfDictionary helloWorld = new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        pdfDoc.getCatalog().getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        helloWorld.flush();
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes4.pdf");
    }

    private void validateUseObjectForMultipleTimesTest(String filename) throws IOException {
        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDoc = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary page = pdfDoc.getPage(1).getPdfObject();
        Assert.assertNotNull(page);
        PdfDictionary helloWorld = page.getAsDictionary(new PdfName("HelloWorld"));
        Assert.assertNotNull(helloWorld);
        PdfString world = helloWorld.getAsString(new PdfName("Hello"));
        Assert.assertEquals("World", world.toString());
        helloWorld = pdfDoc.getCatalog().getPdfObject().getAsDictionary(new PdfName("HelloWorld"));
        Assert.assertNotNull(helloWorld);
        world = helloWorld.getAsString(new PdfName("Hello"));
        Assert.assertEquals("World", world.toString());
        reader.close();
    }

    /**
     * Copying direct objects. Objects of all types are added into document catalog.
     *
     * @throws IOException
     * @throws PdfException
     */
    @Test
    public void copyObject1() throws IOException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyObject1_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.flush();
        PdfDictionary catalog1 = pdfDoc1.getCatalog().getPdfObject();
        PdfArray aDirect = new PdfArray();
        aDirect.add(new PdfArray(new ArrayList<PdfObject>() {{
            add(new PdfNumber(1));
            add(new PdfNumber(2));
        }}));
        aDirect.add(new PdfBoolean(true));
        aDirect.add(new PdfDictionary(new TreeMap<PdfName, PdfObject>() {{
            put(new PdfName("one"), new PdfNumber(1));
            put(new PdfName("two"), new PdfNumber(2));
        }}));
        aDirect.add(new PdfName("name"));
        aDirect.add(new PdfNull());
        aDirect.add(new PdfNumber(100));
        aDirect.add(new PdfString("string"));
        catalog1.put(new PdfName("aDirect"), aDirect);

        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyObject1_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.flush();
        PdfDictionary catalog2 = pdfDoc2.getCatalog().getPdfObject();
        catalog2.put(new PdfName("aDirect"), aDirect.copyTo(pdfDoc2));

        pdfDoc1.close();
        pdfDoc2.close();

        PdfReader reader = new PdfReader(destinationFolder + "copyObject1_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary catalog = pdfDocument.getCatalog().getPdfObject();
        PdfArray a = (PdfArray) catalog.get(new PdfName("aDirect"));
        Assert.assertNotNull(a);
        Assert.assertEquals(1, ((PdfNumber) ((PdfArray) a.get(0)).get(0)).getIntValue());
        Assert.assertEquals(2, ((PdfNumber) ((PdfArray) a.get(0)).get(1)).getIntValue());
        Assert.assertEquals(true, ((PdfBoolean) a.get(1)).getValue());
        Assert.assertEquals(1, ((PdfNumber) ((PdfDictionary) a.get(2)).get(new PdfName("one"))).getIntValue());
        Assert.assertEquals(2, ((PdfNumber) ((PdfDictionary) a.get(2)).get(new PdfName("two"))).getIntValue());
        Assert.assertEquals(new PdfName("name"), a.get(3));
        Assert.assertTrue(a.get(4).isNull());
        Assert.assertEquals(100, ((PdfNumber) a.get(5)).getIntValue());
        Assert.assertEquals("string", ((PdfString) a.get(6)).toUnicodeString());
        reader.close();

    }

    /**
     * Copying objects, some of those are indirect. Objects of all types are added into document catalog.
     *
     * @throws IOException
     * @throws PdfException
     */
    @Test
    public void copyObject2() throws IOException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyObject2_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        final PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.flush();
        PdfDictionary catalog1 = pdfDoc1.getCatalog().getPdfObject();
        PdfName aDirectName = new PdfName("aDirect");
        PdfArray aDirect = new PdfArray().makeIndirect(pdfDoc1);
        aDirect.add(new PdfArray(new ArrayList<PdfObject>() {{
            add(new PdfNumber(1));
            add(new PdfNumber(2).makeIndirect(pdfDoc1));
        }}));
        aDirect.add(new PdfBoolean(true));
        aDirect.add(new PdfDictionary(new TreeMap<PdfName, PdfObject>() {{
            put(new PdfName("one"), new PdfNumber(1));
            put(new PdfName("two"), new PdfNumber(2).makeIndirect(pdfDoc1));
        }}));
        aDirect.add(new PdfName("name"));
        aDirect.add(new PdfNull().makeIndirect(pdfDoc1));
        aDirect.add(new PdfNumber(100));
        aDirect.add(new PdfString("string"));
        catalog1.put(aDirectName, aDirect);
        pdfDoc1.close();

        PdfDocument pdfDoc1R = new PdfDocument(new com.itextpdf.kernel.pdf.PdfReader(destinationFolder + "copyObject2_1.pdf"));
        aDirect = (PdfArray) pdfDoc1R.getCatalog().getPdfObject().get(aDirectName);

        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyObject2_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.flush();
        PdfDictionary catalog2 = pdfDoc2.getCatalog().getPdfObject();
        catalog2.put(aDirectName, aDirect.copyTo(pdfDoc2));

        pdfDoc1R.close();
        pdfDoc2.close();

        PdfReader reader = new PdfReader(destinationFolder + "copyObject2_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary catalog = pdfDocument.getCatalog().getPdfObject();
        PdfArray a = catalog.getAsArray(new PdfName("aDirect"));
        Assert.assertNotNull(a);
        Assert.assertEquals(1, ((PdfNumber) ((PdfArray) a.get(0)).get(0)).getIntValue());
        Assert.assertEquals(2, ((PdfArray) a.get(0)).getAsNumber(1).getIntValue());
        Assert.assertEquals(true, ((PdfBoolean) a.get(1)).getValue());
        Assert.assertEquals(1, ((PdfNumber) ((PdfDictionary) a.get(2)).get(new PdfName("one"))).getIntValue());
        Assert.assertEquals(2, ((PdfDictionary) a.get(2)).getAsNumber(new PdfName("two")).getIntValue());
        Assert.assertEquals(new PdfName("name"), a.get(3));

        Assert.assertTrue(a.get(4).isNull());
        Assert.assertEquals(100, ((PdfNumber) a.get(5)).getIntValue());
        Assert.assertEquals("string", ((PdfString) a.get(6)).toUnicodeString());
        reader.close();
    }

    /**
     * Copy objects recursively.
     *
     * @throws IOException
     * @throws PdfException
     */
    @Test
    public void copyObject3() throws IOException {
        {
            FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyObject3_1.pdf");
            PdfWriter writer1 = new PdfWriter(fos1);
            final PdfDocument pdfDoc1 = new PdfDocument(writer1);
            PdfPage page1 = pdfDoc1.addNewPage();
            page1.flush();
            PdfDictionary catalog1 = pdfDoc1.getCatalog().getPdfObject();
            PdfArray arr1 = new PdfArray().makeIndirect(pdfDoc1);
            PdfArray arr2 = new PdfArray().makeIndirect(pdfDoc1);
            arr1.add(arr2);
            PdfDictionary dic1 = new PdfDictionary().makeIndirect(pdfDoc1);
            arr2.add(dic1);
            PdfDictionary dic2 = new PdfDictionary().makeIndirect(pdfDoc1);
            dic1.put(new PdfName("dic2"), dic2);
            PdfName arr1Name = new PdfName("arr1");
            dic2.put(arr1Name, arr1);
            catalog1.put(arr1Name, arr1);
            pdfDoc1.close();

            PdfDocument pdfDoc1R = new PdfDocument(new com.itextpdf.kernel.pdf.PdfReader(destinationFolder + "copyObject3_1.pdf"));
            arr1 = (PdfArray) pdfDoc1R.getCatalog().getPdfObject().get(arr1Name);

            FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyObject3_2.pdf");
            PdfWriter writer2 = new PdfWriter(fos2);
            PdfDocument pdfDoc2 = new PdfDocument(writer2);
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.flush();
            PdfDictionary catalog2 = pdfDoc2.getCatalog().getPdfObject();
            catalog2.put(arr1Name, arr1.copyTo(pdfDoc2));

            pdfDoc1R.close();
            pdfDoc2.close();
        }

        {
            PdfReader reader = new PdfReader(destinationFolder + "copyObject3_2.pdf");
            PdfDocument pdfDocument = new PdfDocument(reader);
            Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
            PdfDictionary catalog = pdfDocument.getCatalog().getPdfObject();
            PdfArray arr1 = catalog.getAsArray(new PdfName("arr1"));
            PdfArray arr2 = arr1.getAsArray(0);
            PdfDictionary dic1 = arr2.getAsDictionary(0);
            PdfDictionary dic2 = dic1.getAsDictionary(new PdfName("dic2"));
            Assert.assertEquals(arr1, dic2.getAsArray(new PdfName("arr1")));
            reader.close();
        }
    }

    /**
     * Copies stream.
     *
     * @throws IOException
     * @throws PdfException
     */
    @Test
    public void copyObject4() throws IOException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyObject4_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        final PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.flush();
        PdfDictionary catalog1 = pdfDoc1.getCatalog().getPdfObject();
        PdfStream stream1 = new PdfStream().makeIndirect(pdfDoc1);
        stream1.getOutputStream().write(new PdfArray(new ArrayList<PdfObject>() {{
            add(new PdfNumber(1));
            add(new PdfNumber(2));
            add(new PdfNumber(3));
        }}));
        catalog1.put(new PdfName("stream"), stream1);
        pdfDoc1.close();

        PdfDocument pdfDoc1R = new PdfDocument(new com.itextpdf.kernel.pdf.PdfReader(destinationFolder + "copyObject4_1.pdf"));
        stream1 = (PdfStream) pdfDoc1R.getCatalog().getPdfObject().get(new PdfName("stream"));

        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyObject4_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.flush();
        PdfDictionary catalog2 = pdfDoc2.getCatalog().getPdfObject();
        catalog2.put(new PdfName("stream"), stream1.copyTo(pdfDoc2));

        pdfDoc1R.close();
        pdfDoc2.close();

        PdfReader reader = new PdfReader(destinationFolder + "copyObject4_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        PdfDictionary catalog = pdfDocument.getCatalog().getPdfObject();
        PdfStream stream = (PdfStream)catalog.getAsStream(new PdfName("stream"));
        byte[] bytes = stream.getBytes();
        Assert.assertArrayEquals(OutputStream.getIsoBytes("[1 2 3]"), bytes);
        reader.close();
    }

    /**
     * Copies page.
     *
     * @throws IOException
     * @throws PdfException
     */
    @Test
    public void copyObject5() throws IOException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyObject5_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        final PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfOutputStream.getIsoBytes("%Page_1"));
        page1.flush();
        pdfDoc1.close();

        PdfDocument pdfDoc1R = new PdfDocument(new com.itextpdf.kernel.pdf.PdfReader(destinationFolder + "copyObject5_1.pdf"));
        page1 = pdfDoc1R.getPage(1);

        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyObject5_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        PdfPage page2 = page1.copyTo(pdfDoc2);
        pdfDoc2.addPage(page2);
        page2.flush();
        page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(PdfOutputStream.getIsoBytes("%Page_2"));

        page2.flush();
        pdfDoc1R.close();
        pdfDoc2.close();

        PdfReader reader = new PdfReader(destinationFolder + "copyObject5_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        Assert.assertEquals(8, reader.trailer.getAsNumber(PdfName.Size).getIntValue());
        byte[] bytes = pdfDocument.getPage(1).getContentBytes();
        Assert.assertArrayEquals(PdfOutputStream.getIsoBytes("%Page_1"), bytes);
        bytes = pdfDocument.getPage(2).getContentBytes();
        Assert.assertArrayEquals(PdfOutputStream.getIsoBytes("%Page_2"), bytes);
        reader.close();
    }

    /**
     * Copies object with different method overloads.
     *
     * @throws IOException
     */
    @Test
    public void copyObject6() throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "copyObject6_1.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfDictionary helloWorld = new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        PdfPage page = pdfDoc.addNewPage();
        page.getPdfObject().put(new PdfName("HelloWorld"), helloWorld);
        pdfDoc.close();

        pdfDoc = new PdfDocument(new com.itextpdf.kernel.pdf.PdfReader(destinationFolder + "copyObject6_1.pdf"));
        helloWorld = (PdfDictionary) pdfDoc.getPage(1).getPdfObject().get(new PdfName("HelloWorld"));
        PdfDocument pdfDoc1 = new PdfDocument(new PdfWriter(new FileOutputStream(destinationFolder + "copyObject6_2.pdf")));
        PdfPage page1 = pdfDoc1.addNewPage();

        page1.getPdfObject().put(new PdfName("HelloWorldCopy1"), helloWorld.copyTo(pdfDoc1));
        page1.getPdfObject().put(new PdfName("HelloWorldCopy2"), helloWorld.copyTo(pdfDoc1, true));
        page1.getPdfObject().put(new PdfName("HelloWorldCopy3"), helloWorld.copyTo(pdfDoc1, false));
        page1.flush();

        pdfDoc.close();
        pdfDoc1.close();


        PdfReader reader = new PdfReader(destinationFolder + "copyObject6_2.pdf");
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());

        PdfObject obj1 = pdfDocument.getPage(1).getPdfObject().get(new PdfName("HelloWorldCopy1"));

        PdfIndirectReference ref1 = obj1.getIndirectReference();
        Assert.assertEquals(6, ref1.objNr);
        Assert.assertEquals(0, ref1.genNr);

        PdfObject obj2 = pdfDocument.getPage(1).getPdfObject().get(new PdfName("HelloWorldCopy2"));

        PdfIndirectReference ref2 =  obj2.getIndirectReference();
        Assert.assertEquals(7, ref2.getObjNumber());
        Assert.assertEquals(0, ref2.getGenNumber());

        PdfObject obj3 = pdfDocument.getPage(1).getPdfObject().get(new PdfName("HelloWorldCopy3"));

        PdfIndirectReference ref3 =  obj3.getIndirectReference();
        Assert.assertEquals(7, ref3.getObjNumber());
        Assert.assertEquals(0, ref3.getGenNumber());

        reader.close();
    }

    /**
     * Attempts to copy from the document that is being written.
     *
     * @throws IOException
     */
    @Test()
    public void copyObject7() throws IOException {
        String exceptionMessage = null;

        PdfDocument pdfDoc1;
        PdfDocument pdfDoc2;
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyObject6_1.pdf");
        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyObject6_2.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        PdfWriter writer2 = new PdfWriter(fos2);
        pdfDoc1 = new PdfDocument(writer1);
        pdfDoc2 = new PdfDocument(writer2);
        try {

            PdfPage page1 = pdfDoc1.addNewPage();
            PdfDictionary directDict = new PdfDictionary();
            PdfObject indirectDict = new PdfDictionary().makeIndirect(pdfDoc1);
            page1.getPdfObject().put(new PdfName("HelloWorldDirect"), directDict);
            page1.getPdfObject().put(new PdfName("HelloWorldIndirect"), indirectDict);

            PdfPage page2 = pdfDoc2.addNewPage();
            page2.getPdfObject().put(new PdfName("HelloWorldDirect"), directDict.copyTo(pdfDoc2));
            page2.getPdfObject().put(new PdfName("HelloWorldIndirect"), indirectDict.copyTo(pdfDoc2));
        } catch (PdfException ex) {
            exceptionMessage = ex.getMessage();
        } finally {
            pdfDoc1.close();
            pdfDoc2.close();
        }

        Assert.assertEquals(exceptionMessage, PdfException.CannotCopyIndirectObjectFromTheDocumentThatIsBeingWritten);
    }

    /**
     * Attempts to copy to copy with null document
     *
     * @throws IOException
     */
    @Test()
    public void copyObject8() throws IOException {
        String exceptionMessage = null;

        PdfDocument pdfDoc1;
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyObject6_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        pdfDoc1 = new PdfDocument(writer1);
        try {
            PdfPage page1 = pdfDoc1.addNewPage();
            PdfDictionary directDict = new PdfDictionary();
            PdfObject indirectDict = new PdfDictionary().makeIndirect(pdfDoc1);
            page1.getPdfObject().put(new PdfName("HelloWorldDirect"), directDict);
            page1.getPdfObject().put(new PdfName("HelloWorldIndirect"), indirectDict);

            indirectDict.copyTo(null);

        } catch (PdfException ex) {
            exceptionMessage = ex.getMessage();
        } finally {
            pdfDoc1.close();
        }

        Assert.assertEquals(exceptionMessage, PdfException.DocumentToCopyToCannotBeNull);
    }

    @Test(expected = IOException.class)
    public void closeStream1() throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "closeStream1.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addNewPage();
        pdfDoc.close();
        fos.write(1);
    }

    @Test
    public void closeStream2() throws IOException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "closeStream2.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCloseStream(false);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addNewPage();
        pdfDoc.close();
        fos.write(1);
    }

    @Test
    public void directInIndirectChain() throws IOException {
        String filename = destinationFolder + "directInIndirectChain.pdf";

        PdfWriter writer = new PdfWriter(new FileOutputStream(filename));
        PdfDocument pdfDoc = new PdfDocument(writer);
        PdfArray level1 = new PdfArray();
        level1.add(new PdfNumber(1).makeIndirect(pdfDoc));
        PdfDictionary level2 = new PdfDictionary();
        level1.add(level2);
        PdfArray level3 = new PdfArray();
        level2.put(new PdfName("level3"), level3);
        level2.put(new PdfName("num"), new PdfNumber(2).makeIndirect(pdfDoc));
        level3.add(new PdfNumber(3).makeIndirect(pdfDoc));
        level3.add(new PdfNumber(3).makeIndirect(pdfDoc));
        PdfDictionary level4 = new PdfDictionary();
        level4.put(new PdfName("num"), new PdfNumber(4).makeIndirect(pdfDoc));
        level3.add(level4);
        PdfPage page1 = pdfDoc.addNewPage();

        page1.getPdfObject().put(new PdfName("test"), level1);

        pdfDoc.close();

        PdfReader reader = new PdfReader(filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        Assert.assertEquals("Page count", 1, pdfDocument.getNumberOfPages());
        PdfDictionary page = pdfDocument.getPage(1).getPdfObject();
        Assert.assertEquals(PdfName.Page, page.get(PdfName.Type));
        reader.close();
    }

    @Test
    public void createPdfStreamByInputStream() throws IOException {
        String filename = destinationFolder + "createPdfStreamByInputStream.pdf";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        document.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page = document.addNewPage();
        page.flush();

        String streamContent = "Some text content with strange symbols ∞²";
        PdfStream stream = new PdfStream(document, new ByteArrayInputStream(streamContent.getBytes()));
        stream.flush();
        int streamIndirectNumber = stream.getIndirectReference().getObjNumber();
        document.close();

//        com.itextpdf.text.pdf.PdfReader reader = new PdfReader(filename);
//        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
//        Assert.assertNotNull(reader.getPageN(1));
//        String date = reader.getDocumentInfo().get("CreationDate");
//        Calendar cl = com.itextpdf.text.pdf.PdfDate.decode(date);
//        long diff = new GregorianCalendar().getTimeInMillis() - cl.getTimeInMillis();
//        String message = "Unexpected creation date. Different from now is " + (float)diff/1000 + "s";
//        Assert.assertTrue(message, diff < 5000);
//        reader.close();

        com.itextpdf.kernel.pdf.PdfReader reader6 = new com.itextpdf.kernel.pdf.PdfReader(filename);
        document = new PdfDocument(reader6);
        Assert.assertEquals("Rebuilt", false, reader6.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader6.hasFixedXref());
        PdfStream pdfStream = (PdfStream) document.getXref().get(streamIndirectNumber).getRefersTo();
        Assert.assertArrayEquals("Stream by InputStream", streamContent.getBytes(), pdfStream.getBytes());
        document.close();
    }
}
