package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfMarkupAnnotationTest extends ExtendedITextTest {
    @Test
    public void externalDataTest() {
        PdfMarkupAnnotation annotation = new PdfCircleAnnotation(new Rectangle(0, 0, 10, 10));
        annotation.setExternalData(new PdfDictionary());
        Assert.assertEquals(PdfObject.DICTIONARY, annotation.getExternalData().getType());
    }
}
