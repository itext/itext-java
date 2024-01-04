/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
    Authors: Apryse Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
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
