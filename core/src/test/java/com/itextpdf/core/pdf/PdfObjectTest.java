package com.itextpdf.core.pdf;

import com.itextpdf.core.testutils.annotations.type.IntegrationTest;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;

@Category(IntegrationTest.class)
public class PdfObjectTest {

    @Test
    public void indirectsChain1() {
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
    public void indirectsChain2() {
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
        Assert.assertNotNull(((PdfIndirectReference) catalog.get(new PdfName("a"))).getRefersTo(true));
        document.close();
    }

    @Test
    public void indirectsChain3() {
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
        Assert.assertEquals(new PdfName("c").toString(), ((PdfDictionary) object).get(new PdfName("b")).toString());
        document.close();
    }

    @Test
    public void indirectsChain4() {
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
        PdfIndirectReference reference = new PdfIndirectReference(null, 1);
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
                reference.checkState((byte) (PdfIndirectReference.Free | PdfIndirectReference.Reading)));

        reference.clearState(PdfIndirectReference.Modified);

        Assert.assertEquals("Free", false, reference.checkState(PdfIndirectReference.Free));
        Assert.assertEquals("Reading", false, reference.checkState(PdfIndirectReference.Reading));
        Assert.assertEquals("Modified", false, reference.checkState(PdfIndirectReference.Modified));


        Assert.assertEquals("Is InUse", true, !reference.isFree());

        reference.setState(PdfIndirectReference.Free);

        Assert.assertEquals("Not IsInUse", false, !reference.isFree());
    }

    @Test
    public void pdtIndirectReferenceLateInitializing1() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument document = new PdfDocument(writer);
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();

        PdfIndirectReference indRef = document.createNextIndirectReference();
        catalog.put(new PdfName("Smth"), indRef);

        PdfDictionary dictionary = new PdfDictionary();
        dictionary.put(new PdfName("A"), new PdfString("a"));

        dictionary.makeIndirect(document, indRef);

        document.close();


        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        PdfReader reader = new PdfReader(bais);

        document = new PdfDocument(reader);

        PdfObject object = document.getCatalog().getPdfObject().get(new PdfName("Smth"));
        Assert.assertTrue(object instanceof PdfDictionary);
        dictionary = (PdfDictionary) object;
        PdfString a = (PdfString) dictionary.get(new PdfName("A"));
        Assert.assertTrue(a.getValue().equals("a"));

        document.close();
    }

    @Test
    public void pdtIndirectReferenceLateInitializing2() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);

        PdfDocument document = new PdfDocument(writer);
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();

        PdfIndirectReference indRef1 = document.createNextIndirectReference();
        PdfIndirectReference indRef2 = document.createNextIndirectReference();

        catalog.put(new PdfName("Smth1"), indRef1);
        catalog.put(new PdfName("Smth2"), indRef2);

        PdfArray array = new PdfArray();
        array.add(new PdfString("array string"));
        array.makeIndirect(document, indRef2);

        document.close();

        ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
        PdfReader reader = new PdfReader(bais);
        document = new PdfDocument(reader);

        PdfDictionary catalogDict = document.getCatalog().getPdfObject();
        PdfObject object1 = catalogDict.get(new PdfName("Smth1"));
        PdfObject object2 = catalogDict.get(new PdfName("Smth2"));
        Assert.assertTrue(object1 instanceof PdfNull);
        Assert.assertTrue(object2 instanceof PdfArray);
    }

    @Test
    public void pdtIndirectReferenceLateInitializing3() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);

        PdfDocument document = new PdfDocument(writer);
        document.addNewPage();
        PdfDictionary catalog = document.getCatalog().getPdfObject();

        PdfIndirectReference indRef1 = document.createNextIndirectReference();
        PdfIndirectReference indRef2 = document.createNextIndirectReference();

        PdfArray array = new PdfArray();
        catalog.put(new PdfName("array1"), array);
        PdfString string = new PdfString("array string");
        array.add(string);
        array.add(indRef1);
        array.add(indRef2);

        PdfDictionary dict = new PdfDictionary();
        dict.makeIndirect(document, indRef1);


        PdfArray arrayClone = (PdfArray) array.clone();

        PdfObject object0 = arrayClone.get(0, false);
        PdfObject object1 = arrayClone.get(1, false);
        PdfObject object2 = arrayClone.get(2, false);

        Assert.assertTrue(object0 instanceof PdfString);
        Assert.assertTrue(object1 instanceof PdfDictionary);
        Assert.assertTrue(object2 instanceof PdfNull);

        PdfString string1 = (PdfString)object0;
        Assert.assertTrue(string != string1);
        Assert.assertTrue(string.getValue().equals(string1.getValue()));

        PdfDictionary dict1 = (PdfDictionary) object1;
        Assert.assertTrue(dict1.getIndirectReference().getObjNumber() == dict.getIndirectReference().getObjNumber());
        Assert.assertTrue(dict1.getIndirectReference().getGenNumber() == dict.getIndirectReference().getGenNumber());
        Assert.assertTrue(dict1 == dict);

        document.close();
    }
}
