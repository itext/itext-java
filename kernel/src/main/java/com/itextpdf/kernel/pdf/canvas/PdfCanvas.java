/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf.canvas;

import com.itextpdf.commons.datastructures.Tuple2;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.io.font.otf.ActualTextIterator;
import com.itextpdf.io.font.otf.Glyph;
import com.itextpdf.io.font.otf.GlyphLine;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.io.image.ImageType;
import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.io.util.StreamUtil;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.colors.DeviceGray;
import com.itextpdf.kernel.colors.PatternColor;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.MemoryLimitsAwareException;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfType0Font;
import com.itextpdf.kernel.geom.AffineTransform;
import com.itextpdf.kernel.geom.Matrix;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.geom.Vector;
import com.itextpdf.kernel.pdf.PageContentRotationHelper;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfOutputStream;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfResources;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.canvas.wmf.WmfImageHelper;
import com.itextpdf.kernel.pdf.colorspace.PdfColorSpace;
import com.itextpdf.kernel.pdf.colorspace.PdfDeviceCs;
import com.itextpdf.kernel.pdf.colorspace.PdfPattern;
import com.itextpdf.kernel.pdf.colorspace.PdfSpecialCs;
import com.itextpdf.kernel.pdf.colorspace.shading.AbstractPdfShading;
import com.itextpdf.kernel.pdf.extgstate.PdfExtGState;
import com.itextpdf.kernel.pdf.layer.IPdfOCG;
import com.itextpdf.kernel.pdf.layer.PdfLayer;
import com.itextpdf.kernel.pdf.layer.PdfLayerMembership;
import com.itextpdf.kernel.pdf.tagutils.TagReference;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;
import com.itextpdf.kernel.validation.context.CanvasBmcValidationContext;
import com.itextpdf.kernel.validation.context.CanvasStackValidationContext;
import com.itextpdf.kernel.validation.context.CanvasTextAdditionContext;
import com.itextpdf.kernel.validation.context.CanvasWritingContentValidationContext;
import com.itextpdf.kernel.validation.context.ExtendedGStateValidationContext;
import com.itextpdf.kernel.validation.context.FillColorValidationContext;
import com.itextpdf.kernel.validation.context.FontGlyphsGStateValidationContext;
import com.itextpdf.kernel.validation.context.FontValidationContext;
import com.itextpdf.kernel.validation.context.InlineImageValidationContext;
import com.itextpdf.kernel.validation.context.RenderingIntentValidationContext;
import com.itextpdf.kernel.validation.context.StrokeColorValidationContext;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

/**
 * PdfCanvas class represents an algorithm for writing data into content stream.
 * To write into page content, create PdfCanvas from a page instance.
 * To write into form XObject, create PdfCanvas from a form XObject instance.
 * Make sure to call PdfCanvas.release() after you finished writing to the canvas.
 * It will save some memory.
 */
public class PdfCanvas {

    private static final byte[] B = ByteUtils.getIsoBytes("B\n");
    private static final byte[] b = ByteUtils.getIsoBytes("b\n");
    private static final byte[] BDC = ByteUtils.getIsoBytes("BDC\n");
    private static final byte[] BI = ByteUtils.getIsoBytes("BI\n");
    private static final byte[] BMC = ByteUtils.getIsoBytes("BMC\n");
    private static final byte[] BStar = ByteUtils.getIsoBytes("B*\n");
    private static final byte[] bStar = ByteUtils.getIsoBytes("b*\n");
    private static final byte[] BT = ByteUtils.getIsoBytes("BT\n");
    private static final byte[] c = ByteUtils.getIsoBytes("c\n");
    private static final byte[] cm = ByteUtils.getIsoBytes("cm\n");
    private static final byte[] cs = ByteUtils.getIsoBytes("cs\n");
    private static final byte[] CS = ByteUtils.getIsoBytes("CS\n");
    private static final byte[] d = ByteUtils.getIsoBytes("d\n");
    private static final byte[] Do = ByteUtils.getIsoBytes("Do\n");
    private static final byte[] EI = ByteUtils.getIsoBytes("EI\n");
    private static final byte[] EMC = ByteUtils.getIsoBytes("EMC\n");
    private static final byte[] ET = ByteUtils.getIsoBytes("ET\n");
    private static final byte[] f = ByteUtils.getIsoBytes("f\n");
    private static final byte[] fStar = ByteUtils.getIsoBytes("f*\n");
    private static final byte[] G = ByteUtils.getIsoBytes("G\n");
    private static final byte[] g = ByteUtils.getIsoBytes("g\n");
    private static final byte[] gs = ByteUtils.getIsoBytes("gs\n");
    private static final byte[] h = ByteUtils.getIsoBytes("h\n");
    private static final byte[] i = ByteUtils.getIsoBytes("i\n");
    private static final byte[] ID = ByteUtils.getIsoBytes("ID\n");
    private static final byte[] j = ByteUtils.getIsoBytes("j\n");
    private static final byte[] J = ByteUtils.getIsoBytes("J\n");
    private static final byte[] K = ByteUtils.getIsoBytes("K\n");
    private static final byte[] k = ByteUtils.getIsoBytes("k\n");
    private static final byte[] l = ByteUtils.getIsoBytes("l\n");
    private static final byte[] m = ByteUtils.getIsoBytes("m\n");
    private static final byte[] M = ByteUtils.getIsoBytes("M\n");
    private static final byte[] n = ByteUtils.getIsoBytes("n\n");
    private static final byte[] q = ByteUtils.getIsoBytes("q\n");
    private static final byte[] Q = ByteUtils.getIsoBytes("Q\n");
    private static final byte[] re = ByteUtils.getIsoBytes("re\n");
    private static final byte[] rg = ByteUtils.getIsoBytes("rg\n");
    private static final byte[] RG = ByteUtils.getIsoBytes("RG\n");
    private static final byte[] ri = ByteUtils.getIsoBytes("ri\n");
    private static final byte[] S = ByteUtils.getIsoBytes("S\n");
    private static final byte[] s = ByteUtils.getIsoBytes("s\n");
    private static final byte[] scn = ByteUtils.getIsoBytes("scn\n");
    private static final byte[] SCN = ByteUtils.getIsoBytes("SCN\n");
    private static final byte[] sh = ByteUtils.getIsoBytes("sh\n");
    private static final byte[] Tc = ByteUtils.getIsoBytes("Tc\n");
    private static final byte[] Td = ByteUtils.getIsoBytes("Td\n");
    private static final byte[] TD = ByteUtils.getIsoBytes("TD\n");
    private static final byte[] Tf = ByteUtils.getIsoBytes("Tf\n");
    private static final byte[] TJ = ByteUtils.getIsoBytes("TJ\n");
    private static final byte[] Tj = ByteUtils.getIsoBytes("Tj\n");
    private static final byte[] TL = ByteUtils.getIsoBytes("TL\n");
    private static final byte[] Tm = ByteUtils.getIsoBytes("Tm\n");
    private static final byte[] Tr = ByteUtils.getIsoBytes("Tr\n");
    private static final byte[] Ts = ByteUtils.getIsoBytes("Ts\n");
    private static final byte[] TStar = ByteUtils.getIsoBytes("T*\n");
    private static final byte[] Tw = ByteUtils.getIsoBytes("Tw\n");
    private static final byte[] Tz = ByteUtils.getIsoBytes("Tz\n");
    private static final byte[] v = ByteUtils.getIsoBytes("v\n");
    private static final byte[] W = ByteUtils.getIsoBytes("W\n");
    private static final byte[] w = ByteUtils.getIsoBytes("w\n");
    private static final byte[] WStar = ByteUtils.getIsoBytes("W*\n");
    private static final byte[] y = ByteUtils.getIsoBytes("y\n");

    private static final PdfDeviceCs.Gray gray = new PdfDeviceCs.Gray();
    private static final PdfDeviceCs.Rgb rgb = new PdfDeviceCs.Rgb();
    private static final PdfDeviceCs.Cmyk cmyk = new PdfDeviceCs.Cmyk();
    private static final PdfSpecialCs.Pattern pattern = new PdfSpecialCs.Pattern();

    private static final float IDENTITY_MATRIX_EPS = 1e-4f;

    // Flag showing whether to check the color on drawing or not
    // Normally the color is checked on setColor but not the default one which is DeviceGray.BLACK
    private boolean defaultDeviceGrayBlackColorCheckRequired = true;

    /**
     * a LIFO stack of graphics state saved states.
     */
    protected Stack<CanvasGraphicsState> gsStack = new Stack<>();
    /**
     * the current graphics state.
     */
    protected CanvasGraphicsState currentGs = new CanvasGraphicsState();
    /**
     * the content stream for this canvas object.
     */
    protected PdfStream contentStream;
    /**
     * the resources for the page that this canvas belongs to.
     *
     * @see PdfResources
     */
    protected PdfResources resources;
    /**
     * the document that the resulting content stream of this canvas will be written to.
     */
    protected PdfDocument document;
    /**
     * a counter variable for the marked content stack.
     */
    protected int mcDepth;

    /**
     * The list where we save/restore the layer depth.
     */
    protected List<Integer> layerDepth;

    private Stack<Tuple2<PdfName, PdfDictionary>> tagStructureStack = new Stack<>();
    protected boolean drawingOnPage = false;

    /**
     * Creates PdfCanvas from content stream of page, form XObject, pattern etc.
     *
     * @param contentStream The content stream
     * @param resources     The resources, a specialized dictionary that can be used by PDF instructions in the content stream
     * @param document      The document that the resulting content stream will be written to
     */
    public PdfCanvas(PdfStream contentStream, PdfResources resources, PdfDocument document) {
        this.contentStream = ensureStreamDataIsReadyToBeProcessed(contentStream);
        this.resources = resources;
        this.document = document;
    }

    /**
     * Convenience method for fast PdfCanvas creation by a certain page.
     *
     * @param page page to create canvas from.
     */
    public PdfCanvas(PdfPage page) {
        this(page, (page.getDocument().getReader() != null && page.getDocument().getWriter() != null
                && page.getContentStreamCount() > 0 && page.getLastContentStream().getLength() > 0)
                || (page.getRotation() != 0 && page.isIgnorePageRotationForContent()));
    }

    /**
     * Convenience method for fast PdfCanvas creation by a certain page.
     *
     * @param page           page to create canvas from.
     * @param wrapOldContent true to wrap all old content streams into q/Q operators so that the state of old
     *                       content streams would not affect the new one
     */
    public PdfCanvas(PdfPage page, boolean wrapOldContent) {
        this(getPageStream(page), page.getResources(), page.getDocument());
        if (wrapOldContent) {
            // Wrap old content in q/Q in order not to get unexpected results because of the CTM
            page.newContentStreamBefore().getOutputStream().writeBytes(ByteUtils.getIsoBytes("q\n"));
            contentStream.getOutputStream().writeBytes(ByteUtils.getIsoBytes("Q\n"));
        }
        if (page.getRotation() != 0 && page.isIgnorePageRotationForContent()
                && (wrapOldContent || !PageContentRotationHelper.isPageRotationInverseMatrixWritten(page))) {
            applyRotation(page);
            PageContentRotationHelper.setPageRotationInverseMatrixWritten(page);
        }
        this.drawingOnPage = true;
    }

    /**
     * Creates a PdfCanvas from a PdfFormXObject.
     *
     * @param xObj     the PdfFormXObject used to create the PdfCanvas
     * @param document the document to which the resulting content stream will be written
     */
    public PdfCanvas(PdfFormXObject xObj, PdfDocument document) {
        this(xObj.getPdfObject(), xObj.getResources(), document);
    }

