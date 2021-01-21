/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool.ObjectPath;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CompareToolObjectPathTest extends ExtendedITextTest {

    @Test
    public void baseEqualsTest() {
        CompareTool compareTool = new CompareTool();
        PdfIndirectReference firstReference = createIndirectReference(null, 41, 0);
        PdfIndirectReference secondReference = createIndirectReference(null, 42, 0);
        ObjectPath path = compareTool.new ObjectPath(firstReference, secondReference);
        Assert.assertTrue(path.equals(path));
        Assert.assertFalse(path.equals(null));

        Assert.assertFalse(compareTool.new ObjectPath(firstReference, secondReference).equals(
                compareTool.new ObjectPath(null, secondReference)));
        Assert.assertFalse(compareTool.new ObjectPath(null, secondReference).equals(
                compareTool.new ObjectPath(firstReference, secondReference)));
        Assert.assertFalse(compareTool.new ObjectPath(firstReference, secondReference).equals(
                compareTool.new ObjectPath(firstReference, null)));
        Assert.assertFalse(compareTool.new ObjectPath(firstReference, secondReference).equals(
                compareTool.new ObjectPath(null, secondReference)));

        Assert.assertFalse(compareTool.new ObjectPath(firstReference, secondReference).equals(
                compareTool.new ObjectPath(new TestIndirectReference(null, 41, 0), secondReference)));
        Assert.assertFalse(compareTool.new ObjectPath(firstReference, secondReference).equals(
                compareTool.new ObjectPath(firstReference, new TestIndirectReference(null, 42, 0))));
    }

    @Test
    public void equalsWithDocTest() {
        CompareTool compareTool = new CompareTool();
        try (PdfDocument firstDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                PdfDocument secondDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            // add a page to avoid exception throwing on close
            firstDoc.addNewPage();
            secondDoc.addNewPage();

            PdfIndirectReference obj41Gen0 = createIndirectReference(firstDoc, 41, 0);
            PdfIndirectReference obj42Gen0 = createIndirectReference(firstDoc, 42, 0);

            Assert.assertTrue(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    compareTool.new ObjectPath(createIndirectReference(firstDoc, 41, 0), obj42Gen0)));
            Assert.assertTrue(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    compareTool.new ObjectPath(obj41Gen0, createIndirectReference(firstDoc, 42, 0))));

            Assert.assertFalse(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    compareTool.new ObjectPath(createIndirectReference(firstDoc, 42, 0), obj42Gen0)));
            Assert.assertFalse(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    compareTool.new ObjectPath(obj41Gen0, createIndirectReference(firstDoc, 41, 0))));

            Assert.assertFalse(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    compareTool.new ObjectPath(createIndirectReference(firstDoc, 41, 1), obj42Gen0)));
            Assert.assertFalse(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    compareTool.new ObjectPath(obj41Gen0, createIndirectReference(firstDoc, 42, 1))));

            // TODO: DEVSIX-4756 start asserting false
            Assert.assertTrue(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    compareTool.new ObjectPath(createIndirectReference(null, 41, 0), obj42Gen0)));
            // TODO: DEVSIX-4756 start asserting false
            Assert.assertTrue(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    compareTool.new ObjectPath(obj41Gen0, createIndirectReference(null, 42, 0))));

            // TODO: DEVSIX-4756 start asserting false
            Assert.assertTrue(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    compareTool.new ObjectPath(createIndirectReference(secondDoc, 41, 0), obj42Gen0)));
            // TODO: DEVSIX-4756 start asserting false
            Assert.assertTrue(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    compareTool.new ObjectPath(obj41Gen0, createIndirectReference(secondDoc, 42, 0))));

        }
    }

    @Test
    public void hashCodeTest() {
        CompareTool compareTool = new CompareTool();
        try (PdfDocument firstDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                PdfDocument secondDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            // add a page to avoid exception throwing on close
            firstDoc.addNewPage();
            secondDoc.addNewPage();

            PdfIndirectReference obj41Gen0 = createIndirectReference(firstDoc, 41, 0);
            PdfIndirectReference obj42Gen0 = createIndirectReference(firstDoc, 42, 0);

            Assert.assertEquals(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).hashCode(),
                    compareTool.new ObjectPath(
                            createIndirectReference(firstDoc, 41, 0),
                            createIndirectReference(firstDoc, 42, 0)
                    ).hashCode());

            Assert.assertNotEquals(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).hashCode(),
                    compareTool.new ObjectPath(
                            createIndirectReference(firstDoc, 42, 0),
                            obj42Gen0
                    ).hashCode());
            Assert.assertNotEquals(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).hashCode(),
                    compareTool.new ObjectPath(
                            obj41Gen0,
                            createIndirectReference(firstDoc, 41, 0)
                    ).hashCode());

            // TODO: DEVSIX-4756 start asserting not equals
            Assert.assertEquals(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).hashCode(),
                    compareTool.new ObjectPath(
                            createIndirectReference(null, 41, 0),
                            createIndirectReference(firstDoc, 42, 0)
                    ).hashCode());
            // TODO: DEVSIX-4756 start asserting not equals
            Assert.assertEquals(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).hashCode(),
                    compareTool.new ObjectPath(
                            createIndirectReference(firstDoc, 41, 0),
                            createIndirectReference(null, 42, 0)
                    ).hashCode());

            // TODO: DEVSIX-4756 start asserting not equals
            Assert.assertEquals(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).hashCode(),
                    compareTool.new ObjectPath(
                            createIndirectReference(secondDoc, 41, 0),
                            createIndirectReference(firstDoc, 42, 0)
                    ).hashCode());
            // TODO: DEVSIX-4756 start asserting not equals
            Assert.assertEquals(compareTool.new ObjectPath(obj41Gen0, obj42Gen0).hashCode(),
                    compareTool.new ObjectPath(
                            createIndirectReference(firstDoc, 41, 0),
                            createIndirectReference(secondDoc, 42, 0)
                    ).hashCode());
        }
    }
    
    private PdfIndirectReference createIndirectReference(PdfDocument doc, int objNr, int genNr) {
        return new PdfIndirectReferenceWithPublicConstructor(doc, objNr, genNr);
    }
    
    private static class PdfIndirectReferenceWithPublicConstructor extends PdfIndirectReference {
        public PdfIndirectReferenceWithPublicConstructor(PdfDocument doc, int objNr, int genNr) {
            super(doc, objNr, genNr);
        }
    }

    private static class TestIndirectReference extends PdfIndirectReferenceWithPublicConstructor {
        public TestIndirectReference(PdfDocument doc, int objNr, int genNr) {
            super(doc, objNr, genNr);
        }
    }
}
