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
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.IOException;
import java.security.Security;

import java.util.Arrays;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
@Category(BouncyCastleIntegrationTest.class)
public class DocumentRevisionsValidatorIntegrationTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/v1/DocumentRevisionsValidatorIntegrationTest/";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private ValidatorChainBuilder builder;
    private final ValidationContext validationContext = new ValidationContext(
            ValidatorContext.DOCUMENT_REVISIONS_VALIDATOR, CertificateSource.SIGNER_CERT, TimeBasedContext.PRESENT);
    private final boolean continueValidationAfterFail;

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }


    @Before
    public void setUp() {
        builder = new ValidatorChainBuilder();
        builder.getProperties().setContinueAfterFailure(ValidatorContexts.all(), CertificateSources.all(), continueValidationAfterFail);
    }

    public DocumentRevisionsValidatorIntegrationTest(Object continueValidationAfterFail) {
        this.continueValidationAfterFail = (boolean) continueValidationAfterFail;
    }

    @Parameterized.Parameters(name = "Continue validation after failure: {0}")
    public static Iterable<Object[]> createParameters() {
        return Arrays.asList(new Object[] {false}, new Object[] {true});
    }

    @Test
    public void noSignaturesDocTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "noSignaturesDoc.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.DOCUMENT_WITHOUT_SIGNATURES)
                            .withStatus(ReportItemStatus.INFO)));

            Assert.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void linearizedDocTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "linearizedDoc.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INDETERMINATE)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.LINEARIZED_NOT_SUPPORTED)
                            .withStatus(ReportItemStatus.INDETERMINATE)));

            Assert.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void multipleRevisionsDocumentWithoutPermissionsTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocumentWithoutPermissions.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));

            Assert.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void multipleRevisionsDocumentWithPermissionsTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocumentWithPermissions.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));

            Assert.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void twoCertificationSignaturesTest() throws IOException {
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

            Assert.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void signatureNotFoundTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "signatureNotFound.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.SIGNATURE_REVISION_NOT_FOUND)
                            .withStatus(ReportItemStatus.INVALID)));

            Assert.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void differentFieldLockLevelsTest() throws IOException {
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

            Assert.assertEquals(AccessPermissions.NO_CHANGES_PERMITTED, validator.getAccessPermissions());
        }
    }

    @Test
    public void fieldLockLevelIncreaseTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fieldLockLevelIncrease.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INDETERMINATE)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.ACCESS_PERMISSIONS_ADDED, i -> "Signature3")
                            .withStatus(ReportItemStatus.INDETERMINATE)));

            Assert.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void certificationSignatureAfterApprovalTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "certificationSignatureAfterApproval.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));

            Assert.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());
        }
    }


    @Test
    public void fieldLockChildModificationAllowedTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fieldLockChildModificationAllowed.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));
        }
    }

    @Test
    public void fieldLockChildModificationNotAllowedTest() throws IOException {
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

    @Test
    public void fieldLockRootModificationAllowedTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fieldLockRootModificationAllowed.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));
        }
    }

    @Test
    public void fieldLockRootModificationNotAllowedTest() throws IOException {
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

    @Test
    public void fieldLockSequentialExcludeValuesTest() throws IOException {
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

    @Test
    public void fieldLockSequentialIncludeValuesTest() throws IOException {
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

    @Test
    public void fieldLockKidsRemovedAndAddedTest() throws IOException {
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

    @Test
    public void pageAndParentIndirectReferenceModifiedTest() throws IOException {
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

    @Test
    public void lockedSignatureFieldModifiedTest() throws IOException {
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

    @Test
    public void lockedFieldRemoveAddKidsEntryTest() throws IOException {
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

    @Test
    public void removedLockedFieldTest() throws IOException {
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

    @Test
    public void danglingWidgetAnnotationTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "danglingWidgetAnnotation.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            // New widget annotation not included into the acroform was added to the 1st page.
            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.PAGE_ANNOTATIONS_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID)));

            Assert.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void removeAllThePageAnnotationsTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "removeAllAnnots.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            // All the annotations on the 2nd page were removed.
            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));

            Assert.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void removeAllTheFieldAnnotationsTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "removeFieldAnnots.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            // All the annotations of the text field were removed. Note that Acrobat considers it invalid.
            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));

            Assert.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void removeUnnamedFieldTest() throws Exception {
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

            Assert.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void fullCompressionModeLevel1Test() throws Exception {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fullCompressionModeLevel1.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            Assert.assertEquals(AccessPermissions.NO_CHANGES_PERMITTED, validator.getAccessPermissions());

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(0));
        }
    }

    @Test
    public void fullCompressionModeLevel2Test() throws Exception {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fullCompressionModeLevel2.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            Assert.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(0));
        }
    }

    @Test
    public void fullCompressionModeLevel3Test() throws Exception {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fullCompressionModeLevel3.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            ValidationReport report = validator.validateAllDocumentRevisions(validationContext, document);

            Assert.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(0));
        }
    }
}
