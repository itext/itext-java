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
package com.itextpdf.signatures;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.FileUtil;
import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfSigFieldLock;
import com.itextpdf.forms.fields.NonTerminalFormFieldBuilder;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.forms.fields.SignatureFormFieldBuilder;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.xobject.PdfFormXObject;
import com.itextpdf.pdfa.PdfADocument;
import com.itextpdf.signatures.PdfSigner.ISignatureEvent;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.testutils.PemFileHelper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Security;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Calendar;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("BouncyCastleUnitTest")
public class PdfSignerUnitTest extends ExtendedITextTest {
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private static final byte[] OWNER = "owner".getBytes();
    private static final byte[] USER = "user".getBytes();

    private static final String PDFA_RESOURCES = "./src/test/resources/com/itextpdf/signatures/pdfa/";
    private static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/signatures/PdfSignerUnitTest/";
    private static final String CERTS_SRC = "./src/test/resources/com/itextpdf/signatures/certs/";

    private static final char[] PASSWORD = "testpassphrase".toCharArray();

    private Certificate[] chain;
    private PrivateKey pk;


    @BeforeAll
    public static void before() {
        Security.addProvider(FACTORY.getProvider());
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @BeforeEach
    public void init()
            throws IOException, CertificateException, AbstractPKCSException, AbstractOperatorCreationException {
        pk = PemFileHelper.readFirstKey(CERTS_SRC + "signCertRsa01.pem", PASSWORD);
        chain = PemFileHelper.readFirstChain(CERTS_SRC + "signCertRsa01.pem");
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void createNewSignatureFormFieldInvisibleAnnotationTest() throws IOException {
        PdfSigner signer = new PdfSigner(new PdfReader(
                new ByteArrayInputStream(createEncryptedDocumentWithoutWidgetAnnotation()),
                new ReaderProperties().setPassword(OWNER)), new ByteArrayOutputStream(), new StampingProperties());
        signer.cryptoDictionary = new PdfSignature();
        SignerProperties signerProperties = new SignerProperties()
                .setPageRect(new Rectangle(100, 100, 0, 0));
        signer.setSignerProperties(signerProperties);

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(signer.document, true);
        signer.createNewSignatureFormField(acroForm, signerProperties.getFieldName());
        PdfFormField formField = acroForm.getField(signerProperties.getFieldName());

        PdfDictionary formFieldDictionary = formField.getPdfObject();
        Assertions.assertNotNull(formFieldDictionary);
        Assertions.assertTrue(formFieldDictionary.containsKey(PdfName.AP));
        PdfFormXObject ap = new PdfFormXObject(formFieldDictionary.getAsDictionary(PdfName.AP).getAsStream(PdfName.N));
        Assertions.assertTrue(new Rectangle(0, 0).equalsWithEpsilon(ap.getBBox().toRectangle()));
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true)})
    public void createNewSignatureFormFieldNotInvisibleAnnotationTest() throws IOException {
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(createEncryptedDocumentWithoutWidgetAnnotation()),
                        new ReaderProperties().setPassword(OWNER)), new ByteArrayOutputStream(), new StampingProperties());
        signer.cryptoDictionary = new PdfSignature();
        PdfSigFieldLock fieldLock = new PdfSigFieldLock();
        SignerProperties signerProperties = new SignerProperties()
                .setPageRect(new Rectangle(100, 100, 10, 10))
                .setFieldLockDict(fieldLock);
        signer.setSignerProperties(signerProperties);

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(signer.document, true);
        Assertions.assertEquals(fieldLock, signer.createNewSignatureFormField(acroForm, signerProperties.getFieldName()));
        PdfFormField formField = acroForm.getField(signerProperties.getFieldName());

        PdfDictionary formFieldDictionary = formField.getPdfObject();
        Assertions.assertNotNull(formFieldDictionary);
        Assertions.assertTrue(formFieldDictionary.containsKey(PdfName.AP));
    }

    @Test
    public void signWithFieldLockNotNullTest() throws IOException, GeneralSecurityException {
        PdfSigner signer = new PdfSigner(new PdfReader(
                new ByteArrayInputStream(createSimpleDocument(PdfVersion.PDF_2_0))),
                new ByteArrayOutputStream(),
                new StampingProperties());
        signer.cryptoDictionary = new PdfSignature();
        SignerProperties signerProperties = new SignerProperties()
                .setPageRect(new Rectangle(100, 100, 10, 10))
                .setFieldLockDict(new PdfSigFieldLock());
        signer.setSignerProperties(signerProperties);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);
        Assertions.assertTrue(signer.closed);
    }

    @Test
    public void signDetachedWhenAlreadySignedIsNotPossibleTest() throws IOException, GeneralSecurityException {
        PdfSigner signer = new PdfSigner(new PdfReader(
                new ByteArrayInputStream(createSimpleDocument())),
                new ByteArrayOutputStream(),
                new StampingProperties());
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        Exception e = Assertions.assertThrows(PdfException.class, () ->
                signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0,
                        PdfSigner.CryptoStandard.CADES));
        Assertions.assertEquals(SignExceptionMessageConstant.THIS_INSTANCE_OF_PDF_SIGNER_ALREADY_CLOSED, e.getMessage());
    }

    @Test
    public void signExternalWhenAlreadySignedIsNotPossibleTest() throws IOException, GeneralSecurityException {
        PdfSigner signer = new PdfSigner(new PdfReader(
                new ByteArrayInputStream(createSimpleDocument())),
                new ByteArrayOutputStream(),
                new StampingProperties());
        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        Exception e = Assertions.assertThrows(PdfException.class, () ->
                signer.signExternalContainer(new ExternalBlankSignatureContainer(new PdfDictionary()), 0));
        Assertions.assertEquals(SignExceptionMessageConstant.THIS_INSTANCE_OF_PDF_SIGNER_ALREADY_CLOSED, e.getMessage());
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void populateExistingSignatureFormFieldInvisibleAnnotationTest() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument document = new PdfDocument(new PdfWriter(outputStream,
                new WriterProperties().setStandardEncryption(USER, OWNER, 0, EncryptionConstants.STANDARD_ENCRYPTION_128)));
        document.addNewPage();
        PdfWidgetAnnotation widgetAnnotation = new PdfWidgetAnnotation(new Rectangle(100, 100, 0, 0));
        document.getPage(1).addAnnotation(widgetAnnotation);
        document.close();
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()), new ReaderProperties().setPassword(OWNER)),
                new ByteArrayOutputStream(), new StampingProperties());
        signer.cryptoDictionary = new PdfSignature();
        SignerProperties signerProperties = new SignerProperties()
                .setPageRect(new Rectangle(100, 100, 0, 0));
        signer.setSignerProperties(signerProperties);

        widgetAnnotation = (PdfWidgetAnnotation) signer.document.getPage(1).getAnnotations().get(0);
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(signer.document, true);
        PdfFormField formField = new ExtendedPdfSignatureFormField(widgetAnnotation, signer.document);
        formField.setFieldName(signerProperties.getFieldName());
        acroForm.addField(formField);
        signer.populateExistingSignatureFormField(acroForm);
        formField = acroForm.getField(signerProperties.getFieldName());

        PdfDictionary formFieldDictionary = formField.getPdfObject();
        Assertions.assertNotNull(formFieldDictionary);
        Assertions.assertTrue(formFieldDictionary.containsKey(PdfName.AP));
        PdfFormXObject ap = new PdfFormXObject(formFieldDictionary.getAsDictionary(PdfName.AP).getAsStream(PdfName.N));
        Assertions.assertTrue(new Rectangle(0, 0).equalsWithEpsilon(ap.getBBox().toRectangle()));
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void populateExistingSignatureFormFieldNotInvisibleAnnotationTest() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument document = new PdfDocument(new PdfWriter(outputStream,
                new WriterProperties().setStandardEncryption(USER, OWNER, 0, EncryptionConstants.STANDARD_ENCRYPTION_128)));
        document.addNewPage();
        PdfWidgetAnnotation widgetAnnotation = new PdfWidgetAnnotation(new Rectangle(100, 100, 0, 0));
        document.getPage(1).addAnnotation(widgetAnnotation);
        document.close();
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()), new ReaderProperties().setPassword(OWNER)),
                new ByteArrayOutputStream(), new StampingProperties());
        signer.cryptoDictionary = new PdfSignature();
        PdfSigFieldLock fieldLock = new PdfSigFieldLock();
        SignerProperties signerProperties = new SignerProperties()
                .setPageRect(new Rectangle(100, 100, 10, 10))
                .setFieldLockDict(fieldLock);
        signer.setSignerProperties(signerProperties);

        widgetAnnotation = (PdfWidgetAnnotation) signer.document.getPage(1).getAnnotations().get(0);
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(signer.document, true);
        PdfFormField formField = new ExtendedPdfSignatureFormField(widgetAnnotation, signer.document);
        formField.setFieldName(signerProperties.getFieldName());
        acroForm.addField(formField);
        Assertions.assertEquals(signer.populateExistingSignatureFormField(acroForm), fieldLock);
        formField = acroForm.getField(signerProperties.getFieldName());

        PdfDictionary formFieldDictionary = formField.getPdfObject();
        Assertions.assertNotNull(formFieldDictionary);
        Assertions.assertTrue(formFieldDictionary.containsKey(PdfName.AP));
    }

    @Test
    public void setAlternativeName() throws IOException, GeneralSecurityException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument document = new PdfDocument(new PdfWriter(outputStream, new WriterProperties()));
        document.setTagged();
        document.addNewPage();
        document.close();

        ByteArrayOutputStream signedOutputStream = new ByteArrayOutputStream();
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(outputStream.toByteArray())), signedOutputStream,
                new StampingProperties());
        SignerProperties signerProperties = new SignerProperties()
                .setFieldName("Signature1")
                .setPageNumber(1)
                .setPageRect(new Rectangle(100, 100, 10, 10));
        signer.setSignerProperties(signerProperties);

        SignatureFieldAppearance appearance = new SignatureFieldAppearance(SignerProperties.IGNORED_ID);
        appearance.setContent("Some text");
        appearance.getAccessibilityProperties().setAlternateDescription("Alternate description");
        signerProperties.setSignatureAppearance(appearance);

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0, PdfSigner.CryptoStandard.CADES);

        signer.document.close();

        PdfDocument signedDocument = new PdfDocument(new PdfReader(new ByteArrayInputStream(signedOutputStream.toByteArray())), new PdfWriter(new ByteArrayOutputStream()));
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(signedDocument, true);
        PdfFormField formField = acroForm.getField(signerProperties.getFieldName());
        Assertions.assertEquals("Alternate description", formField.getPdfObject().get(PdfName.TU).toString());
    }

    @Test
    public void tempFileProvidedTest() throws IOException {
        String tempFileName = "tempFile";
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(createSimpleDocument())),
                new ByteArrayOutputStream(), DESTINATION_FOLDER + tempFileName, new StampingProperties());
        Assertions.assertNotNull(signer.tempFile);
        Assertions.assertEquals(tempFileName, signer.tempFile.getName());
        Assertions.assertNull(signer.temporaryOS);
    }

    // Android-Conversion-Skip-Block-Start (TODO DEVSIX-7372 investigate why a few tests related to PdfA in PdfSignerUnitTest were cut)
    @Test
    public void initPdfaDocumentTest() throws IOException {
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(createSimplePdfaDocument())),
                new ByteArrayOutputStream(), new StampingProperties());
        Assertions.assertEquals(PdfAConformance.PDF_A_1A, signer.getDocument().getConformance().getAConformance());
    }

    @Test
    public void signingDateSetGetTest() throws IOException {
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(createSimplePdfaDocument())),
                new ByteArrayOutputStream(), new StampingProperties());
        Calendar testDate = DateTimeUtil.getCurrentTimeCalendar();
        SignerProperties signerProperties = new SignerProperties()
                .setClaimedSignDate(testDate);
        signer.setSignerProperties(signerProperties);

        Assertions.assertEquals(testDate, signerProperties.getClaimedSignDate());
    }

    @Test
    public void certificationLevelSetGetTest() throws IOException {
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(createSimplePdfaDocument())),
                new ByteArrayOutputStream(), new StampingProperties());
        Assertions.assertEquals(AccessPermissions.UNSPECIFIED, signer.getSignerProperties().getCertificationLevel());

        AccessPermissions testLevel = AccessPermissions.NO_CHANGES_PERMITTED;
        signer.getSignerProperties().setCertificationLevel(testLevel);
        Assertions.assertEquals(testLevel, signer.getSignerProperties().getCertificationLevel());
    }

    @Test
    public void signatureDictionarySetGetTest() throws IOException {
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(createSimplePdfaDocument())),
                new ByteArrayOutputStream(), new StampingProperties());
        Assertions.assertNull(signer.getSignatureDictionary());

        PdfSignature testSignature = new PdfSignature();
        signer.cryptoDictionary = testSignature;
        Assertions.assertEquals(testSignature, signer.getSignatureDictionary());
    }

    @Test
    public void signatureEventSetGetTest() throws IOException {
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(createSimplePdfaDocument())),
                new ByteArrayOutputStream(), new StampingProperties());
        Assertions.assertNull(signer.getSignatureEvent());

        ISignatureEvent testEvent = new DummySignatureEvent();
        signer.setSignatureEvent(testEvent);
        Assertions.assertEquals(testEvent, signer.getSignatureEvent());
    }

    @Test
    public void signatureFieldNameMustNotContainDotTest() throws IOException {
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(createSimplePdfaDocument())),
                new ByteArrayOutputStream(), new StampingProperties());
        SignerProperties signerProperties = new SignerProperties()
                .setFieldName("name.with.dots");
        Exception exception =
                Assertions.assertThrows(IllegalArgumentException.class, () -> signer.setSignerProperties(signerProperties));
        Assertions.assertEquals(SignExceptionMessageConstant.FIELD_NAMES_CANNOT_CONTAIN_A_DOT, exception.getMessage());
    }

    @Test
    public void changeSignatureFieldNameToInvalidTest() throws IOException {
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(createSimplePdfaDocument())),
                new ByteArrayOutputStream(), new StampingProperties());
        SignerProperties signerProperties = new SignerProperties();
        signer.setSignerProperties(signerProperties);
        Assertions.assertEquals("Signature1", signer.getSignerProperties().getFieldName());

        signerProperties.setFieldName("name.with.dots");
        Assertions.assertEquals("name.with.dots", signer.getSignerProperties().getFieldName());

        IExternalSignature pks = new PrivateKeySignature(pk, DigestAlgorithms.SHA256, FACTORY.getProviderName());
        Exception exception =
                Assertions.assertThrows(IllegalArgumentException.class,
                        () -> signer.signDetached(new BouncyCastleDigest(), pks, chain, null, null, null, 0,
                                PdfSigner.CryptoStandard.CADES));
        Assertions.assertEquals(SignExceptionMessageConstant.FIELD_NAMES_CANNOT_CONTAIN_A_DOT, exception.getMessage());
    }

    @Test
    public void documentWithoutReaderCannotBeSetToSignerTest() throws IOException {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(createSimplePdfaDocument()));
        PdfSigner signer = new PdfSigner(reader, new ByteArrayOutputStream(), new StampingProperties());

        PdfDocument documentWithoutReader = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        Exception e =
                Assertions.assertThrows(IllegalArgumentException.class, () -> signer.setDocument(documentWithoutReader));
        Assertions.assertEquals(SignExceptionMessageConstant.DOCUMENT_MUST_HAVE_READER, e.getMessage());
    }

    @Test
    public void documentSetGetTest() throws IOException {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(createSimplePdfaDocument()));
        PdfSigner signer = new PdfSigner(reader, new ByteArrayOutputStream(), new StampingProperties());

        PdfDocument document = signer.getDocument();
        Assertions.assertEquals(reader, document.getReader());

        PdfDocument documentWithoutReader = new PdfDocument(
                new PdfReader(new ByteArrayInputStream(createSimpleDocument())),
                new PdfWriter(new ByteArrayOutputStream()));
        signer.setDocument(documentWithoutReader);
        Assertions.assertEquals(documentWithoutReader, signer.getDocument());
    }

    @Test
    public void outputStreamSetGetTest() throws IOException {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(createSimplePdfaDocument()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfSigner signer = new PdfSigner(reader, outputStream, new StampingProperties());

        Assertions.assertEquals(outputStream, signer.originalOS);

        ByteArrayOutputStream anotherStream = new ByteArrayOutputStream();
        signer.setOriginalOutputStream(anotherStream);
        Assertions.assertEquals(anotherStream, signer.originalOS);
    }

    @Test
    public void fieldLockSetGetTest() throws IOException {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(createSimplePdfaDocument()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfSigner signer = new PdfSigner(reader, outputStream, new StampingProperties());

        Assertions.assertNull(signer.getSignerProperties().getFieldLockDict());

        PdfSigFieldLock fieldLock = new PdfSigFieldLock();
        signer.getSignerProperties().setFieldLockDict(fieldLock);
        Assertions.assertEquals(fieldLock, signer.getSignerProperties().getFieldLockDict());
    }
    // Android-Conversion-Skip-Block-End

    @Test
    public void setFieldNameNullForDefaultSignerTest() throws IOException {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(createSimpleDocument()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfSigner signer = new PdfSigner(reader, outputStream, new StampingProperties());

        SignerProperties signerProperties = new SignerProperties()
                .setFieldName(null);
        signer.setSignerProperties(signerProperties);
        Assertions.assertEquals("Signature1", signer.getSignerProperties().getFieldName());
    }

    @Test
    public void keepFieldNameAfterSetToNullTest() throws IOException {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(createSimpleDocument()));
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfSigner signer = new PdfSigner(reader, outputStream, new StampingProperties());

        String testName = "test_name";
        SignerProperties signerProperties = new SignerProperties().setFieldName(testName);
        signer.setSignerProperties(signerProperties);
        signerProperties.setFieldName(null);
        Assertions.assertEquals(testName, signer.getSignerProperties().getFieldName());
    }

    @Test
    public void setFieldNameToFieldWithSameNameAndNoSigTest() throws IOException {
        PdfReader reader = new PdfReader(new ByteArrayInputStream(createDocumentWithEmptyField()));
        PdfSigner signer = new PdfSigner(reader, new ByteArrayOutputStream(), new StampingProperties());

        SignerProperties signerProperties = new SignerProperties().setFieldName("test_field");
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> signer.setSignerProperties(signerProperties));
        Assertions.assertEquals(SignExceptionMessageConstant.FIELD_TYPE_IS_NOT_A_SIGNATURE_FIELD_TYPE, e.getMessage());

        reader.close();
    }

    @Test
    public void setFieldNameToSigFieldWithValueTest() throws IOException {
        String fieldName = "test_field";
        String fieldValue = "test_value";
        PdfReader reader = new PdfReader(
                new ByteArrayInputStream(createDocumentWithSignatureWithTestValueField(fieldName, fieldValue)));
        PdfSigner signer = new PdfSigner(reader, new ByteArrayOutputStream(), new StampingProperties());

        SignerProperties signerProperties = new SignerProperties().setFieldName(fieldName);
        Exception e = Assertions.assertThrows(IllegalArgumentException.class,
                () -> signer.setSignerProperties(signerProperties));
        Assertions.assertEquals(SignExceptionMessageConstant.FIELD_ALREADY_SIGNED, e.getMessage());

        reader.close();
    }

    @Test
    public void setFieldNameToSigFieldWithoutWidgetsTest() throws IOException {
        String fieldName = "test_field";
        PdfReader reader = new PdfReader(new ByteArrayInputStream(createDocumentWithSignatureField(fieldName)));
        PdfSigner signer = new PdfSigner(reader, new ByteArrayOutputStream(), new StampingProperties());

        SignerProperties signerProperties = new SignerProperties().setFieldName(fieldName);
        signer.setSignerProperties(signerProperties);
        Assertions.assertEquals(fieldName, signer.getSignerProperties().getFieldName());

        reader.close();
    }

    private static byte[] createDocumentWithEmptyField() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
        PdfFormField formField = new NonTerminalFormFieldBuilder(pdfDocument, "test_field").createNonTerminalFormField();
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDocument, true);
        acroForm.addField(formField);
        pdfDocument.close();
        return outputStream.toByteArray();
    }

    private static byte[] createDocumentWithSignatureWithTestValueField(String fieldName, String fieldValue) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
        PdfFormField formField = new SignatureFormFieldBuilder(pdfDocument, fieldName).createSignature()
                .setValue(fieldValue);
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDocument, true);
        acroForm.addField(formField);
        pdfDocument.close();
        return outputStream.toByteArray();
    }

    private static byte[] createDocumentWithSignatureField(String fieldName) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outputStream));
        PdfFormField formField = new SignatureFormFieldBuilder(pdfDocument, fieldName).createSignature();
        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDocument, true);
        acroForm.addField(formField);
        pdfDocument.close();
        return outputStream.toByteArray();
    }

    private static byte[] createEncryptedDocumentWithoutWidgetAnnotation() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument document = new PdfDocument(new PdfWriter(outputStream,
                new WriterProperties()
                        .setStandardEncryption(USER, OWNER, 0, EncryptionConstants.STANDARD_ENCRYPTION_128)));
        document.addNewPage();
        document.close();
        return outputStream.toByteArray();
    }

    private static byte[] createSimpleDocument() {
        return createSimpleDocument(PdfVersion.PDF_1_7);
    }

    private static byte[] createSimpleDocument(PdfVersion version) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        WriterProperties writerProperties = new WriterProperties();
        if (null != version) {
            writerProperties.setPdfVersion(version);
        }
        PdfDocument document = new PdfDocument(new PdfWriter(outputStream, writerProperties));
        document.addNewPage();
        document.close();
        return outputStream.toByteArray();
    }

    // Android-Conversion-Skip-Block-Start (TODO DEVSIX-7372 investigate why a few tests related to PdfA in PdfSignerUnitTest were cut)
    private static byte[] createSimplePdfaDocument() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(outputStream);
        InputStream is = FileUtil.getInputStreamForFile(PDFA_RESOURCES + "sRGB Color Space Profile.icm");
        PdfOutputIntent outputIntent =
                new PdfOutputIntent("Custom", "", "http://www.color.org", "sRGB IEC61966-2.1", is);
        PdfDocument document = new PdfADocument(writer, PdfAConformance.PDF_A_1A, outputIntent);

        document.setTagged();
        document.getCatalog().setLang(new PdfString("en-US"));

        document.addNewPage();
        document.close();

        return outputStream.toByteArray();
    }
    // Android-Conversion-Skip-Block-End

    static class ExtendedPdfSignatureFormField extends PdfSignatureFormField {
        public ExtendedPdfSignatureFormField(PdfWidgetAnnotation widgetAnnotation, PdfDocument document) {
            super(widgetAnnotation, document);
        }
    }

    static class DummySignatureEvent implements ISignatureEvent {

        @Override
        public void getSignatureDictionary(PdfSignature sig) {
            // Do nothing.
        }
    }
}
