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
package com.itextpdf.forms.fields.merging;


import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.PdfPageFormCopier;
import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormFactory;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.forms.form.element.CheckBox;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class OnDuplicateFormFieldNameStrategyTest extends ExtendedITextTest {

    private final static String DESTINATION_FOLDER = "./target/test/com/itextpdf/forms/merging/";
    private final static String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/merging/";

    @BeforeEach
    public void setUp() {
        createDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void alwaysThrowExceptionOnDuplicateFormFieldName01() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true, new AlwaysThrowExceptionStrategy());
        PdfButtonFormField field1 = new CheckBoxFormFieldBuilder(pdfDocument, "test").createCheckBox();
        form.addField(field1);
        PdfButtonFormField field2 = new CheckBoxFormFieldBuilder(pdfDocument,
                "test").createCheckBox();
        Assertions.assertThrows(PdfException.class, () -> form.addField(field2));
        pdfDocument.close();
    }

    @Test
    public void alwaysThrowExceptionOnDuplicateFormFieldName02() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true, new AlwaysThrowExceptionStrategy());
        form.addField(new CheckBoxFormFieldBuilder(pdfDocument, "test").createCheckBox());
        form.addField(new CheckBoxFormFieldBuilder(pdfDocument, "test1").createCheckBox());

        Assertions.assertNotNull(form.getField("test"));
        Assertions.assertNotNull(form.getField("test1"));

        pdfDocument.close();
    }

    @Test
    public void incrementFieldNameEven() throws IOException, InterruptedException {
        String destination = DESTINATION_FOLDER + "incrementFieldNameEven.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true, new AddIndexStrategy());
            for (int i = 1; i < 3; i++) {
                Rectangle rect = new Rectangle(20, 20);
                rect.setY(100 * i);
                rect.setX(100);
                PdfButtonFormField field1 = new CheckBoxFormFieldBuilder(pdfDocument, "test").setWidgetRectangle(rect)
                        .createCheckBox();
                form.addField(field1);
                Rectangle rect2 = new Rectangle(20, 20);
                rect2.setY(100 * i);
                rect2.setX(200);
                PdfButtonFormField field2 = new CheckBoxFormFieldBuilder(pdfDocument, "bingbong")
                        .setWidgetRectangle(rect2)
                        .createCheckBox();
                form.addField(field2);
            }
            PdfFormField field1 = form.getField("test");
            PdfFormField field2 = form.getField("bingbong");
            PdfFormField field3 = form.getField("test_1");
            PdfFormField field4 = form.getField("bingbong_1");

            Assertions.assertNotNull(field1);
            Assertions.assertNotNull(field2);
            Assertions.assertNotNull(field3);
            Assertions.assertNotNull(field4);
        }

        Assertions.assertNull(new CompareTool().compareByContent(destination,
                SOURCE_FOLDER + "cmp_incrementalFieldNameEven.pdf", DESTINATION_FOLDER, "diff_"));

    }

    @Test
    public void testAddFormFieldWithoutConfiguration() throws IOException, InterruptedException {
        String destination = DESTINATION_FOLDER + "testAddFormFieldWithoutConfiguration.pdf";
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));) {
            Rectangle rect = new Rectangle(20, 20);
            rect.setY(100);
            rect.setX(100);
            PdfFormField field1 = new TextFormFieldBuilder(pdfDocument, "parent")
                    .setWidgetRectangle(rect)
                    .createText();
            Rectangle rect2 = new Rectangle(20, 20);
            rect2.setY(100);
            rect2.setX(200);
            PdfFormField child1 = new TextFormFieldBuilder(pdfDocument, "child")
                    .setWidgetRectangle(rect2)
                    .createText();
            Rectangle rect3 = new Rectangle(20, 20);
            rect3.setY(100);
            rect3.setX(300);
            PdfFormField child2 = new TextFormFieldBuilder(pdfDocument, "child")
                    .setWidgetRectangle(rect3)
                    .createText();
            field1.addKid(child1);
            field1.addKid(child2);
            PdfAcroForm.getAcroForm(pdfDocument, true).addField(field1);
            Assertions.assertEquals(2, field1.getKids().size());
        }

        Assertions.assertNull(new CompareTool().compareByContent(destination,
                SOURCE_FOLDER + "cmp_testAddFormFieldWithoutConfiguration.pdf", DESTINATION_FOLDER, "diff_"));
    }

    @Test
    public void incrementFieldNameUnEven() throws IOException, InterruptedException {
        String destination = DESTINATION_FOLDER + "incrementFieldNameUnEven.pdf";
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(destination));

        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true, new AddIndexStrategy());
        for (int i = 1; i < 4; i++) {
            Rectangle rect = new Rectangle(20, 20);
            rect.setY(100 * i);
            rect.setX(100);
            PdfButtonFormField field1 = new CheckBoxFormFieldBuilder(pdfDocument, "test")
                    .setWidgetRectangle(rect)
                    .createCheckBox();
            form.addField(field1);
            Rectangle rect2 = new Rectangle(20, 20);
            rect2.setY(100 * i);
            rect2.setX(200);
            PdfButtonFormField field2 = new CheckBoxFormFieldBuilder(pdfDocument, "bingbong")
                    .setWidgetRectangle(rect2)
                    .createCheckBox();
            form.addField(field2);
        }

        PdfFormField field1 = form.getField("test");
        PdfFormField field2 = form.getField("bingbong");
        PdfFormField field3 = form.getField("test_1");
        PdfFormField field4 = form.getField("bingbong_1");
        PdfFormField field5 = form.getField("test_2");
        PdfFormField field6 = form.getField("bingbong_2");

        Assertions.assertNotNull(field1);
        Assertions.assertNotNull(field2);
        Assertions.assertNotNull(field3);
        Assertions.assertNotNull(field4);
        Assertions.assertNotNull(field5);
        Assertions.assertNotNull(field6);

        pdfDocument.close();

        Assertions.assertNull(new CompareTool().compareByContent(destination,
                SOURCE_FOLDER + "cmp_incrementFieldNameUnEven.pdf", DESTINATION_FOLDER, "diff_"));
    }


    @Test
    public void addIndexDotOperatorThrowsException() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true, new AddIndexStrategy("."));
            });
        }
    }

    @Test
    public void addIndexNullOperatorThrowsException() {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(baos))) {
            Assertions.assertThrows(IllegalArgumentException.class, () -> {
                PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true, new AddIndexStrategy(null));
            });
        }
    }


    @Test
    public void invalidParamsToExecuteNull() {
        Assertions.assertFalse(new AddIndexStrategy().execute(null, null, false));
    }

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 4)
    })
    public void flattenReadOnlyAddIndexTo() throws IOException, InterruptedException {
        String destination = DESTINATION_FOLDER + "flattenReadOnlyAddIndexTo.pdf";
        PdfWriter writer = new PdfWriter(destination);
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
        Assertions.assertTrue(isReadOnly);
        Assertions.assertEquals(4, amount);

        Assertions.assertNull(new CompareTool().compareByContent(destination,
                SOURCE_FOLDER + "cmp_flattenReadOnlyAddIndexTo.pdf", DESTINATION_FOLDER, "diff_"));
    }


    @Test
    public void addIndexStrategySeparatesTheFields() throws IOException, InterruptedException {

        try {
            PdfFormCreator.setFactory(new PdfFormFactory() {
                @Override
                public PdfAcroForm getAcroForm(PdfDocument document, boolean createIfNotExist) {
                    return PdfAcroForm.getAcroForm(document, createIfNotExist, new AddIndexStrategy());
                }
            });

            try (PdfDocument pdfInnerDoc = new PdfDocument(new PdfWriter(DESTINATION_FOLDER + "add_index.pdf"))) {
                Document doc = new Document(pdfInnerDoc);

                doc.add(new CheckBox("test1").setBorder(new SolidBorder(ColorConstants.RED, 1)));
                doc.add(new CheckBox("test1").setBorder(new SolidBorder(ColorConstants.RED, 1)));

                doc.add(new CheckBox("test").setInteractive(true));
                doc.add(new CheckBox("test").setInteractive(true));
            }

            Assertions.assertNull(new CompareTool().compareByContent(DESTINATION_FOLDER + "add_index.pdf",
                    SOURCE_FOLDER + "cmp_add_index.pdf", DESTINATION_FOLDER, "diff_"));

        } finally {
            PdfFormCreator.setFactory(new PdfFormFactory());
        }

    }
}
