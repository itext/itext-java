package com.itextpdf.canvas;

import com.itextpdf.canvas.colors.Color;
import com.itextpdf.core.exceptions.PdfException;
import com.itextpdf.core.fonts.PdfFont;
import com.itextpdf.core.pdf.IPdfXObject;
import com.itextpdf.core.pdf.PdfContentStream;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.fonts.PdfEncodings;
import com.itextpdf.io.streams.OutputStream;

import java.io.IOException;
import java.util.Stack;

public class PdfCanvas {

    static final private byte[] q = OutputStream.getIsoBytes("q\n");
    static final private byte[] Q = OutputStream.getIsoBytes("Q\n");
    static final private byte[] f = OutputStream.getIsoBytes("f\n");
    static final private byte[] fStar = OutputStream.getIsoBytes("f*\n");
    static final private byte[] re = OutputStream.getIsoBytes("re\n");
    static final private byte[] BT = OutputStream.getIsoBytes("BT\n");
    static final private byte[] ET = OutputStream.getIsoBytes("ET\n");
    static final private byte[] Tf = OutputStream.getIsoBytes(" Tf\n");
    static final private byte[] Tj = OutputStream.getIsoBytes("Tj\n");
    static final private byte[] Tm = OutputStream.getIsoBytes(" Tm\n");
    static final private byte[] Td = OutputStream.getIsoBytes(" Td\n");
    static final private byte[] Tr = OutputStream.getIsoBytes(" Tr\n");
    static final private byte[] escR = OutputStream.getIsoBytes("\r");
    static final private byte[] escN = OutputStream.getIsoBytes("\n");
    static final private byte[] escT = OutputStream.getIsoBytes("\t");
    static final private byte[] escB = OutputStream.getIsoBytes("\b");
    static final private byte[] escF = OutputStream.getIsoBytes("\f");

    protected Stack<PdfGraphicsState> gsStack = new Stack<PdfGraphicsState>();
    protected PdfGraphicsState currentGs = new PdfGraphicsState();
    protected PdfContentStream contentStream;

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
    public PdfCanvas saveState() throws IOException {
        gsStack.push(currentGs);
        currentGs = new PdfGraphicsState(currentGs);
        contentStream.getOutputStream().writeBytes(q);
        return this;
    }

    /**
     * Restores graphics state.
     *
     * @return current canvas.
     */
    public PdfCanvas restoreState() throws IOException {
        currentGs = gsStack.pop();
        contentStream.getOutputStream().writeBytes(Q);
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
    public PdfCanvas beginText() throws IOException {
        contentStream.getOutputStream().writeBytes(BT);
        return this;
    }

    /**
     * Ends text block (PDF ET operator).
     *
     * @return current canvas.
     */
    public PdfCanvas endText() throws IOException {
        contentStream.getOutputStream().writeBytes(ET);
        return this;
    }

    /**
     * Sets font and size (PDF Tf operator).
     *
     * @param font
     * @param size
     * @return current canvas.
     */
    public PdfCanvas setFontAndSize(PdfFont font, float size) throws IOException, PdfException {
        if (size < 0.0001f && size > -0.0001f)
            throw new PdfException(PdfException.fontSizeTooSmall);
        currentGs.size = size;
        currentGs.fontName = contentStream.getResources().addFont(font);
        contentStream.getOutputStream()
                .write(currentGs.fontName)
                .writeSpace()
                .writeFloat(size)
                .writeBytes(Tf);
        return this;
    }

    /**
     * Moves text by shifting text line matrix (PDF Td operator).
     *
     * @param x
     * @param y
     * @return current canvas.
     */
    public PdfCanvas moveText(float x, float y) throws IOException {
        contentStream.getOutputStream()
                .writeFloat(x)
                .writeSpace()
                .writeFloat(y)
                .writeBytes(Td);
        return this;
    }

    /**
     * Sets text rendering mode.
     *
     * @param textRenderingMode
     * @return current canvas.
     */
    public PdfCanvas setTextRenderingMode(int textRenderingMode) throws IOException {
        currentGs.textRenderingMode = textRenderingMode;
        contentStream.getOutputStream()
                .writeInteger(textRenderingMode)
                .writeBytes(Tr);
        return this;
    }

    /**
     * Changes the text matrix.
     * <p/>
     * Remark: this operation also initializes the current point position.</P>
     *
     * @param a operand 1,1 in the matrix
     * @param b operand 1,2 in the matrix
     * @param c operand 2,1 in the matrix
     * @param d operand 2,2 in the matrix
     * @param x operand 3,1 in the matrix
     * @param y operand 3,2 in the matrix
     * @return current canvas.
     */
    public PdfCanvas setTextMatrix(float a, float b, float c, float d, float x, float y) throws IOException {
        contentStream.getOutputStream()
                .writeFloat(a)
                .writeSpace()
                .writeFloat(b)
                .writeSpace()
                .writeFloat(c)
                .writeSpace()
                .writeFloat(d)
                .writeSpace()
                .writeFloat(x)
                .writeSpace()
                .writeFloat(y)
                .writeBytes(Tm);
        return this;
    }

    /**
     * Shows text (operator Tj).
     *
     * @param text
     * @return current canvas.
     */
    public PdfCanvas showText(String text) throws IOException, PdfException {
        showText2(text);
        contentStream.getOutputStream().write(Tj);
        return this;
    }

    /**
     * A helper to insert into the content stream the <CODE>text</CODE>
     * converted to bytes according to the font's encoding.
     *
     * @param text the text to write
     */
    private void showText2(final String text) throws IOException, PdfException {
        if (currentGs.fontName == null)
            throw new PdfException(PdfException.fontAndSizeMustBeSetBeforeWritingAnyText);
        byte b[] = PdfEncodings.convertToBytes(text, PdfEncodings.WINANSI);
        escapeString(b);
    }

    /**
     * Escapes a <CODE>byte</CODE> array according to the PDF conventions.
     *
     * @param b the <CODE>byte</CODE> array to escape
     */
    private void escapeString(final byte b[]) throws IOException {
        OutputStream output = contentStream.getOutputStream();
        output.writeChar('(');
        for (int k = 0; k < b.length; ++k) {
            byte c = b[k];
            switch (c) {
                case '\r':
                    output.writeBytes(escR);
                    break;
                case '\n':
                    output.writeBytes(escN);
                    break;
                case '\t':
                    output.writeBytes(escT);
                    break;
                case '\b':
                    output.writeBytes(escB);
                    break;
                case '\f':
                    output.writeBytes(escF);
                    break;
                case '(':
                case ')':
                case '\\':
                    output.writeChar('\\').writeByte(c);
                    break;
                default:
                    output.writeByte(c);
            }
        }
        output.writeChar(')');
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
                writeSpace().
                writeFloat(y).
                writeSpace().
                writeFloat(width).
                writeSpace().
                writeFloat(height).
                writeSpace().
                writeBytes(re);
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
        contentStream.getOutputStream().writeBytes(f);
        return this;
    }

    /**
     * EOFills current path.
     *
     * @return current canvas.
     */
    public PdfCanvas eoFill() throws IOException {
        contentStream.getOutputStream().writeBytes(fStar);
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
