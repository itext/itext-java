package com.itextpdf.barcodes;

import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDocument;

public abstract class Barcode2D {

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
}


