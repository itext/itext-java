package com.itextpdf.kernel.geom;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.Arrays;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ShapeTransformUtilTest extends ExtendedITextTest {
    @Test
    public void transformBezierCurveTest() {
        BezierCurve inBezierCurve = new BezierCurve(Arrays.asList(new Point(0, 0), new Point(0, 5), new Point(5, 5), new Point(5, 0)));
        Matrix ctm = new Matrix(1, 0, 0, 1, 5, 5);
        BezierCurve outBezierCurve = ShapeTransformUtil.transformBezierCurve(inBezierCurve, ctm);

        BezierCurve cmpBezierCurve = new BezierCurve(Arrays.asList(new Point(-5, -5), new Point(-5, 0), new Point(0, 0), new Point(0, -5)));

        Assert.assertArrayEquals(cmpBezierCurve.getBasePoints().toArray(), outBezierCurve.getBasePoints().toArray());
    }

    @Test
    public void transformLineTest() {
        Line inLine = new Line(new Point(0, 0), new Point(10, 10));
        Matrix ctm = new Matrix(2, 0, 0, 1, 5, 5);
        Line outLine = ShapeTransformUtil.transformLine(inLine, ctm);

        Line cmpLine = new Line(new Point(-2.5, -5), new Point(2.5, 5));

        Assert.assertArrayEquals(cmpLine.getBasePoints().toArray(), outLine.getBasePoints().toArray());
    }

    @Test
    public void transformPathTest() {
        Line inLine = new Line(new Point(0, 0), new Point(10, 10));
        BezierCurve inBezierCurve = new BezierCurve(Arrays.asList(new Point(0, 0), new Point(0, 5), new Point(5, 5), new Point(5, 0)));
        Subpath inSubpath = new Subpath();
        inSubpath.addSegment(inLine);
        inSubpath.addSegment(inBezierCurve);
        Path inPath = new Path(Arrays.asList(inSubpath));
        Matrix ctm = new Matrix(1, 0, 0, 1, 5, 5);
        Path outPath = ShapeTransformUtil.transformPath(inPath, ctm);

        Line cmpLine = new Line(new Point(-5, -5), new Point(5, 5));
        BezierCurve cmpBezierCurve = new BezierCurve(Arrays.asList(new Point(-5, -5), new Point(-5, 0), new Point(0, 0), new Point(0, -5)));
        Subpath cmpSubpath = new Subpath();
        inSubpath.addSegment(cmpLine);
        inSubpath.addSegment(cmpBezierCurve);
        Path cmpPath = new Path(Arrays.asList(cmpSubpath));

        for (int i = 0; i < cmpPath.getSubpaths().size(); i++) {
            Subpath subpath = cmpPath.getSubpaths().get(i);
            for (int j = 0; j < subpath.getSegments().size(); j++) {
                IShape cmpShape = subpath.getSegments().get(j);
                IShape outShape = outPath.getSubpaths().get(i).getSegments().get(j);
                Assert.assertArrayEquals(cmpShape.getBasePoints().toArray(), outShape.getBasePoints().toArray());
            }
        }
    }
}
