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

import com.itextpdf.kernel.pdf.PdfAConformance;
import com.itextpdf.kernel.pdf.PdfConformance;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfUAConformance;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;

import java.io.ByteArrayOutputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class FormFieldBuilderTest extends ExtendedITextTest {
    private static final PdfDocument DUMMY_DOCUMENT = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
    private static final String DUMMY_NAME = "dummy name";

    @Test
    public void constructorTest() {
        TestBuilder builder = new TestBuilder(DUMMY_DOCUMENT, DUMMY_NAME);

        Assertions.assertSame(DUMMY_DOCUMENT, builder.getDocument());
        Assertions.assertSame(DUMMY_NAME, builder.getFormFieldName());
    }

    @Test
    public void getSetConformanceLevelTest() {
        TestBuilder builder = new TestBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        builder.setConformance(PdfConformance.PDF_A_1A);
        Assertions.assertSame(PdfAConformance.PDF_A_1A, builder.getConformance().getAConformance());
    }

    @Test
    public void getSetConformanceLevelPdfUATest() {
        TestBuilder builder = new TestBuilder(DUMMY_DOCUMENT, DUMMY_NAME);
        builder.setConformance(PdfConformance.PDF_UA_1);
        Assertions.assertSame(PdfUAConformance.PDF_UA_1, builder.getConformance().getUAConformance());
    }

    private static class TestBuilder extends FormFieldBuilder<TestBuilder> {

        protected TestBuilder(PdfDocument document, String formFieldName) {
            super(document, formFieldName);
        }

        @Override
        protected TestBuilder getThis() {
            return this;
        }
    }
}
