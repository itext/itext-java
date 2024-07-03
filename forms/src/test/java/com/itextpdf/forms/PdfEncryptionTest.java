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
package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.forms.fields.RadioFormFieldBuilder;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;
import com.itextpdf.kernel.pdf.EncryptionConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.ReaderProperties;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Tag("BouncyCastleIntegrationTest")
public class PdfEncryptionTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfEncryptionTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfEncryptionTest/";

    /**
     * User password.
     */
    public static byte[] USER = "Hello".getBytes(StandardCharsets.ISO_8859_1);

    /**
     * Owner password.
     */
    public static byte[] OWNER = "World".getBytes(StandardCharsets.ISO_8859_1);

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    // Custom entry in Info dictionary is used because standard entried are gone into metadata in PDF 2.0
    static final String customInfoEntryKey = "Custom";
    static final String customInfoEntryValue = "String";

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptedDocumentWithFormFields() throws IOException {
        PdfReader reader = new PdfReader(sourceFolder + "encryptedDocumentWithFormFields.pdf",
                new ReaderProperties().setPassword("12345".getBytes(StandardCharsets.ISO_8859_1)));
        PdfDocument pdfDocument = new PdfDocument(reader);

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDocument, false);

        acroForm.getField("personal.name").getPdfObject();
        pdfDocument.close();
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptAes256Pdf2PermissionsTest01() throws InterruptedException, IOException {
        String filename = "encryptAes256Pdf2PermissionsTest01.pdf";
        int permissions = EncryptionConstants.ALLOW_FILL_IN | EncryptionConstants.ALLOW_SCREENREADERS | EncryptionConstants.ALLOW_DEGRADED_PRINTING;
        PdfDocument pdfDoc = new PdfDocument(
                new PdfWriter(destinationFolder + filename,
                        new WriterProperties()
                                .setPdfVersion(PdfVersion.PDF_2_0)
                                .setStandardEncryption(USER, OWNER, permissions, EncryptionConstants.ENCRYPTION_AES_256)));
        pdfDoc.getDocumentInfo().setMoreInfo(customInfoEntryKey, customInfoEntryValue);
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfTextFormField textField1 = new TextFormFieldBuilder(pdfDoc, "Name")
                .setWidgetRectangle(new Rectangle(100, 600, 200, 30)).createText();
        textField1.setValue("Enter your name");
        form.addField(textField1);
        PdfTextFormField textField2 = new TextFormFieldBuilder(pdfDoc, "Surname")
                .setWidgetRectangle(new Rectangle(100, 550, 200, 30)).createText();
        textField2.setValue("Enter your surname");
        form.addField(textField2);

        String sexFormFieldName = "Sex";
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(pdfDoc, sexFormFieldName);
        PdfButtonFormField group = builder.createRadioGroup();
        group.setValue("Male");
        PdfFormAnnotation radio1 = builder
                .createRadioButton( "Male",new Rectangle(100, 530, 10, 10));
        PdfFormAnnotation radio2 = builder
                .createRadioButton( "Female",  new Rectangle(120, 530, 10, 10));
        group.addKid(radio1);
        group.addKid(radio2);

        form.addField(group);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_", USER, USER);
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    @LogMessages(messages = @LogMessage(messageTemplate = KernelLogMessageConstant.MD5_IS_NOT_FIPS_COMPLIANT,
            ignore = true))
    public void encryptAes256Pdf2PermissionsTest02() throws InterruptedException, IOException {
        String filename = "encryptAes256Pdf2PermissionsTest02.pdf";
        // This test differs from the previous one (encryptAes256Pdf2PermissionsTest01) only in permissions.
        // Here we do not allow to fill the form in.
        int permissions = EncryptionConstants.ALLOW_SCREENREADERS | EncryptionConstants.ALLOW_DEGRADED_PRINTING;
        PdfDocument pdfDoc = new PdfDocument(
                new PdfWriter(destinationFolder + filename,
                        new WriterProperties()
                                .setPdfVersion(PdfVersion.PDF_2_0)
                                .setStandardEncryption(USER, OWNER, permissions, EncryptionConstants.ENCRYPTION_AES_256)));
        pdfDoc.getDocumentInfo().setMoreInfo(customInfoEntryKey, customInfoEntryValue);
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfTextFormField textField1 = new TextFormFieldBuilder(pdfDoc, "Name")
                .setWidgetRectangle(new Rectangle(100, 600, 200, 30)).createText();
        textField1.setValue("Enter your name");
        form.addField(textField1);
        PdfTextFormField textField2 = new TextFormFieldBuilder(pdfDoc, "Surname")
                .setWidgetRectangle(new Rectangle(100, 550, 200, 30)).createText();
        textField2.setValue("Enter your surname");
        form.addField(textField2);

        String sexFormFieldName = "Sex";
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(pdfDoc, sexFormFieldName);
        PdfButtonFormField group = builder.createRadioGroup();
        group.setValue("Male");
        PdfFormAnnotation radio1 = new RadioFormFieldBuilder(pdfDoc, sexFormFieldName)
                .createRadioButton("Male",new Rectangle(100, 530, 10, 10));
        PdfFormAnnotation radio2 = new RadioFormFieldBuilder(pdfDoc, sexFormFieldName)
                .createRadioButton( "Female",new Rectangle(120, 530, 10, 10));

        group.addKid(radio1);
        group.addKid(radio2);

        form.addField(group);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(destinationFolder + filename, sourceFolder + "cmp_" + filename, destinationFolder, "diff_", USER, USER);
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }
}
