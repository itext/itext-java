package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfDocument;
import com.itextpdf.core.pdf.PdfName;

public class PdfTextMarkupAnnotation extends PdfMarkupAnnotation {

    /**
     * Subtypes
     */
    public static final PdfName MarkupHighlight = PdfName.Highlight;
    public static final PdfName MarkupUnderline = PdfName.Underline;
    public static final PdfName MarkupStrikeout = PdfName.StrikeOut;
    public static final PdfName MarkupSquiggly = PdfName.Squiggly;

    public PdfTextMarkupAnnotation(PdfDocument document, Rectangle rect, PdfName subtype, float quadPoints[]) {
        super(document, rect);
        setSubtype(subtype);
        setQuadPoints(new PdfArray(quadPoints));
    }

    public PdfTextMarkupAnnotation(PdfDictionary pdfObject, PdfDocument document) {
        super(pdfObject, document);
    }

    static public PdfTextMarkupAnnotation createHighLight(PdfDocument document, Rectangle rect, float quadPoints[]) {
        return new PdfTextMarkupAnnotation(document, rect, MarkupHighlight, quadPoints);
    }

    static public PdfTextMarkupAnnotation createUnderline(PdfDocument document, Rectangle rect, float quadPoints[]) {
        return new PdfTextMarkupAnnotation(document, rect, MarkupUnderline, quadPoints);
    }

    static public PdfTextMarkupAnnotation createStrikeout(PdfDocument document, Rectangle rect, float quadPoints[]) {
        return new PdfTextMarkupAnnotation(document, rect, MarkupStrikeout, quadPoints);
    }

    static public PdfTextMarkupAnnotation createSquiggly(PdfDocument document, Rectangle rect, float quadPoints[]) {
        return new PdfTextMarkupAnnotation(document, rect, MarkupSquiggly, quadPoints);
    }

    @Override
    public PdfName getSubtype() {
        return getPdfObject().getAsName(PdfName.Subtype);
    }

    private void setSubtype(PdfName subtype){
        put(PdfName.Subtype, subtype);
    }

}
