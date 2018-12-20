package com.itextpdf.svg.renderers.path;

import com.itextpdf.svg.renderers.path.impl.PathShapeMapper;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

public class PathShapeMapperTest {

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
    public void testExistsSmoothCubicAbs() {
        Assert.assertNotNull(mapper.getMapping().get("S"));
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

    /* TODO: implement currently unsupported operator
     * DEVSIX-2267: relative alternatives for existing absolute operators
     * DEVSIX-2611: smooth quadratic curves (absolute and relative)
     */
    @Test
    public void testNotExistsQuadRel() {
        Assert.assertNull(mapper.getMapping().get("q"));
    }
    @Test
    public void testNotExistsSmoothCubicRel() {
        Assert.assertNull(mapper.getMapping().get("s"));
    }
    @Test
    public void testNotExistsSmoothQuadRel() {
        Assert.assertNull(mapper.getMapping().get("t"));
    }
    @Test
    public void testNotExistsSmoothQuadAbs() {
        Assert.assertNull(mapper.getMapping().get("T"));
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
