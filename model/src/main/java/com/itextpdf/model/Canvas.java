package com.itextpdf.model;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.model.renderer.CanvasRenderer;
import com.itextpdf.model.renderer.RootRenderer;

public class Canvas extends RootElement<Canvas> {

    protected PdfCanvas pdfCanvas;
    protected Rectangle rootArea;

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

    @Override
    protected RootRenderer ensureRootRendererNotNull() {
        if (rootRenderer == null)
            rootRenderer = new CanvasRenderer(this, immediateFlush);
        return rootRenderer;
    }

}