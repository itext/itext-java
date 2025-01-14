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
package com.itextpdf.kernel.pdf.tagging;

import com.itextpdf.kernel.pdf.PdfDictionary;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfObject;
import com.itextpdf.test.ExtendedITextTest;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class PdfStructureAttributesTest extends ExtendedITextTest {

    @Test
    public void ownerIsNullTest() {
        PdfStructureAttributes pdfStructureAttributes = new PdfStructureAttributes(new PdfDictionary());
        Assertions.assertNull(pdfStructureAttributes.getPdfOwner());
    }

    @Test
    public void ownerIsNotNullTest() {
        Map<PdfName, PdfObject> map = new HashMap<>();
        map.put(PdfName.O, new PdfName("owner"));
        PdfStructureAttributes pdfStructureAttributes = new PdfStructureAttributes(new PdfDictionary(map));
        String pdfOwner = pdfStructureAttributes.getPdfOwner();
        Assertions.assertEquals("owner", pdfOwner);
    }

}
