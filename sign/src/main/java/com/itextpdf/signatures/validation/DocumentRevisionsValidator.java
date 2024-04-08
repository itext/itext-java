/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.signatures.validation;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.source.RASInputStream;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.WindowRandomAccessSource;
import com.itextpdf.kernel.pdf.DocumentRevision;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * Validator, which is responsible for document revisions validation according to doc-MDP rules.
 */
class DocumentRevisionsValidator {
    static final String DOC_MDP_CHECK = "DocMDP check.";
    static final String NOT_ALLOWED_CATALOG_CHANGES =
            "PDF document catalog contains changes other than DSS dictionary addition, which is not allowed.";
    static final String DSS_REMOVED = "DSS dictionary was removed from catalog.";
    static final String EXTENSIONS_REMOVED = "Extensions dictionary was removed from catalog.";
    static final String DEVELOPER_EXTENSION_REMOVED =
            "Developer extension \"{0}\" dictionary was removed or unexpectedly modified.";
    static final String EXTENSION_LEVEL_DECREASED =
            "Extension level number in developer extension \"{0}\" dictionary was decreased.";
    static final String OBJECT_REMOVED =
            "Object \"{0}\", which is not allowed to be removed, was removed from the document through XREF table.";
    static final String UNEXPECTED_ENTRY_IN_XREF =
            "New PDF document revision contains unexpected entry \"{0}\" in XREF table.";

    DocumentRevisionsValidator() {
        // Empty constructor.
    }

