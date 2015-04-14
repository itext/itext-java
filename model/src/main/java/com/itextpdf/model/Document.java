package com.itextpdf.model;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.PageSize;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.element.IElement;
import com.itextpdf.model.renderer.DocumentRenderer;

import java.util.ArrayList;
import java.util.List;

public class Document implements IPropertyContainer {

    protected PdfDocument pdfDocument;
    protected DocumentRenderer documentRenderer;
    protected boolean immediateFlush = true;
    protected List<IElement> childElements = new ArrayList<>();

    public Document(PdfDocument pdfDoc) {
        this(pdfDoc, pdfDoc.getDefaultPageSize());
    }

    public Document(PdfDocument pdfDoc, PageSize pageSize) {
        this(pdfDoc, pageSize, true);
    }

    public Document(PdfDocument pdfDoc, PageSize pageSize, boolean immediateFlush) {
        this.pdfDocument = pdfDoc;
        this.pdfDocument.setDefaultPageSize(pageSize);
        this.immediateFlush = immediateFlush;
    }

    /**
     * Closes the document and associated PdfDocument.
     */
    public void close() throws PdfException {
        if (documentRenderer != null && !immediateFlush)
            documentRenderer.flush();
        pdfDocument.close();
    }

    /**
     * Adds an element to the document. The element is immediately placed with the layout manager.
     *
     * @param element
     * @return
     */
    public Document add(IElement element) throws PdfException {
        childElements.add(element);
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

    public DocumentRenderer getRenderer() {
        return documentRenderer;
    }

    public void setRenderer(DocumentRenderer documentRenderer) {
        this.documentRenderer = documentRenderer;
    }

    public void flush() {
        documentRenderer.flush();
    }

    public void relayout() {
        try {
            while (pdfDocument.getNumOfPages() > 0)
                pdfDocument.removePage(pdfDocument.getNumOfPages());
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
        documentRenderer = new DocumentRenderer(this, immediateFlush);
        for (IElement element : childElements) {
            ensureDocumentRendererNotNull().addChild(element.makeRenderer());
        }
    }

    @Override
    public <T> T getProperty(Integer propertyKey) {
        return null;
    }

    @Override
    public <T> T getDefaultProperty(Integer propertyKey) {
        return null;
    }

    private DocumentRenderer ensureDocumentRendererNotNull() {
        if (documentRenderer == null)
            documentRenderer = new DocumentRenderer(this, immediateFlush);
        return documentRenderer;
    }
}
