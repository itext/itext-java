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
import com.itextpdf.kernel.pdf.DocumentRevision;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfRevisionsReader;
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
import com.itextpdf.test.annotations.type.BouncyCastleUnitTest;

import java.io.IOException;
import java.security.Security;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
@Category(BouncyCastleUnitTest.class)
public class DocumentRevisionsValidatorTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/v1/DocumentRevisionsValidatorTest/";

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

    public DocumentRevisionsValidatorTest(Object continueValidationAfterFail) {
        this.continueValidationAfterFail = (boolean) continueValidationAfterFail;
    }

    @Parameterized.Parameters(name = "Continue validation after failure: {0}")
    public static Iterable<Object[]> createParameters() {
        return Arrays.asList(new Object[] {false}, new Object[] {true});
    }

    @Test
    public void multipleRevisionsDocumentLevel1Test() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocument.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.NO_CHANGES_PERMITTED);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(0), documentRevisions.get(1), document, validationReport, validationContext);

            // Between these two revisions DSS and timestamp are added, which is allowed,
            // but there is unused entry in the xref table, which is an itext signature generation artifact.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.UNEXPECTED_ENTRY_IN_XREF, i -> 27)
                            .withStatus(ReportItemStatus.INFO)));

            validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(1), documentRevisions.get(2), document, validationReport, validationContext);

            // Between these two revisions only DSS is updated, which is allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID));
        }
    }

    @Test
    public void hugeDocumentTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hugeDocument.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.NO_CHANGES_PERMITTED);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(0), documentRevisions.get(1), document, validationReport, validationContext);

            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID));
        }
    }

    @Test
    public void extensionsModificationsTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "extensionsModifications.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.NO_CHANGES_PERMITTED);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(0), documentRevisions.get(1), document, validationReport, validationContext);

            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID));

            validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(1), documentRevisions.get(2), document, validationReport, validationContext);

            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.BASE_VERSION_DECREASED, i -> PdfName.ESIC)
                            .withStatus(ReportItemStatus.INVALID)));

            validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(2), documentRevisions.get(3), document, validationReport, validationContext);

            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.EXTENSION_LEVEL_DECREASED, i -> PdfName.ESIC)
                            .withStatus(ReportItemStatus.INVALID)));


            validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(3), documentRevisions.get(4), document, validationReport, validationContext);

            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.DEVELOPER_EXTENSION_REMOVED, i -> PdfName.ESIC)
                            .withStatus(ReportItemStatus.INVALID)));

            validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(4), documentRevisions.get(5), document, validationReport, validationContext);

            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.EXTENSIONS_REMOVED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void completelyInvalidDocumentTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "completelyInvalidDocument.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.NO_CHANGES_PERMITTED);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(0), documentRevisions.get(1), document, validationReport, validationContext);

            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.PAGES_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void makeFontDirectAndIndirectTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "makeFontDirectAndIndirect.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            validator.setAccessPermissions(AccessPermissions.NO_CHANGES_PERMITTED);
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(0), documentRevisions.get(1), document, validationReport, validationContext);

            // Adobe Acrobat doesn't complain about such change. We consider this incorrect.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(2).hasNumberOfLogs(2)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.FIELD_REMOVED, i -> "Signature1")
                            .withStatus(ReportItemStatus.INVALID))
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.NOT_ALLOWED_ACROFORM_CHANGES)
                            .withStatus(ReportItemStatus.INVALID)));

            validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(1), documentRevisions.get(2), document, validationReport, validationContext);

            // Adobe Acrobat doesn't complain about such change. We consider this incorrect.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(2).hasNumberOfLogs(2)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.FIELD_REMOVED, i -> "Signature1")
                            .withStatus(ReportItemStatus.INVALID))
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.NOT_ALLOWED_ACROFORM_CHANGES)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void randomEntryAddedTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "randomEntryAdded.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.NO_CHANGES_PERMITTED);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(0), documentRevisions.get(1), document, validationReport, validationContext);

            // Adobe Acrobat doesn't complain about such change. We consider this incorrect.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.NOT_ALLOWED_CATALOG_CHANGES)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void randomEntryWithoutUsageTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "randomEntryWithoutUsage.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator()
                    .setAccessPermissions(AccessPermissions.NO_CHANGES_PERMITTED)
                    .setUnexpectedXrefChangesStatus(ReportItemStatus.INVALID);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(0), documentRevisions.get(1), document, validationReport, validationContext);

            // Adobe Acrobat doesn't complain about such change. We consider this incorrect.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.UNEXPECTED_ENTRY_IN_XREF, i -> 16)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void changeExistingFontTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "changeExistingFont.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.NO_CHANGES_PERMITTED);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(0), documentRevisions.get(1), document, validationReport, validationContext);

            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.PAGE_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void changeExistingFontAndAddAsDssTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "changeExistingFontAndAddAsDss.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.NO_CHANGES_PERMITTED);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(0), documentRevisions.get(1), document, validationReport, validationContext);

            // Adobe Acrobat doesn't complain about such change. We consider this incorrect.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.PAGE_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void fillInFieldAtLevel1Test() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fillInField.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.NO_CHANGES_PERMITTED);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(0), documentRevisions.get(1), document, validationReport, validationContext);

            // Between these two revisions forms were filled in, it is not allowed at docMDP level 1.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(2).hasNumberOfLogs(2)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.FIELD_REMOVED, i -> "input")
                            .withStatus(ReportItemStatus.INVALID))
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.NOT_ALLOWED_ACROFORM_CHANGES)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void multipleRevisionsDocumentLevel2Test() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocument2.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.FORM_FIELDS_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(0), documentRevisions.get(1), document, validationReport, validationContext);

            // Between these two revisions forms were filled in, it is allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(0));

            validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(1), documentRevisions.get(2), document, validationReport, validationContext);

            // Between these two revisions existing signature field was signed, it is allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(0));

            validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(2), documentRevisions.get(3), document, validationReport, validationContext);

            // Between these two revisions newly added signature field was signed, it is allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(0));
        }
    }

    @Test
    public void removePermissionsTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "removePermissions.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.FORM_FIELDS_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(documentRevisions.size() - 2),
                    documentRevisions.get(documentRevisions.size() - 1), document, validationReport, validationContext);

            // Between these two revisions /Perms key was removed, it is not allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.PERMISSIONS_REMOVED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void removeDSSTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "removeDSS.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.FORM_FIELDS_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(documentRevisions.size() - 2),
                    documentRevisions.get(documentRevisions.size() - 1), document, validationReport, validationContext);

            // Between these two revisions /DSS key was removed, it is not allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.DSS_REMOVED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void removeAcroformTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "removeAcroform.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.FORM_FIELDS_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(documentRevisions.size() - 2),
                    documentRevisions.get(documentRevisions.size() - 1), document, validationReport, validationContext);

            // Between these two revisions /Acroform key was removed, it is not allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.ACROFORM_REMOVED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void removeFieldTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "removeField.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.FORM_FIELDS_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(documentRevisions.size() - 2),
                    documentRevisions.get(documentRevisions.size() - 1), document, validationReport, validationContext);

            // Between these two revisions field was removed, it is not allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.NOT_ALLOWED_ACROFORM_CHANGES)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void renameFieldTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "renameField.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.FORM_FIELDS_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(documentRevisions.size() - 2),
                    documentRevisions.get(documentRevisions.size() - 1), document, validationReport, validationContext);

            // Between these two revisions field was renamed, it is not allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(2).hasNumberOfLogs(2)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.FIELD_REMOVED, i -> "input")
                            .withStatus(ReportItemStatus.INVALID))
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.NOT_ALLOWED_ACROFORM_CHANGES)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void addTextFieldTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "addTextField.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.FORM_FIELDS_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(documentRevisions.size() - 2),
                    documentRevisions.get(documentRevisions.size() - 1), document, validationReport, validationContext);

            // Between these two revisions new field was added, it is not allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(2).hasNumberOfLogs(2)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.UNEXPECTED_FORM_FIELD, i -> "text")
                            .withStatus(ReportItemStatus.INVALID))
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.NOT_ALLOWED_ACROFORM_CHANGES)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void addUnsignedSignatureFieldTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "addUnsignedSignatureField.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.FORM_FIELDS_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(documentRevisions.size() - 2),
                    documentRevisions.get(documentRevisions.size() - 1), document, validationReport, validationContext);

            // Between these two revisions new unsigned signature field was added, it is not allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(2).hasNumberOfLogs(2)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.UNEXPECTED_FORM_FIELD, i -> "signature")
                            .withStatus(ReportItemStatus.INVALID))
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.NOT_ALLOWED_ACROFORM_CHANGES)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void brokenSignatureFieldDictionaryTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "brokenSignatureFieldDictionary.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.FORM_FIELDS_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(documentRevisions.size() - 2),
                    documentRevisions.get(documentRevisions.size() - 1), document, validationReport, validationContext);

            // Between these two revisions signature value was replaced by text, it is not allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(3).hasNumberOfLogs(3)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.SIGNATURE_MODIFIED, i -> "Signature1")
                            .withStatus(ReportItemStatus.INVALID))
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.FIELD_REMOVED, i -> "Signature1")
                            .withStatus(ReportItemStatus.INVALID))
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.NOT_ALLOWED_ACROFORM_CHANGES)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void modifyPageAnnotsTest() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "modifyPageAnnots.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.FORM_FIELDS_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(documentRevisions.size() - 2),
                    documentRevisions.get(documentRevisions.size() - 1), document, validationReport, validationContext);

            // Between these two revisions circle annotation was added to the first page, it is not allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.PAGE_ANNOTATIONS_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void multipleRevisionsDocumentLevel3Test() throws IOException {
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocument3.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.ANNOTATION_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(0), documentRevisions.get(1), document, validationReport, validationContext);

            // Between these two revisions annotations were added and deleted, text field was filled-in.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(0));

            validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(1), documentRevisions.get(2), document, validationReport, validationContext);

            // Between these two revisions existed annotations were modified, it is allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(0));
        }
    }
}
