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
