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
