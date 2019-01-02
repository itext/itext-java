/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.barcodes;


import com.itextpdf.kernel.PdfException;
import com.itextpdf.io.font.FontProgram;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.colors.Color;


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
        n = 8; // horizontal distance between the two barcodes
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
     * <TABLE BORDER=1 SUMMARY="barcode properties">
     * <TR>
     * <TH><P><CODE>barColor</CODE></TH>
     * <TH><P><CODE>textColor</CODE></TH>
     * <TH><P>Result</TH>
     * </TR>
     * <TR>
     * <TD><P><CODE>null</CODE></TD>
     * <TD><P><CODE>null</CODE></TD>
     * <TD><P>bars and text painted with current fill color</TD>
     * </TR>
     * <TR>
     * <TD><P><CODE>barColor</CODE></TD>
     * <TD><P><CODE>null</CODE></TD>
     * <TD><P>bars and text painted with <CODE>barColor</CODE></TD>
     * </TR>
     * <TR>
     * <TD><P><CODE>null</CODE></TD>
     * <TD><P><CODE>textColor</CODE></TD>
     * <TD><P>bars painted with current color<br>text painted with <CODE>textColor</CODE></TD>
     * </TR>
     * <TR>
     * <TD><P><CODE>barColor</CODE></TD>
     * <TD><P><CODE>textColor</CODE></TD>
     * <TD><P>bars painted with <CODE>barColor</CODE><br>text painted with <CODE>textColor</CODE></TD>
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
        if (supp.getFont() != null) {
            float sizeCoef = supp.getSize() / FontProgram.UNITS_NORMALIZATION;
            supp.setBarHeight(ean.getBarHeight() + supp.getBaseline()
                    - supp.getFont().getFontProgram().getFontMetrics().getCapHeight() * sizeCoef);
        } else {
            supp.setBarHeight(ean.getBarHeight());
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

    // AWT related methods (remove this if you port to Android / GAE)

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
        throw new UnsupportedOperationException(PdfException.TwoBarcodeMustBeExternally);
    }
}
