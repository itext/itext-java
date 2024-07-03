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
package com.itextpdf.kernel.pdf;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.pdf.PdfPagesTree.NullUnlimitedList;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PdfPagesTreeTest extends ExtendedITextTest {
    @Test
    public void generateTreeDocHasNoPagesTest() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        AssertUtil.doesNotThrow(() -> pdfDoc.close());
    }

    @Test
    public void nullUnlimitedListAddTest() {
        NullUnlimitedList<String> list = new NullUnlimitedList<>();
        list.add("hey");
        list.add("bye");
        Assertions.assertEquals(2, list.size());
        list.add(-1, "hello");
        list.add(3, "goodbye");
        Assertions.assertEquals(2, list.size());
    }

    @Test
    public void nullUnlimitedListIndexOfTest() {
        NullUnlimitedList<String> list = new NullUnlimitedList<>();
        list.add("hey");
        list.add(null);
        list.add("bye");
        list.add(null);
        Assertions.assertEquals(4, list.size());
        Assertions.assertEquals(1, list.indexOf(null));
    }

    @Test
    public void nullUnlimitedListRemoveTest() {
        NullUnlimitedList<String> list = new NullUnlimitedList<>();
        list.add("hey");
        list.add("bye");
        Assertions.assertEquals(2, list.size());
        list.remove(-1);
        list.remove(2);
        Assertions.assertEquals(2, list.size());
    }
}
