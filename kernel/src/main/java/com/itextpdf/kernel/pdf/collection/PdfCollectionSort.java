package com.itextpdf.kernel.pdf.collection;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.*;

import java.util.Arrays;

public class PdfCollectionSort extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -3871923275239410475L;

	public PdfCollectionSort(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Constructs a PDF Collection Sort Dictionary.
     * @param key the key of the field that will be used to sort entries
     */
    public PdfCollectionSort(String key) {
        this(new PdfDictionary());
        put(PdfName.S, new PdfName(key));
    }

    /**
     * Constructs a PDF Collection Sort Dictionary.
     * @param keys	the keys of the fields that will be used to sort entries
     */
    public PdfCollectionSort(String[] keys) {
        this(new PdfDictionary());
        put(PdfName.S, new PdfArray(Arrays.asList(keys), true));
    }

    /**
     * Defines the sort order of the field (ascending or descending).
     * @param ascending true is the default, use false for descending order
     * @return
     */
    public PdfCollectionSort setSortOrder(boolean ascending) {
        PdfObject obj = getPdfObject().get(PdfName.S);
        if (obj.isName()) {
            put(PdfName.A, new PdfBoolean(ascending));
        } else {
            throw new PdfException(PdfException.YouHaveToDefineABooleanArrayForThisCollectionSortDictionary);
        }
        return this;
    }

    /**
     * Defines the sort order of the field (ascending or descending).
     * @param ascending	an array with every element corresponding with a name of a field.
     * @return
     */
    public PdfCollectionSort setSortOrder(boolean[] ascending) {
        PdfObject obj  = getPdfObject().get(PdfName.S);
        if (obj.isArray()) {
            if (((PdfArray)obj).size() != ascending.length) {
                throw new PdfException(PdfException.TheNumberOfBooleansInTheArrayDoesntCorrespondWithTheNumberOfFields);
            }
            return put(PdfName.A, new PdfArray(ascending));
        } else {
            throw new PdfException(PdfException.YouNeedASingleBooleanForThisCollectionSortDictionary);
        }
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
