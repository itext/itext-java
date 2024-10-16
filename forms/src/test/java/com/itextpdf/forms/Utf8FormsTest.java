/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.forms.fields.TextFormFieldBuilder;
import com.itextpdf.io.font.PdfEncodings;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.util.Map;

@Tag("UnitTest")
public class Utf8FormsTest extends ExtendedITextTest {

    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/Utf8FormsTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/Utf8FormsTest/";
    public static final String FONT = "./src/test/resources/com/itextpdf/forms/Utf8FormsTest/NotoSansCJKsc-Regular.otf";

    @BeforeEach
    public void before() {
        createDestinationFolder(destinationFolder);
    }

    @Test
    public void readUtf8FieldName() throws java.io.IOException {
        String filename = sourceFolder + "utf-8-field-name.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        Map<String,PdfFormField> fields = form.getAllFormFields();
        pdfDoc.close();
        for (String fldName : fields.keySet()) {
            //  لا
            Assertions.assertEquals("\u0644\u0627", fldName);
        }
        pdfDoc.close();
    }

    @Test
    public void readUtf8TextAnnot() throws java.io.IOException {
        String filename = sourceFolder + "utf-8-text-annot.pdf";
        PdfDocument pdfDoc = new PdfDocument(new PdfReader(filename));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        Map<String,PdfFormField> fields = form.getAllFormFields();
        pdfDoc.close();
        for (String fldName : fields.keySet()) {
            //  福昕 福昕UTF8
            Assertions.assertEquals("\u798F\u6615 \u798F\u6615UTF8", fields.get(fldName).getValueAsString());
        }
    }

    @Test
    //TODO DEVSIX-2798
    public void writeUtf8FieldNameAndValue() throws java.io.IOException, InterruptedException {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(destinationFolder + "writeUtf8FieldNameAndValue.pdf"));
        PdfAcroForm form = PdfFormCreator.getAcroForm(pdfDoc, true);
        PdfTextFormField field = new TextFormFieldBuilder(pdfDoc, "")
                .setWidgetRectangle(new Rectangle(99, 753, 425, 15)).createText();
        field.setValue("");
        field.setFont(PdfFontFactory.createFont(FONT, PdfEncodings.IDENTITY_H));
        //  لا
        field.put(PdfName.T, new PdfString("\u0644\u0627", PdfEncodings.UTF8));
        //  福昕 福昕UTF8
        field.put(PdfName.V, new PdfString("\u798F\u6615 \u798F\u6615UTF8", PdfEncodings.UTF8));
        field.regenerateField();
        form.addField(field);
        pdfDoc.close();
        Assertions.assertNull(new CompareTool().compareByContent(destinationFolder + "writeUtf8FieldNameAndValue.pdf", sourceFolder + "cmp_writeUtf8FieldNameAndValue.pdf", destinationFolder, "diffFieldNameAndValue_"));
    }
}
