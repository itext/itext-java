package com.itextpdf.canvas;

import com.itextpdf.canvas.colors.Color;
import com.itextpdf.basics.PdfException;
import com.itextpdf.core.fonts.PdfEncodings;
import com.itextpdf.core.fonts.PdfFont;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.core.pdf.xobject.PdfXObject;
import com.itextpdf.basics.io.OutputStream;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Stack;

public class PdfCanvas {

    static final private byte[] c = OutputStream.getIsoBytes(" c\n");
    static final private byte[] l = OutputStream.getIsoBytes(" l\n");
    static final private byte[] m = OutputStream.getIsoBytes(" m\n");
    static final private byte[] v = OutputStream.getIsoBytes(" v\n");
    static final private byte[] y = OutputStream.getIsoBytes(" y\n");
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
    static final private byte[] w = OutputStream.getIsoBytes(" w\n");
    static final private byte[] h = OutputStream.getIsoBytes("h\n");
    static final private byte[] n = OutputStream.getIsoBytes("n\n");
    static final private byte[] S = OutputStream.getIsoBytes("S\n");
    static final private byte[] s = OutputStream.getIsoBytes("s\n");

    protected Stack<PdfGraphicsState> gsStack = new Stack<PdfGraphicsState>();
    protected PdfGraphicsState currentGs = new PdfGraphicsState();
    protected PdfStream contentStream;
    protected PdfResources resources;

    /**
     * Creates PdfCanvas from content stream of page, form XObject, pattern etc.
     *
     * @param contentStream @see PdfStream.
     */
    public PdfCanvas(PdfStream contentStream, PdfResources resources) {
        this.contentStream = contentStream;
        this.resources = resources;
    }

    /**
     * Convenience method for fast PdfCanvas creation by a certain page.
     *
     * @param page page to create canvas from.
     */
    public PdfCanvas(PdfPage page) {
        this(page.getContentStream(), page.getResources());
    }

    public PdfCanvas(PdfFormXObject xObj) {
        this(xObj.getPdfObject(), xObj.getResources());
    }

    /**
     * Convenience method for fast PdfCanvas creation by a certain page.
     *
     * @param doc     @see PdfDocument.
     * @param pageNum page number.
     */
    public PdfCanvas(PdfDocument doc, int pageNum) {
        this(doc.getPage(pageNum));
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
     * @param font @see PdfFont.
     * @param size Font size.
     * @return current canvas.
     */
    public PdfCanvas setFontAndSize(PdfFont font, float size) throws IOException, PdfException {
        if (size < 0.0001f && size > -0.0001f)
            throw new PdfException(PdfException.FontSizeTooSmall, size);
        currentGs.size = size;
        currentGs.fontName = resources.addFont(font);
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
     * @param x x coordinate.
     * @param y y coordinate.
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
     * @param textRenderingMode text rendering mode @see PdfCanvasConstants.
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
     *
     * @param a operand 1,1 in the matrix.
     * @param b operand 1,2 in the matrix.
     * @param c operand 2,1 in the matrix.
     * @param d operand 2,2 in the matrix.
     * @param x operand 3,1 in the matrix.
     * @param y operand 3,2 in the matrix.
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
     * @param text text to show.
     * @return current canvas.
     */
    public PdfCanvas showText(String text) throws IOException, PdfException {
        showText2(text);
        contentStream.getOutputStream().write(Tj);
        return this;
    }

    /**
     * A helper to insert into the content stream the <code>text</code>
     * converted to bytes according to the font's encoding.
     *
     * @param text the text to write.
     */
    private void showText2(final String text) throws IOException, PdfException {
        if (currentGs.fontName == null)
            throw new PdfException(PdfException.FontAndSizeMustBeSetBeforeWritingAnyText, currentGs);
        byte b[] = PdfEncodings.convertToBytes(text, PdfEncodings.WINANSI);
        escapeString(b);
    }

    /**
     * Escapes a <code>byte</code> array according to the PDF conventions.
     *
     * @param b the <code>byte</code> array to escape.
     */
    private void escapeString(final byte b[]) throws IOException {
        OutputStream output = contentStream.getOutputStream();
        output.writeByte((byte) '(');
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
                    output.writeByte((byte) '\\').writeByte(c);
                    break;
                default:
                    output.writeByte(c);
            }
        }
        output.writeByte((byte) ')');
    }


