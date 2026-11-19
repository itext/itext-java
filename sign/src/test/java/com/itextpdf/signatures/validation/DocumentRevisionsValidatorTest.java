/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.io.image.ImageDataFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.DocumentRevision;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfRevisionsReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfSquareAnnotation;
import com.itextpdf.kernel.pdf.xobject.PdfImageXObject;
import com.itextpdf.signatures.AccessPermissions;
import com.itextpdf.signatures.validation.context.CertificateSource;
import com.itextpdf.signatures.validation.context.CertificateSources;
import com.itextpdf.signatures.validation.context.TimeBasedContext;
import com.itextpdf.signatures.validation.context.ValidationContext;
import com.itextpdf.signatures.validation.context.ValidatorContext;
import com.itextpdf.signatures.validation.context.ValidatorContexts;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Security;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("BouncyCastleUnitTest")
public class DocumentRevisionsValidatorTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/DocumentRevisionsValidatorTest/";

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

    public static Iterable<Object[]> createParameters() {
        return Arrays.asList(new Object[] {false}, new Object[] {true});
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void multipleRevisionsDocumentLevel1Test(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void hugeDocumentTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void extensionsModificationsTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void completelyInvalidDocumentTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void makeFontDirectAndIndirectTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void randomEntryAddedTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void randomEntryWithoutUsageTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void changeExistingFontTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void changeExistingFontAndAddAsDssTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void fillInFieldAtLevel1Test(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void multipleRevisionsDocumentLevel2Test(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void removePermissionsTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void removeDSSTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void removeAcroformTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void removeFieldTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void renameFieldTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void addTextFieldTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void addUnsignedSignatureFieldTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "addUnsignedSignatureField.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.FORM_FIELDS_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(documentRevisions.size() - 2),
                    documentRevisions.get(documentRevisions.size() - 1), document, validationReport, validationContext);

            // Between these two revisions new unsigned signature field was added, it is not allowed.
            // Acrobat allows it though.
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void brokenSignatureFieldDictionaryTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void modifyPageAnnotsTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void gotoReferencesModifiedStructTreeElemTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "gotoReferencesModifiedStructTreeElem.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.FORM_FIELDS_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(documentRevisions.size() - 2),
                    documentRevisions.get(documentRevisions.size() - 1), document, validationReport, validationContext);

            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void catalogReferenceInRandomPlaceTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "catalogReferenceInRandomPlace.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.FORM_FIELDS_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(documentRevisions.size() - 2),
                    documentRevisions.get(documentRevisions.size() - 1), document, validationReport, validationContext);

            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID));

            validator.validateRevision(documentRevisions.get(documentRevisions.size() - 3),
                    documentRevisions.get(documentRevisions.size() - 2), document, validationReport, validationContext);
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void catalogReferenceInRandomPlaceModifiedTest(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "catalogReferenceInRandomPlaceModified.pdf"))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.FORM_FIELDS_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(documentRevisions.size() - 2),
                    documentRevisions.get(documentRevisions.size() - 1), document, validationReport, validationContext);

            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.PAGE_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void multipleRevisionsDocumentLevel3Test(boolean continueValidationAfterFail) throws IOException {
        setUp(continueValidationAfterFail);
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

    @ParameterizedTest(name = "Continue validation after failure: {0}")
    @MethodSource("createParameters")
    public void pageResourcesSharedWithStructElementModifiedTest(boolean continueValidationAfterFail)
            throws IOException {
        setUp(continueValidationAfterFail);
        try (PdfDocument document = new PdfDocument(new PdfReader(new ByteArrayInputStream(
                createDocumentWithReplacedPageImage())))) {
            DocumentRevisionsValidator validator = builder.buildDocumentRevisionsValidator();
            validator.setAccessPermissions(AccessPermissions.ANNOTATION_MODIFICATION);
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport = new ValidationReport();
            validator.validateRevision(documentRevisions.get(0), documentRevisions.get(1), document, validationReport,
                    validationContext);

            // The page resources dictionary is also a structure element. Replacing the only annotation tagged by it
            // must not make the image drawn on the page modifiable.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.PAGE_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    private static byte[] createDocumentWithReplacedPageImage() throws IOException {
        ByteArrayOutputStream firstRevision = new ByteArrayOutputStream();
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(firstRevision))) {
            PdfPage page = pdfDocument.addNewPage();
            PdfAnnotation annotation = new PdfSquareAnnotation(new Rectangle(10, 10, 20, 20));
            page.addAnnotation(annotation);

            PdfDictionary structTreeRoot = new PdfDictionary();
            structTreeRoot.put(PdfName.Type, PdfName.StructTreeRoot);
            structTreeRoot.makeIndirect(pdfDocument);

            PdfDictionary objectReference = new PdfDictionary();
            objectReference.put(PdfName.Type, PdfName.OBJR);
            objectReference.put(PdfName.Obj, annotation.getPdfObject());

            PdfDictionary xObjects = new PdfDictionary();
            xObjects.put(new PdfName("Im0"), createImage(pdfDocument, (byte) 0xFF, (byte) 0x00));
            PdfDictionary resources = new PdfDictionary();
            resources.put(PdfName.XObject, xObjects);
            resources.put(PdfName.S, PdfName.Figure);
            resources.put(PdfName.P, structTreeRoot);
            resources.put(PdfName.K, objectReference);
            resources.makeIndirect(pdfDocument);

            structTreeRoot.put(PdfName.K, new PdfArray(resources));
            pdfDocument.getCatalog().put(PdfName.StructTreeRoot, structTreeRoot);

            page.getPdfObject().put(PdfName.Resources, resources);
            page.getFirstContentStream().setData(
                    "q 100 0 0 100 50 50 cm /Im0 Do Q".getBytes(StandardCharsets.ISO_8859_1));
        }

        ByteArrayOutputStream secondRevision = new ByteArrayOutputStream();
        try (PdfDocument pdfDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(
                firstRevision.toByteArray())), new PdfWriter(secondRevision),
                new StampingProperties().useAppendMode())) {
            PdfDictionary page = pdfDocument.getPage(1).getPdfObject();
            PdfDictionary newAnnotation = new PdfSquareAnnotation(new Rectangle(40, 40, 20, 20)).getPdfObject();
            newAnnotation.put(PdfName.P, page);
            newAnnotation.makeIndirect(pdfDocument);
            page.put(PdfName.Annots, new PdfArray(newAnnotation));
            page.setModified();

            PdfDictionary resources = page.getAsDictionary(PdfName.Resources);
            resources.getAsDictionary(PdfName.K).put(PdfName.Obj, newAnnotation);
            resources.getAsDictionary(PdfName.XObject).put(new PdfName("Im0"),
                    createImage(pdfDocument, (byte) 0x00, (byte) 0xFF));
            resources.setModified();
        }
        return secondRevision.toByteArray();
    }

    private static PdfDictionary createImage(PdfDocument pdfDocument, byte red, byte blue) {
        PdfImageXObject image = new PdfImageXObject(ImageDataFactory.create(1, 1, 3, 8,
                new byte[] {red, (byte) 0x00, blue}, null));
        image.makeIndirect(pdfDocument);
        return image.getPdfObject();
    }
}
