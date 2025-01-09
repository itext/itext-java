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
package com.itextpdf.nativeimage;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.CheckBoxFormFieldBuilder;
import com.itextpdf.forms.fields.PdfButtonFormField;
import com.itextpdf.forms.fields.merging.AlwaysThrowExceptionStrategy;
import com.itextpdf.forms.fields.merging.MergeFieldsStrategy;
import com.itextpdf.forms.fields.merging.OnDuplicateFormFieldNameStrategy;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class FormsTest {
    @Test
    void defaultStrategy() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        OnDuplicateFormFieldNameStrategy strategy = pdfDocument.getDiContainer()
                .getInstance(OnDuplicateFormFieldNameStrategy.class);
        Assertions.assertEquals(MergeFieldsStrategy.class, strategy.getClass());
    }

    @Test
    void alwaysThrowStrategy() {
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDocument, true, new AlwaysThrowExceptionStrategy());
        PdfButtonFormField field1 = new CheckBoxFormFieldBuilder(pdfDocument, "test").createCheckBox();
        form.addField(field1);
        PdfButtonFormField field2 = new CheckBoxFormFieldBuilder(pdfDocument, "test").createCheckBox();

        Exception exception = Assertions.assertThrows(PdfException.class, () -> form.addField(field2));
        Assertions.assertEquals("Field name test already exists in the form.", exception.getMessage());
    }
}
