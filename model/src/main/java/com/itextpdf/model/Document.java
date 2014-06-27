package com.itextpdf.model;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.model.elements.IElement;
import com.itextpdf.model.layout.DefaultLayoutMgr;
import com.itextpdf.model.layout.ILayoutMgr;
import com.itextpdf.model.layout.shapes.BoxShape;
import com.itextpdf.model.layout.shapes.ILayoutShape;

import java.util.ArrayList;

public class Document {

    protected ILayoutMgr layoutMgr;
    protected PdfDocument pdfDocument;

    public Document(PdfDocument pdfDoc) {
        this(pdfDoc, pdfDoc.getDefaultPageSize());
    }

    public Document(PdfDocument pdfDoc, PageSize pageSize) {
        pdfDocument = pdfDoc;
        pdfDocument.setDefaultPageSize(pageSize);
        layoutMgr = new DefaultLayoutMgr(this);
    }

    /**
     * Closes the document and associated PdfDocument.
     */
    public void close() {

    }

    public Document add(IElement element) {
        if (pdfDocument.getCurrentPage() == null)
            newPage();
        layoutMgr.placeElement(element);
        return this;
    }

    public Document newPage() {
        return newPage(pdfDocument.getDefaultPageSize());
    }

    public Document newPage(PageSize pageSize) {
        if (pdfDocument.getCurrentPage() != null) {
            pdfDocument.getCurrentPage().flush();
        }
        PdfPage page = pdfDocument.addNewPage(pageSize);
        layoutMgr.setCanvas(new PdfCanvas(page.getContentStream()));
        final BoxShape boxShape = new BoxShape(pageSize);
        layoutMgr.setShapes(new ArrayList<ILayoutShape>() {{
            add(boxShape);
        }});
        return this;
    }

    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    public ILayoutMgr getLayoutMgr() {
        return layoutMgr;
    }

    public void setLayoutMgr(ILayoutMgr layoutMgr) {
        this.layoutMgr = layoutMgr;
    }

}
