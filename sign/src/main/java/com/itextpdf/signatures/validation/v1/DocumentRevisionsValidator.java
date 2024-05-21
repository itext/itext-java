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
import com.itextpdf.kernel.pdf.PdfReader.StrictnessLevel;
import com.itextpdf.kernel.pdf.PdfRevisionsReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.signatures.AccessPermissions;
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
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Validator, which is responsible for document revisions validation according to doc-MDP rules.
 */
class DocumentRevisionsValidator {
    static final String DOC_MDP_CHECK = "DocMDP check.";
    static final String FIELD_MDP_CHECK = "FieldMDP check.";

    static final String ACCESS_PERMISSIONS_ADDED = "Access permissions level specified for \"{0}\" approval signature "
            + "is higher than previous one specified. These access permissions will be ignored.";
    static final String ACROFORM_REMOVED = "AcroForm dictionary was removed from catalog.";
    static final String ANNOTATIONS_MODIFIED = "Field annotations were removed, added or unexpectedly modified.";
    static final String DEVELOPER_EXTENSION_REMOVED =
            "Developer extension \"{0}\" dictionary was removed or unexpectedly modified.";
    static final String DIRECT_OBJECT = "{0} must be an indirect reference.";
    static final String DOCUMENT_WITHOUT_SIGNATURES = "Document doesn't contain any signatures.";
    static final String DSS_REMOVED = "DSS dictionary was removed from catalog.";
    static final String EXTENSIONS_REMOVED = "Extensions dictionary was removed from the catalog.";
    static final String EXTENSIONS_TYPE = "Developer extensions must be a dictionary.";
    static final String EXTENSION_LEVEL_DECREASED =
            "Extension level number in developer extension \"{0}\" dictionary was decreased.";
    static final String FIELD_NOT_DICTIONARY =
            "Form field \"{0}\" or one of its widgets is not a dictionary. It will not be validated.";
    static final String FIELD_REMOVED = "Form field {0} was removed or unexpectedly modified.";
    static final String LOCKED_FIELD_KIDS_ADDED =
            "Kids were added to locked form field \"{0}\".";
    static final String LOCKED_FIELD_KIDS_REMOVED =
            "Kids were removed from locked form field \"{0}\" .";
    static final String LOCKED_FIELD_MODIFIED = "Locked form field \"{0}\" or one of its widgets was modified.";
    static final String LOCKED_FIELD_REMOVED = "Locked form field \"{0}\" was removed from the document.";
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
    static final String REVISIONS_READING_EXCEPTION = "IOException occurred during document revisions reading.";
    static final String REVISIONS_RETRIEVAL_FAILED = "Wasn't possible to retrieve document revisions.";
    static final String SIGNATURE_MODIFIED = "Signature {0} was unexpectedly modified.";
    static final String SIGNATURE_REVISION_NOT_FOUND =
            "Not possible to identify document revision corresponding to the first signature in the document.";
    static final String TOO_MANY_CERTIFICATION_SIGNATURES = "Document contains more than one certification signature.";
    static final String UNEXPECTED_ENTRY_IN_XREF =
            "New PDF document revision contains unexpected entry \"{0}\" in XREF table.";
    static final String UNEXPECTED_FORM_FIELD = "New PDF document revision contains unexpected form field \"{0}\".";
    static final String UNKNOWN_ACCESS_PERMISSIONS = "Access permissions level number specified for \"{0}\" signature "
            + "is undefined. Default level 2 will be used instead.";
    static final String UNRECOGNIZED_ACTION = "Signature field lock dictionary contains unrecognized "
            + "\"Action\" value \"{0}\". \"All\" will be used instead.";

    private IMetaInfo metaInfo = new ValidationMetaInfo();
    private AccessPermissions accessPermissions = AccessPermissions.ANNOTATION_MODIFICATION;
    private AccessPermissions requestedAccessPermissions = AccessPermissions.UNSPECIFIED;
    private final Set<String> lockedFields = new HashSet<>();
    private final PdfDocument document;
    private Set<PdfObject> checkedAnnots;
    private Set<PdfDictionary> newlyAddedFields;

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
     * If value is provided, access permission related signature parameters will be ignored during the validation.
     *
     * @param accessPermissions {@link AccessPermissions} docMDP validation level
     *
     * @return the same {@link DocumentRevisionsValidator} instance
     */
    public DocumentRevisionsValidator setAccessPermissions(AccessPermissions accessPermissions) {
        this.requestedAccessPermissions = accessPermissions;
        return this;
    }

    AccessPermissions getAccessPermissions() {
        return requestedAccessPermissions == AccessPermissions.UNSPECIFIED ? accessPermissions :
                requestedAccessPermissions;
    }

