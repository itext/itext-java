/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.kernel.pdf.action;

import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
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


    private PdfTarget(PdfDictionary pdfObject) {
        super(pdfObject);
    }

    /**
     * Creates a new {@link PdfTarget} object by the underlying dictionary.
     *
     * @param pdfObject the underlying dictionary object
     * @return a new {@link PdfTarget} object by the underlying dictionary
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
            throw new PdfException(KernelExceptionMessageConstant.ANNOTATION_SHALL_HAVE_REFERENCE_TO_PAGE);
        } else {
            put(PdfName.P, new PdfNumber(pdfDocument.getPageNumber(page) - 1));
            int indexOfAnnotation = -1;
            final List<PdfAnnotation> annots = page.getAnnotations();
            for (int i = 0; i < annots.size(); i++) {
                if (annots.get(i) != null &&
                        pdfAnnotation.getPdfObject().equals(annots.get(i).getPdfObject())) {
                    indexOfAnnotation = i;
                    break;
                }
            }
            put(PdfName.A, new PdfNumber(indexOfAnnotation));
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
            // zero-based index is used
            page = pdfDocument.getPage(((PdfNumber) pValue).intValue() + 1);
        } else if (pValue instanceof PdfString) {
            PdfNameTree destsTree = pdfDocument.getCatalog().getNameTree(PdfName.Dests);
            Map<PdfString, PdfObject> dests = destsTree.getNames();
            PdfArray pdfArray = (PdfArray) dests.get((PdfString) pValue);
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
            logger.error(IoLogMessageConstant.SOME_TARGET_FIELDS_ARE_NOT_SET_OR_INCORRECT);
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
