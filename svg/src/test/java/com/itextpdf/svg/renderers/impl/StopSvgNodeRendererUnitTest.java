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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.SvgConstants.Tags;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class StopSvgNodeRendererUnitTest extends ExtendedITextTest {
    private static final float DELTA = 0;

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void getOffsetPercentageValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, "50%");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        double expected = 0.5;

        Assert.assertEquals(expected, renderer.getOffset(), DELTA);
    }

    @Test
    public void getOffsetNumericValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, "0.5");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        double expected = 0.5;

        Assert.assertEquals(expected, renderer.getOffset(), DELTA);
    }

    @Test
    public void getOffsetMoreThanOneValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, "2");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        double expected = 1;

        Assert.assertEquals(expected, renderer.getOffset(), DELTA);
    }

    @Test
    public void getOffsetLessThanZeroValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, "-2");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        double expected = 0;

        Assert.assertEquals(expected, renderer.getOffset(), DELTA);
    }

    @Test
    public void getOffsetNoneValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, null);

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        double expected = 0;

        Assert.assertEquals(expected, renderer.getOffset(), DELTA);
    }

    @Test
    public void getOffsetRandomStringValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, "Hello");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        double expected = 0;

        Assert.assertEquals(expected, renderer.getOffset(), DELTA);
    }

    @Test
    public void getOffsetEmptyStringValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, "");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        double expected = 0;

        Assert.assertEquals(expected, renderer.getOffset(), DELTA);
    }

    @Test
    public void getStopColorTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_COLOR, "red");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float[] expected = {1, 0, 0, 1};
        float[] actual = renderer.getStopColor();

        Assert.assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    public void getStopColorNoneValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_COLOR, null);

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float[] expected = {0, 0, 0, 1};
        float[] actual = renderer.getStopColor();

        Assert.assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    public void getStopColorRandomStringValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_COLOR, "Hello");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float[] expected = {0, 0, 0, 1};
        float[] actual = renderer.getStopColor();

        Assert.assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    public void getStopColorEmptyStringTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_COLOR, "");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float[] expected = {0, 0, 0, 1};
        float[] actual = renderer.getStopColor();

        Assert.assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    public void getStopColorOpacityTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_COLOR, "rgba(0.5, 0.5, 0.5, 0.5)");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float[] expected = {0, 0, 0, 1};
        float[] actual = renderer.getStopColor();

        Assert.assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    public void getStopOpacityTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_OPACITY, "1");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float expected = 1;

        Assert.assertEquals(expected, renderer.getStopOpacity(), DELTA);
    }

    @Test
    public void getStopOpacityNoneValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_OPACITY, null);

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float expected = 1;

        Assert.assertEquals(expected, renderer.getStopOpacity(), DELTA);
    }

    @Test
    public void getStopOpacityRandomStringValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_OPACITY, "Hello");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float expected = 1;

        Assert.assertEquals(expected, renderer.getStopOpacity(), DELTA);
    }

    @Test
    public void getStopOpacityEmptyStringTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_OPACITY, "");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float expected = 1;

        Assert.assertEquals(expected, renderer.getStopOpacity(), DELTA);
    }

    @Test
    public void createDeepCopyTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, "0.5");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        ISvgNodeRenderer copy = renderer.createDeepCopy();

        Assert.assertNotSame(renderer, copy);
        Assert.assertEquals(renderer.getClass(), copy.getClass());
        Assert.assertEquals(renderer.getAttributeMapCopy(), copy.getAttributeMapCopy());
    }

    @Test
    public void doDrawTest() {
        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();

        junitExpectedException.expect(UnsupportedOperationException.class);
        junitExpectedException.expectMessage("Can't draw current SvgNodeRenderer.");
        renderer.doDraw(new SvgDrawContext(null, null));
    }
}
