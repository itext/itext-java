package com.itextpdf.core.pdf.annot;

import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;

public class PdfTextMarkupAnnotation extends PdfMarkupAnnotation {

    /**
     * Subtypes
     */
    public static final PdfName MarkupHighlight = PdfName.Highlight;
    public static final PdfName MarkupUnderline = PdfName.Underline;
    public static final PdfName MarkupStrikeout = PdfName.StrikeOut;
    public static final PdfName MarkupSquiggly = PdfName.Squiggly;

    public PdfTextMarkupAnnotation(Rectangle rect, PdfName subtype, float quadPoints[]) {
        super(rect);
        setSubtype(subtype);
        setQuadPoints(new PdfArray(quadPoints));
    }

    public PdfTextMarkupAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public static PdfTextMarkupAnnotation createHighLight(Rectangle rect, float quadPoints[]) {
        return new PdfTextMarkupAnnotation(rect, MarkupHighlight, quadPoints);
    }

    public static PdfTextMarkupAnnotation createUnderline(Rectangle rect, float quadPoints[]) {
        return new PdfTextMarkupAnnotation(rect, MarkupUnderline, quadPoints);
    }

    public static PdfTextMarkupAnnotation createStrikeout(Rectangle rect, float quadPoints[]) {
        return new PdfTextMarkupAnnotation(rect, MarkupStrikeout, quadPoints);
    }

    public static PdfTextMarkupAnnotation createSquiggly(Rectangle rect, float quadPoints[]) {
        return new PdfTextMarkupAnnotation(rect, MarkupSquiggly, quadPoints);
    }

    @Override
    public PdfName getSubtype() {
        return getPdfObject().getAsName(PdfName.Subtype);
    }

    private void setSubtype(PdfName subtype) {
        put(PdfName.Subtype, subtype);
    }

}
