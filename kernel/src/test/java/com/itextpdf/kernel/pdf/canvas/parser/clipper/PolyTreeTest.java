/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.kernel.pdf.canvas.parser.clipper;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PolyTreeTest extends ExtendedITextTest {

    @Test
    public void clearPolyTreeTest() {
        PolyTree tree = new PolyTree();
        tree.addChild(new PolyNode());

        List<PolyNode> allPolys = tree.getAllPolys();
        allPolys.add(new PolyNode());

        Assert.assertFalse(allPolys.isEmpty());
        Assert.assertEquals(1, tree.getChildCount());

        tree.Clear();

        Assert.assertTrue(allPolys.isEmpty());
        Assert.assertEquals(0, tree.getChildCount());
    }

    @Test
    public void getFistChildInPolyTreeTest() {
        PolyTree tree = new PolyTree();

        PolyNode firstChild = new PolyNode();
        PolyNode secondChild = new PolyNode();

        tree.addChild(firstChild);
        tree.addChild(secondChild);

        Assert.assertSame(firstChild, tree.getFirst());
    }

    @Test
    public void getFistChildInEmptyPolyTreeTest() {
        PolyTree tree = new PolyTree();

        Assert.assertNull(tree.getFirst());
    }

    @Test
    public void getTotalSizePolyTreeEmptyTest() {
        PolyTree tree = new PolyTree();

        Assert.assertEquals(0, tree.getTotalSize());
    }

    @Test
    public void getTotalSizeDifferentPolyNodeTest() {
        PolyTree tree = new PolyTree();

        List<PolyNode> allPolys = tree.getAllPolys();
        allPolys.add(new PolyNode());
        allPolys.add(new PolyNode());

        tree.addChild(new PolyNode());

        Assert.assertEquals(1, tree.getTotalSize());
    }

    @Test
    public void getTotalSizeSamePolyNodeTest() {
        PolyTree tree = new PolyTree();
        PolyNode node = new PolyNode();

        List<PolyNode> allPolys = tree.getAllPolys();
        allPolys.add(node);
        allPolys.add(new PolyNode());

        tree.addChild(node);

        Assert.assertEquals(2, tree.getTotalSize());
    }
}
