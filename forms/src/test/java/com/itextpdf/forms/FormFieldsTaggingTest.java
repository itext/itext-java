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

import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PushButtonFormFieldBuilder;
import com.itextpdf.forms.fields.RadioFormFieldBuilder;
import com.itextpdf.io.logs.IoLogMessageConstant;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import org.xml.sax.SAXException;

@Tag("IntegrationTest")
public class FormFieldsTaggingTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/FormFieldsTaggingTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/FormFieldsTaggingTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    /**
     * Form fields addition to the tagged document.
     */
    @Test
    public void formFieldTaggingTest01() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms01.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms01.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfDocument pdfDoc = new PdfDocument(writer);
        pdfDoc.setTagged();

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        addFormFieldsToDocument(pdfDoc, form);

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    /**
     * Form fields copying from the tagged document.
     */
    @Test
    public void formFieldTaggingTest02() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms02.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        pdfDoc.setTagged();
        pdfDoc.initializeOutlines();

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDoc, true);
        acroForm.addField(new CheckBoxFormFieldBuilder(pdfDoc, "TestCheck")
                .setWidgetRectangle(new Rectangle(36, 560, 20, 20)).createCheckBox().setValue("1", true));

        PdfDocument docToCopyFrom = new PdfDocument(new PdfReader(sourceFolder + "cmp_taggedPdfWithForms07.pdf"));
        docToCopyFrom.copyPagesTo(1, docToCopyFrom.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    /**
     * Form fields flattening in the tagged document.
     */
    @Test
    public void formFieldTaggingTest03() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms03.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "cmp_taggedPdfWithForms01.pdf"), new PdfWriter(outFileName));

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDoc, false);
        acroForm.flattenFields();

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    /**
     * Removing fields from tagged document.
     */
    @Test
    public void formFieldTaggingTest04() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms04.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms04.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "cmp_taggedPdfWithForms01.pdf"), new PdfWriter(outFileName));

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDoc, false);
        acroForm.removeField("TestCheck");
        acroForm.removeField("push");

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    /**
     * Form fields flattening in the tagged document (writer mode).
     */
    @Test
    public void formFieldTaggingTest05() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms05.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms05.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        pdfDoc.setTagged();

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        addFormFieldsToDocument(pdfDoc, form);

        form.flattenFields();

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    /**
     * Removing fields from tagged document (writer mode).
     */
    @Test
    public void formFieldTaggingTest06() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms06.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms06.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        pdfDoc.setTagged();

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        addFormFieldsToDocument(pdfDoc, form);

        form.removeField("TestCheck");
        form.removeField("push");

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    /**
     * Addition of the form field at the specific position in tag structure.
     */
    @Test
    public void formFieldTaggingTest07() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms07.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms07.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocWithFields.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, writer);

        // Original document is already tagged, so there is no need to mark it as tagged again
