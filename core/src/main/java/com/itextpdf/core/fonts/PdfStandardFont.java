package com.itextpdf.core.fonts;

import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

/**
 * Nothing here...
 * We do not yet know how the font class should look like.
 */
public class PdfStandardFont extends PdfFont {

    /** This is a possible values of a base 14 type 1 font */
    public static final PdfName Courier = new PdfName("Courier");
    public static final PdfName CourierBold = new PdfName("Courier-Bold");
    public static final PdfName CourierOblique = new PdfName("Courier-Oblique");
    public static final PdfName CourierBoldOblique = new PdfName("Courier-BoldOblique");
    public static final PdfName Helvetica = new PdfName("Helvetica");
    public static final PdfName HelveticaBold = new PdfName("Helvetica-Bold");
    public static final PdfName HelveticaOblique = new PdfName("Helvetica-Oblique");
    public static final PdfName HelveticaBoldOblique = new PdfName("Helvetica-BoldOblique");
    public static final PdfName Symbol = new PdfName("Symbol");
    public static final PdfName TimesRoman = new PdfName("Times-Roman");
    public static final PdfName TimesBold = new PdfName("Times-Bold");
    public static final PdfName TimesItalic = new PdfName("Times-Italic");
    public static final PdfName TimesBoldItalic = new PdfName("Times-BoldItalic");
    public static final PdfName ZapfDingbats = new PdfName("ZapfDingbats");

    public PdfStandardFont(PdfDocument doc, PdfName fontName) {
        super(doc);
        put(PdfName.BaseFont, fontName);
        put(PdfName.Subtype, PdfName.Type1);
        if (!fontName.equals(Symbol)) {
            put(PdfName.Encoding, PdfName.WinAnsiEncoding);
        }
    }
}
