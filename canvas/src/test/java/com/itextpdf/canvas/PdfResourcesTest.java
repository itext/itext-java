package com.itextpdf.canvas;

import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.extgstate.PdfExtGState;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;
import java.util.Set;

public class PdfResourcesTest {

//    static final public String sourceFolder = "./src/test/resources/com/itextpdf/canvas/PdfResourcesTest/";
//    static final public String destinationFolder = "./target/test/com/itextpdf/canvas/PdfResourcesTest/";

//    @BeforeClass
//    static public void beforeClass() {
//        new File(destinationFolder).mkdirs();
//    }

    @Test
    public void resourcesTest1() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        final PdfDocument document = new PdfDocument(writer);

        PdfPage page = document.addNewPage();
        PdfExtGState egs1 = new PdfExtGState();
        PdfExtGState egs2 = new PdfExtGState();
        PdfResources resources = page.getResources();
        PdfName n1 = resources.addExtGState(egs1);
        Assert.assertEquals("Gs1", n1.getValue());
        PdfName n2 = resources.addExtGState(egs2);
        Assert.assertEquals("Gs2", n2.getValue());
        n1 = resources.addExtGState(egs1);
        Assert.assertEquals("Gs1", n1.getValue());

        document.close();
    }

    @Test
    public void resourcesTest2() throws Exception {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);
        PdfPage page = document.addNewPage();
        PdfExtGState egs1 = new PdfExtGState();
        PdfExtGState egs2 = new PdfExtGState();
        PdfResources resources = page.getResources();
        resources.addExtGState(egs1);
        resources.addExtGState(egs2);
        document.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(baos.toByteArray()));
        document = new PdfDocument(reader, new PdfWriter(new com.itextpdf.basics.io.ByteArrayOutputStream()));
        page = document.getPage(1);
        resources = page.getResources();
        Set<PdfName> names = resources.getResourceNames();
        Assert.assertEquals(2, names.size());
        Iterator<PdfName> iterator = names.iterator();
        PdfName n1 = iterator.next();
        Assert.assertEquals("Gs1", n1.getValue());
        PdfName n2 = iterator.next();
        Assert.assertEquals("Gs2", n2.getValue());
        PdfExtGState egs3 = new PdfExtGState();
        PdfName n3 = resources.addExtGState(egs3);
        Assert.assertEquals("Gs3", n3.getValue());
        PdfDictionary egsResources = page.getPdfObject().getAsDictionary(PdfName.Resources).getAsDictionary(PdfName.ExtGState);
        PdfObject e1 = egsResources.get(new PdfName("Gs1"), false);
        n1 = resources.addExtGState(e1);
        Assert.assertEquals("Gs1", n1.getValue());
        PdfObject e2 = egsResources.get(new PdfName("Gs2"));
        n2 = resources.addExtGState(e2);
        Assert.assertEquals("Gs2", n2.getValue());
        PdfObject e4 = e2.copy(document);
        PdfName n4 = resources.addExtGState(e4);
        Assert.assertEquals("Gs4", n4.getValue());
        document.close();
    }

}
