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
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class TextFormFieldBuilderTest extends ExtendedITextTest {
    private static final PdfDocument DUMMY_DOCUMENT = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
    private static final String DUMMY_NAME = "dummy name";
    private static final Rectangle DUMMY_RECTANGLE = new Rectangle(7, 11, 13, 17);

    @Test
    public void constructorTest() {
        TextFormFieldBuilder builder = new TextFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME);

        Assertions.assertSame(DUMMY_DOCUMENT, builder.getDocument());
        Assertions.assertSame(DUMMY_NAME, builder.getFormFieldName());
    }

    @Test
    public void createTextWithWidgetTest() {
        PdfTextFormField textFormField = new TextFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).createText();

        PdfDictionary expectedDictionary = new PdfDictionary();
        expectedDictionary.put(PdfName.Ff, new PdfNumber(0));

        compareTexts(expectedDictionary, textFormField, true);
    }

    @Test
    public void createTextWithoutWidgetTest() {
        PdfTextFormField textFormField = new TextFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME).createText();

        PdfDictionary expectedDictionary = new PdfDictionary();
        expectedDictionary.put(PdfName.Ff, new PdfNumber(0));

        compareTexts(expectedDictionary, textFormField, false);
    }

    @Test
    public void createTextWithConformanceLevelTest() {
        PdfTextFormField textFormField = new TextFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).setConformance(PdfConformance.PDF_A_1A).createText();

        PdfDictionary expectedDictionary = new PdfDictionary();
        expectedDictionary.put(PdfName.Ff, new PdfNumber(0));

        compareTexts(expectedDictionary, textFormField, true);
    }

    @Test
    public void createMultilineTextWithWidgetTest() {
        PdfTextFormField textFormField = new TextFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).createMultilineText();

        PdfDictionary expectedDictionary = new PdfDictionary();
        expectedDictionary.put(PdfName.Ff, new PdfNumber(PdfTextFormField.FF_MULTILINE));

        compareTexts(expectedDictionary, textFormField, true);
    }

    @Test
    public void createMultilineTextWithoutWidgetTest() {
        PdfTextFormField textFormField = new TextFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME).createMultilineText();

        PdfDictionary expectedDictionary = new PdfDictionary();
        expectedDictionary.put(PdfName.Ff, new PdfNumber(PdfTextFormField.FF_MULTILINE));

        compareTexts(expectedDictionary, textFormField, false);
    }

    @Test
    public void createMultilineTextWithConformanceLevelTest() {
        PdfTextFormField textFormField = new TextFormFieldBuilder(DUMMY_DOCUMENT, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).setConformance(PdfConformance.PDF_A_1A)
                .createMultilineText();

        PdfDictionary expectedDictionary = new PdfDictionary();
        expectedDictionary.put(PdfName.Ff, new PdfNumber(PdfTextFormField.FF_MULTILINE));

        compareTexts(expectedDictionary, textFormField, true);
    }

    private static void compareTexts(PdfDictionary expectedDictionary,
                                     PdfTextFormField textFormField, boolean widgetExpected) {
        List<PdfWidgetAnnotation> widgets = textFormField.getWidgets();

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

        putIfAbsent(expectedDictionary, PdfName.FT, PdfName.Tx);
        putIfAbsent(expectedDictionary, PdfName.T, new PdfString(DUMMY_NAME));
        putIfAbsent(expectedDictionary, PdfName.V, new PdfString(""));
        putIfAbsent(expectedDictionary, PdfName.DA, new PdfString("/F1 12 Tf"));

        expectedDictionary.makeIndirect(DUMMY_DOCUMENT);
        textFormField.makeIndirect(DUMMY_DOCUMENT);
        Assertions.assertNull(
                new CompareTool().compareDictionariesStructure(expectedDictionary, textFormField.getPdfObject()));
    }

    private static void putIfAbsent(PdfDictionary dictionary, PdfName name, PdfObject value) {
        if (!dictionary.containsKey(name)) {
            dictionary.put(name, value);
        }
    }
}
