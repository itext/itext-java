package com.itextpdf.svg.renderers.impl;

import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class NoDrawOperationSvgNodeRendererUnitTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void dontDrawTest() {
        junitExpectedException.expect(UnsupportedOperationException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.DRAW_NO_DRAW);

        NoDrawOperationSvgNodeRenderer renderer = new NoDrawOperationSvgNodeRenderer();
        renderer.doDraw(null);
    }
}