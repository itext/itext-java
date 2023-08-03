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
package com.itextpdf.kernel.utils.objectpathitems;

import com.itextpdf.io.source.ByteArrayOutputStream;
import com.itextpdf.io.util.XmlUtil;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.w3c.dom.Document;

import java.util.Stack;

@Category(IntegrationTest.class)
public class TrailerPathTest extends ExtendedITextTest {

    @Test
    public void equalsAndHashCodeTest() {
        PdfDocument src = createDocument();
        PdfDocument dest = createDocument();

        Stack<LocalPathItem> stack = new Stack<LocalPathItem>();
        stack.push(new ArrayPathItem(1));
        TrailerPath path1 = new TrailerPath(src, dest, stack);
        TrailerPath path2 = new TrailerPath(src, dest, stack);

        boolean result = path1.equals(path2);
        Assert.assertTrue(result);
        Assert.assertEquals(path1.hashCode(), path2.hashCode());
    }

    @Test
    public void notEqualsAndHashCodeTest() {
        PdfDocument src = createDocument();
        PdfDocument dest = createDocument();

        Stack<LocalPathItem> stack = new Stack<LocalPathItem>();
        stack.push(new ArrayPathItem(1));
        TrailerPath path1 = new TrailerPath(src, dest, stack);

        stack = new Stack<LocalPathItem>();
        TrailerPath path2 = new TrailerPath(src, dest, stack);

        boolean result = path1.equals(path2);
        Assert.assertFalse(result);
        Assert.assertNotEquals(path1.hashCode(), path2.hashCode());
    }

    @Test
    public void cloneConstructorTest() {
        PdfDocument src = createDocument();
        PdfDocument dest = createDocument();

        Stack<LocalPathItem> stack = new Stack<LocalPathItem>();
        stack.push(new ArrayPathItem(1));
        TrailerPath path1 = new TrailerPath(src, dest, stack);

        TrailerPath path2 = new TrailerPath(path1);

        boolean result = path1.equals(path2);
        Assert.assertTrue(result);
        Assert.assertEquals(path1.hashCode(), path2.hashCode());
    }

    @Test
    public void toStringTest() {
        PdfDocument src = createDocument();
        PdfDocument dest = createDocument();

        Stack<LocalPathItem> stack = new Stack<LocalPathItem>();
        stack.push(new ArrayPathItem(1));
        TrailerPath path1 = new TrailerPath(src, dest, stack);

        Assert.assertEquals("Base cmp object: trailer. Base out object: trailer\nArray index: 1",
                path1.toString());
    }

    @Test
    public void getPdfDocumentsTest() {
        PdfDocument cmp = createDocument();
        PdfDocument out = createDocument();
        Stack<LocalPathItem> stack = new Stack<LocalPathItem>();

        TrailerPath path = new TrailerPath(cmp, out, stack);

        Assert.assertEquals(cmp, path.getCmpDocument());
        Assert.assertEquals(out, path.getOutDocument());
    }

    @Test
    public void toXmlNodeTest() throws Exception {
        PdfDocument src = createDocument();
        PdfDocument dest = createDocument();

        Stack<LocalPathItem> stack = new Stack<LocalPathItem>();
        stack.push(new ArrayPathItem(1));
        TrailerPath path1 = new TrailerPath(src, dest, stack);
        Document doc = XmlUtil.initNewXmlDocument();

        Assert.assertNotNull(path1.toXmlNode(doc));
    }

    private static PdfDocument createDocument() {
        PdfDocument doc = new PdfDocument(new PdfWriter(new ByteArrayOutputStream()));
        doc.addNewPage();
        doc.close();

        return doc;
    }
}
