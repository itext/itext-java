package com.itextpdf.canvas;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.Utilities;
import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.io.OutputStream;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.canvas.color.PatternColor;
import com.itextpdf.canvas.image.WmfImageHelper;
import com.itextpdf.core.font.PdfFont;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.colorspace.*;
import com.itextpdf.core.pdf.extgstate.PdfExtGState;
import com.itextpdf.core.pdf.layer.PdfLayer;
import com.itextpdf.core.pdf.layer.PdfLayerMembership;
import com.itextpdf.core.pdf.layer.PdfOCG;
import com.itextpdf.core.pdf.tagging.IPdfTag;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;
import com.itextpdf.core.pdf.xobject.PdfImageXObject;
import com.itextpdf.core.pdf.xobject.PdfXObject;

import java.util.ArrayList;
import java.util.HashMap;
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
    static final private byte[] Tc = OutputStream.getIsoBytes("Tc\n");
    static final private byte[] Tr = OutputStream.getIsoBytes("Tr\n");
    static final private byte[] Ts = OutputStream.getIsoBytes("Ts\n");
    static final private byte[] Tw = OutputStream.getIsoBytes("Tw\n");
    static final private byte[] w = OutputStream.getIsoBytes("w\n");
    static final private byte[] W = OutputStream.getIsoBytes("W\n");
    static final private byte[] WStar = OutputStream.getIsoBytes("W*\n");
    static final private byte[] J = OutputStream.getIsoBytes("J\n");
    static final private byte[] j = OutputStream.getIsoBytes("j\n");
    static final private byte[] M = OutputStream.getIsoBytes("M\n");
    static final private byte[] d = OutputStream.getIsoBytes("d\n");
    static final private byte[] b = OutputStream.getIsoBytes("b\n");
    static final private byte[] bStar = OutputStream.getIsoBytes("b*\n");
    static final private byte[] B = OutputStream.getIsoBytes("B\n");
    static final private byte[] BStar = OutputStream.getIsoBytes("B*\n");
    static final private byte[] ri = OutputStream.getIsoBytes("ri\n");
    static final private byte[] i = OutputStream.getIsoBytes("i\n");
    static final private byte[] h = OutputStream.getIsoBytes("h\n");
    static final private byte[] n = OutputStream.getIsoBytes("n\n");
    static final private byte[] S = OutputStream.getIsoBytes("S\n");
    static final private byte[] s = OutputStream.getIsoBytes("s\n");
    static final private byte[] sh = OutputStream.getIsoBytes("sh\n");
    static final private byte[] Do = OutputStream.getIsoBytes("Do\n");
    static final private byte[] cm = OutputStream.getIsoBytes("cm\n");
    static final private byte[] gs = OutputStream.getIsoBytes("gs\n");
    static final private byte[] BI = OutputStream.getIsoBytes("BI\n");
    static final private byte[] ID = OutputStream.getIsoBytes("ID\n");
    static final private byte[] EI = OutputStream.getIsoBytes("EI\n");
    static final private byte[] BMC = OutputStream.getIsoBytes("BMC\n");
    static final private byte[] BDC = OutputStream.getIsoBytes("BDC\n");
    static final private byte[] EMC = OutputStream.getIsoBytes("EMC\n");
    static final private byte[] g = OutputStream.getIsoBytes("g\n");
    static final private byte[] G = OutputStream.getIsoBytes("G\n");
    static final private byte[] k = OutputStream.getIsoBytes("k\n");
    static final private byte[] K = OutputStream.getIsoBytes("K\n");
    static final private byte[] rg = OutputStream.getIsoBytes("rg\n");
    static final private byte[] RG = OutputStream.getIsoBytes("RG\n");
    static final private byte[] cs = OutputStream.getIsoBytes("cs\n");
    static final private byte[] CS = OutputStream.getIsoBytes("CS\n");
    static final private byte[] scn = OutputStream.getIsoBytes("scn\n");
    static final private byte[] SCN = OutputStream.getIsoBytes("SCN\n");
    static final private byte[] TL = OutputStream.getIsoBytes("TL\n");
    static final private byte[] TD = OutputStream.getIsoBytes("TD\n");
    static final private byte[] Tz = OutputStream.getIsoBytes("Tz\n");
    static final private byte[] TStar = OutputStream.getIsoBytes("T*\n");

    static private final PdfDeviceCs.Gray gray = new PdfDeviceCs.Gray();
    static private final PdfDeviceCs.Rgb rgb = new PdfDeviceCs.Rgb();
    static private final PdfDeviceCs.Cmyk cmyk = new PdfDeviceCs.Cmyk();
    static private final PdfSpecialCs.Pattern pattern = new PdfSpecialCs.Pattern();

    protected Stack<PdfGraphicsState> gsStack = new Stack<PdfGraphicsState>();
    protected PdfGraphicsState currentGs = new PdfGraphicsState();
    protected PdfStream contentStream;
    protected PdfResources resources;
    protected PdfDocument document;
    protected int mcDepth;
    protected int mcid = 0;
    protected ArrayList<Integer> layerDepth;

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
        this(getPageStream(page), page.getResources());
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

    public PdfGraphicsState getGraphicsState() {
        return currentGs;
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
        currentGs.setFontSize(size);
        PdfName fontName = resources.addFont(font);
        currentGs.setFont(font);
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
     * Sets the text leading parameter.
     * <P>
     * The leading parameter is measured in text space units. It specifies the vertical distance
     * between the baselines of adjacent lines of text.</P>
     *
     * @param leading the new leading.
     * @return current canvas.
     */
    public PdfCanvas setLeading(final float leading) throws PdfException {
        currentGs.setLeading(leading);
        contentStream.getOutputStream()
                .writeFloat(leading)
                .writeSpace()
                .writeBytes(TL);

        return this;
    }

    /**
     * Moves to the start of the next line, offset from the start of the current line.
     * <P>
     * As a side effect, this sets the leading parameter in the text state.</P>
     *
     * @param x offset of the new current point
     * @param y y-coordinate of the new current point
     * @return current canvas.
     */
    public PdfCanvas moveTextWithLeading(final float x, final float y) throws PdfException {
        currentGs.setLeading(-y);
        contentStream.getOutputStream()
                .writeFloat(x)
                .writeSpace()
                .writeFloat(y)
                .writeSpace()
                .writeBytes(TD);
        return this;
    }

    /**
     * Moves to the start of the next line.
     * @return current canvas.
     */
    public PdfCanvas newlineText() throws PdfException {
        contentStream.getOutputStream()
                .writeBytes(TStar);
        return this;
    }

    /**
     * Moves to the next line and shows {@code text}.
     *
     * @param text the text to write
     * @return current canvas.
     */
    public PdfCanvas newlineShowText(final String text) throws PdfException {
        showText2(text);
        contentStream.getOutputStream()
                .writeByte((byte) '\'')
                .writeNewLine();
        return this;
    }

    /**
     * Moves to the next line and shows text string, using the given values of the character and word spacing parameters.
     *
     * @param       wordSpacing     a parameter
     * @param       charSpacing     a parameter
     * @param text the text to write
     * @return current canvas.
     */
    public PdfCanvas newlineShowText(final float wordSpacing, final float charSpacing, final String text) throws PdfException {
        contentStream.getOutputStream()
                .writeFloat(wordSpacing)
                .writeSpace()
                .writeFloat(charSpacing);
        showText2(text);
        contentStream.getOutputStream()
                .writeByte((byte) '"')
                .writeNewLine();
        // The " operator sets charSpace and wordSpace into graphics state
        // (cfr PDF reference v1.6, table 5.6)
        currentGs.setCharSpacing(charSpacing);
        currentGs.setWordSpacing(wordSpacing);
        return this;
    }

    /**
     * Sets text rendering mode.
     *
     * @param textRenderingMode text rendering mode @see PdfCanvasConstants.
     * @return current canvas.
     */
    public PdfCanvas setTextRenderingMode(int textRenderingMode) throws PdfException {
        currentGs.setTextRenderingMode(textRenderingMode);
        contentStream.getOutputStream()
                .writeInteger(textRenderingMode).writeSpace()
                .writeBytes(Tr);
        return this;
    }

    /**
     * Sets the text rise parameter.
     * <p/>
     * This allows to write text in subscript or superscript mode.</P>
     *
     * @param textRise a parameter
     * @return current canvas.
     */
    public PdfCanvas setTextRise(final float textRise) throws PdfException {
        currentGs.setTextRise(textRise);
        contentStream.getOutputStream()
                .writeFloat(textRise).writeSpace()
                .writeBytes(Ts);
        return this;
    }

    /**
     * Sets the word spacing parameter.
     *
     * @param wordSpacing a parameter
     * @return current canvas.
     */
    public PdfCanvas setWordSpacing(final float wordSpacing) throws PdfException {
        currentGs.setWordSpacing(wordSpacing);
        contentStream.getOutputStream()
                .writeFloat(wordSpacing).writeSpace()
                .writeBytes(Tw);
        return this;
    }

    /**
     * Sets the character spacing parameter.
     *
     * @param charSpacing a parameter
     * @return current canvas.
     */
    public PdfCanvas setCharacterSpacing(final float charSpacing) throws PdfException {
        currentGs.setCharSpacing(charSpacing);
        contentStream.getOutputStream()
                .writeFloat(charSpacing).writeSpace()
                .writeBytes(Tc);
        return this;
    }

    /**
     * Sets the horizontal scaling parameter.
     *
     * @param scale a parameter.
     * @return current canvas.
     */
    public PdfCanvas setHorizontalScaling(float scale) throws PdfException {
        currentGs.setHorizontalScaling(scale);
        contentStream.getOutputStream()
                .writeFloat(scale)
                .writeSpace()
                .writeBytes(Tz);
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
     * Changes the text matrix.
     *
     * @param x operand 3,1 in the matrix.
     * @param y operand 3,2 in the matrix.
     * @return current canvas.
     */
    public PdfCanvas setTextMatrix(float x, float y) throws PdfException {
        return setTextMatrix(1, 0, 0, 1, x, y);
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
     * Draws an ellipse inscribed within the rectangle x1,y1,x2,y2.
     *
     * @param x1 a corner of the enclosing rectangle
     * @param y1 a corner of the enclosing rectangle
     * @param x2 a corner of the enclosing rectangle
     * @param y2 a corner of the enclosing rectangle
     */
    public PdfCanvas ellipse(final float x1, final float y1, final float x2, final float y2) throws PdfException {
        return arc(x1, y1, x2, y2, 0f, 360f);
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
     * Draws a rectangle.
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
     * Draws a rectangle.
     * @param rectangle a rectangle to be drawn
     * @return current canvas.
     */
    public PdfCanvas rectangle(Rectangle rectangle) throws PdfException {
        return rectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
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

    public PdfCanvas paintShading(PdfShading shading) throws PdfException {
        PdfName shadingName = resources.addShading(shading);
        contentStream.getOutputStream().write(shadingName).writeSpace().writeBytes(sh);
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
     * Closes the path, fills it using the even-odd rule to determine the region to fill and strokes it.
     *
     * @return current canvas.
     */
    public PdfCanvas closePathEoFillStroke() throws PdfException {
        contentStream.getOutputStream().writeBytes(bStar);
        return this;
    }

    /**
     * Closes the path, fills it using the non-zero winding number rule to determine the region to fill and strokes it.
     *
     * @return current canvas.
     */
    public PdfCanvas closePathFillStroke() throws PdfException {
        contentStream.getOutputStream().writeBytes(b);
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

    public PdfCanvas clip() throws PdfException {
        contentStream.getOutputStream().writeBytes(W);
        return this;
    }

    /**
     * Modify the current clipping path by intersecting it with the current path, using the
     * even-odd rule to determine which regions lie inside the clipping path.
     *
     * @return current canvas.
     */
    public PdfCanvas eoClip() throws PdfException {
        contentStream.getOutputStream().writeBytes(WStar);
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
     * Fills the path using the non-zero winding number rule to determine the region to fill and strokes it.
     *
     * @return current canvas.
     */
    public PdfCanvas fillStroke() throws PdfException {
        contentStream.getOutputStream().writeBytes(B);
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
     * Fills the path, using the even-odd rule to determine the region to fill and strokes it.
     *
     * @return current canvas.
     */
    public PdfCanvas eoFillStroke() throws PdfException {
        contentStream.getOutputStream().writeBytes(BStar);
        return this;
    }

    /**
     * Sets line width.
     *
     * @param lineWidth line width.
     * @return current canvas.
     */
    public PdfCanvas setLineWidth(float lineWidth) throws PdfException {
        if (floatsAreEqual(currentGs.getLineWidth(), lineWidth))
            return this;
        currentGs.setLineWidth(lineWidth);
        contentStream.getOutputStream()
                .writeFloat(lineWidth).writeSpace()
                .writeBytes(w);
        return this;
    }

    public PdfCanvas setLineCapStyle(int lineCapStyle) throws PdfException {
        if (integersAreEqual(currentGs.getLineCapStyle(), lineCapStyle))
            return this;
        currentGs.setLineCapStyle(lineCapStyle);
        contentStream.getOutputStream()
                .writeInteger(lineCapStyle).writeSpace()
                .writeBytes(J);
        return this;
    }

    public PdfCanvas setLineJoinStyle(int lineJoinStyle) throws PdfException {
        if (integersAreEqual(currentGs.getLineJoinStyle(), lineJoinStyle))
            return this;
        currentGs.setLineJoinStyle(lineJoinStyle);
        contentStream.getOutputStream()
                .writeInteger(lineJoinStyle).writeSpace()
                .writeBytes(j);
        return this;
    }

    public PdfCanvas setMiterLimit(float miterLimit) throws PdfException {
        if (floatsAreEqual(currentGs.getMiterLimit(), miterLimit))
            return this;
        currentGs.setMiterLimit(miterLimit);
        contentStream.getOutputStream()
                .writeFloat(miterLimit).writeSpace()
                .writeBytes(M);
        return this;
    }

    /**
     * Changes the value of the <VAR>line dash pattern</VAR>.
     * <p/>
     * The line dash pattern controls the pattern of dashes and gaps used to stroke paths.
     * It is specified by an <I>array</I> and a <I>phase</I>. The array specifies the length
     * of the alternating dashes and gaps. The phase specifies the distance into the dash
     * pattern to start the dash.
     *
     * @param phase the value of the phase
     */
    public PdfCanvas setLineDash(final float phase) throws PdfException {
        contentStream.getOutputStream().writeByte((byte) '[').writeByte((byte) ']').writeSpace()
                .writeFloat(phase).writeSpace()
                .writeBytes(d);
        return this;
    }

    /**
     * Changes the value of the <VAR>line dash pattern</VAR>.
     * <p/>
     * The line dash pattern controls the pattern of dashes and gaps used to stroke paths.
     * It is specified by an <I>array</I> and a <I>phase</I>. The array specifies the length
     * of the alternating dashes and gaps. The phase specifies the distance into the dash
     * pattern to start the dash.
     *
     * @param phase   the value of the phase
     * @param unitsOn the number of units that must be 'on' (equals the number of units that must be 'off').
     */
    public void setLineDash(final float unitsOn, final float phase) throws PdfException {
        contentStream.getOutputStream().writeByte((byte) '[').writeFloat(unitsOn).writeByte((byte) ']').writeSpace()
                .writeFloat(phase).writeSpace()
                .writeBytes(d);
    }

    /**
     * Changes the value of the <VAR>line dash pattern</VAR>.
     * <p/>
     * The line dash pattern controls the pattern of dashes and gaps used to stroke paths.
     * It is specified by an <I>array</I> and a <I>phase</I>. The array specifies the length
     * of the alternating dashes and gaps. The phase specifies the distance into the dash
     * pattern to start the dash.
     *
     * @param phase    the value of the phase
     * @param unitsOn  the number of units that must be 'on'
     * @param unitsOff the number of units that must be 'off'
     */
    public void setLineDash(final float unitsOn, final float unitsOff, final float phase) throws PdfException {
        contentStream.getOutputStream().writeByte((byte) '[').writeFloat(unitsOn).writeSpace()
                .writeFloat(unitsOff).writeByte((byte) ']').writeSpace()
                .writeFloat(phase).writeSpace()
                .writeBytes(d);
    }

    /**
     * Changes the value of the <VAR>line dash pattern</VAR>.
     * <p/>
     * The line dash pattern controls the pattern of dashes and gaps used to stroke paths.
     * It is specified by an <I>array</I> and a <I>phase</I>. The array specifies the length
     * of the alternating dashes and gaps. The phase specifies the distance into the dash
     * pattern to start the dash.
     *
     * @param array length of the alternating dashes and gaps
     * @param phase the value of the phase
     */
    public final void setLineDash(final float[] array, final float phase) throws PdfException {
        PdfOutputStream out = contentStream.getOutputStream();
        out.writeByte((byte) '[');
        for (int i = 0; i < array.length; i++) {
            out.writeFloat(array[i]);
            if (i < array.length - 1)
                out.writeSpace();
        }
        out.writeByte((byte) ']').writeSpace().writeFloat(phase).writeSpace().writeBytes(d);
    }

    public PdfCanvas setRenderingIntent(PdfName renderingIntent) throws PdfException {
        if (renderingIntent.equals(currentGs.getRenderingIntent()))
            return this;
        currentGs.setRenderingIntent(renderingIntent);
        contentStream.getOutputStream()
                .write(renderingIntent).writeSpace()
                .writeBytes(ri);
        return this;
    }

    /**
     * Changes the <VAR>Flatness</VAR>.
     * <p/>
     * <VAR>Flatness</VAR> sets the maximum permitted distance in device pixels between the
     * mathematically correct path and an approximation constructed from straight line segments.<BR>
     *
     * @param flatnessTolerance a value
     * @return current canvas.
     */
    public PdfCanvas setFlatnessTolerance(float flatnessTolerance) throws PdfException {
        if (floatsAreEqual(currentGs.getFlatnessTolerance(), flatnessTolerance))
            return this;
        currentGs.setFlatnessTolerance(flatnessTolerance);
        contentStream.getOutputStream()
                .writeFloat(flatnessTolerance).writeSpace()
                .writeBytes(i);
        return this;
    }

    /**
     * Sets fill color.
     *
     * @param color fill color.
     * @return current canvas.
     */
    public PdfCanvas setFillColor(Color color) throws PdfException {
        return setColor(color, true);
    }

    /**
     * Sets stroke color.
     *
     * @param color stroke color.
     * @return current canvas.
     */
    public PdfCanvas setStrokeColor(Color color) throws PdfException {
        return setColor(color, false);
    }

    public PdfCanvas setColor(Color color, boolean fill) throws PdfException {
        if (color instanceof PatternColor) {
            return setColor(color.getColorSpace(), color.getColorValue(), ((PatternColor) color).getPattern(), fill);
        } else {
            return setColor(color.getColorSpace(), color.getColorValue(), fill);
        }
    }

    public PdfCanvas setColor(PdfColorSpace colorSpace, float[] colorValue, boolean fill) throws PdfException {
        return setColor(colorSpace, colorValue, null, fill);
    }

    public PdfCanvas setColor(PdfColorSpace colorSpace, float[] colorValue, PdfPattern pattern, boolean fill) throws PdfException {
        boolean setColorValueOnly = false;
        Color c = fill ? currentGs.getFillColor() : currentGs.getStrokeColor();
        Color newColor = createColor(colorSpace, colorValue, pattern);
        if (c.equals(newColor))
            return this;
        else if (c.getColorSpace().equals(colorSpace)) {
            c.setColorValue(colorValue);
            if (c instanceof PatternColor) {
                ((PatternColor)c).setPattern(pattern);
            }
            setColorValueOnly = true;
        } else {
            if (fill)
                currentGs.setFillColor(newColor);
            else
                currentGs.setStrokeColor(newColor);
        }
        if (colorSpace instanceof PdfDeviceCs.Gray)
            contentStream.getOutputStream().writeFloats(colorValue).writeSpace().writeBytes(fill ? g : G);
        else if (colorSpace instanceof PdfDeviceCs.Rgb)
            contentStream.getOutputStream().writeFloats(colorValue).writeSpace().writeBytes(fill ? rg : RG);
        else if (colorSpace instanceof PdfDeviceCs.Cmyk)
            contentStream.getOutputStream().writeFloats(colorValue).writeSpace().writeBytes(fill ? k : K);
        else if (colorSpace instanceof PdfSpecialCs.UncoloredTilingPattern)
            contentStream.getOutputStream().write(resources.addColorSpace(colorSpace)).writeSpace().writeBytes(fill ? cs : CS).
                    writeNewLine().writeFloats(colorValue).writeSpace().write(resources.addPattern(pattern)).writeSpace().writeBytes(fill ? scn : SCN);
        else if (colorSpace instanceof PdfSpecialCs.Pattern)
            contentStream.getOutputStream().write(PdfName.Pattern).writeSpace().writeBytes(fill ? cs : CS).
                    writeNewLine().write(resources.addPattern(pattern)).writeSpace().writeBytes(fill ? scn : SCN);
        else if (colorSpace.getPdfObject().getIndirectReference() != null) {
            if (!setColorValueOnly) {
                PdfName name = resources.addColorSpace(colorSpace);
                contentStream.getOutputStream().write(name).writeSpace().writeBytes(fill ? cs : CS);
            }
            contentStream.getOutputStream().writeFloats(colorValue).writeSpace().writeBytes(fill ? scn : SCN);
        }
        return this;
    }

    public PdfCanvas setFillColorGray(float g) throws PdfException {
        return setColor(gray, new float[]{g}, true);
    }

    public PdfCanvas setStrokeColorGray(float g) throws PdfException {
        return setColor(gray, new float[]{g}, false);
    }

    /**
     * Changes the current color for filling paths to black.
     *
     * @return current canvas.
     */
    public PdfCanvas resetFillColorGray() throws PdfException {
        return setFillColorGray(0);
    }

    /**
     * Changes the current color for stroking paths to black.
     *
     * @return current canvas.
     */
    public PdfCanvas resetStrokeColorGray() throws PdfException {
        return setStrokeColorGray(0);
    }

    public PdfCanvas setFillColorRgb(float r, float g, float b) throws PdfException {
        return setColor(rgb, new float[]{r, g, b}, true);
    }

    public PdfCanvas setStrokeColorRgb(float r, float g, float b) throws PdfException {
        return setColor(rgb, new float[]{r, g, b}, false);
    }

    public PdfCanvas setFillColorShading(PdfPattern.Shading shading) throws PdfException {
        return setColor(pattern, null, shading, true);
    }

    public PdfCanvas setStrokeColorShading(PdfPattern.Shading shading) throws PdfException {
        return setColor(pattern, null, shading, false);
    }

    /**
     * Changes the current color for filling paths to black.
     *
     * @return current canvas.
     */
    public PdfCanvas resetFillColorRgb() throws PdfException {
        return resetFillColorGray();
    }

    /**
     * Changes the current color for stroking paths to black.
     *
     * @return current canvas.
     */
    public PdfCanvas resetStrokeColorRgb() throws PdfException {
        return resetStrokeColorGray();
    }


    public PdfCanvas setFillColorCmyk(float c, float m, float y, float k) throws PdfException {
        return setColor(cmyk, new float[]{c, m, y, k}, true);
    }

    public PdfCanvas setStrokeColorCmyk(float c, float m, float y, float k) throws PdfException {
        return setColor(cmyk, new float[]{c, m, y, k}, false);
    }

    /**
     * Changes the current color for filling paths to black.
     *
     * @return current canvas.
     */
    public PdfCanvas resetFillColorCmyk() throws PdfException {
        return setFillColorCmyk(0, 0, 0, 1);
    }

    /**
     * Changes the current color for stroking paths to black.
     *
     * @return current canvas.
     */
    public PdfCanvas resetStrokeColorCmyk() throws PdfException {
        return setStrokeColorCmyk(0, 0, 0, 1);
    }

    /**
     * Begins a graphic block whose visibility is controlled by the <CODE>layer</CODE>.
     * Blocks can be nested. Each block must be terminated by an {@link #endLayer()}.<p>
     * Note that nested layers with {@link PdfLayer#addChild(PdfLayer)} only require a single
     * call to this method and a single call to {@link #endLayer()}; all the nesting control
     * is built in.
     *
     * @param layer @see PdfLayer.
     * @return current canvas.
     */
    public PdfCanvas beginLayer(final PdfOCG layer) throws PdfException {
        if (layer instanceof PdfLayer && ((PdfLayer)layer).getTitle() != null)
            throw new IllegalArgumentException("Illegal layer argument.");
        if (layerDepth == null)
            layerDepth = new ArrayList<Integer>();
        if (layer instanceof PdfLayerMembership) {
            layerDepth.add(1);
            addToPropertiesAndBeginLayer(layer);
        } else if (layer instanceof PdfLayer) {
            int n = 0;
            PdfLayer la = (PdfLayer)layer;
            while (la != null) {
                if (la.getTitle() == null) {
                    addToPropertiesAndBeginLayer(la);
                    n++;
                }
                la = la.getParent();
            }
            layerDepth.add(n);
        } else
            throw new UnsupportedOperationException("Unsupported type for operand: layer");
        return this;
    }

    /**
     * Ends OCG layer.
     *
     * @return current canvas.
     */
    public PdfCanvas endLayer() throws PdfException {
        int n = 1;
        if (layerDepth != null && !layerDepth.isEmpty()) {
            n = layerDepth.get(layerDepth.size() - 1);
            layerDepth.remove(layerDepth.size() - 1);
        } else {
            throw new PdfException(PdfException.UnbalancedLayerOperators);
        }
        while (n-- > 0)
            contentStream.getOutputStream().writeBytes(EMC).writeNewLine();
        return this;
    }

    /**
     * Creates Image XObject from image and adds it to canvas.
     *
     * @param image    the {@code PdfImageXObject} object
     * @param a        an element of the transformation matrix
     * @param b        an element of the transformation matrix
     * @param c        an element of the transformation matrix
     * @param d        an element of the transformation matrix
     * @param e        an element of the transformation matrix
     * @param f        an element of the transformation matrix
     * @param asInline true if to add image as in-line.
     * @return created Image XObject or null in case of in-line image (asInline = true).
     * @throws PdfException on error
     */
    public PdfXObject addImage(Image image, float a, float b, float c, float d, float e, float f, boolean asInline) throws PdfException {
        if (image.getOriginalType() == Image.WMF) {
            WmfImageHelper wmf = new WmfImageHelper(image);
            // TODO add matrix parameters
            return wmf.createPdfForm(document);
        } else if (asInline) {
            PdfImageXObject imageXObject = new PdfImageXObject(null, image);
            addInlineImage(imageXObject, a, b, c, d, e, f);
            return null;
        } else {
            PdfImageXObject imageXObject = new PdfImageXObject(document, image);
            addImage(imageXObject, a, b, c, d, e, f);
            return imageXObject;
        }
    }

    /**
     * Creates Image XObject from image and adds it to canvas.
     *
     * @param image
     * @param rect
     * @param asInline true if to add image as in-line.
     * @return created XObject or null in case of in-line image (asInline = true).
     * @throws PdfException
     */
    public PdfXObject addImage(Image image, Rectangle rect, boolean asInline) throws PdfException {
        return addImage(image, rect.getWidth(), 0, 0, rect.getHeight(), rect.getX(), rect.getY(), asInline);
    }

    /**
     * Creates Image XObject from image and adds it to canvas.
     *
     * @param image
     * @param x
     * @param y
     * @param asInline true if to add image as in-line.
     * @return created XObject or null in case of in-line image (asInline = true).
     * @throws PdfException
     */
    public PdfXObject addImage(Image image, float x, float y, boolean asInline) throws PdfException {
        if (image.getOriginalType() == Image.WMF) {
            WmfImageHelper wmf = new WmfImageHelper(image);
            // TODO add matrix parameters
            return wmf.createPdfForm(document);
        } else if (asInline) {
            PdfImageXObject imageXObject = new PdfImageXObject(null, image);
            addInlineImage(imageXObject, image.getWidth(), 0, 0, image.getHeight(), x, y);
            return null;
        } else {
            PdfImageXObject imageXObject = new PdfImageXObject(document, image);
            addImage(imageXObject, image.getWidth(), 0, 0, image.getHeight(), x, y);
            return imageXObject;
        }
    }

    /**
     * Creates Image XObject from image and adds it to the specified position with specified width preserving aspect ratio.
     *
     * @param image
     * @param x
     * @param y
     * @param width
     * @param asInline true if to add image as in-line.
     * @return created XObject or null in case of in-line image (asInline = true).
     * @throws PdfException on error.
     */
    public PdfXObject addImage(Image image, float x, float y, float width, boolean asInline) throws PdfException {
        if (image.getOriginalType() == Image.WMF) {
            WmfImageHelper wmf = new WmfImageHelper(image);
            // TODO add matrix parameters
            return wmf.createPdfForm(document);
        } else if (asInline) {
            PdfImageXObject imageXObject = new PdfImageXObject(null, image);
            addInlineImage(imageXObject, width, 0, 0, width / image.getWidth() * image.getHeight(), x, y);
            return null;
        } else {
            PdfImageXObject imageXObject = new PdfImageXObject(document, image);
            addImage(imageXObject, width, 0, 0, width / image.getWidth() * image.getHeight(), x, y);
            return imageXObject;
        }
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
     * @return created XObject or null in case of in-line image (asInline = true).
     * @throws PdfException
     */
    public PdfXObject addImage(Image image, float x, float y, float height, boolean asInline, boolean dummy) throws PdfException {
        return addImage(image, height / image.getHeight() * image.getWidth(), 0, 0, height, x, y, asInline);
    }

    /**
     * Adds {@code PdfXObject} to canvas.
     *
     * @param xObject the {@code PdfImageXObject} object
     * @param a     an element of the transformation matrix
     * @param b     an element of the transformation matrix
     * @param c     an element of the transformation matrix
     * @param d     an element of the transformation matrix
     * @param e     an element of the transformation matrix
     * @param f     an element of the transformation matrix
     * @return canvas a reference to this object.
     * @throws PdfException on error.
     */
    public PdfCanvas addXObject(PdfXObject xObject, float a, float b, float c, float d, float e, float f) throws PdfException {
        if (xObject instanceof PdfFormXObject) {
            return  addForm((PdfFormXObject)xObject, a, b, c, d, e, f);
        } else if (xObject instanceof PdfImageXObject) {
            return  addImage((PdfImageXObject) xObject, a, b, c, d, e, f);
        } else {
            throw new IllegalArgumentException("PdfFormXObject or PdfImageXObject expected.");
        }
    }

    /**
     * Adds {@code PdfXObject} to the specified position.
     *
     * @param xObject
     * @param x
     * @param y
     * @return
     * @throws PdfException on error.
     */
    public PdfCanvas addXObject(PdfXObject xObject, float x, float y) throws PdfException {
        if (xObject instanceof PdfFormXObject) {
            return  addForm((PdfFormXObject)xObject, x, y);
        } else if (xObject instanceof PdfImageXObject) {
            return  addImage((PdfImageXObject) xObject, x, y);
        } else {
            throw new IllegalArgumentException("PdfFormXObject or PdfImageXObject expected.");
        }
    }

    /**
     * Adds {@code PdfXObject} to specified rectangle on canvas.
     *
     * @param xObject
     * @param rect
     * @return
     * @throws PdfException on error.
     */
    public PdfCanvas addXObject(PdfXObject xObject, Rectangle rect) throws PdfException {
        if (xObject instanceof PdfFormXObject) {
            return  addForm((PdfFormXObject)xObject, rect);
        } else if (xObject instanceof PdfImageXObject) {
            return  addImage((PdfImageXObject) xObject, rect);
        } else {
            throw new IllegalArgumentException("PdfFormXObject or PdfImageXObject expected.");
        }
    }

    /**
     * Adds {@code PdfXObject} to the specified position with specified width preserving aspect ratio.
     *
     * @param xObject
     * @param x
     * @param y
     * @param width
     * @return
     * @throws PdfException on error.
     */
    public PdfCanvas addXObject(PdfXObject xObject, float x, float y, float width) throws PdfException {
        if (xObject instanceof PdfFormXObject) {
            return  addForm((PdfFormXObject)xObject, x, y, width);
        } else if (xObject instanceof PdfImageXObject) {
            return  addImage((PdfImageXObject) xObject, x, y, width);
        } else {
            throw new IllegalArgumentException("PdfFormXObject or PdfImageXObject expected.");
        }
    }

    /**
     * Adds {@code PdfXObject} to the specified position with specified height preserving aspect ratio.
     *
     * @param xObject
     * @param x
     * @param y
     * @param height
     * @param dummy
     * @return
     * @throws PdfException on error.
     */
    public PdfCanvas addXObject(PdfXObject xObject, float x, float y, float height, boolean dummy) throws PdfException {
        if (xObject instanceof PdfFormXObject) {
            return  addForm((PdfFormXObject)xObject, x, y, height, dummy);
        } else if (xObject instanceof PdfImageXObject) {
            return  addImage((PdfImageXObject) xObject, x, y, height, dummy);
        } else {
            throw new IllegalArgumentException("PdfFormXObject or PdfImageXObject expected.");
        }
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

    public PdfCanvas openTag(final IPdfTag tag) throws PdfException {
        if (tag.getRole() == null)
            return this;
//        if ((tag.getStructParentIndex() == null) && !(tag instanceof PdfArtifact))
//            throw new PdfException(PdfException.StructureElementIsNotLinkedToStructParent, tag);
        return beginMarkedContent(tag.getRole(), tag.getMcid() == null ? null : new PdfDictionary(new HashMap<PdfName, PdfObject>() {{
            put(PdfName.MCID, new PdfNumber(tag.getMcid()));
        }}));
    }

//    public PdfCanvas openTag(final PdfStructElem structElem) throws PdfException {
//        List<IPdfStructElem> kids = structElem.getKids();
//        boolean pdfTagFound = false;
//        if (kids != null) {
//            for (IPdfStructElem kid : kids)
//                if (kid instanceof IPdfTag) {
//                    openTag((IPdfTag)kid);
//                    pdfTagFound = true;
//                    break;
//                }
//        }
//        if (!pdfTagFound)
//            openTag(structElem.addKid(new PdfMcrNumber()))
//        return this;
//    }

    public PdfCanvas closeTag() throws PdfException {
        return endMarkedContent();
    }

    /**
     * Outputs a {@code String} directly to the content.
     *
     * @param s the {@code String}
     * @return current canvas.
     */
    public PdfCanvas setLiteral(final String s) throws PdfException {
        contentStream.getOutputStream().writeString(s);
        return this;
    }

    /**
     * Outputs a {@code char} directly to the content.
     *
     * @param c the {@code char}
     * @return current canvas.
     */
    public PdfCanvas setLiteral(final char c) throws PdfException {
        contentStream.getOutputStream().writeInteger((int) c);
        return this;
    }

    /**
     * Outputs a {@code float} directly to the content.
     *
     * @param n the {@code float}
     * @return current canvas.
     */
    public PdfCanvas setLiteral(final float n) throws PdfException {
        contentStream.getOutputStream().writeFloat(n);
        return this;
    }

    /**
     * Adds {@code PdfImageXObject} to canvas.
     *
     * @param imageXObject the {@code PdfImageXObject} object
     * @param a     an element of the transformation matrix
     * @param b     an element of the transformation matrix
     * @param c     an element of the transformation matrix
     * @param d     an element of the transformation matrix
     * @param e     an element of the transformation matrix
     * @param f     an element of the transformation matrix
     * @throws PdfException on error
     */
    protected void addInlineImage(PdfImageXObject imageXObject, float a, float b, float c, float d, float e, float f) throws PdfException {
        saveState();
        concatMatrix(a, b, c, d, e, f);
        PdfOutputStream os = contentStream.getOutputStream();
        os.writeBytes(BI);
        for (Map.Entry<PdfName, PdfObject> entry : imageXObject.getPdfObject().entrySet()) {
            PdfName key = entry.getKey();
            if (!PdfName.Type.equals(key) && !PdfName.Subtype.equals(key) && !PdfName.Length.equals(key)) {
                os.write(entry.getKey()).writeSpace();
                os.write(entry.getValue()).writeNewLine();
            }
        }
        os.writeBytes(ID);
        os.writeBytes(imageXObject.getPdfObject().getBytes()).writeNewLine().writeBytes(EI).writeNewLine();
        restoreState();
    }

    /**
     * Adds {@code PdfFormXObject} to canvas.
     *
     * @param form the {@code PdfImageXObject} object
     * @param a     an element of the transformation matrix
     * @param b     an element of the transformation matrix
     * @param c     an element of the transformation matrix
     * @param d     an element of the transformation matrix
     * @param e     an element of the transformation matrix
     * @param f     an element of the transformation matrix
     * @return canvas a reference to this object.
     * @throws PdfException on error
     */
    private PdfCanvas addForm(PdfFormXObject form, float a, float b, float c, float d, float e, float f) throws PdfException {
        saveState();
        concatMatrix(a, b, c, d, e, f);
        PdfName name = resources.addForm(form);
        contentStream.getOutputStream().write(name).writeSpace().writeBytes(Do);
        restoreState();
        return this;
    }

    /**
     * Adds {@code PdfFormXObject} to the specified position.
     *
     * @param form
     * @param x
     * @param y
     * @return
     * @throws PdfException
     */
    private PdfCanvas addForm(PdfFormXObject form, float x, float y) throws PdfException {
        return addForm(form, 1, 0, 0, 1, x, y);
    }

    /**
     * Adds {@code PdfFormXObject} to specified rectangle on canvas.
     *
     * @param form
     * @param rect
     * @return
     * @throws PdfException
     */
    private PdfCanvas addForm(PdfFormXObject form, Rectangle rect) throws PdfException {
        return addForm(form, rect.getWidth(), 0, 0, rect.getHeight(), rect.getX(), rect.getY());
    }

    /**
     * Adds I{@code PdfFormXObject} to the specified position with specified width preserving aspect ratio.
     *
     * @param form
     * @param x
     * @param y
     * @param width
     * @return
     * @throws PdfException
     */
    private PdfCanvas addForm(PdfFormXObject form, float x, float y, float width) throws PdfException {
        PdfArray bbox = form.getPdfObject().getAsArray(PdfName.BBox);
        if (bbox == null)
            throw new PdfException(PdfException.PdfFormXobjectHasInvalidBbox);
        Float formWidth = Math.abs(bbox.getAsFloat(2) - bbox.getAsFloat(0));
        Float formHeight = Math.abs(bbox.getAsFloat(3) - bbox.getAsFloat(1));
        return addForm(form, width, 0, 0, width / formWidth * formHeight, x, y);
    }

    /**
     * Adds {@code PdfFormXObject} to the specified position with specified height preserving aspect ratio.
     *
     * @param form
     * @param x
     * @param y
     * @param height
     * @param dummy
     * @return
     * @throws PdfException on error.
     */
    private PdfCanvas addForm(PdfFormXObject form, float x, float y, float height, boolean dummy) throws PdfException {
        PdfArray bbox = form.getPdfObject().getAsArray(PdfName.BBox);
        if (bbox == null)
            throw new PdfException(PdfException.PdfFormXobjectHasInvalidBbox);
        Float formWidth = Math.abs(bbox.getAsFloat(2) - bbox.getAsFloat(0));
        Float formHeight = Math.abs(bbox.getAsFloat(3) - bbox.getAsFloat(1));
        return addForm(form, height / formHeight * formWidth, 0, 0, height, x, y);
    }

    /**
     * Adds {@code PdfImageXObject} to canvas.
     *
     * @param image the {@code PdfImageXObject} object
     * @param a     an element of the transformation matrix
     * @param b     an element of the transformation matrix
     * @param c     an element of the transformation matrix
     * @param d     an element of the transformation matrix
     * @param e     an element of the transformation matrix
     * @param f     an element of the transformation matrix
     * @return canvas a reference to this object.
     * @throws PdfException on error
     */
    private PdfCanvas addImage(PdfImageXObject image, float a, float b, float c, float d, float e, float f) throws PdfException {
        saveState();
        concatMatrix(a, b, c, d, e, f);
        PdfName name = resources.addImage(image);
        contentStream.getOutputStream().write(name).writeSpace().writeBytes(Do);
        restoreState();
        return this;
    }

    /**
     * Adds {@code PdfImageXObject} to the specified position.
     *
     * @param image
     * @param x
     * @param y
     * @return
     * @throws PdfException
     */
    private PdfCanvas addImage(PdfImageXObject image, float x, float y) throws PdfException {
        return addImage(image, image.getWidth(), 0, 0, image.getHeight(), x, y);
    }

    /**
     * Adds {@code PdfImageXObject} to specified rectangle on canvas.
     *
     * @param image
     * @param rect
     * @return
     * @throws PdfException
     */
    private PdfCanvas addImage(PdfImageXObject image, Rectangle rect) throws PdfException {
        return addImage(image, rect.getWidth(), 0, 0, rect.getHeight(), rect.getX(), rect.getY());
    }

    /**
     * Adds {@code PdfImageXObject} to the specified position with specified width preserving aspect ratio.
     *
     * @param image
     * @param x
     * @param y
     * @param width
     * @return
     * @throws PdfException
     */
    private PdfCanvas addImage(PdfImageXObject image, float x, float y, float width) throws PdfException {
        return addImage(image, width, 0, 0, width / image.getWidth() * image.getHeight(), x, y);
    }

    /**
     * Adds {@code PdfImageXObject} to the specified position with specified height preserving aspect ratio.
     *
     * @param image
     * @param x
     * @param y
     * @param height
     * @param dummy
     * @return
     * @throws PdfException on error.
     */
    private PdfCanvas addImage(PdfImageXObject image, float x, float y, float height, boolean dummy) throws PdfException {
        return addImage(image, height / image.getHeight() * image.getWidth(), 0, 0, height, x, y);
    }



    private static boolean floatsAreEqual(Float f1, Float f2) {
        if (f1 == null && f2 == null)
            return true;
        else if (f1 == null || f2 == null)
            return false;
        else
            return Float.compare(f1, f2) == 0;
    }

    private static boolean integersAreEqual(Integer i1, Integer i2) {
        if (i1 == null && i2 == null)
            return true;
        else if (i1 == null || i2 == null)
            return false;
        else
            return Integer.compare(i1, i2) == 0;
    }

    private static PdfStream getPageStream(PdfPage page) throws PdfException {
        PdfStream stream = page.getContentStream(page.getContentStreamCount() - 1);
        return stream.getOutputStream() == null ? page.newContentStreamAfter() : stream;
    }

    /**
     * A helper to insert into the content stream the {@code text}
     * converted to bytes according to the font's encoding.
     *
     * @param text the text to write.
     */
    private void showText2(final String text) throws PdfException {
        if (currentGs.getFont() == null)
            throw new PdfException(PdfException.FontAndSizeMustBeSetBeforeWritingAnyText, currentGs);
        byte b[] = currentGs.getFont().convertToBytes(text);
        Utilities.writeEscapedString(contentStream.getOutputStream(), b);
    }

    private void addToPropertiesAndBeginLayer(final PdfOCG layer) throws PdfException {
        PdfName name = resources.addProperties(layer.getPdfObject());
        contentStream.getOutputStream().write(PdfName.OC).writeSpace().write(name).writeSpace().writeBytes(BDC).writeNewLine();
    }

    private Color createColor(PdfColorSpace colorSpace, float[] colorValue, PdfPattern pattern) throws PdfException {
        if (colorSpace instanceof PdfSpecialCs.UncoloredTilingPattern) {
            return new PatternColor((PdfPattern.Tiling) pattern, ((PdfSpecialCs.UncoloredTilingPattern) colorSpace).getUnderlyingColorSpace(), colorValue);
        } else if (colorSpace instanceof PdfSpecialCs.Pattern) {
            return new PatternColor(pattern);
        }
        return new Color(colorSpace, colorValue);
    }
}
