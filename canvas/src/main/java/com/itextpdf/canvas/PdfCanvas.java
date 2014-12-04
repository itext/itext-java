package com.itextpdf.canvas;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.io.OutputStream;
import com.itextpdf.canvas.colors.Color;
import com.itextpdf.core.fonts.PdfEncodings;
import com.itextpdf.core.fonts.PdfFont;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.extgstate.PdfExtGState;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;

import java.util.ArrayList;
import java.util.Map;
import java.util.Stack;

/**
 * PdfCanvas class represents algorithm for writing data into content stream.
 * To write into page content create PdfCanvas from page instance.
 * To write into form XObject create PdfCanvas from form XObject instance.
 * Take care about calling PdfCanvas.release() after you finished writing to canvas. It will save some memory.
 */
public class PdfCanvas {

    static final private byte[] c = OutputStream.getIsoBytes("c\n");
    static final private byte[] l = OutputStream.getIsoBytes("l\n");
    static final private byte[] m = OutputStream.getIsoBytes("m\n");
    static final private byte[] v = OutputStream.getIsoBytes("v\n");
    static final private byte[] y = OutputStream.getIsoBytes("y\n");
    static final private byte[] q = OutputStream.getIsoBytes("q\n");
    static final private byte[] Q = OutputStream.getIsoBytes("Q\n");
    static final private byte[] f = OutputStream.getIsoBytes("f\n");
    static final private byte[] fStar = OutputStream.getIsoBytes("f*\n");
    static final private byte[] re = OutputStream.getIsoBytes("re\n");
    static final private byte[] BT = OutputStream.getIsoBytes("BT\n");
    static final private byte[] ET = OutputStream.getIsoBytes("ET\n");
    static final private byte[] Tf = OutputStream.getIsoBytes("Tf\n");
    static final private byte[] Tj = OutputStream.getIsoBytes("Tj\n");
    static final private byte[] Tm = OutputStream.getIsoBytes("Tm\n");
    static final private byte[] Td = OutputStream.getIsoBytes("Td\n");
    static final private byte[] Tr = OutputStream.getIsoBytes("Tr\n");
    static final private byte[] escR = OutputStream.getIsoBytes("\r");
    static final private byte[] escN = OutputStream.getIsoBytes("\n");
    static final private byte[] escT = OutputStream.getIsoBytes("\t");
    static final private byte[] escB = OutputStream.getIsoBytes("\b");
    static final private byte[] escF = OutputStream.getIsoBytes("\f");
    static final private byte[] w = OutputStream.getIsoBytes("w\n");
    static final private byte[] h = OutputStream.getIsoBytes("h\n");
    static final private byte[] n = OutputStream.getIsoBytes("n\n");
    static final private byte[] S = OutputStream.getIsoBytes("S\n");
    static final private byte[] s = OutputStream.getIsoBytes("s\n");
    static final private byte[] Do = OutputStream.getIsoBytes("Do\n");
    static final private byte[] cm = OutputStream.getIsoBytes("cm\n");
    static final private byte[] gs = OutputStream.getIsoBytes("gs\n");
    static final private byte[] BI = OutputStream.getIsoBytes("BI\n");
    static final private byte[] ID = OutputStream.getIsoBytes("ID\n");
    static final private byte[] EI = OutputStream.getIsoBytes("EI\n");
    static final private byte[] BMC = OutputStream.getIsoBytes("BMC\n");
    static final private byte[] BDC = OutputStream.getIsoBytes("BDC\n");
    static final private byte[] EMC = OutputStream.getIsoBytes("EMC\n");

    protected Stack<PdfGraphicsState> gsStack = new Stack<PdfGraphicsState>();
    protected PdfGraphicsState currentGs = new PdfGraphicsState();
    protected PdfStream contentStream;
    protected PdfResources resources;
    protected PdfDocument document;
    protected int mcDepth;

