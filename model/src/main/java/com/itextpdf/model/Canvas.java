package com.itextpdf.model;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.model.renderer.CanvasRenderer;
import com.itextpdf.model.renderer.RootRenderer;

public class Canvas extends RootElement<Canvas> {

    protected PdfCanvas pdfCanvas;
    protected Rectangle rootArea;

    /**
     * Is initialized and used only when Canvas element autotagging is enabled, see {@link #enableAutoTagging(PdfPage)}.
     * It is also used to determine if autotagging is enabled.
     */
    protected PdfPage page;

    public Canvas(PdfCanvas pdfCanvas, PdfDocument pdfDocument, Rectangle rootArea) {
        this.pdfDocument = pdfDocument;
        this.pdfCanvas = pdfCanvas;
        this.rootArea = rootArea;
    }

    public Canvas(PdfFormXObject formXObject, PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
        this.pdfCanvas = new PdfCanvas(formXObject, pdfDocument);
        this.rootArea = formXObject.getBBox().toRectangle();
    }

    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    public Rectangle getRootArea() {
        return rootArea;
    }

    public PdfCanvas getPdfCanvas() {
        return pdfCanvas;
    }

    public void setRenderer(CanvasRenderer canvasRenderer) {
        this.rootRenderer = canvasRenderer;
    }

    /**
     * Returned value is not null only in case when autotagging is enabled.
     * @return the page, on which this canvas will be rendered, or null if autotagging is not enabled.
     */
    public PdfPage getPage() {
        return page;
    }

    /**
     * Enables canvas content autotagging. By default it is disabled.
     * @param page the page, on which this canvas will be rendered.
     */
    public void enableAutoTagging(PdfPage page) {
        this.page = page;
    }

    /**
     * @return true if autotagging of canvas content is enabled. Default value - false.
     */
    public boolean isAutoTaggingEnabled() {
        return page != null;
    }

    @Override
    protected RootRenderer ensureRootRendererNotNull() {
        if (rootRenderer == null)
            rootRenderer = new CanvasRenderer(this, immediateFlush);
        return rootRenderer;
    }

}