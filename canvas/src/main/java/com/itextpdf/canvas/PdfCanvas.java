package com.itextpdf.canvas;

import com.itextpdf.canvas.colors.Color;
import com.itextpdf.core.fonts.Font;
import com.itextpdf.core.pdf.IPdfXObject;
import com.itextpdf.core.pdf.PdfContentStream;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfWriter;

import java.io.IOException;
import java.util.Stack;

public class PdfCanvas {

    protected Stack<PdfGraphicsState> gsStack = new Stack<PdfGraphicsState>();
    protected PdfGraphicsState currentGs = new PdfGraphicsState();
    protected PdfContentStream contentStream;

    protected PdfCanvas() {

    }

    /**
     * Creates PdfCanvas from content stream of page, form XObject, patter etc.
     *
     * @param contentStream
     */
    public PdfCanvas(PdfContentStream contentStream) {
        this.contentStream = contentStream;
    }

    /**
     * Convenience method for fast PDfCanvas creation by a certain page.
     *
     * @param doc
     * @param pageNum
     */
    public PdfCanvas(PdfDocument doc, int pageNum) {
        this(doc.getPage(pageNum).getContentStream());
    }

    /**
     * Saves graphics state.
     *
     * @return current canvas.
     */
    public PdfCanvas saveState() {
        gsStack.push(currentGs);
        currentGs = new PdfGraphicsState(currentGs);
        return this;
    }

    /**
     * Restores graphics state.
     *
     * @return current canvas.
     */
    public PdfCanvas restoreState() {
        currentGs = gsStack.pop();
        return this;
    }

    /**
     * Gets current graphics state.
     *
     * @return current graphics state.
     */
    public PdfGraphicsState currentState() {
        return currentGs;
    }

    /**
     * Begins text block (PDF BT operator).
     *
     * @return current canvas.
     */
    public PdfCanvas beginText() {
        return this;
    }

    /**
     * Ends text block (PDF ET operator).
     *
     * @return current canvas.
     */
    public PdfCanvas endText() {
        return this;
    }

    /**
     * Sets font and size (PDF Tf operator).
     *
     * @param font
     * @param size
     * @return current canvas.
     */
    public PdfCanvas setFontAndSize(Font font, float size) {
        return this;
    }

    /**
     * Moves text by shifting text line matrix (PDF Td operator).
     *
     * @param x
     * @param y
     * @return current canvas.
     */
    public PdfCanvas moveText(float x, float y) {
        return this;
    }

    /**
     * Sets text renderiing mode.
     *
     * @param textRenderingMode
     * @return current canvas.
     */
    public PdfCanvas setTextRenderingMode(int textRenderingMode) {
        return this;
    }

    /**
     * Sets text matrix.
     *
     * @param a
     * @param b
     * @param c
     * @param d
     * @param e
     * @param f
     * @return current canvas.
     */
    public PdfCanvas setTextMatrix(float a, float b, float c, float d, float e, float f) {
        return this;
    }

    /**
     * Shows text (operator Tj).
     *
     * @param text
     * @return current canvas.
     */
    public PdfCanvas showText(String text) {
        return this;
    }

    /**
     * Draws rectangle.
     *
     * @param x
     * @param y
     * @param width
     * @param height
     * @return current canvas.
     */
    public PdfCanvas rectangle(float x, float y, float width, float height) throws IOException {
        contentStream.getOutputStream().writeFloat(x).
                writeChar(' ').
                writeFloat(y).
                writeChar(' ').
                writeFloat(width).
                writeChar(' ').
                writeFloat(height).
                writeString(" re\n");
        return this;
    }

    /**
     * Draws rounded rectangle.
     *
     * @param x
     * @param y
     * @param w
     * @param h
     * @param r
     * @return current canvas.
     */
    public PdfCanvas roundRectangle(float x, float y, float w, float h, float r) {
        return this;
    }

    /**
     * Fills current path.
     *
     * @return current canvas.
     */
    public PdfCanvas fill() throws IOException {
        contentStream.getOutputStream().writeString("f\n");
        return this;
    }

    /**
     * EOFills current path.
     *
     * @return current canvas.
     */
    public PdfCanvas eoFill() {
        return this;
    }

    /**
     * Adds XObject.
     *
     * @param xObj
     * @param x
     * @param y
     * @return current canvas.
     */
    public PdfCanvas addXObject(IPdfXObject xObj, float x, float y) {
        return this;
    }

    /**
     * Sets line width.
     *
     * @param lineWidth
     * @return current canvas.
     */
    public PdfCanvas setLineWidth(float lineWidth) {
        return this;
    }

    /**
     * Sets fill color.
     *
     * @param color
     * @return current canvas.
     */
    public PdfCanvas setFillColor(Color color) {
        return this;
    }

    /**
     * Sets stroke color.
     *
     * @param color
     * @return current canvas.
     */
    public PdfCanvas setStrokeColor(Color color) {
        return this;
    }

    /**
     * Begins OCG layer.
     *
     * @param layer
     * @return current canvas.
     */
    public PdfCanvas beginLayer(PdfLayer layer) {
        return this;
    }

    /**
     * Ends OCG layer.
     *
     * @return current canvas.
     */
    public PdfCanvas endLayer() {
        return this;
    }


}
