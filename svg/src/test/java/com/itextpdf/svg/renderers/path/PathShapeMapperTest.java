/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
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
