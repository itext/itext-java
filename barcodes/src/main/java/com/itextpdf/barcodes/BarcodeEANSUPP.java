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

import com.itextpdf.barcodes.exceptions.BarcodesExceptionMessageConstant;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.kernel.colors.Color;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;

/**
 * Implements the most commonly used EAN standard is the thirteen-digit EAN-13, a superset of the original 12-digit
 * Universal Product Code (UPC-A)
 */
public class BarcodeEANSUPP extends Barcode1D {

    /**
     * The barcode with the EAN/UPC.
     */
    protected Barcode1D ean;
    /**
     * The barcode with the supplemental.
     */
    protected Barcode1D supp;

    /**
     * Creates new combined barcode.
     *
     * @param ean  the EAN/UPC barcode
     * @param supp the supplemental barcode
     */
    public BarcodeEANSUPP(Barcode1D ean, Barcode1D supp) {
        super(ean.document);
        // horizontal distance between the two barcodes
        n = 8;
        this.ean = ean;
        this.supp = supp;
    }

    /**
     * Gets the maximum area that the barcode and the text, if
     * any, will occupy. The lower left corner is always (0, 0).
     *
     * @return the size the barcode occupies.
     */
    public Rectangle getBarcodeSize() {
        Rectangle rect = ean.getBarcodeSize();
        rect.setWidth(rect.getWidth() + supp.getBarcodeSize().getWidth() + n);
        return rect;
    }

    /**
     * Places the barcode in a <CODE>PdfCanvas</CODE>. The
     * barcode is always placed at coordinates (0, 0). Use the
     * translation matrix to move it elsewhere.<p>
     * The bars and text are written in the following colors:
     * <br>
     * <TABLE BORDER="1" SUMMARY="barcode properties">
     * <TR>
     * <TH><CODE>barColor</CODE></TH>
     * <TH><CODE>textColor</CODE></TH>
     * <TH>Result</TH>
     * </TR>
     * <TR>
     * <TD><CODE>null</CODE></TD>
     * <TD><CODE>null</CODE></TD>
     * <TD>bars and text painted with current fill color</TD>
     * </TR>
     * <TR>
     * <TD><CODE>barColor</CODE></TD>
     * <TD><CODE>null</CODE></TD>
     * <TD>bars and text painted with <CODE>barColor</CODE></TD>
     * </TR>
     * <TR>
     * <TD><CODE>null</CODE></TD>
     * <TD><CODE>textColor</CODE></TD>
     * <TD>bars painted with current color<br>text painted with <CODE>textColor</CODE></TD>
     * </TR>
     * <TR>
     * <TD><CODE>barColor</CODE></TD>
     * <TD><CODE>textColor</CODE></TD>
     * <TD>bars painted with <CODE>barColor</CODE><br>text painted with <CODE>textColor</CODE></TD>
     * </TR>
     * </TABLE>
     *
     * @param canvas    the <CODE>PdfCanvas</CODE> where the barcode will be placed
     * @param barColor  the color of the bars. It can be <CODE>null</CODE>
     * @param textColor the color of the text. It can be <CODE>null</CODE>
     * @return the dimensions the barcode occupies
     */
    @Override
    public Rectangle placeBarcode(PdfCanvas canvas, Color barColor, Color textColor) {
        if (supp.getFont() == null) {
            supp.setBarHeight(ean.getBarHeight());
        } else {
            final float sizeCoefficient = FontProgram.convertTextSpaceToGlyphSpace(supp.getSize());
            supp.setBarHeight(ean.getBarHeight() + supp.getBaseline()
                    - supp.getFont().getFontProgram().getFontMetrics().getCapHeight() * sizeCoefficient);
        }
        Rectangle eanR = ean.getBarcodeSize();
        canvas.saveState();
        ean.placeBarcode(canvas, barColor, textColor);
        canvas.restoreState();
        canvas.saveState();
        canvas.concatMatrix(1, 0, 0, 1, eanR.getWidth() + n, eanR.getHeight() - ean.getBarHeight());
        supp.placeBarcode(canvas, barColor, textColor);
        canvas.restoreState();
        return getBarcodeSize();
    }

    // Android-Conversion-Skip-Block-Start (java.awt library isn't available on Android)
    /**
     * Creates a <CODE>java.awt.Image</CODE>. This image only
     * contains the bars without any text.
     *
     * @param foreground the color of the bars
     * @param background the color of the background
     * @return the image
     */
    @Override
    public java.awt.Image createAwtImage(java.awt.Color foreground, java.awt.Color background) {
        throw new UnsupportedOperationException(BarcodesExceptionMessageConstant.TWO_BARCODE_MUST_BE_EXTERNALLY);
    }
    // Android-Conversion-Skip-Block-End
}
