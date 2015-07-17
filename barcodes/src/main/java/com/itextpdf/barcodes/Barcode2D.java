package com.itextpdf.barcodes;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfResources;
import com.itextpdf.core.pdf.PdfStream;
import com.itextpdf.core.pdf.xobject.PdfFormXObject;

public abstract class Barcode2D {

    protected static final float DEFAULT_MODULE_WIDTH = 1;
    protected static final float DEFAULT_MODULE_HEIGHT = 1;

    public Barcode2D () {
    }

    /**
     * Gets the maximum area that the barcode and the text, if
     * any, will occupy. The lower left corner is always (0, 0).
     * @return the size the barcode occupies.
     */
    public abstract Rectangle getBarcodeSize();

    /**
     ** Places the barcode in a <CODE>PdfCanvas</CODE>. The
     * barcode is always placed at coordinates (0, 0). Use the
     * translation matrix to move it elsewhere.
     * @param canvas the <CODE>PdfCanvas</CODE> where the barcode will be placed
     * @param foreground the foreground color. It can be <CODE>null</CODE>
     * @return the dimensions the barcode occupies
     */
    public abstract Rectangle placeBarcode(PdfCanvas canvas, Color foreground);

    /** Creates a PdfFormXObject with the barcode.
     * @param document
     * @param foreground the color of the pixels. It can be <CODE>null</CODE>
     * @return the XObject.
     */

    public PdfFormXObject createFormXObject(PdfDocument document, Color foreground) {
        PdfStream stream = new PdfStream(document);
        PdfCanvas canvas = new PdfCanvas(stream, new PdfResources());
        Rectangle rect = placeBarcode(canvas, foreground);

        PdfFormXObject xObject = new PdfFormXObject(document, rect);
        xObject.getPdfObject().getOutputStream().writeBytes(stream.getBytes());
        return xObject;
    }
}


