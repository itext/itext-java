package com.itextpdf.core.pdf.annot;

import com.itextpdf.basics.geom.Rectangle;
import com.itextpdf.core.pdf.PdfDictionary;
import com.itextpdf.core.pdf.PdfName;
import com.itextpdf.core.pdf.PdfObject;
import com.itextpdf.core.pdf.action.PdfAction;
import com.itextpdf.core.pdf.navigation.PdfDestination;

public class PdfLinkAnnotation extends PdfAnnotation {

    /**
     * Highlight modes.
     */
    static public final PdfName None = PdfName.N;
    static public final PdfName Invert = PdfName.I;
    static public final PdfName Outline = PdfName.O;
    static public final PdfName Push = PdfName.P;

    public PdfLinkAnnotation(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfLinkAnnotation(Rectangle rect) {
        super(rect);
    }

    public PdfName getSubtype() {
        return PdfName.Link;
    }

    public PdfObject getDestinationObject() {
        return getPdfObject().get(PdfName.Dest);
    }

    public PdfLinkAnnotation setDestination(PdfObject destination) {
        return put(PdfName.Dest, destination);
    }

    public PdfLinkAnnotation setDestination(PdfDestination destination) {
        return put(PdfName.Dest, destination);
    }

    public PdfDictionary getActionObject() {
        return getPdfObject().getAsDictionary(PdfName.A);
    }

    public PdfLinkAnnotation setAction(PdfDictionary action) {
        return put(PdfName.A, action);
    }

    public PdfLinkAnnotation setAction(PdfAction action) {
        return put(PdfName.A, action);
    }

    public PdfName getHighlightMode() {
        return getPdfObject().getAsName(PdfName.H);
    }

    public PdfLinkAnnotation setHighlightMode(PdfName hlMode) {
        return put(PdfName.H, hlMode);
    }

    public PdfDictionary getUriActionObject() {
        return getPdfObject().getAsDictionary(PdfName.PA);
    }

    public PdfLinkAnnotation setUriAction(PdfDictionary action) {
        return put(PdfName.PA, action);
    }

    public PdfLinkAnnotation setUriAction(PdfAction action) {
        return put(PdfName.PA, action);
    }



}
