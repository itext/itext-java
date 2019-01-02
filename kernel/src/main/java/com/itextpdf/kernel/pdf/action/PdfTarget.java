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
package com.itextpdf.kernel.pdf.action;

import com.itextpdf.io.LogMessageConstant;
import com.itextpdf.kernel.PdfException;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNameTree;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfObjectWrapper;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfFileAttachmentAnnotation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

/**
 * A target dictionary locates the target in relation to the source,
 * in much the same way that a relative path describes the physical
 * relationship between two files in a file system. Target dictionaries may be
 * nested recursively to specify one or more intermediate targets before reaching the final one.
 */
public class PdfTarget extends PdfObjectWrapper<PdfDictionary> {

    private static final long serialVersionUID = -5814265943827690509L;

    private PdfTarget(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates a new {@link PdfTarget} object by the underlying dictionary.
     *
     * @param pdfObject the underlying dictionary object
     */
    public static PdfTarget create(PdfDictionary pdfObject) {
        return new PdfTarget(pdfObject);
    }

    /**
     * Creates a new {@link PdfTarget} object given its type. The type must be either
     * {@link PdfName#P}, or {@link PdfName#C}. If it is {@link PdfName#C}, additional entries must be specified
     * according to the spec.
     *
     * @param r the relationship between the current document and the target
     */
    private static PdfTarget create(PdfName r) {
        PdfTarget pdfTarget = new PdfTarget(new PdfDictionary());
        pdfTarget.put(PdfName.R, r);
        return pdfTarget;
    }

    /**
     * Creates a new target object pointing to the parent of the current document.
     *
     * @return created {@link PdfTarget}
     */
    public static PdfTarget createParentTarget() {
        return PdfTarget.create(PdfName.P);
    }

    /**
     * Creates a new target object pointing to the child of the current document.
     *
     * @return created {@link PdfTarget}
     */
    public static PdfTarget createChildTarget() {
        return PdfTarget.create(PdfName.C);
    }

    /**
     * Creates a new target object pointing to a file in the EmbeddedFiles name tree.
     *
     * @param embeddedFileName the name of the file in the EmbeddedFiles name tree
     * @return created object
     */
    public static PdfTarget createChildTarget(String embeddedFileName) {
        return PdfTarget.create(PdfName.C).
                put(PdfName.N, new PdfString(embeddedFileName));
    }

    /**
     * Creates a new target object pointing to a file attachment annotation.
     *
     * @param namedDestination     a named destination in the current document that
     *                             provides the page number of the file attachment annotation
     * @param annotationIdentifier a unique annotation identifier ({@link PdfName#NM} entry) of the annotation
     * @return created object
     */
    public static PdfTarget createChildTarget(String namedDestination, String annotationIdentifier) {
        return PdfTarget.create(PdfName.C).
                put(PdfName.P, new PdfString(namedDestination)).
                put(PdfName.A, new PdfString(annotationIdentifier));
    }

    /**
     * Creates a new target object pointing to a file attachment annotation.
     *
     * @param pageNumber      the number of the page in the current document, one-based
     * @param annotationIndex the index of the annotation in the Annots entry of the page, zero-based
     * @return created object
     */
    public static PdfTarget createChildTarget(int pageNumber, int annotationIndex) {
        return PdfTarget.create(PdfName.C).
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
    public PdfTarget setName(String name) {
        return put(PdfName.N, new PdfString(name));
    }

    /**
     * Gets name of the file in the EmbeddedFiles name tree for the child target located
     * in the EmbeddedFiles.
     *
     * @return the name of the child file for this target
     */
    public String getName() {
        return getPdfObject().getAsString(PdfName.N).toString();
    }

    /**
     * Sets the /P and /A values corresponding to provided annotation, which is already added to a page.
     *
     * @param pdfAnnotation the annotation to be set
     * @param pdfDocument   the corresponding document
     * @return this object wrapper
     */
    public PdfTarget setAnnotation(PdfFileAttachmentAnnotation pdfAnnotation, PdfDocument pdfDocument) {
        PdfPage page = pdfAnnotation.getPage();
        if (null == page) {
            throw new PdfException(PdfException.AnnotationShallHaveReferenceToPage);
        } else {
            put(PdfName.P, new PdfNumber(pdfDocument.getPageNumber(page)));
            put(PdfName.A, new PdfNumber(page.getAnnotations().indexOf(pdfAnnotation)));
        }
        return this;
    }

    /**
     * Gets the annotation specified by /A and /P entry values.
     *
     * @param pdfDocument specifies the corresponding document
     * @return the annotation specified by /A and /P entry value.
     */
    public PdfFileAttachmentAnnotation getAnnotation(PdfDocument pdfDocument) {
        PdfObject pValue = getPdfObject().get(PdfName.P);
        PdfPage page = null;
        if (pValue instanceof PdfNumber) {
            page = pdfDocument.getPage(((PdfNumber) pValue).intValue() + 1); // zero-based index is used
        } else if (pValue instanceof PdfString) {
            PdfNameTree destsTree = pdfDocument.getCatalog().getNameTree(PdfName.Dests);
            Map<String, PdfObject> dests = destsTree.getNames();
            PdfArray pdfArray = (PdfArray) dests.get(((PdfString) pValue).getValue());
            if (null != pdfArray) {
                if (pdfArray.get(0) instanceof PdfNumber) {
                    page = pdfDocument.getPage(((PdfNumber) pdfArray.get(0)).intValue());
                } else {
                    page = pdfDocument.getPage((PdfDictionary) pdfArray.get(0));
                }
            }
        }

        List<PdfAnnotation> pageAnnotations = null;
        if (null != page) {
            pageAnnotations = page.getAnnotations();
        }
        PdfObject aValue = getPdfObject().get(PdfName.A);
        PdfFileAttachmentAnnotation resultAnnotation = null;
        if (null != pageAnnotations) {
            if (aValue instanceof PdfNumber) {
                resultAnnotation = (PdfFileAttachmentAnnotation) pageAnnotations.get(((PdfNumber) aValue).intValue());
            } else if (aValue instanceof PdfString) {
                for (PdfAnnotation annotation : pageAnnotations) {
                    if (aValue.equals(annotation.getName())) {
                        resultAnnotation = (PdfFileAttachmentAnnotation) annotation;
                        break;
                    }
                }
            }
        }
        if (null == resultAnnotation) {
            Logger logger = LoggerFactory.getLogger(PdfTarget.class);
            logger.error(LogMessageConstant.SOME_TARGET_FIELDS_ARE_NOT_SET_OR_INCORRECT);
        }
        return resultAnnotation;
    }

    /**
     * Sets a target dictionary specifying additional path information to the target document.
     * If this entry is absent, the current document is the target file containing the destination.
     *
     * @param target the additional path target dictionary
     * @return this object wrapper
     */
    public PdfTarget setTarget(PdfTarget target) {
        return put(PdfName.T, target.getPdfObject());
    }

    /**
     * Get a target dictionary specifying additional path information to the target document.
     * If the current target object is the final node in the target path, <code>null</code> is returned.
     *
     * @return a target dictionary specifying additional path information to the target document
     */
    public PdfTarget getTarget() {
        PdfDictionary targetDictObject = getPdfObject().getAsDictionary(PdfName.T);
        return targetDictObject != null ? new PdfTarget(targetDictObject) : null;
    }

    /**
     * This is a convenient method to put key-value pairs to the underlying {@link PdfObject}.
     *
     * @param key   the key, a {@link PdfName} instance
     * @param value the value
     * @return this object wrapper
     */
    public PdfTarget put(PdfName key, PdfObject value) {
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
