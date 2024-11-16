package com.itextpdf.svg.renderers.impl;

import com.itextpdf.layout.font.FontProvider;
import com.itextpdf.styledxmlparser.resolver.resource.ResourceResolver;
import com.itextpdf.svg.exceptions.SvgExceptionMessageConstant;
import com.itextpdf.svg.exceptions.SvgProcessingException;
import com.itextpdf.svg.renderers.SvgDrawContext;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

@Tag("UnitTest")
public class CircleSvgNodeRendererUnitTest {

    @Test
    public void noViewPortTest() {
        CircleSvgNodeRenderer renderer = new CircleSvgNodeRenderer();
        SvgDrawContext context = new SvgDrawContext(new ResourceResolver(""), new FontProvider());
        Map<String, String> styles = new HashMap<>();
        styles.put("r", "50%");
        renderer.setAttributesAndStyles(styles);
        Exception e = Assertions.assertThrows(SvgProcessingException.class, () -> renderer.setParameters(context));
        Assertions.assertEquals(SvgExceptionMessageConstant.ILLEGAL_RELATIVE_VALUE_NO_VIEWPORT_IS_SET, e.getMessage());
    }
}
