/*

    This file is part of the iText (R) project.
    Copyright (c) 1998-2016 iText Group NV
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
package com.itextpdf.kernel.pdf.action;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfString;

public class PdfTargetDictionary extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -5814265943827690509L;

	public PdfTargetDictionary(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    public PdfTargetDictionary(PdfName r) {
        this(new PdfDictionary());
        put(PdfName.R, r);
    }

    public PdfTargetDictionary(PdfName r, PdfString n, PdfObject p, PdfObject a, PdfTargetDictionary t) {
        this(new PdfDictionary());
        put(PdfName.R, r).put(PdfName.N, n).
                put(PdfName.P, p).
                put(PdfName.A, a).put(PdfName.T, t.getPdfObject());
    }

    /**
     * Sets the name of the file in the EmbeddedFiles name tree.
     * @param name the name of the file
     * @return
     */
    public PdfTargetDictionary setName(String name) {
        return put(PdfName.N, new PdfString(name));
    }

    /**
     * Gets name of the file
     * @return
     */
    public PdfString getName() {
        return getPdfObject().getAsString(PdfName.N);
    }

    /**
     * Sets the page number in the current document containing the file attachment annotation.
     * @param pageNumber
     * @return
     */
    public PdfTargetDictionary setPage(int pageNumber) {
        return put(PdfName.P, new PdfNumber(pageNumber));
    }

    /**
     * Sets a named destination in the current document that provides the page number of the file attachment annotation.
     * @param namedDestination
     * @return
     */
    public PdfTargetDictionary setPage(String namedDestination) {
        return put(PdfName.P, new PdfString(namedDestination));
    }

    /**
     * Get the page number or a named destination that provides the page number containing the file attachment annotation
     * @return
     */
    public PdfObject getPage() {
        return getPdfObject().get(PdfName.P);
    }

    /**
     * Sets the index of the annotation in Annots array of the page specified by /P entry.
     * @param annotNumber
     * @return
     */
    public PdfTargetDictionary setAnnotation(int annotNumber) {
        return put(PdfName.A, new PdfNumber(annotNumber));
    }

    /**
     * Sets the text value, which specifies the value of the /NM entry in the annotation dictionary.
     * @param annotationName
     * @return
     */
    public PdfTargetDictionary setAnnotation(String annotationName) {
        return put(PdfName.A, new PdfString(annotationName));
    }

    public PdfObject getAnnotation() {
        return getPdfObject().get(PdfName.A);
    }

    /**
     * Sets a target dictionary specifying additional path information to the target document.
     * @param target
     * @return
     */
    public PdfTargetDictionary setTarget(PdfTargetDictionary target) {
        return put(PdfName.T, target.getPdfObject());
    }

    public PdfTargetDictionary getTarget() {
        return new PdfTargetDictionary(getPdfObject().getAsDictionary(PdfName.T));
    }

    public PdfTargetDictionary put(PdfName key, PdfObject value) {
        getPdfObject().put(key, value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected boolean isWrappedObjectMustBeIndirect() {
        return false;
    }
}
