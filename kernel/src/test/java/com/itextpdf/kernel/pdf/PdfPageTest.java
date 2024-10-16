/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfPageTest extends ExtendedITextTest {
    private PdfDocument dummyDoc;

    @BeforeEach
    public void before() {
        dummyDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        dummyDoc.addNewPage();
    }

    @AfterEach
    public void after() {
        dummyDoc.close();
    }

    @Test
    public void pageConstructorModifiedStateTest() {
        PdfDictionary pageDictionary = new PdfDictionary();
        simulateIndirectState(pageDictionary);
        PdfPage pdfPage = new PdfPage(pageDictionary);

        Assertions.assertFalse(pageDictionary.isModified());
    }

    @Test
    public void removeLastAnnotationTest() {
        PdfDictionary pageDictionary = new PdfDictionary();
        simulateIndirectState(pageDictionary);
        PdfDictionary annotDictionary = new PdfDictionary();
        pageDictionary.put(PdfName.Annots, new PdfArray(Collections.singletonList((PdfObject) annotDictionary)));

        Assertions.assertFalse(pageDictionary.isModified());

        PdfPage pdfPage = new PdfPage(pageDictionary);
        pdfPage.removeAnnotation(PdfAnnotation.makeAnnotation(annotDictionary));

        Assertions.assertTrue(pdfPage.getAnnotations().isEmpty());
        Assertions.assertFalse(pageDictionary.containsKey(PdfName.Annots));
        Assertions.assertTrue(pageDictionary.isModified());
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

        Assertions.assertFalse(pageDictionary.isModified());

        PdfPage pdfPage = new PdfPage(pageDictionary);
        pdfPage.removeAnnotation(PdfAnnotation.makeAnnotation(annotDictionary1));

        Assertions.assertEquals(1, pdfPage.getAnnotations().size());
        Assertions.assertEquals(annotDictionary2, pdfPage.getAnnotations().get(0).getPdfObject());
        Assertions.assertTrue(pageDictionary.isModified());
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

        Assertions.assertFalse(annotsArray.isModified());

        PdfPage pdfPage = new PdfPage(pageDictionary);
        pdfPage.removeAnnotation(PdfAnnotation.makeAnnotation(annotDictionary1));

        Assertions.assertEquals(1, pdfPage.getAnnotations().size());
        Assertions.assertEquals(annotDictionary2, pdfPage.getAnnotations().get(0).getPdfObject());
        Assertions.assertFalse(pageDictionary.isModified());
        Assertions.assertTrue(annotsArray.isModified());
    }

    @Test
    public void setArtBoxTest() {
        PdfDictionary pageDictionary = new PdfDictionary();
        PdfArray preExistingTrimBoxArr = new PdfArray(new int[] {0, 0, 100, 100});
        pageDictionary.put(PdfName.TrimBox, preExistingTrimBoxArr);
        simulateIndirectState(pageDictionary);

        Assertions.assertFalse(pageDictionary.isModified());

        PdfPage pdfPage = new PdfPage(pageDictionary);
        pdfPage.setArtBox(new Rectangle(25, 40));

        PdfArray expectedArtBoxArr = new PdfArray(
                Arrays.asList(new PdfNumber(0), new PdfNumber(0), new PdfNumber(25), (PdfObject) new PdfNumber(40))
        );
        Assertions.assertTrue(
                new CompareTool().compareArrays(pageDictionary.getAsArray(PdfName.ArtBox), expectedArtBoxArr));
        // trimbox not removed
        Assertions.assertTrue(
                new CompareTool().compareArrays(pageDictionary.getAsArray(PdfName.TrimBox), preExistingTrimBoxArr));
        Assertions.assertTrue(pageDictionary.isModified());
    }

    @Test
    public void setTrimBoxTest() {
        PdfDictionary pageDictionary = new PdfDictionary();
        PdfArray preExistingArtBoxArr = new PdfArray(new int[] {0, 0, 100, 100});
        pageDictionary.put(PdfName.ArtBox, preExistingArtBoxArr);
        simulateIndirectState(pageDictionary);

        Assertions.assertFalse(pageDictionary.isModified());

        PdfPage pdfPage = new PdfPage(pageDictionary);
        pdfPage.setTrimBox(new Rectangle(25, 40));

        PdfArray expectedTrimBoxArr = new PdfArray(
                Arrays.asList(new PdfNumber(0), new PdfNumber(0), new PdfNumber(25), (PdfObject) new PdfNumber(40))
        );
        Assertions.assertTrue(
                new CompareTool().compareArrays(pageDictionary.getAsArray(PdfName.TrimBox), expectedTrimBoxArr));
        // artbox not removed
        Assertions.assertTrue(
                new CompareTool().compareArrays(pageDictionary.getAsArray(PdfName.ArtBox), preExistingArtBoxArr));
        Assertions.assertTrue(pageDictionary.isModified());
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
