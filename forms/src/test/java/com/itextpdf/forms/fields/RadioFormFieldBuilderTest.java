/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
    Authors: iText Software.

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
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.ByteArrayOutputStream;
import java.util.List;

@Category(UnitTest.class)
public class RadioFormFieldBuilderTest extends ExtendedITextTest {

    private static final PdfDocument DUMMY_DOCUMENT = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
    private static final String DUMMY_NAME = "dummy name";
    private static final Rectangle DUMMY_RECTANGLE = new Rectangle(7, 11, 13, 17);
    private static final String DUMMY_APPEARANCE_NAME = "dummy appearance name";

    @Test
    public void oneParameterConstructorTest() {
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(DUMMY_DOCUMENT);

        Assert.assertSame(DUMMY_DOCUMENT, builder.getDocument());
        Assert.assertNull(builder.getFormFieldName());
    }

    @Test
    public void twoParametersConstructorTest() {
        RadioFormFieldBuilder builder = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);

        Assert.assertSame(DUMMY_DOCUMENT, builder.getDocument());
        Assert.assertSame(DUMMY_NAME, builder.getFormFieldName());
    }

    @Test
    public void createRadioGroupTest() {
        PdfButtonFormField radioGroup = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME).createRadioGroup();

        compareRadioGroups(radioGroup);
    }

    @Test
    public void createRadioButtonWithWidgetTest() {
        PdfButtonFormField radioGroup = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME).createRadioGroup();
        PdfFormField radioFormField = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).createRadioButton(radioGroup, DUMMY_APPEARANCE_NAME);

        compareRadioButtons(radioFormField, radioGroup, true);
    }

    @Test
    public void createRadioButtonWithoutWidgetTest() {
        PdfButtonFormField radioGroup = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME).createRadioGroup();
        PdfFormField radioFormField = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME)
                .createRadioButton(radioGroup, DUMMY_APPEARANCE_NAME);

        compareRadioButtons(radioFormField, radioGroup, false);
    }

    @Test
    public void createRadioButtonWithConformanceLevelTest() {
        PdfButtonFormField radioGroup = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME).createRadioGroup();
        PdfFormField radioFormField = new RadioFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).setConformanceLevel(PdfAConformanceLevel.PDF_A_1A)
                .createRadioButton(radioGroup, DUMMY_APPEARANCE_NAME);

        compareRadioButtons(radioFormField, radioGroup, true);
    }

    private static void compareRadioGroups(PdfButtonFormField radioGroupFormField) {
        PdfDictionary expectedDictionary = new PdfDictionary();

        putIfAbsent(expectedDictionary, PdfName.FT, PdfName.Btn);
        putIfAbsent(expectedDictionary, PdfName.Ff, new PdfNumber(PdfButtonFormField.FF_RADIO));
        putIfAbsent(expectedDictionary, PdfName.T, new PdfString(DUMMY_NAME));

        expectedDictionary.makeIndirect(DUMMY_DOCUMENT);
        radioGroupFormField.makeIndirect(DUMMY_DOCUMENT);
        Assert.assertNull(
                new CompareTool().compareDictionariesStructure(expectedDictionary, radioGroupFormField.getPdfObject()));
    }

    private static void compareRadioButtons(PdfFormField radioButtonFormField,
                                            PdfButtonFormField radioGroup, boolean widgetExpected) {
        PdfDictionary expectedDictionary = new PdfDictionary();

        List<PdfWidgetAnnotation> widgets = radioButtonFormField.getWidgets();

        if (widgetExpected) {
            Assert.assertEquals(1, widgets.size());

            PdfWidgetAnnotation annotation = widgets.get(0);

            Assert.assertTrue(DUMMY_RECTANGLE.equalsWithEpsilon(annotation.getRectangle().toRectangle()));

            PdfArray kids = new PdfArray();
            kids.add(annotation.getPdfObject());
            putIfAbsent(expectedDictionary, PdfName.Kids, kids);
        } else {
            Assert.assertEquals(0, widgets.size());
        }

        putIfAbsent(expectedDictionary, PdfName.Parent, radioGroup.getPdfObject());
        putIfAbsent(expectedDictionary, PdfName.FT, PdfName.Btn);

        expectedDictionary.makeIndirect(DUMMY_DOCUMENT);
        radioButtonFormField.makeIndirect(DUMMY_DOCUMENT);
        Assert.assertNull(new CompareTool().compareDictionariesStructure(
                expectedDictionary, radioButtonFormField.getPdfObject()));
    }

    private static void putIfAbsent(PdfDictionary dictionary, PdfName name, PdfObject value) {
        if (!dictionary.containsKey(name)) {
            dictionary.put(name, value);
        }
    }
}
