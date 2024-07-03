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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.layout.properties.BackgroundRepeat.BackgroundRepeatValue;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssBackgroundUtilsTest extends ExtendedITextTest {
    @Test
    public void parseBackgroundRepeatTest() {
        Assertions.assertEquals(BackgroundRepeatValue.REPEAT, CssBackgroundUtils.parseBackgroundRepeat("repeat"));
        Assertions.assertEquals(BackgroundRepeatValue.REPEAT, CssBackgroundUtils.parseBackgroundRepeat("RePeAt"));

        Assertions.assertEquals(BackgroundRepeatValue.NO_REPEAT, CssBackgroundUtils.parseBackgroundRepeat("no-repeat"));
        Assertions.assertEquals(BackgroundRepeatValue.REPEAT, CssBackgroundUtils.parseBackgroundRepeat("no- repeat"));

        Assertions.assertEquals(BackgroundRepeatValue.ROUND, CssBackgroundUtils.parseBackgroundRepeat("round"));
        Assertions.assertEquals(BackgroundRepeatValue.REPEAT, CssBackgroundUtils.parseBackgroundRepeat("ro!und"));

        Assertions.assertEquals(BackgroundRepeatValue.SPACE, CssBackgroundUtils.parseBackgroundRepeat("space"));
        Assertions.assertEquals(BackgroundRepeatValue.REPEAT, CssBackgroundUtils.parseBackgroundRepeat(" space "));

        Assertions.assertEquals(BackgroundRepeatValue.REPEAT, CssBackgroundUtils.parseBackgroundRepeat("something"));
    }

    @LogMessages(messages = {@LogMessage(messageTemplate =
            StyledXmlParserLogMessageConstant.URL_IS_NOT_CLOSED_IN_CSS_EXPRESSION)})
    @Test
    public void resolveBackgroundPropertyTypeTest() {
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.UNDEFINED,
                CssBackgroundUtils.resolveBackgroundPropertyType("jaja"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.UNDEFINED,
                CssBackgroundUtils.resolveBackgroundPropertyType("ul(rock_texture.jpg)"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.UNDEFINED,
                CssBackgroundUtils.resolveBackgroundPropertyType("url(rock_texture.jpg"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.UNDEFINED,
                CssBackgroundUtils.resolveBackgroundPropertyType("url(rock(_texture.jpg)"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.UNDEFINED,
                CssBackgroundUtils.resolveBackgroundPropertyType("url(rock_t(ext)ure.jpg)"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.UNDEFINED,
                CssBackgroundUtils.resolveBackgroundPropertyType("url(url(rock_texture.jpg)"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_IMAGE,
                CssBackgroundUtils.resolveBackgroundPropertyType("url(rock_texture.jpg)"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_IMAGE,
                CssBackgroundUtils.resolveBackgroundPropertyType("linear-gradient(#e66465, #9198e5)"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_IMAGE,
                CssBackgroundUtils.resolveBackgroundPropertyType("none"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_REPEAT,
                CssBackgroundUtils.resolveBackgroundPropertyType("repeat-x"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION_X,
                CssBackgroundUtils.resolveBackgroundPropertyType("left"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION_Y,
                CssBackgroundUtils.resolveBackgroundPropertyType("bottom"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION,
                CssBackgroundUtils.resolveBackgroundPropertyType("center"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION_OR_SIZE,
                CssBackgroundUtils.resolveBackgroundPropertyType("10%"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_SIZE,
                CssBackgroundUtils.resolveBackgroundPropertyType("contain"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ORIGIN_OR_CLIP,
                CssBackgroundUtils.resolveBackgroundPropertyType("padding-box"));
        Assertions.assertEquals(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ATTACHMENT,
                CssBackgroundUtils.resolveBackgroundPropertyType("fixed"));
    }

    @Test
    public void getBackgroundPropertyNameFromType() {
        Assertions.assertEquals(CommonCssConstants.BACKGROUND_COLOR,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_COLOR));
        Assertions.assertEquals(CommonCssConstants.BACKGROUND_IMAGE,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_IMAGE));
        Assertions.assertEquals(CommonCssConstants.BACKGROUND_CLIP,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_CLIP));
        Assertions.assertEquals(CommonCssConstants.BACKGROUND_ORIGIN,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ORIGIN));
        Assertions.assertEquals(CommonCssConstants.BACKGROUND_POSITION,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_POSITION));
        Assertions.assertEquals(CommonCssConstants.BACKGROUND_REPEAT,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_REPEAT));
        Assertions.assertEquals(CommonCssConstants.BACKGROUND_SIZE,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_SIZE));
        Assertions.assertEquals(CommonCssConstants.BACKGROUND_ATTACHMENT,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.BACKGROUND_ATTACHMENT));
        Assertions.assertEquals(CommonCssConstants.UNDEFINED_NAME,
                CssBackgroundUtils.getBackgroundPropertyNameFromType(CssBackgroundUtils.BackgroundPropertyType.UNDEFINED));
    }
}
