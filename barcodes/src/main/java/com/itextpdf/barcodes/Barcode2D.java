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
package com.itextpdf.barcodes;

import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;

/**
 * This is a class that is used to implement the logic common to all 2D barcodes.
 * A 2D barcode is a barcode with two dimensions; this means that
 * data can be encoded vertically and horizontally.
 */
public abstract class Barcode2D {

    protected static final float DEFAULT_MODULE_SIZE = 1;

    /**
     * Gets the maximum area that the barcode and the text, if
     * any, will occupy. The lower left corner is always (0, 0).
     *
     * @return the size the barcode occupies.
     */
    public abstract Rectangle getBarcodeSize();

    /**
     * Places the barcode in a <CODE>PdfCanvas</CODE>. The
     * barcode is always placed at coordinates (0, 0). Use the
     * translation matrix to move it elsewhere.
     *
     * @param canvas     the <CODE>PdfCanvas</CODE> where the barcode will be placed
     * @param foreground the foreground color. It can be <CODE>null</CODE>
     * @return the dimensions the barcode occupies
     */
    public abstract Rectangle placeBarcode(PdfCanvas canvas, Color foreground);

    /**
     * Creates a PdfFormXObject with the barcode.
     * Default foreground color will be used.
     *
     * @param document The document
     * @return the XObject.
     */
    public PdfFormXObject createFormXObject(PdfDocument document) {
        return createFormXObject(null, document);
    }

    /**
     * Creates a PdfFormXObject with the barcode.
     *
     * @param foreground    The color of the pixels. It can be <CODE>null</CODE>
     * @param document      The document
     * @return the XObject.
     */
    public abstract PdfFormXObject createFormXObject(Color foreground, PdfDocument document);
}


