/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.forms.fields.merging;


import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class OnDuplicateFormFieldNameStrategyTest extends ExtendedITextTest {


    @Test
    public void alwaysThrowExceptionOnDuplicateFormFieldName01() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true, new AlwaysThrowExceptionStrategy());
        PdfButtonFormField field1 = new CheckBoxFormFieldBuilder(pdfDocument, "test").createCheckBox();
        form.addField(field1);
        PdfButtonFormField field2 = new CheckBoxFormFieldBuilder(pdfDocument,
                "test").createCheckBox();
        Assert.assertThrows(PdfException.class, () -> form.addField(field2));
        pdfDocument.close();
    }

    @Test
    public void alwaysThrowExceptionOnDuplicateFormFieldName02() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true, new AlwaysThrowExceptionStrategy());
        form.addField(new CheckBoxFormFieldBuilder(pdfDocument, "test").createCheckBox());
        form.addField(new CheckBoxFormFieldBuilder(pdfDocument, "test1").createCheckBox());

        Assert.assertNotNull(form.getField("test"));
        Assert.assertNotNull(form.getField("test1"));

        pdfDocument.close();
    }

    @Test
    public void incrementFieldNameEven() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true, new AddIndexStrategy());
        for (int i = 0; i < 2; i++) {
            PdfButtonFormField field1 = new CheckBoxFormFieldBuilder(pdfDocument, "test").createCheckBox();
            form.addField(field1);
            PdfButtonFormField field2 = new CheckBoxFormFieldBuilder(pdfDocument, "bingbong").createCheckBox();
            form.addField(field2);
        }
        PdfFormField field1 = form.getField("test");
        PdfFormField field2 = form.getField("bingbong");
        PdfFormField field3 = form.getField("test_1");
        PdfFormField field4 = form.getField("bingbong_1");

        Assert.assertNotNull(field1);
        Assert.assertNotNull(field2);
        Assert.assertNotNull(field3);
        Assert.assertNotNull(field4);
    }

    @Test
    public void testAddFormFieldWithoutConfiguration() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos));) {
            PdfFormField field1 = new TextFormFieldBuilder(pdfDocument, "parent").createText();
            PdfFormField child1 = new TextFormFieldBuilder(pdfDocument, "child").createText();
            PdfFormField child2 = new TextFormFieldBuilder(pdfDocument, "child").createText();
            field1.addKid(child1);
            field1.addKid(child2);
            Assert.assertEquals(1, field1.getKids().size());
        }
    }

    @Test
    public void incrementFieldNameUnEven() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true, new AddIndexStrategy());
        for (int i = 0; i < 3; i++) {
            PdfButtonFormField field1 = new CheckBoxFormFieldBuilder(pdfDocument, "test").createCheckBox();
            form.addField(field1);
            PdfButtonFormField field2 = new CheckBoxFormFieldBuilder(pdfDocument, "bingbong").createCheckBox();
            form.addField(field2);
        }

        PdfFormField field1 = form.getField("test");
        PdfFormField field2 = form.getField("bingbong");
        PdfFormField field3 = form.getField("test_1");
        PdfFormField field4 = form.getField("bingbong_1");
        PdfFormField field5 = form.getField("test_2");
        PdfFormField field6 = form.getField("bingbong_2");

        Assert.assertNotNull(field1);
        Assert.assertNotNull(field2);
        Assert.assertNotNull(field3);
        Assert.assertNotNull(field4);
        Assert.assertNotNull(field5);
        Assert.assertNotNull(field6);

        pdfDocument.close();
    }


    @Test
    public void addIndexDotOperatorThrowsException() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true, new AddIndexStrategy("."));
            });
        }
    }

    @Test
    public void addIndexNullOperatorThrowsException() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {
            Assert.assertThrows(IllegalArgumentException.class, () -> {
                PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true, new AddIndexStrategy(null));
            });
        }
    }


    @Test
    public void invalidParamsToExecuteNull() {
        Assert.assertFalse(new AddIndexStrategy().execute(null, null, false));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 4)
    })
    public void flattenReadOnlyAddIndexTo() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfWriter writer = new PdfWriter(baos);
        PdfDocument pdfDoc = new PdfDocument(writer);

        final String sourceFolder = "./src/test/resources/com/itextpdf/forms/FormFieldFlatteningTest/";
        try (PdfDocument pdfInnerDoc = new PdfDocument(new PdfReader(sourceFolder + "readOnlyForm.pdf"))) {
            pdfInnerDoc.copyPagesTo(1, pdfInnerDoc.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());
        }
        try (PdfDocument pdfInnerDoc = new PdfDocument(new PdfReader(sourceFolder + "readOnlyForm.pdf"))) {
            pdfInnerDoc.copyPagesTo(1, pdfInnerDoc.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());
        }

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, false, new AddIndexStrategy());
        boolean isReadOnly = true;
        for (PdfFormField field : form.getAllFormFields().values()) {
            isReadOnly = (isReadOnly && field.isReadOnly());
        }
        int amount = form.getAllFormFields().size();
        pdfDoc.close();
        Assert.assertTrue(isReadOnly);
        Assert.assertEquals(4, amount);
    }

}