    ValidationReport validateRevision(PdfDocument originalDocument, PdfDocument documentWithoutRevision,
            DocumentRevision revision) throws IOException {
        ValidationReport validationReport = new ValidationReport();
        try (InputStream inputStream = createInputStreamFromRevision(originalDocument, revision);
                PdfReader newReader = new PdfReader(inputStream);
                PdfDocument documentWithRevision = new PdfDocument(newReader)) {
            Set<PdfIndirectReference> indirectReferences = revision.getModifiedObjects();
            if (!compareCatalogs(documentWithoutRevision, documentWithRevision, validationReport)) {
                return validationReport;
            }
            List<ReferencesPair> allowedReferences =
                    createAllowedReferences(documentWithRevision, documentWithoutRevision);
            for (PdfIndirectReference indirectReference : indirectReferences) {
                if (indirectReference.isFree()) {
                    // In this boolean flag we check that reference which is about to be removed is the one which
                    // changed in the new revision. For instance DSS reference was 5 0 obj and changed to be 6 0 obj.
                    // In this case and only in this case reference with obj number 5 can be safely removed.
                    boolean referenceAllowedToBeRemoved = allowedReferences.stream().anyMatch(
                            reference -> reference.getPreviousReference() != null &&
                            reference.getPreviousReference().getObjNumber() == indirectReference.getObjNumber() &&
                            (reference.getCurrentReference() == null ||
                            reference.getCurrentReference().getObjNumber() != indirectReference.getObjNumber()));
                    // If some reference wasn't in the previous document, it is safe to remove it,
                    // since it is not possible to introduce new reference and remove it at the same revision.
                    boolean referenceWasInPrevDocument =
                            documentWithoutRevision.getPdfObject(indirectReference.getObjNumber()) != null;
                    if (!isMaxGenerationObject(indirectReference) &&
                            referenceWasInPrevDocument && !referenceAllowedToBeRemoved) {
                        validationReport.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(
                                OBJECT_REMOVED, indirectReference.getObjNumber()), ReportItemStatus.INVALID));
                    }
                } else if (!checkAllowedReferences(allowedReferences, indirectReference, documentWithoutRevision)) {
                    validationReport.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(
                            UNEXPECTED_ENTRY_IN_XREF, indirectReference.getObjNumber()), ReportItemStatus.INVALID));
                }
            }
        }
        return validationReport;
    }

    private boolean checkAllowedReferences(List<ReferencesPair> allowedReferences,
            PdfIndirectReference indirectReference, PdfDocument documentWithoutRevision) {
        for (ReferencesPair allowedReference : allowedReferences) {
            if (isSameReference(allowedReference.getCurrentReference(), indirectReference)) {
                return documentWithoutRevision.getPdfObject(indirectReference.getObjNumber()) == null ||
                        allowedReferences.stream().anyMatch(
                                reference -> isSameReference(reference.getPreviousReference(), indirectReference));
            }
        }
        return false;
    }

    private List<ReferencesPair> createAllowedReferences(PdfDocument documentWithRevision,
            PdfDocument documentWithoutRevision) {
        // First indirect reference in the pair is an allowed reference to be present in new xref table,
        // and the second indirect reference in the pair is the same entry in the previous document.
        // If any reference is null, we expect this object to be newly generated or direct reference.
        List<ReferencesPair> allowedReferences = new ArrayList<>();

        if (documentWithRevision.getTrailer().get(PdfName.Info) != null) {
            allowedReferences.add(new ReferencesPair(
                    documentWithRevision.getTrailer().get(PdfName.Info).getIndirectReference(),
                    getIndirectReferenceOrNull(() ->
                            documentWithoutRevision.getTrailer().get(PdfName.Info).getIndirectReference())));
        }
        if (documentWithRevision.getCatalog().getPdfObject() == null) {
            return allowedReferences;
        }
        allowedReferences.add(new ReferencesPair(
                documentWithRevision.getCatalog().getPdfObject().getIndirectReference(),
                getIndirectReferenceOrNull(() ->
                        documentWithoutRevision.getCatalog().getPdfObject().getIndirectReference())));
        if (documentWithRevision.getCatalog().getPdfObject().get(PdfName.Metadata) != null) {
            allowedReferences.add(new ReferencesPair(
                    documentWithRevision.getCatalog().getPdfObject().get(PdfName.Metadata).getIndirectReference(),
                    getIndirectReferenceOrNull(() -> documentWithoutRevision.getCatalog().getPdfObject()
                            .get(PdfName.Metadata).getIndirectReference())));
        }


        PdfDictionary currentDssDictionary =
                documentWithRevision.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        PdfDictionary previousDssDictionary =
                documentWithoutRevision.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        if (currentDssDictionary == null) {
            return allowedReferences;
        }
        allowedReferences.add(new ReferencesPair(currentDssDictionary.getIndirectReference(),
                getIndirectReferenceOrNull(() -> previousDssDictionary.getIndirectReference())));

        allowedReferences.addAll(createAllowedDssEntries(documentWithRevision, documentWithoutRevision));
        return allowedReferences;
    }

    private List<ReferencesPair> createAllowedDssEntries(PdfDocument documentWithRevision,
            PdfDocument documentWithoutRevision) {
        List<ReferencesPair> allowedReferences = new ArrayList<>();
        PdfDictionary currentDssDictionary =
                documentWithRevision.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        PdfDictionary previousDssDictionary =
                documentWithoutRevision.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        PdfArray certs = currentDssDictionary.getAsArray(PdfName.Certs);
        if (certs != null) {
            allowedReferences.add(new ReferencesPair(certs.getIndirectReference(),
                    getIndirectReferenceOrNull(() -> previousDssDictionary.get(PdfName.Certs).getIndirectReference())));
            for (int i = 0; i < certs.size(); ++i) {
                int finalI = i;
                allowedReferences.add(
                        new ReferencesPair(certs.get(i).getIndirectReference(),
                                getIndirectReferenceOrNull(() -> previousDssDictionary.getAsArray(PdfName.Certs)
                                        .get(finalI).getIndirectReference())));
            }
        }
        PdfArray ocsps = currentDssDictionary.getAsArray(PdfName.OCSPs);
        if (ocsps != null) {
            allowedReferences.add(new ReferencesPair(ocsps.getIndirectReference(),
                    getIndirectReferenceOrNull(() -> previousDssDictionary.get(PdfName.OCSPs).getIndirectReference())));
            for (int i = 0; i < ocsps.size(); ++i) {
                int finalI = i;
                allowedReferences.add(new ReferencesPair(
                        ocsps.get(i).getIndirectReference(),
                        getIndirectReferenceOrNull(() -> previousDssDictionary.getAsArray(PdfName.OCSPs)
                                .get(finalI).getIndirectReference())));
            }
        }
        PdfArray crls = currentDssDictionary.getAsArray(PdfName.CRLs);
        if (crls != null) {
            allowedReferences.add(new ReferencesPair(crls.getIndirectReference(),
                    getIndirectReferenceOrNull(() -> previousDssDictionary.get(PdfName.CRLs).getIndirectReference())));
            for (int i = 0; i < crls.size(); ++i) {
                int finalI = i;
                allowedReferences.add(new ReferencesPair(crls.get(i).getIndirectReference(), getIndirectReferenceOrNull(
                        () -> previousDssDictionary.getAsArray(PdfName.CRLs).get(finalI).getIndirectReference())));
            }
        }
        PdfDictionary vris = currentDssDictionary.getAsDictionary(PdfName.VRI);
        if (vris != null) {
            allowedReferences.add(new ReferencesPair(vris.getIndirectReference(),
                    getIndirectReferenceOrNull(() -> previousDssDictionary.get(PdfName.VRI).getIndirectReference())));
            for (Map.Entry<PdfName, PdfObject> vri : vris.entrySet()) {
                allowedReferences.add(new ReferencesPair(
                        vri.getValue().getIndirectReference(),
                        getIndirectReferenceOrNull(() -> previousDssDictionary.getAsDictionary(PdfName.VRI)
                                .get(vri.getKey()).getIndirectReference())));
                if (vri.getValue() instanceof PdfDictionary) {
                    PdfDictionary vriDictionary = (PdfDictionary) vri.getValue();
                    PdfArray vriCerts = vriDictionary.getAsArray(PdfName.Cert);
                    if (vriCerts != null) {
                        allowedReferences.add(new ReferencesPair(vriCerts.getIndirectReference(),
                                getIndirectReferenceOrNull(() -> previousDssDictionary.getAsDictionary(PdfName.VRI)
                                        .getAsDictionary(vri.getKey()).get(PdfName.Cert).getIndirectReference())));
                        for (int i = 0; i < vriCerts.size(); ++i) {
                            int finalI = i;
                            allowedReferences.add(new ReferencesPair(vriCerts.get(i).getIndirectReference(),
                                    getIndirectReferenceOrNull(() -> previousDssDictionary.getAsDictionary(PdfName.VRI)
                                            .getAsDictionary(vri.getKey()).getAsArray(PdfName.Cert).get(finalI)
                                            .getIndirectReference())));
                        }
                    }
                    PdfArray vriOcsps = vriDictionary.getAsArray(PdfName.OCSP);
                    if (vriOcsps != null) {
                        allowedReferences.add(new ReferencesPair(
                                vriOcsps.getIndirectReference(),
                                getIndirectReferenceOrNull(() -> previousDssDictionary.getAsDictionary(PdfName.VRI)
                                        .getAsDictionary(vri.getKey()).get(PdfName.OCSP).getIndirectReference())));
                        for (int i = 0; i < vriOcsps.size(); ++i) {
                            int finalI = i;
                            allowedReferences.add(new ReferencesPair(vriOcsps.get(i).getIndirectReference(),
                                    getIndirectReferenceOrNull(() -> previousDssDictionary.getAsDictionary(PdfName.VRI)
                                            .getAsDictionary(vri.getKey()).getAsArray(PdfName.OCSP).get(finalI)
                                            .getIndirectReference())));
                        }
                    }
                    PdfArray vriCrls = vriDictionary.getAsArray(PdfName.CRL);
                    if (vriCrls != null) {
                        allowedReferences.add(new ReferencesPair(vriCrls.getIndirectReference(),
                                getIndirectReferenceOrNull(() -> previousDssDictionary.getAsDictionary(PdfName.VRI)
                                        .getAsDictionary(vri.getKey()).get(PdfName.CRL).getIndirectReference())));
                        for (int i = 0; i < vriCrls.size(); ++i) {
                            int finalI = i;
                            allowedReferences.add(new ReferencesPair(vriCrls.get(i).getIndirectReference(),
                                    getIndirectReferenceOrNull(() -> previousDssDictionary.getAsDictionary(PdfName.VRI)
                                            .getAsDictionary(vri.getKey()).getAsArray(PdfName.CRL).get(finalI)
                                            .getIndirectReference())));
                        }
                    }
                    if (vriDictionary.get(new PdfName("TS")) != null) {
                        allowedReferences.add(new ReferencesPair(
                                vriDictionary.get(new PdfName("TS")).getIndirectReference(),
                                getIndirectReferenceOrNull(() -> previousDssDictionary.getAsDictionary(PdfName.VRI)
                                        .getAsDictionary(vri.getKey()).get(new PdfName("TS"))
                                        .getIndirectReference())));
                    }
                }
            }
        }
        return allowedReferences;
    }

    private boolean compareCatalogs(PdfDocument documentWithoutRevision, PdfDocument documentWithRevision,
            ValidationReport report) {
        PdfDictionary previousCatalog = documentWithoutRevision.getCatalog().getPdfObject();
        PdfDictionary currentCatalog = documentWithRevision.getCatalog().getPdfObject();

        PdfDictionary previousCatalogCopy = new PdfDictionary(previousCatalog);
        previousCatalogCopy.remove(PdfName.DSS);
        previousCatalogCopy.remove(PdfName.Extensions);
        previousCatalogCopy.remove(PdfName.Metadata);
        PdfDictionary currentCatalogCopy = new PdfDictionary(currentCatalog);
        currentCatalogCopy.remove(PdfName.DSS);
        currentCatalogCopy.remove(PdfName.Extensions);
        currentCatalogCopy.remove(PdfName.Metadata);
        if (!comparePdfObjects(previousCatalogCopy, currentCatalogCopy)) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, NOT_ALLOWED_CATALOG_CHANGES, ReportItemStatus.INVALID));
            return false;
        }
        return compareExtensions(previousCatalog.get(PdfName.Extensions),
                currentCatalog.get(PdfName.Extensions), report) &&
                compareDss(previousCatalog.get(PdfName.DSS), currentCatalog.get(PdfName.DSS), report);
    }

    private boolean compareExtensions(PdfObject previousExtensions, PdfObject currentExtensions,
            ValidationReport report) {
        if (previousExtensions == null || comparePdfObjects(previousExtensions, currentExtensions)) {
            return true;
        }
        if (currentExtensions == null) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, EXTENSIONS_REMOVED, ReportItemStatus.INVALID));
            return false;
        }
        if (!(previousExtensions instanceof PdfDictionary) || !(currentExtensions instanceof PdfDictionary)) {
            return false;
        }
        PdfDictionary previousExtensionsDictionary = (PdfDictionary) previousExtensions;
        PdfDictionary currentExtensionsDictionary = (PdfDictionary) currentExtensions;
        for (Map.Entry<PdfName, PdfObject> previousExtension : previousExtensionsDictionary.entrySet()) {
            PdfDictionary currentExtension = currentExtensionsDictionary.getAsDictionary(previousExtension.getKey());
            if (currentExtension == null) {
                report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(
                        DEVELOPER_EXTENSION_REMOVED, previousExtension.getKey()), ReportItemStatus.INVALID));
                return false;
            } else {
                PdfDictionary currentExtensionCopy = new PdfDictionary(currentExtension);
                currentExtensionCopy.remove(PdfName.ExtensionLevel);
                PdfDictionary previousExtensionCopy = new PdfDictionary((PdfDictionary) previousExtension.getValue());
                previousExtensionCopy.remove(PdfName.ExtensionLevel);
                // Apart from extension level dictionaries are expected to be equal.
                if (!comparePdfObjects(previousExtensionCopy, currentExtensionCopy)) {
                    report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(
                            DEVELOPER_EXTENSION_REMOVED, previousExtension.getKey()), ReportItemStatus.INVALID));
                    return false;
                }
                PdfNumber previousExtensionLevel = ((PdfDictionary) previousExtension.getValue())
                        .getAsNumber(PdfName.ExtensionLevel);
                PdfNumber currentExtensionLevel = currentExtension.getAsNumber(PdfName.ExtensionLevel);
                if (previousExtensionLevel != null) {
                    if (currentExtensionLevel == null ||
                            previousExtensionLevel.intValue() > currentExtensionLevel.intValue()) {
                        report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(
                                EXTENSION_LEVEL_DECREASED, previousExtension.getKey()), ReportItemStatus.INVALID));
                        return false;
                    }
                }
            }
        }
        return true;
    }

    private boolean compareDss(PdfObject previousDss, PdfObject currentDss, ValidationReport report) {
        if (previousDss == null) {
            return true;
        }
        if (currentDss == null) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, DSS_REMOVED, ReportItemStatus.INVALID));
            return false;
        }
        return true;
    }

    static InputStream createInputStreamFromRevision(PdfDocument originalDocument, DocumentRevision revision) {
        RandomAccessFileOrArray raf = originalDocument.getReader().getSafeFile();
        WindowRandomAccessSource source = new WindowRandomAccessSource(
                raf.createSourceView(), 0, revision.getEofOffset());
        return new RASInputStream(source);
    }

    private static boolean comparePdfObjects(PdfObject pdfObject1, PdfObject pdfObject2) {
        return comparePdfObjects(pdfObject1, pdfObject2, new HashSet<>());
    }

    private static boolean comparePdfObjects(PdfObject pdfObject1, PdfObject pdfObject2,
            Set<PdfObject> visitedObjects) {
        if (visitedObjects.contains(pdfObject1)) {
            return true;
        }
        visitedObjects.add(pdfObject1);
        if (Objects.equals(pdfObject1, pdfObject2)) {
            return true;
        }
        if (pdfObject1 == null || pdfObject2 == null) {
            return false;
        }
        if (pdfObject1.getClass() != pdfObject2.getClass()) {
            return false;
        }
        // We don't allow objects to be direct and indirect.
        // Acrobat however allows it, but such change can invalidate the document.
        if (pdfObject1.getIndirectReference() == null ^ pdfObject2.getIndirectReference() == null) {
            return false;
        }
        switch (pdfObject1.getType()) {
            case PdfObject.BOOLEAN:
            case PdfObject.NAME:
            case PdfObject.NULL:
            case PdfObject.LITERAL:
            case PdfObject.NUMBER:
            case PdfObject.STRING:
                return pdfObject1.equals(pdfObject2);
            case PdfObject.INDIRECT_REFERENCE:
                return comparePdfObjects(((PdfIndirectReference) pdfObject1).getRefersTo(),
                        ((PdfIndirectReference) pdfObject2).getRefersTo(), visitedObjects);
            case PdfObject.ARRAY:
                return comparePdfArrays((PdfArray) pdfObject1,(PdfArray) pdfObject2, visitedObjects);
            case PdfObject.DICTIONARY:
                return comparePdfDictionaries((PdfDictionary) pdfObject1, (PdfDictionary) pdfObject2,
                        visitedObjects);
            case PdfObject.STREAM:
                return comparePdfStreams((PdfStream) pdfObject1, (PdfStream) pdfObject2, visitedObjects);
            default:
                return false;
        }
    }

    private static boolean comparePdfArrays(PdfArray array1, PdfArray array2, Set<PdfObject> visitedObjects) {
        if (array1.size() != array2.size()) {
            return false;
        }
        for (int i = 0; i < array1.size(); i++) {
            if (!comparePdfObjects(array1.get(i), array2.get(i), visitedObjects)) {
                return false;
            }
        }
        return true;
    }

    private static boolean comparePdfDictionaries(PdfDictionary dictionary1, PdfDictionary dictionary2,
            Set<PdfObject> visitedObjects) {
        Set<Map.Entry<PdfName, PdfObject>> entrySet1 = dictionary1.entrySet();
        Set<Map.Entry<PdfName, PdfObject>> entrySet2 = dictionary2.entrySet();
        if (entrySet1.size() != entrySet2.size()) {
            return false;
        }
        for (Map.Entry<PdfName, PdfObject> entry1 : entrySet1) {
            if (!entrySet2.stream().anyMatch(entry2 -> entry2.getKey().equals(entry1.getKey()) &&
                            comparePdfObjects(entry2.getValue(), entry1.getValue(), visitedObjects))) {
                return false;
            }
        }
        return true;
    }

    private static boolean comparePdfStreams(PdfStream stream1, PdfStream stream2, Set<PdfObject> visitedObjects) {
        return Arrays.equals(stream1.getBytes(), stream2.getBytes()) &&
                comparePdfDictionaries(stream1, stream2, visitedObjects);
    }

    private static boolean isSameReference(PdfIndirectReference indirectReference1,
            PdfIndirectReference indirectReference2) {
        if (indirectReference1 == indirectReference2) {
            return true;
        }
        if (indirectReference1 == null || indirectReference2 == null) {
            return false;
        }
        return indirectReference1.getObjNumber() == indirectReference2.getObjNumber() &&
                indirectReference1.getGenNumber() == indirectReference2.getGenNumber();
    }

    private static boolean isMaxGenerationObject(PdfIndirectReference indirectReference) {
        return indirectReference.getObjNumber() == 0 && indirectReference.getGenNumber() == 65535;
    }

    private static PdfIndirectReference getIndirectReferenceOrNull(Supplier<PdfIndirectReference> referenceGetter) {
        try {
            return referenceGetter.get();
        } catch (Exception ignored) {
            return null;
        }
    }

    private static class ReferencesPair {
        private final PdfIndirectReference currentReference;
        private final PdfIndirectReference previousReference;

        ReferencesPair(PdfIndirectReference currentReference, PdfIndirectReference previousReference) {
            this.currentReference = currentReference;
            this.previousReference = previousReference;
        }

        public PdfIndirectReference getCurrentReference() {
            return currentReference;
        }

        public PdfIndirectReference getPreviousReference() {
            return previousReference;
        }
    }
}
