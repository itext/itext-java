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
package com.itextpdf.styledxmlparser.css.validate;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.styledxmlparser.css.validate.impl.CssDefaultValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.CssDeviceCmykAwareValidator;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssDeclarationValidationMasterTest extends ExtendedITextTest {

    @Test
    public void fontSizeEnumValidationTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "larger")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "smaller")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "xx-small")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "x-small")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "small")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "medium")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "large")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "x-large")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "xx-large")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "smaler")));
    }

    @Test
    public void fontSizeNumericValidationTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "5jaja")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "normal")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "5%")));
    }

    @Test
    public void wordSpacingValidationTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.WORD_SPACING, "5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.WORD_SPACING, "5jaja")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.WORD_SPACING, "normal")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.WORD_SPACING, "5%")));
    }

    @Test
    public void letterSpacingValidationTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LETTER_SPACING, "5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LETTER_SPACING, "5jaja")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LETTER_SPACING, "normal")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LETTER_SPACING, "5%")));
    }

    @Test
    public void textIndentValidationTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.TEXT_INDENT, "5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.TEXT_INDENT, "5jaja")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.TEXT_INDENT, "normal")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.TEXT_INDENT, "5%")));
    }

    @Test
    public void lineHeightValidationTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LINE_HEIGHT, "5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LINE_HEIGHT, "5jaja")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LINE_HEIGHT, "normal")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LINE_HEIGHT, "5%")));
    }

    @Test
    public void backgroundRepeatValidationTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "initial")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "no-repeat")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "repeat no-repeat")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "space")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "round")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "space repeat")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "no-repeat round")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "no-repeat,repeat")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "no-repeat repeat,repeat")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "repeat-x, repeat no-repeat")));

        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "ja")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "repeat-x repeat")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "initial repeat")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "repeat, repeat-x repeat")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "ja, repeat")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "initial, repeat")));
    }

    @Test
    public void backgroundPositionValidationTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_X, "initial")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_Y, "-0")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_Y, "5px")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_X, "5em")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_Y, "5px, 5%, bottom")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_X, "left 5%, right")));

        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_X, "5")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_Y, "ja")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_X, "initial 5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_Y, "ja, 5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_X, "initial, 5px")));
    }

    @Test
    public void backgroundSizeTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "initial")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "0")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "10px")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "10%")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "10% 10px")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "10px 10em")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "auto")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "auto 10px")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "10px auto")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "cover")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "contain")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "5px, 10%, auto")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "5px 10%, 20em")));

        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "ja")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "10")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "cover 10px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "initial 10px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "10px contain")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "ja, 5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "5px, ja")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "initial, 10px")));
    }

    @Test
    public void backgroundOriginTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "initial")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "border-box")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "padding-box")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "content-box")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "content-box, border-box")));

        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "ja")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "border-box border-box")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "content-box padding-box")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "ja, padding-box")));
    }

    @Test
    public void backgroundClipTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "initial")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "border-box")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "padding-box")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "content-box")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "content-box, border-box")));

        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "ja")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "border-box border-box")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "content-box padding-box")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "ja, padding-box")));
    }

    @Test
    public void backgroundImageTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "initial")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "url(rock_texture.jpg)")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "linear-gradient(red,green,blue)")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "url()")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "none")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "url(img.jpg),url(img2.jpg)")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "none,url(img2.jpg)")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "linear-gradient(red,green,blue),url(img2.jpg)")));

        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "ja")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "url(url(rock_texture.jpg)")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "true-linear-gradient(red,green,blue)")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "url(img.jpg) url(img2.jpg)")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "initial,url(img.jpg)")));
    }

    @Test
    public void overflowWrapTest() {
        String[] overflowWrapOrWordWrap = new String[] {CommonCssConstants.OVERFLOW_WRAP, CommonCssConstants.WORDWRAP};
        
        for (String overflowWrap : overflowWrapOrWordWrap) {
            Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "normal")));
            Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "anywhere")));
            Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "break-word")));
            Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "inherit")));
            Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "unset")));
            Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "initial")));
            Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "auto")));
            Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "norm")));
        }
    }

    @Test
    public void wordWrapTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "normal")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "break-all")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "keep-all")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "break-word")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "inherit")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "unset")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "initial")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "auto")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "norm")));

    }

    @Test
    public void justifyContentTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "inherit")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "right")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "normal")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "space-between")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "self-end")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "unsafe self-end")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "stretch")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "space-evenly")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "flex-start")));

        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "baseline")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "safe right")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "unsafe normal")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "unsafe space-between")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "self-center")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "self-end unsafe")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "safe stretch")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "space_evenly")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "flex-start left")));
    }

    @Test
    public void multicolValidationTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_COUNT, "auto")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_COUNT, "3")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_COUNT, "-3")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, "auto")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, "30px")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, "20%")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, "5em")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, "5rem")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, "-5rem")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, "10")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_GAP, "normal")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_GAP, "30px")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_GAP, "15%")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_GAP, "2em")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_GAP, "3rem")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_GAP, "-5em")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_GAP, "10")));
    }

    @Test
    public void gridRowColumnGapTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.GRID_ROW_GAP, "normal")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.GRID_COLUMN_GAP, "30px")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.GRID_ROW_GAP, "15%")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.GRID_ROW_GAP, "2em")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.GRID_COLUMN_GAP, "3rem")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.GRID_COLUMN_GAP, "-5em")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.GRID_ROW_GAP, "10")));
    }

    @Test
    public void changeValidatorTest() {
        try {
            CssDeclarationValidationMaster.setValidator(new CssDeviceCmykAwareValidator());
            Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.COLOR, "device-cmyk(0, 100%, 70%, 0)")));
        } finally {
            CssDeclarationValidationMaster.setValidator(new CssDefaultValidator());
        }
    }
}
