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
package com.itextpdf.kernel.pdf.canvas.parser.clipper;

import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.EndType;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.JoinType;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PolyNodeTest extends ExtendedITextTest {

    @Test
    public void addAndGetChildTest() {
        PolyNode node = new PolyNode();

        PolyNode child = new PolyNode();
        node.addChild(child);

        Assert.assertSame(child, node.getChilds().get(0));
        Assert.assertEquals(1,  node.getChilds().size());
    }

    @Test
    public void unmodifiableListOfChildsTest() {
        PolyNode node = new PolyNode();

        List<PolyNode> childs = node.getChilds();

        Assert.assertThrows(UnsupportedOperationException.class, () -> childs.add(new PolyNode()));
    }

    @Test
    public void getChildCountTest() {
        PolyNode node = new PolyNode();
        node.addChild(new PolyNode());

        Assert.assertEquals(1, node.getChildCount());
    }

    @Test
    public void getContourAndPolygonTest() {
        PolyNode node = new PolyNode();

        Assert.assertTrue(node.getContour()instanceof Path);
        Assert.assertSame(node.getContour(), node.getPolygon());
    }

    @Test
    public void setAndGetEndTypeTest() {
        PolyNode node = new PolyNode();
        node.setEndType(EndType.CLOSED_POLYGON);

        Assert.assertEquals(EndType.CLOSED_POLYGON, node.getEndType());
    }

    @Test
    public void setAndGetJoinTypeTest() {
        PolyNode node = new PolyNode();
        node.setJoinType(JoinType.ROUND);

        Assert.assertEquals(JoinType.ROUND, node.getJoinType());
    }

    @Test
    public void setAndIsOpenTest() {
        PolyNode node = new PolyNode();
        node.setOpen(true);

        Assert.assertTrue(node.isOpen());
    }

    @Test
    public void setAndGetParentTest() {
        PolyNode parentNode = new PolyNode();

        PolyNode child = new PolyNode();
        child.setParent(parentNode);

        Assert.assertSame(parentNode, child.getParent());
    }

    @Test
    public void getNextPolyNodeNotEmptyTest() {
        PolyNode node = new PolyNode();
        node.addChild(new PolyNode());
        node.addChild(new PolyNode());

        Assert.assertSame(node.getChilds().get(0), node.getNext());
    }

    @Test
    public void getNextNoChildsTest() {
        PolyNode node = new PolyNode();

        Assert.assertNull(node.getNext());
    }

    @Test
    public void getNextPolyNodeWithSiblingTest() {
        PolyNode node = new PolyNode();
        PolyNode child1 = new PolyNode();
        PolyNode child2 = new PolyNode();

        node.addChild(child1);
        node.addChild(child2);
        child1.setParent(node);
        child2.setParent(node);

        Assert.assertSame(child2, child1.getNext());
    }

    @Test
    public void isHoleTest() {
        PolyNode node = new PolyNode();

        Assert.assertTrue(node.isHole());
    }

    @Test
    public void isNotHoleTest() {
        PolyNode node = new PolyNode();
        PolyNode child = new PolyNode();

        node.addChild(child);

        Assert.assertFalse(child.isHole());
    }
}
