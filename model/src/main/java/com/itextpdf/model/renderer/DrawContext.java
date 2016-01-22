package com.itextpdf.model.renderer;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.pdf.PdfDocument;

public class DrawContext {
    private PdfDocument document;
    private PdfCanvas canvas;
    private boolean taggingEnabled;

    public DrawContext(PdfDocument document, PdfCanvas canvas) {
        this(document, canvas, true);
    }

    public DrawContext(PdfDocument document, PdfCanvas canvas, boolean enableTagging) {
        this.document = document;
        this.canvas = canvas;
        this.taggingEnabled = enableTagging;
    }

    public PdfDocument getDocument() {
        return document;
    }

    public PdfCanvas getCanvas() {
        return canvas;
    }

    public boolean isTaggingEnabled() {
        return taggingEnabled;
    }

    public void setTaggingEnabled(boolean taggingEnabled) {
        this.taggingEnabled = taggingEnabled;
    }
}
