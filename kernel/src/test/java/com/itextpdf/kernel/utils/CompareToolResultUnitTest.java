/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
package com.itextpdf.kernel.utils;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfIndirectReference;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.utils.objectpathitems.ObjectPath;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
// Android-Conversion-Skip-File (during Android conversion the class will be replaced by DeferredCompare)
public class CompareToolResultUnitTest extends ExtendedITextTest {

    @Test
    public void errorCountTest() {
        CompareToolResult result = new CompareToolResult(2);
        result.addError(new ObjectPath(), "error1");

        Assertions.assertEquals(2, result.getMessageLimit());
        Assertions.assertEquals(1, result.getDifferences().size());
        Assertions.assertEquals(1, result.getErrorCount());
    }

    @Test
    public void addErrorTest() {
        PdfDocument document = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        PdfIndirectReference ref1 = document.getCatalog().getDocument().createNextIndirectReference();
        PdfIndirectReference ref2 = document.getCatalog().getDocument().createNextIndirectReference();

        CompareToolResult result = new CompareToolResult(1);
        result.addError(new ObjectPath(ref1, ref1), "error1");
        result.addError(new ObjectPath(ref2, ref2), "error2");

        Assertions.assertEquals(1, result.getErrorCount());
        Assertions.assertTrue(result.isMessageLimitReached());
        Assertions.assertTrue(result.getDifferences().containsValue("error1"));
        Assertions.assertFalse(result.getDifferences().containsValue("error2"));
    }
}
