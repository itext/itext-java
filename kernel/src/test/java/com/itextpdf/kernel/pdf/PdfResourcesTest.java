package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Set;

@Category(IntegrationTest.class)
public class PdfResourcesTest {


    @Test
    public void resourcesTest1() throws Exception {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));

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
        PdfDocument document = new PdfDocument(new PdfWriter(baos));
        PdfPage page = document.addNewPage();
        PdfExtGState egs1 = new PdfExtGState();
        PdfExtGState egs2 = new PdfExtGState();
        PdfResources resources = page.getResources();
        resources.addExtGState(egs1);
        resources.addExtGState(egs2);
        document.close();

        PdfReader reader = new PdfReader(new ByteArrayInputStream(baos.toByteArray()));
        document = new PdfDocument(reader, new PdfWriter(new com.itextpdf.io.source.ByteArrayOutputStream()));
        page = document.getPage(1);
        resources = page.getResources();
        Set<PdfName> names = resources.getResourceNames();
        Assert.assertEquals(2, names.size());

        String[] expectedNames = { "Gs1", "Gs2"};
        int i = 0;
        for (PdfName name : names) {
            Assert.assertEquals(expectedNames[i++], name.getValue());
        }

        PdfExtGState egs3 = new PdfExtGState();
        PdfName n3 = resources.addExtGState(egs3);
        Assert.assertEquals("Gs3", n3.getValue());
        PdfDictionary egsResources = page.getPdfObject().getAsDictionary(PdfName.Resources).getAsDictionary(PdfName.ExtGState);
        PdfObject e1 = egsResources.get(new PdfName("Gs1"), false);
        PdfName n1 = resources.addExtGState(e1);
        Assert.assertEquals("Gs1", n1.getValue());
        PdfObject e2 = egsResources.get(new PdfName("Gs2"));
        PdfName n2 = resources.addExtGState(e2);
        Assert.assertEquals("Gs2", n2.getValue());
        PdfObject e4 = (PdfObject) e2.clone();
        PdfName n4 = resources.addExtGState(e4);
        Assert.assertEquals("Gs4", n4.getValue());
        document.close();
    }

}
