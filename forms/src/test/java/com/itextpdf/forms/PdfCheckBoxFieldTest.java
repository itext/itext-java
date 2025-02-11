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
package com.itextpdf.forms;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.PdfFormAnnotation;
import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.properties.CheckBoxType;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfStream;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfCheckBoxFieldTest extends ExtendedITextTest {

    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfCheckBoxFieldTest/";
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfCheckBoxFieldTest/";

    @BeforeAll
    public static void beforeClass() {
        createOrClearDestinationFolder(destinationFolder);
    }

    @Test
    public void checkBoxFontSizeTest01() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "checkBoxFontSizeTest01.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxFontSizeTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        pdfDoc.addNewPage();
        addCheckBox(pdfDoc, 6, 750, 7, 7);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void checkBoxFontSizeTest02() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "checkBoxFontSizeTest02.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxFontSizeTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        pdfDoc.addNewPage();
        addCheckBox(pdfDoc, 0, 730, 7, 7);
        // fallback to default fontsize — 12 is expected.
        addCheckBox(pdfDoc, -1, 710, 7, 7);

        addCheckBox(pdfDoc, 0, 640, 20, 20);
        addCheckBox(pdfDoc, 0, 600, 40, 20);
        addCheckBox(pdfDoc, 0, 550, 20, 40);

        addCheckBox(pdfDoc, 0, 520, 5, 5);
        addCheckBox(pdfDoc, 0, 510, 5, 3);
        addCheckBox(pdfDoc, 0, 500, 3, 5);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void checkBoxFontSizeTest03() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "checkBoxFontSizeTest03.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxFontSizeTest03.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        pdfDoc.addNewPage();
        addCheckBox(pdfDoc, 2, 730, 7, 7);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void checkBoxFontSizeTest04() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "checkBoxFontSizeTest04.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxFontSizeTest04.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        pdfDoc.addNewPage();
        addCheckBox(pdfDoc, 0, 730, 10, new CheckBoxFormFieldBuilder(pdfDoc, "cb_1")
                .setWidgetRectangle(new Rectangle(50, 730, 10, 10)).createCheckBox()
                .setCheckType(CheckBoxType.CIRCLE).setValue("YES"));
        addCheckBox(pdfDoc, 0, 700, 10, new CheckBoxFormFieldBuilder(pdfDoc, "cb_2")
                .setWidgetRectangle(new Rectangle(50, 700, 10, 10)).createCheckBox()
                .setCheckType(CheckBoxType.CROSS).setValue("YES"));
        addCheckBox(pdfDoc, 0, 670, 10, new CheckBoxFormFieldBuilder(pdfDoc, "cb_3")
                .setWidgetRectangle(new Rectangle(50, 670, 10, 10)).createCheckBox()
                .setCheckType(CheckBoxType.DIAMOND).setValue("YES"));
        addCheckBox(pdfDoc, 0, 640, 10, new CheckBoxFormFieldBuilder(pdfDoc, "cb_4")
                .setWidgetRectangle(new Rectangle(50, 640, 10, 10)).createCheckBox()
                .setCheckType(CheckBoxType.SQUARE).setValue("YES"));
        addCheckBox(pdfDoc, 0, 610, 10, new CheckBoxFormFieldBuilder(pdfDoc, "cb_5")
                .setWidgetRectangle(new Rectangle(50, 610, 10, 10)).createCheckBox()
                .setCheckType(CheckBoxType.STAR).setValue("YES"));

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void checkBoxFontSizeTest05() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "checkBoxFontSizeTest05.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxFontSizeTest05.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf));
        pdfDoc.addNewPage();
        addCheckBox(pdfDoc, 0, 730, 40, 40);
        addCheckBox(pdfDoc, 0, 600, 100, 100);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void checkBoxToggleTest01() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "checkBoxToggledOn.pdf";
        String outPdf = destinationFolder + "checkBoxToggleTest01.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxToggleTest01.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfFormField checkBox = form.getField("cb_fs_6_7_7");
        checkBox.setValue("Off");

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void checkBoxToggleTest02() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "checkBoxToggledOn.pdf";
        String outPdf = destinationFolder + "checkBoxToggleTest02.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxToggleTest02.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfFormField checkBox = form.getField("cb_fs_6_7_7");
        checkBox.setValue("Off", false);

        pdfDoc.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder, "diff_");
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }
    
    @Test
    public void keepCheckTypeTest() throws IOException, InterruptedException {
        String srcPdf = destinationFolder + "keepCheckTypeTestInput.pdf";
        String outPdf = destinationFolder + "keepCheckTypeTest.pdf";
        String cmpPdf = sourceFolder + "cmp_keepCheckTypeTest.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(srcPdf))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

            PdfButtonFormField checkField = new CheckBoxFormFieldBuilder(pdfDoc, "checkField")
                    .setWidgetRectangle(new Rectangle(100, 600, 100, 100))
                    .setCheckType(CheckBoxType.CHECK).createCheckBox();
            checkField.setValue("Off");

            checkField.setFontSizeAutoScale();
            form.addField(checkField);
        }

        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
            form.getField("checkField").setValue("Yes");
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void appearanceRegenerationTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "appearanceRegenerationTest.pdf";
        String cmpPdf = sourceFolder + "cmp_appearanceRegenerationTest.pdf";

        try (PdfDocument pdfDoc = new PdfDocument(new PdfWriter(outPdf))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

            PdfButtonFormField checkBox1 = new CheckBoxFormFieldBuilder(pdfDoc, "checkbox1")
                    .setWidgetRectangle(new Rectangle(10, 650, 40, 20)).createCheckBox();
            checkBox1.setValue("My_Value");

            String offStream = "1 0 0 1 0.86 0.5 cm 0 0 m\n" +
                    "0 0.204 -0.166 0.371 -0.371 0.371 c\n" +
                    "-0.575 0.371 -0.741 0.204 -0.741 0 c\n" +
                    "-0.741 -0.204 -0.575 -0.371 -0.371 -0.371 c\n" +
                    "-0.166 -0.371 0 -0.204 0 0 c\n" +
                    "f\n";
            checkBox1.getFirstFormAnnotation().setAppearance(PdfName.N, "Off",
                    new PdfStream(offStream.getBytes()));
            String onStream = "1 0 0 1 0.835 0.835 cm 0 0 -0.669 -0.67 re\n" +
                    "f\n";
            checkBox1.getFirstFormAnnotation().setAppearance(PdfName.N, "My_Value",
                    new PdfStream(onStream.getBytes()));

            checkBox1.regenerateField();
            form.addField(checkBox1);
        }

        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void setValueForMutuallyExclusiveCheckBoxTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "setValueForMutuallyExclusiveCheckBox.pdf";
        String cmpPdf = sourceFolder + "cmp_setValueForMutuallyExclusiveCheckBox.pdf";
        String srcPdf = sourceFolder + "mutuallyExclusiveCheckBox.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(doc, true);
            PdfFormField radioGroupField = acroForm.getField("group");
            radioGroupField.setValue("1");
            radioGroupField.setValue("2");
            radioGroupField.regenerateField();
            PdfFormField checkBoxField = acroForm.getField("check");
            checkBoxField.setValue("1");
            checkBoxField.setValue("2");
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void changeOnStateAppearanceNameForCheckBoxWidgetTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "changeOnStateAppearanceNameForCheckBoxWidget.pdf";
        String cmpPdf = sourceFolder + "cmp_changeOnStateAppearanceNameForCheckBoxWidget.pdf";
        String srcPdf = sourceFolder + "mutuallyExclusiveCheckBox.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(doc, true);
            PdfFormField checkBoxField = acroForm.getField("check");
            checkBoxField.setValue("3");
            checkBoxField.getFirstFormAnnotation().setCheckBoxAppearanceOnStateName("3");
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void changeOnStateAppearanceNameSeveralTimesTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "changeOnStateAppearanceNameSeveralTimes.pdf";
        String cmpPdf = sourceFolder + "cmp_changeOnStateAppearanceNameSeveralTimes.pdf";
        String srcPdf = sourceFolder + "mutuallyExclusiveCheckBox.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(doc, true);
            PdfFormField checkBoxField = acroForm.getField("check");
            checkBoxField.setValue("3");
            checkBoxField.getFirstFormAnnotation().setCheckBoxAppearanceOnStateName("3");
            checkBoxField.getFirstFormAnnotation().setCheckBoxAppearanceOnStateName("1");
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void checkBoxWidgetAppearanceTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "checkBoxWidgetAppearance.pdf";
        String cmpPdf = sourceFolder + "cmp_checkBoxWidgetAppearance.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(doc, true);

            PdfButtonFormField checkBox = new CheckBoxFormFieldBuilder(doc, "checkbox")
                    .setWidgetRectangle(new Rectangle(10, 650, 40, 20)).createCheckBox();
            PdfFormAnnotation widget = checkBox.getFirstFormAnnotation();

            // Default case
            widget.setCheckBoxAppearanceOnStateName("initial");
            Assertions.assertTrue(Arrays.asList(widget.getAppearanceStates()).contains("initial"));
            Assertions.assertEquals("Off", widget.getPdfObject().getAsName(PdfName.AS).getValue());

            // Setting value changes on state name and appearance state for widget
            checkBox.setValue("value");
            Assertions.assertTrue(Arrays.asList(widget.getAppearanceStates()).contains("value"));
            Assertions.assertEquals("value", widget.getPdfObject().getAsName(PdfName.AS).getValue());

            // Setting value generates normal appearance and changes appearance state for widget
            widget.getWidget().setNormalAppearance(new PdfDictionary());
            checkBox.setValue("new_value");
            List<String> appearanceStates = Arrays.asList(widget.getAppearanceStates());
            Assertions.assertTrue(appearanceStates.contains("new_value"));
            Assertions.assertTrue(appearanceStates.contains("Off"));
            Assertions.assertEquals("new_value", widget.getPdfObject().getAsName(PdfName.AS).getValue());

            acroForm.addField(checkBox);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void setInvalidCheckBoxOnAppearanceTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "setInvalidCheckBoxOnAppearance.pdf";
        String cmpPdf = sourceFolder + "cmp_setInvalidCheckBoxOnAppearance.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(doc, true);

            PdfButtonFormField checkBox = new CheckBoxFormFieldBuilder(doc, "checkbox")
                    .setWidgetRectangle(new Rectangle(10, 650, 40, 20)).createCheckBox();
            PdfFormAnnotation widget = checkBox.getFirstFormAnnotation();
            checkBox.setValue("value");
            List<String> appearanceStates = Arrays.asList(widget.getAppearanceStates());
            Assertions.assertTrue(appearanceStates.contains("value"));
            Assertions.assertTrue(appearanceStates.contains("Off"));
            Assertions.assertEquals("value", widget.getPdfObject().getAsName(PdfName.AS).getValue());

            // Setting invalid appearance name for on state does nothing
            widget.setCheckBoxAppearanceOnStateName("Off");
            appearanceStates = Arrays.asList(widget.getAppearanceStates());
            Assertions.assertTrue(appearanceStates.contains("value"));
            Assertions.assertTrue(appearanceStates.contains("Off"));
            Assertions.assertEquals("value", widget.getPdfObject().getAsName(PdfName.AS).getValue());

            widget.setCheckBoxAppearanceOnStateName("");
            appearanceStates = Arrays.asList(widget.getAppearanceStates());
            Assertions.assertTrue(appearanceStates.contains("value"));
            Assertions.assertTrue(appearanceStates.contains("Off"));
            Assertions.assertEquals("value", widget.getPdfObject().getAsName(PdfName.AS).getValue());

            acroForm.addField(checkBox);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void createMutuallyExclusiveCheckBoxesTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "createMutuallyExclusiveCheckBoxes.pdf";
        String cmpPdf = sourceFolder + "cmp_createMutuallyExclusiveCheckBoxes.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(doc, true);

            PdfButtonFormField checkBox = new CheckBoxFormFieldBuilder(doc, "checkbox")
                    .setWidgetRectangle(new Rectangle(10, 650, 40, 20)).createCheckBox();
            checkBox.addKid(new PdfWidgetAnnotation(new Rectangle(60, 650, 40, 20)));
            checkBox.addKid(new PdfWidgetAnnotation(new Rectangle(110, 650, 40, 20)));
            checkBox.setValue("3");
            checkBox.getFirstFormAnnotation().setCheckBoxAppearanceOnStateName("1");
            checkBox.getChildFormAnnotations().get(1).setCheckBoxAppearanceOnStateName("2");
            acroForm.addField(checkBox);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    @Test
    public void createNotMutuallyExclusiveCheckBoxTest() throws IOException, InterruptedException {
        String outPdf = destinationFolder + "createNotMutuallyExclusiveCheckBox.pdf";
        String cmpPdf = sourceFolder + "cmp_createNotMutuallyExclusiveCheckBox.pdf";
        try (PdfDocument doc = new PdfDocument(new PdfWriter(outPdf))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(doc, true);

            PdfButtonFormField checkBox = new CheckBoxFormFieldBuilder(doc, "checkbox")
                    .setWidgetRectangle(new Rectangle(10, 650, 40, 20)).createCheckBox();
            checkBox.setValue("1");
            checkBox.addKid(new PdfWidgetAnnotation(new Rectangle(60, 650, 40, 20)));
            Assertions.assertNull(checkBox.getWidgets().get(1).getNormalAppearanceObject());
            checkBox.setValue("2");
            acroForm.addField(checkBox);
        }
        Assertions.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, destinationFolder, "diff_"));
    }

    private void addCheckBox(PdfDocument pdfDoc, float fontSize, float yPos, float checkBoxW, float checkBoxH)
            throws IOException {
        Rectangle rect = new Rectangle(50, yPos, checkBoxW, checkBoxH);
        addCheckBox(pdfDoc, fontSize, yPos, checkBoxW, new CheckBoxFormFieldBuilder(pdfDoc,
                MessageFormatUtil.format("cb_fs_{0}_{1}_{2}", fontSize, checkBoxW, checkBoxH))
                .setWidgetRectangle(rect).createCheckBox()
                .setCheckType(CheckBoxType.CHECK).setValue("YES"));
    }

    private void addCheckBox(PdfDocument pdfDoc, float fontSize, float yPos, float checkBoxW, PdfFormField checkBox)
            throws IOException {
        PdfPage page = pdfDoc.getFirstPage();
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        if (fontSize >= 0) {
            checkBox.setFontSize(fontSize);
        }
        checkBox.getFirstFormAnnotation().setBorderWidth(1);
        checkBox.getFirstFormAnnotation().setBorderColor(ColorConstants.BLACK);

        form.addField(checkBox, page);

        PdfCanvas canvas = new PdfCanvas(page);
        canvas.saveState()
                .beginText()
                .moveText(50 + checkBoxW + 10, yPos)
                .setFontAndSize(PdfFontFactory.createFont(), 12)
                .showText("okay?")
                .endText()
                .restoreState();
    }
}
