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
import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.io.ByteArrayOutputStream;

@Tag("UnitTest")
public class TerminalFormFieldBuilderTest extends ExtendedITextTest {
    private static final String DUMMY_NAME = "dummy name";
    private static final Rectangle DUMMY_RECTANGLE = new Rectangle(7, 11, 13, 17);

    @Test
    public void constructorTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        TestBuilder builder = new TestBuilder(pdfDoc, DUMMY_NAME);

        Assertions.assertSame(pdfDoc, builder.getDocument());
        Assertions.assertSame(DUMMY_NAME, builder.getFormFieldName());
    }

    @Test
    public void getSetWidgetTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        TestBuilder builder = new TestBuilder(pdfDoc, DUMMY_NAME);
        builder.setWidgetRectangle(DUMMY_RECTANGLE);

        Assertions.assertSame(DUMMY_RECTANGLE, builder.getWidgetRectangle());
    }

    @Test
    public void getSetPageTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        TestBuilder builder = new TestBuilder(pdfDoc, DUMMY_NAME);
        PdfPage page = pdfDoc.addNewPage();
        builder.setPage(page);

        Assertions.assertEquals(1, builder.getPage());

        builder.setPage(5);

        Assertions.assertEquals(5, builder.getPage());
    }

    @Test
    public void setPageToFieldTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        TestBuilder builder = new TestBuilder(pdfDoc, DUMMY_NAME);
        builder.setPage(5);

        PdfFormAnnotation formFieldAnnot = new PdfFormAnnotation((PdfDictionary)new PdfDictionary().makeIndirect(pdfDoc)) {
            @Override
            public PdfFormAnnotation setPage(int pageNum) {
                Assertions.assertEquals(5, pageNum);
                return this;
            }
        };
        PdfFormField formField = PdfFormCreator.createFormField(pdfDoc).addKid(formFieldAnnot);
        builder.setPageToField(formField);
    }

    private static class TestBuilder extends TerminalFormFieldBuilder<TestBuilder> {

        protected TestBuilder(PdfDocument document, String formFieldName) {
            super(document, formFieldName);
        }

        @Override
        protected TestBuilder getThis() {
            return this;
        }
    }
}
