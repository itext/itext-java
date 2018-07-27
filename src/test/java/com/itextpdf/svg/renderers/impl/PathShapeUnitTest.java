package com.itextpdf.svg.renderers.impl;

import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.svg.SvgConstants;
import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.path.impl.AbstractPathShape;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.HashMap;
import java.util.Map;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class PathShapeUnitTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void nullAttributesTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.ATTRIBUTES_NULL);

        new DummyShape().getCoordinate(null, "");
    }

    @Test
    public void nullCoordinateTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.COORDINATE_VALUE_ABSENT);

        Map<String, String> attributes = new HashMap<>();
        attributes.put(SvgConstants.Attributes.X, null);

        new DummyShape().getCoordinate(attributes, SvgConstants.Attributes.X);
    }

    @Test
    public void emptyCoordinateTest() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.COORDINATE_VALUE_ABSENT);

        Map<String, String> attributes = new HashMap<>();
        attributes.put(SvgConstants.Attributes.X, "");

        new DummyShape().getCoordinate(attributes, SvgConstants.Attributes.X);
    }

    private class DummyShape extends AbstractPathShape {

        @Override
        public void draw(PdfCanvas canvas) {

        }

        @Override
        public void setCoordinates(String[] coordinates) {

        }
    }
}