    /**
     * Creates PdfCanvas from content stream of page, form XObject, pattern etc.
     *
     * @param contentStream @see PdfStream.
     */
    public PdfCanvas(PdfStream contentStream, PdfResources resources) {
        this.contentStream = contentStream;
        this.resources = resources;
        document = contentStream.getDocument();
    }

    /**
     * Convenience method for fast PdfCanvas creation by a certain page.
     *
     * @param page page to create canvas from.
     */
    public PdfCanvas(PdfPage page) throws PdfException {
        this(page.getContentStream(page.getContentStreamCount() - 1), page.getResources());
    }

    public PdfCanvas(PdfFormXObject xObj) throws PdfException {
        this(xObj.getPdfObject(), xObj.getResources());
    }

    /**
     * Convenience method for fast PdfCanvas creation by a certain page.
     *
     * @param doc     @see PdfDocument.
     * @param pageNum page number.
     */
    public PdfCanvas(PdfDocument doc, int pageNum) throws PdfException {
        this(doc.getPage(pageNum));
    }

    public PdfResources getResources() {
        return resources;
    }

    /**
     * Attaches new content stream to the canvas.
     * This method is supposed to be used when you want to write in different PdfStream keeping context (gsStack, currentGs, ...) the same.
     *
     * @param contentStream a content stream to attach.
     */
    public void attachContentStream(PdfStream contentStream) {
        this.contentStream = contentStream;
    }

    /**
     * Releases the canvas.
     * Use this method after you finished working with canvas.
     */
    public void release() {
        gsStack = null;
        currentGs = null;
        contentStream = null;
        resources = null;
        document = null;
    }

