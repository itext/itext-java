package com.itextpdf.model;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.element.IElement;
import com.itextpdf.model.renderer.DocumentRenderer;

public class Document implements IPropertyContainer {

    protected PdfDocument pdfDocument;
    protected DocumentRenderer documentRenderer;

    public Document(PdfDocument pdfDoc) {
        this(pdfDoc, pdfDoc.getDefaultPageSize());
    }

    public Document(PdfDocument pdfDoc, PageSize pageSize) {
        pdfDocument = pdfDoc;
        pdfDocument.setDefaultPageSize(pageSize);
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
        ensureDocumentRendererNotNull().addChild(element.makeRenderer());
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

    public void setRenderer(DocumentRenderer documentRenderer) {
        this.documentRenderer = documentRenderer;
    }

    private DocumentRenderer ensureDocumentRendererNotNull() {
        if (documentRenderer == null)
            documentRenderer = new DocumentRenderer(this);
        return documentRenderer;
    }

    @Override
    public <T> T getProperty(Integer propertyKey) {
        return null;
    }

    @Override
    public <T> T getDefaultProperty(Integer propertyKey) {
        return null;
    }
}
