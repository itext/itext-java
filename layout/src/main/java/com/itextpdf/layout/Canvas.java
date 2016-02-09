package com.itextpdf.layout;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.layout.renderer.CanvasRenderer;
import com.itextpdf.layout.renderer.RootRenderer;

/**
 * This class is used for adding content directly onto a specified {@link PdfCanvas}.
 * {@link Canvas} does not know the concept of a page, so it can't reflow to a 'next' {@link Canvas}.
 * 
 * This class effectively acts as a bridge between the high-level <em>layout</em>
 * API and the low-level <em>kernel</em> API.
 */
public class Canvas extends RootElement<Canvas> {

    protected PdfCanvas pdfCanvas;
    protected Rectangle rootArea;

    /**
     * Is initialized and used only when Canvas element autotagging is enabled, see {@link #enableAutoTagging(PdfPage)}.
     * It is also used to determine if autotagging is enabled.
     */
    protected PdfPage page;

    /**
     * Creates a new Canvas to manipulate a specific document and page.
     * 
     * @param pdfCanvas the low-level content stream writer
     * @param pdfDocument the document that the resulting content stream will be written to
     * @param rootArea the maximum area that the Canvas may write upon
     */
    public Canvas(PdfCanvas pdfCanvas, PdfDocument pdfDocument, Rectangle rootArea) {
        this.pdfDocument = pdfDocument;
        this.pdfCanvas = pdfCanvas;
        this.rootArea = rootArea;
    }

    /**
     * Creates a new Canvas to manipulate a specific {@link PdfFormXObject}.
     * 
     * @param formXObject the form
     * @param pdfDocument the document that the resulting content stream will be written to
     */
    public Canvas(PdfFormXObject formXObject, PdfDocument pdfDocument) {
        this.pdfDocument = pdfDocument;
        this.pdfCanvas = new PdfCanvas(formXObject, pdfDocument);
        this.rootArea = formXObject.getBBox().toRectangle();
    }

    /**
     * Gets the {@link PdfDocument} for this canvas.
     * @return the document that the resulting content stream will be written to
     */
    public PdfDocument getPdfDocument() {
        return pdfDocument;
    }

    /**
     * Gets the root area rectangle.
     * @return the maximum area that the Canvas may write upon
     */
    public Rectangle getRootArea() {
        return rootArea;
    }

    /**
     * Gets the {@link PdfCanvas}.
     * @return the low-level content stream writer
     */
    public PdfCanvas getPdfCanvas() {
        return pdfCanvas;
    }

    /**
     * Sets the {@link IRenderer} for this Canvas.
     * 
     * @param canvasRenderer a renderer specific for canvas operations
     */
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