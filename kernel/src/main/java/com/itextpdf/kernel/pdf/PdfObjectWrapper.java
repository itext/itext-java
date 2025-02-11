/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;


public abstract class PdfObjectWrapper<T extends PdfObject> {


    private T pdfObject = null;

    protected PdfObjectWrapper(T pdfObject) {
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
     * @param document a document the indirect reference belongs to.
     * @param reference a reference which will be assigned for the object behind wrapper.
     * @return object itself.
     */
    public PdfObjectWrapper<T> makeIndirect(PdfDocument document, PdfIndirectReference reference) {
        getPdfObject().makeIndirect(document, reference);
        return this;
    }

    /**
     * Marks object behind wrapper to be saved as indirect.
     *
     * @param document a document the indirect reference will belong to.
     * @return object itself.
     */
    public PdfObjectWrapper<T> makeIndirect(PdfDocument document) {
        return makeIndirect(document, null);
    }

    public PdfObjectWrapper<T> setModified() {
        pdfObject.setModified();
        return this;
    }

    public void flush() {
        pdfObject.flush();
    }

    public boolean isFlushed() {
        return pdfObject.isFlushed();
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
            pdfObject.setState(PdfObject.FORBID_RELEASE);
        }
    }

    protected void unsetForbidRelease() {
        if (pdfObject != null) {
            pdfObject.clearState(PdfObject.FORBID_RELEASE);
        }
    }

    protected void ensureUnderlyingObjectHasIndirectReference() {
        if (getPdfObject().getIndirectReference() == null) {
            throw new PdfException(
                    KernelExceptionMessageConstant.TO_FLUSH_THIS_WRAPPER_UNDERLYING_OBJECT_MUST_BE_ADDED_TO_DOCUMENT);
        }
    }

    protected static void markObjectAsIndirect(PdfObject pdfObject) {
        if (pdfObject.getIndirectReference() == null) {
            pdfObject.setState(PdfObject.MUST_BE_INDIRECT);
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
            throw new PdfException(KernelExceptionMessageConstant.OBJECT_MUST_BE_INDIRECT_TO_WORK_WITH_THIS_WRAPPER);
        }
    }

}
