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
public class RadioTest extends ExtendedITextTest {
    public static final String SOURCE_FOLDER =
            "./src/test/resources/com/itextpdf/forms/form/element/RadioTest/";
    public static final String DESTINATION_FOLDER =
            "./target/test/com/itextpdf/forms/form/element/RadioTest/";

    @BeforeClass
    public static void beforeClass() {
        createOrClearDestinationFolder(DESTINATION_FOLDER);
    }

    @Test
    public void basicRadioTest() throws IOException, InterruptedException {
        String outPdf = DESTINATION_FOLDER + "basicRadio.pdf";
        String cmpPdf = SOURCE_FOLDER + "cmp_basicRadio.pdf";

        try (Document document = new Document(new PdfDocument(new PdfWriter(outPdf)))) {
            Radio formRadio1 = new Radio("form radio button 1");
            formRadio1.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            // TODO DEVSIX-7360 Form field value is used as group name which is a little bit counterintuitive, maybe we
            //  we can come up with something more obvious.
            formRadio1.setProperty(FormProperty.FORM_FIELD_VALUE, "form radio group");
            formRadio1.setProperty(FormProperty.FORM_FIELD_CHECKED, false);
            document.add(formRadio1);

            Radio formRadio2 = new Radio("form radio button 2");
            formRadio2.setProperty(FormProperty.FORM_FIELD_FLATTEN, false);
            formRadio2.setProperty(FormProperty.FORM_FIELD_VALUE, "form radio group");
            // TODO DEVSIX-7360 True doesn't work and considered as checked radio button, it shouldn't be that way.
            formRadio2.setProperty(FormProperty.FORM_FIELD_CHECKED, null);
            document.add(formRadio2);

            Radio flattenRadio1 = new Radio("flatten radio button 1");
            flattenRadio1.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenRadio1.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten radio group");
            flattenRadio1.setProperty(FormProperty.FORM_FIELD_CHECKED, false);
            document.add(flattenRadio1);

            Radio flattenRadio2 = new Radio("flatten radio button 2");
            flattenRadio2.setProperty(FormProperty.FORM_FIELD_FLATTEN, true);
            flattenRadio2.setProperty(FormProperty.FORM_FIELD_VALUE, "flatten radio group");
            // TODO DEVSIX-7360 True doesn't work and considered as checked radio button, it shouldn't be that way.
            flattenRadio2.setProperty(FormProperty.FORM_FIELD_CHECKED, null);
            document.add(flattenRadio2);
        }

        Assert.assertNull(new CompareTool().compareByContent(outPdf, cmpPdf, DESTINATION_FOLDER));
    }
}
