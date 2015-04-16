package com.itextpdf.canvas.font;

import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.image.Image;
import com.itextpdf.basics.io.OutputStream;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;
import com.itextpdf.core.pdf.xobject.PdfXObject;

/**
 * The content where Type3 glyphs are written to.
 */
public class Type3Glyph extends PdfCanvas {

    static final private byte[] d0 = OutputStream.getIsoBytes("d0\n");
    static final private byte[] d1 = OutputStream.getIsoBytes("d1\n");
    private boolean isColor = false;

    public Type3Glyph(PdfDocument pdfDocument) throws PdfException {
        super(new PdfStream(pdfDocument), null);
    }

    public void writeMetrics(float wx, float llx, float lly, float urx, float ury, boolean isColor) throws PdfException {
        this.isColor = isColor;
        if (isColor) {
            contentStream.getOutputStream()
                    .writeFloat(wx)
                    .writeSpace()
                    .writeInteger(0)
                    .writeSpace()
                    .writeBytes(d0);

        } else {
            contentStream.getOutputStream()
                    .writeFloat(wx)
                    .writeSpace()
                    .writeInteger(0)
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

    public PdfXObject addImage(Image image, float a, float b, float c, float d, float e, float f, boolean inlineImage) throws PdfException {
        if (!isColor && (!image.isMask() || !(image.getBpc() == 1 || image.getBpc() > 0xff))) {
            throw new PdfException("not.colorized.typed3.fonts.only.accept.mask.images");
        }

        return super.addImage(image, a, b, c, d, e, f, inlineImage);
    }

    public PdfStream getContentStream() {
        return contentStream;
    }

}
