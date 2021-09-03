package com.itextpdf.svg.renderers.factories;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class DefaultSvgNodeRendererMapperTest extends ExtendedITextTest {

    private DefaultSvgNodeRendererMapper mapper = new DefaultSvgNodeRendererMapper();

    @Test
    public void mapperNotEmptyTest() {
        boolean result = mapper.getMapping().isEmpty();
        Assert.assertFalse(result);
    }

    @Test
    public void ignoredTagsNotEmptyTest() {
        boolean result = mapper.getIgnoredTags().isEmpty();
        Assert.assertFalse(result);
    }
}
