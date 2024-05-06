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
import com.itextpdf.kernel.pdf.DocumentRevision;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfRevisionsReader;
import com.itextpdf.signatures.validation.v1.report.ReportItem;
import com.itextpdf.signatures.validation.v1.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.v1.report.ValidationReport;
import com.itextpdf.signatures.validation.v1.report.ValidationReport.ValidationResult;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.BouncyCastleIntegrationTest;

import java.io.IOException;
import java.io.InputStream;
import java.security.Security;
import java.util.List;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(BouncyCastleIntegrationTest.class)
public class DocumentRevisionsValidatorTest extends ExtendedITextTest {
    private static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/signatures/validation/v1/DocumentRevisionsValidatorTest/";

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    @BeforeClass
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
    }

    @Test
    public void multipleRevisionsDocumentLevel1Test() throws IOException {
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 1;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocument.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document, documentRevisions.get(0));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(1));
            }
            // Between these two revisions DSS and timestamp are added, it is allowed.
            // But PDF is generated with extra annotation (it was a bug).
            Assert.assertEquals(1, validationReport.getFailures().size());
            ReportItem reportItem1 = validationReport.getFailures().get(0);
            Assert.assertEquals(DocumentRevisionsValidator.DOC_MDP_CHECK, reportItem1.getCheckName());
            Assert.assertEquals(MessageFormatUtil.format(DocumentRevisionsValidator.UNEXPECTED_ENTRY_IN_XREF, 27),
                    reportItem1.getMessage());
            Assert.assertEquals(ReportItemStatus.INVALID, reportItem1.getStatus());

            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document, documentRevisions.get(1));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(2));
            }
            // Between these two revisions only DSS is updated, which is allowed.
            Assert.assertEquals(0, validationReport.getFailures().size());
        }
    }

    @Test
    public void hugeDocumentTest() throws IOException {
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 1;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "hugeDocument.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document, documentRevisions.get(0));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(1));
            }
            Assert.assertEquals(0, validationReport.getFailures().size());
        }
    }

    @Test
    public void extensionsModificationsTest() throws IOException {
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 1;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "extensionsModifications.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document, documentRevisions.get(0));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(1));
            }
            Assert.assertEquals(0, validationReport.getFailures().size());

            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document, documentRevisions.get(1));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(2));
            }
            Assert.assertEquals(1, validationReport.getFailures().size());
            ReportItem reportItem1 = validationReport.getFailures().get(0);
            Assert.assertEquals(DocumentRevisionsValidator.DOC_MDP_CHECK, reportItem1.getCheckName());
            Assert.assertEquals(MessageFormatUtil.format(
                    DocumentRevisionsValidator.DEVELOPER_EXTENSION_REMOVED, PdfName.ESIC), reportItem1.getMessage());
            Assert.assertEquals(ReportItemStatus.INVALID, reportItem1.getStatus());

            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document, documentRevisions.get(2));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(3));
            }
            Assert.assertEquals(1, validationReport.getFailures().size());
            ReportItem reportItem2 = validationReport.getFailures().get(0);
            Assert.assertEquals(DocumentRevisionsValidator.DOC_MDP_CHECK, reportItem2.getCheckName());
            Assert.assertEquals(MessageFormatUtil.format(
                    DocumentRevisionsValidator.EXTENSION_LEVEL_DECREASED, PdfName.ESIC), reportItem2.getMessage());
            Assert.assertEquals(ReportItemStatus.INVALID, reportItem2.getStatus());

            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document, documentRevisions.get(3));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(4));
            }
            Assert.assertEquals(1, validationReport.getFailures().size());
            ReportItem reportItem3 = validationReport.getFailures().get(0);
            Assert.assertEquals(DocumentRevisionsValidator.DOC_MDP_CHECK, reportItem3.getCheckName());
            Assert.assertEquals(MessageFormatUtil.format(
                    DocumentRevisionsValidator.DEVELOPER_EXTENSION_REMOVED, PdfName.ESIC), reportItem3.getMessage());
            Assert.assertEquals(ReportItemStatus.INVALID, reportItem3.getStatus());

            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document, documentRevisions.get(4));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(5));
            }
            Assert.assertEquals(1, validationReport.getFailures().size());
            ReportItem reportItem4 = validationReport.getFailures().get(0);
            Assert.assertEquals(DocumentRevisionsValidator.DOC_MDP_CHECK, reportItem4.getCheckName());
            Assert.assertEquals(DocumentRevisionsValidator.EXTENSIONS_REMOVED, reportItem4.getMessage());
            Assert.assertEquals(ReportItemStatus.INVALID, reportItem4.getStatus());
        }
    }

    @Test
    public void completelyInvalidDocumentTest() throws IOException {
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 1;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "completelyInvalidDocument.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(0));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(1));
            }
            Assert.assertEquals(1, validationReport.getFailures().size());
            ReportItem reportItem = validationReport.getFailures().get(0);
            Assert.assertEquals(DocumentRevisionsValidator.DOC_MDP_CHECK, reportItem.getCheckName());
            Assert.assertEquals(DocumentRevisionsValidator.PAGES_MODIFIED, reportItem.getMessage());
            Assert.assertEquals(ReportItemStatus.INVALID, reportItem.getStatus());
        }
    }

    @Test
    public void makeFontDirectAndIndirectTest() throws IOException {
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 1;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "makeFontDirectAndIndirect.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(0));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(1));
            }
            // Adobe Acrobat doesn't complain about such change. We consider this incorrect.
            Assert.assertEquals(2, validationReport.getFailures().size());
            ReportItem reportItem1 = validationReport.getFailures().get(0);
            Assert.assertEquals(DocumentRevisionsValidator.DOC_MDP_CHECK, reportItem1.getCheckName());
            Assert.assertEquals(MessageFormatUtil.format(DocumentRevisionsValidator.FIELD_REMOVED, "Signature1"),
                    reportItem1.getMessage());
            Assert.assertEquals(ReportItemStatus.INVALID, reportItem1.getStatus());
            ReportItem reportItem2 = validationReport.getFailures().get(1);
            Assert.assertEquals(DocumentRevisionsValidator.DOC_MDP_CHECK, reportItem2.getCheckName());
            Assert.assertEquals(DocumentRevisionsValidator.NOT_ALLOWED_ACROFORM_CHANGES, reportItem2.getMessage());
            Assert.assertEquals(ReportItemStatus.INVALID, reportItem2.getStatus());

            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(1));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(2));
            }
            // Adobe Acrobat doesn't complain about such change. We consider this incorrect.
            Assert.assertEquals(2, validationReport.getFailures().size());
            reportItem1 = validationReport.getFailures().get(0);
            Assert.assertEquals(DocumentRevisionsValidator.DOC_MDP_CHECK, reportItem1.getCheckName());
            Assert.assertEquals(MessageFormatUtil.format(DocumentRevisionsValidator.FIELD_REMOVED, "Signature1"),
                    reportItem1.getMessage());
            Assert.assertEquals(ReportItemStatus.INVALID, reportItem1.getStatus());
            reportItem2 = validationReport.getFailures().get(1);
            Assert.assertEquals(DocumentRevisionsValidator.DOC_MDP_CHECK, reportItem2.getCheckName());
            Assert.assertEquals(DocumentRevisionsValidator.NOT_ALLOWED_ACROFORM_CHANGES, reportItem2.getMessage());
            Assert.assertEquals(ReportItemStatus.INVALID, reportItem2.getStatus());
        }
    }

    @Test
    public void randomEntryAddedTest() throws IOException {
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 1;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "randomEntryAdded.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(0));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(1));
            }
            // Adobe Acrobat doesn't complain about such change. We consider this incorrect.
            Assert.assertEquals(1, validationReport.getFailures().size());
            ReportItem reportItem1 = validationReport.getFailures().get(0);
            Assert.assertEquals(DocumentRevisionsValidator.DOC_MDP_CHECK, reportItem1.getCheckName());
            Assert.assertEquals(DocumentRevisionsValidator.NOT_ALLOWED_CATALOG_CHANGES, reportItem1.getMessage());
            Assert.assertEquals(ReportItemStatus.INVALID, reportItem1.getStatus());
        }
    }

    @Test
    public void randomEntryWithoutUsageTest() throws IOException {
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 1;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "randomEntryWithoutUsage.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(0));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(1));
            }
            // Adobe Acrobat doesn't complain about such change. We consider this incorrect.
            Assert.assertEquals(1, validationReport.getFailures().size());
            ReportItem reportItem1 = validationReport.getFailures().get(0);
            Assert.assertEquals(DocumentRevisionsValidator.DOC_MDP_CHECK, reportItem1.getCheckName());
            Assert.assertEquals(MessageFormatUtil.format(DocumentRevisionsValidator.UNEXPECTED_ENTRY_IN_XREF, 16), reportItem1.getMessage());
            Assert.assertEquals(ReportItemStatus.INVALID, reportItem1.getStatus());
        }
    }

    @Test
    public void changeExistingFontTest() throws IOException {
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 1;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "changeExistingFont.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(0));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(1));
            }
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.PAGE_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }

    @Test
    public void changeExistingFontAndAddAsDssTest() throws IOException {
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 1;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "changeExistingFontAndAddAsDss.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(0));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(1));
            }
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
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 1;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "fillInField.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document, documentRevisions.get(0));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(1));
            }
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
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 2;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "multipleRevisionsDocument2.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document, documentRevisions.get(0));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(1));
            }
            // Between these two revisions forms were filled in, it is allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(0));

            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document, documentRevisions.get(1));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(2));
            }
            // Between these two revisions existing signature field was signed, it is allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(0));

            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document, documentRevisions.get(2));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument, documentRevisions.get(3));
            }
            // Between these two revisions newly added signature field was signed, it is allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.VALID)
                    .hasNumberOfFailures(0).hasNumberOfLogs(0));
        }
    }

    @Test
    public void removePermissionsTest() throws IOException {
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 2;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "removePermissions.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(documentRevisions.size() - 2));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument,
                        documentRevisions.get(documentRevisions.size() - 1));
            }
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
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 2;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "removeDSS.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(documentRevisions.size() - 2));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument,
                        documentRevisions.get(documentRevisions.size() - 1));
            }
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
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 2;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "removeAcroform.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(documentRevisions.size() - 2));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument,
                        documentRevisions.get(documentRevisions.size() - 1));
            }
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
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 2;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "removeField.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(documentRevisions.size() - 2));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument,
                        documentRevisions.get(documentRevisions.size() - 1));
            }
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
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 2;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "renameField.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(documentRevisions.size() - 2));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument,
                        documentRevisions.get(documentRevisions.size() - 1));
            }
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
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 2;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "addTextField.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(documentRevisions.size() - 2));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument,
                        documentRevisions.get(documentRevisions.size() - 1));
            }
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
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 2;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "addUnsignedSignatureField.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(documentRevisions.size() - 2));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument,
                        documentRevisions.get(documentRevisions.size() - 1));
            }
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
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 2;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "brokenSignatureFieldDictionary.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(documentRevisions.size() - 2));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument,
                        documentRevisions.get(documentRevisions.size() - 1));
            }
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
        DocumentRevisionsValidator validator = new DocumentRevisionsValidator();
        validator.docMDP = 2;
        try (PdfDocument document = new PdfDocument(new PdfReader(SOURCE_FOLDER + "modifyPageAnnots.pdf"))) {
            PdfRevisionsReader revisionsReader = new PdfRevisionsReader(document.getReader());
            List<DocumentRevision> documentRevisions = revisionsReader.getAllRevisions();

            ValidationReport validationReport;
            try (InputStream inputStream = DocumentRevisionsValidator.createInputStreamFromRevision(document,
                    documentRevisions.get(documentRevisions.size() - 2));
                 PdfDocument previousDocument = new PdfDocument(new PdfReader(inputStream))) {
                validationReport = validator.validateRevision(document, previousDocument,
                        documentRevisions.get(documentRevisions.size() - 1));
            }
            // Between these two revisions circle annotation was added to the first page, it is not allowed.
            AssertValidationReport.assertThat(validationReport, a -> a.hasStatus(ValidationResult.INVALID)
                    .hasNumberOfFailures(1).hasNumberOfLogs(1)
                    .hasLogItem(l -> l.withCheckName(DocumentRevisionsValidator.DOC_MDP_CHECK)
                            .withMessage(DocumentRevisionsValidator.PAGE_ANNOTATIONS_MODIFIED)
                            .withStatus(ReportItemStatus.INVALID)));
        }
    }
}
