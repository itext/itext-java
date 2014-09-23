package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.text.pdf.PdfReader;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PdfWriterTest {

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/PdfWriterTest/";
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
        Assert.assertNotNull(reader.getPageN(1));
        reader.close();

    }

    @Test
    public void useObjectForMultipleTimes1() throws IOException, PdfException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "useObjectForMultipleTimes1.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfDictionary helloWorld = (PdfDictionary)new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        PdfPage page = pdfDoc.addNewPage();
        page.put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        pdfDoc.getCatalog().put(new PdfName("HelloWorld"), helloWorld);
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes1.pdf");
    }

    @Test
    public void useObjectForMultipleTimes2() throws IOException, PdfException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "useObjectForMultipleTimes2.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfDictionary helloWorld = (PdfDictionary)new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        helloWorld.flush();
        PdfPage page = pdfDoc.addNewPage();
        page.put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        pdfDoc.getCatalog().put(new PdfName("HelloWorld"), helloWorld);
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes2.pdf");
    }

    @Test
    public void useObjectForMultipleTimes3() throws IOException, PdfException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "useObjectForMultipleTimes3.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfDictionary helloWorld = (PdfDictionary)new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        PdfPage page = pdfDoc.addNewPage();
        page.put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        helloWorld.flush();
        pdfDoc.getCatalog().put(new PdfName("HelloWorld"), helloWorld);
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes3.pdf");
    }

    @Test
    public void useObjectForMultipleTimes4() throws IOException, PdfException {
        FileOutputStream fos = new FileOutputStream(destinationFolder + "useObjectForMultipleTimes4.pdf");
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        PdfDictionary helloWorld = (PdfDictionary)new PdfDictionary().makeIndirect(pdfDoc);
        helloWorld.put(new PdfName("Hello"), new PdfString("World"));
        PdfPage page = pdfDoc.addNewPage();
        page.put(new PdfName("HelloWorld"), helloWorld);
        page.flush();
        pdfDoc.getCatalog().put(new PdfName("HelloWorld"), helloWorld);
        helloWorld.flush();
        pdfDoc.close();

        validateUseObjectForMultipleTimesTest(destinationFolder + "useObjectForMultipleTimes4.pdf");
    }

    private void validateUseObjectForMultipleTimesTest(String filename) throws IOException {
        com.itextpdf.text.pdf.PdfReader reader = new PdfReader(filename);
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

}
