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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.AccessPermissions;
import com.itextpdf.signatures.validation.v1.context.CertificateSource;
import com.itextpdf.signatures.validation.v1.context.CertificateSources;
import com.itextpdf.signatures.validation.v1.context.TimeBasedContext;
import com.itextpdf.signatures.validation.v1.context.ValidationContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContext;
import com.itextpdf.signatures.validation.v1.context.ValidatorContexts;
import com.itextpdf.signatures.validation.v1.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;
import com.itextpdf.signatures.validation.v1.report.ValidationReport.ValidationResult;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.security.Security;
import java.util.Arrays;

@Tag("BouncyCastleIntegrationTest")
public class DocumentRevisionsValidatorIntegrationTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/v1/DocumentRevisionsValidatorIntegrationTest/";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private ValidatorChainBuilder builder;
    private final ValidationContext validationContext = new ValidationContext(
            ValidatorContext.DOCUMENT_REVISIONS_VALIDATOR, CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);

    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    public void setUp(boolean continueValidationAfterFail) {
        builder = new ValidatorChainBuilder();
        builder.getProperties().setContinueAfterFailure(ValidatorContexts.all(), CertificateSources.all(), continueValidationAfterFail);
    }

    public static Iterable<Object[]> CreateParameters() {
        return Arrays.asList(new Object[]{false}, new Object[]{true});
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void noSignaturesDocTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "noSignaturesDoc.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.DOCUMENT_WITHOUT_SIGNATURES)
                            .withStatus(ReportItemStatus.INFO)));

            Assertions.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void linearizedDocTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "linearizedDoc.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INDETERMINATE)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.LINEARIZED_NOT_SUPPORTED)
                            .withStatus(ReportItemStatus.INDETERMINATE)));

            Assertions.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void multipleRevisionsDocumentWithoutPermissionsTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocumentWithoutPermissions.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));

            Assertions.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void multipleRevisionsDocumentWithPermissionsTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocumentWithPermissions.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));

            Assertions.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void eolNotIncludedIntoByteRangeTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "eolNotIncludedIntoByteRange.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void twoCertificationSignaturesTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "twoCertificationSignatures.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            if (continueValidationAfterFail) {
                AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                        .hasNumberOfFailures(2).hasNumberOfLogs(2)
                        .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                                .withMessage(DocumentRevisionsValidator.PERMISSION_REMOVED, i -> PdfName.DocMDP)
                                .withStatus(ReportItemStatus.INVALID))
                        .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                                .withMessage(DocumentRevisionsValidator.TOO_MANY_CERTIFICATION_SIGNATURES)
                                .withStatus(ReportItemStatus.INDETERMINATE)));
            } else {
                AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                        .hasNumberOfFailures(1).hasNumberOfLogs(1)
                        .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                                .withMessage(DocumentRevisionsValidator.PERMISSION_REMOVED, i -> PdfName.DocMDP)
                                .withStatus(ReportItemStatus.INVALID)));
            }

            Assertions.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void signatureNotFoundTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "signatureNotFound.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.SIGNATURE_REVISION_NOT_FOUND)
                            .withStatus(ReportItemStatus.INVALID)));

            Assertions.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void differentFieldLockLevelsTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "differentFieldLockLevels.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(2).hasNumberOfLogs(2)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.UNEXPECTED_FORM_FIELD, i -> "Signature4")
                            .withStatus(ReportItemStatus.INVALID))
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.NOT_ALLOWED_ACROFORM_CHANGES)
                            .withStatus(ReportItemStatus.INVALID)));

            Assertions.assertEquals(AccessPermissions.NO_CHANGES_PERMITTED, validator.getAccessPermissions());
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void fieldLockLevelIncreaseTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fieldLockLevelIncrease.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INDETERMINATE)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.ACCESS_PERMISSIONS_ADDED, i -> "Signature3")
                            .withStatus(ReportItemStatus.INDETERMINATE)));

            Assertions.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void certificationSignatureAfterApprovalTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "certificationSignatureAfterApproval.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a
                    .hasStatus(ValidationResult.INDETERMINATE)
                    .hasNumberOfFailures(1)
                    .hasLogItem(l -> l
                            .withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.NOT_ALLOWED_CERTIFICATION_SIGNATURE)
                            .withStatus(ReportItemStatus.INDETERMINATE)));

            Assertions.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void fieldLockChildModificationAllowedTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fieldLockChildModificationAllowed.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void fieldLockChildModificationNotAllowedTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fieldLockChildModificationNotAllowed.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_MODIFIED, i -> "rootField.childTextField")
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void fieldLockRootModificationAllowedTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fieldLockRootModificationAllowed.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void fieldLockRootModificationNotAllowedTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fieldLockRootModificationNotAllowed.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_MODIFIED, i -> "childTextField")
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void fieldLockSequentialExcludeValuesTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fieldLockSequentialExcludeValues.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_MODIFIED, i -> "rootField.childTextField")
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void fieldLockSequentialIncludeValuesTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fieldLockSequentialIncludeValues.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            if (continueValidationAfterFail) {
                AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                        .hasNumberOfFailures(2).hasNumberOfLogs(2)
                        .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                                .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_MODIFIED,
                                        i -> "rootField.childTextField")
                                .withStatus(ReportItemStatus.INVALID))
                        .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                                .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_MODIFIED, i -> "childTextField")
                                .withStatus(ReportItemStatus.INVALID)));
            } else {
                AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                        .hasNumberOfFailures(1).hasNumberOfLogs(1)
                        .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                                .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_MODIFIED,
                                        i -> "rootField.childTextField")
                                .withStatus(ReportItemStatus.INVALID)));
            }
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void fieldLockKidsRemovedAndAddedTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fieldLockKidsRemovedAndAdded.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            if (continueValidationAfterFail) {
                AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                        .hasNumberOfFailures(2).hasNumberOfLogs(2)
                        .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                                .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_KIDS_REMOVED, i -> "rootField")
                                .withStatus(ReportItemStatus.INVALID))
                        .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                                .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_KIDS_ADDED, i -> "rootField")
                                .withStatus(ReportItemStatus.INVALID)));
            } else {
                AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                        .hasNumberOfFailures(1).hasNumberOfLogs(1)
                        .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                                .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_KIDS_REMOVED, i -> "rootField")
                                .withStatus(ReportItemStatus.INVALID)));
            }
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void pageAndParentIndirectReferenceModifiedTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "pageAndParentIndirectReferenceModified.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_MODIFIED, i -> "rootField.childTextField2")
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void lockedSignatureFieldModifiedTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "lockedSignatureFieldModified.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_MODIFIED, i -> "Signature2")
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void lockedFieldRemoveAddKidsEntryTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "lockedFieldRemoveAddKidsEntry.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            if (continueValidationAfterFail) {
                AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                        .hasNumberOfFailures(2).hasNumberOfLogs(2)
                        .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                                .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_KIDS_REMOVED, i -> "rootField")
                                .withStatus(ReportItemStatus.INVALID))
                        .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                                .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_KIDS_ADDED, i -> "rootField")
                                .withStatus(ReportItemStatus.INVALID)));
            } else {
                AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                        .hasNumberOfFailures(1).hasNumberOfLogs(1)
                        .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                                .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_KIDS_REMOVED, i -> "rootField")
                                .withStatus(ReportItemStatus.INVALID)));
            }
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void removedLockedFieldTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "removedLockedField.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.FIELD_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.LOCKED_FIELD_REMOVED, i -> "textField")
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void danglingWidgetAnnotationTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "danglingWidgetAnnotation.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            // New widget annotation not included into the acroform was added to the 1st page.
            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.PAGE_ANNOTATIONS_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID)));

            Assertions.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void removeAllThePageAnnotationsTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "removeAllAnnots.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            // All the annotations on the 2nd page were removed.
            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));

            Assertions.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void removeAllTheFieldAnnotationsTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "removeFieldAnnots.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            // All the annotations of the text field were removed. Note that Acrobat considers it invalid.
            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));

            Assertions.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void simpleTaggedDocTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "simpleTaggedDoc.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfLogs(0));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void taggedDocAddAndRemoveAnnotationsTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "taggedDocAddAndRemoveAnnotations.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            // Annotations were removed, but were also considered modified objects and therefore are added to xref table.
            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfLogs(2)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.UNEXPECTED_ENTRY_IN_XREF, m -> "18")
                            .withStatus(ReportItemStatus.INFO))
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.UNEXPECTED_ENTRY_IN_XREF, m -> "50")
                            .withStatus(ReportItemStatus.INFO)));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void taggedDocRemoveStructTreeElementTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "taggedDocRemoveStructTreeElement.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.STRUCT_TREE_ROOT_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void taggedDocRemoveStructTreeAnnotationTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "taggedDocRemoveStructTreeAnnotation.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.STRUCT_TREE_ROOT_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void taggedDocModifyAnnotationTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "taggedDocModifyAnnotation.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfLogs(0));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void taggedDocModifyAnnotationAndStructElementTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "taggedDocModifyAnnotationAndStructElement.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(2)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.STRUCT_TREE_ROOT_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID))
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.STRUCT_TREE_ELEMENT_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void taggedDocModifyAnnotationAndStructContentTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "taggedDocModifyAnnotationAndStructContent.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID).hasNumberOfLogs(0));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void taggedDocModifyStructElementTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "taggedDocModifyStructElement.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(2)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.STRUCT_TREE_ROOT_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID))
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.STRUCT_TREE_ELEMENT_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void removeUnnamedFieldTest(boolean continueValidationAfterFail) throws Exception {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "removeUnnamedField.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            // Child field was removed, so parent field was modified. Both fields are unnamed.
            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(3).hasNumberOfLogs(3)
                    .hasLogItems(2, 2, l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(MessageFormatUtil.format(DocumentRevisionsValidator.FIELD_REMOVED, ""))
                            .withStatus(ReportItemStatus.INVALID))
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.NOT_ALLOWED_ACROFORM_CHANGES)
                            .withStatus(ReportItemStatus.INVALID)));

            Assertions.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void fullCompressionModeLevel1Test(boolean continueValidationAfterFail) throws Exception {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fullCompressionModeLevel1.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            Assertions.assertEquals(AccessPermissions.NO_CHANGES_PERMITTED, validator.getAccessPermissions());

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(0));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void fullCompressionModeLevel2Test(boolean continueValidationAfterFail) throws Exception {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fullCompressionModeLevel2.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            Assertions.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(0));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("CreateParameters")
    public void fullCompressionModeLevel3Test(boolean continueValidationAfterFail) throws Exception {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fullCompressionModeLevel3.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            Assertions.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(0));
        }
    }
}