//        pdfDoc.setTagged();

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfButtonFormField pushButton = new PushButtonFormFieldBuilder(pdfDoc, "push")
                .setWidgetRectangle(new Rectangle(36, 650, 40, 20)).setCaption("Capcha").createPushButton();
        pushButton.setFontSize(12f);

        TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
        tagPointer.moveToKid(StandardRoles.DIV);
        acroForm.addField(pushButton);

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    @Test
    public void mergeFieldTaggingTest08() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "mergeFieldTaggingTest08.pdf";
        String cmpFileName = sourceFolder + "cmp_mergeFieldTaggingTest08.pdf";
        String srcFileName = sourceFolder + "mergeFieldTaggingTest08.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcFileName), new PdfWriter(outFileName))) {
            pdfDoc.setTagged();

            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

            addFormFieldsToDocument(pdfDoc, form);
        }

        compareOutput(outFileName, cmpFileName);
    }

    @Test
    public void mergeFieldTaggingTest09() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "mergeFieldTaggingTest09.pdf";
        String cmpFileName = sourceFolder + "cmp_mergeFieldTaggingTest09.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName))) {
            pdfDoc.setTagged();

            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

            addFormFieldsToDocument(pdfDoc, form);
            addFormFieldsToDocument(pdfDoc, form);
        }

        compareOutput(outFileName, cmpFileName);
        compareOutput(outFileName, sourceFolder + "cmp_mergeFieldTaggingTest08.pdf");
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = IoLogMessageConstant.DOCUMENT_ALREADY_HAS_FIELD, count = 2)})
    public void formFieldTaggingTest10() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms10.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms10.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outFileName));
        pdfDoc.setTagged();
        pdfDoc.initializeOutlines();

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDoc, true);
        acroForm.addField(new CheckBoxFormFieldBuilder(pdfDoc, "TestCheck")
                .setWidgetRectangle(new Rectangle(36, 560, 20, 20)).createCheckBox().setValue("1", true));

        PdfDocument docToCopyFrom = new PdfDocument(new PdfReader(sourceFolder + "cmp_taggedPdfWithForms07.pdf"));
        docToCopyFrom.copyPagesTo(1, docToCopyFrom.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());
        docToCopyFrom.copyPagesTo(1, docToCopyFrom.getNumberOfPages(), pdfDoc, new PdfPageFormCopier());

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    @Test
    public void formFieldTaggingTest11() throws IOException, InterruptedException, ParserConfigurationException, SAXException {
        String outFileName = destinationFolder + "taggedPdfWithForms11.pdf";
        String cmpFileName = sourceFolder + "cmp_taggedPdfWithForms11.pdf";

        PdfWriter writer = new PdfWriter(outFileName);
        PdfReader reader = new PdfReader(sourceFolder + "taggedDocWithFields.pdf");
        PdfDocument pdfDoc = new PdfDocument(reader, writer);
        pdfDoc.setTagged();

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDoc, true);

        PdfButtonFormField pushButton = new PushButtonFormFieldBuilder(pdfDoc, "push")
                .setWidgetRectangle(new Rectangle(36, 650, 40, 20)).setCaption("Button 1").createPushButton();
        pushButton.setFontSize(12f);

        PdfButtonFormField pushButton2 = new PushButtonFormFieldBuilder(pdfDoc, "push 2")
                .setWidgetRectangle(new Rectangle(36, 600, 40, 20)).setCaption("Button 2").createPushButton();
        pushButton.setFontSize(12f);

        TagTreePointer tagPointer = pdfDoc.getTagStructureContext().getAutoTaggingPointer();
        tagPointer.moveToKid(StandardRoles.DIV);
        acroForm.addField(pushButton);
        tagPointer.moveToKid(StandardRoles.FORM);
        acroForm.addField(pushButton2);

        pdfDoc.close();

        compareOutput(outFileName, cmpFileName);
    }

    private void addFormFieldsToDocument(PdfDocument pdfDoc, PdfAcroForm acroForm) {
        Rectangle rect = new Rectangle(36, 700, 20, 20);
        Rectangle rect1 = new Rectangle(36, 680, 20, 20);

        String formFieldName = "TestGroup";
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(pdfDoc, formFieldName);
        PdfButtonFormField group = builder.createRadioGroup();
        group.setValue("1", true);

        group.addKid(builder.createRadioButton("1", rect));
        group.addKid(builder.createRadioButton("2", rect1));

        acroForm.addField(group);

        PdfButtonFormField pushButton = new PushButtonFormFieldBuilder(pdfDoc, "push")
                .setWidgetRectangle(new Rectangle(36, 650, 40, 20)).setCaption("Capcha").createPushButton();
        PdfButtonFormField checkBox = new CheckBoxFormFieldBuilder(pdfDoc, "TestCheck")
                .setWidgetRectangle(new Rectangle(36, 560, 20, 20)).createCheckBox();
        checkBox.setValue("1", true);

        acroForm.addField(pushButton);
        acroForm.addField(checkBox);
    }

    private void compareOutput(String outFileName, String cmpFileName) throws InterruptedException, IOException, ParserConfigurationException, SAXException {
        CompareTool compareTool = new CompareTool();
        String compareResult = compareTool.compareTagStructures(outFileName, cmpFileName);
        if (compareResult != null) {
            Assertions.fail(compareResult);
        }

        compareResult = compareTool.compareByContent(outFileName, cmpFileName, destinationFolder, "diff" + outFileName);

        if (compareResult != null) {
            Assertions.fail(compareResult);
        }
    }
}
