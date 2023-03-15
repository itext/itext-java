/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
public class InputButtonTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/InputButtonTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/InputButtonTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicInputButtonTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicInputButton.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicInputButton.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            InputButton formInputButton = new InputButton("form input button");
            formInputButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formInputButton.setProperty(FormProperty.FORM_FIELD_VALUE, "form input button");
            document.add(formInputButton);

            InputButton flattenInputButton = new InputButton("flatten input button");
            flattenInputButton.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenInputButton.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten input button");
            document.add(flattenInputButton);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
}