    /**
     * Move the current point <i>(x, y)</i>, omitting any connecting line segment.
     *
     * @param x x coordinate.
     * @param y y coordinate.
     * @return current canvas.
     */
    public PdfCanvas moveTo(final float x, final float y) throws IOException {
        contentStream.getOutputStream()
                .writeFloat(x)
                .writeSpace()
                .writeFloat(y)
                .writeBytes(m);
        return this;
    }

    /**
     * Appends a straight line segment from the current point <i>(x, y)</i>. The new current
     * point is <i>(x, y)</i>.
     *
     * @param x x coordinate.
     * @param y y coordinate.
     * @return current canvas.
     */
    public PdfCanvas lineTo(final float x, final float y) throws IOException {
        contentStream.getOutputStream()
                .writeFloat(x)
                .writeSpace()
                .writeFloat(y)
                .writeBytes(l);
        return this;
    }

    /**
     * Appends a B&#xea;zier curve to the path, starting from the current point.
     *
     * @param x1 x coordinate of the first control point.
     * @param y1 y coordinate of the first control point.
     * @param x2 x coordinate of the second control point.
     * @param y2 y coordinate of the second control point.
     * @param x3 x coordinate of the ending point.
     * @param y3 y coordinate of the ending point.
     * @return current canvas.
     */
    public PdfCanvas curveTo(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3) throws IOException {
        contentStream.getOutputStream()
                .writeFloat(x1)
                .writeSpace()
                .writeFloat(y1)
                .writeSpace()
                .writeFloat(x2)
                .writeSpace()
                .writeFloat(y2)
                .writeSpace()
                .writeFloat(x3)
                .writeSpace()
                .writeFloat(y3)
                .writeBytes(c);
        return this;
    }

    /**
     * Appends a Bézier curve to the path, starting from the current point.
     *
     * @param x2 x coordinate of the second control point.
     * @param y2 y coordinate of the second control point.
     * @param x3 x coordinate of the ending point.
     * @param y3 y coordinate of the ending point.
     * @return current canvas.
     */
    public PdfCanvas curveTo(final float x2, final float y2, final float x3, final float y3) throws IOException {
        contentStream.getOutputStream()
                .writeFloat(x2)
                .writeSpace()
                .writeFloat(y2)
                .writeSpace()
                .writeFloat(x3)
                .writeSpace()
                .writeFloat(y3)
                .writeBytes(v);
        return this;
    }

    /**
     * Appends a Bézier curve to the path, starting from the current point.
     *
     * @param x1 x coordinate of the first control point.
     * @param y1 y coordinate of the first control point.
     * @param x3 x coordinate of the ending point.
     * @param y3 y coordinate of the ending point.
     * @return current canvas.
     */
    public PdfCanvas curveFromTo(final float x1, final float y1, final float x3, final float y3) throws IOException {
        contentStream.getOutputStream()
                .writeFloat(x1)
                .writeSpace()
                .writeFloat(y1)
                .writeSpace()
                .writeFloat(x3)
                .writeSpace()
                .writeFloat(y3)
                .writeBytes(y);
        return this;
    }


    /**
     * Draws a partial ellipse inscribed within the rectangle x1,y1,x2,y2,
     * starting at startAng degrees and covering extent degrees. Angles
     * start with 0 to the right (+x) and increase counter-clockwise.
     *
     * @param x1       a corner of the enclosing rectangle.
     * @param y1       a corner of the enclosing rectangle.
     * @param x2       a corner of the enclosing rectangle.
     * @param y2       a corner of the enclosing rectangle.
     * @param startAng starting angle in degrees.
     * @param extent   angle extent in degrees.
     * @return current canvas.
     */
    public PdfCanvas arc(final float x1, final float y1, final float x2, final float y2,
                         final float startAng, final float extent) throws IOException {
        ArrayList<float[]> ar = bezierArc(x1, y1, x2, y2, startAng, extent);
        if (ar.isEmpty())
            return this;
        float pt[] = ar.get(0);
        moveTo(pt[0], pt[1]);
        for (int k = 0; k < ar.size(); ++k) {
            pt = ar.get(k);
            curveTo(pt[2], pt[3], pt[4], pt[5], pt[6], pt[7]);
        }

        return this;
    }

