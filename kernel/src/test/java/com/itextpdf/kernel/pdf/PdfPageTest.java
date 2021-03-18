package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PdfPageTest extends ExtendedITextTest {
    private PdfDocument dummyDoc;

    @Before
    public void before() {
        dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        dummyDoc.addNewPage();
    }

    @After
    public void after() {
        dummyDoc.close();
    }

    @Test
    public void pageConstructorModifiedStateTest() {
        PdfDictionary pageDictionary = new PdfDictionary();
        simulateIndirectState(pageDictionary);
        PdfPage pdfPage = new PdfPage(pageDictionary);

        Assert.assertFalse(pageDictionary.isModified());
    }

    @Test
    public void removeLastAnnotationTest() {
        PdfDictionary pageDictionary = new PdfDictionary();
        simulateIndirectState(pageDictionary);
        PdfDictionary annotDictionary = new PdfDictionary();
        pageDictionary.put(PdfName.Annots, new PdfArray(Collections.singletonList((PdfObject) annotDictionary)));

        Assert.assertFalse(pageDictionary.isModified());

        PdfPage pdfPage = new PdfPage(pageDictionary);
        pdfPage.removeAnnotation(PdfAnnotation.makeAnnotation(annotDictionary));

        Assert.assertTrue(pdfPage.getAnnotations().isEmpty());
        Assert.assertFalse(pageDictionary.containsKey(PdfName.Annots));
        Assert.assertTrue(pageDictionary.isModified());
    }

    @Test
    public void removeAnnotationTest() {
        PdfDictionary pageDictionary = new PdfDictionary();
        simulateIndirectState(pageDictionary);
        PdfDictionary annotDictionary1 = new PdfDictionary();
        PdfDictionary annotDictionary2 = new PdfDictionary();
        pageDictionary.put(PdfName.Annots, new PdfArray(
                Arrays.asList(annotDictionary1, (PdfObject) annotDictionary2))
        );

        Assert.assertFalse(pageDictionary.isModified());

        PdfPage pdfPage = new PdfPage(pageDictionary);
        pdfPage.removeAnnotation(PdfAnnotation.makeAnnotation(annotDictionary1));

        Assert.assertEquals(1, pdfPage.getAnnotations().size());
        Assert.assertEquals(annotDictionary2, pdfPage.getAnnotations().get(0).getPdfObject());
        Assert.assertTrue(pageDictionary.isModified());
    }

    @Test
    public void removeAnnotationWithIndirectAnnotsArrayTest() {
        PdfDictionary pageDictionary = new PdfDictionary();
        simulateIndirectState(pageDictionary);
        PdfDictionary annotDictionary1 = new PdfDictionary();
        PdfDictionary annotDictionary2 = new PdfDictionary();
        PdfArray annotsArray = new PdfArray(
                Arrays.asList(annotDictionary1, (PdfObject) annotDictionary2)
        );
        simulateIndirectState(annotsArray);

        pageDictionary.put(PdfName.Annots, annotsArray);

        Assert.assertFalse(annotsArray.isModified());

        PdfPage pdfPage = new PdfPage(pageDictionary);
        pdfPage.removeAnnotation(PdfAnnotation.makeAnnotation(annotDictionary1));

        Assert.assertEquals(1, pdfPage.getAnnotations().size());
        Assert.assertEquals(annotDictionary2, pdfPage.getAnnotations().get(0).getPdfObject());
        Assert.assertFalse(pageDictionary.isModified());
        Assert.assertTrue(annotsArray.isModified());
    }

    @Test
    public void setArtBoxTest() {
        PdfDictionary pageDictionary = new PdfDictionary();
        PdfArray preExistingTrimBoxArr = new PdfArray(new int[] {0, 0, 100, 100});
        pageDictionary.put(PdfName.TrimBox, preExistingTrimBoxArr);
        simulateIndirectState(pageDictionary);

        Assert.assertFalse(pageDictionary.isModified());

        PdfPage pdfPage = new PdfPage(pageDictionary);
        pdfPage.setArtBox(new Rectangle(25, 40));

        PdfArray expectedArtBoxArr = new PdfArray(
                Arrays.asList(new PdfNumber(0), new PdfNumber(0), new PdfNumber(25), (PdfObject) new PdfNumber(40))
        );
        Assert.assertTrue(
                new CompareTool().compareArrays(pageDictionary.getAsArray(PdfName.ArtBox), expectedArtBoxArr));
        // trimbox not removed
        Assert.assertTrue(
                new CompareTool().compareArrays(pageDictionary.getAsArray(PdfName.TrimBox), preExistingTrimBoxArr));
        Assert.assertTrue(pageDictionary.isModified());
    }

    @Test
    public void setTrimBoxTest() {
        PdfDictionary pageDictionary = new PdfDictionary();
        PdfArray preExistingArtBoxArr = new PdfArray(new int[] {0, 0, 100, 100});
        pageDictionary.put(PdfName.ArtBox, preExistingArtBoxArr);
        simulateIndirectState(pageDictionary);

        Assert.assertFalse(pageDictionary.isModified());

        PdfPage pdfPage = new PdfPage(pageDictionary);
        pdfPage.setTrimBox(new Rectangle(25, 40));

        PdfArray expectedTrimBoxArr = new PdfArray(
                Arrays.asList(new PdfNumber(0), new PdfNumber(0), new PdfNumber(25), (PdfObject) new PdfNumber(40))
        );
        Assert.assertTrue(
                new CompareTool().compareArrays(pageDictionary.getAsArray(PdfName.TrimBox), expectedTrimBoxArr));
        // artbox not removed
        Assert.assertTrue(
                new CompareTool().compareArrays(pageDictionary.getAsArray(PdfName.ArtBox), preExistingArtBoxArr));
        Assert.assertTrue(pageDictionary.isModified());
    }

    /**
     * Simulates indirect state of object making sure it is not marked as modified.
     *
     * @param obj object to which indirect state simulation is applied
     */
    private void simulateIndirectState(PdfObject obj) {
        obj.setIndirectReference(new PdfIndirectReference(dummyDoc, 0));
    }
}
