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

/**
 * A target dictionary locates the target in relation to the source,
 * in much the same way that a relative path describes the physical
 * relationship between two files in a file system. Target dictionaries may be
 * nested recursively to specify one or more intermediate targets before reaching the final one.
 */
public class PdfTargetDictionary extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -5814265943827690509L;

    /**
     * Creates a new {@link PdfTargetDictionary} object by the underlying dictionary.
     *
     * @param pdfObject the underlying dictionary object
     */
    public PdfTargetDictionary(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates a new {@link PdfTargetDictionary} object given its type. The type must be either
     * {@link PdfName#P}, or {@link PdfName#C}. If it is {@link PdfName#C}, additional entries must be specified
     * according to the spec.
     * @param r the relationship between the current document and the target
     */
    public PdfTargetDictionary(PdfName r) {
        this(new PdfDictionary());
        put(PdfName.R, r);
    }

    /**
     * Creates a new {@link PdfTargetDictionary} object.
     * @param r the relationship between the current document and the target
     * @param n the name of the file in the EmbeddedFiles name tree
     * @param p if the value is an integer, it specifies the page number (zero-based) in the current
     *          document containing the file attachment annotation. If the value is a string,
     *          it specifies a named destination in the current document that provides the page
     *          number of the file attachment annotation
     * @param a If the value is an integer, it specifies the index (zero-based) of the annotation in the
     *          Annots array of the page specified by P. If the value is a text string,
     *          it specifies the value of NM in the annotation dictionary
     * @param t A target dictionary specifying additional path information to the target document.
     *          If this entry is absent, the current document is the target file containing the destination
     */
    public PdfTargetDictionary(PdfName r, PdfString n, PdfObject p, PdfObject a, PdfTargetDictionary t) {
        this(new PdfDictionary());
        put(PdfName.R, r).
                put(PdfName.N, n).
                put(PdfName.P, p).
                put(PdfName.A, a).
                put(PdfName.T, t.getPdfObject());
    }

    /**
     * Creates a new target object pointing to the parent of the current document.
     * @return created {@link PdfTargetDictionary}
     */
    public static PdfTargetDictionary createParentTarget() {
        return new PdfTargetDictionary(PdfName.P);
    }

    /**
     * Creates a new target object pointing to a file in the EmbeddedFiles name tree.
     * @param embeddedFileName the name of the file in the EmbeddedFiles name tree
     * @return created object
     */
    public static PdfTargetDictionary createChildTarget(String embeddedFileName) {
        return new PdfTargetDictionary(PdfName.C).
                put(PdfName.N, new PdfString(embeddedFileName));
    }

    /**
     * Creates a new target object pointing to a file attachment annotation.
     * @param namedDestination a named destination in the current document that
     *                         provides the page number of the file attachment annotation
     * @param annotationIdentifier a unique annotation identifier ({@link PdfName#NM} entry) of the annotation
     * @return created object
     */
    public static PdfTargetDictionary createChildTarget(String namedDestination, String annotationIdentifier) {
        return new PdfTargetDictionary(PdfName.C).
                put(PdfName.P, new PdfString(namedDestination)).
                put(PdfName.A, new PdfString(annotationIdentifier));
    }

    /**
     * Creates a new target object pointing to a file attachment annotation.
     * @param pageNumber the number of the page in the current document, one-based
     * @param annotationIndex the index of the annotation in the Annots entry of the page, zero-based
     * @return created object
     */
    public static PdfTargetDictionary createChildTarget(int pageNumber, int annotationIndex) {
        return new PdfTargetDictionary(PdfName.C).
                put(PdfName.P, new PdfNumber(pageNumber - 1)).
                put(PdfName.A, new PdfNumber(annotationIndex));
    }

    /**
     * Sets the name of the file in the EmbeddedFiles name tree for the child target located
     * in the EmbeddedFiles.
     *
     * @param name the name of the file
     * @return this object wrapper
     */
    public PdfTargetDictionary setName(String name) {
        return put(PdfName.N, new PdfString(name));
    }

    /**
     * Gets name of the file in the EmbeddedFiles name tree for the child target located
     * in the EmbeddedFiles.
     *
     * @return the name of the child file for this target
     */
    public PdfString getName() {
        return getPdfObject().getAsString(PdfName.N);
    }

    /**
     * Sets the page number in the current document containing the file attachment annotation for the
     * child target associates with a file attachment annotation.
     *
     * @param pageNumber the page number (one-based) in the current document containing
     *                   the file attachment annotation
     * @return this object wrapper
     */
    public PdfTargetDictionary setPage(int pageNumber) {
        return put(PdfName.P, new PdfNumber(pageNumber - 1));
    }

    /**
     * Sets a named destination in the current document that provides the page number of the file
     * attachment annotation for the child target associated with a file attachment annotation.
     *
     * @param namedDestination a named destination in the current document that provides the page
     *                         number of the file attachment annotation
     * @return this object wrapper
     */
    public PdfTargetDictionary setPage(String namedDestination) {
        return put(PdfName.P, new PdfString(namedDestination));
    }

    /**
     * Get the contents of the /P entry of this target object.
     * If the value is an integer, it specifies the page number (zero-based)
     * in the current document containing the file attachment annotation.
     * If the value is a string, it specifies a named destination in the current
     * document that provides the page number of the file attachment annotation.
     *
     * @return the /P entry of target object
     */
    public PdfObject getPage() {
        return getPdfObject().get(PdfName.P);
    }

    /**
     * Sets the index of the annotation in Annots array of the page specified by /P entry
     * for the child target associated with a file attachment annotation.
     *
     * @param annotationIndex the index (zero-based) of the annotation in the Annots array
     * @return this object wrapper
     */
    public PdfTargetDictionary setAnnotation(int annotationIndex) {
        return put(PdfName.A, new PdfNumber(annotationIndex));
    }

    /**
     * Sets the text value, which uniquely identifies an annotation (/NM entry) in an annotation dictionary
     * for the child target associated with a file attachment annotation.
     *
     * @param annotationName specifies the value of NM in the annotation dictionary of the target annotation
     * @return this object wrapper
     */
    public PdfTargetDictionary setAnnotation(String annotationName) {
        return put(PdfName.A, new PdfString(annotationName));
    }

    /**
     * Gets the object in the /A entry of the underlying object. If the value is an integer,
     * it specifies the index (zero-based) of the annotation in the Annots array of the page specified by P.
     * If the value is a text string, it specifies the value of NM in the annotation dictionary.
     *
     * @return the /A entry in the target object
     */
    public PdfObject getAnnotation() {
        return getPdfObject().get(PdfName.A);
    }

    /**
     * Sets a target dictionary specifying additional path information to the target document.
     * If this entry is absent, the current document is the target file containing the destination.
     *
     * @param target the additional path target dictionary
     * @return this object wrapper
     */
    public PdfTargetDictionary setTarget(PdfTargetDictionary target) {
        return put(PdfName.T, target.getPdfObject());
    }

    /**
     * Get a target dictionary specifying additional path information to the target document.
     * If the current target object is the final node in the target path, <code>null</code> is returned.
     *
     * @return a target dictionary specifying additional path information to the target document
     */
    public PdfTargetDictionary getTarget() {
        PdfDictionary targetDictObject = getPdfObject().getAsDictionary(PdfName.T);
        return targetDictObject != null ? new PdfTargetDictionary(targetDictObject) : null;
    }

    /**
     * This is a convenient method to put key-value pairs to the underlying {@link PdfObject}.
     *
     * @param key   the key, a {@link PdfName} instance
     * @param value the value
     * @return this object wrapper
     */
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
