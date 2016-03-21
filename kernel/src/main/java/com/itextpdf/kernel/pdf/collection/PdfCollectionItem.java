package com.itextpdf.kernel.pdf.collection;

import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.*;

public class PdfCollectionItem extends PdfObjectWrapper<PdfDictionary>{

    private PdfCollectionSchema schema;

    public PdfCollectionItem(PdfCollectionSchema schema) {
        super(new PdfDictionary());
        this.schema = schema;
    }

    /**
     * Sets the value of the collection item.
     * @param key
     * @param value
     * @return
     */
    public PdfCollectionItem addItem(String key, String value) {
        PdfCollectionField field = schema.getField(key);
        return put(new PdfName(key), field.getValue(value));
    }

    /**
     * Sets the value of the collection item.
     * @param d
     */
    public void addItem(String key, PdfDate d) {
        PdfCollectionField field = schema.getField(key);
        if (field.subType == PdfCollectionField.DATE) {
            put(new PdfName(key), d);
        }
    }

    /**
     * Sets the value of the collection item.
     * @param n
     */
    public void addItem(String key, PdfNumber n) {
        PdfCollectionField field = schema.getField(key);
        if (field.subType == PdfCollectionField.NUMBER) {
            put(new PdfName(key), n);
        }
    }

    /**
     * Adds a prefix for the Collection item.
     * You can only use this method after you have set the value of the item.
     * @param key
     * @param prefix
     * @return
     */
    public PdfCollectionItem setPrefix(String key, String prefix) {
        PdfName fieldName = new PdfName(key);
        PdfObject obj = getPdfObject().get(fieldName);
        if (obj == null) {
            throw new PdfException(PdfException.YouMustSetAValueBeforeAddingAPrefix);
        }
        PdfDictionary subItem = new PdfDictionary();
        subItem.put(PdfName.D, obj);
        subItem.put(PdfName.P, new PdfString(prefix));
        return put(fieldName, subItem);
    }

    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
