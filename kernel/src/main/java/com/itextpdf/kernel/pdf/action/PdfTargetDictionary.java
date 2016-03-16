package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.*;

public class PdfTargetDictionary extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -5814265943827690509L;

	public PdfTargetDictionary(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfTargetDictionary(PdfName r) {
        this(new PdfDictionary());
        put(PdfName.R, r);
    }

    public PdfTargetDictionary(PdfName r, PdfString n, PdfObject p, PdfObject a, PdfTargetDictionary t) {
        this(new PdfDictionary());
        put(PdfName.R, r).put(PdfName.N, n).
                put(PdfName.P, p).
                put(PdfName.A, a).put(PdfName.T, t);
    }

    /**
     * Sets the name of the file in the EmbeddedFiles name tree.
     * @param name the name of the file
     * @return
     */
    public PdfTargetDictionary setName(String name) {
        return put(PdfName.N, new PdfString(name));
    }

    /**
     * Gets name of the file
     * @return
     */
    public PdfString getName() {
        return getPdfObject().getAsString(PdfName.N);
    }

    /**
     * Sets the page number in the current document containing the file attachment annotation.
     * @param pageNumber
     * @return
     */
    public PdfTargetDictionary setPage(int pageNumber) {
        return put(PdfName.P, new PdfNumber(pageNumber));
    }

    /**
     * Sets a named destination in the current document that provides the page number of the file attachment annotation.
     * @param namedDestination
     * @return
     */
    public PdfTargetDictionary setPage(String namedDestination) {
        return put(PdfName.P, new PdfString(namedDestination));
    }

    /**
     * Get the page number or a named destination that provides the page number containing the file attachment annotation
     * @return
     */
    public PdfObject getPage() {
        return getPdfObject().get(PdfName.P);
    }

    /**
     * Sets the index of the annotation in Annots array of the page specified by /P entry.
     * @param annotNumber
     * @return
     */
    public PdfTargetDictionary setAnnotation(int annotNumber) {
        return put(PdfName.A, new PdfNumber(annotNumber));
    }

    /**
     * Sets the text value, which specifies the value of the /NM entry in the annotation dictionary.
     * @param annotationName
     * @return
     */
    public PdfTargetDictionary setAnnotation(String annotationName) {
        return put(PdfName.A, new PdfString(annotationName));
    }

    public PdfObject getAnnotation() {
        return getPdfObject().get(PdfName.A);
    }

    /**
     * Sets a target dictionary specifying additional path information to the target document.
     * @param target
     * @return
     */
    public PdfTargetDictionary setTarget(PdfTargetDictionary target) {
        return put(PdfName.T, target);
    }

    public PdfTargetDictionary getTarget() {
        return new PdfTargetDictionary(getPdfObject().getAsDictionary(PdfName.T));
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
