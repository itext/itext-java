package com.itextpdf.core.pdf;

import com.itextpdf.basics.PdfException;

public class PdfObjectWrapper<T extends PdfObject> {

    private T pdfObject = null;

    public PdfObjectWrapper(T pdfObject) {
        this.pdfObject = pdfObject;
    }

    public PdfObjectWrapper(T pdfObject, PdfDocument pdfDocument) throws PdfException {
        this(pdfObject);
        pdfObject.makeIndirect(pdfDocument);
    }

    public T getPdfObject() {
        return pdfObject;
    }

    public PdfObjectWrapper<T> setModified() {
        if (pdfObject != null) {
            pdfObject.setModified();
        }
        return this;
    }

    public void flush() throws PdfException {
        pdfObject.flush();
    }

    public boolean isFlushed() {
        return pdfObject.isFlushed();
    }

    public PdfDocument getDocument() {
        return pdfObject.getDocument();
    }

    public <T1 extends PdfObjectWrapper> T1 copy(PdfDocument document) throws PdfException {
        throw new RuntimeException("Not implemented");
    }

    public <T1 extends PdfObjectWrapper> T1 copy() throws PdfException {
        return copy(getDocument());
    }

    public <T1 extends PdfObjectWrapper<T>> T1 put(PdfName key, PdfObject value) {
        if (value != null) {
            if (getPdfObject().isDictionary() || getPdfObject().isStream())
                ((PdfDictionary) getPdfObject()).put(key, value);
            else
                throw new UnsupportedOperationException();
        }
        return (T1) this;
    }

    public <T1 extends PdfObjectWrapper<T>> T1 put(PdfName key, PdfObjectWrapper value) {
        if (value != null) {
            if (getPdfObject().isDictionary())
                ((PdfDictionary) getPdfObject()).put(key, value.getPdfObject());
            else
                throw new UnsupportedOperationException();
        }
        return (T1) this;
    }

    public <T1 extends PdfObjectWrapper<T>> T1 add(PdfObject value) {
        if (value != null) {
            if (getPdfObject().isArray())
                ((PdfArray) getPdfObject()).add(value);
            else
                throw new UnsupportedOperationException();
        }
        return (T1) this;
    }

    public <T1 extends PdfObjectWrapper<T>> T1 add(int index, PdfObject value) {
        if (value != null) {
            if (getPdfObject().isArray())
                ((PdfArray) getPdfObject()).add(index, value);
            else
                throw new UnsupportedOperationException();
        }
        return (T1) this;
    }

    public <T1 extends PdfObjectWrapper<T>> T1 add(PdfObjectWrapper value) {
        if (value != null) {

            if (getPdfObject().isArray())
                ((PdfArray) getPdfObject()).add(value.getPdfObject());
            else
                throw new UnsupportedOperationException();
        }
        return (T1) this;
    }

    public <T1 extends PdfObjectWrapper<T>> T1 add(int index, PdfObjectWrapper value) {
        if (value != null) {
            if (getPdfObject().isArray())
                ((PdfArray) getPdfObject()).add(index, value.getPdfObject());
            else
                throw new UnsupportedOperationException();
        }
        return (T1) this;
    }

}
