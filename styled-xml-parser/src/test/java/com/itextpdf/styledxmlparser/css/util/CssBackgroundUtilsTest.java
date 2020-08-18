package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssBackgroundUtilsTest extends ExtendedITextTest {

    @Test
    public void resolveBackgroundPropertyTypeTest() {
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.UNDEFINED,
                CssBackgroundUtils.resolveBackgroundPropertyType("jaja"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.UNDEFINED,
                CssBackgroundUtils.resolveBackgroundPropertyType("ul(rock_texture.jpg)"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.UNDEFINED,
                CssBackgroundUtils.resolveBackgroundPropertyType("url(rock_texture.jpg"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.UNDEFINED,
                CssBackgroundUtils.resolveBackgroundPropertyType("url(rock(_texture.jpg)"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.UNDEFINED,
                CssBackgroundUtils.resolveBackgroundPropertyType("url(rock_t(ext)ure.jpg)"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.UNDEFINED,
                CssBackgroundUtils.resolveBackgroundPropertyType("url(url(rock_texture.jpg)"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_IMAGE,
                CssBackgroundUtils.resolveBackgroundPropertyType("url(rock_texture.jpg)"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_IMAGE,
                CssBackgroundUtils.resolveBackgroundPropertyType("linear-gradient(#e66465, #9198e5)"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_IMAGE,
                CssBackgroundUtils.resolveBackgroundPropertyType("none"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_REPEAT,
                CssBackgroundUtils.resolveBackgroundPropertyType("repeat-x"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION_X,
                CssBackgroundUtils.resolveBackgroundPropertyType("left"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION_Y,
                CssBackgroundUtils.resolveBackgroundPropertyType("bottom"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION,
                CssBackgroundUtils.resolveBackgroundPropertyType("center"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION_OR_SIZE,
                CssBackgroundUtils.resolveBackgroundPropertyType("10%"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_SIZE,
                CssBackgroundUtils.resolveBackgroundPropertyType("contain"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ORIGIN_OR_CLIP,
                CssBackgroundUtils.resolveBackgroundPropertyType("padding-box"));
        Assert.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ATTACHMENT,
                CssBackgroundUtils.resolveBackgroundPropertyType("fixed"));
    }

    @Test
    public void getBackgroundPropertyNameFromType() {
        Assert.assertEquals(CommonCssConstants.BACKGROUND_COLOR,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_COLOR));
        Assert.assertEquals(CommonCssConstants.BACKGROUND_IMAGE,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_IMAGE));
        Assert.assertEquals(CommonCssConstants.BACKGROUND_CLIP,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_CLIP));
        Assert.assertEquals(CommonCssConstants.BACKGROUND_ORIGIN,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ORIGIN));
        Assert.assertEquals(CommonCssConstants.BACKGROUND_POSITION,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION));
        Assert.assertEquals(CommonCssConstants.BACKGROUND_REPEAT,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_REPEAT));
        Assert.assertEquals(CommonCssConstants.BACKGROUND_SIZE,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_SIZE));
        Assert.assertEquals(CommonCssConstants.BACKGROUND_ATTACHMENT,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ATTACHMENT));
        Assert.assertEquals(CommonCssConstants.UNDEFINED_NAME,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.UNDEFINED));
    }
}
