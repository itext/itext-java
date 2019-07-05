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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.geom.Point;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.SvgIntegrationTest;
import com.itextpdf.svg.renderers.path.IPathShape;
import com.itextpdf.svg.renderers.path.impl.ClosePath;
import com.itextpdf.svg.renderers.path.impl.EllipticalCurveTo;
import com.itextpdf.svg.renderers.path.impl.MoveTo;
import com.itextpdf.svg.renderers.path.impl.SmoothSCurveTo;
import com.itextpdf.test.annotations.type.IntegrationTest;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

import java.util.List;

@Category(IntegrationTest.class)
public class PathSvgNodeRendererLowLevelIntegrationTest extends SvgIntegrationTest {
    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void testRelativeArcOperatorShapes() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 a 10 10 0 0 0 10 10";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        List<IPathShape> segments = (List<IPathShape>) path.getShapes();
        Assert.assertEquals(2, segments.size());
        Assert.assertTrue(segments.get(0) instanceof MoveTo);
        Assert.assertTrue(segments.get(1) instanceof EllipticalCurveTo);
    }

    @Test
    public void testRelativeArcOperatorCoordinates() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 a 10 10 0 0 0 10 10";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        IPathShape arc = ((List<IPathShape>) path.getShapes()).get(1);
        Point end = arc.getEndingPoint();
        Assert.assertEquals(new Point(210, 310), end);
    }

    @Test
    public void testMultipleRelativeArcOperatorCoordinates() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 a 10 10 0 0 0 10 10 a 10 10 0 0 0 10 10";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        IPathShape arc = ((List<IPathShape>) path.getShapes()).get(2);
        Point end = arc.getEndingPoint();
        Assert.assertEquals(new Point(220, 320), end);
    }

    @Test
    public void testAbsoluteArcOperatorCoordinates() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 A 10 10 0 0 0 210 310";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        IPathShape arc = ((List<IPathShape>) path.getShapes()).get(1);
        Point end = arc.getEndingPoint();
        Assert.assertEquals(new Point(210, 310), end);
    }

    @Test
    public void testMultipleAbsoluteArcOperatorCoordinates() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 200,300 A 10 10 0 0 0 210 310 A 10 10 0 0 0 220 320";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        IPathShape arc = ((List<IPathShape>) path.getShapes()).get(2);
        Point end = arc.getEndingPoint();
        Assert.assertEquals(new Point(220, 320), end);
    }

    // tests resulting in empty path
    @Test
    public void testEmptyPath() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testNonsensePathNoOperators() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "200";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testNonsensePathNotExistingOperator() {
        junitExpectedException.expect(SvgProcessingException.class);
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "F";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testClosePathNoPrecedingPathsOperator() {
        junitExpectedException.expect(SvgProcessingException.class);
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "z";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testMoveNoArgsOperator() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testMoveOddArgsOperator() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 500";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertTrue(path.getShapes().isEmpty());
    }

    @Test
    public void testAddMultipleArgsOperator() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 500 500 200 200 300 300";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertEquals(3, path.getShapes().size());
    }

    @Test
    public void testAddMultipleOddArgsOperator() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "L 500 500 200 200 300";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertEquals(2, path.getShapes().size());
    }

    @Test
    public void testAddMultipleOddArgsOperatorThenOtherStuff() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 500 500 200 200 300 z";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertEquals(3, path.getShapes().size());
        Assert.assertTrue(((List<IPathShape>) path.getShapes()).get(2) instanceof ClosePath);
    }

    @Test
    public void testAddDoubleArgsOperator() {
        PathSvgNodeRenderer path = new PathSvgNodeRenderer();
        String instructions = "M 500 500 S 200 100 100 200 300 300 400 400";
        path.setAttribute(SvgConstants.Attributes.D, instructions);
        Assert.assertEquals(3, path.getShapes().size());
        Assert.assertTrue(((List<IPathShape>) path.getShapes()).get(2) instanceof SmoothSCurveTo);
    }
}
