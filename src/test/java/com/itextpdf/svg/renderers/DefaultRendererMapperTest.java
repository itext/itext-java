package com.itextpdf.svg.renderers;

import com.itextpdf.svg.renderers.factories.DefaultSvgNodeRendererMapper;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class DefaultRendererMapperTest {

    @Test
    public void mapperNotEmptyTest() {
        DefaultSvgNodeRendererMapper mapper = new DefaultSvgNodeRendererMapper();
        Assert.assertFalse(mapper.getMapping().isEmpty());
    }

    @Test
    public void createAllRenderersTest() throws InstantiationException, IllegalAccessException {
        DefaultSvgNodeRendererMapper mapper = new DefaultSvgNodeRendererMapper();
        for (Class<? extends ISvgNodeRenderer> rendererClazz : mapper.getMapping().values()) {
            // the test is that this method does not throw an exception on any class here
            // meaning that every (non-abstract) implementation must have a public no-args constructor
            rendererClazz.newInstance();
        }
    }
}
