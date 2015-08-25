package com.itextpdf.barcodes;


import com.itextpdf.basics.PdfException;
import com.itextpdf.basics.font.FontConstants;
import com.itextpdf.canvas.PdfCanvas;
import com.itextpdf.canvas.color.Color;
import com.itextpdf.basics.geom.Rectangle;


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
     * The bars and text are written in the following colors:<p>
     * <P><TABLE BORDER=1>
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
        if (supp.getFont() != null)
            supp.setBarHeight(ean.getBarHeight() + supp.getBaseline() - supp.getFont().getFontProgram().getFontDescriptor(FontConstants.CAPHEIGHT, supp.getSize()));
        else
            supp.setBarHeight(ean.getBarHeight());
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
