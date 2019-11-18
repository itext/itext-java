/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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

import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import java.util.Map;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;


@Category(IntegrationTest.class)
public class PdfFormFieldsHierarchyTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfFormFieldsHierarchyTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfFormFieldsHierarchyTest/";

    @BeforeClass
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void fillingFormWithKidsTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "formWithKids.pdf";
        String cmpPdf = sourceFolder + "cmp_fillingFormWithKidsTest.pdf";
        String outPdf = destinationFolder + "fillingFormWithKidsTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));

        PdfAcroForm acroForm = PdfAcroForm.getAcroForm(pdfDocument, false);

        Map<String, PdfFormField> formFields = acroForm.getFormFields();

        for (String key : formFields.keySet()) {
            PdfFormField field = acroForm.getField(key);
            field.setValue(key);
        }

        pdfDocument.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder);
        if (errorMessage != null) {
            Assert.fail(errorMessage);
        }
    }
}