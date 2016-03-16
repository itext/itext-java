package com.itextpdf.kernel.pdf.annot;

import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;

public class PdfFreeTextAnnotation extends PdfMarkupAnnotation {

    private static final long serialVersionUID = -7835504102518915220L;
	/**
     * Text justification options.
     */
    public static final int LeftJustified = 0;
    public static final int Centered = 1;
    public static final int RightJustified = 2;

    public PdfFreeTextAnnotation(Rectangle rect, String appearanceString) {
        super(rect);
        setDefaultAppearance(new PdfString(appearanceString));
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
