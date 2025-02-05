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
package com.itextpdf.forms;

import com.itextpdf.forms.fields.PdfFormCreator;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;

import java.io.IOException;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class PdfFormFieldsHierarchyTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/PdfFormFieldsHierarchyTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/PdfFormFieldsHierarchyTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void fillingFormWithKidsTest() throws IOException, InterruptedException {
        String srcPdf = sourceFolder + "formWithKids.pdf";
        String cmpPdf = sourceFolder + "cmp_fillingFormWithKidsTest.pdf";
        String outPdf = destinationFolder + "fillingFormWithKidsTest.pdf";

        PdfDocument pdfDocument = new PdfDocument(new PdfReader(srcPdf), new PdfWriter(outPdf));

        PdfAcroForm acroForm = PdfFormCreator.getAcroForm(pdfDocument, false);

        Map<String, PdfFormField> formFields = acroForm.getAllFormFields();

        for (String key : formFields.keySet()) {
            PdfFormField field = acroForm.getField(key);
            field.setValue(key);
        }

        pdfDocument.close();

        CompareTool compareTool = new CompareTool();
        String errorMessage = compareTool.compareByContent(outPdf, cmpPdf, destinationFolder);
        if (errorMessage != null) {
            Assertions.fail(errorMessage);
        }
    }

    @Test
    public void autosizeInheritedDAFormFieldsTest() throws IOException, InterruptedException {
        String inPdf = destinationFolder + "autosizeInheritedDAFormFields.pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + "autosizeInheritedDAFormFields.pdf"),
                new PdfWriter(inPdf));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        Map<String, PdfFormField> fields = form.getAllFormFields();

        fields.get("field_1").setValue("1111 2222 3333 4444");
        fields.get("field_2").setValue("1111 2222 3333 4444");
        fields.get("field_3").setValue("surname surname surname surname surname surname");

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(inPdf,
                sourceFolder + "cmp_autosizeInheritedDAFormFields.pdf", destinationFolder, "diff_"));
    }

    @Test
    public void autosizeInheritedDAFormFieldsWithKidsTest() throws IOException, InterruptedException {
        String inPdf = destinationFolder + "autosizeInheritedDAFormFieldsWithKids.pdf";

        PdfDocument pdfDoc = new PdfDocument(
                new PdfReader(sourceFolder + "autosizeInheritedDAFormFieldsWithKids.pdf"),
                new PdfWriter(inPdf));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);

        form.getField("root.child.text1").setValue("surname surname surname surname surname");
        form.getField("root.child.text2").setValue("surname surname surname surname surname");

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(inPdf,
                sourceFolder + "cmp_autosizeInheritedDAFormFieldsWithKids.pdf", destinationFolder, inPdf));
    }

    @Test
    public void alignmentInheritanceInFieldsTest() throws IOException, InterruptedException {
        String name = "alignmentInheritanceInFields";
        String fileName = destinationFolder + name + ".pdf";

        PdfDocument pdfDoc = new PdfDocument(new PdfReader(sourceFolder + name + ".pdf"),
                new PdfWriter(fileName));

        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        form.setGenerateAppearance(false);

        Map<String, PdfFormField> fields = form.getAllFormFields();
        fields.get("root").setValue("Deutschland");

        form.flattenFields();

        pdfDoc.close();

        Assertions.assertNull(new CompareTool().compareByContent(fileName, sourceFolder + "cmp_" + name + ".pdf",
                destinationFolder + name, "diff_"));
    }
}
