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
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import java.util.List;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class SignatureFormFieldBuilderTest extends ExtendedITextTest {
    private static final String DUMMY_NAME = "dummy name";
    private static final Rectangle DUMMY_RECTANGLE = new Rectangle(7, 11, 13, 17);

    @Test
    public void constructorTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        SignatureFormFieldBuilder builder = new SignatureFormFieldBuilder(pdfDoc, DUMMY_NAME);

        Assertions.assertSame(pdfDoc, builder.getDocument());
        Assertions.assertSame(DUMMY_NAME, builder.getFormFieldName());
    }

    @Test
    public void createSignatureWithWidgetTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfSignatureFormField signatureFormField = new SignatureFormFieldBuilder(pdfDoc, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).createSignature();

        compareSignatures(signatureFormField, pdfDoc, true);
    }

    @Test
    public void createSignatureWithoutWidgetTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfSignatureFormField signatureFormField =
                new SignatureFormFieldBuilder(pdfDoc, DUMMY_NAME).createSignature();

        compareSignatures(signatureFormField, pdfDoc, false);
    }

    @Test
    public void createSignatureWithIncorrectNameTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        AssertUtil.doesNotThrow(() -> new SignatureFormFieldBuilder(pdfDoc, "incorrect.name")
                .setWidgetRectangle(DUMMY_RECTANGLE).createSignature());
    }

    @Test
    public void createSignatureWithConformanceLevelTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfSignatureFormField signatureFormField = new SignatureFormFieldBuilder(pdfDoc, DUMMY_NAME)
                .setWidgetRectangle(DUMMY_RECTANGLE).setConformance(PdfConformance.PDF_A_1A)
                .createSignature();

        compareSignatures(signatureFormField, pdfDoc, true);
    }

    private static void compareSignatures(PdfSignatureFormField signatureFormField,PdfDocument pdfDoc, boolean widgetExpected) {
        PdfDictionary expectedDictionary = new PdfDictionary();

        List<PdfWidgetAnnotation> widgets = signatureFormField.getWidgets();

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

        putIfAbsent(expectedDictionary, PdfName.FT, PdfName.Sig);
        putIfAbsent(expectedDictionary, PdfName.T, new PdfString(DUMMY_NAME));

        expectedDictionary.makeIndirect(pdfDoc);
        signatureFormField.makeIndirect(pdfDoc);
        Assertions.assertNull(
                new CompareTool().compareDictionariesStructure(expectedDictionary, signatureFormField.getPdfObject()));
    }

    private static void putIfAbsent(PdfDictionary dictionary, PdfName name, PdfObject value) {
        if (!dictionary.containsKey(name)) {
            dictionary.put(name, value);
        }
    }
}
