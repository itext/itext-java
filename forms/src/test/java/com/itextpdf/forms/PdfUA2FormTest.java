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

import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.forms.form.element.InputField;
import com.itextpdf.forms.form.element.SignatureFieldAppearance;
import com.itextpdf.forms.form.element.TextArea;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.font.PdfFontFactory.EmbeddingStrategy;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfDocumentInfo;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfVersion;
import com.itextpdf.kernel.pdf.PdfViewerPreferences;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.kernel.pdf.tagging.StandardRoles;
import com.itextpdf.kernel.pdf.tagutils.TagTreePointer;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.kernel.xmp.XMPException;
import com.itextpdf.kernel.xmp.XMPMeta;
import com.itextpdf.kernel.xmp.XMPMetaFactory;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.borders.SolidBorder;
import com.itextpdf.layout.element.Div;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.pdfa.VeraPdfValidator; // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.fail;

@Tag("IntegrationTest")
public class PdfUA2FormTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/PdfUA2FormTest/";
    public static final String DESTINATION_FOLDER = "./target/test/com/itextpdf/forms//PdfUA2FormTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void checkFormFieldTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "formFieldTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_formFieldTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDocument, true);
            Rectangle rect = new Rectangle(210, 490, 150, 22);
            PdfTextFormField field = new TextFormFieldBuilder(pdfDocument, "fieldName")
                    .setWidgetRectangle(rect).createText();
            field.put(PdfName.Contents, new PdfString("Description"));
            field.setValue("some value");
            field.setFont(font);
            form.addField(field);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkTextAreaWithLabelTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "textAreaTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_textAreaTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            Paragraph paragraph = new Paragraph("Widget label").setFont(font);
            paragraph.getAccessibilityProperties().setRole(StandardRoles.LBL);

            TextArea formTextArea = new TextArea("form text1");
            formTextArea.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formTextArea.setProperty(FormProperty.FORM_FIELD_VALUE, "form\ntext\narea");

            Div div = new Div();
            div.getAccessibilityProperties().setRole(StandardRoles.FORM);
            div.add(paragraph);
            div.add(formTextArea);

            document.add(div);
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkInputFieldTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "inputFieldTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_inputFieldTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);

            InputField formInputField = new InputField("form input field");
            formInputField.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formInputField.setProperty(FormProperty.FORM_FIELD_VALUE, "form input field");
            formInputField.setProperty(FormProperty.FORM_FIELD_LABEL, "label form field");

            document.add(formInputField);
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDocument, true);
            form.getField("form input field").getPdfObject().put(PdfName.Contents, new PdfString("Description"));
        }
        compareAndValidate(outFile, cmpFile);
    }

    @Test
    public void checkSignatureFormTest() throws IOException, XMPException, InterruptedException {
        String outFile = DESTINATION_FOLDER + "signatureFormTest.pdf";
        String cmpFile = SOURCE_FOLDER + "cmp_signatureFormTest.pdf";

        try (PdfDocument pdfDocument = new PdfDocument(new PdfWriter(outFile, new WriterProperties().setPdfVersion(
                PdfVersion.PDF_2_0)))){
            Document document = new Document(pdfDocument);
            PdfFont font = PdfFontFactory.createFont(SOURCE_FOLDER + "FreeSans.ttf",
                    "WinAnsi", EmbeddingStrategy.FORCE_EMBEDDED);
            document.setFont(font);
            createSimplePdfUA2Document(pdfDocument);
            TagTreePointer tagPointer = pdfDocument.getTagStructureContext().getAutoTaggingPointer();
            tagPointer.addTag(StandardRoles.FIGURE);
            tagPointer.getProperties().setAlternateDescription("Alt Description");

            SignatureFieldAppearance formSigField = new SignatureFieldAppearance("form SigField");
            formSigField.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formSigField.setContent("form SigField");

            formSigField.setBorder(new SolidBorder(ColorConstants.YELLOW, 1));
            formSigField.setFont(font);
            document.add(formSigField);
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDocument, true);
            form.getField("form SigField").getPdfObject().put(PdfName.Contents, new PdfString("Description"));
        }
        compareAndValidate(outFile, cmpFile);
    }

    private void createSimplePdfUA2Document(PdfDocument pdfDocument) throws IOException, XMPException {
        byte[] bytes = Files.readAllBytes(Paths.get(SOURCE_FOLDER + "simplePdfUA2.xmp"));
        XMPMeta xmpMeta = XMPMetaFactory.parse(new ByteArrayInputStream(bytes));
        pdfDocument.setXmpMetadata(xmpMeta);
        pdfDocument.setTagged();
        pdfDocument.getCatalog().setViewerPreferences(new PdfViewerPreferences().setDisplayDocTitle(true));
        pdfDocument.getCatalog().setLang(new PdfString("en-US"));
        PdfDocumentInfo info = pdfDocument.getDocumentInfo();
        info.setTitle("PdfUA2 Title");
    }

    private void compareAndValidate(String outPdf, String cmpPdf) throws IOException, InterruptedException {
        Assertions.assertNull(new VeraPdfValidator().validate(outPdf)); // Android-Conversion-Skip-Line (TODO DEVSIX-7377 introduce pdf\a validation on Android)
        String result = new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER, "diff_");
        if (result != null) {
            fail(result);
        }
    }
}
