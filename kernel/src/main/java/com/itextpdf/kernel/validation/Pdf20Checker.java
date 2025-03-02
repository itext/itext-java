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
package com.itextpdf.kernel.validation;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.KernelExceptionMessageConstant;
import com.itextpdf.kernel.exceptions.Pdf20ConformanceException;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfCatalog;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.tagging.IStructureNode;
import com.itextpdf.kernel.pdf.tagging.PdfNamespace;
import com.itextpdf.kernel.pdf.tagging.PdfStructElem;
import com.itextpdf.kernel.pdf.tagging.PdfStructTreeRoot;
import com.itextpdf.kernel.pdf.tagutils.ITagTreeIteratorHandler;
import com.itextpdf.kernel.pdf.tagutils.TagStructureContext;
import com.itextpdf.kernel.pdf.tagutils.TagTreeIterator;
import com.itextpdf.kernel.utils.checkers.PdfCheckersUtil;
import com.itextpdf.kernel.validation.context.PdfDocumentValidationContext;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;

import java.util.function.Function;

/**
 * Class that will run through all necessary checks defined in the PDF 2.0 standard. The standard that is followed is
 * the series of ISO 32000 specifications, starting from ISO 32000-2:2020.
 */
public class Pdf20Checker implements IValidationChecker {

    private static final Function<String, PdfException> EXCEPTION_SUPPLIER = (msg) -> new Pdf20ConformanceException(msg);

    private final TagStructureContext tagStructureContext;

    /**
     * Creates new {@link Pdf20Checker} instance to validate PDF document against PDF 2.0 standard.
     *
     * @param pdfDocument {@link PdfDocument} to check
     */
    public Pdf20Checker(PdfDocument pdfDocument) {
        this.tagStructureContext = pdfDocument.isTagged() ? pdfDocument.getTagStructureContext() : null;
    }

    @Override
    public void validate(IValidationContext validationContext) {
        switch (validationContext.getType()) {
            case PDF_DOCUMENT:
                PdfDocumentValidationContext pdfDocContext = (PdfDocumentValidationContext) validationContext;
                checkCatalog(pdfDocContext.getPdfDocument().getCatalog());
                checkStructureTreeRoot(pdfDocContext.getPdfDocument().getStructTreeRoot());
                break;
        }
    }

    @Override
    public boolean isPdfObjectReadyToFlush(PdfObject object) {
        return true;
    }

    /**
     * Checks that natural language is declared using the methods described in ISO 32000-2:2020, 14.9.2.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary
     */
    void checkLang(PdfCatalog catalog) {
        PdfDictionary catalogDict = catalog.getPdfObject();
        PdfObject lang = catalogDict.get(PdfName.Lang);
        if (lang instanceof PdfString && !((PdfString) lang).getValue().isEmpty()) {
            PdfCheckersUtil.validateLang(catalogDict, EXCEPTION_SUPPLIER);
        }
    }

    /**
     * Checks that the value of the {@code Metadata} key from the {@code Catalog} dictionary of a conforming file
     * is a metadata stream as defined in ISO 32000-2:2020.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary
     */
    void checkMetadata(PdfCatalog catalog) {
        PdfDictionary catalogDict = catalog.getPdfObject();
        if (!catalogDict.containsKey(PdfName.Metadata)) {
            return;
        }
        try {
            XMPMeta metadata = catalog.getDocument().getXmpMetadata();
            if (metadata == null) {
                throw new Pdf20ConformanceException(
                        KernelExceptionMessageConstant.INVALID_METADATA_VALUE);
            }

            PdfStream pdfStream = catalogDict.getAsStream(PdfName.Metadata);
            PdfName type = pdfStream.getAsName(PdfName.Type);
            PdfName subtype = pdfStream.getAsName(PdfName.Subtype);
            if (!PdfName.Metadata.equals(type) || !PdfName.XML.equals(subtype)) {
                throw new Pdf20ConformanceException(
                        KernelExceptionMessageConstant.METADATA_STREAM_REQUIRES_METADATA_TYPE_AND_XML_SUBTYPE);
            }
        } catch (XMPException e) {
            throw new Pdf20ConformanceException(
                    KernelExceptionMessageConstant.INVALID_METADATA_VALUE, e);
        }
    }

    /**
     * Validates document structure tree root dictionary against PDF 2.0 standard.
     *
     * <p>
     * Checks, that all structure elements are belong to, or role mapped to (such role mapping may be transitive through
     * other namespaces), at least one of the following namespaces specified in ISO 32000-2:2020, 14.8.6:
     * — the PDF 1.7 namespace;
     * — the PDF 2.0 namespace;
     * — the MathML namespace.
     * A structure element with no explicit namespace may be present. Such a structure element shall have, after
     * any role mapping, a structure type matching one of the unique PDF 1.7 element types (the default standard
     * structure namespace in ISO 32000-2 is defined as the PDF 1.7 namespace).
     *
     * @param structTreeRoot {@link PdfStructTreeRoot} to validate
     */
    void checkStructureTreeRoot(PdfStructTreeRoot structTreeRoot) {
        if (tagStructureContext == null) {
            return;
        }
        TagTreeIterator tagTreeIterator = new TagTreeIterator(structTreeRoot);
        tagTreeIterator.addHandler(new StructureTreeRootHandler(tagStructureContext));
        tagTreeIterator.traverse();
    }

    /**
     * Validates document catalog dictionary against PDF 2.0 standard.
     *
     * <p>
     * For now, only {@code Metadata} and {@code Lang} are checked.
     *
     * @param catalog {@link PdfCatalog} document catalog dictionary to check
     */
    private void checkCatalog(PdfCatalog catalog) {
        checkLang(catalog);
        checkMetadata(catalog);
    }

    /**
     * Handler class that checks structure nodes while traversing the document structure tree.
     */
    private static class StructureTreeRootHandler implements ITagTreeIteratorHandler {
        private final TagStructureContext tagStructureContext;

        /**
         * Creates new {@link StructureTreeRootHandler} instance.
         *
         * @param tagStructureContext {@link TagStructureContext} of the current tagged document
         */
        public StructureTreeRootHandler(TagStructureContext tagStructureContext) {
            this.tagStructureContext = tagStructureContext;
        }

        @Override
        public boolean accept(IStructureNode node) {
            return node != null;
        }

        @Override
        public void processElement(IStructureNode elem) {
            if (!(elem instanceof PdfStructElem)) {
                return;
            }
            PdfStructElem structElem = (PdfStructElem) elem;
            String role = structElem.getRole().getValue();
            PdfNamespace namespace = structElem.getNamespace();
            if (!tagStructureContext.checkIfRoleShallBeMappedToStandardRole(role, namespace)) {
                throw new Pdf20ConformanceException(MessageFormatUtil.format(namespace == null ?
                                KernelExceptionMessageConstant.ROLE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE :
                                KernelExceptionMessageConstant.ROLE_IN_NAMESPACE_IS_NOT_MAPPED_TO_ANY_STANDARD_ROLE,
                        role, namespace != null ? namespace.getNamespaceName() : null));
            }
        }
    }
}
