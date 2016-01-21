package com.itextpdf.core.pdf;

import com.itextpdf.core.testutils.annotations.type.IntegrationTest;
import com.itextpdf.core.xmp.XMPException;
import com.itextpdf.test.ExtendedITextTest;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class XMPMetadataTest extends ExtendedITextTest{

    static final public String sourceFolder = "./src/test/resources/com/itextpdf/core/pdf/XmpWriterTest/";
    static final public String destinationFolder = "./target/test/com/itextpdf/core/pdf/XmpWriterTest/";

    @BeforeClass
    static public void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void createEmptyDocumentWithXmp() throws IOException, XMPException {
        String filename = "emptyDocumentWithXmp.pdf";
        FileOutputStream fos = new FileOutputStream(destinationFolder +filename);
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        pdfDoc.getInfo().getPdfObject().remove(PdfName.CreationDate);
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.setXmpMetadata();
        pdfDoc.close();

        PdfReader reader = new PdfReader(destinationFolder +filename);
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        Assert.assertEquals(readFile(sourceFolder + "emptyDocumentWithXmp.xml").length, pdfDocument.getXmpMetadata().getLength());
        Assert.assertNotNull(reader.pdfDocument.getPage(1));
        reader.close();

    }

    @Test
    public void createEmptyDocumentWithAbcXmp() throws IOException, XMPException {
        ByteArrayOutputStream fos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(fos);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.getInfo().setAuthor("Alexander Chingarev").
                setCreator("iText 6").
                setTitle("Empty iText 6 Document");
        pdfDoc.getInfo().getPdfObject().remove(PdfName.CreationDate);
        PdfPage page = pdfDoc.addNewPage();
        page.flush();
        pdfDoc.setXmpMetadata("abc".getBytes());
        pdfDoc.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(fos.toByteArray()));
        PdfDocument pdfDocument = new PdfDocument(reader);
        Assert.assertEquals("Rebuilt", false, reader.hasRebuiltXref());
        Assert.assertArrayEquals("abc".getBytes(), pdfDocument.getXmpMetadata().getBytes());
        Assert.assertNotNull(pdfDocument.getPage(1));
        reader.close();

    }


}
