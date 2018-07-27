package com.itextpdf.svg.renderers.factories;

import com.itextpdf.svg.exceptions.SvgLogMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class DefaultSvgNodeRendererFactoryTest {

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void createSvgNodeRenderer() {
        junitExpectedException.expect(SvgProcessingException.class);
        junitExpectedException.expectMessage(SvgLogMessageConstant.TAGPARAMETERNULL);

        ISvgNodeRendererFactory nodeRendererFactory = new DefaultSvgNodeRendererFactory(null);
        nodeRendererFactory.createSvgNodeRendererForTag(null, null);
    }
}