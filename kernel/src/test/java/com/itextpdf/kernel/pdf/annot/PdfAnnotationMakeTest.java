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
package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

import java.io.ByteArrayOutputStream;

@Tag("UnitTest")
public class PdfAnnotationMakeTest extends ExtendedITextTest {

    @Test
    public void makePdfAnnotationTest() {
        PdfDictionary object = new PdfDictionary();
        PdfAnnotation result = PdfAnnotation.makeAnnotation(object);
        Assertions.assertNull(result.getSubtype());
    }

    @Test
    public void makePdfTextAnnotationTest() {
        PdfDictionary object = new PdfDictionary();
        object.put(PdfName.Subtype, PdfName.Text);
        PdfAnnotation result = PdfAnnotation.makeAnnotation(object);
        Assertions.assertTrue(result instanceof PdfTextAnnotation);
    }

    @Test
    public void makeIndirectPdfAnnotationTest() {
        PdfDictionary object = new PdfDictionary();
        try (PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            doc.addNewPage();
            PdfObject indirect = object.makeIndirect(doc);
            object.put(PdfName.Subtype, PdfName.Text);
            PdfAnnotation result = PdfAnnotation.makeAnnotation(indirect);
            Assertions.assertTrue(result instanceof PdfTextAnnotation);
        }
    }

    @Test
    public void makePdfPolyAnnotationTest() {
        PdfDictionary object = new PdfDictionary();
        object.put(PdfName.Subtype, PdfName.Polygon);
        PdfAnnotation result = PdfAnnotation.makeAnnotation(object);
        Assertions.assertTrue(result instanceof PdfPolyGeomAnnotation);
    }

    @Test
    public void makePdfUnknownAnnotationTest() {
        PdfDictionary object = new PdfDictionary();
        // from DEVSIX-2661
        object.put(PdfName.Subtype, new PdfName("BatesN"));
        PdfAnnotation result = PdfAnnotation.makeAnnotation(object);
        Assertions.assertTrue(result instanceof PdfAnnotation.PdfUnknownAnnotation);
    }
}
