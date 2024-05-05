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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.signatures.validation.v1.DocumentRevisionsValidator.AccessPermissions;
import com.itextpdf.signatures.validation.v1.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;
import com.itextpdf.signatures.validation.v1.report.ValidationReport.ValidationResult;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.IOException;
import java.security.Security;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleIntegrationTest.class)
public class DocumentRevisionsValidatorIntegrationTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/v1/DocumentRevisionsValidatorIntegrationTest/";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    @Test
    public void noSignaturesDocTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "noSignaturesDoc.pdf"))) {
            DocumentRevisionsValidator validator = new DocumentRevisionsValidator(document);
            ValidationReport report = validator.validateAllDocumentRevisions();

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.DOCUMENT_WITHOUT_SIGNATURES)
                            .withStatus(ReportItemStatus.INFO)));

            Assert.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void multipleRevisionsDocumentWithoutPermissionsTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocumentWithoutPermissions.pdf"))) {
            DocumentRevisionsValidator validator = new DocumentRevisionsValidator(document);
            ValidationReport report = validator.validateAllDocumentRevisions();

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));

            Assert.assertEquals(AccessPermissions.ANNOTATION_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void multipleRevisionsDocumentWithPermissionsTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocumentWithPermissions.pdf"))) {
            DocumentRevisionsValidator validator = new DocumentRevisionsValidator(document);
            ValidationReport report = validator.validateAllDocumentRevisions();

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));

            Assert.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void twoCertificationSignaturesTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "twoCertificationSignatures.pdf"))) {
            DocumentRevisionsValidator validator = new DocumentRevisionsValidator(document);
            ValidationReport report = validator.validateAllDocumentRevisions();

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(2).hasNumberOfLogs(2)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.PERMISSION_REMOVED, i -> PdfName.DocMDP)
                            .withStatus(ReportItemStatus.INVALID))
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.TOO_MANY_CERTIFICATION_SIGNATURES)
                            .withStatus(ReportItemStatus.INDETERMINATE)));

            Assert.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());
        }
    }

    @Test
    public void signatureNotFoundTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "signatureNotFound.pdf"))) {
            DocumentRevisionsValidator validator = new DocumentRevisionsValidator(document);
            ValidationReport report = validator.validateAllDocumentRevisions();

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
            DocumentRevisionsValidator validator = new DocumentRevisionsValidator(document);
            ValidationReport report = validator.validateAllDocumentRevisions();

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
            DocumentRevisionsValidator validator = new DocumentRevisionsValidator(document);
            ValidationReport report = validator.validateAllDocumentRevisions();

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
            DocumentRevisionsValidator validator = new DocumentRevisionsValidator(document);
            ValidationReport report = validator.validateAllDocumentRevisions();

            AssertValidationReport.assertThat(report, a -> a.hasStatus(ValidationResult.VALID));

            Assert.assertEquals(AccessPermissions.FORM_FIELDS_MODIFICATION, validator.getAccessPermissions());
        }
    }
}
