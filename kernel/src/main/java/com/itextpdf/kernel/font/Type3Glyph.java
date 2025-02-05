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
package com.itextpdf.kernel.font;

import com.itextpdf.io.source.ByteUtils;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.io.image.ImageData;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.xobject.PdfXObject;

import java.nio.charset.StandardCharsets;

/**
 * The content where Type3 glyphs are written to.
 */
public final class Type3Glyph extends PdfCanvas {

    private static final String D_0_STR = "d0\n";
    private static final String D_1_STR = "d1\n";
    private static final byte[] d0 = ByteUtils.getIsoBytes(D_0_STR);
    private static final byte[] d1 = ByteUtils.getIsoBytes(D_1_STR);

    private float wx;
    private float llx;
    private float lly;
    private float urx;
    private float ury;
    private boolean isColor = false;

    /**
     * Creates a Type3Glyph canvas with a new Content Stream.
     *
     * @param pdfDocument the document that this canvas is created for
     */
    Type3Glyph(PdfDocument pdfDocument, float wx, float llx, float lly, float urx, float ury, boolean isColor) {
        super((PdfStream)new PdfStream().makeIndirect(pdfDocument), null, pdfDocument);
        writeMetrics(wx, llx, lly, urx, ury, isColor);
    }

    /**
     * Creates a Type3Glyph canvas with a non-empty Content Stream.
     *
     * @param pdfStream {@code PdfStream} from existed document.
     * @param document document to which {@code PdfStream} belongs.
     */
    Type3Glyph(PdfStream pdfStream, PdfDocument document) {
        super(pdfStream, null, document);
        if (pdfStream.getBytes() != null) {
            fillBBFromBytes(pdfStream.getBytes());
        }
    }

    public float getWx() {
        return wx;
    }

    public float getLlx() {
        return llx;
    }

    public float getLly() {
        return lly;
    }

    public float getUrx() {
        return urx;
    }

    public float getUry() {
        return ury;
    }

    /**
     * Indicates if the glyph color specified in the glyph description or not.
     *
     * @return whether the glyph color is specified in the glyph description or not
     */
    public boolean isColor() {
        return isColor;
    }

    /**
     * Writes the width and optionally the bounding box parameters for a glyph
     *
     * @param wx      the advance this character will have
     * @param llx     the X lower left corner of the glyph bounding box. If the <CODE>isColor</CODE> option is
     *                <CODE>true</CODE> the value is ignored
     * @param lly     the Y lower left corner of the glyph bounding box. If the <CODE>isColor</CODE> option is
     *                <CODE>true</CODE> the value is ignored
     * @param urx     the X upper right corner of the glyph bounding box. If the <CODE>isColor</CODE> option is
     *                <CODE>true</CODE> the value is ignored
     * @param ury     the Y upper right corner of the glyph bounding box. If the <CODE>isColor</CODE> option is
     *                <CODE>true</CODE> the value is ignored
     * @param isColor defines whether the glyph color is specified in the glyph description in the font.
     *                The consequence of value <CODE>true</CODE> is that the bounding box parameters are ignored.
     */
    private void writeMetrics(float wx, float llx, float lly, float urx, float ury, boolean isColor) {
        this.isColor = isColor;
        this.wx = wx;

        this.llx = llx;
        this.lly = lly;
        this.urx = urx;
        this.ury = ury;

        if (isColor) {
            contentStream.getOutputStream()
                    .writeFloat(wx)
                    .writeSpace()
                    //wy
                    .writeFloat(0)
                    .writeSpace()
                    .writeBytes(d0);
        } else {
            contentStream.getOutputStream()
                    .writeFloat(wx)
                    .writeSpace()
                    //wy
                    .writeFloat(0)
                    .writeSpace()
                    .writeFloat(llx)
                    .writeSpace()
                    .writeFloat(lly)
                    .writeSpace()
                    .writeFloat(urx)
                    .writeSpace()
                    .writeFloat(ury)
                    .writeSpace()
                    .writeBytes(d1);
        }
    }

    /**
     * Creates Image XObject from image and adds it to canvas. Performs additional checks to make
     * sure that we only add mask images to not colorized type 3 fonts.
     *
     * @param image       the {@code PdfImageXObject} object
     * @param a           an element of the transformation matrix
     * @param b           an element of the transformation matrix
     * @param c           an element of the transformation matrix
     * @param d           an element of the transformation matrix
     * @param e           an element of the transformation matrix
     * @param f           an element of the transformation matrix
     * @param inlineImage true if to add image as in-line.
     * @return created Image XObject or null in case of in-line image (asInline = true).
     */
    @Override
    public PdfXObject addImageWithTransformationMatrix(ImageData image, float a, float b, float c, float d, float e, float f, boolean inlineImage) {
        if (!isColor && (!image.isMask() || !(image.getBpc() == 1 || image.getBpc() > 0xff))) {
            throw new PdfException("Not colorized type3 fonts accept only mask images.");
        }
        return super.addImageWithTransformationMatrix(image, a, b, c, d, e, f, inlineImage);
    }

    private void fillBBFromBytes(byte[] bytes) {
        String str = new String(bytes, StandardCharsets.ISO_8859_1);
        int d0Pos = str.indexOf(D_0_STR);
        int d1Pos = str.indexOf(D_1_STR);
        if (d0Pos != -1) {
            isColor = true;
            String[] bbArray = str.substring(0, d0Pos - 1).split(" ");
            if (bbArray.length == 2)
                this.wx = Float.parseFloat(bbArray[0]);
        } else if (d1Pos != -1) {
            isColor = false;
            String[] bbArray = str.substring(0, d1Pos - 1).split(" ");
            if (bbArray.length == 6) {
                this.wx = Float.parseFloat(bbArray[0]);
                this.llx = Float.parseFloat(bbArray[2]);
                this.lly = Float.parseFloat(bbArray[3]);
                this.urx = Float.parseFloat(bbArray[4]);
                this.ury = Float.parseFloat(bbArray[5]);
            }
        }
    }
}