    /**
     * Convenience method for fast PdfCanvas creation by a certain page.
     *
     * @param doc     The document
     * @param pageNum The page number
     */
    public PdfCanvas(PdfDocument doc, int pageNum) {
        this(doc.getPage(pageNum));
    }

    /**
     * Get the resources of the page that this canvas belongs to..
     *
     * @return PdfResources of the page that this canvas belongs to..
     */
    public PdfResources getResources() {
        return resources;
    }

    /**
     * Get the document this canvas belongs to
     *
     * @return PdfDocument the document that this canvas belongs to
     */
    public PdfDocument getDocument() {
        return document;
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
     * Gets current {@link CanvasGraphicsState}.
     *
     * @return container containing properties for the current state of the canvas.
     */
    public CanvasGraphicsState getGraphicsState() {
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
    }

    /**
     * Saves graphics state.
     *
     * @return current canvas.
     */
    public PdfCanvas saveState() {
        document.checkIsoConformance(new CanvasStackValidationContext('q'));
        gsStack.push(currentGs);
        currentGs = new CanvasGraphicsState(currentGs);
        contentStream.getOutputStream().writeBytes(q);
        return this;
    }

    /**
     * Restores graphics state.
     *
     * @return current canvas.
     */
    public PdfCanvas restoreState() {
        document.checkIsoConformance(new CanvasStackValidationContext('Q'));
        if (gsStack.isEmpty()) {
            throw new PdfException(KernelExceptionMessageConstant.UNBALANCED_SAVE_RESTORE_STATE_OPERATORS);
        }
        currentGs = gsStack.pop();
        contentStream.getOutputStream().writeBytes(Q);
        return this;
    }

    /**
     * Concatenates the 2x3 affine transformation matrix to the current matrix in the
     * content stream managed by this Canvas.
     *
     * <p>
     * Detailed explanation of {@code [a b c d e f]} parameters of transformation
     * matrix can be found in {@link Matrix} documentation.
     *
     * @param a operand 1,1 in the matrix.
     * @param b operand 1,2 in the matrix.
     * @param c operand 2,1 in the matrix.
     * @param d operand 2,2 in the matrix.
     * @param e operand 3,1 in the matrix.
     * @param f operand 3,2 in the matrix.
     * @return current canvas
     */
    public PdfCanvas concatMatrix(double a, double b, double c, double d, double e, double f) {
        currentGs.updateCtm((float) a, (float) b, (float) c, (float) d, (float) e, (float) f);
        contentStream.getOutputStream().writeDouble(a).writeSpace().
                writeDouble(b).writeSpace().
                writeDouble(c).writeSpace().
                writeDouble(d).writeSpace().
                writeDouble(e).writeSpace().
                writeDouble(f).writeSpace().writeBytes(cm);
        return this;
    }

    /**
     * Concatenates the 2x3 affine transformation matrix to the current matrix
     * in the content stream managed by this Canvas.
     * If an array not containing the 6 values of the matrix is passed,
     * The current canvas is returned unchanged.
     *
     * @param array affine transformation stored as a PdfArray with 6 values
     * @return current canvas
     */
    public PdfCanvas concatMatrix(PdfArray array) {
        if (array.size() != 6) {
            //Throw exception or warning here
            return this;
        }
        for (int i = 0; i < array.size(); i++) {
            if (!array.get(i).isNumber()) {
                return this;
            }
        }
        return concatMatrix(array.getAsNumber(0).doubleValue(), array.getAsNumber(1).doubleValue(), array.getAsNumber(2).doubleValue(), array.getAsNumber(3).doubleValue(), array.getAsNumber(4).doubleValue(), array.getAsNumber(5).doubleValue());
    }

    /**
     * Concatenates the affine transformation matrix to the current matrix
     * in the content stream managed by this Canvas.
     *
     * @param transform affine transformation matrix to be concatenated to the current matrix
     * @return current canvas
     * @see #concatMatrix(double, double, double, double, double, double)
     */
    public PdfCanvas concatMatrix(AffineTransform transform) {
        float[] matrix = new float[6];
        transform.getMatrix(matrix);
        return concatMatrix(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
    }

    /**
     * Begins text block (PDF BT operator).
     *
     * @return current canvas.
     */
    public PdfCanvas beginText() {
        contentStream.getOutputStream().writeBytes(BT);
        return this;
    }

    /**
     * Ends text block (PDF ET operator).
     *
     * @return current canvas.
     */
    public PdfCanvas endText() {
        contentStream.getOutputStream().writeBytes(ET);
        return this;
    }

    /**
     * Begins variable text block
     *
     * @return current canvas
     */
    public PdfCanvas beginVariableText() {
        return beginMarkedContent(PdfName.Tx);
    }

    /**
     * Ends variable text block
     *
     * @return current canvas
     */
    public PdfCanvas endVariableText() {
        return endMarkedContent();
    }

    /**
     * Sets font and size (PDF Tf operator).
     *
     * @param font The font
     * @param size The font size.
     * @return The edited canvas.
     */
    public PdfCanvas setFontAndSize(PdfFont font, float size) {
        currentGs.setFontSize(size);
        PdfName fontName = resources.addFont(document, font);
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
    public PdfCanvas moveText(double x, double y) {
        contentStream.getOutputStream()
                .writeDouble(x)
                .writeSpace()
                .writeDouble(y).writeSpace()
                .writeBytes(Td);
        return this;
    }

    /**
     * Sets the text leading parameter.
     * <br>
     * The leading parameter is measured in text space units. It specifies the vertical distance
     * between the baselines of adjacent lines of text.
     * <br>
     *
     * @param leading the new leading.
     * @return current canvas.
     */
    public PdfCanvas setLeading(float leading) {
        currentGs.setLeading(leading);
        contentStream.getOutputStream()
                .writeFloat(leading)
                .writeSpace()
                .writeBytes(TL);

        return this;
    }

    /**
     * Moves to the start of the next line, offset from the start of the current line.
     * <br>
     * As a side effect, this sets the leading parameter in the text state.
     * <br>
     *
     * @param x offset of the new current point
     * @param y y-coordinate of the new current point
     * @return current canvas.
     */
    public PdfCanvas moveTextWithLeading(float x, float y) {
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
     *
     * @return current canvas.
     */
    public PdfCanvas newlineText() {
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
    public PdfCanvas newlineShowText(String text) {
        checkDefaultDeviceGrayBlackColor(getColorKeyForText());

        showTextInt(text);
        contentStream.getOutputStream()
                .writeByte('\'')
                .writeNewLine();
        return this;
    }

    /**
     * Moves to the next line and shows text string, using the given values of the character and word spacing parameters.
     *
     * @param wordSpacing a parameter
     * @param charSpacing a parameter
     * @param text        the text to write
     * @return current canvas.
     */
    public PdfCanvas newlineShowText(float wordSpacing, float charSpacing, String text) {
        checkDefaultDeviceGrayBlackColor(getColorKeyForText());

        contentStream.getOutputStream()
                .writeFloat(wordSpacing)
                .writeSpace()
                .writeFloat(charSpacing);
        showTextInt(text);
        contentStream.getOutputStream()
                .writeByte('"')
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
    public PdfCanvas setTextRenderingMode(int textRenderingMode) {
        currentGs.setTextRenderingMode(textRenderingMode);
        contentStream.getOutputStream()
                .writeInteger(textRenderingMode).writeSpace()
                .writeBytes(Tr);
        return this;
    }

    /**
     * Sets the text rise parameter.
     * <br>
     * This allows to write text in subscript or superscript mode.
     * <br>
     *
     * @param textRise a parameter
     * @return current canvas.
     */
    public PdfCanvas setTextRise(float textRise) {
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
    public PdfCanvas setWordSpacing(float wordSpacing) {
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
    public PdfCanvas setCharacterSpacing(float charSpacing) {
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
    public PdfCanvas setHorizontalScaling(float scale) {
        currentGs.setHorizontalScaling(scale);
        contentStream.getOutputStream()
                .writeFloat(scale)
                .writeSpace()
                .writeBytes(Tz);
        return this;
    }

    /**
     * Replaces the text matrix.
     *
     * <p>
     * Detailed explanation of {@code [a b c d e f]} parameters of transformation
     * matrix can be found in {@link Matrix} documentation.
     *
     * @param a operand 1,1 in the matrix.
     * @param b operand 1,2 in the matrix.
     * @param c operand 2,1 in the matrix.
     * @param d operand 2,2 in the matrix.
     * @param e operand 3,1 in the matrix.
     * @param f operand 3,2 in the matrix.
     * @return current canvas.
     */
    public PdfCanvas setTextMatrix(float a, float b, float c, float d, float e, float f) {
        contentStream.getOutputStream()
                .writeFloat(a)
                .writeSpace()
                .writeFloat(b)
                .writeSpace()
                .writeFloat(c)
                .writeSpace()
                .writeFloat(d)
                .writeSpace()
                .writeFloat(e)
                .writeSpace()
                .writeFloat(f).writeSpace()
                .writeBytes(Tm);
        return this;
    }

    /**
     * Replaces the text matrix.
     *
     * @param transform new textmatrix as transformation
     * @return current canvas
     */
    public PdfCanvas setTextMatrix(AffineTransform transform) {
        float[] matrix = new float[6];
        transform.getMatrix(matrix);
        return setTextMatrix(matrix[0], matrix[1], matrix[2], matrix[3], matrix[4], matrix[5]);
    }

    /**
     * Changes the text matrix.
     *
     * @param x operand 3,1 in the matrix.
     * @param y operand 3,2 in the matrix.
     * @return current canvas.
     */
    public PdfCanvas setTextMatrix(float x, float y) {
        return setTextMatrix(1, 0, 0, 1, x, y);
    }

    /**
     * Shows text (operator Tj).
     *
     * @param text text to show.
     * @return current canvas.
     */
    public PdfCanvas showText(String text) {
        checkDefaultDeviceGrayBlackColor(getColorKeyForText());

        showTextInt(text);
        contentStream.getOutputStream().writeBytes(Tj);
        return this;
    }

    /**
     * Shows text (operator Tj).
     *
     * @param text text to show.
     * @return current canvas.
     */
    public PdfCanvas showText(GlyphLine text) {
        return showText(text, new ActualTextIterator(text));
    }

    /**
     * Shows text (operator Tj).
     *
     * @param text     text to show.
     * @param iterator iterator over parts of the glyph line that should be wrapped into some marked content groups,
     *                 e.g. /ActualText or /ReversedChars
     * @return current canvas.
     */
    public PdfCanvas showText(GlyphLine text, Iterator<GlyphLine.GlyphLinePart> iterator) {
        checkDefaultDeviceGrayBlackColor(getColorKeyForText());
        document.checkIsoConformance(new FontGlyphsGStateValidationContext(currentGs, contentStream));
        this.checkIsoConformanceWritingOnContent();
        PdfFont font;
        if ((font = currentGs.getFont()) == null) {
            throw new PdfException(
                    KernelExceptionMessageConstant.FONT_AND_SIZE_MUST_BE_SET_BEFORE_WRITING_ANY_TEXT, currentGs);
        }

        checkTextOnAddition(text);
        document.checkIsoConformance(new FontValidationContext(text.toString(), currentGs.getFont()));

        final float fontSize = FontProgram.convertTextSpaceToGlyphSpace(currentGs.getFontSize());
        float charSpacing = currentGs.getCharSpacing();
        float scaling = currentGs.getHorizontalScaling() / 100f;
        List<GlyphLine.GlyphLinePart> glyphLineParts = iteratorToList(iterator);
        for (int partIndex = 0; partIndex < glyphLineParts.size(); ++partIndex) {
            GlyphLine.GlyphLinePart glyphLinePart = glyphLineParts.get(partIndex);
            if (glyphLinePart.getActualText() != null) {
                PdfDictionary properties = new PdfDictionary();
                properties.put(PdfName.ActualText, new PdfString(glyphLinePart.getActualText(),
                        PdfEncodings.UNICODE_BIG).setHexWriting(true));
                beginMarkedContent(PdfName.Span, properties);
            } else if (glyphLinePart.isReversed()) {
                beginMarkedContent(PdfName.ReversedChars);
            }
            int sub = glyphLinePart.getStart();
            for (int i = glyphLinePart.getStart(); i < glyphLinePart.getEnd(); i++) {
                Glyph glyph = text.get(i);
                if (glyph.hasOffsets()) {
                    if (i - 1 - sub >= 0) {
                        font.writeText(text, sub, i - 1, contentStream.getOutputStream());
                        contentStream.getOutputStream().writeBytes(Tj);
                        contentStream.getOutputStream()
                                .writeFloat(getSubrangeWidth(text, sub, i - 1), true)
                                .writeSpace()
                                .writeFloat(0)
                                .writeSpace()
                                .writeBytes(Td);
                    }
                    float xPlacement = Float.NaN;
                    float yPlacement = Float.NaN;
                    if (glyph.hasPlacement()) {

                        {
                            float xPlacementAddition = 0;
                            int currentGlyphIndex = i;
                            Glyph currentGlyph = text.get(i);
                            // if xPlacement is not zero, anchorDelta is expected to be non-zero as well
                            while (currentGlyph != null && (currentGlyph.getAnchorDelta() != 0)) {
                                xPlacementAddition += currentGlyph.getXPlacement();
                                if (currentGlyph.getAnchorDelta() == 0) {
                                    break;
                                } else {
                                    currentGlyphIndex += currentGlyph.getAnchorDelta();
                                    currentGlyph = text.get(currentGlyphIndex);
                                }
                            }
                            xPlacement = -getSubrangeWidth(text, currentGlyphIndex, i) + xPlacementAddition * fontSize * scaling;
                        }

                        {
                            float yPlacementAddition = 0;
                            int currentGlyphIndex = i;
                            Glyph currentGlyph = text.get(i);
                            while (currentGlyph != null && currentGlyph.getYPlacement() != 0) {
                                yPlacementAddition += currentGlyph.getYPlacement();
                                if (currentGlyph.getAnchorDelta() == 0) {
                                    break;
                                } else {
                                    currentGlyphIndex += currentGlyph.getAnchorDelta();
                                    currentGlyph = text.get(currentGlyphIndex);
                                }
                            }
                            yPlacement = -getSubrangeYDelta(text, currentGlyphIndex, i) + yPlacementAddition * fontSize;
                        }

                        contentStream.getOutputStream()
                                .writeFloat(xPlacement, true)
                                .writeSpace()
                                .writeFloat(yPlacement, true)
                                .writeSpace()
                                .writeBytes(Td);
                    }
                    font.writeText(text, i, i, contentStream.getOutputStream());
                    contentStream.getOutputStream().writeBytes(Tj);
                    if (!Float.isNaN(xPlacement)) {
                        contentStream.getOutputStream()
                                .writeFloat(-xPlacement, true)
                                .writeSpace()
                                .writeFloat(-yPlacement, true)
                                .writeSpace()
                                .writeBytes(Td);

                    }
                    if (glyph.hasAdvance()) {
                        contentStream.getOutputStream()
                                // Let's explicitly ignore width of glyphs with placement if they also have xAdvance, since their width doesn't affect text cursor position.
                                .writeFloat((((glyph.hasPlacement() ? 0 : glyph.getWidth()) + glyph.getXAdvance()) * fontSize + charSpacing + getWordSpacingAddition(glyph)) * scaling, true)
                                .writeSpace()
                                .writeFloat(glyph.getYAdvance() * fontSize, true)
                                .writeSpace()
                                .writeBytes(Td);
                    }
                    sub = i + 1;
                }
            }
            if (glyphLinePart.getEnd() - sub > 0) {
                font.writeText(text, sub, glyphLinePart.getEnd() - 1, contentStream.getOutputStream());
                contentStream.getOutputStream().writeBytes(Tj);
            }
            if (glyphLinePart.getActualText() != null) {
                endMarkedContent();
            } else if (glyphLinePart.isReversed()) {
                endMarkedContent();
            }
            if (glyphLinePart.getEnd() > sub && partIndex + 1 < glyphLineParts.size()) {
                contentStream.getOutputStream()
                        .writeFloat(getSubrangeWidth(text, sub, glyphLinePart.getEnd() - 1), true)
                        .writeSpace()
                        .writeFloat(0)
                        .writeSpace()
                        .writeBytes(Td);
            }
        }
        return this;
    }

    /**
     * Sets whether we are currently drawing on a page.
     *
     * @param drawingOnPage {@code true} if we are currently drawing on page {@code false} if not
     */
    public void setDrawingOnPage(boolean drawingOnPage) {
        this.drawingOnPage = drawingOnPage;
    }

    /**
     * Finds horizontal distance between the start of the `from` glyph and end of `to` glyph.
     * Glyphs with placement are ignored.
     * XAdvance is not taken into account neither before `from` nor after `to` glyphs.
     */
    private float getSubrangeWidth(GlyphLine text, int from, int to) {
        final float fontSize = FontProgram.convertTextSpaceToGlyphSpace(currentGs.getFontSize());
        float charSpacing = currentGs.getCharSpacing();
        float scaling = currentGs.getHorizontalScaling() / 100f;
        float width = 0;
        for (int iter = from; iter <= to; iter++) {
            Glyph glyph = text.get(iter);
            if (!glyph.hasPlacement()) {
                width += (glyph.getWidth() * fontSize + charSpacing + getWordSpacingAddition(glyph)) * scaling;
            }

            if (iter > from) {
                width += text.get(iter - 1).getXAdvance() * fontSize * scaling;
            }

        }
        return width;
    }

    private float getSubrangeYDelta(GlyphLine text, int from, int to) {
        final float fontSize = FontProgram.convertTextSpaceToGlyphSpace(currentGs.getFontSize());
        float yDelta = 0;
        for (int iter = from; iter < to; iter++) {
            yDelta += text.get(iter).getYAdvance() * fontSize;
        }
        return yDelta;
    }

    private float getWordSpacingAddition(Glyph glyph) {
        // From the spec: Word spacing is applied to every occurrence of the single-byte character code 32 in
        // a string when using a simple font or a composite font that defines code 32 as a single-byte code.
        // It does not apply to occurrences of the byte value 32 in multiple-byte codes.
        return !(currentGs.getFont() instanceof PdfType0Font) && glyph.hasValidUnicode() && glyph.getCode() == ' ' ? currentGs.getWordSpacing() : 0;
    }

    /**
     * Shows text (operator TJ)
     *
     * @param textArray the text array. Each element of array can be a string or a number.
     *                  If the element is a string, this operator shows the string.
     *                  If it is a number, the operator adjusts the text position by that amount.
     *                  The number is expressed in thousandths of a unit of text space.
     *                  This amount is subtracted from the current horizontal or vertical coordinate, depending on the writing mode.
     * @return current canvas.
     */
    public PdfCanvas showText(PdfArray textArray) {
        checkDefaultDeviceGrayBlackColor(getColorKeyForText());
        document.checkIsoConformance(new FontGlyphsGStateValidationContext(currentGs, contentStream));
        this.checkIsoConformanceWritingOnContent();
        if (currentGs.getFont() == null) {
            throw new PdfException(
                    KernelExceptionMessageConstant.FONT_AND_SIZE_MUST_BE_SET_BEFORE_WRITING_ANY_TEXT, currentGs);
        }

        contentStream.getOutputStream().writeBytes(ByteUtils.getIsoBytes("["));
        for (PdfObject obj : textArray) {
            if (obj.isString()) {
                StreamUtil.writeEscapedString(contentStream.getOutputStream(), ((PdfString) obj).getValueBytes());
            } else if (obj.isNumber()) {
                contentStream.getOutputStream().writeFloat(((PdfNumber) obj).floatValue());
            }
        }
        contentStream.getOutputStream().writeBytes(ByteUtils.getIsoBytes("]"));
        contentStream.getOutputStream().writeBytes(TJ);
        return this;
    }

    /**
     * Move the current point <i>(x, y)</i>, omitting any connecting line segment.
     *
     * @param x x coordinate.
     * @param y y coordinate.
     * @return current canvas.
     */
    public PdfCanvas moveTo(double x, double y) {
        contentStream.getOutputStream()
                .writeDouble(x)
                .writeSpace()
                .writeDouble(y).writeSpace()
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
    public PdfCanvas lineTo(double x, double y) {
        contentStream.getOutputStream()
                .writeDouble(x)
                .writeSpace()
                .writeDouble(y).writeSpace()
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
    public PdfCanvas curveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        contentStream.getOutputStream()
                .writeDouble(x1)
                .writeSpace()
                .writeDouble(y1)
                .writeSpace()
                .writeDouble(x2)
                .writeSpace()
                .writeDouble(y2)
                .writeSpace()
                .writeDouble(x3)
                .writeSpace()
                .writeDouble(y3)
                .writeSpace()
                .writeBytes(c);
        return this;
    }

    /**
     * Appends a Bezier curve to the path, starting from the current point.
     *
     * @param x2 x coordinate of the second control point.
     * @param y2 y coordinate of the second control point.
     * @param x3 x coordinate of the ending point.
     * @param y3 y coordinate of the ending point.
     * @return current canvas.
     */
    public PdfCanvas curveTo(double x2, double y2, double x3, double y3) {
        contentStream.getOutputStream()
                .writeDouble(x2)
                .writeSpace()
                .writeDouble(y2)
                .writeSpace()
                .writeDouble(x3)
                .writeSpace()
                .writeDouble(y3).writeSpace()
                .writeBytes(v);
        return this;
    }

    /**
     * Appends a Bezier curve to the path, starting from the current point.
     *
     * @param x1 x coordinate of the first control point.
     * @param y1 y coordinate of the first control point.
     * @param x3 x coordinate of the ending point.
     * @param y3 y coordinate of the ending point.
     * @return current canvas.
     */
    public PdfCanvas curveFromTo(double x1, double y1, double x3, double y3) {
        contentStream.getOutputStream()
                .writeDouble(x1)
                .writeSpace()
                .writeDouble(y1)
                .writeSpace()
                .writeDouble(x3)
                .writeSpace()
                .writeDouble(y3).writeSpace()
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
    public PdfCanvas arc(double x1, double y1, double x2, double y2,
                         double startAng, double extent) {
        return drawArc(x1, y1, x2, y2, startAng, extent, false);
    }

    /**
     * Draws a partial ellipse with the preceding line to the start of the arc to prevent path
     * broking. The target arc is inscribed within the rectangle x1,y1,x2,y2, starting
     * at startAng degrees and covering extent degrees. Angles start with 0 to the right (+x)
     * and increase counter-clockwise.
     *
     * @param x1 a corner of the enclosing rectangle
     * @param y1 a corner of the enclosing rectangle
     * @param x2 a corner of the enclosing rectangle
     * @param y2 a corner of the enclosing rectangle
     * @param startAng starting angle in degrees
     * @param extent angle extent in degrees
     *
     * @return the current canvas
     */
    public PdfCanvas arcContinuous(double x1, double y1, double x2, double y2,
            double startAng, double extent) {
        return drawArc(x1, y1, x2, y2, startAng, extent, true);
    }

    /**
     * Draws an ellipse inscribed within the rectangle x1,y1,x2,y2.
     *
     * @param x1 a corner of the enclosing rectangle
     * @param y1 a corner of the enclosing rectangle
     * @param x2 a corner of the enclosing rectangle
     * @param y2 a corner of the enclosing rectangle
     * @return current canvas.
     */
    public PdfCanvas ellipse(double x1, double y1, double x2, double y2) {
        return arc(x1, y1, x2, y2, 0f, 360f);
    }

    /**
     * Generates an array of bezier curves to draw an arc.
     * <br>
     * (x1, y1) and (x2, y2) are the corners of the enclosing rectangle.
     * Angles, measured in degrees, start with 0 to the right (the positive X
     * axis) and increase counter-clockwise.  The arc extends from startAng
     * to startAng+extent.  i.e. startAng=0 and extent=180 yields an openside-down
     * semi-circle.
     * <br>
     * The resulting coordinates are of the form double[]{x1,y1,x2,y2,x3,y3, x4,y4}
     * such that the curve goes from (x1, y1) to (x4, y4) with (x2, y2) and
     * (x3, y3) as their respective Bezier control points.
     * <br>
     * Note: this code was taken from ReportLab (www.reportlab.org), an excellent
     * PDF generator for Python (BSD license: http://www.reportlab.org/devfaq.html#1.3 ).
     *
     * @param x1       a corner of the enclosing rectangle.
     * @param y1       a corner of the enclosing rectangle.
     * @param x2       a corner of the enclosing rectangle.
     * @param y2       a corner of the enclosing rectangle.
     * @param startAng starting angle in degrees.
     * @param extent   angle extent in degrees.
     * @return a list of double[] with the bezier curves.
     */
    public static List<double[]> bezierArc(double x1, double y1, double x2, double y2, double startAng, double extent) {
        return Bezier.bezierArc(x1, y1, x2, y2, startAng, extent);
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
    public PdfCanvas rectangle(double x, double y, double width, double height) {
        contentStream.getOutputStream().writeDouble(x).
                writeSpace().
                writeDouble(y).
                writeSpace().
                writeDouble(width).
                writeSpace().
                writeDouble(height).
                writeSpace().
                writeBytes(re);
        return this;
    }

    /**
     * Draws a rectangle.
     *
     * @param rectangle a rectangle to be drawn
     * @return current canvas.
     */
    public PdfCanvas rectangle(Rectangle rectangle) {
        return rectangle(rectangle.getX(), rectangle.getY(), rectangle.getWidth(), rectangle.getHeight());
    }

    /**
     * Draws rounded rectangle.
     *
     * @param x         x coordinate of the starting point
     * @param y         y coordinate of the starting point
     * @param width     width
     * @param height    height
     * @param radius    radius of the arc corner
     *
     * @return current canvas
     */
    public PdfCanvas roundRectangle(double x, double y, double width, double height, double radius) {
        return roundRectangle(x, y, width, height, radius, radius, null);
    }

    /**
     * Draws rounded rectangle.
     *
     * @param x x coordinate of the starting point
     * @param y y coordinate of the starting point
     * @param width width
     * @param height height
     * @param rx x radius of the arc corner
     * @param ry y radius of the arc corner
     * @param transform {@link AffineTransform} to apply before drawing,
     *                  or {@code null} in case transform shouldn't be applied
     *
     * @return current canvas
     */
    public PdfCanvas roundRectangle(double x, double y, double width, double height, double rx, double ry,
                                    AffineTransform transform) {
        if (width < 0) {
            x += width;
            width = -width;
        }
        if (height < 0) {
            y += height;
            height = -height;
        }
        if (rx < 0) {
            rx = -rx;
        }
        if (ry < 0) {
            ry = -ry;
        }

        double[] points = getEllipseRoundedRectPoints(x, y, width, height, rx, ry);
        if (transform != null) {
            transform.transform(points, 0, points, 0, points.length / 2);
        }

        int i = 0;
        this.moveTo(points[i++], points[i++])
                .lineTo(points[i++], points[i++])
                .curveTo(points[i++], points[i++], points[i++], points[i++], points[i++], points[i++])
                .lineTo(points[i++], points[i++])
                .curveTo(points[i++], points[i++], points[i++], points[i++], points[i++], points[i++])
                .lineTo(points[i++], points[i++])
                .curveTo(points[i++], points[i++], points[i++], points[i++], points[i++], points[i++])
                .lineTo(points[i++], points[i++])
                .curveTo(points[i++], points[i++], points[i++], points[i++], points[i++], points[i])
                .closePath();
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
    public PdfCanvas circle(double x, double y, double r) {
        final double curve = 0.5523f;
        moveTo(x + r, y);
        curveTo(x + r, y + r * curve, x + r * curve, y + r, x, y + r);
        curveTo(x - r * curve, y + r, x - r, y + r * curve, x - r, y);
        curveTo(x - r, y - r * curve, x - r * curve, y - r, x, y - r);
        curveTo(x + r * curve, y - r, x + r, y - r * curve, x + r, y);
        return this;
    }

    /**
     * Paints a shading object and adds it to the resources of this canvas
     *
     * @param shading a shading object to be painted
     * @return current canvas.
     */
    public PdfCanvas paintShading(AbstractPdfShading shading) {
        PdfName shadingName = resources.addShading(shading);
        contentStream.getOutputStream().write((PdfObject) shadingName).writeSpace().writeBytes(sh);
        return this;
    }

    /**
     * Closes the current subpath by appending a straight line segment from the current point
     * to the starting point of the subpath.
     *
     * @return current canvas.
     */
    public PdfCanvas closePath() {
        contentStream.getOutputStream().writeBytes(h);
        return this;
    }

    /**
     * Closes the path, fills it using the even-odd rule to determine the region to fill and strokes it.
     *
     * @return current canvas.
     */
    public PdfCanvas closePathEoFillStroke() {
        checkIsoConformanceWritingOnContent();
        checkDefaultDeviceGrayBlackColor(CheckColorMode.FILL_AND_STROKE);

        contentStream.getOutputStream().writeBytes(bStar);
        return this;
    }

    /**
     * Closes the path, fills it using the non-zero winding number rule to determine the region to fill and strokes it.
     *
     * @return current canvas.
     */
    public PdfCanvas closePathFillStroke() {
        checkDefaultDeviceGrayBlackColor(CheckColorMode.FILL_AND_STROKE);

        contentStream.getOutputStream().writeBytes(b);
        return this;
    }

    /**
     * Ends the path without filling or stroking it.
     *
     * @return current canvas.
     */
    public PdfCanvas endPath() {
        contentStream.getOutputStream().writeBytes(n);
        return this;
    }

    /**
     * Strokes the path.
     *
     * @return current canvas.
     */
    public PdfCanvas stroke() {
        checkDefaultDeviceGrayBlackColor(CheckColorMode.STROKE);

        contentStream.getOutputStream().writeBytes(S);
        return this;
    }

    /**
     * Modify the current clipping path by intersecting it with the current path, using the
     * nonzero winding rule to determine which regions lie inside the clipping path.
     *
     * @return current canvas.
     */
    public PdfCanvas clip() {
        contentStream.getOutputStream().writeBytes(W);
        return this;
    }

    /**
     * Modify the current clipping path by intersecting it with the current path, using the
     * even-odd rule to determine which regions lie inside the clipping path.
     *
     * @return current canvas.
     */
    public PdfCanvas eoClip() {
        contentStream.getOutputStream().writeBytes(WStar);
        return this;
    }

    /**
     * Closes the path and strokes it.
     *
     * @return current canvas.
     */
    public PdfCanvas closePathStroke() {
        checkIsoConformanceWritingOnContent();
        contentStream.getOutputStream().writeBytes(s);
        return this;
    }

    /**
     * Fills current path.
     *
     * @return current canvas.
     */
    public PdfCanvas fill() {
        checkIsoConformanceWritingOnContent();
        checkDefaultDeviceGrayBlackColor(CheckColorMode.FILL);

        contentStream.getOutputStream().writeBytes(f);
        return this;
    }

    /**
     * Fills the path using the non-zero winding number rule to determine the region to fill and strokes it.
     *
     * @return current canvas.
     */
    public PdfCanvas fillStroke() {
        checkIsoConformanceWritingOnContent();
        checkDefaultDeviceGrayBlackColor(CheckColorMode.FILL_AND_STROKE);

        contentStream.getOutputStream().writeBytes(B);
        return this;
    }

    /**
     * EOFills current path.
     *
     * @return current canvas.
     */
    public PdfCanvas eoFill() {
        checkIsoConformanceWritingOnContent();
        checkDefaultDeviceGrayBlackColor(CheckColorMode.FILL);

        contentStream.getOutputStream().writeBytes(fStar);
        return this;
    }

    /**
     * Fills the path, using the even-odd rule to determine the region to fill and strokes it.
     *
     * @return current canvas.
     */
    public PdfCanvas eoFillStroke() {
        checkIsoConformanceWritingOnContent();
        checkDefaultDeviceGrayBlackColor(CheckColorMode.FILL_AND_STROKE);

        contentStream.getOutputStream().writeBytes(BStar);
        return this;
    }

    /**
     * Sets line width.
     *
     * @param lineWidth line width.
     * @return current canvas.
     */
    public PdfCanvas setLineWidth(float lineWidth) {
        if (currentGs.getLineWidth() == lineWidth) {
            return this;
        }
        currentGs.setLineWidth(lineWidth);
        contentStream.getOutputStream()
                .writeFloat(lineWidth).writeSpace()
                .writeBytes(w);
        return this;
    }

    /**
     * Sets the line cap style, the shape to be used at the ends of open subpaths
     * when they are stroked.
     *
     * @param lineCapStyle a line cap style to be set
     * @return current canvas.
     * @see PdfCanvasConstants.LineCapStyle for possible values.
     */
    public PdfCanvas setLineCapStyle(int lineCapStyle) {
        if (currentGs.getLineCapStyle() == lineCapStyle)
            return this;
        currentGs.setLineCapStyle(lineCapStyle);
        contentStream.getOutputStream()
                .writeInteger(lineCapStyle).writeSpace()
                .writeBytes(J);
        return this;
    }

    /**
     * Sets the line join style, the shape to be used at the corners of paths
     * when they are stroked.
     *
     * @param lineJoinStyle a line join style to be set
     * @return current canvas.
     * @see PdfCanvasConstants.LineJoinStyle for possible values.
     */
    public PdfCanvas setLineJoinStyle(int lineJoinStyle) {
        if (currentGs.getLineJoinStyle() == lineJoinStyle)
            return this;
        currentGs.setLineJoinStyle(lineJoinStyle);
        contentStream.getOutputStream()
                .writeInteger(lineJoinStyle).writeSpace()
                .writeBytes(j);
        return this;
    }

    /**
     * Sets the miter limit, a parameter specifying the maximum length a miter join
     * may extend beyond the join point, relative to the angle of the line segments.
     *
     * @param miterLimit a miter limit to be set
     * @return current canvas.
     */
    public PdfCanvas setMiterLimit(float miterLimit) {
        if (currentGs.getMiterLimit() == miterLimit)
            return this;
        currentGs.setMiterLimit(miterLimit);
        contentStream.getOutputStream()
                .writeFloat(miterLimit).writeSpace()
                .writeBytes(M);
        return this;
    }

    /**
     * Changes the value of the <VAR>line dash pattern</VAR>.
     * <br>
     * The line dash pattern controls the pattern of dashes and gaps used to stroke paths.
     * It is specified by an <I>array</I> and a <I>phase</I>. The array specifies the length
     * of the alternating dashes and gaps. The phase specifies the distance into the dash
     * pattern to start the dash.
     *
     * @param phase the value of the phase
     * @return current canvas.
     */
    public PdfCanvas setLineDash(float phase) {
        PdfArray dashPattern = getDashPatternArray(phase);
        currentGs.setDashPattern(dashPattern);
        contentStream.getOutputStream().writeByte('[').writeByte(']').writeSpace()
                .writeFloat(phase).writeSpace()
                .writeBytes(d);
        return this;
    }

    /**
     * Changes the value of the <VAR>line dash pattern</VAR>.
     * <br>
     * The line dash pattern controls the pattern of dashes and gaps used to stroke paths.
     * It is specified by an <I>array</I> and a <I>phase</I>. The array specifies the length
     * of the alternating dashes and gaps. The phase specifies the distance into the dash
     * pattern to start the dash.
     *
     * @param phase   the value of the phase
     * @param unitsOn the number of units that must be 'on' (equals the number of units that must be 'off').
     * @return current canvas.
     */
    public PdfCanvas setLineDash(float unitsOn, float phase) {
        PdfArray dashPattern = getDashPatternArray(new float[]{unitsOn}, phase);
        if (dashPattern == null) {
            return this;
        }
        currentGs.setDashPattern(dashPattern);
        contentStream.getOutputStream().writeByte('[').writeFloat(unitsOn).writeByte(']').writeSpace()
                .writeFloat(phase).writeSpace()
                .writeBytes(d);

        return this;
    }

    /**
     * Changes the value of the <VAR>line dash pattern</VAR>.
     * <br>
     * The line dash pattern controls the pattern of dashes and gaps used to stroke paths.
     * It is specified by an <I>array</I> and a <I>phase</I>. The array specifies the length
     * of the alternating dashes and gaps. The phase specifies the distance into the dash
     * pattern to start the dash.
     *
     * @param phase    the value of the phase
     * @param unitsOn  the number of units that must be 'on'
     * @param unitsOff the number of units that must be 'off'
     * @return current canvas.
     */
    public PdfCanvas setLineDash(float unitsOn, float unitsOff, float phase) {
        PdfArray dashPattern = getDashPatternArray(new float[]{unitsOn, unitsOff}, phase);
        if (dashPattern == null) {
            return this;
        }
        currentGs.setDashPattern(dashPattern);
        contentStream.getOutputStream().writeByte('[').writeFloat(unitsOn).writeSpace()
                .writeFloat(unitsOff).writeByte(']').writeSpace()
                .writeFloat(phase).writeSpace()
                .writeBytes(d);
        return this;
    }

    /**
     * Changes the value of the <VAR>line dash pattern</VAR>.
     * <br>
     * The line dash pattern controls the pattern of dashes and gaps used to stroke paths.
     * It is specified by an <I>array</I> and a <I>phase</I>. The array specifies the length
     * of the alternating dashes and gaps. The phase specifies the distance into the dash
     * pattern to start the dash.
     *
     * @param array length of the alternating dashes and gaps
     * @param phase the value of the phase
     * @return current canvas.
     */
    public PdfCanvas setLineDash(float[] array, float phase) {
        PdfArray dashPattern = getDashPatternArray(array, phase);
        if (dashPattern == null) {
            return this;
        }
        currentGs.setDashPattern(dashPattern);
        PdfOutputStream out = contentStream.getOutputStream();
        out.writeByte('[');
        for (int iter = 0; iter < array.length; iter++) {
            out.writeFloat(array[iter]);
            if (iter < array.length - 1)
                out.writeSpace();
        }
        out.writeByte(']').writeSpace().writeFloat(phase).writeSpace().writeBytes(d);
        return this;
    }

    /**
     * Set the rendering intent. possible values are: PdfName.AbsoluteColorimetric,
     * PdfName.RelativeColorimetric, PdfName.Saturation, PdfName.Perceptual.
     *
     * @param renderingIntent a PdfName containing a color metric
     * @return current canvas.
     */
    public PdfCanvas setRenderingIntent(PdfName renderingIntent) {
        document.checkIsoConformance(new RenderingIntentValidationContext(renderingIntent));
        if (renderingIntent.equals(currentGs.getRenderingIntent()))
            return this;
        currentGs.setRenderingIntent(renderingIntent);
        contentStream.getOutputStream()
                .write(renderingIntent).writeSpace()
                .writeBytes(ri);
        return this;
    }

    /**
     * Changes the Flatness.
     * <p>
     * Flatness sets the maximum permitted distance in device pixels between the
     * mathematically correct path and an approximation constructed from straight line segments.
     *
     * @param flatnessTolerance a value
     * @return current canvas.
     */
    public PdfCanvas setFlatnessTolerance(float flatnessTolerance) {
        if (currentGs.getFlatnessTolerance() == flatnessTolerance)
            return this;
        currentGs.setFlatnessTolerance(flatnessTolerance);
        contentStream.getOutputStream()
                .writeFloat(flatnessTolerance).writeSpace()
                .writeBytes(i);
        return this;
    }

    /**
     * Changes the current color for filling paths.
     *
     * @param color fill color.
     * @return current canvas.
     */
    public PdfCanvas setFillColor(Color color) {
        return setColor(color, true);
    }

    /**
     * Changes the current color for stroking paths.
     *
     * @param color stroke color.
     * @return current canvas.
     */
    public PdfCanvas setStrokeColor(Color color) {
        return setColor(color, false);
    }

    /**
     * Changes the current color for paths.
     *
     * @param color the new color.
     * @param fill  set fill color (<code>true</code>) or stroke color (<code>false</code>)
     * @return current canvas.
     */
    public PdfCanvas setColor(Color color, boolean fill) {
        if (color instanceof PatternColor) {
            return setColor(color.getColorSpace(), color.getColorValue(), ((PatternColor) color).getPattern(), fill);
        } else {
            return setColor(color.getColorSpace(), color.getColorValue(), fill);
        }
    }

    /**
     * Changes the current color for paths.
     *
     * @param colorSpace the color space of the new color
     * @param colorValue a list of numerical values with a length corresponding to the specs of the color space. Values should be in the range [0,1]
     * @param fill       set fill color (<code>true</code>) or stroke color (<code>false</code>)
     * @return current canvas.
     */
    public PdfCanvas setColor(PdfColorSpace colorSpace, float[] colorValue, boolean fill) {
        return setColor(colorSpace, colorValue, null, fill);
    }

    /**
     * Changes the current color for paths with an explicitly defined pattern.
     *
     * @param colorSpace the color space of the new color
     * @param colorValue a list of numerical values with a length corresponding to the specs of the color space. Values should be in the range [0,1]
     * @param pattern    a pattern for the colored line or area
     * @param fill       set fill color (<code>true</code>) or stroke color (<code>false</code>)
     * @return current canvas.
     */
    public PdfCanvas setColor(PdfColorSpace colorSpace, float[] colorValue, PdfPattern pattern, boolean fill) {
        boolean setColorValueOnly = false;
        Color oldColor = fill ? currentGs.getFillColor() : currentGs.getStrokeColor();
        Color newColor = createColor(colorSpace, colorValue, pattern);
        if (oldColor.equals(newColor))
            return this;
        else {
            if (fill) {
                currentGs.setFillColor(newColor);
            } else {
                currentGs.setStrokeColor(newColor);
            }
            if (oldColor.getColorSpace().getPdfObject().equals(colorSpace.getPdfObject())) {
                setColorValueOnly = true;
            }
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
        else if (colorSpace.getPdfObject().isIndirect()) {
            if (!setColorValueOnly) {
                PdfName name = resources.addColorSpace(colorSpace);
                contentStream.getOutputStream().write(name).writeSpace().writeBytes(fill ? cs : CS);
            }
            contentStream.getOutputStream().writeFloats(colorValue).writeSpace().writeBytes(fill ? scn : SCN);
        }
        if (fill) {
            document.checkIsoConformance(new FillColorValidationContext(currentGs, resources, contentStream));
        } else {
            document.checkIsoConformance(new StrokeColorValidationContext(currentGs, resources, contentStream));
        }
        return this;
    }

    /**
     * Changes the current color for filling paths to a grayscale value.
     *
     * @param g a grayscale value in the range [0,1]
     * @return current canvas.
     */
    public PdfCanvas setFillColorGray(float g) {
        return setColor(gray, new float[]{g}, true);
    }

    /**
     * Changes the current color for stroking paths to a grayscale value.
     *
     * @param g a grayscale value in the range [0,1]
     * @return current canvas.
     */
    public PdfCanvas setStrokeColorGray(float g) {
        return setColor(gray, new float[]{g}, false);
    }

    /**
     * Changes the current color for filling paths to black.
     *
     * @return current canvas.
     */
    public PdfCanvas resetFillColorGray() {
        return setFillColorGray(0);
    }

    /**
     * Changes the current color for stroking paths to black.
     *
     * @return current canvas.
     */
    public PdfCanvas resetStrokeColorGray() {
        return setStrokeColorGray(0);
    }

    /**
     * Changes the current color for filling paths to an RGB value.
     *
     * @param r a red value in the range [0,1]
     * @param g a green value in the range [0,1]
     * @param b a blue value in the range [0,1]
     * @return current canvas.
     */
    public PdfCanvas setFillColorRgb(float r, float g, float b) {
        return setColor(rgb, new float[]{r, g, b}, true);
    }

    /**
     * Changes the current color for stroking paths to an RGB value.
     *
     * @param r a red value in the range [0,1]
     * @param g a green value in the range [0,1]
     * @param b a blue value in the range [0,1]
     * @return current canvas.
     */
    public PdfCanvas setStrokeColorRgb(float r, float g, float b) {
        return setColor(rgb, new float[]{r, g, b}, false);
    }

    /**
     * Adds or changes the shading of the current fill color path.
     *
     * @param shading the shading
     * @return current canvas.
     */
    public PdfCanvas setFillColorShading(PdfPattern.Shading shading) {
        return setColor(pattern, null, shading, true);
    }

    /**
     * Adds or changes the shading of the current stroke color path.
     *
     * @param shading the shading
     * @return current canvas.
     */
    public PdfCanvas setStrokeColorShading(PdfPattern.Shading shading) {
        return setColor(pattern, null, shading, false);
    }

    /**
     * Changes the current color for filling paths to black.
     *
     * @return current canvas.
     */
    public PdfCanvas resetFillColorRgb() {
        return resetFillColorGray();
    }

    /**
     * Changes the current color for stroking paths to black.
     *
     * @return current canvas.
     */
    public PdfCanvas resetStrokeColorRgb() {
        return resetStrokeColorGray();
    }

    /**
     * Changes the current color for filling paths to a CMYK value.
     *
     * @param c a cyan value in the range [0,1]
     * @param m a magenta value in the range [0,1]
     * @param y a yellow value in the range [0,1]
     * @param k a key (black) value in the range [0,1]
     * @return current canvas.
     */
    public PdfCanvas setFillColorCmyk(float c, float m, float y, float k) {
        return setColor(cmyk, new float[]{c, m, y, k}, true);
    }

    /**
     * Changes the current color for stroking paths to a CMYK value.
     *
     * @param c a cyan value in the range [0,1]
     * @param m a magenta value in the range [0,1]
     * @param y a yellow value in the range [0,1]
     * @param k a key (black) value in the range [0,1]
     * @return current canvas.
     */
    public PdfCanvas setStrokeColorCmyk(float c, float m, float y, float k) {
        return setColor(cmyk, new float[]{c, m, y, k}, false);
    }

    /**
     * Changes the current color for filling paths to black.
     *
     * @return current canvas.
     */
    public PdfCanvas resetFillColorCmyk() {
        return setFillColorCmyk(0, 0, 0, 1);
    }

    /**
     * Changes the current color for stroking paths to black.
     *
     * @return current canvas.
     */
    public PdfCanvas resetStrokeColorCmyk() {
        return setStrokeColorCmyk(0, 0, 0, 1);
    }

    /**
     * Begins a graphic block whose visibility is controlled by the <CODE>layer</CODE>.
     * Blocks can be nested. Each block must be terminated by an {@link #endLayer()}.<p>
     * Note that nested layers with {@link PdfLayer#addChild(PdfLayer)} only require a single
     * call to this method and a single call to {@link #endLayer()}; all the nesting control
     * is built in.
     *
     * @param layer The layer to begin
     * @return The edited canvas.
     */
    public PdfCanvas beginLayer(IPdfOCG layer) {
        if (layer instanceof PdfLayer && ((PdfLayer) layer).getTitle() != null)
            throw new IllegalArgumentException("Illegal layer argument.");
        if (layerDepth == null)
            layerDepth = new ArrayList<>();
        if (layer instanceof PdfLayerMembership) {
            layerDepth.add(1);
            addToPropertiesAndBeginLayer(layer);
        } else if (layer instanceof PdfLayer) {
            PdfLayer la = (PdfLayer) layer;
            Set<PdfLayer> layers = new HashSet<>();
            int depth = beginLayerTree(la, layers);
            layerDepth.add(depth);
        } else
            throw new UnsupportedOperationException("Unsupported type for operand: layer");
        return this;
    }

    /**
     * Ends OCG layer.
     *
     * @return current canvas.
     */
    public PdfCanvas endLayer() {
        int num;
        if (layerDepth != null && !layerDepth.isEmpty()) {
            num = (int) layerDepth.get(layerDepth.size() - 1);
            layerDepth.remove(layerDepth.size() - 1);
        } else {
            throw new PdfException(KernelExceptionMessageConstant.UNBALANCED_LAYER_OPERATORS);
        }
        while (num-- > 0)
            contentStream.getOutputStream().writeBytes(EMC).writeNewLine();
        return this;
    }

    /**
     * Creates {@link PdfImageXObject} from image and adds it to canvas.
     *
     * <p>
     * The float arguments will be used in concatenating the transformation matrix as operands.
     *
     * <p>
     * Detailed explanation of {@code [a b c d e f]} parameters of transformation
     * matrix can be found in {@link Matrix} documentation.
     *
     * @param image the image from which {@link PdfImageXObject} will be created
     * @param a     an element of the transformation matrix
     * @param b     an element of the transformation matrix
     * @param c     an element of the transformation matrix
     * @param d     an element of the transformation matrix
     * @param e     an element of the transformation matrix
     * @param f     an element of the transformation matrix
     *
     * @return the created imageXObject or null in case of in-line image (asInline = true)
     *
     * @see #concatMatrix(double, double, double, double, double, double)
     */
    public PdfXObject addImageWithTransformationMatrix(ImageData image, float a, float b, float c, float d, float e, float f) {
        checkIsoConformanceWritingOnContent();
        return addImageWithTransformationMatrix(image, a, b, c, d, e, f, false);
    }

    /**
     * Creates {@link PdfImageXObject} from image and adds it to canvas.
     *
     * <p>
     * The float arguments will be used in concatenating the transformation matrix as operands.
     *
     * <p>
     * Detailed explanation of {@code [a b c d e f]} parameters of transformation
     * matrix can be found in {@link Matrix} documentation.
     *
     * @param image    the image from which {@link PdfImageXObject} will be created
     * @param a        an element of the transformation matrix
     * @param b        an element of the transformation matrix
     * @param c        an element of the transformation matrix
     * @param d        an element of the transformation matrix
     * @param e        an element of the transformation matrix
     * @param f        an element of the transformation matrix
     * @param asInline true if to add image as in-line
     *
     * @return the created imageXObject or null in case of in-line image (asInline = true)
     *
     * @see #concatMatrix(double, double, double, double, double, double)
     */
    public PdfXObject addImageWithTransformationMatrix(ImageData image, float a, float b, float c, float d, float e, float f, boolean asInline) {
        checkIsoConformanceWritingOnContent();
        if (image.getOriginalType() == ImageType.WMF) {
            WmfImageHelper wmf = new WmfImageHelper(image);
            PdfXObject xObject = wmf.createFormXObject(document);
            addXObjectWithTransformationMatrix(xObject, a, b, c, d, e, f);
            return xObject;
        } else {
            PdfImageXObject imageXObject = new PdfImageXObject(image);
            if (asInline && image.canImageBeInline()) {
                addInlineImage(imageXObject, a, b, c, d, e, f);
                return null;
            } else {
                addImageWithTransformationMatrix(imageXObject, a, b, c, d, e, f);
                return imageXObject;
            }
        }
    }

    /**
     * Creates {@link PdfImageXObject} from image and fitted into specific rectangle on canvas.
     * The created imageXObject will be fit inside on the specified rectangle without preserving aspect ratio.
     *
     * <p>
     * The x, y, width and height parameters of the rectangle will be used in concatenating
     * the transformation matrix as operands.
     *
     * @param image the image from which {@link PdfImageXObject} will be created
     * @param rect the rectangle in which the created imageXObject will be fit
     * @param asInline true if to add image as in-line
     * @return the created imageXObject or null in case of in-line image (asInline = true)
     * @see #concatMatrix(double, double, double, double, double, double)
     * @see PdfXObject#calculateProportionallyFitRectangleWithWidth(PdfXObject, float, float, float)
     * @see PdfXObject#calculateProportionallyFitRectangleWithHeight(PdfXObject, float, float, float)
     */
    public PdfXObject addImageFittedIntoRectangle(ImageData image, Rectangle rect, boolean asInline) {
        checkIsoConformanceWritingOnContent();
        return addImageWithTransformationMatrix(image, rect.getWidth(), 0, 0, rect.getHeight(),
                rect.getX(), rect.getY(), asInline);
    }

    /**
     * Creates {@link PdfImageXObject} from image and adds it to the specified position.
     *
     * @param image the image from which {@link PdfImageXObject} will be created
     * @param x the horizontal position of the imageXObject
     * @param y the vertical position of the imageXObject
     * @param asInline true if to add image as in-line
     * @return the created imageXObject or null in case of in-line image (asInline = true)
     */
    public PdfXObject addImageAt(ImageData image, float x, float y, boolean asInline) {
        checkIsoConformanceWritingOnContent();
        if (image.getOriginalType() == ImageType.WMF) {
            WmfImageHelper wmf = new WmfImageHelper(image);
            PdfXObject xObject = wmf.createFormXObject(document);
            //For FormXObject args "a" and "d" will become multipliers and will not set the size, as for ImageXObject
            addXObjectWithTransformationMatrix(xObject, 1, 0, 0, 1, x, y);
            return xObject;
        } else {
            PdfImageXObject imageXObject = new PdfImageXObject(image);
            if (asInline && image.canImageBeInline()) {
                addInlineImage(imageXObject, image.getWidth(), 0, 0, image.getHeight(), x, y);
                return null;
            } else {
                addImageWithTransformationMatrix(imageXObject, image.getWidth(), 0, 0, image.getHeight(), x, y);
                return imageXObject;
            }
        }
    }

    /**
     * Adds {@link PdfXObject} to canvas.
     *
     * <p>
     * The float arguments will be used in concatenating the transformation matrix as operands.
     *
     * <p>
     * Detailed explanation of {@code [a b c d e f]} parameters of transformation
     * matrix can be found in {@link Matrix} documentation.
     *
     * @param xObject the xObject to add
     * @param a       an element of the transformation matrix
     * @param b       an element of the transformation matrix
     * @param c       an element of the transformation matrix
     * @param d       an element of the transformation matrix
     * @param e       an element of the transformation matrix
     * @param f       an element of the transformation matrix
     *
     * @return the current canvas
     *
     * @see #concatMatrix(double, double, double, double, double, double)
     */
    public PdfCanvas addXObjectWithTransformationMatrix(PdfXObject xObject, float a, float b, float c, float d, float e, float f) {
        checkIsoConformanceWritingOnContent();
        if (xObject instanceof PdfFormXObject) {
            return addFormWithTransformationMatrix((PdfFormXObject) xObject, a, b, c, d, e, f, true);
        } else if (xObject instanceof PdfImageXObject) {
            return addImageWithTransformationMatrix(xObject, a, b, c, d, e, f);
        } else {
            throw new IllegalArgumentException("PdfFormXObject or PdfImageXObject expected.");
        }
    }

    /**
     * Adds {@link PdfXObject} to the specified position.
     *
     * @param xObject the xObject to add
     * @param x the horizontal position of the xObject
     * @param y the vertical position of the xObject
     * @return the current canvas
     */
    public PdfCanvas addXObjectAt(PdfXObject xObject, float x, float y) {
        checkIsoConformanceWritingOnContent();
        if (xObject instanceof PdfFormXObject) {
            return addFormAt((PdfFormXObject) xObject, x, y);
        } else if (xObject instanceof PdfImageXObject) {
            return addImageAt((PdfImageXObject) xObject, x, y);
        } else {
            throw new IllegalArgumentException("PdfFormXObject or PdfImageXObject expected.");
        }
    }

    /**
     * Adds {@link PdfXObject} fitted into specific rectangle on canvas.
     *
     * @param xObject the xObject to add
     * @param rect the rectangle in which the xObject will be fitted
     * @return the current canvas
     * @see PdfXObject#calculateProportionallyFitRectangleWithWidth(PdfXObject, float, float, float)
     * @see PdfXObject#calculateProportionallyFitRectangleWithHeight(PdfXObject, float, float, float)
     */
    public PdfCanvas addXObjectFittedIntoRectangle(PdfXObject xObject, Rectangle rect) {
        checkIsoConformanceWritingOnContent();
        if (xObject instanceof PdfFormXObject) {
            return addFormFittedIntoRectangle((PdfFormXObject) xObject, rect);
        } else if (xObject instanceof PdfImageXObject) {
            return addImageFittedIntoRectangle((PdfImageXObject) xObject, rect);
        } else {
            throw new IllegalArgumentException("PdfFormXObject or PdfImageXObject expected.");
        }
    }

    /**
     * Adds {@link PdfXObject} on canvas.
     *
     * <p>
     * Note: the {@link PdfImageXObject} will be placed at coordinates (0, 0) with its
     * original width and height, the {@link PdfFormXObject} will be fitted in its bBox.
     *
     * @param xObject the xObject to add
     * @return the current canvas
     */
    public PdfCanvas addXObject(PdfXObject xObject) {
        checkIsoConformanceWritingOnContent();
        if (xObject instanceof PdfFormXObject) {
            return addFormWithTransformationMatrix((PdfFormXObject) xObject, 1, 0, 0, 1, 0, 0, false);
        } else if (xObject instanceof PdfImageXObject) {
            return addImageAt((PdfImageXObject) xObject, 0, 0);
        } else {
            throw new IllegalArgumentException("PdfFormXObject or PdfImageXObject expected.");
        }
    }

    /**
     * Sets the ExtGState dictionary for the current graphics state
     *
     * @param extGState a dictionary that maps resource names to graphics state parameter dictionaries
     * @return current canvas.
     */
    public PdfCanvas setExtGState(PdfExtGState extGState) {
        if (!extGState.isFlushed())
            currentGs.updateFromExtGState(extGState, document);
        PdfName name = resources.addExtGState(extGState);
        contentStream.getOutputStream().write(name).writeSpace().writeBytes(gs);
        document.checkIsoConformance(new ExtendedGStateValidationContext(currentGs, contentStream));
        return this;
    }

    /**
     * Sets the ExtGState dictionary for the current graphics state
     *
     * @param extGState a dictionary that maps resource names to graphics state parameter dictionaries
     * @return current canvas.
     */
    public PdfExtGState setExtGState(PdfDictionary extGState) {
        PdfExtGState egs = new PdfExtGState(extGState);
        setExtGState(egs);
        return egs;
    }

    /**
     * Manually start a Marked Content sequence. Used primarily for Tagged PDF
     *
     * @param tag the type of content contained
     * @return current canvas
     */
    public PdfCanvas beginMarkedContent(PdfName tag) {
        return beginMarkedContent(tag, null);
    }

    /**
     * Manually start a Marked Content sequence with properties. Used primarily for Tagged PDF
     *
     * @param tag        the type of content that will be contained
     * @param properties the properties of the content, including Marked Content ID. If null, the PDF marker is BMC, else it is BDC
     * @return current canvas
     */
    public PdfCanvas beginMarkedContent(PdfName tag, PdfDictionary properties) {
        mcDepth++;
        PdfOutputStream out = contentStream.getOutputStream().write(tag).writeSpace();
        if (properties == null) {
            out.writeBytes(BMC);
        } else if (properties.containsIndirectReference()) {
            out.write(resources.addProperties(properties)).writeSpace().writeBytes(BDC);
        } else {
            out.write(properties).writeSpace().writeBytes(BDC);
        }

        final Tuple2<PdfName, PdfDictionary> tuple2 = new Tuple2<>(tag, properties);
        if (this.drawingOnPage){
            document.checkIsoConformance(new CanvasBmcValidationContext(tagStructureStack, tuple2));
        }
        tagStructureStack.push(tuple2);
        return this;
    }

    /**
     * Manually end a Marked Content sequence. Used primarily for Tagged PDF
     *
     * @return current canvas
     */
    public PdfCanvas endMarkedContent() {
        if (--mcDepth < 0)
            throw new PdfException(KernelExceptionMessageConstant.UNBALANCED_BEGIN_END_MARKED_CONTENT_OPERATORS);
        contentStream.getOutputStream().writeBytes(EMC);
        tagStructureStack.pop();
        return this;
    }

    /**
     * Manually open a canvas tag, beginning a Marked Content sequence. Used primarily for Tagged PDF
     *
     * @param tag the type of content that will be contained
     * @return current canvas
     */
    public PdfCanvas openTag(CanvasTag tag) {
        if (tag.getRole() == null)
            return this;
        return beginMarkedContent(tag.getRole(), tag.getProperties());
    }

    /**
     * Open a tag, beginning a Marked Content sequence. This MC sequence will belong to the tag from the document
     * logical structure.
     * <br>
     * CanvasTag will be automatically created with assigned mcid(Marked Content id) to it. Mcid serves as a reference
     * between Marked Content sequence and logical structure element.
     *
     * @param tagReference reference to the tag from the document logical structure
     * @return current canvas
     */
    public PdfCanvas openTag(TagReference tagReference) {
        if (tagReference.getRole() == null)
            return this;
        CanvasTag tag = new CanvasTag(tagReference.getRole());
        tag.setProperties(tagReference.getProperties())
                .addProperty(PdfName.MCID, new PdfNumber(tagReference.createNextMcid()));
        return openTag(tag);
    }

    /**
     * Manually close a tag, ending a Marked Content sequence. Used primarily for Tagged PDF
     *
     * @return current canvas
     */
    public PdfCanvas closeTag() {
        return endMarkedContent();
    }

    /**
     * Outputs a {@code String} directly to the content.
     *
     * @param s the {@code String}
     * @return current canvas.
     */
    public PdfCanvas writeLiteral(String s) {
        contentStream.getOutputStream().writeString(s);
        return this;
    }

    /**
     * Outputs a {@code char} directly to the content.
     *
     * @param c the {@code char}
     * @return current canvas.
     */
    public PdfCanvas writeLiteral(char c) {
        contentStream.getOutputStream().writeInteger((int) c);
        return this;
    }

    /**
     * Outputs a {@code float} directly to the content.
     *
     * @param n the {@code float}
     * @return current canvas.
     */
    public PdfCanvas writeLiteral(float n) {
        contentStream.getOutputStream().writeFloat(n);
        return this;
    }

    /**
     * Please, use this method with caution and only if you know what you are doing.
     * Manipulating with underlying stream object of canvas could lead to corruption of it's data.
     *
     * @return the content stream to which this canvas object writes.
     */
    public PdfStream getContentStream() {
        return contentStream;
    }

    /**
     * Adds {@code PdfImageXObject} to canvas.
     *
     * <p>
     * Detailed explanation of {@code [a b c d e f]} parameters of transformation
     * matrix can be found in {@link Matrix} documentation.
     *
     * @param imageXObject the {@code PdfImageXObject} object
     * @param a            an element of the transformation matrix
     * @param b            an element of the transformation matrix
     * @param c            an element of the transformation matrix
     * @param d            an element of the transformation matrix
     * @param e            an element of the transformation matrix
     * @param f            an element of the transformation matrix
     */
    protected void addInlineImage(PdfImageXObject imageXObject, float a, float b, float c, float d, float e, float f) {
        document.checkIsoConformance(new InlineImageValidationContext(imageXObject.getPdfObject(), resources));
        checkIsoConformanceWritingOnContent();
        saveState();
        concatMatrix(a, b, c, d, e, f);
        PdfOutputStream os = contentStream.getOutputStream();
        os.writeBytes(BI);
        byte[] imageBytes = imageXObject.getPdfObject().getBytes(false);
        saveColorSpaceToPageResourcesIfNeeded(imageXObject.getPdfObject());
        for (Map.Entry<PdfName, PdfObject> entry : imageXObject.getPdfObject().entrySet()) {
            PdfName key = entry.getKey();
            if (!PdfName.Type.equals(key) && !PdfName.Subtype.equals(key) && !PdfName.Length.equals(key)) {
                os.write(entry.getKey()).writeSpace();
                os.write(entry.getValue()).writeNewLine();
            }
        }
        if (document.getPdfVersion().compareTo(PdfVersion.PDF_2_0) >= 0) {
            os.write(PdfName.Length).writeSpace();
            os.write(new PdfNumber(imageBytes.length)).writeNewLine();
        }
        os.writeBytes(ID);
        os.writeBytes(imageBytes).writeNewLine().writeBytes(EI).writeNewLine();
        restoreState();
    }

    private void saveColorSpaceToPageResourcesIfNeeded(PdfStream image) {
        PdfObject colorSpace = image.get(PdfName.ColorSpace);
        //The colour space specified by the ColorSpace (or CS) entry shall be one of the standard device colour spaces
        //(DeviceGray, DeviceRGB, or DeviceCMYK).
        if (colorSpace == null
         || colorSpace.equals(PdfName.DeviceGray)
         || colorSpace.equals(PdfName.DeviceRGB)
         || colorSpace.equals(PdfName.DeviceCMYK)) {
            return;
        }
        //PDF 1.2: the value of the ColorSpace entry may also be the name of a colour space in the ColorSpace
        //subdictionary of the current resource dictionary. In this case, the name may designate any colour space
        //that can be used with an image XObject.
        PdfName name = resources.addColorSpace(colorSpace);
        image.remove(PdfName.ColorSpace);
        image.put(PdfName.ColorSpace, name);
    }

    /**
     * Adds {@link PdfFormXObject} to canvas.
     *
     * <p>
     * Detailed explanation of {@code [a b c d e f]} parameters of transformation
     * matrix can be found in {@link Matrix} documentation.
     *
     * @param form the formXObject to add
     * @param a an element of the transformation matrix
     * @param b an element of the transformation matrix
     * @param c an element of the transformation matrix
     * @param d an element of the transformation matrix
     * @param e an element of the transformation matrix
     * @param f an element of the transformation matrix
     * @param writeIdentityMatrix true if the matrix is written in any case, otherwise if the
     *                            {@link #isIdentityMatrix(float, float, float, float, float, float)} method indicates
     *                            that the matrix is identity, the matrix will not be written
     *
     * @return current canvas
     */
    private PdfCanvas addFormWithTransformationMatrix(PdfFormXObject form, float a, float b, float c,
            float d, float e, float f, boolean writeIdentityMatrix) {
        saveState();
        if (writeIdentityMatrix || !PdfCanvas.isIdentityMatrix(a, b, c, d, e, f)) {
            concatMatrix(a, b, c, d, e, f);
        }
        PdfName name = resources.addForm(form);
        contentStream.getOutputStream().write(name).writeSpace().writeBytes(Do);
        restoreState();
        return this;
    }

    /**
     * Adds {@link PdfFormXObject} to the specified position.
     *
     * @param form the formXObject to add
     * @param x the horizontal position of the formXObject
     * @param y the vertical position of the formXObject
     * @return the current canvas
     */
    private PdfCanvas addFormAt(PdfFormXObject form, float x, float y) {
        Rectangle bBox = PdfFormXObject.calculateBBoxMultipliedByMatrix(form);
        Vector bBoxMin = new Vector(bBox.getLeft(), bBox.getBottom(), 1);
        Vector bBoxMax = new Vector(bBox.getRight(), bBox.getTop(), 1);
        Vector rectMin = new Vector(x, y, 1);
        Vector rectMax = new Vector(x + bBoxMax.get(Vector.I1) - bBoxMin.get(Vector.I1),
                y + bBoxMax.get(Vector.I2) - bBoxMin.get(Vector.I2), 1);

        float[] result = PdfCanvas.calculateTransformationMatrix(rectMin, rectMax, bBoxMin, bBoxMax);
        return addFormWithTransformationMatrix(form, result[0], result[1], result[2], result[3], result[4], result[5], false);
    }

    /**
     * Adds {@link PdfFormXObject} fitted into specific rectangle on canvas.
     *
     * @param form the formXObject to add
     * @param rect the rectangle in which the formXObject will be fitted
     * @return the current canvas
     */
    private PdfCanvas addFormFittedIntoRectangle(PdfFormXObject form, Rectangle rect) {
        Rectangle bBox = PdfFormXObject.calculateBBoxMultipliedByMatrix(form);
        Vector bBoxMin = new Vector(bBox.getLeft(), bBox.getBottom(), 1);
        Vector bBoxMax = new Vector(bBox.getRight(), bBox.getTop(), 1);
        Vector rectMin = new Vector(rect.getLeft(), rect.getBottom(), 1);
        Vector rectMax = new Vector(rect.getRight(), rect.getTop(), 1);

        float[] result = PdfCanvas.calculateTransformationMatrix(rectMin, rectMax, bBoxMin, bBoxMax);
        return addFormWithTransformationMatrix(form, result[0], result[1], result[2], result[3], result[4], result[5], false);
    }

    /**
     * Adds {@link PdfXObject} to canvas.
     *
     * <p>
     * Detailed explanation of {@code [a b c d e f]} parameters of transformation
     * matrix can be found in {@link Matrix} documentation.
     *
     * @param xObject the xObject to add
     * @param a     an element of the transformation matrix
     * @param b     an element of the transformation matrix
     * @param c     an element of the transformation matrix
     * @param d     an element of the transformation matrix
     * @param e     an element of the transformation matrix
     * @param f     an element of the transformation matrix
     *
     * @return current canvas
     */
    private PdfCanvas addImageWithTransformationMatrix(PdfXObject xObject, float a, float b, float c, float d, float e, float f) {
        saveState();
        concatMatrix(a, b, c, d, e, f);
        PdfName name;
        if (xObject instanceof PdfImageXObject) {
            name = resources.addImage((PdfImageXObject) xObject);
        } else {
            name = resources.addImage(xObject.getPdfObject());
        }
        contentStream.getOutputStream().write(name).writeSpace().writeBytes(Do);
        restoreState();
        return this;
    }

    /**
     * Adds {@link PdfImageXObject} to the specified position.
     *
     * @param image the imageXObject to add
     * @param x the horizontal position of the imageXObject
     * @param y the vertical position of the imageXObject
     * @return the current canvas
     */
    private PdfCanvas addImageAt(PdfImageXObject image, float x, float y) {
        return addImageWithTransformationMatrix(image, image.getWidth(), 0, 0, image.getHeight(), x, y);
    }

    /**
     * Adds {@link PdfImageXObject} fitted into specific rectangle on canvas.
     *
     * @param image the imageXObject to add
     * @param rect the rectangle in which the imageXObject will be fitted
     * @return current canvas
     */
    private PdfCanvas addImageFittedIntoRectangle(PdfImageXObject image, Rectangle rect) {
        return addImageWithTransformationMatrix(image, rect.getWidth(), 0, 0, rect.getHeight(), rect.getX(), rect.getY());
    }

    private PdfStream ensureStreamDataIsReadyToBeProcessed(PdfStream stream) {
        if (!stream.isFlushed()) {
            if (stream.getOutputStream() == null || stream.containsKey(PdfName.Filter)) {
                try {
                    stream.setData(stream.getBytes());
                } catch (MemoryLimitsAwareException e){
                    throw e;
                } catch (Exception e) {
                    // ignored
                }
            }
        }
        return stream;
    }

    /**
     * A helper to insert into the content stream the {@code text}
     * converted to bytes according to the font's encoding.
     *
     * @param text the text to write.
     */
    private void showTextInt(String text) {
        document.checkIsoConformance(new FontGlyphsGStateValidationContext(currentGs, contentStream));
        if (currentGs.getFont() == null) {
            throw new PdfException(
                    KernelExceptionMessageConstant.FONT_AND_SIZE_MUST_BE_SET_BEFORE_WRITING_ANY_TEXT, currentGs);
        }
        this.checkIsoConformanceWritingOnContent();
        checkTextOnAddition(text);
        document.checkIsoConformance(new FontValidationContext(text, currentGs.getFont()));

        currentGs.getFont().writeText(text, contentStream.getOutputStream());
    }

    private void checkIsoConformanceWritingOnContent(){
        if (this.drawingOnPage){
            document.checkIsoConformance(new CanvasWritingContentValidationContext(tagStructureStack));
        }
    }

    private void addToPropertiesAndBeginLayer(IPdfOCG layer) {
        PdfName name = resources.addProperties(layer.getPdfObject());
        contentStream.getOutputStream().write(PdfName.OC).writeSpace()
                .write(name).writeSpace().writeBytes(BDC).writeNewLine();
    }

    private Color createColor(PdfColorSpace colorSpace, float[] colorValue, PdfPattern pattern) {
        if (colorSpace instanceof PdfSpecialCs.UncoloredTilingPattern) {
            return new PatternColor((PdfPattern.Tiling) pattern, ((PdfSpecialCs.UncoloredTilingPattern) colorSpace).getUnderlyingColorSpace(), colorValue);
        } else if (colorSpace instanceof PdfSpecialCs.Pattern) {
            return new PatternColor(pattern);
        }
        return Color.makeColor(colorSpace, colorValue);
    }

    private PdfArray getDashPatternArray(float phase) {
        return getDashPatternArray(null, phase);
    }

    private PdfArray getDashPatternArray(float[] dashArray, float phase) {
        PdfArray dashPatternArray = new PdfArray();
        PdfArray dArray = new PdfArray();
        if (dashArray != null) {
            float sum = 0;
            for (float fl : dashArray) {
                if (fl < 0) {
                    // Negative values are not allowed.
                    return null;
                }
                sum += fl;
                dArray.add(new PdfNumber(fl));
            }
            if (sum < 1e-6) {
                // All 0 values are not allowed.
                return null;
            }
        }
        dashPatternArray.add(dArray);
        dashPatternArray.add(new PdfNumber(phase));
        return dashPatternArray;
    }

    private void applyRotation(PdfPage page) {
        Rectangle rectangle = page.getPageSizeWithRotation();
        int rotation = page.getRotation();
        switch (rotation) {
            case 90:
                concatMatrix(0, 1, -1, 0, rectangle.getTop(), 0);
                break;
            case 180:
                concatMatrix(-1, 0, 0, -1, rectangle.getRight(), rectangle.getTop());
                break;
            case 270:
                concatMatrix(0, -1, 1, 0, 0, rectangle.getRight());
                break;
        }
    }

    private PdfCanvas drawArc(double x1, double y1, double x2, double y2,
            double startAng, double extent, boolean continuous) {
        List<double[]> ar = bezierArc(x1, y1, x2, y2, startAng, extent);
        if (ar.isEmpty()) {
            return this;
        }

        double[] pt = ar.get(0);
        if (continuous) {
            lineTo(pt[0], pt[1]);
        } else {
            moveTo(pt[0], pt[1]);
        }
        for (int index = 0; index < ar.size(); ++index) {
            pt = ar.get(index);
            curveTo(pt[2], pt[3], pt[4], pt[5], pt[6], pt[7]);
        }
        return this;
    }

    private void checkDefaultDeviceGrayBlackColor(CheckColorMode checkColorMode) {
        if (defaultDeviceGrayBlackColorCheckRequired) {
            // It's enough to check DeviceGray.BLACK once for fill color or stroke color
            // But it's still important to do not check fill color if it's not used and vice versa
            if (currentGs.getFillColor() == DeviceGray.BLACK &&
                    (checkColorMode == CheckColorMode.FILL || checkColorMode == CheckColorMode.FILL_AND_STROKE)) {
                document.checkIsoConformance(new FillColorValidationContext(currentGs, resources, contentStream));
                defaultDeviceGrayBlackColorCheckRequired = false;
            } else if (currentGs.getStrokeColor() == DeviceGray.BLACK &&
                    (checkColorMode == CheckColorMode.STROKE || checkColorMode == CheckColorMode.FILL_AND_STROKE)) {
                document.checkIsoConformance(new StrokeColorValidationContext(currentGs, resources, contentStream));
                defaultDeviceGrayBlackColorCheckRequired = false;
            } else {
                // Nothing
            }
        }
    }

    private CheckColorMode getColorKeyForText() {
        switch (currentGs.getTextRenderingMode()) {
            case PdfCanvasConstants.TextRenderingMode.FILL:
            case PdfCanvasConstants.TextRenderingMode.FILL_CLIP:
                return CheckColorMode.FILL;
            case PdfCanvasConstants.TextRenderingMode.STROKE:
            case PdfCanvasConstants.TextRenderingMode.STROKE_CLIP:
                return CheckColorMode.STROKE;
            case PdfCanvasConstants.TextRenderingMode.FILL_STROKE:
            case PdfCanvasConstants.TextRenderingMode.FILL_STROKE_CLIP:
                return CheckColorMode.FILL_AND_STROKE;
            default:
                return CheckColorMode.NONE;
        }
    }

    private static PdfStream getPageStream(PdfPage page) {
        PdfStream stream = page.getLastContentStream();
        return stream == null || stream.getOutputStream() == null || stream.containsKey(PdfName.Filter) ? page.newContentStreamAfter() : stream;
    }

    private static <T> List<T> iteratorToList(Iterator<T> iterator) {
        List<T> list = new ArrayList<>();
        while (iterator.hasNext()) {
            list.add(iterator.next());
        }
        return list;
    }

    private static float[] calculateTransformationMatrix(Vector expectedMin, Vector expectedMax, Vector actualMin, Vector actualMax) {
        // Calculates a matrix such that if you multiply the actual vertices by it, you get the expected vertices
        float[] result = new float[6];
        result[0] = (expectedMin.get(Vector.I1) - expectedMax.get(Vector.I1)) / (actualMin.get(Vector.I1) - actualMax.get(Vector.I1));
        result[1] = 0;
        result[2] = 0;
        result[3] = (expectedMin.get(Vector.I2) - expectedMax.get(Vector.I2)) / (actualMin.get(Vector.I2) - actualMax.get(Vector.I2));
        result[4] = expectedMin.get(Vector.I1) - actualMin.get(Vector.I1) * result[0];
        result[5] = expectedMin.get(Vector.I2) - actualMin.get(Vector.I2) * result[3];
        return result;
    }

    private static boolean isIdentityMatrix(float a, float b, float c, float d, float e, float f) {
        return Math.abs(1 - a) < IDENTITY_MATRIX_EPS && Math.abs(b) < IDENTITY_MATRIX_EPS && Math.abs(c) < IDENTITY_MATRIX_EPS &&
                Math.abs(1 - d) < IDENTITY_MATRIX_EPS && Math.abs(e) < IDENTITY_MATRIX_EPS && Math.abs(f) < IDENTITY_MATRIX_EPS;
    }

    private static double[] getEllipseRoundedRectPoints(double x, double y, double width, double height,
                                                        double rx, double ry) {
        /*

			y+h    ->    ____________________________
						/                            \
					   /                              \
			y+h-ry -> /                                \
					  |                                |
					  |                                |
					  |                                |
					  |                                |
			y+ry   -> \                                /
					   \                              /
			y      ->   \____________________________/
					  ^  ^                          ^  ^
					  x  x+rx                  x+w-rx  x+w

             */

        double[] pt1 = PdfCanvas.bezierArc(x + width - 2 * rx, y, x + width, y + 2 * ry, -90, 90).get(0);
        double[] pt2 = PdfCanvas.bezierArc(x + width, y + height - 2 * ry, x + width - 2 * rx, y + height, 0, 90).get(0);
        double[] pt3 = PdfCanvas.bezierArc(x + 2 * rx, y + height, x, y + height - 2 * ry, 90, 90).get(0);
        double[] pt4 = PdfCanvas.bezierArc(x, y + 2 * ry, x + 2 * rx, y, 180, 90).get(0);

        return new double[]{x + rx, y,
                x + width - rx, y,
                pt1[2], pt1[3], pt1[4], pt1[5], pt1[6], pt1[7],
                x + width, y + height - ry,
                pt2[2], pt2[3], pt2[4], pt2[5], pt2[6], pt2[7],
                x + rx, y + height,
                pt3[2], pt3[3], pt3[4], pt3[5], pt3[6], pt3[7],
                x, y + ry,
                pt4[2], pt4[3], pt4[4], pt4[5], pt4[6], pt4[7]};
    }

    private void checkTextOnAddition(GlyphLine text) {
        checkTextOnAddition(text.toString());
    }

    private void checkTextOnAddition(String text) {
        PdfDictionary attributes = null;
        if (!tagStructureStack.isEmpty()) {
            attributes = tagStructureStack.peek().getSecond();
        }
        document.checkIsoConformance(new CanvasTextAdditionContext(text, attributes, contentStream));
    }

    /**
     * This method is used to traverse parent tree and begin all layers in it.
     * If layer was already begun during method call, it will not be processed again.
     */
    private int beginLayerTree(PdfLayer layer, Set<PdfLayer> layers) {
        if (layer == null || layers.contains(layer)) {
            return 0;
        }

        layers.add(layer);
        int depth = 0;
        if (layer.getTitle() == null) {
            addToPropertiesAndBeginLayer(layer);
            depth++;
        }

        List<PdfLayer> parentLayers = layer.getParents();
        if (parentLayers != null) {
            for (PdfLayer parentLayer : parentLayers) {
                depth += beginLayerTree(parentLayer, layers);
            }
        }

        return depth;
    }

    private enum CheckColorMode {
        NONE,
        FILL,
        STROKE,
        FILL_AND_STROKE
    }
}
