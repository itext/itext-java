package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfArray;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfString;

public class PdfFreeTextAnnotation extends PdfMarkupAnnotation {

    /**
     * Text justification options.
     */
    public static final int LeftJustified = 0;
    public static final int Centered = 1;
    public static final int RightJustified = 2;

    public PdfFreeTextAnnotation(Rectangle rect, String appearanceString) {
        super(rect);
        setDrawnAfter(new PdfString(appearanceString));
    }

    public PdfFreeTextAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    @Override
    public PdfName getSubtype() {
        return PdfName.FreeText;
    }

    public PdfString getDefaultStyleString() {
        return getPdfObject().getAsString(PdfName.DS);
    }

    public PdfFreeTextAnnotation setDefaultStyleString(PdfString defaultStyleString) {
        return put(PdfName.DS, defaultStyleString);
    }

    public PdfArray getCalloutLine() {
        return getPdfObject().getAsArray(PdfName.CL);
    }

    public PdfFreeTextAnnotation setCalloutLine(float[] calloutLine) {
        return setCalloutLine(new PdfArray(calloutLine));
    }

    public PdfFreeTextAnnotation setCalloutLine(PdfArray calloutLine) {
        return put(PdfName.CL, calloutLine);
    }

    public PdfName getLineEndingStyle() {
        return getPdfObject().getAsName(PdfName.LE);
    }

    public PdfFreeTextAnnotation setLineEndingStyle(PdfName lineEndingStyle) {
        return put(PdfName.LE, lineEndingStyle);
    }


}
