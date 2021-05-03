package com.itextpdf.signatures;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfSigFieldLock;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfSignatureFormField;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.StampingProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayInputStream;
import java.io.IOException;

@Category(UnitTest.class)
public class PdfSignerUnitTest extends ExtendedITextTest {

    @Test
    public void createNewSignatureFormFieldInvisibleAnnotationTest() throws IOException {
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(createDocumentWithoutWidgetAnnotation()),
                        new ReaderProperties().setPassword("owner".getBytes())), new ByteArrayOutputStream(), new StampingProperties());
        signer.cryptoDictionary = new PdfSignature();
        signer.appearance.setPageRect(new Rectangle(100, 100, 0, 0));

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(signer.document, true);
        signer.createNewSignatureFormField(acroForm, signer.fieldName);
        PdfFormField formField = acroForm.getField(signer.fieldName);

        PdfDictionary formFieldDictionary = formField.getPdfObject();
        Assert.assertNotNull(formFieldDictionary);
        Assert.assertFalse(formFieldDictionary.containsKey(PdfName.AP));
    }

    @Test
    public void createNewSignatureFormFieldNotInvisibleAnnotationTest() throws IOException {
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(createDocumentWithoutWidgetAnnotation()),
                        new ReaderProperties().setPassword("owner".getBytes())), new ByteArrayOutputStream(), new StampingProperties());
        signer.cryptoDictionary = new PdfSignature();
        signer.appearance.setPageRect(new Rectangle(100, 100, 10, 10));
        PdfSigFieldLock fieldLock = new PdfSigFieldLock();
        signer.fieldLock = fieldLock;

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(signer.document, true);
        Assert.assertEquals(fieldLock, signer.createNewSignatureFormField(acroForm, signer.fieldName));
        PdfFormField formField = acroForm.getField(signer.fieldName);

        PdfDictionary formFieldDictionary = formField.getPdfObject();
        Assert.assertNotNull(formFieldDictionary);
        Assert.assertTrue(formFieldDictionary.containsKey(PdfName.AP));
    }

    @Test
    public void populateExistingSignatureFormFieldInvisibleAnnotationTest() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument document = new PdfDocument(new PdfWriter(outputStream,
                new WriterProperties().setStandardEncryption("user".getBytes(), "owner".getBytes(), 0, EncryptionConstants.STANDARD_ENCRYPTION_128)));
        document.addNewPage();
        PdfWidgetAnnotation widgetAnnotation = new PdfWidgetAnnotation(new Rectangle(100, 100, 0, 0));
        document.getPage(1).addAnnotation(widgetAnnotation);
        document.close();
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()), new ReaderProperties().setPassword("owner".getBytes())),
                new ByteArrayOutputStream(), new StampingProperties());
        signer.cryptoDictionary = new PdfSignature();
        signer.appearance.setPageRect(new Rectangle(100, 100, 0, 0));

        widgetAnnotation = (PdfWidgetAnnotation) signer.document.getPage(1).getAnnotations().get(0);
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(signer.document, true);
        PdfFormField formField = new ExtendedPdfSignatureFormField(widgetAnnotation, signer.document);
        formField.setFieldName(signer.fieldName);
        acroForm.addField(formField);
        signer.populateExistingSignatureFormField(acroForm);
        formField = acroForm.getField(signer.fieldName);

        PdfDictionary formFieldDictionary = formField.getPdfObject();
        Assert.assertNotNull(formFieldDictionary);
        Assert.assertFalse(formFieldDictionary.containsKey(PdfName.AP));
    }

    @Test
    public void populateExistingSignatureFormFieldNotInvisibleAnnotationTest() throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument document = new PdfDocument(new PdfWriter(outputStream,
                new WriterProperties().setStandardEncryption("user".getBytes(), "owner".getBytes(), 0, EncryptionConstants.STANDARD_ENCRYPTION_128)));
        document.addNewPage();
        PdfWidgetAnnotation widgetAnnotation = new PdfWidgetAnnotation(new Rectangle(100, 100, 0, 0));
        document.getPage(1).addAnnotation(widgetAnnotation);
        document.close();
        PdfSigner signer = new PdfSigner(
                new PdfReader(new ByteArrayInputStream(outputStream.toByteArray()), new ReaderProperties().setPassword("owner".getBytes())),
                new ByteArrayOutputStream(), new StampingProperties());
        signer.cryptoDictionary = new PdfSignature();
        PdfSigFieldLock fieldLock = new PdfSigFieldLock();
        signer.fieldLock = fieldLock;
        signer.appearance.setPageRect(new Rectangle(100, 100, 10, 10));

        widgetAnnotation = (PdfWidgetAnnotation) signer.document.getPage(1).getAnnotations().get(0);
        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(signer.document, true);
        PdfFormField formField = new ExtendedPdfSignatureFormField(widgetAnnotation, signer.document);
        formField.setFieldName(signer.fieldName);
        acroForm.addField(formField);
        Assert.assertEquals(signer.populateExistingSignatureFormField(acroForm), fieldLock);
        formField = acroForm.getField(signer.fieldName);

        PdfDictionary formFieldDictionary = formField.getPdfObject();
        Assert.assertNotNull(formFieldDictionary);
        Assert.assertTrue(formFieldDictionary.containsKey(PdfName.AP));
    }

    private static byte[] createDocumentWithoutWidgetAnnotation() {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        PdfDocument document = new PdfDocument(new PdfWriter(outputStream,
                new WriterProperties().setStandardEncryption("user".getBytes(), "owner".getBytes(), 0, EncryptionConstants.STANDARD_ENCRYPTION_128)));
        document.addNewPage();
        document.close();
        return outputStream.toByteArray();
    }

    static class ExtendedPdfSignatureFormField extends PdfSignatureFormField {
        public ExtendedPdfSignatureFormField(PdfWidgetAnnotation widgetAnnotation, PdfDocument document) {
            super(widgetAnnotation, document);
        }
    }
}
