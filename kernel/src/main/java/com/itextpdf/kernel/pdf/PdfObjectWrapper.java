/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: Bruno Lowagie, Paulo Soares, et al.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.kernel.pdf;

import com.itextpdf.kernel.PdfException;

import java.io.Serializable;

public abstract class PdfObjectWrapper<T extends PdfObject> implements Serializable {

    private static final long serialVersionUID = 3516473712028588356L;

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
     * @param document a document the indirect reference will belong to.
     * @param reference
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
            throw new PdfException(PdfException.ToFlushThisWrapperUnderlyingObjectMustBeAddedToDocument);
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
            throw new PdfException(PdfException.ObjectMustBeIndirectToWorkWithThisWrapper);
        }
    }

}
