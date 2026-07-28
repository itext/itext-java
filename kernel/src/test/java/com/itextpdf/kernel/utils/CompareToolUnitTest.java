/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.objectpathitems.ObjectPath;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
// Android-Conversion-Skip-File (during Android conversion the class will be replaced by DeferredCompareTool)
public class CompareToolUnitTest extends ExtendedITextTest {

    @Test
    public void compareStreamsSizeTest() {
        byte[] bytes1 = {0, 1};
        byte[] bytes2 = {0, 1, 3};

        CompareTool compareTool = new CompareTool();
        Assertions.assertFalse(compareTool.compareStreams(new PdfStream(bytes1), new PdfStream(bytes2)));
    }

    @Test
    public void compareArraysNullTest() {
        int[] array1 = new  int[]{0, 1};
        PdfArray pdfArray1 = new PdfArray(array1);

        CompareTool compareTool = new CompareTool();
        Assertions.assertFalse(compareTool.compareArrays(null, pdfArray1));
    }

    @Test
    public void compareArraysSizeTest() {
        int[] array1 = new  int[]{0, 1};
        PdfArray pdfArray1 = new PdfArray(array1);
        int[] array2 = new  int[]{0, 1, 3 ,4};
        PdfArray pdfArray2 = new PdfArray(array2);

        CompareTool compareTool = new CompareTool();
        Assertions.assertFalse(compareTool.compareArrays(pdfArray1, pdfArray2));
    }

    @Test
    public void compareObjectsNullTest() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfIndirectReference ref1 = document.getCatalog().getDocument().createNextIndirectReference();

        CompareTool compareTool = new CompareTool();
        Assertions.assertTrue(compareTool.compareObjects(null, null, new ObjectPath(ref1, ref1),
                new CompareToolResult(3)));
    }

    @Test
    public void compareObjectsDirectTest() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfIndirectReference ref1 = document.getCatalog().getPdfObject().getIndirectReference();

        CompareToolResult result = new CompareToolResult(3);
        CompareTool compareTool = new CompareTool();
        Assertions.assertFalse(compareTool.compareObjects(ref1, document.getCatalog().getPdfObject(),
                new ObjectPath(ref1, ref1), result));
        Assertions.assertTrue(result.getDifferences().containsValue("Expected direct object."));
    }

    @Test
    public void compareObjectsIndirectTest() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfIndirectReference ref1 = document.getCatalog().getPdfObject().getIndirectReference();

        CompareToolResult result = new CompareToolResult(3);
        CompareTool compareTool = new CompareTool();
        Assertions.assertFalse(compareTool.compareObjects(document.getCatalog().getPdfObject(), ref1,
                new ObjectPath(ref1, ref1), result));
        Assertions.assertTrue(result.getDifferences().containsValue("Expected indirect object."));
    }

    @Test
    public void compareObjectsDifferentTypeTest() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfIndirectReference ref1 = document.getCatalog().getPdfObject().getIndirectReference();
        PdfStream stream = new PdfStream();

        CompareToolResult result = new CompareToolResult(3);
        CompareTool compareTool = new CompareTool();
        Assertions.assertFalse(compareTool.compareObjects(ref1, stream,
                new ObjectPath(ref1, ref1), result));
        Assertions.assertTrue(result.getDifferences().containsValue(
                "Types do not match. Expected: PdfStream. Found: PdfDictionary."));
    }

    @Test
    public void compareObjectsPageTest() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfIndirectReference ref1 = document.addNewPage().getPdfObject().getIndirectReference();
        PdfIndirectReference ref2 = document.getCatalog().getPdfObject().getIndirectReference();

        CompareToolResult result = new CompareToolResult(3);
        CompareTool compareTool = new CompareTool();
        Assertions.assertFalse(compareTool.compareObjects(ref2, ref1,
                new ObjectPath(ref1, ref1), result));
        Assertions.assertTrue(result.getDifferences().containsValue(
                "Expected a page. Found not a page."));
    }
}
