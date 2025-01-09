/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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

import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfIndirectReferenceTest extends ExtendedITextTest {

    @Test
    public void baseEqualsTest() {
        PdfIndirectReference reference = new PdfIndirectReference(null, 41, 0);
        Assertions.assertTrue(reference.equals(reference));
        Assertions.assertFalse(reference.equals(null));
        TestIndirectReference testIndirectReference = new TestIndirectReference(null, 41, 0);
        Assertions.assertFalse(reference.equals(testIndirectReference));
        Assertions.assertFalse(testIndirectReference.equals(reference));

    }

    @Test
    public void equalsWithDocTest() {
        try (PdfDocument firstDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                PdfDocument secondDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            // add a page to avoid exception throwing on close
            firstDoc.addNewPage();
            secondDoc.addNewPage();

            PdfIndirectReference obj41Gen0 = new PdfIndirectReference(firstDoc, 41, 0);

            Assertions.assertTrue(obj41Gen0.equals(new PdfIndirectReference(firstDoc, 41, 0)));
            Assertions.assertFalse(obj41Gen0.equals(new PdfIndirectReference(firstDoc, 42, 0)));
            Assertions.assertFalse(obj41Gen0.equals(new PdfIndirectReference(firstDoc, 41, 1)));
            Assertions.assertFalse(obj41Gen0.equals(new PdfIndirectReference(null, 41, 0)));
            Assertions.assertFalse(obj41Gen0.equals(new PdfIndirectReference(secondDoc, 41, 0)));
        }
    }

    @Test
    public void equalsWithNullDocsTest() {
        PdfIndirectReference obj41Gen0 = new PdfIndirectReference(null, 41, 0);

        Assertions.assertTrue(obj41Gen0.equals(new PdfIndirectReference(null, 41, 0)));
        Assertions.assertFalse(obj41Gen0.equals(new PdfIndirectReference(null, 42, 0)));
        Assertions.assertFalse(obj41Gen0.equals(new PdfIndirectReference(null, 41, 1)));

        try (PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            // add a page to avoid exception throwing on close
            doc.addNewPage();

            Assertions.assertFalse(obj41Gen0.equals(new PdfIndirectReference(doc, 41, 0)));
        }
    }

    @Test
    public void hashCodeTest() {
        PdfIndirectReference firstReference = new PdfIndirectReference(null, 41, 7);
        PdfIndirectReference secondReference = new PdfIndirectReference(null, 41, 7);
        Assertions.assertNotSame(firstReference, secondReference);
        Assertions.assertEquals(firstReference.hashCode(), secondReference.hashCode());
        Assertions.assertNotEquals(firstReference.hashCode(), new PdfIndirectReference(null, 42, 7).hashCode());
        Assertions.assertNotEquals(firstReference.hashCode(), new PdfIndirectReference(null, 41, 5).hashCode());

        try (PdfDocument firstDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                PdfDocument secondDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            // add a page to avoid exception throwing on close
            firstDoc.addNewPage();
            secondDoc.addNewPage();

            PdfIndirectReference obj41Gen0 = new PdfIndirectReference(firstDoc, 41, 0);

            Assertions.assertEquals(obj41Gen0.hashCode(), new PdfIndirectReference(firstDoc, 41, 0).hashCode());
            Assertions.assertNotEquals(obj41Gen0.hashCode(), new PdfIndirectReference(secondDoc, 41, 0).hashCode());
            Assertions.assertNotEquals(obj41Gen0.hashCode(), new PdfIndirectReference(null, 41, 0).hashCode());
        }
    }

    @Test
    public void compareToWithDocTest() {
        try (PdfDocument firstDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
                PdfDocument secondDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            // add a page to avoid exception throwing on close
            firstDoc.addNewPage();
            secondDoc.addNewPage();

            PdfIndirectReference obj41Gen7 = new PdfIndirectReference(firstDoc, 41, 7);

            Assertions.assertEquals(0, obj41Gen7.compareTo(new PdfIndirectReference(firstDoc, 41, 7)));

            Assertions.assertEquals(1, obj41Gen7.compareTo(new PdfIndirectReference(firstDoc, 11, 17)));
            Assertions.assertEquals(-1, obj41Gen7.compareTo(new PdfIndirectReference(firstDoc, 51, 0)));

            Assertions.assertEquals(-1, obj41Gen7.compareTo(new PdfIndirectReference(firstDoc, 41, 17)));
            Assertions.assertEquals(1, obj41Gen7.compareTo(new PdfIndirectReference(firstDoc, 41, 0)));

            Assertions.assertEquals(1, obj41Gen7.compareTo(new PdfIndirectReference(null, 41, 7)));
            // we do not expect that ids could be equal
            int docIdsCompareResult = firstDoc.getDocumentId() > secondDoc.getDocumentId() ? 1 : -1;
            Assertions.assertEquals(docIdsCompareResult,
                    obj41Gen7.compareTo(new PdfIndirectReference(secondDoc, 41, 7)));
            Assertions.assertEquals(-docIdsCompareResult,
                    new PdfIndirectReference(secondDoc, 41, 7).compareTo(obj41Gen7));
        }
    }

    @Test
    public void compareToWithNullDocsTest() {
        PdfIndirectReference obj41Gen7 = new PdfIndirectReference(null, 41, 7);

        Assertions.assertEquals(0, obj41Gen7.compareTo(new PdfIndirectReference(null, 41, 7)));
        Assertions.assertEquals(1, obj41Gen7.compareTo(new PdfIndirectReference(null, 11, 17)));
        Assertions.assertEquals(-1, obj41Gen7.compareTo(new PdfIndirectReference(null, 51, 0)));
        Assertions.assertEquals(-1, obj41Gen7.compareTo(new PdfIndirectReference(null, 41, 17)));
        Assertions.assertEquals(1, obj41Gen7.compareTo(new PdfIndirectReference(null, 41, 0)));

        try (PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            // add a page to avoid exception throwing on close
            doc.addNewPage();

            Assertions.assertEquals(-1, obj41Gen7.compareTo(new PdfIndirectReference(doc, 41, 7)));
            Assertions.assertEquals(1, obj41Gen7.compareTo(new PdfIndirectReference(doc, 11, 17)));
            Assertions.assertEquals(1, obj41Gen7.compareTo(new PdfIndirectReference(doc, 41, 0)));
        }
    }

    private static class TestIndirectReference extends PdfIndirectReference {
        public TestIndirectReference(PdfDocument doc, int objNr, int genNr) {
            super(doc, objNr, genNr);
        }
    }
}