    /**
     * Generates an array of bezier curves to draw an arc.
     * <p/>
     * (x1, y1) and (x2, y2) are the corners of the enclosing rectangle.
     * Angles, measured in degrees, start with 0 to the right (the positive X
     * axis) and increase counter-clockwise.  The arc extends from startAng
     * to startAng+extent.  i.e. startAng=0 and extent=180 yields an openside-down
     * semi-circle.
     * <p/>
     * The resulting coordinates are of the form float[]{x1,y1,x2,y2,x3,y3, x4,y4}
     * such that the curve goes from (x1, y1) to (x4, y4) with (x2, y2) and
     * (x3, y3) as their respective Bezier control points.
     * <p/>
     * Note: this code was taken from ReportLab (www.reportlab.org), an excellent
     * PDF generator for Python (BSD license: http://www.reportlab.org/devfaq.html#1.3 ).
     *
     * @param x1       a corner of the enclosing rectangle.
     * @param y1       a corner of the enclosing rectangle.
     * @param x2       a corner of the enclosing rectangle.
     * @param y2       a corner of the enclosing rectangle.
     * @param startAng starting angle in degrees.
     * @param extent   angle extent in degrees.
     * @return a list of float[] with the bezier curves.
     */
    public static ArrayList<float[]> bezierArc(float x1, float y1, float x2, float y2, final float startAng, final float extent) {
        float tmp;
        if (x1 > x2) {
            tmp = x1;
            x1 = x2;
            x2 = tmp;
        }
        if (y2 > y1) {
            tmp = y1;
            y1 = y2;
            y2 = tmp;
        }

        float fragAngle;
        int Nfrag;
        if (Math.abs(extent) <= 90f) {
            fragAngle = extent;
            Nfrag = 1;
        } else {
            Nfrag = (int) Math.ceil(Math.abs(extent) / 90f);
            fragAngle = extent / Nfrag;
        }
        float x_cen = (x1 + x2) / 2f;
        float y_cen = (y1 + y2) / 2f;
        float rx = (x2 - x1) / 2f;
        float ry = (y2 - y1) / 2f;
        float halfAng = (float) (fragAngle * Math.PI / 360.);
        float kappa = (float) Math.abs(4. / 3. * (1. - Math.cos(halfAng)) / Math.sin(halfAng));
        ArrayList<float[]> pointList = new ArrayList<float[]>();
        for (int i = 0; i < Nfrag; ++i) {
            float theta0 = (float) ((startAng + i * fragAngle) * Math.PI / 180.);
            float theta1 = (float) ((startAng + (i + 1) * fragAngle) * Math.PI / 180.);
            float cos0 = (float) Math.cos(theta0);
            float cos1 = (float) Math.cos(theta1);
            float sin0 = (float) Math.sin(theta0);
            float sin1 = (float) Math.sin(theta1);
            if (fragAngle > 0f) {
                pointList.add(new float[]{x_cen + rx * cos0,
                        y_cen - ry * sin0,
                        x_cen + rx * (cos0 - kappa * sin0),
                        y_cen - ry * (sin0 + kappa * cos0),
                        x_cen + rx * (cos1 + kappa * sin1),
                        y_cen - ry * (sin1 - kappa * cos1),
                        x_cen + rx * cos1,
                        y_cen - ry * sin1});
            } else {
                pointList.add(new float[]{x_cen + rx * cos0,
                        y_cen - ry * sin0,
                        x_cen + rx * (cos0 + kappa * sin0),
                        y_cen - ry * (sin0 - kappa * cos0),
                        x_cen + rx * (cos1 - kappa * sin1),
                        y_cen - ry * (sin1 + kappa * cos1),
                        x_cen + rx * cos1,
                        y_cen - ry * sin1});
            }
        }
        return pointList;
    }

