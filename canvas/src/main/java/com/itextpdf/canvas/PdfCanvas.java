package com.itextpdf.canvas;

import com.itextpdf.canvas.colors.Color;
import com.itextpdf.core.fonts.Font;
import com.itextpdf.core.pdf.IPdfXObject;
import com.itextpdf.core.pdf.PdfContentStream;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfPage;

import java.util.Stack;

public class PdfCanvas {

    protected Stack<PdfGraphicsState> gsStack = new Stack<PdfGraphicsState>();
    protected PdfGraphicsState currentGs = new PdfGraphicsState();

    protected PdfCanvas() {

    }

    public PdfCanvas(PdfContentStream contentStream) {

    }

    public PdfCanvas(PdfDocument doc, int pageNum) {

    }

    /**
     * Saves graphics state.
     * @return current canvas.
     */
    public PdfCanvas saveState() {
        gsStack.push(currentGs);
        currentGs = new PdfGraphicsState(currentGs);
        return this;
    }

    /**
     * Restores graphics state.
     * @return current canvas.
     */
    public PdfCanvas restoreState() {
        currentGs = gsStack.pop();
        return this;
    }

    /**
     * Gets current graphics state.
     * @return current graphics state.
     */
    public PdfGraphicsState currentState() {
        return currentGs;
    }

    /**
     * Begins text block (PDF BT operator).
     * @return current canvas.
     */
    public PdfCanvas beginText() {
        return this;
    }

    /**
     * Ends text block (PDF ET operator).
     * @return current canvas.
     */
    public PdfCanvas endText() {
        return this;
    }

    /**
     * Sets font and size (PDF Tf operator).
     * @param font
     * @param size
     * @return current canvas.
     */
    public PdfCanvas setFontAndSize(Font font, float size) {
        return this;
    }

    /**
     * Moves text by shifting text line matrix (PDF Td operator).
     * @param x
     * @param y
     * @return current canvas.
     */
    public PdfCanvas moveText(float x, float y) {
        return this;
    }

    public PdfCanvas setTextRenderingMode(int textRenderingMode) {
        return this;
    }

    public PdfCanvas setTextMatrix(float a, float b, float c, float d, float e, float f) {
        return this;
    }

    public PdfCanvas showText(String text) {
        return this;
    }

    public PdfCanvas rectangle(float x, float y, float width, float height) {
        return this;
    }

    public PdfCanvas roundRectangle(float x, float y, float w, float h, float r) {
        return this;
    }

    public PdfCanvas fill() {
        return this;
    }

    public PdfCanvas eoFill() {
        return this;
    }

    public PdfCanvas addXObject(IPdfXObject xObj, float x, float y) {
        return this;
    }

    public PdfCanvas setLineWidth(float lineWidth) {
        return this;
    }

    public PdfCanvas setFillColor(Color color) {
        return this;
    }

    public PdfCanvas setStrokeColor(Color color) {
        return this;
    }

    public PdfCanvas beginLayer(PdfLayer layer) {
        return this;
    }

    public PdfCanvas endLayer() {
        return this;
    }


}
