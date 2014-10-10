package com.itextpdf.core.pdf;

import com.itextpdf.core.exceptions.PdfException;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;

public class PdfObjectTest {

    @Test
    public void indirectsChain1() throws PdfException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        catalog.put(new PdfName("a"), new PdfDictionary(new HashMap<PdfName, PdfObject>() {{
            put(new PdfName("b"), new PdfName("c"));
        }}).makeIndirect(document).getIndirectReference().makeIndirect(document).getIndirectReference().makeIndirect(document));
        PdfObject object = ((PdfIndirectReference)catalog.get(new PdfName("a"))).getRefersTo(true);
        Assert.assertTrue(object instanceof PdfDictionary);
        document.close();
    }

    @Test
    public void indirectsChain2() throws PdfException {
        String exceptionMessage = null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        PdfDictionary dictionary = new PdfDictionary(new HashMap<PdfName, PdfObject>() {{
            put(new PdfName("b"), new PdfName("c"));
        }});
        PdfObject object = dictionary;
        for (int i = 0; i < 200; i++) {
            object = object.makeIndirect(document).getIndirectReference();
        }
        catalog.put(new PdfName("a"), object);
        try {
            ((PdfIndirectReference)catalog.get(new PdfName("a"))).getRefersTo(true);
        } catch (PdfException e) {
            exceptionMessage = e.getMessage();
        }
        Assert.assertEquals(PdfException.InfiniteIndirectReferenceChain, exceptionMessage);
        document.close();
    }

    @Test
    public void indirectsChain3() throws PdfException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        PdfDictionary dictionary = new PdfDictionary(new HashMap<PdfName, PdfObject>() {{
            put(new PdfName("b"), new PdfName("c"));
        }});
        PdfObject object = dictionary;
        for (int i = 0; i < 50; i++) {
            object = object.makeIndirect(document).getIndirectReference();
        }
        catalog.put(new PdfName("a"), object);
        object = catalog.get(new PdfName("a"), true);
        Assert.assertTrue(object instanceof PdfDictionary);
        Assert.assertEquals(new PdfName("c").toString(), ((PdfDictionary)object).get(new PdfName("b")).toString());
        document.close();
    }

    @Test
    public void indirectsChain4() throws PdfException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();
        PdfDictionary dictionary = new PdfDictionary(new HashMap<PdfName, PdfObject>() {{
            put(new PdfName("b"), new PdfName("c"));
        }});
        PdfObject object = dictionary;
        for (int i = 0; i < 50; i++) {
            object = object.makeIndirect(document).getIndirectReference();
        }
        PdfArray array = new PdfArray();
        array.add(object);
        catalog.put(new PdfName("a"), array);
        object = ((PdfArray)catalog.get(new PdfName("a"))).get(0, true);
        Assert.assertTrue(object instanceof PdfDictionary);
        Assert.assertEquals(new PdfName("c").toString(), ((PdfDictionary)object).get(new PdfName("b")).toString());
        document.close();
    }


}