    /**
     * Saves graphics state.
     *
     * @return current canvas.
     */
    public PdfCanvas saveState() throws PdfException {
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
    public PdfCanvas restoreState() throws PdfException {
        currentGs = gsStack.pop();
        contentStream.getOutputStream().writeBytes(Q);
        return this;
    }

    public PdfCanvas concatMatrix(float a, float b, float c, float d, float e, float f) throws PdfException {
        contentStream.getOutputStream().writeFloat(a).writeSpace().
                writeFloat(b).writeSpace().
                writeFloat(c).writeSpace().
                writeFloat(d).writeSpace().
                writeFloat(e).writeSpace().
                writeFloat(f).writeSpace().writeBytes(cm);
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
    public PdfCanvas beginText() throws PdfException {
        contentStream.getOutputStream().writeBytes(BT);
        return this;
    }

    /**
     * Ends text block (PDF ET operator).
     *
     * @return current canvas.
     */
    public PdfCanvas endText() throws PdfException {
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
    public PdfCanvas setFontAndSize(PdfFont font, float size) throws PdfException {
        if (size < 0.0001f && size > -0.0001f)
            throw new PdfException(PdfException.FontSizeTooSmall, size);
        currentGs.fontSize = size;
        PdfName fontName = resources.addFont(font);
        currentGs.font = font;
        contentStream.getOutputStream()
                .write(fontName)
                .writeSpace()
                .writeFloat(size).writeSpace()
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
    public PdfCanvas moveText(float x, float y) throws PdfException {
        contentStream.getOutputStream()
                .writeFloat(x)
                .writeSpace()
                .writeFloat(y).writeSpace()
                .writeBytes(Td);
        return this;
    }

    /**
     * Sets text rendering mode.
     *
     * @param textRenderingMode text rendering mode @see PdfCanvasConstants.
     * @return current canvas.
     */
    public PdfCanvas setTextRenderingMode(int textRenderingMode) throws PdfException {
        currentGs.textRenderingMode = textRenderingMode;
        contentStream.getOutputStream()
                .writeInteger(textRenderingMode).writeSpace()
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
    public PdfCanvas setTextMatrix(float a, float b, float c, float d, float x, float y) throws PdfException {
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
                .writeFloat(y).writeSpace()
                .writeBytes(Tm);
        return this;
    }

    /**
     * Shows text (operator Tj).
     *
     * @param text text to show.
     * @return current canvas.
     */
    public PdfCanvas showText(String text) throws PdfException {
        showText2(text);
        contentStream.getOutputStream().writeBytes(Tj);
        return this;
    }

    /**
     * Move the current point <i>(x, y)</i>, omitting any connecting line segment.
     *
     * @param x x coordinate.
     * @param y y coordinate.
     * @return current canvas.
     */
    public PdfCanvas moveTo(final float x, final float y) throws PdfException {
        contentStream.getOutputStream()
                .writeFloat(x)
                .writeSpace()
                .writeFloat(y).writeSpace()
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
    public PdfCanvas lineTo(final float x, final float y) throws PdfException {
        contentStream.getOutputStream()
                .writeFloat(x)
                .writeSpace()
                .writeFloat(y).writeSpace()
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
    public PdfCanvas curveTo(final float x1, final float y1, final float x2, final float y2, final float x3, final float y3) throws PdfException {
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
                .writeSpace()
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
    public PdfCanvas curveTo(final float x2, final float y2, final float x3, final float y3) throws PdfException {
        contentStream.getOutputStream()
                .writeFloat(x2)
                .writeSpace()
                .writeFloat(y2)
                .writeSpace()
                .writeFloat(x3)
                .writeSpace()
                .writeFloat(y3).writeSpace()
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
    public PdfCanvas curveFromTo(final float x1, final float y1, final float x3, final float y3) throws PdfException {
        contentStream.getOutputStream()
                .writeFloat(x1)
                .writeSpace()
                .writeFloat(y1)
                .writeSpace()
                .writeFloat(x3)
                .writeSpace()
                .writeFloat(y3).writeSpace()
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
                         final float startAng, final float extent) throws PdfException {
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
    public PdfCanvas rectangle(float x, float y, float width, float height) throws PdfException {
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
    public PdfCanvas roundRectangle(float x, float y, float width, float height, float radius) throws PdfException {
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
    public PdfCanvas circle(final float x, final float y, final float r) throws PdfException {
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
    public PdfCanvas closePath() throws PdfException {
        contentStream.getOutputStream().writeBytes(h);
        return this;
    }

    /**
     * Ends the path without filling or stroking it.
     *
     * @return current canvas.
     */
    public PdfCanvas newPath() throws PdfException {
        contentStream.getOutputStream().writeBytes(n);
        return this;
    }

    /**
     * Strokes the path.
     *
     * @return current canvas.
     */
    public PdfCanvas stroke() throws PdfException {
        contentStream.getOutputStream().writeBytes(S);
        return this;
    }

    /**
     * Closes the path and strokes it.
     *
     * @return current canvas.
     */
    public PdfCanvas closePathStroke() throws PdfException {
        contentStream.getOutputStream().writeBytes(s);
        return this;
    }

    /**
     * Fills current path.
     *
     * @return current canvas.
     */
    public PdfCanvas fill() throws PdfException {
        contentStream.getOutputStream().writeBytes(f);
        return this;
    }

    /**
     * EOFills current path.
     *
     * @return current canvas.
     */
    public PdfCanvas eoFill() throws PdfException {
        contentStream.getOutputStream().writeBytes(fStar);
        return this;
    }

    /**
     * Sets line width.
     *
     * @param lineWidth line width.
     * @return current canvas.
     */
    public PdfCanvas setLineWidth(float lineWidth) throws PdfException {
        contentStream.getOutputStream()
                .writeFloat(lineWidth).writeSpace()
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

    /**
     * Adds Image XObject to canvas.
     *
     * @param image
     * @param a
     * @param b
     * @param c
     * @param d
     * @param e
     * @param f
     * @return canvas
     * @throws PdfException
     */
    public PdfCanvas addImage(PdfImageXObject image, float a, float b, float c, float d, float e, float f) throws PdfException {
        saveState();
        concatMatrix(a, b, c, d, e, f);
        PdfName name = resources.addImage(image);
        contentStream.getOutputStream().write(name).writeSpace().writeBytes(Do);
        restoreState();
        return this;
    }


    /**
     * Creates Image XObject from image and adds it to canvas.
     *
     * @param image
     * @param a
     * @param b
     * @param c
     * @param d
     * @param e
     * @param f
     * @param asInline true if to add image as in-line.
     * @return created Image XObject or null in case of in-line image (asInline = true).
     * @throws PdfException
     */
    public PdfImageXObject addImage(Image image, float a, float b, float c, float d, float e, float f, boolean asInline) throws PdfException {
        if (asInline) {
            PdfImageXObject imageXObject = new PdfImageXObject(null, image);
            saveState();
            concatMatrix(a, b, c, d, e, f);
            PdfOutputStream os = contentStream.getOutputStream();
            os.writeBytes(BI);
            for (Map.Entry<PdfName, PdfObject> entry : imageXObject.getPdfObject().entrySet()) {
                PdfName key = entry.getKey();
                if (PdfName.Type.equals(key) || PdfName.Subtype.equals(key) || PdfName.Length.equals(key)) {

                } else {
                    os.write(entry.getKey()).writeSpace();
                    os.write(entry.getValue()).writeNewLine();
                }
            }
            os.writeBytes(ID);
            os.writeBytes(imageXObject.getPdfObject().getBytes()).writeNewLine().writeBytes(EI).writeNewLine();
            restoreState();
            return null;
        } else {
            PdfImageXObject imageXObject = new PdfImageXObject(document, image);
            addImage(imageXObject, a, b, c, d, e, f);
            return imageXObject;
        }
    }

    /**
     * Adds Image XObject to specified rectangle on canvas.
     *
     * @param image
     * @param rect
     * @return
     * @throws PdfException
     */
    public PdfCanvas addImage(PdfImageXObject image, Rectangle rect) throws PdfException {
        return addImage(image, rect.getWidth(), 0, 0, rect.getHeight(), rect.getX(), rect.getY());
    }

    /**
     * Creates Image XObject from image and adds it to canvas.
     *
     * @param image
     * @param rect
     * @param asInline true if to add image as in-line.
     * @return created Image XObject or null in case of in-line image (asInline = true).
     * @throws PdfException
     */
    public PdfImageXObject addImage(Image image, Rectangle rect, boolean asInline) throws PdfException {
        return addImage(image, rect.getWidth(), 0, 0, rect.getHeight(), rect.getX(), rect.getY(), asInline);
    }

    /**
     * Adds image to the specified position.
     *
     * @param image
     * @param x
     * @param y
     * @return
     * @throws PdfException
     */
    public PdfCanvas addImage(PdfImageXObject image, float x, float y) throws PdfException {
        return addImage(image, image.getWidth(), 0, 0, image.getHeight(), x, y);
    }

    /**
     * Creates Image XObject from image and adds it to canvas.
     *
     * @param image
     * @param x
     * @param y
     * @param asInline true if to add image as in-line.
     * @return created Image XObject or null in case of in-line image (asInline = true).
     * @throws PdfException
     */
    public PdfImageXObject addImage(Image image, float x, float y, boolean asInline) throws PdfException {
        return addImage(image, image.getWidth(), 0, 0, image.getHeight(), x, y, asInline);
    }

    /**
     * Adds Image XObject to the specified position with specified width preserving aspect ratio.
     *
     * @param image
     * @param x
     * @param y
     * @param width
     * @return
     * @throws PdfException
     */
    public PdfCanvas addImage(PdfImageXObject image, float x, float y, float width) throws PdfException {
        return addImage(image, width, 0, 0, width / image.getWidth() * image.getHeight(), x, y);
    }

    /**
     * Creates Image XObject from image and adds it to the specified position with specified width preserving aspect ratio.
     *
     * @param image
     * @param x
     * @param y
     * @param width
     * @param asInline true if to add image as in-line.
     * @return created Image XObject or null in case of in-line image (asInline = true).
     * @throws PdfException
     */
    public PdfImageXObject addImage(Image image, float x, float y, float width, boolean asInline) throws PdfException {
        return addImage(image, width, 0, 0, width / image.getWidth() * image.getHeight(), x, y, asInline);
    }

    /**
     * Adds Image XObject to the specified position with specified height preserving aspect ratio.
     *
     * @param image
     * @param x
     * @param y
     * @param height
     * @param dummy
     * @return
     * @throws PdfException
     */
    public PdfCanvas addImage(PdfImageXObject image, float x, float y, float height, boolean dummy) throws PdfException {
        return addImage(image, height / image.getHeight() * image.getWidth(), 0, 0, height, x, y);
    }

    /**
     * Creates Image XObject from image and adds it to the specified position with specified width preserving aspect ratio.
     *
     * @param image
     * @param x
     * @param y
     * @param height
     * @param asInline true if to add image as in-line.
     * @param dummy
     * @return created Image XObject or null in case of in-line image (asInline = true).
     * @throws PdfException
     */
    public PdfImageXObject addImage(Image image, float x, float y, float height, boolean asInline, boolean dummy) throws PdfException {
        return addImage(image, height / image.getHeight() * image.getWidth(), 0, 0, height, x, y, asInline);
    }

    public PdfCanvas addForm(PdfFormXObject form, float a, float b, float c, float d, float e, float f) throws PdfException {
        saveState();
        concatMatrix(a, b, c, d, e, f);
        PdfName name = resources.addForm(form);
        contentStream.getOutputStream().write(name).writeSpace().writeBytes(Do);
        restoreState();
        return this;
    }

    public PdfCanvas addForm(PdfFormXObject form, float x, float y) throws PdfException {
        return addForm(form, 1, 0, 0, 1, x, y);
    }

    public PdfCanvas setExtGState(PdfExtGState extGState) throws PdfException {
        if (!extGState.isFlushed())
            currentGs.updateFromExtGState(extGState);
        PdfName name = resources.addExtGState(extGState);
        contentStream.getOutputStream().write(name).writeSpace().writeBytes(gs);
        return this;
    }

    public PdfExtGState setExtGState(PdfDictionary extGState) throws PdfException {
        PdfExtGState egs = new PdfExtGState(extGState, document);
        setExtGState(egs);
        return egs;
    }

    public PdfCanvas beginMarkedContent(PdfName tag) throws PdfException {
        return beginMarkedContent(tag, null);
    }

    public PdfCanvas beginMarkedContent(PdfName tag, PdfDictionary properties) throws PdfException {
        mcDepth++;
        PdfOutputStream out = contentStream.getOutputStream().write(tag).writeSpace();
        if (properties == null) {
            out.writeBytes(BMC);
        } else {
            PdfObject objectToWrite = properties.getIndirectReference() == null ? properties : resources.addProperties(properties);
            out.write(objectToWrite).writeSpace().writeBytes(BDC);
        }
        return this;
    }

    public PdfCanvas endMarkedContent() throws PdfException {
        if (--mcDepth < 0)
            throw new PdfException(PdfException.UnbalancedBeginEndMarkedContentOperators);
        contentStream.getOutputStream().writeBytes(EMC);
        return this;
    }


    /**
     * A helper to insert into the content stream the <code>text</code>
     * converted to bytes according to the font's encoding.
     *
     * @param text the text to write.
     */
    private void showText2(final String text) throws PdfException {
        if (currentGs.font == null)
            throw new PdfException(PdfException.FontAndSizeMustBeSetBeforeWritingAnyText, currentGs);
        byte b[] = PdfEncodings.convertToBytes(text, PdfEncodings.WINANSI);
        escapeString(b);
    }

    /**
     * Escapes a <code>byte</code> array according to the PDF conventions.
     *
     * @param b the <code>byte</code> array to escape.
     */
    private void escapeString(final byte b[]) throws PdfException {
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

}
