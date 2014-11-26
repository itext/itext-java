package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;
import org.junit.Assert;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
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
        PdfObject object = ((PdfIndirectReference)catalog.get(new PdfName("a"), false)).getRefersTo(true);
        Assert.assertTrue(object instanceof PdfDictionary);
        document.close();
    }

    @Test
    public void indirectsChain2() throws PdfException {
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
        ((PdfIndirectReference)catalog.get(new PdfName("a"))).getRefersTo(true);
        Assert.assertNotNull(((PdfIndirectReference)catalog.get(new PdfName("a"))).getRefersTo(true));
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
        for (int i = 0; i < 31; i++) {
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
        for (int i = 0; i < 31; i++) {
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

    @Test
    public void pdfIndirectReferenceFlags(){
        PdfIndirectReference reference = new PdfIndirectReference(null, 1, null);
        reference.setState(PdfIndirectReference.Free);
        reference.setState(PdfIndirectReference.Reading);
        reference.setState(PdfIndirectReference.Modified);

        Assert.assertEquals("Free", true, reference.checkState(PdfIndirectReference.Free));
        Assert.assertEquals("Reading", true, reference.checkState(PdfIndirectReference.Reading));
        Assert.assertEquals("Modified", true, reference.checkState(PdfIndirectReference.Modified));
        Assert.assertEquals("Free|Reading|Modified", true,
                reference.checkState((byte)(PdfIndirectReference.Free|PdfIndirectReference.Modified |PdfIndirectReference.Reading)));

        reference.clearState(PdfIndirectReference.Free);

        Assert.assertEquals("Free", false, reference.checkState(PdfIndirectReference.Free));
        Assert.assertEquals("Reading", true, reference.checkState(PdfIndirectReference.Reading));
        Assert.assertEquals("Modified", true, reference.checkState(PdfIndirectReference.Modified));
        Assert.assertEquals("Reading|Modified", true,
                reference.checkState((byte)(PdfIndirectReference.Reading|PdfIndirectReference.Modified)));
        Assert.assertEquals("Free|Reading|Modified", false,
                reference.checkState((byte)(PdfIndirectReference.Free|PdfIndirectReference.Reading|PdfIndirectReference.Modified)));

        reference.clearState(PdfIndirectReference.Reading);

        Assert.assertEquals("Free", false, reference.checkState(PdfIndirectReference.Free));
        Assert.assertEquals("Reading", false, reference.checkState(PdfIndirectReference.Reading));
        Assert.assertEquals("Modified", true, reference.checkState(PdfIndirectReference.Modified));
        Assert.assertEquals("Free|Reading", false,
                reference.checkState((byte)(PdfIndirectReference.Free|PdfIndirectReference.Reading)));

        reference.clearState(PdfIndirectReference.Modified);

        Assert.assertEquals("Free", false, reference.checkState(PdfIndirectReference.Free));
        Assert.assertEquals("Reading", false, reference.checkState(PdfIndirectReference.Reading));
        Assert.assertEquals("Modified", false, reference.checkState(PdfIndirectReference.Modified));


        Assert.assertEquals("Is InUse", true, reference.isInUse());

        reference.setState(PdfIndirectReference.Free);

        Assert.assertEquals("Not IsInUse", false, reference.isInUse());
    }
}
