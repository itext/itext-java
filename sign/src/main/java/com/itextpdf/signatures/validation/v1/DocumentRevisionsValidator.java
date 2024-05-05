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
package com.itextpdf.signatures.validation.v1;

import com.itextpdf.commons.actions.contexts.IMetaInfo;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PdfFormAnnotationUtil;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.source.RASInputStream;
import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.WindowRandomAccessSource;
import com.itextpdf.kernel.pdf.DocumentProperties;
import com.itextpdf.kernel.pdf.DocumentRevision;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfRevisionsReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.signatures.PdfSignature;
import com.itextpdf.signatures.SignatureUtil;
import com.itextpdf.signatures.validation.v1.report.ReportItem;
import com.itextpdf.signatures.validation.v1.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
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

    static final String ACROFORM_REMOVED = "AcroForm dictionary was removed from catalog.";
    static final String ANNOTATIONS_MODIFIED = "Field annotations were removed, added or unexpectedly modified.";
    static final String DEVELOPER_EXTENSION_REMOVED =
            "Developer extension \"{0}\" dictionary was removed or unexpectedly modified.";
    static final String DIRECT_OBJECT = "{0} must be an indirect reference.";
    static final String DSS_REMOVED = "DSS dictionary was removed from catalog.";
    static final String EXTENSIONS_REMOVED = "Extensions dictionary was removed from the catalog.";
    static final String EXTENSIONS_TYPE = "Developer extensions must be a dictionary.";
    static final String EXTENSION_LEVEL_DECREASED =
            "Extension level number in developer extension \"{0}\" dictionary was decreased.";
    static final String FIELD_REMOVED = "Form field {0} was removed or unexpectedly modified.";
    static final String NOT_ALLOWED_ACROFORM_CHANGES = "PDF document AcroForm contains changes other than " +
            "document timestamp (docMDP level >= 1), form fill-in and digital signatures (docMDP level >= 2), " +
            "adding or editing annotations (docMDP level 3), which are not allowed.";
    static final String NOT_ALLOWED_CATALOG_CHANGES = "PDF document catalog contains changes other than " +
            "DSS dictionary and DTS addition (docMDP level >= 1), " +
            "form fill-in and digital signatures (docMDP level >= 2), " +
            "adding or editing annotations (docMDP level 3).";
    static final String OBJECT_REMOVED =
            "Object \"{0}\", which is not allowed to be removed, was removed from the document through XREF table.";
    static final String PAGES_MODIFIED = "Pages structure was unexpectedly modified.";
    static final String PAGE_ANNOTATIONS_MODIFIED = "Page annotations were unexpectedly modified.";
    static final String PAGE_MODIFIED = "Page was unexpectedly modified.";
    static final String PERMISSIONS_REMOVED = "Permissions dictionary was removed from the catalog.";
    static final String PERMISSIONS_TYPE = "Permissions must be a dictionary.";
    static final String PERMISSION_REMOVED = "Permission \"{0}\" dictionary was removed or unexpectedly modified.";
    static final String REFERENCE_REMOVED = "Signature reference dictionary was removed or unexpectedly modified.";
    static final String SIGNATURE_MODIFIED = "Signature {0} was unexpectedly modified.";
    static final String UNEXPECTED_ENTRY_IN_XREF =
            "New PDF document revision contains unexpected entry \"{0}\" in XREF table.";
    static final String REVISIONS_RETRIEVAL_FAILED = "Wasn't possible to retrieve document revisions.";
    static final String DOCUMENT_WITHOUT_SIGNATURES = "Document doesn't contain any signatures.";
    static final String TOO_MANY_CERTIFICATION_SIGNATURES = "Document contains more than one certification signature.";
    static final String SIGNATURE_REVISION_NOT_FOUND =
            "Not possible to identify document revision corresponding to the first signature in the document.";
    static final String ACCESS_PERMISSIONS_ADDED = "Access permissions level specified for \"{0}\" approval signature "
            + "is higher than previous one specified. These access permissions will be ignored.";
    static final String UNKNOWN_ACCESS_PERMISSIONS = "Access permissions level number specified for \"{0}\" signature "
            + "is undefined. Default level 2 will be used instead.";
    static final String UNEXPECTED_FORM_FIELD = "New PDF document revision contains unexpected form field \"{0}\".";

    private IMetaInfo metaInfo = new ValidationMetaInfo();
    private AccessPermissions accessPermissions = AccessPermissions.ANNOTATION_MODIFICATION;
    private AccessPermissions requestedAccessPermissions = AccessPermissions.UNSPECIFIED;
    private final PdfDocument document;

    DocumentRevisionsValidator(PdfDocument document) {
        this.document = document;
    }

    /**
     * Sets the {@link IMetaInfo} that will be used during {@link PdfDocument} creation.
     *
     * @param metaInfo meta info to set
     *
     * @return the same {@link DocumentRevisionsValidator} instance
     */
    public DocumentRevisionsValidator setEventCountingMetaInfo(IMetaInfo metaInfo) {
        this.metaInfo = metaInfo;
        return this;
    }

    /**
     * Set access permissions to be used during docMDP validation.
     * If value is provided, related signature fields will be ignored during the validation.
     *
     * @param accessPermissions {@link AccessPermissions} docMDP validation level
     *
     * @return the same {@link DocumentRevisionsValidator} instance
     */
    public DocumentRevisionsValidator setAccessPermissions(AccessPermissions accessPermissions) {
        this.requestedAccessPermissions = accessPermissions;
        return this;
    }

    /**
     * Validate all document revisions according to docMDP and fieldMDP transform methods.
     *
     * @return {@link ValidationReport} which contains detailed validation results
     */
    public ValidationReport validateAllDocumentRevisions() {
        ValidationReport report = new ValidationReport();
        PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
        revisionsReader.setEventCountingMetaInfo(metaInfo);
        List<DocumentRevision> documentRevisions;
        try {
            documentRevisions = revisionsReader.getAllRevisions();
        } catch (IOException e) {
            report.addReportItem(
                    new ReportItem(DOC_MDP_CHECK, REVISIONS_RETRIEVAL_FAILED, ReportItemStatus.INVALID));
            return report;
        }
        SignatureUtil signatureUtil = new SignatureUtil(document);
        List<String> signatures = new ArrayList<>(signatureUtil.getSignatureNames());
        if (signatures.isEmpty()) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, DOCUMENT_WITHOUT_SIGNATURES, ReportItemStatus.INFO));
            return report;
        }
        boolean signatureFound = false;
        boolean certificationSignatureFound = false;
        PdfSignature currentSignature = signatureUtil.getSignature(signatures.get(0));
        for (int i = 0; i < documentRevisions.size() - 1; i++) {
            if (currentSignature != null &&
                    revisionContainsSignature(documentRevisions.get(i), signatures.get(0))) {
                signatureFound = true;
                if (isCertificationSignature(currentSignature)) {
                    if (certificationSignatureFound) {
                        report.addReportItem(new ReportItem(DOC_MDP_CHECK,
                                TOO_MANY_CERTIFICATION_SIGNATURES, ReportItemStatus.INDETERMINATE));
                    } else {
                        certificationSignatureFound = true;
                        updateCertificationSignatureAccessPermissions(currentSignature, report);
                    }
                }
                updateApprovalSignatureAccessPermissions(
                        signatureUtil.getSignatureFormFieldDictionary(signatures.get(0)), report);
                signatures.remove(0);
                if (signatures.isEmpty()) {
                    currentSignature = null;
                } else {
                    currentSignature = signatureUtil.getSignature(signatures.get(0));
                }
            }
            if (signatureFound) {
                validateRevision(documentRevisions.get(i), documentRevisions.get(i + 1), report);
            }
        }
        if (!signatureFound) {
            report.addReportItem(
                    new ReportItem(DOC_MDP_CHECK, SIGNATURE_REVISION_NOT_FOUND, ReportItemStatus.INVALID));
        }
        return report;
    }

    ValidationReport validateRevision(DocumentRevision previousRevision, DocumentRevision currentRevision,
            ValidationReport validationReport) {
        try (InputStream previousInputStream = createInputStreamFromRevision(document, previousRevision);
                PdfReader previousReader = new PdfReader(previousInputStream);
                PdfDocument documentWithoutRevision = new PdfDocument(previousReader,
                        new DocumentProperties().setEventCountingMetaInfo(metaInfo));
                InputStream currentInputStream = createInputStreamFromRevision(document, currentRevision);
                PdfReader currentReader = new PdfReader(currentInputStream);
                PdfDocument documentWithRevision = new PdfDocument(currentReader,
                        new DocumentProperties().setEventCountingMetaInfo(metaInfo))) {
            Set<PdfIndirectReference> indirectReferences = currentRevision.getModifiedObjects();
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
                                    reference.getPreviousReference().getObjNumber() == indirectReference.getObjNumber()
                                    && (reference.getCurrentReference() == null || reference.getCurrentReference()
                                    .getObjNumber() != indirectReference.getObjNumber()));
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
        } catch (IOException exception) {
            // error
        }
        return validationReport;
    }

    AccessPermissions getAccessPermissions() {
        return requestedAccessPermissions == AccessPermissions.UNSPECIFIED ? accessPermissions :
                requestedAccessPermissions;
    }

    private static InputStream createInputStreamFromRevision(PdfDocument originalDocument, DocumentRevision revision) {
        RandomAccessFileOrArray raf = originalDocument.getReader().getSafeFile();
        WindowRandomAccessSource source = new WindowRandomAccessSource(
                raf.createSourceView(), 0, revision.getEofOffset());
        return new RASInputStream(source);
    }

    private void updateApprovalSignatureAccessPermissions(PdfDictionary signatureField, ValidationReport report) {
        PdfDictionary fieldLock = signatureField.getAsDictionary(PdfName.Lock);
        if (fieldLock == null || fieldLock.getAsNumber(PdfName.P) == null) {
            return;
        }
        PdfNumber p = fieldLock.getAsNumber(PdfName.P);
        AccessPermissions newAccessPermissions;
        switch (p.intValue()) {
            case 1:
                newAccessPermissions = AccessPermissions.NO_CHANGES_PERMITTED;
                break;
            case 2:
                newAccessPermissions = AccessPermissions.FORM_FIELDS_MODIFICATION;
                break;
            case 3:
                newAccessPermissions = AccessPermissions.ANNOTATION_MODIFICATION;
                break;
            default:
                // Do nothing.
                return;
        }
        if (accessPermissions.compareTo(newAccessPermissions) < 0) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(ACCESS_PERMISSIONS_ADDED,
                    signatureField.get(PdfName.T)), ReportItemStatus.INDETERMINATE));
        } else {
            accessPermissions = newAccessPermissions;
        }
    }

    private void updateCertificationSignatureAccessPermissions(PdfSignature signature, ValidationReport report) {
        PdfArray references = signature.getPdfObject().getAsArray(PdfName.Reference);
        for (PdfObject reference : references) {
            PdfDictionary referenceDict = (PdfDictionary) reference;
            PdfName transformMethod = referenceDict.getAsName(PdfName.TransformMethod);
            if (PdfName.DocMDP.equals(transformMethod)) {
                PdfDictionary transformParameters = referenceDict.getAsDictionary(PdfName.TransformParams);
                if (transformParameters == null || transformParameters.getAsNumber(PdfName.P) == null) {
                    accessPermissions = AccessPermissions.FORM_FIELDS_MODIFICATION;
                    return;
                }
                PdfNumber p = transformParameters.getAsNumber(PdfName.P);
                switch (p.intValue()) {
                    case 1:
                        accessPermissions = AccessPermissions.NO_CHANGES_PERMITTED;
                        break;
                    case 2:
                        accessPermissions = AccessPermissions.FORM_FIELDS_MODIFICATION;
                        break;
                    case 3:
                        accessPermissions = AccessPermissions.ANNOTATION_MODIFICATION;
                        break;
                    default:
                        report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(
                                UNKNOWN_ACCESS_PERMISSIONS, signature.getName()), ReportItemStatus.INDETERMINATE));
                        accessPermissions = AccessPermissions.FORM_FIELDS_MODIFICATION;
                        break;
                }
                return;
            }
        }
    }

    private boolean isCertificationSignature(PdfSignature signature) {
        if (PdfName.DocTimeStamp.equals(signature.getType()) || PdfName.ETSI_RFC3161.equals(signature.getSubFilter())) {
            // Timestamp is never a certification signature.
            return false;
        }
        PdfArray references = signature.getPdfObject().getAsArray(PdfName.Reference);
        if (references != null) {
            for (PdfObject reference : references) {
                if (reference instanceof PdfDictionary) {
                    PdfDictionary referenceDict = (PdfDictionary) reference;
                    PdfName transformMethod = referenceDict.getAsName(PdfName.TransformMethod);
                    return PdfName.DocMDP.equals(transformMethod);
                }
            }
        }
        return false;
    }

    private boolean revisionContainsSignature(DocumentRevision revision, String signature) {
        try (InputStream inputStream = createInputStreamFromRevision(document, revision);
                PdfReader reader = new PdfReader(inputStream);
                PdfDocument documentWithRevision = new PdfDocument(reader,
                        new DocumentProperties().setEventCountingMetaInfo(metaInfo))) {
            SignatureUtil signatureUtil = new SignatureUtil(documentWithRevision);
            return signatureUtil.signatureCoversWholeDocument(signature);
        } catch (IOException ignored) {
        }
        return false;
    }

    private boolean compareCatalogs(PdfDocument documentWithoutRevision, PdfDocument documentWithRevision,
                                    ValidationReport report) {
        PdfDictionary previousCatalog = documentWithoutRevision.getCatalog().getPdfObject();
        PdfDictionary currentCatalog = documentWithRevision.getCatalog().getPdfObject();

        PdfDictionary previousCatalogCopy = copyCatalogEntriesToCompare(previousCatalog);
        PdfDictionary currentCatalogCopy = copyCatalogEntriesToCompare(currentCatalog);

        if (!comparePdfObjects(previousCatalogCopy, currentCatalogCopy)) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, NOT_ALLOWED_CATALOG_CHANGES, ReportItemStatus.INVALID));
            return false;
        }
        return compareExtensions(previousCatalog.get(PdfName.Extensions),
                currentCatalog.get(PdfName.Extensions), report) &&
                comparePermissions(previousCatalog.get(PdfName.Perms), currentCatalog.get(PdfName.Perms), report) &&
                compareDss(previousCatalog.get(PdfName.DSS), currentCatalog.get(PdfName.DSS), report) &&
                comparePages(previousCatalog.getAsDictionary(PdfName.Pages),
                        currentCatalog.getAsDictionary(PdfName.Pages), report) &&
                compareAcroForms(previousCatalog.getAsDictionary(PdfName.AcroForm),
                        currentCatalog.getAsDictionary(PdfName.AcroForm), report);
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
        if (currentDssDictionary != null) {
            PdfDictionary previousDssDictionary =
                    documentWithoutRevision.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
            allowedReferences.add(new ReferencesPair(currentDssDictionary.getIndirectReference(),
                    getIndirectReferenceOrNull(() -> previousDssDictionary.getIndirectReference())));

            allowedReferences.addAll(createAllowedDssEntries(documentWithRevision, documentWithoutRevision));
        }

        PdfDictionary currentAcroForm =
                documentWithRevision.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);
        if (currentAcroForm != null) {
            PdfDictionary previousAcroForm =
                    documentWithoutRevision.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);

            allowedReferences.add(new ReferencesPair(currentAcroForm.getIndirectReference(),
                    getIndirectReferenceOrNull(() -> previousAcroForm.getIndirectReference())));

            allowedReferences.addAll(createAllowedAcroFormEntries(documentWithRevision, documentWithoutRevision));
        }

        PdfDictionary currentPagesDictionary =
                documentWithRevision.getCatalog().getPdfObject().getAsDictionary(PdfName.Pages);
        if (currentPagesDictionary != null) {
            PdfDictionary previousPagesDictionary =
                    documentWithoutRevision.getCatalog().getPdfObject().getAsDictionary(PdfName.Pages);
            allowedReferences.add(new ReferencesPair(currentPagesDictionary.getIndirectReference(),
                    getIndirectReferenceOrNull(() -> previousPagesDictionary.getIndirectReference())));

            allowedReferences.addAll(createAllowedPagesEntries(currentPagesDictionary, previousPagesDictionary));
        }

        return allowedReferences;
    }

    private boolean checkAllowedReferences(List<ReferencesPair> allowedReferences,
                                           PdfIndirectReference indirectReference,
                                           PdfDocument documentWithoutRevision) {
        for (ReferencesPair allowedReference : allowedReferences) {
            if (isSameReference(allowedReference.getCurrentReference(), indirectReference)) {
                return documentWithoutRevision.getPdfObject(indirectReference.getObjNumber()) == null ||
                        allowedReferences.stream().anyMatch(
                                reference -> isSameReference(reference.getPreviousReference(), indirectReference));
            }
        }
        return false;
    }

    // Compare catalogs nested methods section:

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
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, EXTENSIONS_TYPE, ReportItemStatus.INVALID));
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

    private boolean comparePermissions(PdfObject previousPerms, PdfObject currentPerms, ValidationReport report) {
        if (previousPerms == null || comparePdfObjects(previousPerms, currentPerms)) {
            return true;
        }
        if (currentPerms == null) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, PERMISSIONS_REMOVED, ReportItemStatus.INVALID));
            return false;
        }
        if (!(previousPerms instanceof PdfDictionary) || !(currentPerms instanceof PdfDictionary)) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, PERMISSIONS_TYPE, ReportItemStatus.INVALID));
            return false;
        }
        PdfDictionary previousPermsDictionary = (PdfDictionary) previousPerms;
        PdfDictionary currentPermsDictionary = (PdfDictionary) currentPerms;
        for (Map.Entry<PdfName, PdfObject> previousPermission : previousPermsDictionary.entrySet()) {
            PdfDictionary currentPermission = currentPermsDictionary.getAsDictionary(previousPermission.getKey());
            if (currentPermission == null) {
                report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(
                        PERMISSION_REMOVED, previousPermission.getKey()), ReportItemStatus.INVALID));
                return false;
            } else {
                // Perms dictionary is the signature dictionary.
                if (!compareSignatureDictionaries(previousPermission.getValue(), currentPermission, report)) {
                    report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(
                            PERMISSION_REMOVED, previousPermission.getKey()), ReportItemStatus.INVALID));
                    return false;
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

    private boolean comparePages(PdfDictionary prevPages, PdfDictionary currPages, ValidationReport report) {
        if (prevPages == null ^ currPages == null) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, PAGES_MODIFIED, ReportItemStatus.INVALID));
            return false;
        }
        if (prevPages == null) {
            return true;
        }
        PdfDictionary previousPagesCopy = new PdfDictionary(prevPages);
        previousPagesCopy.remove(PdfName.Kids);
        previousPagesCopy.remove(PdfName.Parent);
        PdfDictionary currentPagesCopy = new PdfDictionary(currPages);
        currentPagesCopy.remove(PdfName.Kids);
        currentPagesCopy.remove(PdfName.Parent);
        if (!comparePdfObjects(previousPagesCopy, currentPagesCopy) ||
                !compareIndirectReferencesObjNums(prevPages.get(PdfName.Parent), currPages.get(PdfName.Parent), report,
                        "Page tree node parent")) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, PAGES_MODIFIED, ReportItemStatus.INVALID));
            return false;
        }

        PdfArray prevKids = prevPages.getAsArray(PdfName.Kids);
        PdfArray currKids = currPages.getAsArray(PdfName.Kids);
        if (prevKids.size() != currKids.size()) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, PAGES_MODIFIED, ReportItemStatus.INVALID));
            return false;
        }
        for (int i = 0; i < currKids.size(); ++i) {
            PdfDictionary previousKid = prevKids.getAsDictionary(i);
            PdfDictionary currentKid = currKids.getAsDictionary(i);
            if (PdfName.Pages.equals(previousKid.getAsName(PdfName.Type))) {
                // Compare page tree nodes.
                if (!comparePages(previousKid, currentKid, report)) {
                    return false;
                }
            } else {
                // Compare page objects (leaf node in the page tree).
                PdfDictionary previousPageCopy = new PdfDictionary(previousKid);
                previousPageCopy.remove(PdfName.Annots);
                previousPageCopy.remove(PdfName.Parent);
                PdfDictionary currentPageCopy = new PdfDictionary(currentKid);
                currentPageCopy.remove(PdfName.Annots);
                currentPageCopy.remove(PdfName.Parent);
                if (!comparePdfObjects(previousPageCopy, currentPageCopy) || !compareIndirectReferencesObjNums(
                        previousKid.get(PdfName.Parent), currentKid.get(PdfName.Parent), report, "Page parent")) {
                    report.addReportItem(new ReportItem(DOC_MDP_CHECK, PAGE_MODIFIED, ReportItemStatus.INVALID));
                    return false;
                }

                PdfArray prevAnnots = getAnnotsNotAllowedToBeModified(previousKid);
                PdfArray currAnnots = getAnnotsNotAllowedToBeModified(currentKid);
                if (!comparePdfObjects(prevAnnots, currAnnots)) {
                    report.addReportItem(new ReportItem(DOC_MDP_CHECK, PAGE_ANNOTATIONS_MODIFIED,
                            ReportItemStatus.INVALID));
                    return false;
                }
            }
        }
        return true;
    }

    private boolean compareAcroForms(PdfDictionary prevAcroForm, PdfDictionary currAcroForm, ValidationReport report) {
        if (prevAcroForm == null) {
            if (currAcroForm == null) {
                return true;
            }
            PdfArray fields = currAcroForm.getAsArray(PdfName.Fields);
            for (PdfObject field : fields) {
                PdfDictionary fieldDict = (PdfDictionary) field;
                if (!isAllowedSignatureField(fieldDict, report)) {
                    report.addReportItem(new ReportItem(DOC_MDP_CHECK, NOT_ALLOWED_ACROFORM_CHANGES,
                            ReportItemStatus.INVALID));
                    return false;
                }
            }
            return true;
        }
        if (currAcroForm == null) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, ACROFORM_REMOVED, ReportItemStatus.INVALID));
            return false;
        }

        PdfDictionary previousAcroFormCopy = copyAcroformDictionary(prevAcroForm);
        PdfDictionary currentAcroFormCopy = copyAcroformDictionary(currAcroForm);

        PdfArray prevFields = prevAcroForm.getAsArray(PdfName.Fields);
        PdfArray currFields = currAcroForm.getAsArray(PdfName.Fields);

        if (!comparePdfObjects(previousAcroFormCopy, currentAcroFormCopy) ||
                (prevFields.size() > currFields.size()) ||
                !compareFormFields(prevFields, currFields, report)) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, NOT_ALLOWED_ACROFORM_CHANGES, ReportItemStatus.INVALID));
            return false;
        }

        return true;
    }

    private boolean compareFormFields(PdfArray prevFields, PdfArray currFields, ValidationReport report) {
        Map<String, PdfDictionary> prevFieldsMap = populateFormFieldsMap(prevFields);
        Map<String, PdfDictionary> currFieldsMap = populateFormFieldsMap(currFields);

        for (Map.Entry<String, PdfDictionary> fieldEntry : prevFieldsMap.entrySet()) {
            PdfDictionary previousField = fieldEntry.getValue();
            PdfDictionary currentField = currFieldsMap.get(fieldEntry.getKey());
            if (currentField == null || !compareFields(previousField, currentField, report)) {
                report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(FIELD_REMOVED,
                        fieldEntry.getKey()), ReportItemStatus.INVALID));
                return false;
            }
            currFieldsMap.remove(fieldEntry.getKey());
        }

        for (Map.Entry<String, PdfDictionary> fieldEntry : currFieldsMap.entrySet()) {
            if (!isAllowedSignatureField(fieldEntry.getValue(), report)) {
                return false;
            }
        }
        return compareAnnotations(prevFields, currFields, report);
    }

    /**
     * DocMDP level >= 2 allows setting values of the fields and accordingly update the widget appearances of them. But
     * you cannot change the form structure, so it is not allowed to add, remove or rename fields, change most of their
     * properties.
     *
     * @param previousField field from the previous revision to check
     * @param currentField  field from the current revision to check
     * @param report        validation report
     *
     * @return {@code true} if the changes of the field are allowed, {@code false} otherwise.
     */
    private boolean compareFields(PdfDictionary previousField, PdfDictionary currentField, ValidationReport report) {
        PdfDictionary prevFormDict = copyFieldDictionary(previousField);
        PdfDictionary currFormDict = copyFieldDictionary(currentField);
        if (!comparePdfObjects(prevFormDict, currFormDict) ||
                !compareIndirectReferencesObjNums(prevFormDict.get(PdfName.Parent), currFormDict.get(PdfName.Parent),
                        report, "Form field parent") || !compareIndirectReferencesObjNums(
                prevFormDict.get(PdfName.P), currFormDict.get(PdfName.P), report,
                "Page object with which field annotation is associated")) {
            return false;
        }

        PdfObject prevValue = previousField.get(PdfName.V);
        PdfObject currValue = currentField.get(PdfName.V);
        if (prevValue == null && currValue == null && PdfName.Ch.equals(currentField.getAsName(PdfName.FT))) {
            // Choice field: if the items in the I entry differ from those in the V entry, the V entry shall be used.
            prevValue = previousField.get(PdfName.I);
            currValue = currentField.get(PdfName.I);
        }

        if (PdfName.Sig.equals(currentField.getAsName(PdfName.FT))) {
            if (!compareSignatureDictionaries(prevValue, currValue, report)) {
                report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(
                        SIGNATURE_MODIFIED, currentField.getAsString(PdfName.T).getValue()), ReportItemStatus.INVALID));
                return false;
            }
        } else if (getAccessPermissions() == AccessPermissions.NO_CHANGES_PERMITTED
                && !comparePdfObjects(prevValue, currValue)) {
            return false;
        }

        return compareFormFields(previousField.getAsArray(PdfName.Kids), currentField.getAsArray(PdfName.Kids), report);
    }

    private boolean compareAnnotations(PdfArray prevFields, PdfArray currFields, ValidationReport report) {
        List<PdfDictionary> prevAnnots = populateAnnotations(prevFields);
        List<PdfDictionary> currAnnots = populateAnnotations(currFields);
        if (prevAnnots.size() != currAnnots.size()) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, ANNOTATIONS_MODIFIED, ReportItemStatus.INVALID));
            return false;
        }
        for (int i = 0; i < prevAnnots.size(); i++) {
            PdfDictionary prevAnnot = new PdfDictionary(prevAnnots.get(i));
            removeAppearanceRelatedProperties(prevAnnot);
            PdfDictionary currAnnot = new PdfDictionary(currAnnots.get(i));
            removeAppearanceRelatedProperties(currAnnot);
            if (!comparePdfObjects(prevAnnot, currAnnot) || !compareIndirectReferencesObjNums(
                    prevAnnots.get(i).get(PdfName.P), currAnnots.get(i).get(PdfName.P), report,
                    "Page object with which annotation is associated")) {
                report.addReportItem(new ReportItem(DOC_MDP_CHECK, ANNOTATIONS_MODIFIED, ReportItemStatus.INVALID));
                return false;
            }
        }
        return true;
    }

    private boolean compareSignatureDictionaries(PdfObject prevSigDict, PdfObject curSigDict, ValidationReport report) {
        if (prevSigDict == null) {
            return true;
        }
        if (curSigDict == null) {
            return false;
        }
        if (!(prevSigDict instanceof PdfDictionary) || !(curSigDict instanceof PdfDictionary)) {
            return false;
        }
        PdfDictionary currentSigDictCopy = new PdfDictionary((PdfDictionary) curSigDict);
        currentSigDictCopy.remove(PdfName.Reference);
        PdfDictionary previousSigDictCopy = new PdfDictionary((PdfDictionary) prevSigDict);
        previousSigDictCopy.remove(PdfName.Reference);
        // Apart from the reference, dictionaries are expected to be equal.
        if (!comparePdfObjects(previousSigDictCopy, currentSigDictCopy)) {
            return false;
        }
        PdfArray previousReference = ((PdfDictionary) prevSigDict).getAsArray(PdfName.Reference);
        PdfArray currentReference = ((PdfDictionary) curSigDict).getAsArray(PdfName.Reference);
        return compareSignatureReferenceDictionaries(previousReference, currentReference, report);
    }

    private boolean compareSignatureReferenceDictionaries(PdfArray previousReferences, PdfArray currentReferences,
                                                          ValidationReport report) {
        if (previousReferences == null || comparePdfObjects(previousReferences, currentReferences)) {
            return true;
        }
        if (currentReferences == null || previousReferences.size() != currentReferences.size()) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, REFERENCE_REMOVED, ReportItemStatus.INVALID));
            return false;
        } else {
            for (int i = 0; i < previousReferences.size(); ++i) {
                PdfDictionary currentReferenceCopy = new PdfDictionary(currentReferences.getAsDictionary(i));
                currentReferenceCopy.remove(PdfName.Data);
                PdfDictionary previousReferenceCopy = new PdfDictionary(previousReferences.getAsDictionary(i));
                previousReferenceCopy.remove(PdfName.Data);
                // Apart from the data, dictionaries are expected to be equal. Data is an indirect reference
                // to the object in the document upon which the object modification analysis should be performed.
                if (!comparePdfObjects(previousReferenceCopy, currentReferenceCopy) ||
                        !compareIndirectReferencesObjNums(previousReferences.getAsDictionary(i).get(PdfName.Data),
                                currentReferences.getAsDictionary(i).get(PdfName.Data), report,
                                "Data entry in the signature reference dictionary")) {
                    report.addReportItem(new ReportItem(DOC_MDP_CHECK, REFERENCE_REMOVED, ReportItemStatus.INVALID));
                    return false;
                }
            }
        }
        return true;
    }

    private boolean compareIndirectReferencesObjNums(PdfObject prevObj, PdfObject currObj, ValidationReport report,
                                                     String type) {
        if (prevObj == null ^ currObj == null) {
            return false;
        }
        if (prevObj == null) {
            return true;
        }
        PdfIndirectReference prevObjRef = prevObj.getIndirectReference();
        PdfIndirectReference currObjRef = currObj.getIndirectReference();
        if (prevObjRef == null || currObjRef == null) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(DIRECT_OBJECT, type),
                    ReportItemStatus.INVALID));
            return false;
        }
        return isSameReference(prevObjRef, currObjRef);
    }

    /**
     * DocMDP level <=2 allows adding new fields in the following cases:
     * docMDP level 1: allows adding only DocTimeStamp signature fields;
     * docMDP level 2: same as level 1 and also adding and then signing signature fields,
     * so signature dictionary shouldn't be null.
     *
     * @param field  newly added field entry
     * @param report validation report
     *
     * @return true if newly added field is allowed to be added, false otherwise.
     */
    private boolean isAllowedSignatureField(PdfDictionary field, ValidationReport report) {
        PdfDictionary value = field.getAsDictionary(PdfName.V);
        if (!PdfName.Sig.equals(field.getAsName(PdfName.FT)) || value == null ||
                (getAccessPermissions() == AccessPermissions.NO_CHANGES_PERMITTED
                        && !PdfName.DocTimeStamp.equals(value.getAsName(PdfName.Type)))) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(UNEXPECTED_FORM_FIELD,
                    field.getAsString(PdfName.T).getValue()), ReportItemStatus.INVALID));
            return false;
        }
        return true;
    }

    private Map<String, PdfDictionary> populateFormFieldsMap(PdfArray fieldsArray) {
        Map<String, PdfDictionary> fields = new HashMap<>();
        if (fieldsArray != null) {
            for (int i = 0; i < fieldsArray.size(); ++i) {
                PdfDictionary fieldDict = (PdfDictionary) fieldsArray.get(i);
                if (PdfFormField.isFormField(fieldDict)) {
                    String fieldName = fieldDict.getAsString(PdfName.T).getValue();
                    fields.put(fieldName, fieldDict);
                }
            }
        }
        return fields;
    }

    private List<PdfDictionary> populateAnnotations(PdfArray fieldsArray) {
        List<PdfDictionary> annotations = new ArrayList<>();
        if (fieldsArray != null) {
            for (int i = 0; i < fieldsArray.size(); ++i) {
                PdfDictionary annotDict = (PdfDictionary) fieldsArray.get(i);
                if (PdfFormAnnotationUtil.isPureWidget(annotDict)) {
                    annotations.add(annotDict);
                }
            }
        }
        return annotations;
    }

    private PdfArray getAnnotsNotAllowedToBeModified(PdfDictionary page) {
        PdfArray annots = page.getAsArray(PdfName.Annots);
        if (annots == null) {
            return null;
        }
        PdfArray annotsCopy = new PdfArray(annots);
        for (PdfObject annot : annots) {
            PdfDictionary annotDict = (PdfDictionary) annot;
            if (PdfFormAnnotationUtil.isPureWidgetOrMergedField(annotDict)) {
                // Ideally we should also distinguish between docMDP level 1 (DTS) or 2 allowed annotations
                // (we check them only on the acroform level, but they could be added to the page)
                annotsCopy.remove(annot);
            }
        }
        return annotsCopy;
    }

    private PdfDictionary copyCatalogEntriesToCompare(PdfDictionary catalog) {
        PdfDictionary catalogCopy = new PdfDictionary(catalog);
        catalogCopy.remove(PdfName.Metadata);
        catalogCopy.remove(PdfName.Extensions);
        catalogCopy.remove(PdfName.Perms);
        catalogCopy.remove(PdfName.DSS);
        catalogCopy.remove(PdfName.AcroForm);
        catalogCopy.remove(PdfName.Pages);
        return catalogCopy;
    }

    private PdfDictionary copyAcroformDictionary(PdfDictionary acroForm) {
        PdfDictionary acroFormCopy = new PdfDictionary(acroForm);
        acroFormCopy.remove(PdfName.Fields);
        acroFormCopy.remove(PdfName.DR);
        acroFormCopy.remove(PdfName.DA);
        return acroFormCopy;
    }

    private PdfDictionary copyFieldDictionary(PdfDictionary field) {
        PdfDictionary formDict = new PdfDictionary(field);
        formDict.remove(PdfName.V);
        // Value for the choice fields could be specified by the /I key.
        formDict.remove(PdfName.I);
        formDict.remove(PdfName.Parent);
        formDict.remove(PdfName.Kids);
        // Remove also annotation related properties (e.g. in case of the merged field).
        removeAppearanceRelatedProperties(formDict);
        return formDict;
    }

    private void removeAppearanceRelatedProperties(PdfDictionary annotDict) {
        annotDict.remove(PdfName.P);
        if (getAccessPermissions() != AccessPermissions.NO_CHANGES_PERMITTED) {
            annotDict.remove(PdfName.AP);
            annotDict.remove(PdfName.AS);
            annotDict.remove(PdfName.M);
            annotDict.remove(PdfName.F);
        }
    }

    // Allowed references creation nested methods section:

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

    private Collection<ReferencesPair> createAllowedPagesEntries(PdfDictionary currentPagesDictionary,
            PdfDictionary previousPagesDictionary) {
        List<ReferencesPair> allowedReferences = new ArrayList<>();
        PdfArray currentKids = currentPagesDictionary.getAsArray(PdfName.Kids);
        if (currentKids != null) {
            allowedReferences.add(new ReferencesPair(currentKids.getIndirectReference(), getIndirectReferenceOrNull(
                    () -> previousPagesDictionary.get(PdfName.Kids).getIndirectReference())));
            for (int i = 0; i < currentKids.size(); ++i) {
                int finalI = i;
                PdfDictionary currentPageNode = currentKids.getAsDictionary(i);
                PdfDictionary previousPageNode = null;
                try {
                    previousPageNode = previousPagesDictionary.getAsArray(PdfName.Kids).getAsDictionary(i);
                } catch (NullPointerException ignored) {
                }
                allowedReferences.add(new ReferencesPair(currentKids.get(i).getIndirectReference(),
                        getIndirectReferenceOrNull(() -> previousPagesDictionary.getAsArray(PdfName.Kids)
                                .get(finalI).getIndirectReference())));
                if (currentPageNode != null) {
                    if (PdfName.Pages.equals(currentPageNode.getAsName(PdfName.Type))) {
                        allowedReferences.addAll(createAllowedPagesEntries(currentPageNode, previousPageNode));
                    } else {
                        PdfObject currentAnnots = currentPageNode.get(PdfName.Annots);
                        if (currentAnnots != null) {
                            allowedReferences.add(new ReferencesPair(currentAnnots.getIndirectReference(),
                                    getIndirectReferenceOrNull(
                                            () -> previousPagesDictionary.getAsArray(PdfName.Kids)
                                                    .getAsDictionary(finalI).get(PdfName.Annots)
                                                    .getIndirectReference())));
                        }
                    }
                }
            }
        }
        // We don't need to add annotations because all the allowed ones are already added during acroform processing.
        return allowedReferences;
    }

    private Collection<ReferencesPair> createAllowedAcroFormEntries(PdfDocument documentWithRevision,
                                                                    PdfDocument documentWithoutRevision) {
        List<ReferencesPair> allowedReferences = new ArrayList<>();
        PdfAcroForm prevAcroForm = PdfFormCreator.getAcroForm(documentWithoutRevision, false);
        PdfAcroForm currAcroForm = PdfFormCreator.getAcroForm(documentWithRevision, false);
        Map<String, PdfFormField> prevFields = prevAcroForm == null ? new HashMap<>() : prevAcroForm.getAllFormFields();
        for (Map.Entry<String, PdfFormField> fieldEntry : currAcroForm.getAllFormFields().entrySet()) {
            PdfFormField previousField = prevFields.get(fieldEntry.getKey());
            PdfFormField currentField = fieldEntry.getValue();
            PdfObject value = currentField.getValue();
            if (getAccessPermissions() != AccessPermissions.NO_CHANGES_PERMITTED || (value instanceof PdfDictionary &&
                    PdfName.DocTimeStamp.equals(((PdfDictionary) value).getAsName(PdfName.Type)))) {
                allowedReferences.add(new ReferencesPair(currentField.getPdfObject().getIndirectReference(),
                        getIndirectReferenceOrNull(() -> previousField.getPdfObject().getIndirectReference())));
                if (previousField == null) {
                    // For newly generated form field all references are allowed to be added.
                    addAllNestedDictionaryEntries(allowedReferences, currentField.getPdfObject(), null);
                } else {
                    // For already existing form field only several entries are allowed to be updated.
                    allowedReferences.addAll(createAllowedExistingFormFieldEntries(currentField, previousField));
                }
            }
        }

        PdfDictionary currentResources = currAcroForm.getDefaultResources();
        if (currentResources != null) {
            allowedReferences.add(new ReferencesPair(currentResources.getIndirectReference(),
                    getIndirectReferenceOrNull(() -> prevAcroForm.getDefaultResources().getIndirectReference())));
            addAllNestedDictionaryEntries(allowedReferences, currentResources,
                    prevAcroForm == null ? null : prevAcroForm.getDefaultResources());
        }

        return allowedReferences;
    }

    private Collection<ReferencesPair> createAllowedExistingFormFieldEntries(PdfFormField currentField,
            PdfFormField previousField) {
        List<ReferencesPair> allowedReferences = new ArrayList<>();
        PdfObject currentValue = currentField.getValue();
        if (currentValue != null) {
            allowedReferences.add(new ReferencesPair(currentValue.getIndirectReference(),
                    getIndirectReferenceOrNull(() -> previousField.getValue().getIndirectReference())));
        }

        List<PdfFormAnnotation> currAnnots = currentField.getChildFormAnnotations();
        if (!currAnnots.isEmpty()) {
            List<PdfFormAnnotation> prevAnnots = previousField == null ? null :
                    previousField.getChildFormAnnotations();
            for (int i = 0; i < currAnnots.size(); i++) {
                int finalI = i;
                allowedReferences.add(new ReferencesPair(
                        currAnnots.get(i).getPdfObject().getIndirectReference(), getIndirectReferenceOrNull(
                        () -> prevAnnots.get(finalI).getPdfObject().getIndirectReference())));
                PdfObject currentAppearance = currAnnots.get(i).getPdfObject().get(PdfName.AP);
                if (currentAppearance != null) {
                    allowedReferences.add(new ReferencesPair(currentAppearance.getIndirectReference(),
                            getIndirectReferenceOrNull(() -> prevAnnots.get(finalI).getPdfObject()
                                    .get(PdfName.AP).getIndirectReference())));
                    if (currentAppearance instanceof PdfDictionary) {
                        PdfObject previousAppearance;
                        try {
                            previousAppearance = prevAnnots.get(finalI).getPdfObject().get(PdfName.AP);
                        } catch (NullPointerException e) {
                            previousAppearance = null;
                        }
                        addAllNestedDictionaryEntries(allowedReferences,
                                (PdfDictionary) currentAppearance, previousAppearance);
                    }
                }

                PdfObject currentAppearanceState = currAnnots.get(i).getPdfObject().get(PdfName.AS);
                if (currentAppearanceState != null) {
                    allowedReferences.add(new ReferencesPair(currentAppearanceState.getIndirectReference(),
                            getIndirectReferenceOrNull(() -> prevAnnots.get(finalI).getPdfObject()
                                    .get(PdfName.AS).getIndirectReference())));
                }
                PdfObject currentTimeStamp = currAnnots.get(i).getPdfObject().get(PdfName.M);
                if (currentTimeStamp != null) {
                    allowedReferences.add(new ReferencesPair(currentTimeStamp.getIndirectReference(),
                            getIndirectReferenceOrNull(() -> prevAnnots.get(finalI).getPdfObject()
                                    .get(PdfName.M).getIndirectReference())));
                }
            }
        }
        return allowedReferences;
    }

    private void addAllNestedDictionaryEntries(List<ReferencesPair> allowedReferences, PdfDictionary currentDictionary,
            PdfObject previousDictionary) {
        for (Map.Entry<PdfName, PdfObject> entry : currentDictionary.entrySet()) {
            PdfObject currValue = entry.getValue();
            if (currValue.getIndirectReference() != null && allowedReferences.stream().anyMatch(
                    pair -> isSameReference(pair.getCurrentReference(), currValue.getIndirectReference()))) {
                // Required to not end up in an infinite loop.
                continue;
            }
            PdfObject prevValue = previousDictionary instanceof PdfDictionary ?
                    ((PdfDictionary) previousDictionary).get(entry.getKey()) : null;
            allowedReferences.add(new ReferencesPair(currValue.getIndirectReference(),
                    getIndirectReferenceOrNull(() -> prevValue.getIndirectReference())));
            if (currValue instanceof PdfDictionary) {
                addAllNestedDictionaryEntries(allowedReferences, (PdfDictionary) currValue, prevValue);
            }
            if (currValue instanceof PdfArray) {
                addAllNestedArrayEntries(allowedReferences, (PdfArray) currValue, prevValue);
            }
        }
    }

    private void addAllNestedArrayEntries(List<ReferencesPair> allowedReferences, PdfArray currentArray,
            PdfObject previousArray) {
        for (int i = 0; i < currentArray.size(); ++i) {
            PdfObject currentArrayEntry = currentArray.get(i);
            if (currentArrayEntry.getIndirectReference() != null && allowedReferences.stream().anyMatch(
                    pair -> isSameReference(pair.getCurrentReference(), currentArrayEntry.getIndirectReference()))) {
                // Required to not end up in an infinite loop.
                continue;
            }
            PdfObject previousArrayEntry = previousArray instanceof PdfArray ? ((PdfArray) previousArray).get(i) : null;
            allowedReferences.add(new ReferencesPair(currentArrayEntry.getIndirectReference(),
                    getIndirectReferenceOrNull(() -> previousArrayEntry.getIndirectReference())));
            if (currentArrayEntry instanceof PdfDictionary) {
                addAllNestedDictionaryEntries(allowedReferences, currentArray.getAsDictionary(i), previousArrayEntry);
            }
            if (currentArrayEntry instanceof PdfArray) {
                addAllNestedArrayEntries(allowedReferences, currentArray.getAsArray(i), previousArrayEntry);
            }
        }
    }

    // Compare PDF objects util section:

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
                return comparePdfArrays((PdfArray) pdfObject1, (PdfArray) pdfObject2, visitedObjects);
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

    enum AccessPermissions {
        UNSPECIFIED,
        NO_CHANGES_PERMITTED,
        FORM_FIELDS_MODIFICATION,
        ANNOTATION_MODIFICATION
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
