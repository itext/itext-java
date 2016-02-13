package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;

public class PdfNode extends PdfObjectWrapper<PdfDictionary> {

    private PdfArray kids;
    private PdfArray names;
    private PdfArray limits;

    /**
     * Creates a Node in the current document
     *
     * @throws PdfException
     */
    public PdfNode() {
        super(new PdfDictionary());
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
    public PdfNode(PdfDictionary pdfDictionary) {
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
    public void addName(PdfObject key, PdfObject value) {
        if (names.size() == 0)
            limits.add(key);

        names.add(key);
        names.add(value);
        markObjectAsIndirect(value);
        String keyValue = ((PdfString) key).toUnicodeString();
        if (limits != null) {
            PdfString limit = limits.getAsString(0);
            if (limit != null) {
                if (keyValue.compareTo(limit.toUnicodeString()) < 0) {
                    limits.set(0, key);
                }
            }
            if (limits.size() == 2) {
                limit = limits.getAsString(1);
                if (keyValue.compareTo(limit.toUnicodeString()) > 0) {
                    limits.set(1, key);
                }
            } else {
                limits.add(key);
            }
        }

        getPdfObject().put(PdfName.Names, names);
        getPdfObject().put(PdfName.Limits, limits);
    }

    /**
     * Creates new Kid in the Kids array of the current node
     *
     * @param kid
     */
    protected void addKid(PdfNode kid) {
        if (null == kids) {
            kids = new PdfArray();
        }
        kids.add(kid.getPdfObject());
        getPdfObject().put(PdfName.Kids, this.kids);
    }

    public PdfArray getKids() {
        return kids;
    }

    public PdfArray getNames() {
        return names;
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return true;
    }
}
