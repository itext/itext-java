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
package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.SvgConstants.Attributes;
import com.itextpdf.svg.SvgConstants.Tags;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.renderers.ISvgNodeRenderer;
import com.itextpdf.svg.renderers.SvgDrawContext;
import com.itextpdf.test.ExtendedITextTest;

import java.util.HashMap;
import java.util.Map;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class StopSvgNodeRendererUnitTest extends ExtendedITextTest {
    private static final float DELTA = 0;

    @Test
    public void getOffsetPercentageValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, "50%");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        double expected = 0.5;

        Assertions.assertEquals(expected, renderer.getOffset(), DELTA);
    }

    @Test
    public void getOffsetNumericValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, "0.5");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        double expected = 0.5;

        Assertions.assertEquals(expected, renderer.getOffset(), DELTA);
    }

    @Test
    public void getOffsetMoreThanOneValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, "2");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        double expected = 1;

        Assertions.assertEquals(expected, renderer.getOffset(), DELTA);
    }

    @Test
    public void getOffsetLessThanZeroValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, "-2");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        double expected = 0;

        Assertions.assertEquals(expected, renderer.getOffset(), DELTA);
    }

    @Test
    public void getOffsetNoneValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, null);

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        double expected = 0;

        Assertions.assertEquals(expected, renderer.getOffset(), DELTA);
    }

    @Test
    public void getOffsetRandomStringValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, "Hello");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        double expected = 0;

        Assertions.assertEquals(expected, renderer.getOffset(), DELTA);
    }

    @Test
    public void getOffsetEmptyStringValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, "");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        double expected = 0;

        Assertions.assertEquals(expected, renderer.getOffset(), DELTA);
    }

    @Test
    public void getStopColorTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_COLOR, "red");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float[] expected = {1, 0, 0, 1};
        float[] actual = renderer.getStopColor();

        Assertions.assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    public void getStopColorNoneValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_COLOR, null);

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float[] expected = {0, 0, 0, 1};
        float[] actual = renderer.getStopColor();

        Assertions.assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    public void getStopColorRandomStringValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_COLOR, "Hello");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float[] expected = {0, 0, 0, 1};
        float[] actual = renderer.getStopColor();

        Assertions.assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    public void getStopColorEmptyStringTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_COLOR, "");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float[] expected = {0, 0, 0, 1};
        float[] actual = renderer.getStopColor();

        Assertions.assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    public void getStopColorOpacityTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_COLOR, "rgba(0.5, 0.5, 0.5, 0.5)");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float[] expected = {0, 0, 0, 1};
        float[] actual = renderer.getStopColor();

        Assertions.assertArrayEquals(expected, actual, DELTA);
    }

    @Test
    public void getStopOpacityTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_OPACITY, "1");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float expected = 1;

        Assertions.assertEquals(expected, renderer.getStopOpacity(), DELTA);
    }

    @Test
    public void getStopOpacityNoneValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_OPACITY, null);

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float expected = 1;

        Assertions.assertEquals(expected, renderer.getStopOpacity(), DELTA);
    }

    @Test
    public void getStopOpacityRandomStringValueTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_OPACITY, "Hello");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float expected = 1;

        Assertions.assertEquals(expected, renderer.getStopOpacity(), DELTA);
    }

    @Test
    public void getStopOpacityEmptyStringTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Tags.STOP_OPACITY, "");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        float expected = 1;

        Assertions.assertEquals(expected, renderer.getStopOpacity(), DELTA);
    }

    @Test
    public void createDeepCopyTest() {
        Map<String, String> styles = new HashMap<>();
        styles.put(Attributes.OFFSET, "0.5");

        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        renderer.setAttributesAndStyles(styles);

        ISvgNodeRenderer copy = renderer.createDeepCopy();

        Assertions.assertNotSame(renderer, copy);
        Assertions.assertEquals(renderer.getClass(), copy.getClass());
        Assertions.assertEquals(renderer.getAttributeMapCopy(), copy.getAttributeMapCopy());
    }

    @Test
    public void doDrawTest() {
        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();

        Exception e = Assertions.assertThrows(UnsupportedOperationException.class,
                () -> renderer.doDraw(new SvgDrawContext(null, null))
        );
        Assertions.assertEquals(SvgExceptionMessageConstant.DRAW_NO_DRAW, e.getMessage());
    }

    @Test
    public void noObjectBoundingBoxTest() {
        StopSvgNodeRenderer renderer = new StopSvgNodeRenderer();
        Assertions.assertNull(renderer.getObjectBoundingBox(null));
    }
}
