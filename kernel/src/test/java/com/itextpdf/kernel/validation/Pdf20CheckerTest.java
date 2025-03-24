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