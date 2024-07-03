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
package com.itextpdf.kernel.utils;

import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.objectpathitems.ObjectPath;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CompareToolObjectPathTest extends ExtendedITextTest {

    @Test
    public void baseEqualsTest() {
        PdfIndirectReference firstReference = createIndirectReference(null, 41, 0);
        PdfIndirectReference secondReference = createIndirectReference(null, 42, 0);
        ObjectPath path = new ObjectPath(firstReference, secondReference);
        Assertions.assertTrue(path.equals(path));
        Assertions.assertFalse(path.equals(null));

        Assertions.assertFalse(new ObjectPath(firstReference, secondReference).equals(
                new ObjectPath(null, secondReference)));
        Assertions.assertFalse(new ObjectPath(null, secondReference).equals(
                new ObjectPath(firstReference, secondReference)));
        Assertions.assertFalse(new ObjectPath(firstReference, secondReference).equals(
                new ObjectPath(firstReference, null)));
        Assertions.assertFalse(new ObjectPath(firstReference, secondReference).equals(
                new ObjectPath(null, secondReference)));

        Assertions.assertFalse(new ObjectPath(firstReference, secondReference).equals(
                new ObjectPath(new TestIndirectReference(null, 41, 0), secondReference)));
        Assertions.assertFalse(new ObjectPath(firstReference, secondReference).equals(
                new ObjectPath(firstReference, new TestIndirectReference(null, 42, 0))));
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

            Assertions.assertTrue(new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    new ObjectPath(createIndirectReference(firstDoc, 41, 0), obj42Gen0)));
            Assertions.assertTrue(new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    new ObjectPath(obj41Gen0, createIndirectReference(firstDoc, 42, 0))));

            Assertions.assertFalse(new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    new ObjectPath(createIndirectReference(firstDoc, 42, 0), obj42Gen0)));
            Assertions.assertFalse(new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    new ObjectPath(obj41Gen0, createIndirectReference(firstDoc, 41, 0))));

            Assertions.assertFalse(new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    new ObjectPath(createIndirectReference(firstDoc, 41, 1), obj42Gen0)));
            Assertions.assertFalse(new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    new ObjectPath(obj41Gen0, createIndirectReference(firstDoc, 42, 1))));

            // TODO: DEVSIX-4756 start asserting false
            Assertions.assertTrue(new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    new ObjectPath(createIndirectReference(null, 41, 0), obj42Gen0)));
            // TODO: DEVSIX-4756 start asserting false
            Assertions.assertTrue(new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    new ObjectPath(obj41Gen0, createIndirectReference(null, 42, 0))));

            // TODO: DEVSIX-4756 start asserting false
            Assertions.assertTrue(new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    new ObjectPath(createIndirectReference(secondDoc, 41, 0), obj42Gen0)));
            // TODO: DEVSIX-4756 start asserting false
            Assertions.assertTrue(new ObjectPath(obj41Gen0, obj42Gen0).equals(
                    new ObjectPath(obj41Gen0, createIndirectReference(secondDoc, 42, 0))));

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

            Assertions.assertEquals(new ObjectPath(obj41Gen0, obj42Gen0).hashCode(),
                    new ObjectPath(
                            createIndirectReference(firstDoc, 41, 0),
                            createIndirectReference(firstDoc, 42, 0)
                    ).hashCode());

            Assertions.assertNotEquals(new ObjectPath(obj41Gen0, obj42Gen0).hashCode(),
                    new ObjectPath(
                            createIndirectReference(firstDoc, 42, 0),
                            obj42Gen0
                    ).hashCode());
            Assertions.assertNotEquals(new ObjectPath(obj41Gen0, obj42Gen0).hashCode(),
                    new ObjectPath(
                            obj41Gen0,
                            createIndirectReference(firstDoc, 41, 0)
                    ).hashCode());

            // TODO: DEVSIX-4756 start asserting not equals
            Assertions.assertEquals(new ObjectPath(obj41Gen0, obj42Gen0).hashCode(),
                    new ObjectPath(
                            createIndirectReference(null, 41, 0),
                            createIndirectReference(firstDoc, 42, 0)
                    ).hashCode());
            // TODO: DEVSIX-4756 start asserting not equals
            Assertions.assertEquals(new ObjectPath(obj41Gen0, obj42Gen0).hashCode(),
                    new ObjectPath(
                            createIndirectReference(firstDoc, 41, 0),
                            createIndirectReference(null, 42, 0)
                    ).hashCode());

            // TODO: DEVSIX-4756 start asserting not equals
            Assertions.assertEquals(new ObjectPath(obj41Gen0, obj42Gen0).hashCode(),
                    new ObjectPath(
                            createIndirectReference(secondDoc, 41, 0),
                            createIndirectReference(firstDoc, 42, 0)
                    ).hashCode());
            // TODO: DEVSIX-4756 start asserting not equals
            Assertions.assertEquals(new ObjectPath(obj41Gen0, obj42Gen0).hashCode(),
                    new ObjectPath(
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
