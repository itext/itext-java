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

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.colors.ColorConstants;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.annot.PdfWidgetAnnotation;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfFormCreatorTest extends ExtendedITextTest {
    @Test
    public void getAcroFormTest() {
        PdfFormFactory customFactory = new PdfFormFactory() {
            @Override
            public PdfAcroForm getAcroForm(PdfDocument document, boolean createIfNotExist) {
                // Never create new acroform.
                return super.getAcroForm(document, false);
            }
        };
        
        PdfFormCreator.setFactory(customFactory);
        
        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfAcroForm acroForm = PdfFormCreator.getAcroForm(document, true);
            Assertions.assertNull(acroForm);
        } finally {
            PdfFormCreator.setFactory(new PdfFormFactory());
        }
    }

    @Test
    public void createTextFormFieldTest() {
        PdfFormFactory customFactory = new PdfFormFactory() {
            @Override
            public PdfTextFormField createTextFormField(PdfWidgetAnnotation widgetAnnotation, PdfDocument document) {
                PdfTextFormField formField = super.createTextFormField(widgetAnnotation, document);
                // All text is read by default.
                formField.setColor(ColorConstants.RED);
                return formField;
            }
        };

        PdfFormCreator.setFactory(customFactory);

        try (PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()))) {
            PdfFormField text =
                    new TextFormFieldBuilder(document, "name").setWidgetRectangle(new Rectangle(100, 100)).createText();
            Assertions.assertEquals(ColorConstants.RED, text.getColor());
        } finally {
            PdfFormCreator.setFactory(new PdfFormFactory());
        }
    }
}
