/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.styledxmlparser.css.validate;

import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.css.CssDeclaration;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssDeclarationValidationMasterTest extends ExtendedITextTest {

    @Test
    public void FontSizeEnumValidationTest() {
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
    public void FontSizeNumericValidationTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "5jaja")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "normal")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.FONT_SIZE, "5%")));
    }

    @Test
    public void WordSpacingValidationTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.WORD_SPACING, "5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.WORD_SPACING, "5jaja")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.WORD_SPACING, "normal")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.WORD_SPACING, "5%")));
    }

    @Test
    public void LetterSpacingValidationTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LETTER_SPACING, "5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LETTER_SPACING, "5jaja")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LETTER_SPACING, "normal")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LETTER_SPACING, "5%")));
    }

    @Test
    public void TextIndentValidationTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.TEXT_INDENT, "5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.TEXT_INDENT, "5jaja")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.TEXT_INDENT, "normal")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.TEXT_INDENT, "5%")));
    }

    @Test
    public void LineHeightValidationTest() {
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LINE_HEIGHT, "5px")));
        Assert.assertFalse(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LINE_HEIGHT, "5jaja")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LINE_HEIGHT, "normal")));
        Assert.assertTrue(CssDeclarationValidationMaster.checkDeclaration(new CssDeclaration(CommonCssConstants.LINE_HEIGHT, "5%")));
    }
}
