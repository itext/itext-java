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
package com.itextpdf.forms.fields;

import com.itextpdf.forms.exceptions.FormsExceptionMessageConstant;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfArray;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfNumber;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class ChoiceFormFieldBuilderTest extends ExtendedITextTest {
    private static final String DUMMY_NAME = "dummy name";
    private static final Rectangle DUMMY_RECTANGLE = new Rectangle(7, 11, 13, 17);
    private static final PdfArray DUMMY_OPTIONS = new PdfArray(Arrays.asList("option1", "option2", "option3"), false);

    @Test
    public void constructorTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        ChoiceFormFieldBuilder builder = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME);

        Assertions.assertSame(pdfDoc, builder.getDocument());
        Assertions.assertSame(DUMMY_NAME, builder.getFormFieldName());
    }

    @Test
    public void setGetOptionsAsPdfArrayTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        ChoiceFormFieldBuilder builder = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME);

        builder.setOptions(DUMMY_OPTIONS);

        Assertions.assertSame(DUMMY_OPTIONS, builder.getOptions());
    }

    @Test
    public void setGetOptionsAsStringArrayTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        ChoiceFormFieldBuilder builder = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME);

        String[] options = new String[]{"option1", "option2", "option3"};

        builder.setOptions(options);

        for (int i = 0; i < options.length; ++i) {
            Assertions.assertEquals(
                    new PdfString(options[i], PdfEncodings.UNICODE_BIG), builder.getOptions().getAsString(i));
        }
    }

    @Test
    public void setGetOptionsAsTwoDimensionalStringArrayTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        ChoiceFormFieldBuilder builder = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME);

        String[][] options = new String[][]{
                new String[]{"option1", "option2"}, new String[]{"option3", "option4"}};
        builder.setOptions(options);

        for (int i = 0; i < options.length; ++i) {
            for (int j = 0; j < options[i].length; ++j) {
                Assertions.assertEquals(
                        new PdfString(options[i][j], PdfEncodings.UNICODE_BIG),
                        builder.getOptions().getAsArray(i).getAsString(j));
            }
        }
    }

    @Test
    public void setGetOptionsAsIllegalTwoDimensionalStringArrayTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        ChoiceFormFieldBuilder builder = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME);

        String[][] options = new String[][]{new String[]{"option1", "option2", "option3"}};

        Exception exception = Assertions.assertThrows(IllegalArgumentException.class, () -> builder.setOptions(options));
        Assertions.assertEquals(FormsExceptionMessageConstant.INNER_ARRAY_SHALL_HAVE_TWO_ELEMENTS, exception.getMessage());
    }

    @Test
    public void createComboBoxWithWidgetTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfChoiceFormField choiceFormField = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).createComboBox();

        compareChoices(new PdfDictionary(), choiceFormField, pdfDoc, true);
    }

    @Test
    public void createComboBoxWithoutWidgetTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfChoiceFormField choiceFormField = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME).createComboBox();

        compareChoices(new PdfDictionary(), choiceFormField, pdfDoc, false);
    }

    @Test
    public void createComboBoxWithIncorrectNameTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        AssertUtil.doesNotThrow(() -> new ChoiceFormFieldBuilder(pdfDoc, "incorrect.name")
                .setWidgetRectangle(DUMMY_RECTANGLE).createComboBox());
    }

    @Test
    public void createComboBoxWithConformanceLevelTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfChoiceFormField choiceFormField = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).setConformance(PdfConformance.PDF_A_1A)
                .createComboBox();

        compareChoices(new PdfDictionary(), choiceFormField, pdfDoc, true);
    }

    @Test
    public void createComboBoxWithOptionsTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfChoiceFormField choiceFormField = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).setOptions(DUMMY_OPTIONS).createComboBox();

        PdfDictionary expectedDictionary = new PdfDictionary();
        expectedDictionary.put(PdfName.Opt, DUMMY_OPTIONS);

        compareChoices(expectedDictionary, choiceFormField, pdfDoc, true);
    }

    @Test
    public void createComboBoxWithoutOptionsTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfChoiceFormField choiceFormField = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).createComboBox();

        compareChoices(new PdfDictionary(), choiceFormField, pdfDoc, true);
    }

    @Test
    public void createListWithWidgetTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfChoiceFormField choiceFormField = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).createList();

        PdfDictionary expectedDictionary = new PdfDictionary();
        expectedDictionary.put(PdfName.Ff, new PdfNumber(0));

        compareChoices(expectedDictionary, choiceFormField, pdfDoc, true);
    }

    @Test
    public void createListWithoutWidgetTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfChoiceFormField choiceFormField = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME).createList();

        PdfDictionary expectedDictionary = new PdfDictionary();
        expectedDictionary.put(PdfName.Ff, new PdfNumber(0));

        compareChoices(expectedDictionary, choiceFormField, pdfDoc, false);
    }

    @Test
    public void createListWithIncorrectNameTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        AssertUtil.doesNotThrow(() -> new ChoiceFormFieldBuilder(pdfDoc, "incorrect.name")
                .setWidgetRectangle(DUMMY_RECTANGLE).createList());
    }

    @Test
    public void createListWithConformanceLevelTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfChoiceFormField choiceFormField = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE)
                .setConformance(PdfConformance.PDF_A_1A).createList();

        PdfDictionary expectedDictionary = new PdfDictionary();
        expectedDictionary.put(PdfName.Ff, new PdfNumber(0));

        compareChoices(expectedDictionary, choiceFormField, pdfDoc, true);
    }

    @Test
    public void createListWithOptionsTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfChoiceFormField choiceFormField = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).setOptions(DUMMY_OPTIONS).createList();

        PdfDictionary expectedDictionary = new PdfDictionary();
        expectedDictionary.put(PdfName.Ff, new PdfNumber(0));
        expectedDictionary.put(PdfName.Opt, DUMMY_OPTIONS);

        compareChoices(expectedDictionary, choiceFormField, pdfDoc, true);
    }

    @Test
    public void createListWithoutOptionsTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfChoiceFormField choiceFormField = new ChoiceFormFieldBuilder(pdfDoc, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).createList();

        PdfDictionary expectedDictionary = new PdfDictionary();
        expectedDictionary.put(PdfName.Ff, new PdfNumber(0));

        compareChoices(expectedDictionary, choiceFormField, pdfDoc, true);
    }

    private static void compareChoices(PdfDictionary expectedDictionary,
                                       PdfChoiceFormField choiceFormField, PdfDocument pdfDoc, boolean widgetExpected) {
        List<PdfWidgetAnnotation> widgets = choiceFormField.getWidgets();

        if (widgetExpected) {
            Assertions.assertEquals(1, widgets.size());

            PdfWidgetAnnotation annotation = widgets.get(0);

            Assertions.assertTrue(DUMMY_RECTANGLE.equalsWithEpsilon(annotation.getRectangle().toRectangle()));

            PdfArray kids = new PdfArray();
            kids.add(annotation.getPdfObject());
            putIfAbsent(expectedDictionary, PdfName.Kids, kids);
        } else {
            Assertions.assertEquals(0, widgets.size());
        }

        putIfAbsent(expectedDictionary, PdfName.FT, PdfName.Ch);
        putIfAbsent(expectedDictionary, PdfName.Ff, new PdfNumber(PdfChoiceFormField.FF_COMBO));
        putIfAbsent(expectedDictionary, PdfName.Opt, new PdfArray());
        putIfAbsent(expectedDictionary, PdfName.T, new PdfString(DUMMY_NAME));
        putIfAbsent(expectedDictionary, PdfName.V, new PdfArray());
        putIfAbsent(expectedDictionary, PdfName.DA, choiceFormField.getPdfObject().get(PdfName.DA));

        expectedDictionary.makeIndirect(pdfDoc);
        choiceFormField.makeIndirect(pdfDoc);
        Assertions.assertNull(
                new CompareTool().compareDictionariesStructure(expectedDictionary, choiceFormField.getPdfObject()));
    }

    private static void putIfAbsent(PdfDictionary dictionary, PdfName name, PdfObject value) {
        if (!dictionary.containsKey(name)) {
            dictionary.put(name, value);
        }
    }
}
