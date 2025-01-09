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
package com.itextpdf.svg.renderers.path;

import com.itextpdf.svg.renderers.path.impl.PathShapeMapper;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class PathShapeMapperTest extends ExtendedITextTest {

    private static IPathShapeMapper mapper;

    @BeforeAll
    public static void setUpClass() {
        mapper = new PathShapeMapper();
    }
    @Test
    public void testExistsEllipseRel() {
        Assertions.assertNotNull(mapper.getMapping().get("a"));
    }
    @Test
    public void testExistsEllipseAbs() {
        Assertions.assertNotNull(mapper.getMapping().get("A"));
    }
    @Test
    public void testExistsCubicRel() {
        Assertions.assertNotNull(mapper.getMapping().get("c"));
    }
    @Test
    public void testExistsCubicAbs() {
        Assertions.assertNotNull(mapper.getMapping().get("C"));
    }
    @Test
    public void testExistsHorizontalLineRel() {
        Assertions.assertNotNull(mapper.getMapping().get("h"));
    }
    @Test
    public void testExistsHorizontalLineAbs() {
        Assertions.assertNotNull(mapper.getMapping().get("H"));
    }
    @Test
    public void testExistsLineRel() {
        Assertions.assertNotNull(mapper.getMapping().get("l"));
    }
    @Test
    public void testExistsLineAbs() {
        Assertions.assertNotNull(mapper.getMapping().get("L"));
    }
    @Test
    public void testExistsMoveRel() {
        Assertions.assertNotNull(mapper.getMapping().get("m"));
    }
    @Test
    public void testExistsMoveAbs() {
        Assertions.assertNotNull(mapper.getMapping().get("M"));
    }
    @Test
    public void testExistsQuadAbs() {
        Assertions.assertNotNull(mapper.getMapping().get("Q"));
    }
    @Test
    public void testExistsQuadRel() {
        Assertions.assertNotNull(mapper.getMapping().get("q"));
    }
    @Test
    public void testExistsSmoothCubicAbs() {
        Assertions.assertNotNull(mapper.getMapping().get("S"));
    }
    @Test
    public void testExistsSmoothCubicRel() {
        Assertions.assertNotNull(mapper.getMapping().get("s"));
    }
    @Test
    public void testExistsVerticalLineRel() {
        Assertions.assertNotNull(mapper.getMapping().get("v"));
    }
    @Test
    public void testExistsVerticalLineAbs() {
        Assertions.assertNotNull(mapper.getMapping().get("V"));
    }
    @Test
    public void testExistsClosePathRel() {
        Assertions.assertNotNull(mapper.getMapping().get("z"));
    }
    @Test
    public void testExistsClosePathAbs() {
        Assertions.assertNotNull(mapper.getMapping().get("Z"));
    }
    @Test
    public void testExistsSmoothQuadAbs() {
        Assertions.assertNotNull(mapper.getMapping().get("T"));
    }
    @Test
    public void testExistsSmoothQuadRel() {
        Assertions.assertNotNull(mapper.getMapping().get("t"));
    }

    // nonsensical operators
    @Test
    public void testNotExistsNonExistingOperator1() {
        Assertions.assertNull(mapper.getMapping().get("e"));
    }
    @Test
    public void testNotExistsNonExistingOperator2() {
        Assertions.assertNull(mapper.getMapping().get("Y"));
    }
    @Test
    public void testNotExistsNonExistingOperator3() {
        Assertions.assertNull(mapper.getMapping().get("3"));
    }
    @Test
    public void testNotExistsNonExistingOperator4() {
        Assertions.assertNull(mapper.getMapping().get("am"));
    }
    @Test
    public void testNotExistsNonExistingOperator5() {
        Assertions.assertNull(mapper.getMapping().get("Pos"));
    }
}
