package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;

public abstract class PdfObjectWrapper<T extends PdfObject> {

    private T pdfObject = null;

    public PdfObjectWrapper(T pdfObject) {
        this.pdfObject = pdfObject;
        if (isWrappedObjectMustBeIndirect()) {
            markObjectAsIndirect(this.pdfObject);
        }
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

    /**
     * Defines if the object behind this wrapper must be an indirect object in the
     * resultant document.
     * <br><br>
     * If this method returns <i>true</i> it doesn't necessarily mean that object
     * must be in the indirect state at any moment, but rather defines that
     * when the object will be written to the document it will be transformed into
     * indirect object if it's not indirect yet.
     * <br><br>
     * Return value of this method shouldn't depend on any logic, it should return
     * always <i>true</i> or <i>false</i>.
     * @return <i>true</i> if in the resultant document the object behind the wrapper
     *          must be indirect, otherwise <i>false</i>.
     */
    protected abstract boolean isWrappedObjectMustBeIndirect();

    protected void setPdfObject(T pdfObject){
        this.pdfObject = pdfObject;
    }

    protected void setForbidRelease() {
        if (pdfObject != null) {
            pdfObject.setState(PdfObject.ForbidRelease);
        }
    }

    protected void unsetForbidRelease() {
        if (pdfObject != null) {
            pdfObject.clearState(PdfObject.ForbidRelease);
        }
    }

    protected static void markObjectAsIndirect(PdfObject pdfObject) {
        if (pdfObject.getIndirectReference() == null) {
            pdfObject.setState(PdfObject.MustBeIndirect);
        }
    }

    /**
     * Some wrappers use object's indirect reference to obtain the {@code PdfDocument}
     * to which the object belongs to. For this matter, for these wrappers it is implicitly defined
     * that they work with indirect objects only. Commonly these wrappers have two constructors: one with
     * {@code PdfDocument} as parameter to create a new object, and the other one which
     * wraps around the given {@code PdfObject}. This method should be used in the second
     * type of constructors to ensure that wrapper will able to obtain the {@code PdfDocument} instance.
     *
     * @param object the {@code PdfObject} to be checked if it is indirect.
     */
    protected static void ensureObjectIsAddedToDocument(PdfObject object) {
        if (object.getIndirectReference() == null) {
            throw new PdfException(PdfException.ObjectMustBeIndirectToWorkWithThisWrapper);
        }
    }
}
