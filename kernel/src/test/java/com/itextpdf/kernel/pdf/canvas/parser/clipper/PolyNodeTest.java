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

import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.EndType;
import com.itextpdf.kernel.pdf.canvas.parser.clipper.IClipper.JoinType;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.List;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PolyNodeTest extends ExtendedITextTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

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
        junitExpectedException.expect(UnsupportedOperationException.class);
        PolyNode node = new PolyNode();

        List<PolyNode> childs = node.getChilds();
        childs.add(new PolyNode());
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