    /**
     * Draws rectangle.
     *
     * @param x      x coordinate of the starting point.
     * @param y      y coordinate of the starting point.
     * @param width  width.
     * @param height height.
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
     * @param x      x coordinate of the starting point.
     * @param y      y coordinate of the starting point.
     * @param width  width.
     * @param height height.
     * @param radius radius of the arc corner.
     * @return current canvas.
     */
    public PdfCanvas roundRectangle(float x, float y, float width, float height, float radius) throws IOException {
        if (width < 0) {
            x += width;
            width = -width;
        }
        if (height < 0) {
            y += height;
            height = -height;
        }
        if (radius < 0)
            radius = -radius;
        float b = 0.4477f;
        moveTo(x + radius, y);
        lineTo(x + width - radius, y);
        curveTo(x + width - radius * b, y, x + width, y + radius * b, x + width, y + radius);
        lineTo(x + width, y + height - radius);
        curveTo(x + width, y + height - radius * b, x + width - radius * b, y + height, x + width - radius, y + height);
        lineTo(x + radius, y + height);
        curveTo(x + radius * b, y + height, x, y + height - radius * b, x, y + height - radius);
        lineTo(x, y + radius);
        curveTo(x, y + radius * b, x + radius * b, y, x + radius, y);
        return this;
    }

    /**
     * Draws a circle. The endpoint will (x+r, y).
     *
     * @param x x center of circle.
     * @param y y center of circle.
     * @param r radius of circle.
     * @return current canvas.
     */
    public PdfCanvas circle(final float x, final float y, final float r) throws IOException {
        float b = 0.5523f;
        moveTo(x + r, y);
        curveTo(x + r, y + r * b, x + r * b, y + r, x, y + r);
        curveTo(x - r * b, y + r, x - r, y + r * b, x - r, y);
        curveTo(x - r, y - r * b, x - r * b, y - r, x, y - r);
        curveTo(x + r * b, y - r, x + r, y - r * b, x + r, y);
        return this;
    }

    /**
     * Closes the current subpath by appending a straight line segment from the current point
     * to the starting point of the subpath.
     *
     * @return current canvas.
     */
    public PdfCanvas closePath() throws IOException {
        contentStream.getOutputStream().writeBytes(h);
        return this;
    }

    /**
     * Ends the path without filling or stroking it.
     *
     * @return current canvas.
     */
    public PdfCanvas newPath() throws IOException {
        contentStream.getOutputStream().writeBytes(n);
        return this;
    }

    /**
     * Strokes the path.
     *
     * @return current canvas.
     */
    public PdfCanvas stroke() throws IOException {
        contentStream.getOutputStream().writeBytes(S);
        return this;
    }

    /**
     * Closes the path and strokes it.
     *
     * @return current canvas.
     */
    public PdfCanvas closePathStroke() throws IOException {
        contentStream.getOutputStream().writeBytes(s);
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
     * @param xObj the XObject.
     * @param x    x coordinate.
     * @param y    y coordinate.
     * @return current canvas.
     */
    public PdfCanvas addXObject(PdfXObject xObj, float x, float y) {
        return this;
    }

    /**
     * Sets line width.
     *
     * @param lineWidth line width.
     * @return current canvas.
     */
    public PdfCanvas setLineWidth(float lineWidth) throws IOException {
        contentStream.getOutputStream()
                .writeFloat(lineWidth)
                .writeBytes(w);
        return this;
    }

    /**
     * Sets fill color.
     *
     * @param color fill color.
     * @return current canvas.
     */
    public PdfCanvas setFillColor(Color color) {
        return this;
    }

    /**
     * Sets stroke color.
     *
     * @param color stroke color.
     * @return current canvas.
     */
    public PdfCanvas setStrokeColor(Color color) {
        return this;
    }

    /**
     * Begins OCG layer.
     *
     * @param layer @see PdfLayer.
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
