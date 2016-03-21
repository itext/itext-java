package com.itextpdf.kernel.pdf.collection;

import com.itextpdf.kernel.pdf.*;

public class PdfCollection extends PdfObjectWrapper<PdfDictionary> {

    /** A type of initial view */
    public static final int DETAILS = 0;
    /** A type of initial view */
    public static final int TILE = 1;
    /** A type of initial view */
    public static final int HIDDEN = 2;

    public PdfCollection(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Constructs a PDF Collection.
     */
    public PdfCollection() {
        this(new PdfDictionary());
    }

    /**
     * Sets the Collection schema dictionary.
     * @param schema	an overview of the collection fields
     * @return
     */
    public PdfCollection setSchema(PdfCollectionSchema schema) {
        return put(PdfName.Schema, schema);
    }

    public PdfCollectionSchema getSchema(){
        return new PdfCollectionSchema(getPdfObject().getAsDictionary(PdfName.Schema));
    }

    /**
     * Identifies the document that will be initially presented
     * in the user interface.
     * @param documentName a string that identifies an entry in the EmbeddedFiles name tree
     * @return
     */
    public PdfCollection setInitialDocument(String documentName) {
        return put(PdfName.D, new PdfString(documentName));
    }

    public PdfString getInitialDocument() {
        return getPdfObject().getAsString(PdfName.D);
    }

    /**
     * Sets the initial view.
     * @param viewType
     * @return
     */
    public PdfCollection setView(int viewType) {
        switch (viewType) {
            default:
                put(PdfName.View, PdfName.D);
                break;
            case TILE:
                put(PdfName.View, PdfName.T);
                break;
            case HIDDEN:
                put(PdfName.View, PdfName.H);
                break;
        }
        return this;
    }

    public PdfNumber getView() {
        return getPdfObject().getAsNumber(PdfName.View);
    }

    /**
     * Sets the Collection sort dictionary.
     * @param sort
     * @return
     */
    public PdfCollection setSort(PdfCollectionSort sort){
        return put(PdfName.Sort, sort);
    }

    public PdfCollectionSort getSort() {
        return new PdfCollectionSort(getPdfObject().getAsDictionary(PdfName.Sort));
    }


    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
