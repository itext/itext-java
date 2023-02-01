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
package com.itextpdf.forms;

import com.itextpdf.forms.logs.FormsLogMessageConstants;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class PdfAcroFormIntegrationTest extends ExtendedITextTest {

    public static final String SOURCE_FOLDER = "./src/test/resources/com/itextpdf/forms/PdfAcroFormIntegrationTest/";

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = FormsLogMessageConstants.CANNOT_CREATE_FORMFIELD)})
    public void orphandNamelessFormFieldTest() throws IOException {
        try (PdfDocument pdfDoc = new PdfDocument(new PdfReader(SOURCE_FOLDER + "orphanedFormField.pdf"))) {
            PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
            // 2 out of 3 have been gathered
            Assert.assertEquals(2, form.getDirectFormFields().size());
        }
    }
}
