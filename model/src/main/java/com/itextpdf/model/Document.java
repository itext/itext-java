package com.itextpdf.model;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.model.elements.IElement;
import com.itextpdf.model.layout.DefaultLayoutMgr;
import com.itextpdf.model.layout.ILayoutMgr;
import com.itextpdf.model.layout.shapes.BoxShape;
import com.itextpdf.model.layout.shapes.ILayoutShape;

import java.io.IOException;
import java.util.ArrayList;

public class Document {

    protected ILayoutMgr layoutMgr;
    protected PdfDocument pdfDocument;
    protected PdfPage page = null;

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
    public void close() throws PdfException {
        pdfDocument.close();
    }

    /**
     * Adds an element to the document. The element is immediately placed with the layout manager.
     *
     * @param element
     * @return
     */
    public Document add(IElement element) throws PdfException {
        if (page == null)
            newPage();
        layoutMgr.placeElement(element);
        return this;
    }

    /**
     * Requests a new page with a default page size.
     *
     * @return
     */
    public Document newPage() throws PdfException {
        return newPage(pdfDocument.getDefaultPageSize());
    }

    /**
     * Requests a new pages with a certain page size.
     *
     * @param pageSize
     * @return
     */
    public Document newPage(PageSize pageSize) throws PdfException {
        if (page != null) {
            page.flush();
        }
        PdfPage page = pdfDocument.addNewPage(pageSize);
        layoutMgr.setCanvas(new PdfCanvas(page));
        final BoxShape boxShape = new BoxShape(pageSize);
        layoutMgr.setShapes(new ArrayList<ILayoutShape>() {{
            add(boxShape);
        }});
        return this;
    }

    /**
     * Gets PDF document.
     *
     * @return
     */
    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    /**
     * Gets current layout manager.
     *
     * @return
     */
    public ILayoutMgr getLayoutMgr() {
        return layoutMgr;
    }

    /**
     * Sets layout manager.
     *
     * @param layoutMgr
     */
    public void setLayoutMgr(ILayoutMgr layoutMgr) {
        this.layoutMgr = layoutMgr;
    }

}
