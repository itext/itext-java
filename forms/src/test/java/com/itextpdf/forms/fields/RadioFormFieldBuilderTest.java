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
package com.itextpdf.forms.fields;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfAnnotation;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class RadioFormFieldBuilderTest extends ExtendedITextTest {

    private static final PdfDocument DUMMY_DOCUMENT = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
    private static final String DUMMY_NAME = "dummy name";
    private static final Rectangle DUMMY_RECTANGLE = new Rectangle(7, 11, 13, 17);
    private static final String DUMMY_APPEARANCE_NAME = "dummy appearance name";
    private static final String DUMMY_APPEARANCE_NAME2 = "dummy appearance name 2";


    @Test
    public void twoParametersConstructorTest() {
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        Assertions.assertSame(DUMMY_DOCUMENT, builder.getDocument());
        Assertions.assertSame(DUMMY_NAME, builder.getFormFieldName());
    }

    @Test
    public void createRadioGroupTest() {
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        PdfButtonFormField radioGroup = builder.createRadioGroup();
        compareRadioGroups(radioGroup);
    }

    @Test
    public void createRadioButtonWithWidgetTest() {
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        PdfButtonFormField radioGroup = builder.createRadioGroup();
        PdfFormAnnotation radioAnnotation = builder
                .createRadioButton(DUMMY_APPEARANCE_NAME, DUMMY_RECTANGLE);
        compareRadioButtons(radioAnnotation, radioGroup, false);
    }

    @Test
    public void createRadioButtonWithWidgetUseSetWidgetRectangleTest() {
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        PdfButtonFormField radioGroup = builder.createRadioGroup();
        PdfFormAnnotation radioAnnotation = builder.setWidgetRectangle(DUMMY_RECTANGLE)
                .createRadioButton(DUMMY_APPEARANCE_NAME, null );
        compareRadioButtons(radioAnnotation, radioGroup, false);
    }

    @Test
    public void createRadioButtonWithEmptyAppearanceNameThrowsExceptionTest() {
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        Assertions.assertThrows(PdfException.class, () -> {
            builder.createRadioButton(null, DUMMY_RECTANGLE);
        });
        Assertions.assertThrows(PdfException.class, () -> {
            builder.createRadioButton("", DUMMY_RECTANGLE);
        });

    }

    @Test
    public void createRadioButtonWithWidgetAddedToRadioGroupTest() {
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        PdfButtonFormField radioGroup = builder.createRadioGroup();
        PdfFormAnnotation radioAnnotation = builder
                .createRadioButton(DUMMY_APPEARANCE_NAME, DUMMY_RECTANGLE);
        radioGroup.addKid(radioAnnotation);
        compareRadioButtons(radioAnnotation, radioGroup, true);
    }

    @Test
    public void create2RadioButtonWithWidgetAddedToRadioGroupTest() {
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        PdfButtonFormField radioGroup = builder.createRadioGroup();
        PdfFormAnnotation radioAnnotation = builder
                .createRadioButton(DUMMY_APPEARANCE_NAME, DUMMY_RECTANGLE);

        PdfFormAnnotation radioAnnotation2 = builder
                .createRadioButton(DUMMY_APPEARANCE_NAME, DUMMY_RECTANGLE);

        radioGroup.addKid(radioAnnotation);
        radioGroup.addKid(radioAnnotation2);

        compareRadioButtons(radioAnnotation, radioGroup, true);
        compareRadioButtons(radioAnnotation2, radioGroup, true);
        Assertions.assertEquals(2, radioGroup.getWidgets().size());
    }


    @Test
    public void create2RadioButtonWithWidgetAddedToRadioGroupOneSelectedTest() {
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        PdfButtonFormField radioGroup = builder.createRadioGroup();
        PdfFormAnnotation radioAnnotation = builder
                .createRadioButton(DUMMY_APPEARANCE_NAME, DUMMY_RECTANGLE);

        PdfFormAnnotation radioAnnotation2 = builder
                .createRadioButton(DUMMY_APPEARANCE_NAME2, DUMMY_RECTANGLE);

        radioGroup.setValue(DUMMY_APPEARANCE_NAME);
        radioGroup.addKid(radioAnnotation);
        radioGroup.addKid(radioAnnotation2);

        Assertions.assertEquals(PdfFormAnnotation.OFF_STATE_VALUE, radioAnnotation2.getAppearanceStates()[0]);
        Assertions.assertEquals(DUMMY_APPEARANCE_NAME,  radioAnnotation.getPdfObject().getAsName(PdfName.AS).getValue());
        compareRadioButtons(radioAnnotation, radioGroup, true);
        compareRadioButtons(radioAnnotation2, radioGroup, true);
        Assertions.assertEquals(2, radioGroup.getWidgets().size());
    }

    @Test
    public void createRadioButtonWithoutWidgetTest() {
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        PdfButtonFormField radioGroup = builder.createRadioGroup();
        PdfFormAnnotation radioAnnotation = builder
                .createRadioButton(DUMMY_APPEARANCE_NAME, DUMMY_RECTANGLE);

        compareRadioButtons(radioAnnotation, radioGroup, false);
    }

    @Test
    public void createRadioButtonWithoutWidgetThrowsExceptionTest() {
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        Assertions.assertThrows(PdfException.class,()->{
            PdfFormAnnotation radioAnnotation = builder
                    .createRadioButton(DUMMY_APPEARANCE_NAME, null);
        });
    }

    @Test
    public void createRadioButtonWithConformanceLevelTest() {
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        PdfButtonFormField radioGroup = builder.createRadioGroup();
        PdfFormAnnotation radioAnnotation = builder
                .setGenericConformanceLevel(PdfAConformanceLevel.PDF_A_1A)
                .createRadioButton(DUMMY_APPEARANCE_NAME, DUMMY_RECTANGLE);
        compareRadioButtons(radioAnnotation, radioGroup, false);
    }


    @Test
    public void createRadioButtonWithConformanceLevelAddedToGroupTest() {
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        PdfButtonFormField radioGroup = builder.createRadioGroup();
        PdfFormAnnotation radioAnnotation = builder
                .setGenericConformanceLevel(PdfAConformanceLevel.PDF_A_1A)
                .createRadioButton(DUMMY_APPEARANCE_NAME, DUMMY_RECTANGLE);
        radioGroup.addKid(radioAnnotation);
        compareRadioButtons(radioAnnotation, radioGroup, true);
    }

    private static void compareRadioGroups(PdfButtonFormField radioGroupFormField) {
        PdfDictionary expectedDictionary = new PdfDictionary();

        putIfAbsent(expectedDictionary, PdfName.FT, PdfName.Btn);
        putIfAbsent(expectedDictionary, PdfName.Ff, new PdfNumber(PdfButtonFormField.FF_RADIO));
        putIfAbsent(expectedDictionary, PdfName.T, new PdfString(DUMMY_NAME));

        expectedDictionary.makeIndirect(DUMMY_DOCUMENT);
        radioGroupFormField.makeIndirect(DUMMY_DOCUMENT);
        Assertions.assertNull(
                new CompareTool().compareDictionariesStructure(expectedDictionary, radioGroupFormField.getPdfObject()));
    }

    @Test
    public void createRadioButtonShouldNotContainTerminalFieldKeys() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(document, true);
            PdfButtonFormField radioGroup = new RadioFormFieldBuilder(document, DUMMY_NAME).createRadioGroup();
            PdfFormAnnotation radioAnnotation = new RadioFormFieldBuilder(document, DUMMY_NAME)

                    .createRadioButton(DUMMY_APPEARANCE_NAME, DUMMY_RECTANGLE);
            form.addField(radioGroup);
            Assertions.assertTrue(PdfFormAnnotationUtil.isPureWidget(radioAnnotation.getPdfObject()));
        }
    }

    @Test
    public void createRadioButtonButDontAddToGroupGroupContainsNoRadioButton() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(document, true);
            PdfButtonFormField radioGroup = new RadioFormFieldBuilder(document, DUMMY_NAME).createRadioGroup();
            new RadioFormFieldBuilder(document, DUMMY_NAME)

                    .createRadioButton(DUMMY_APPEARANCE_NAME, DUMMY_RECTANGLE);
            form.addField(radioGroup);
            Assertions.assertNull(radioGroup.getPdfObject().get(PdfName.Kids));
            Assertions.assertEquals(0, radioGroup.getWidgets().size());
        }
    }


    @Test
    public void createRadioButtonAddToGroupGroupContainsOneRadioButton() {
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfAcroForm form = PdfFormCreator.getAcroForm(document, true);
            PdfButtonFormField radioGroup = new RadioFormFieldBuilder(document, DUMMY_NAME).createRadioGroup();
            PdfFormAnnotation radioAnnotation = new RadioFormFieldBuilder(document, DUMMY_NAME)

                    .createRadioButton(DUMMY_APPEARANCE_NAME, DUMMY_RECTANGLE);
            radioGroup.addKid(radioAnnotation);
            form.addField(radioGroup);
            //In the previous implementation the radio buttons got added as kids, we want to avoid this
            Assertions.assertNull(radioGroup.getKids());
            // It should now contain one single radio button
            Assertions.assertEquals(1, radioGroup.getWidgets().size());
        }
    }

    private static void compareRadioButtons(PdfFormAnnotation radioButtonFormField,
            PdfButtonFormField radioGroup, boolean isAddedToRadioGroup) {
        PdfDictionary expectedDictionary = new PdfDictionary();

        List<PdfWidgetAnnotation> widgets = new ArrayList<>();
        if (radioButtonFormField.getWidget() != null) {
            widgets.add(radioButtonFormField.getWidget());
        }

        // if a rectangle is assigned in the builder than we should check it
        if (radioButtonFormField.getWidget().getRectangle() != null
                && radioButtonFormField.getWidget().getRectangle().toRectangle() != null) {
            Assertions.assertEquals(1, widgets.size());
            PdfWidgetAnnotation annotation = widgets.get(0);
            Assertions.assertTrue(DUMMY_RECTANGLE.equalsWithEpsilon(annotation.getRectangle().toRectangle()));
            putIfAbsent(expectedDictionary, PdfName.Rect, new PdfArray(DUMMY_RECTANGLE));

            // if the radiobutton has been added to the radiogroup we expect the AP to be generated
            if (isAddedToRadioGroup) {
                putIfAbsent(expectedDictionary, PdfName.AP,
                        radioButtonFormField.getPdfObject().getAsDictionary(PdfName.AP));
            }
        }
        if (radioButtonFormField.pdfConformanceLevel != null) {
            putIfAbsent(expectedDictionary, PdfName.F,
                    new PdfNumber(PdfAnnotation.PRINT));
        }
        // for the AS key if it's added to the group we expect it to be off or the value if the radiogroup was selected
        // if its was not added we expect it to be the value
        if (isAddedToRadioGroup) {
            PdfName expectedAS = new PdfName(PdfFormAnnotation.OFF_STATE_VALUE);
            PdfName radioGroupValue = radioGroup.getPdfObject().getAsName(PdfName.V);
            if (radioGroupValue != null && radioGroupValue.equals(
                    radioButtonFormField.getPdfObject().get(PdfName.AS))) {
                expectedAS = new PdfName(DUMMY_APPEARANCE_NAME);
            }
            putIfAbsent(expectedDictionary, PdfName.AS, expectedAS);
            putIfAbsent(expectedDictionary, PdfName.Parent, radioGroup.getPdfObject());
        } else {
            putIfAbsent(expectedDictionary, PdfName.AS, new PdfName(DUMMY_APPEARANCE_NAME));
        }
        putIfAbsent(expectedDictionary, PdfName.Subtype, PdfName.Widget);

        expectedDictionary.makeIndirect(DUMMY_DOCUMENT);
        radioButtonFormField.makeIndirect(DUMMY_DOCUMENT);

        Assertions.assertNull(new CompareTool().compareDictionariesStructure(
                expectedDictionary, radioButtonFormField.getPdfObject()));
    }

    private static void putIfAbsent(PdfDictionary dictionary, PdfName name, PdfObject value) {
        if (!dictionary.containsKey(name)) {
            dictionary.put(name, value);
        }
    }
}
