package com.itextpdf.model;

import com.itextpdf.basics.geom.PageSize;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.model.element.AreaBreak;
import com.itextpdf.model.element.BlockElement;
import com.itextpdf.model.element.IElement;
import com.itextpdf.model.element.ILargeElement;
import com.itextpdf.model.renderer.DocumentRenderer;
import com.itextpdf.model.renderer.RootRenderer;

public class Document extends RootElement<Document> {

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
    public void close() {
        if (rootRenderer != null && !immediateFlush)
            rootRenderer.flush();
        pdfDocument.close();
    }

    public Document add(AreaBreak areaBreak) {
        childElements.add(areaBreak);
        ensureRootRendererNotNull().addChild(areaBreak.createRendererSubTree());
        return this;
    }

    @Override
    public Document add(BlockElement element) {
        super.add(element);
        if (element instanceof ILargeElement) {
            ((ILargeElement) element).setDocument(this);
            ((ILargeElement) element).flushContent();
        }
        return this;
    }

    /**
     * Gets PDF document.
     */
    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    public void setRenderer(DocumentRenderer documentRenderer) {
        this.rootRenderer = documentRenderer;
    }

    public void flush() {
        rootRenderer.flush();
    }

    public void relayout() {
        if (immediateFlush) {
            throw new IllegalStateException("Operation not supported with immediate flush");
        }

        try {
            while (pdfDocument.getNumOfPages() > 0)
                pdfDocument.removePage(pdfDocument.getNumOfPages());
        } catch (Exception exc) {
            throw new RuntimeException(exc);
        }
        rootRenderer = new DocumentRenderer(this, immediateFlush);
        for (IElement element : childElements) {
            rootRenderer.addChild(element.createRendererSubTree());
        }
    }

    protected RootRenderer ensureRootRendererNotNull() {
        if (rootRenderer == null)
            rootRenderer = new DocumentRenderer(this, immediateFlush);
        return rootRenderer;
    }
}
