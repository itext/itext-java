package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.*;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfObjRefUnitTest extends ExtendedITextTest {

    @Test
    public void refObjAsStreamTest() {
       PdfDictionary ref = new PdfStream();
       ref.put(PdfName.Name, new PdfString("reference"));

       PdfDictionary obj = new PdfDictionary();
       obj.put(PdfName.Obj, ref);

       PdfObjRef objRef = new PdfObjRef(obj, new PdfStructElem(new PdfDictionary()));

       Assert.assertTrue(objRef.getReferencedObject() instanceof PdfStream);
       Assert.assertTrue(objRef.getReferencedObject().containsKey(PdfName.Name));
    }

    @Test
    public void refObjAsInvalidTypeTest() {
        PdfDictionary obj = new PdfDictionary();
        obj.put(PdfName.Obj, new PdfString("incorrect type"));

        PdfObjRef objRef = new PdfObjRef(obj, new PdfStructElem(new PdfDictionary()));

        Assert.assertNull(objRef.getReferencedObject());
    }
}
