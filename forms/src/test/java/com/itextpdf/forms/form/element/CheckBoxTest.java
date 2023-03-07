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
package com.itextpdf.forms.form.element;

import com.itextpdf.forms.form.FormProperty;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.CompareTool;
import com.itextpdf.layout.Document;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;

import java.io.IOException;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(IntegrationTest.class)
public class CheckBoxTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/CheckBoxTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/CheckBoxTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }
    
    @Test
    public void basicCheckBoxTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicCheckBox.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicCheckBox.pdf";
        
        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            CheckBox formCheckbox = new CheckBox("form checkbox");
            formCheckbox.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            document.add(formCheckbox);
            
            CheckBox flattenCheckbox = new CheckBox("flatten checkbox");
            flattenCheckbox.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            document.add(flattenCheckbox);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
}
