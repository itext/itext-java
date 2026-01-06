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
package com.itextpdf.kernel.validation;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.Pdf20ConformanceException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.PdfMcrNumber;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class Pdf20CheckerTest extends ExtendedITextTest {

    @Test
    public void parentChildRelationshipHandlerAccept() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.setTagged();
        Pdf20Checker.ParentChildRelationshipHandler handler = new Pdf20Checker.ParentChildRelationshipHandler(
                doc.getTagStructureContext());
        Assertions.assertFalse(handler.accept(null));
        Assertions.assertTrue(
                handler.accept(new PdfMcrNumber(new PdfNumber(10), new PdfStructElem(new PdfDictionary()))));
    }


    @Test
    public void parentChildRelationshipHandlerProcessRoot() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.setTagged();
        Pdf20Checker.ParentChildRelationshipHandler handler = new Pdf20Checker.ParentChildRelationshipHandler(
                doc.getTagStructureContext());
        PdfStructTreeRoot root = new PdfStructTreeRoot(doc);
        AssertUtil.doesNotThrow(() -> {
            handler.processElement(root);
        });
    }


    @Test
    public void parentChildRelationshipHandlerProcessPdfStructElemContentThatMayNotHaveContent() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.setTagged();
        Pdf20Checker.ParentChildRelationshipHandler handler = new Pdf20Checker.ParentChildRelationshipHandler(
                doc.getTagStructureContext());
        PdfStructElem elem = new PdfStructElem(new PdfDictionary());
        elem.getPdfObject().put(PdfName.S, PdfName.Div);
        PdfArray a = new PdfArray();
        a.add(new PdfMcrNumber(new PdfNumber(10), new PdfStructElem(new PdfDictionary())).getPdfObject());
        elem.getPdfObject().put(PdfName.K, a);
        Assertions.assertThrows(Pdf20ConformanceException.class, () -> {
            handler.processElement(elem);
        });

    }


    @Test
    public void parentChildRelationshipHandlerProcessPdfStructElemContentThatMayNotHaveChild() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.setTagged();
        Pdf20Checker.ParentChildRelationshipHandler handler = new Pdf20Checker.ParentChildRelationshipHandler(
                doc.getTagStructureContext());
        PdfStructElem elem = new PdfStructElem(new PdfDictionary());
        elem.getPdfObject().put(PdfName.S, PdfName.P);
        PdfArray a = new PdfArray();


        PdfStructElem child = new PdfStructElem(new PdfDictionary());
        child.getPdfObject().put(PdfName.S, PdfName.P);
        a.add(child.getPdfObject());
        elem.getPdfObject().put(PdfName.K, a);
        Exception e = Assertions.assertThrows(Pdf20ConformanceException.class, () -> {
            handler.processElement(elem);
        });

    }

    @Test
    public void parentChildRelationshipInvalidChild() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.setTagged();
        Pdf20Checker.ParentChildRelationshipHandler handler = new Pdf20Checker.ParentChildRelationshipHandler(
                doc.getTagStructureContext());

        PdfMcrNumber n = new PdfMcrNumber(new PdfNumber(10), new PdfStructElem(new PdfDictionary()));

        AssertUtil.doesNotThrow(() -> {
            handler.processElement(n);
        });

    }
}
