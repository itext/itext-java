package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.TimeZone;

@Category(IntegrationTest.class)
public class XMPMetadataTest extends ExtendedITextTest{

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/kernel/pdf/XmpWriterTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/kernel/pdf/XmpWriterTest/";

    static  public TimeZone CURRENT_USER_TIME_ZONE;

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
        CURRENT_USER_TIME_ZONE = TimeZone.getDefault();
        TimeZone.setDefault(TimeZone.getTimeZone("Europe/Brussels"));
    }

    @Test
    public void createEmptyDocumentWithXmp() throws Exception {

        String filename = "emptyDocumentWithXmp.pdf";
        FileOutputStream fos = new FileOutputStream(destinationFolder +filename);
        PdfWriter writer = new PdfWriter(fos,  new WriterProperties().addXmpMetadata());
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        pdfDoc.getDocumentInfo().getPdfObject().remove(PdfName.CreationDate);
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.close();
        PdfReader reader = new PdfReader(destinationFolder +filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        Assert.assertEquals(readFile(sourceFolder + "emptyDocumentWithXmp.xml").length, pdfDocument.getXmpMetadata().length);
        Assert.assertNotNull(reader.pdfDocument.getPage(1));
        reader.close();
    }


    @Test
    public void createEmptyDocumentWithAbcXmp() throws IOException, XMPException {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getDocumentInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        pdfDoc.getDocumentInfo().getPdfObject().remove(PdfName.CreationDate);
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.setXmpMetadata("abc".getBytes());
        pdfDoc.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(fos.toByteArray()));
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        Assert.assertArrayEquals("abc".getBytes(), pdfDocument.getXmpMetadata());
        Assert.assertNotNull(pdfDocument.getPage(1));
        reader.close();

    }

    @AfterClass
    public static void afterClass(){
        TimeZone.setDefault(CURRENT_USER_TIME_ZONE);
    }


}
