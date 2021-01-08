/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.layout.property.BackgroundRepeat.BackgroundRepeatValue;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssBackgroundUtilsTest extends ExtendedITextTest {
    @Test
    public void parseBackgroundRepeatTest() {
        Assert.assertEquals(BackgroundRepeatValue.REPEAT, CssBackgroundUtils.parseBackgroundRepeat("repeat"));
        Assert.assertEquals(BackgroundRepeatValue.REPEAT, CssBackgroundUtils.parseBackgroundRepeat("RePeAt"));

        Assert.assertEquals(BackgroundRepeatValue.NO_REPEAT, CssBackgroundUtils.parseBackgroundRepeat("no-repeat"));
        Assert.assertEquals(BackgroundRepeatValue.REPEAT, CssBackgroundUtils.parseBackgroundRepeat("no- repeat"));

        Assert.assertEquals(BackgroundRepeatValue.ROUND, CssBackgroundUtils.parseBackgroundRepeat("round"));
        Assert.assertEquals(BackgroundRepeatValue.REPEAT, CssBackgroundUtils.parseBackgroundRepeat("ro!und"));

        Assert.assertEquals(BackgroundRepeatValue.SPACE, CssBackgroundUtils.parseBackgroundRepeat("space"));
        Assert.assertEquals(BackgroundRepeatValue.REPEAT, CssBackgroundUtils.parseBackgroundRepeat(" space "));

        Assert.assertEquals(BackgroundRepeatValue.REPEAT, CssBackgroundUtils.parseBackgroundRepeat("something"));
    }

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
