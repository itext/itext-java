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
package com.itextpdf.svg.renderers.path;

import com.itextpdf.svg.renderers.path.impl.PathShapeMapper;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class PathShapeMapperTest extends ExtendedITextTest {

    private static IPathShapeMapper mapper;

    @BeforeClass
    public static void setUpClass() {
        mapper = new PathShapeMapper();
    }
    @Test
    public void testExistsEllipseRel() {
        Assert.assertNotNull(mapper.getMapping().get("a"));
    }
    @Test
    public void testExistsEllipseAbs() {
        Assert.assertNotNull(mapper.getMapping().get("A"));
    }
    @Test
    public void testExistsCubicRel() {
        Assert.assertNotNull(mapper.getMapping().get("c"));
    }
    @Test
    public void testExistsCubicAbs() {
        Assert.assertNotNull(mapper.getMapping().get("C"));
    }
    @Test
    public void testExistsHorizontalLineRel() {
        Assert.assertNotNull(mapper.getMapping().get("h"));
    }
    @Test
    public void testExistsHorizontalLineAbs() {
        Assert.assertNotNull(mapper.getMapping().get("H"));
    }
    @Test
    public void testExistsLineRel() {
        Assert.assertNotNull(mapper.getMapping().get("l"));
    }
    @Test
    public void testExistsLineAbs() {
        Assert.assertNotNull(mapper.getMapping().get("L"));
    }
    @Test
    public void testExistsMoveRel() {
        Assert.assertNotNull(mapper.getMapping().get("m"));
    }
    @Test
    public void testExistsMoveAbs() {
        Assert.assertNotNull(mapper.getMapping().get("M"));
    }
    @Test
    public void testExistsQuadAbs() {
        Assert.assertNotNull(mapper.getMapping().get("Q"));
    }
    @Test
    public void testExistsQuadRel() {
        Assert.assertNotNull(mapper.getMapping().get("q"));
    }
    @Test
    public void testExistsSmoothCubicAbs() {
        Assert.assertNotNull(mapper.getMapping().get("S"));
    }
    @Test
    public void testExistsSmoothCubicRel() {
        Assert.assertNotNull(mapper.getMapping().get("s"));
    }
    @Test
    public void testExistsVerticalLineRel() {
        Assert.assertNotNull(mapper.getMapping().get("v"));
    }
    @Test
    public void testExistsVerticalLineAbs() {
        Assert.assertNotNull(mapper.getMapping().get("V"));
    }
    @Test
    public void testExistsClosePathRel() {
        Assert.assertNotNull(mapper.getMapping().get("z"));
    }
    @Test
    public void testExistsClosePathAbs() {
        Assert.assertNotNull(mapper.getMapping().get("Z"));
    }
    @Test
    public void testExistsSmoothQuadAbs() {
        Assert.assertNotNull(mapper.getMapping().get("T"));
    }
    @Test
    public void testExistsSmoothQuadRel() {
        Assert.assertNotNull(mapper.getMapping().get("t"));
    }

    // nonsensical operators
    @Test
    public void testNotExistsNonExistingOperator1() {
        Assert.assertNull(mapper.getMapping().get("e"));
    }
    @Test
    public void testNotExistsNonExistingOperator2() {
        Assert.assertNull(mapper.getMapping().get("Y"));
    }
    @Test
    public void testNotExistsNonExistingOperator3() {
        Assert.assertNull(mapper.getMapping().get("3"));
    }
    @Test
    public void testNotExistsNonExistingOperator4() {
        Assert.assertNull(mapper.getMapping().get("am"));
    }
    @Test
    public void testNotExistsNonExistingOperator5() {
        Assert.assertNull(mapper.getMapping().get("Pos"));
    }
}