    /**
     * Validate all document revisions according to docMDP and fieldMDP transform methods.
     *
     * @return {@link ValidationReport} which contains detailed validation results.
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
                    new ReportItem(DOC_MDP_CHECK, REVISIONS_RETRIEVAL_FAILED, ReportItemStatus.INDETERMINATE));
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
                updateApprovalSignatureFieldLock(documentRevisions.get(i),
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

    //
    //
    // Revisions validation util section:
    //
    //

    void validateRevision(DocumentRevision previousRevision, DocumentRevision currentRevision,
                          ValidationReport validationReport) {
        try (InputStream previousInputStream = createInputStreamFromRevision(document, previousRevision);
             PdfReader previousReader = new PdfReader(previousInputStream)
                     .setStrictnessLevel(StrictnessLevel.CONSERVATIVE);
             PdfDocument documentWithoutRevision = new PdfDocument(previousReader,
                     new DocumentProperties().setEventCountingMetaInfo(metaInfo));
             InputStream currentInputStream = createInputStreamFromRevision(document, currentRevision);
             PdfReader currentReader = new PdfReader(currentInputStream)
                     .setStrictnessLevel(StrictnessLevel.CONSERVATIVE);
             PdfDocument documentWithRevision = new PdfDocument(currentReader,
                     new DocumentProperties().setEventCountingMetaInfo(metaInfo))) {
            Set<PdfIndirectReference> indirectReferences = currentRevision.getModifiedObjects();
            if (!compareCatalogs(documentWithoutRevision, documentWithRevision, validationReport)) {
                return;
            }
            Set<PdfIndirectReference> currentAllowedReferences = createAllowedReferences(documentWithRevision);
            Set<PdfIndirectReference> previousAllowedReferences = createAllowedReferences(documentWithoutRevision);
            for (PdfIndirectReference indirectReference : indirectReferences) {
                if (indirectReference.isFree()) {
                    // In this boolean flag we check that reference which is about to be removed is the one which
                    // changed in the new revision. For instance DSS reference was 5 0 obj and changed to be 6 0 obj.
                    // In this case and only in this case reference with obj number 5 can be safely removed.
                    boolean referenceAllowedToBeRemoved = previousAllowedReferences.stream().anyMatch(reference ->
                            reference != null && reference.getObjNumber() == indirectReference.getObjNumber()) &&
                            !currentAllowedReferences.stream().anyMatch(reference ->
                                    reference != null && reference.getObjNumber() == indirectReference.getObjNumber());
                    // If some reference wasn't in the previous document, it is safe to remove it,
                    // since it is not possible to introduce new reference and remove it at the same revision.
                    boolean referenceWasInPrevDocument =
                            documentWithoutRevision.getPdfObject(indirectReference.getObjNumber()) != null;
                    if (!isMaxGenerationObject(indirectReference) &&
                            referenceWasInPrevDocument && !referenceAllowedToBeRemoved) {
                        validationReport.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(
                                OBJECT_REMOVED, indirectReference.getObjNumber()), ReportItemStatus.INVALID));
                    }
                } else if (!checkAllowedReferences(currentAllowedReferences, previousAllowedReferences,
                        indirectReference, documentWithoutRevision)) {
                    validationReport.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(
                            UNEXPECTED_ENTRY_IN_XREF, indirectReference.getObjNumber()), ReportItemStatus.INVALID));
                }
            }
        } catch (IOException exception) {
            validationReport.addReportItem(new ReportItem(DOC_MDP_CHECK, REVISIONS_READING_EXCEPTION,
                    exception, ReportItemStatus.INDETERMINATE));
        }
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
            PdfString fieldName = signatureField.getAsString(PdfName.T);
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(ACCESS_PERMISSIONS_ADDED,
                    fieldName == null ? "" : fieldName.getValue()), ReportItemStatus.INDETERMINATE));
        } else {
            accessPermissions = newAccessPermissions;
        }
    }

    private void updateApprovalSignatureFieldLock(DocumentRevision revision, PdfDictionary signatureField,
                                                  ValidationReport report) {
        PdfDictionary fieldLock = signatureField.getAsDictionary(PdfName.Lock);
        if (fieldLock == null || fieldLock.getAsName(PdfName.Action) == null) {
            return;
        }

        PdfName action = fieldLock.getAsName(PdfName.Action);
        if (PdfName.Include.equals(action)) {
            PdfArray fields = fieldLock.getAsArray(PdfName.Fields);
            if (fields != null) {
                for (PdfObject fieldName : fields) {
                    if (fieldName instanceof PdfString) {
                        lockedFields.add(((PdfString) fieldName).toUnicodeString());
                    }
                }
            }
        } else if (PdfName.Exclude.equals(action)) {
            PdfArray fields = fieldLock.getAsArray(PdfName.Fields);
            List<String> excludedFields = Collections.<String>emptyList();
            if (fields != null) {
                excludedFields = fields.subList(0, fields.size()).stream().map(
                        field -> field instanceof PdfString ? ((PdfString) field).toUnicodeString() : "")
                        .collect(Collectors.toList());
            }
            lockAllFormFields(revision, excludedFields, report);
        } else {
            if (!PdfName.All.equals(action)) {
                report.addReportItem(new ReportItem(FIELD_MDP_CHECK, MessageFormatUtil.format(
                        UNRECOGNIZED_ACTION, action.getValue()), ReportItemStatus.INVALID));
            }
            lockAllFormFields(revision, Collections.<String>emptyList(), report);
        }
    }

    private void lockAllFormFields(DocumentRevision revision, List<String> excludedFields, ValidationReport report) {
        try (InputStream inputStream = createInputStreamFromRevision(document, revision);
             PdfReader reader = new PdfReader(inputStream);
             PdfDocument documentWithRevision = new PdfDocument(reader,
                     new DocumentProperties().setEventCountingMetaInfo(metaInfo))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(documentWithRevision, false);
            if (acroForm != null) {
                for (String fieldName : acroForm.getAllFormFields().keySet()) {
                    if (!excludedFields.contains(fieldName)) {
                        lockedFields.add(fieldName);
                    }
                }
            }
        } catch (IOException exception) {
            report.addReportItem(new ReportItem(FIELD_MDP_CHECK, REVISIONS_READING_EXCEPTION, exception,
                    ReportItemStatus.INDETERMINATE));
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

    //
    //
    // Compare catalogs section:
    //
    //

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
                compareAcroFormsWithFieldMDP(documentWithoutRevision, documentWithRevision, report) &&
                compareAcroForms(previousCatalog.getAsDictionary(PdfName.AcroForm),
                        currentCatalog.getAsDictionary(PdfName.AcroForm), report) &&
                comparePages(previousCatalog.getAsDictionary(PdfName.Pages),
                        currentCatalog.getAsDictionary(PdfName.Pages), report);
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

    private boolean compareAcroFormsWithFieldMDP(PdfDocument documentWithoutRevision, PdfDocument documentWithRevision,
                                                 ValidationReport report) {
        PdfAcroForm currentAcroForm = PdfFormCreator.getAcroForm(documentWithRevision, false);
        PdfAcroForm previousAcroForm = PdfFormCreator.getAcroForm(documentWithoutRevision, false);

        if (currentAcroForm == null || previousAcroForm == null) {
            // This is not a part of FieldMDP validation.
            return true;
        }
        if (accessPermissions == AccessPermissions.NO_CHANGES_PERMITTED) {
            // In this case FieldMDP makes no sense, because related changes are forbidden anyway.
            return true;
        }
        for (Map.Entry<String, PdfFormField> previousField : previousAcroForm.getAllFormFields().entrySet()) {
            if (lockedFields.contains(previousField.getKey())) {
                // For locked form fields nothing can change,
                // however annotations can contain page link which should be excluded from direct comparison.
                PdfFormField currentFormField = currentAcroForm.getField(previousField.getKey());
                if (currentFormField == null) {
                    report.addReportItem(new ReportItem(FIELD_MDP_CHECK, MessageFormatUtil.format(
                            LOCKED_FIELD_REMOVED, previousField.getKey()), ReportItemStatus.INVALID));
                    return false;
                }
                if (!compareFormFieldWithFieldMDP(previousField.getValue().getPdfObject(),
                        currentFormField.getPdfObject(), previousField.getKey(), report)) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean compareFormFieldWithFieldMDP(PdfDictionary previousField, PdfDictionary currentField,
                                                 String fieldName, ValidationReport report) {
        PdfDictionary previousFieldCopy = new PdfDictionary(previousField);
        previousFieldCopy.remove(PdfName.Kids);
        previousFieldCopy.remove(PdfName.P);
        previousFieldCopy.remove(PdfName.Parent);
        previousFieldCopy.remove(PdfName.V);
        PdfDictionary currentFieldCopy = new PdfDictionary(currentField);
        currentFieldCopy.remove(PdfName.Kids);
        currentFieldCopy.remove(PdfName.P);
        currentFieldCopy.remove(PdfName.Parent);
        currentFieldCopy.remove(PdfName.V);
        if (!comparePdfObjects(previousFieldCopy, currentFieldCopy)) {
            report.addReportItem(new ReportItem(FIELD_MDP_CHECK, MessageFormatUtil.format(
                    LOCKED_FIELD_MODIFIED, fieldName), ReportItemStatus.INVALID));
            return false;
        }

        PdfObject prevValue = previousField.get(PdfName.V);
        PdfObject currValue = currentField.get(PdfName.V);
        if (PdfName.Sig.equals(currentField.getAsName(PdfName.FT))) {
            if (!compareSignatureDictionaries(prevValue, currValue, report)) {
                report.addReportItem(new ReportItem(FIELD_MDP_CHECK, MessageFormatUtil.format(
                        LOCKED_FIELD_MODIFIED, fieldName), ReportItemStatus.INVALID));
                return false;
            }
        } else if (!comparePdfObjects(prevValue, currValue)) {
            report.addReportItem(new ReportItem(FIELD_MDP_CHECK, MessageFormatUtil.format(
                    LOCKED_FIELD_MODIFIED, fieldName), ReportItemStatus.INVALID));
            return false;
        }

        if (!compareIndirectReferencesObjNums(previousField.get(PdfName.P), currentField.get(PdfName.P), report,
                "Page object with which field annotation is associated") ||
                !compareIndirectReferencesObjNums(previousField.get(PdfName.Parent), currentField.get(PdfName.Parent),
                        report, "Form field parent")) {
            report.addReportItem(new ReportItem(FIELD_MDP_CHECK, MessageFormatUtil.format(
                    LOCKED_FIELD_MODIFIED, fieldName), ReportItemStatus.INVALID));
            return false;
        }

        PdfArray previousKids = previousField.getAsArray(PdfName.Kids);
        PdfArray currentKids = currentField.getAsArray(PdfName.Kids);
        if (previousKids == null && currentKids != null) {
            report.addReportItem(new ReportItem(FIELD_MDP_CHECK, MessageFormatUtil.format(
                    LOCKED_FIELD_KIDS_ADDED, fieldName), ReportItemStatus.INVALID));
            return false;
        }
        if (previousKids != null && currentKids == null) {
            report.addReportItem(new ReportItem(FIELD_MDP_CHECK, MessageFormatUtil.format(
                    LOCKED_FIELD_KIDS_REMOVED, fieldName), ReportItemStatus.INVALID));
            return false;
        }
        if (previousKids == currentKids) {
            return true;
        }
        if (previousKids.size() < currentKids.size()) {
            report.addReportItem(new ReportItem(FIELD_MDP_CHECK, MessageFormatUtil.format(
                    LOCKED_FIELD_KIDS_ADDED, fieldName), ReportItemStatus.INVALID));
            return false;
        }
        if (previousKids.size() > currentKids.size()) {
            report.addReportItem(new ReportItem(FIELD_MDP_CHECK, MessageFormatUtil.format(
                    LOCKED_FIELD_KIDS_REMOVED, fieldName), ReportItemStatus.INVALID));
            return false;
        }
        for (int i = 0; i < previousKids.size(); ++i) {
            PdfDictionary previousKid = previousKids.getAsDictionary(i);
            PdfDictionary currentKid = currentKids.getAsDictionary(i);
            if (previousKid == null || currentKid == null) {
                report.addReportItem(new ReportItem(FIELD_MDP_CHECK, MessageFormatUtil.format(
                        FIELD_NOT_DICTIONARY, fieldName), ReportItemStatus.INDETERMINATE));
                continue;
            }
            if (PdfFormAnnotationUtil.isPureWidget(previousKid) &&
                    !compareFormFieldWithFieldMDP(previousKid, currentKid, fieldName, report)) {
                return false;
            }
        }
        return true;
    }

    private boolean compareAcroForms(PdfDictionary prevAcroForm, PdfDictionary currAcroForm, ValidationReport report) {
        checkedAnnots = new HashSet<>();
        newlyAddedFields = new HashSet<>();
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
        Set<PdfDictionary> prevFieldsSet = populateFormFields(prevFields);
        Set<PdfDictionary> currFieldsSet = populateFormFields(currFields);

        for (PdfDictionary previousField : prevFieldsSet) {
            PdfDictionary currentField = retrieveTheSameField(currFieldsSet, previousField);
            if (currentField == null || !compareFields(previousField, currentField, report)) {
                PdfString fieldName = previousField.getAsString(PdfName.T);
                report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(FIELD_REMOVED,
                        fieldName == null ? "" : fieldName.getValue()), ReportItemStatus.INVALID));
                return false;
            }
            if (PdfFormAnnotationUtil.isPureWidgetOrMergedField(previousField)) {
                checkedAnnots.add(previousField);
            }
            if (PdfFormAnnotationUtil.isPureWidgetOrMergedField(currentField)) {
                checkedAnnots.add(currentField);
            }
            currFieldsSet.remove(currentField);
        }

        for (PdfDictionary field : currFieldsSet) {
            if (!isAllowedSignatureField(field, report)) {
                return false;
            }
        }
        return compareWidgets(prevFields, currFields, report);
    }

    private PdfDictionary retrieveTheSameField(Set<PdfDictionary> currFields, PdfDictionary previousField) {
        for (PdfDictionary currentField : currFields) {
            PdfDictionary prevFormDict = copyFieldDictionary(previousField);
            PdfDictionary currFormDict = copyFieldDictionary(currentField);
            if (comparePdfObjects(prevFormDict, currFormDict) &&
                    compareIndirectReferencesObjNums(prevFormDict.get(PdfName.Parent), currFormDict.get(PdfName.Parent),
                            new ValidationReport(), "Form field parent") &&
                    compareIndirectReferencesObjNums(prevFormDict.get(PdfName.P), currFormDict.get(PdfName.P),
                            new ValidationReport(), "Page object with which field annotation is associated")) {
                return currentField;
            }
        }
        return null;
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
        PdfObject prevValue = previousField.get(PdfName.V);
        PdfObject currValue = currentField.get(PdfName.V);
        if (prevValue == null && currValue == null && PdfName.Ch.equals(currentField.getAsName(PdfName.FT))) {
            // Choice field: if the items in the I entry differ from those in the V entry, the V entry shall be used.
            prevValue = previousField.get(PdfName.I);
            currValue = currentField.get(PdfName.I);
        }

        if (PdfName.Sig.equals(currentField.getAsName(PdfName.FT))) {
            if (!compareSignatureDictionaries(prevValue, currValue, report)) {
                PdfString fieldName = currentField.getAsString(PdfName.T);
                report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(
                        SIGNATURE_MODIFIED, fieldName == null ? "" : fieldName.getValue()), ReportItemStatus.INVALID));
                return false;
            }
        } else if (getAccessPermissions() == AccessPermissions.NO_CHANGES_PERMITTED
                && !comparePdfObjects(prevValue, currValue)) {
            return false;
        }

        return compareFormFields(previousField.getAsArray(PdfName.Kids), currentField.getAsArray(PdfName.Kids), report);
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

    private boolean compareWidgets(PdfArray prevFields, PdfArray currFields, ValidationReport report) {
        if (getAccessPermissions() == AccessPermissions.ANNOTATION_MODIFICATION) {
            return true;
        }
        List<PdfDictionary> prevAnnots = populateWidgetAnnotations(prevFields);
        List<PdfDictionary> currAnnots = populateWidgetAnnotations(currFields);
        if (prevAnnots.size() != currAnnots.size()) {
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, ANNOTATIONS_MODIFIED, ReportItemStatus.INVALID));
            return false;
        }
        for (int i = 0; i < prevAnnots.size(); i++) {
            PdfDictionary prevAnnot = new PdfDictionary(prevAnnots.get(i));
            removeAppearanceRelatedProperties(prevAnnot);
            PdfDictionary currAnnot = new PdfDictionary(currAnnots.get(i));
            removeAppearanceRelatedProperties(currAnnot);
            if (!comparePdfObjects(prevAnnot, currAnnot) ||
                    !compareIndirectReferencesObjNums(
                            prevAnnots.get(i).get(PdfName.P), currAnnots.get(i).get(PdfName.P), report,
                            "Page object with which annotation is associated") ||
                    !compareIndirectReferencesObjNums(
                            prevAnnots.get(i).get(PdfName.Parent), currAnnots.get(i).get(PdfName.Parent), report,
                            "Annotation parent")) {
                report.addReportItem(new ReportItem(DOC_MDP_CHECK, ANNOTATIONS_MODIFIED, ReportItemStatus.INVALID));
                return false;
            }
            checkedAnnots.add(prevAnnots.get(i));
            checkedAnnots.add(currAnnots.get(i));
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
                if (!comparePageAnnotations(prevAnnots, currAnnots, report)) {
                    report.addReportItem(new ReportItem(DOC_MDP_CHECK, PAGE_ANNOTATIONS_MODIFIED,
                            ReportItemStatus.INVALID));
                    return false;
                }
            }
        }
        return true;
    }

    private boolean comparePageAnnotations(PdfArray prevAnnots, PdfArray currAnnots, ValidationReport report) {
        if (prevAnnots == null && currAnnots == null) {
            return true;
        }
        if (prevAnnots == null || currAnnots == null || prevAnnots.size() != currAnnots.size()) {
            return false;
        }
        for (int i = 0; i < prevAnnots.size(); i++) {
            PdfDictionary prevAnnot = new PdfDictionary(prevAnnots.getAsDictionary(i));
            prevAnnot.remove(PdfName.P);
            prevAnnot.remove(PdfName.Parent);
            PdfDictionary currAnnot = new PdfDictionary(currAnnots.getAsDictionary(i));
            currAnnot.remove(PdfName.P);
            currAnnot.remove(PdfName.Parent);
            if (!comparePdfObjects(prevAnnot, currAnnot) ||
                    !compareIndirectReferencesObjNums(prevAnnots.getAsDictionary(i).get(PdfName.P),
                            currAnnots.getAsDictionary(i).get(PdfName.P), report,
                            "Page object with which annotation is associated") ||
                    !compareIndirectReferencesObjNums(prevAnnots.getAsDictionary(i).get(PdfName.Parent),
                            currAnnots.getAsDictionary(i).get(PdfName.Parent), report, "Annotation parent")) {
                return false;
            }
        }
        return true;
    }

    // Compare catalogs util methods section:

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
            if (report != null) {
                report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(DIRECT_OBJECT, type),
                        ReportItemStatus.INVALID));
            }
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
            PdfString fieldName = field.getAsString(PdfName.T);
            report.addReportItem(new ReportItem(DOC_MDP_CHECK, MessageFormatUtil.format(UNEXPECTED_FORM_FIELD,
                    fieldName == null ? "" : fieldName.getValue()), ReportItemStatus.INVALID));
            return false;
        }

        if (PdfFormAnnotationUtil.isPureWidgetOrMergedField(field)) {
            checkedAnnots.add(field);
        } else {
            PdfArray kids = field.getAsArray(PdfName.Kids);
            checkedAnnots.addAll(populateWidgetAnnotations(kids));
        }
        newlyAddedFields.add(field);

        return true;
    }

    private Set<PdfDictionary> populateFormFields(PdfArray fieldsArray) {
        Set<PdfDictionary> fields = new HashSet<>();
        if (fieldsArray != null) {
            for (int i = 0; i < fieldsArray.size(); ++i) {
                PdfDictionary fieldDict = (PdfDictionary) fieldsArray.get(i);
                if (PdfFormField.isFormField(fieldDict)) {
                    fields.add(fieldDict);
                }
            }
        }
        return fields;
    }

    private List<PdfDictionary> populateWidgetAnnotations(PdfArray fieldsArray) {
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
        if (annots == null || getAccessPermissions() == AccessPermissions.ANNOTATION_MODIFICATION) {
            return null;
        }
        PdfArray annotsCopy = new PdfArray(annots);
        for (PdfObject annot : annots) {
            // checkedAnnots contains all the fields' widget annotations from the Acroform which were already validated
            // during the compareAcroForms call, so we shouldn't check them once again
            if (checkedAnnots.contains(annot)) {
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
        annotDict.remove(PdfName.Parent);
        if (getAccessPermissions() == AccessPermissions.FORM_FIELDS_MODIFICATION) {
            annotDict.remove(PdfName.AP);
            annotDict.remove(PdfName.AS);
            annotDict.remove(PdfName.M);
            annotDict.remove(PdfName.F);
        }
        if (getAccessPermissions() == AccessPermissions.ANNOTATION_MODIFICATION) {
            for (PdfName key : new PdfDictionary(annotDict).keySet()) {
                if (!PdfFormField.getFormFieldKeys().contains(key)) {
                    annotDict.remove(key);
                }
            }
        }
    }

    //
    //
    // Compare PDF objects util section:
    //
    //

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

    //
    //
    // Allowed references section:
    //
    //

    private Set<PdfIndirectReference> createAllowedReferences(PdfDocument document) {
        // Each indirect reference in the set is an allowed reference to be present in the new xref table
        // or the same entry in the previous document.
        // If any reference is null, we expect this object to be newly generated or direct reference.
        Set<PdfIndirectReference> allowedReferences = new HashSet<>();

        if (document.getTrailer().get(PdfName.Info) != null) {
            allowedReferences.add(document.getTrailer().get(PdfName.Info).getIndirectReference());
        }
        if (document.getCatalog().getPdfObject() == null) {
            return allowedReferences;
        }
        allowedReferences.add(document.getCatalog().getPdfObject().getIndirectReference());
        if (document.getCatalog().getPdfObject().get(PdfName.Metadata) != null) {
            allowedReferences.add(document.getCatalog().getPdfObject().get(PdfName.Metadata).getIndirectReference());
        }

        PdfDictionary dssDictionary = document.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        if (dssDictionary != null) {
            allowedReferences.add(dssDictionary.getIndirectReference());
            allowedReferences.addAll(createAllowedDssEntries(document));
        }

        PdfDictionary acroForm = document.getCatalog().getPdfObject().getAsDictionary(PdfName.AcroForm);
        if (acroForm != null) {
            allowedReferences.add(acroForm.getIndirectReference());

            PdfArray fields = acroForm.getAsArray(PdfName.Fields);
            createAllowedFormFieldEntries(fields, allowedReferences);

            PdfDictionary resources = acroForm.getAsDictionary(PdfName.DR);
            if (resources != null) {
                allowedReferences.add(resources.getIndirectReference());
                addAllNestedDictionaryEntries(allowedReferences, resources);
            }
        }

        PdfDictionary pagesDictionary = document.getCatalog().getPdfObject().getAsDictionary(PdfName.Pages);
        if (pagesDictionary != null) {
            allowedReferences.add(pagesDictionary.getIndirectReference());
            allowedReferences.addAll(createAllowedPagesEntries(pagesDictionary));
        }

        return allowedReferences;
    }

    private boolean checkAllowedReferences(Set<PdfIndirectReference> currentAllowedReferences,
                                           Set<PdfIndirectReference> previousAllowedReferences,
                                           PdfIndirectReference indirectReference,
                                           PdfDocument documentWithoutRevision) {
        for (PdfIndirectReference currentAllowedReference : currentAllowedReferences) {
            if (isSameReference(currentAllowedReference, indirectReference)) {
                return documentWithoutRevision.getPdfObject(indirectReference.getObjNumber()) == null ||
                        previousAllowedReferences.stream().anyMatch(
                                reference -> isSameReference(reference, indirectReference));
            }
        }
        return false;
    }

    // Allowed references creation nested methods section:

    private Set<PdfIndirectReference> createAllowedDssEntries(PdfDocument document) {
        Set<PdfIndirectReference> allowedReferences = new HashSet<>();
        PdfDictionary dssDictionary = document.getCatalog().getPdfObject().getAsDictionary(PdfName.DSS);
        PdfArray certs = dssDictionary.getAsArray(PdfName.Certs);
        if (certs != null) {
            allowedReferences.add(certs.getIndirectReference());
            for (int i = 0; i < certs.size(); ++i) {
                allowedReferences.add(certs.get(i).getIndirectReference());
            }
        }
        PdfArray ocsps = dssDictionary.getAsArray(PdfName.OCSPs);
        if (ocsps != null) {
            allowedReferences.add(ocsps.getIndirectReference());
            for (int i = 0; i < ocsps.size(); ++i) {
                allowedReferences.add(ocsps.get(i).getIndirectReference());
            }
        }
        PdfArray crls = dssDictionary.getAsArray(PdfName.CRLs);
        if (crls != null) {
            allowedReferences.add(crls.getIndirectReference());
            for (int i = 0; i < crls.size(); ++i) {
                allowedReferences.add(crls.get(i).getIndirectReference());
            }
        }
        PdfDictionary vris = dssDictionary.getAsDictionary(PdfName.VRI);
        if (vris != null) {
            allowedReferences.add(vris.getIndirectReference());
            for (Map.Entry<PdfName, PdfObject> vri : vris.entrySet()) {
                allowedReferences.add(vri.getValue().getIndirectReference());
                if (vri.getValue() instanceof PdfDictionary) {
                    PdfDictionary vriDictionary = (PdfDictionary) vri.getValue();
                    PdfArray vriCerts = vriDictionary.getAsArray(PdfName.Cert);
                    if (vriCerts != null) {
                        allowedReferences.add(vriCerts.getIndirectReference());
                        for (int i = 0; i < vriCerts.size(); ++i) {
                            allowedReferences.add(vriCerts.get(i).getIndirectReference());
                        }
                    }
                    PdfArray vriOcsps = vriDictionary.getAsArray(PdfName.OCSP);
                    if (vriOcsps != null) {
                        allowedReferences.add(vriOcsps.getIndirectReference());
                        for (int i = 0; i < vriOcsps.size(); ++i) {
                            allowedReferences.add(vriOcsps.get(i).getIndirectReference());
                        }
                    }
                    PdfArray vriCrls = vriDictionary.getAsArray(PdfName.CRL);
                    if (vriCrls != null) {
                        allowedReferences.add(vriCrls.getIndirectReference());
                        for (int i = 0; i < vriCrls.size(); ++i) {
                            allowedReferences.add(vriCrls.get(i).getIndirectReference());
                        }
                    }
                    if (vriDictionary.get(new PdfName("TS")) != null) {
                        allowedReferences.add(vriDictionary.get(new PdfName("TS")).getIndirectReference());
                    }
                }
            }
        }
        return allowedReferences;
    }

    private Collection<PdfIndirectReference> createAllowedPagesEntries(PdfDictionary pagesDictionary) {
        Set<PdfIndirectReference> allowedReferences = new HashSet<>();
        PdfArray kids = pagesDictionary.getAsArray(PdfName.Kids);
        if (kids != null) {
            allowedReferences.add(kids.getIndirectReference());
            for (int i = 0; i < kids.size(); ++i) {
                PdfDictionary pageNode = kids.getAsDictionary(i);
                allowedReferences.add(kids.get(i).getIndirectReference());
                if (pageNode != null) {
                    if (PdfName.Pages.equals(pageNode.getAsName(PdfName.Type))) {
                        allowedReferences.addAll(createAllowedPagesEntries(pageNode));
                    } else {
                        PdfObject annots = pageNode.get(PdfName.Annots);
                        if (annots != null) {
                            allowedReferences.add(annots.getIndirectReference());
                            if (getAccessPermissions() == AccessPermissions.ANNOTATION_MODIFICATION) {
                                addAllNestedArrayEntries(allowedReferences, (PdfArray) annots);
                            }
                        }
                    }
                }
            }
        }
        return allowedReferences;
    }

    private void createAllowedFormFieldEntries(PdfArray fields, Set<PdfIndirectReference> allowedReferences) {
        if (fields == null) {
            return;
        }
        for (PdfObject field : fields) {
            PdfDictionary fieldDict = (PdfDictionary) field;
            if (PdfFormField.isFormField(fieldDict)) {
                PdfObject value = fieldDict.get(PdfName.V);
                if (getAccessPermissions() != AccessPermissions.NO_CHANGES_PERMITTED || (value instanceof PdfDictionary &&
                        PdfName.DocTimeStamp.equals(((PdfDictionary) value).getAsName(PdfName.Type)))) {
                    allowedReferences.add(fieldDict.getIndirectReference());
                    PdfString fieldName = PdfFormCreator.createFormField(fieldDict).getFieldName();
                    if (newlyAddedFields.contains(fieldDict)) {
                        // For newly generated form field all references are allowed to be added.
                        addAllNestedDictionaryEntries(allowedReferences, fieldDict);
                    } else if (fieldName == null || !lockedFields.contains(fieldName.getValue())) {
                        // For already existing form field only several entries are allowed to be updated.
                        if (value != null) {
                            allowedReferences.add(value.getIndirectReference());
                        }
                        if (PdfFormAnnotationUtil.isPureWidgetOrMergedField(fieldDict)) {
                            addWidgetAnnotation(allowedReferences, fieldDict);
                        } else {
                            PdfArray kids = fieldDict.getAsArray(PdfName.Kids);
                            createAllowedFormFieldEntries(kids, allowedReferences);
                        }
                    }
                }
            } else {
                // Add annotation.
                addWidgetAnnotation(allowedReferences, fieldDict);
            }
        }
    }

    private void addWidgetAnnotation(Set<PdfIndirectReference> allowedReferences, PdfDictionary annotDict) {
        allowedReferences.add(annotDict.getIndirectReference());
        if (getAccessPermissions() == AccessPermissions.ANNOTATION_MODIFICATION) {
            PdfDictionary pureAnnotDict = new PdfDictionary(annotDict);
            for (PdfName key : annotDict.keySet()) {
                if (PdfFormField.getFormFieldKeys().contains(key)) {
                    pureAnnotDict.remove(key);
                }
            }
            addAllNestedDictionaryEntries(allowedReferences, pureAnnotDict);
        } else {
            PdfObject appearance = annotDict.get(PdfName.AP);
            if (appearance != null) {
                allowedReferences.add(appearance.getIndirectReference());
                if (appearance instanceof PdfDictionary) {
                    addAllNestedDictionaryEntries(allowedReferences, (PdfDictionary) appearance);
                }
            }
            PdfObject appearanceState = annotDict.get(PdfName.AS);
            if (appearanceState != null) {
                allowedReferences.add(appearanceState.getIndirectReference());
            }
            PdfObject timeStamp = annotDict.get(PdfName.M);
            if (timeStamp != null) {
                allowedReferences.add(timeStamp.getIndirectReference());
            }
        }
    }

    private void addAllNestedDictionaryEntries(Set<PdfIndirectReference> allowedReferences, PdfDictionary dictionary) {
        for (Map.Entry<PdfName, PdfObject> entry : dictionary.entrySet()) {
            PdfObject value = entry.getValue();
            if (value.getIndirectReference() != null && allowedReferences.stream().anyMatch(
                    reference -> isSameReference(reference, value.getIndirectReference()))) {
                // Required to not end up in an infinite loop.
                continue;
            }
            allowedReferences.add(value.getIndirectReference());
            if (value instanceof PdfDictionary) {
                addAllNestedDictionaryEntries(allowedReferences, (PdfDictionary) value);
            }
            if (value instanceof PdfArray) {
                addAllNestedArrayEntries(allowedReferences, (PdfArray) value);
            }
        }
    }

    private void addAllNestedArrayEntries(Set<PdfIndirectReference> allowedReferences, PdfArray pdfArray) {
        for (int i = 0; i < pdfArray.size(); ++i) {
            PdfObject arrayEntry = pdfArray.get(i);
            if (arrayEntry.getIndirectReference() != null && allowedReferences.stream().anyMatch(
                    reference -> isSameReference(reference, arrayEntry.getIndirectReference()))) {
                // Required to not end up in an infinite loop.
                continue;
            }
            allowedReferences.add(arrayEntry.getIndirectReference());

            if (arrayEntry instanceof PdfDictionary) {
                addAllNestedDictionaryEntries(allowedReferences, pdfArray.getAsDictionary(i));
            }
            if (arrayEntry instanceof PdfArray) {
                addAllNestedArrayEntries(allowedReferences, pdfArray.getAsArray(i));
            }
        }
    }
}
