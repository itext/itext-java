package com.itextpdf.core.pdf;


import com.itextpdf.basics.PdfException;

public class PdfNode extends PdfObjectWrapper<PdfDictionary> {

    private PdfArray kids;
    private PdfArray names;
    private PdfArray limits;
    private PdfDocument document;

    /**
     * Creates a Node in the current document
     *
     * @param pdfDocument
     * @throws PdfException
     */
    public PdfNode(PdfDocument pdfDocument) throws PdfException {
        super(new PdfDictionary());
        this.document = pdfDocument;
        getPdfObject().makeIndirect(document);
        kids = new PdfArray();
        names = new PdfArray();
        limits = new PdfArray();
    }

    /**
     * Creates root node in the current document
     *
     * @param pdfDictionary
     * @throws PdfException
     */
    public PdfNode(PdfDictionary pdfDictionary) throws PdfException {
        super(pdfDictionary);
        kids = pdfDictionary.getAsArray(PdfName.Kids);
        names = pdfDictionary.getAsArray(PdfName.Names);
        limits = pdfDictionary.getAsArray(PdfName.Limits);
    }

    /**
     * Creates new Name in the Names array of the current Node
     *
     * @param key
     * @param value
     * @throws PdfException
     */
    public void addName(PdfObject key, PdfObject value) throws PdfException {
        if (names.size() == 0)
            limits.add(key);

        names.add(key);
        PdfDictionary dict = new PdfDictionary();
        dict.makeIndirect(this.document);
        dict.put(PdfName.D, value);
        names.add(dict.getIndirectReference());
        if (limits.size() == 2)
            limits.remove(1);
        limits.add(1, key);
        getPdfObject().put(PdfName.Names, names);
        getPdfObject().put(PdfName.Limits, limits);
    }

    /**
     * Creates new Kid in the Kids array of the current node
     *
     * @param kid
     */
    protected void addKid(PdfNode kid) {
        kids.add(kid.getPdfObject());
        getPdfObject().put(PdfName.Kids, this.kids);
    }

    public PdfArray getKids() {
        return kids;
    }

    public PdfArray getNames() {
        return names;
    }
}
