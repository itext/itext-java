package com.itextpdf.kernel.pdf;

public abstract class PdfObjectWrapper<T extends PdfObject> {

    private T pdfObject = null;

    public PdfObjectWrapper(T pdfObject) {
        this.pdfObject = pdfObject;
    }

    public T getPdfObject() {
        return pdfObject;
    }

    /**
     * Marks object behind wrapper to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    public <T1 extends PdfObjectWrapper<T>> T1 makeIndirect(PdfDocument document, PdfIndirectReference reference) {
        getPdfObject().makeIndirect(document, reference);
        return (T1) this;
    }

    /**
     * Marks object behind wrapper to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    public <T1 extends PdfObjectWrapper<T>> T1 makeIndirect(PdfDocument document) {
        return makeIndirect(document, null);
    }

    public PdfObjectWrapper<T> setModified() {
        if (pdfObject != null) {
            pdfObject.setModified();
        }
        return this;
    }

    public void flush() {
        pdfObject.flush();
    }

    public boolean isFlushed() {
        return pdfObject.isFlushed();
    }

    public PdfDocument getDocument() {
        return pdfObject.getDocument();
    }

    public <T1 extends PdfObjectWrapper> T1 copyTo(PdfDocument document) {
        throw new RuntimeException("Not implemented");
    }

//    @Override
//    public PdfObjectWrapper clone() {
//        return copyTo(getDocument());
//    }

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

    public <T1 extends PdfObjectWrapper<T>> T1 remove(PdfName key) {
        if (getPdfObject().isDictionary()) {
            ((PdfDictionary) getPdfObject()).remove(key);
        } else {
            throw new UnsupportedOperationException();
        }

        return (T1) this;
    }
    protected void setPdfObject(T pdfObject){
        this.pdfObject = pdfObject;
    }

    protected void mustBeIndirect() {
        if (pdfObject.getIndirectReference() == null) {
            pdfObject.setState(PdfObject.MustBeIndirect);
        }
    }

    //TODO or move that method to PdfObject?
    protected void markObjectAsIndirect(PdfObject pdfObject) {
        if (pdfObject.getIndirectReference() == null) {
            pdfObject.setState(PdfObject.MustBeIndirect);
        }
    }
}
