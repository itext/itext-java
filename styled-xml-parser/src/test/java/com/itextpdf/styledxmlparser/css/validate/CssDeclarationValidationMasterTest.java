/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssDeclarationValidationMasterTest extends ExtendedITextTest {

    @Test
    public void fontSizeEnumValidationTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "larger")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "smaller")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "xx-small")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "x-small")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "small")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "medium")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "large")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "x-large")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "xx-large")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "smaler")));
    }

    @Test
    public void fontSizeNumericValidationTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "5px")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "5jaja")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "normal")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "5%")));
    }

    @Test
    public void wordSpacingValidationTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.WORD_SPACING, "5px")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.WORD_SPACING, "5jaja")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.WORD_SPACING, "normal")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.WORD_SPACING, "5%")));
    }

    @Test
    public void letterSpacingValidationTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LETTER_SPACING, "5px")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LETTER_SPACING, "5jaja")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LETTER_SPACING, "normal")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LETTER_SPACING, "5%")));
    }

    @Test
    public void textIndentValidationTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.TEXT_INDENT, "5px")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.TEXT_INDENT, "5jaja")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.TEXT_INDENT, "normal")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.TEXT_INDENT, "5%")));
    }

    @Test
    public void lineHeightValidationTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LINE_HEIGHT, "5px")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LINE_HEIGHT, "5jaja")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LINE_HEIGHT, "normal")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LINE_HEIGHT, "5%")));
    }

    @Test
    public void backgroundRepeatValidationTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "initial")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "no-repeat")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "repeat no-repeat")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "space")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "round")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "space repeat")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "no-repeat round")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "no-repeat,repeat")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "no-repeat repeat,repeat")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "repeat-x, repeat no-repeat")));

        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "5px")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "ja")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "repeat-x repeat")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "initial repeat")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "repeat, repeat-x repeat")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "ja, repeat")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_REPEAT, "initial, repeat")));
    }

    @Test
    public void backgroundPositionValidationTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_X, "initial")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_Y, "-0")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_Y, "5px")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_X, "5em")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_Y, "5px, 5%, bottom")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_X, "left 5%, right")));

        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_X, "5")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_Y, "ja")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_X, "initial 5px")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_Y, "ja, 5px")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_POSITION_X, "initial, 5px")));
    }

    @Test
    public void backgroundSizeTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "initial")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "0")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "10px")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "10%")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "10% 10px")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "10px 10em")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "auto")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "auto 10px")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "10px auto")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "cover")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "contain")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "5px, 10%, auto")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "5px 10%, 20em")));

        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "ja")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "10")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "cover 10px")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "initial 10px")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "10px contain")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "ja, 5px")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "5px, ja")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_SIZE, "initial, 10px")));
    }

    @Test
    public void backgroundOriginTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "initial")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "border-box")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "padding-box")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "content-box")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "content-box, border-box")));

        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "5px")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "ja")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "border-box border-box")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "content-box padding-box")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_ORIGIN, "ja, padding-box")));
    }

    @Test
    public void backgroundClipTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "initial")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "border-box")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "padding-box")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "content-box")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "content-box, border-box")));

        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "5px")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "ja")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "border-box border-box")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "content-box padding-box")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_CLIP, "ja, padding-box")));
    }

    @Test
    public void backgroundImageTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "initial")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "url(rock_texture.jpg)")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "linear-gradient(red,green,blue)")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "url()")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "none")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "url(img.jpg),url(img2.jpg)")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "none,url(img2.jpg)")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "linear-gradient(red,green,blue),url(img2.jpg)")));

        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "ja")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "5px")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "url(url(rock_texture.jpg)")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "true-linear-gradient(red,green,blue)")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "url(img.jpg) url(img2.jpg)")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.BACKGROUND_IMAGE, "initial,url(img.jpg)")));
    }

    @Test
    public void overflowWrapTest() {
        String[] overflowWrapOrWordWrap = new String[] {CommonCssConstants.OVERFLOW_WRAP, CommonCssConstants.WORDWRAP};
        
        for (String overflowWrap : overflowWrapOrWordWrap) {
            Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "normal")));
            Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "anywhere")));
            Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "break-word")));
            Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "inherit")));
            Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "unset")));
            Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "initial")));
            Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "auto")));
            Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                    new CssDeclaration(overflowWrap, "norm")));
        }
    }

    @Test
    public void wordWrapTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "normal")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "break-all")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "keep-all")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "break-word")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "inherit")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "unset")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "initial")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "auto")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.WORD_BREAK, "norm")));

    }

    @Test
    public void justifyContentTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "inherit")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "right")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "normal")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "space-between")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "self-end")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "unsafe self-end")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "stretch")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "space-evenly")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "flex-start")));

        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "baseline")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "safe right")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "unsafe normal")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "unsafe space-between")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "self-center")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "self-end unsafe")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "safe stretch")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "space_evenly")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.JUSTIFY_CONTENT, "flex-start left")));
    }

    @Test
    public void multicolValidationTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_COUNT, "auto")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_COUNT, "3")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_COUNT, "-3")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, "auto")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, "30px")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, "20%")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, "5em")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, "5rem")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, "-5rem")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_WIDTH, "10")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_GAP, "normal")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_GAP, "30px")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_GAP, "15%")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_GAP, "2em")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_GAP, "3rem")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_GAP, "-5em")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.COLUMN_GAP, "10")));
    }

    @Test
    public void gridRowColumnGapTest() {
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.GRID_ROW_GAP, "normal")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.GRID_COLUMN_GAP, "30px")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.GRID_ROW_GAP, "15%")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.GRID_ROW_GAP, "2em")));
        Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.GRID_COLUMN_GAP, "3rem")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.GRID_COLUMN_GAP, "-5em")));
        Assertions.assertFalse(CssDeclarationValidationMaster.checkDeclaration(
                new CssDeclaration(CommonCssConstants.GRID_ROW_GAP, "10")));
    }

    @Test
    public void changeValidatorTest() {
        try {
            CssDeclarationValidationMaster.setValidator(new CssDeviceCmykAwareValidator());
            Assertions.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.COLOR, "device-cmyk(0, 100%, 70%, 0)")));
        } finally {
            CssDeclarationValidationMaster.setValidator(new CssDefaultValidator());
        }
    }
}
