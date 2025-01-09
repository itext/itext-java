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
package com.itextpdf.nativeimage;

import com.itextpdf.commons.datastructures.ISimpleList;
import com.itextpdf.commons.datastructures.SimpleArrayList;
import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.kernel.di.pagetree.IPageTreeListFactory;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;

import com.itextpdf.kernel.pdf.PdfWriter;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

class KernelTest {
    @Test
    void staticPdfNames() {
        Assertions.assertTrue(PdfName.staticNames.size() > 800);
    }

    @Test
    void testDefaultPagesFactory(){
        PdfDocument pdfDocument = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        IPageTreeListFactory factory = pdfDocument.getDiContainer().getInstance(IPageTreeListFactory.class);
        ISimpleList<String> f = factory.createList(null);
        Assertions.assertInstanceOf(SimpleArrayList.class, f);
    }
}
