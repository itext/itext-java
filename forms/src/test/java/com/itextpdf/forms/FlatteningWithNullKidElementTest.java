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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import java.io.IOException;

@Tag("IntegrationTest")
public class FlatteningWithNullKidElementTest extends ExtendedITextTest {
    public static final String sourceFolder = "./src/test/resources/com/itextpdf/forms/FlatteningWithNullKidElementTest/";
    public static final String destinationFolder = "./target/test/com/itextpdf/forms/FlatteningWithNullKidElementTest/";

    @BeforeAll
    public static void beforeClass() {
        createDestinationFolder(destinationFolder);
    }


    @Test
    public void formFlatteningTestWithNullKidElement() throws IOException {
        String filename = "Form_NullKidElement";

        String src = sourceFolder + filename + ".pdf";
        String temp = destinationFolder + "temp.pdf";

        PdfDocument doc = new PdfDocument(new PdfReader(src), new PdfWriter(temp));

        boolean fail = false;
        try {
            PdfFormCreator.getAcroForm(doc, true).flattenFields();

            doc.close();
        }catch(Exception e){
            fail=true;
        }

        Assertions.assertFalse(fail);

    }

}
