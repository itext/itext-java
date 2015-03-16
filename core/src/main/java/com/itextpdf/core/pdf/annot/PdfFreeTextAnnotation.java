package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.PdfException;
import com.itextpdf.core.geom.Rectangle;
import com.itextpdf.core.pdf.*;

public class PdfFreeTextAnnotation extends PdfMarkupAnnotation {

    /**
     * Text justification options.
     */
    public static final int LeftJustified = 0;
    public static final int Centered = 1;
    public static final int RightJustified = 2;

    public PdfFreeTextAnnotation(PdfDocument document, Rectangle rect, String appearanceString) throws PdfException {
        super(document, rect);
        put(PdfName.DA, new PdfString(appearanceString));
    }

    public PdfFreeTextAnnotation(PdfDictionary pdfObject, PdfDocument document) throws PdfException {
        super(pdfObject, document);
    }

    @Override
    public PdfName getSubtype() throws PdfException {
        return PdfName.FreeText;
    }

    public int getJustification() throws PdfException {
        PdfNumber q = getPdfObject().getAsNumber(PdfName.Q);
        return q == null ? 0 : q.getIntValue();
    }

    public PdfFreeTextAnnotation setJustification(int justification) {
        return put(PdfName.Q, new PdfNumber(justification));
    }

    public PdfString getDefaultStyleString() throws PdfException {
        return getPdfObject().getAsString(PdfName.DS);
    }

    public PdfFreeTextAnnotation setDefaultStyleString(PdfString defaultStyleString) {
        return put(PdfName.DS, defaultStyleString);
    }

    public PdfArray getCalloutLine() throws PdfException {
        return getPdfObject().getAsArray(PdfName.CL);
    }

    public PdfFreeTextAnnotation setCalloutLine(float[] calloutLine) {
        return setCalloutLine(new PdfArray(calloutLine));
    }

    public PdfFreeTextAnnotation setCalloutLine(PdfArray calloutLine) {
        return put(PdfName.CL, calloutLine);
    }

    public PdfName getLineEndingStyle() throws PdfException {
        return getPdfObject().getAsName(PdfName.LE);
    }

    public PdfFreeTextAnnotation setLineEndingStyle(PdfName lineEndingStyle) {
        return put(PdfName.LE, lineEndingStyle);
    }


}
