package com.itextpdf.kernel.pdf.collection;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;

public class PdfCollectionSchema extends PdfObjectWrapper<PdfDictionary>{

	private static final long serialVersionUID = -4388183665435879535L;

	public PdfCollectionSchema(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates a Collection Schema dictionary.
     */
    public PdfCollectionSchema() {
        this(new PdfDictionary());
    }

    /**
     * Adds a Collection field to the Schema.
     * @param name the name of the collection field
     * @param field a Collection Field
     * @return
     */
    public PdfCollectionSchema addField(String name, PdfCollectionField field) {
        return put(new PdfName(name), field);
    }

    public PdfCollectionField getField(String name) {
        return new PdfCollectionField(getPdfObject().getAsDictionary(new PdfName(name)));
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
