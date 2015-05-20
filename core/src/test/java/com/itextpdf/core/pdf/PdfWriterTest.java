package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import com.itextpdf.text.DocWriter;
import com.itextpdf.text.pdf.PRIndirectReference;
import com.itextpdf.text.pdf.PRStream;
import com.itextpdf.text.pdf.PdfReader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TreeMap;

public class PdfWriterTest {

    static final public String destinationFolder = "./target/test/com/itextpdf/core/pdf/PdfWriterTest/";

    @BeforeClass
    static public void beforeClass() {
        new File(destinationFolder).mkdirs();
    }

    @Test
    public void createEmptyDocument() throws IOException, PdfException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "emptyDocument.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.close();

        com.itextpdf.text.pdf.PdfReader reader = new PdfReader(destinationFolder + "emptyDocument.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        Assert.assertNotNull(reader.getPageN(1));
        String date = reader.getInfo().get("CreationDate");
        Calendar cl = com.itextpdf.text.pdf.PdfDate.decode(date);
        long diff = new GregorianCalendar().getTimeInMillis() - cl.getTimeInMillis();
        String message = "Unexpected creation date. Different from now is " + (float)diff/1000 + "s";
        Assert.assertTrue(message, diff < 5000);
        reader.close();

    }

    @Test
    public void useObjectForMultipleTimes1() throws IOException, PdfException {
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
    public void useObjectForMultipleTimes2() throws IOException, PdfException {
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
    public void useObjectForMultipleTimes3() throws IOException, PdfException {
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
    public void useObjectForMultipleTimes4() throws IOException, PdfException {
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
        com.itextpdf.text.pdf.PdfReader reader = new PdfReader(filename);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertNotNull(page);
        com.itextpdf.text.pdf.PdfDictionary helloWorld = page.getAsDict(new com.itextpdf.text.pdf.PdfName("HelloWorld"));
        Assert.assertNotNull(helloWorld);
        com.itextpdf.text.pdf.PdfString world = helloWorld.getAsString(new com.itextpdf.text.pdf.PdfName("Hello"));
        Assert.assertEquals("World", world.toString());
        helloWorld = reader.getCatalog().getAsDict(new com.itextpdf.text.pdf.PdfName("HelloWorld"));
        Assert.assertNotNull(helloWorld);
        world = helloWorld.getAsString(new com.itextpdf.text.pdf.PdfName("Hello"));
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
    public void copyObject1() throws IOException, PdfException {
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
        catalog2.put(new PdfName("aDirect"), aDirect.copy());

        pdfDoc1.close();
        pdfDoc2.close();

        PdfReader reader = new PdfReader(destinationFolder + "copyObject1_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary catalog = reader.getCatalog();
        com.itextpdf.text.pdf.PdfArray a = (com.itextpdf.text.pdf.PdfArray) catalog.get(new com.itextpdf.text.pdf.PdfName("aDirect"));
        Assert.assertNotNull(a);
        Assert.assertEquals(1, ((com.itextpdf.text.pdf.PdfNumber) ((com.itextpdf.text.pdf.PdfArray) a.getPdfObject(0)).getPdfObject(0)).intValue());
        Assert.assertEquals(2, ((com.itextpdf.text.pdf.PdfNumber) ((com.itextpdf.text.pdf.PdfArray) a.getPdfObject(0)).getPdfObject(1)).intValue());
        Assert.assertEquals(true, ((com.itextpdf.text.pdf.PdfBoolean) a.getPdfObject(1)).booleanValue());
        Assert.assertEquals(1, ((com.itextpdf.text.pdf.PdfNumber) ((com.itextpdf.text.pdf.PdfDictionary) a.getPdfObject(2)).get(new com.itextpdf.text.pdf.PdfName("one"))).intValue());
        Assert.assertEquals(2, ((com.itextpdf.text.pdf.PdfNumber) ((com.itextpdf.text.pdf.PdfDictionary) a.getPdfObject(2)).get(new com.itextpdf.text.pdf.PdfName("two"))).intValue());
        Assert.assertEquals(new com.itextpdf.text.pdf.PdfName("name"), a.getPdfObject(3));
        Assert.assertTrue(a.getPdfObject(4).isNull());
        Assert.assertEquals(100, ((com.itextpdf.text.pdf.PdfNumber) a.getPdfObject(5)).intValue());
        Assert.assertEquals("string", ((com.itextpdf.text.pdf.PdfString) a.getPdfObject(6)).toUnicodeString());
        reader.close();

    }

    /**
     * Copying objects, some of those are indirect. Objects of all types are added into document catalog.
     *
     * @throws IOException
     * @throws PdfException
     */
    @Test
    public void copyObject2() throws IOException, PdfException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyObject2_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        final PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.flush();
        PdfDictionary catalog1 = pdfDoc1.getCatalog().getPdfObject();
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
        catalog1.put(new PdfName("aDirect"), aDirect);

        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyObject2_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.flush();
        PdfDictionary catalog2 = pdfDoc2.getCatalog().getPdfObject();
        catalog2.put(new PdfName("aDirect"), aDirect.copy(pdfDoc2));

        pdfDoc1.close();
        pdfDoc2.close();

        PdfReader reader = new PdfReader(destinationFolder + "copyObject2_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary catalog = reader.getCatalog();
        Assert.assertTrue(catalog.get(new com.itextpdf.text.pdf.PdfName("aDirect")) instanceof PRIndirectReference);
        com.itextpdf.text.pdf.PdfArray a = catalog.getAsArray(new com.itextpdf.text.pdf.PdfName("aDirect"));
        Assert.assertNotNull(a);
        Assert.assertEquals(1, ((com.itextpdf.text.pdf.PdfNumber) ((com.itextpdf.text.pdf.PdfArray) a.getPdfObject(0)).getPdfObject(0)).intValue());
        Assert.assertTrue(((com.itextpdf.text.pdf.PdfArray) a.getPdfObject(0)).getPdfObject(1) instanceof PRIndirectReference);
        Assert.assertEquals(2, ((com.itextpdf.text.pdf.PdfArray) a.getPdfObject(0)).getAsNumber(1).intValue());
        Assert.assertEquals(true, ((com.itextpdf.text.pdf.PdfBoolean) a.getPdfObject(1)).booleanValue());
        Assert.assertEquals(1, ((com.itextpdf.text.pdf.PdfNumber) ((com.itextpdf.text.pdf.PdfDictionary) a.getPdfObject(2)).get(new com.itextpdf.text.pdf.PdfName("one"))).intValue());
        Assert.assertTrue(((com.itextpdf.text.pdf.PdfDictionary) a.getPdfObject(2)).get(new com.itextpdf.text.pdf.PdfName("two")) instanceof PRIndirectReference);
        Assert.assertEquals(2, ((com.itextpdf.text.pdf.PdfDictionary) a.getPdfObject(2)).getAsNumber(new com.itextpdf.text.pdf.PdfName("two")).intValue());
        Assert.assertEquals(new com.itextpdf.text.pdf.PdfName("name"), a.getPdfObject(3));
        Assert.assertTrue(a.getPdfObject(4) instanceof PRIndirectReference);
        Assert.assertTrue(a.getDirectObject(4).isNull());
        Assert.assertEquals(100, ((com.itextpdf.text.pdf.PdfNumber) a.getPdfObject(5)).intValue());
        Assert.assertEquals("string", ((com.itextpdf.text.pdf.PdfString) a.getPdfObject(6)).toUnicodeString());
        reader.close();
    }

    /**
     * Copy objects recursively.
     *
     * @throws IOException
     * @throws PdfException
     */
    @Test
    public void copyObject3() throws IOException, PdfException {
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
            dic2.put(new PdfName("arr1"), arr1);
            catalog1.put(new PdfName("arr1"), arr1);

            FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyObject3_2.pdf");
            PdfWriter writer2 = new PdfWriter(fos2);
            PdfDocument pdfDoc2 = new PdfDocument(writer2);
            PdfPage page2 = pdfDoc2.addNewPage();
            page2.flush();
            PdfDictionary catalog2 = pdfDoc2.getCatalog().getPdfObject();
            catalog2.put(new PdfName("arr1"), arr1.copy(pdfDoc2));

            pdfDoc1.close();
            pdfDoc2.close();
        }

        {
            PdfReader reader = new PdfReader(destinationFolder + "copyObject3_2.pdf");
            Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
            com.itextpdf.text.pdf.PdfDictionary catalog = reader.getCatalog();
            Assert.assertTrue(catalog.get(new com.itextpdf.text.pdf.PdfName("arr1")) instanceof PRIndirectReference);
            com.itextpdf.text.pdf.PdfArray arr1 = catalog.getAsArray(new com.itextpdf.text.pdf.PdfName("arr1"));
            Assert.assertTrue(arr1.getPdfObject(0) instanceof PRIndirectReference);
            com.itextpdf.text.pdf.PdfArray arr2 = arr1.getAsArray(0);
            Assert.assertTrue(arr2.getPdfObject(0) instanceof PRIndirectReference);
            com.itextpdf.text.pdf.PdfDictionary dic1 = arr2.getAsDict(0);
            Assert.assertTrue(dic1.get(new com.itextpdf.text.pdf.PdfName("dic2")) instanceof PRIndirectReference);
            com.itextpdf.text.pdf.PdfDictionary dic2 = dic1.getAsDict(new com.itextpdf.text.pdf.PdfName("dic2"));
            Assert.assertTrue(dic2.get(new com.itextpdf.text.pdf.PdfName("arr1")) instanceof PRIndirectReference);
            Assert.assertEquals(arr1, dic2.getAsArray(new com.itextpdf.text.pdf.PdfName("arr1")));
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
    public void copyObject4() throws IOException, PdfException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyObject4_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        final PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.flush();
        PdfDictionary catalog1 = pdfDoc1.getCatalog().getPdfObject();
        PdfStream stream1 = new PdfStream(pdfDoc1);
        stream1.getOutputStream().write(new PdfArray(new ArrayList<PdfObject>() {{
            add(new PdfNumber(1));
            add(new PdfNumber(2));
            add(new PdfNumber(3));
        }}));
        catalog1.put(new PdfName("stream"), stream1);

        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyObject4_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        PdfPage page2 = pdfDoc2.addNewPage();
        page2.flush();
        PdfDictionary catalog2 = pdfDoc2.getCatalog().getPdfObject();
        catalog2.put(new PdfName("stream"), stream1.copy(pdfDoc2));

        pdfDoc1.close();
        pdfDoc2.close();

        PdfReader reader = new PdfReader(destinationFolder + "copyObject4_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        com.itextpdf.text.pdf.PdfDictionary catalog = reader.getCatalog();
        PRStream stream = (PRStream)catalog.getAsStream(new com.itextpdf.text.pdf.PdfName("stream"));
        byte[] bytes = PdfReader.getStreamBytes(stream);
        Assert.assertArrayEquals(DocWriter.getISOBytes("[1 2 3]"), bytes);
        reader.close();
    }

    /**
     * Copies page.
     *
     * @throws IOException
     * @throws PdfException
     */
    @Test
    public void copyObject5() throws IOException, PdfException {
        FileOutputStream fos1 = new FileOutputStream(destinationFolder + "copyObject5_1.pdf");
        PdfWriter writer1 = new PdfWriter(fos1);
        final PdfDocument pdfDoc1 = new PdfDocument(writer1);
        PdfPage page1 = pdfDoc1.addNewPage();
        page1.getContentStream(0).getOutputStream().write(PdfOutputStream.getIsoBytes("%Page_1"));

        FileOutputStream fos2 = new FileOutputStream(destinationFolder + "copyObject5_2.pdf");
        PdfWriter writer2 = new PdfWriter(fos2);
        PdfDocument pdfDoc2 = new PdfDocument(writer2);
        PdfPage page2 = page1.copy(pdfDoc2);
        pdfDoc2.addPage(page2);
        page2.flush();
        page2 = pdfDoc2.addNewPage();
        page2.getContentStream(0).getOutputStream().write(PdfOutputStream.getIsoBytes("%Page_2"));

        page1.flush();
        page2.flush();
        pdfDoc1.close();
        pdfDoc2.close();

        PdfReader reader = new PdfReader(destinationFolder + "copyObject5_2.pdf");
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        Assert.assertEquals(8, reader.getTrailer().getAsNumber(com.itextpdf.text.pdf.PdfName.SIZE).intValue());
        byte[] bytes = reader.getPageContent(1);
        Assert.assertArrayEquals(DocWriter.getISOBytes("%Page_1"), bytes);
        bytes = reader.getPageContent(2);
        Assert.assertArrayEquals(DocWriter.getISOBytes("%Page_2"), bytes);
        reader.close();
    }

    @Test(expected = IOException.class)
    public void closeStream1() throws IOException, PdfException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "closeStream1.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addNewPage();
        pdfDoc.close();
        fos.write(1);
    }

    @Test
    public void closeStream2() throws IOException, PdfException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "closeStream2.pdf");
        PdfWriter writer = new PdfWriter(fos);
        writer.setCloseStream(false);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.addNewPage();
        pdfDoc.close();
        fos.write(1);
    }

    @Test
    public void directInIndirectChain() throws IOException, PdfException {
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

        com.itextpdf.text.pdf.PdfReader reader = new com.itextpdf.text.pdf.PdfReader(filename);
        Assert.assertEquals("Rebuilt", false, reader.isRebuilt());
        Assert.assertEquals("Page count", 1, reader.getNumberOfPages());
        com.itextpdf.text.pdf.PdfDictionary page = reader.getPageN(1);
        Assert.assertEquals(com.itextpdf.text.pdf.PdfName.PAGE, page.get(com.itextpdf.text.pdf.PdfName.TYPE));
        reader.close();
    }

    @Test
    public void createPdfStreamByInputStream() throws IOException, PdfException {
        String filename = destinationFolder + "createPdfStreamByInputStream.pdf";

        FileOutputStream fos = new FileOutputStream(filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument document = new PdfDocument(writer);
        document.getInfo().setAuthor("Alexander Chingarev").
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
//        String date = reader.getInfo().get("CreationDate");
//        Calendar cl = com.itextpdf.text.pdf.PdfDate.decode(date);
//        long diff = new GregorianCalendar().getTimeInMillis() - cl.getTimeInMillis();
//        String message = "Unexpected creation date. Different from now is " + (float)diff/1000 + "s";
//        Assert.assertTrue(message, diff < 5000);
//        reader.close();

        com.itextpdf.core.pdf.PdfReader reader6 = new com.itextpdf.core.pdf.PdfReader(filename);
        document = new PdfDocument(reader6);
        Assert.assertEquals("Rebuilt", false, reader6.hasRebuiltXref());
        Assert.assertEquals("Fixed", false, reader6.hasFixedXref());
        PdfStream pdfStream = (PdfStream) document.getXref().get(streamIndirectNumber).getRefersTo();
        Assert.assertArrayEquals("Stream by InputStream", streamContent.getBytes(), pdfStream.getBytes());
        document.close();
    }

}
