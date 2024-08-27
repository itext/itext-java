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

import com.itextpdf.commons.datastructures.NullUnlimitedList;
import com.itextpdf.commons.datastructures.SimpleArrayList;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.di.pagetree.DefaultPageTreeListFactory;
import com.itextpdf.kernel.di.pagetree.IPageTreeListFactory;
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
    public void defaultImplementationIsExpectedInstance() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        pdfDoc.getCatalog().put(PdfName.Count, new PdfNumber(10));
        IPageTreeListFactory factory = pdfDoc.getDiContainer().getInstance(IPageTreeListFactory.class);
        Assertions.assertTrue(factory instanceof DefaultPageTreeListFactory);
    }


    @Test
    public void defaultImplementationWritingOnlyReturnArrayList() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        IPageTreeListFactory factory = pdfDoc.getDiContainer().getInstance(IPageTreeListFactory.class);
        Assertions.assertTrue(factory.<Object>createList(null) instanceof SimpleArrayList<?>);
    }

    @Test
    public void defaultImplementationReadingAndModifyingNullUnlimitedList() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        IPageTreeListFactory factory = pdfDoc.getDiContainer().getInstance(IPageTreeListFactory.class);
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Count, new PdfNumber(Integer.MAX_VALUE));
        Assertions.assertTrue(factory.<Object>createList(dict) instanceof NullUnlimitedList<?>);
    }


    @Test
    public void defaultImplementationReadingAndModifyingArrayList() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        IPageTreeListFactory factory = pdfDoc.getDiContainer().getInstance(IPageTreeListFactory.class);
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Count, new PdfNumber(10));
        Assertions.assertTrue(factory.<Object>createList(dict) instanceof SimpleArrayList<?>);
    }


    @Test
    public void defaultImplementationReadingAndModifyingArrayListNegative() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        IPageTreeListFactory factory = pdfDoc.getDiContainer().getInstance(IPageTreeListFactory.class);
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Count, new PdfNumber(-10));
        Assertions.assertTrue(factory.<Object>createList(dict) instanceof NullUnlimitedList<?>);
    }


    @Test
    public void defaultImplementationReadingAndModifyingArrayListNull() {
        PdfDocument pdfDoc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        IPageTreeListFactory factory = pdfDoc.getDiContainer().getInstance(IPageTreeListFactory.class);
        PdfDictionary dict = new PdfDictionary();
        dict.put(PdfName.Count, new PdfNull());
        Assertions.assertTrue(factory.<Object>createList(dict) instanceof NullUnlimitedList<?>);
    }
}
