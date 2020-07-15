package com.itextpdf.styledxmlparser.css.validate;

import com.itextpdf.styledxmlparser.LogMessageConstant;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssNumericValueValidator;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssNumericValueValidatorTest extends ExtendedITextTest {

    @Test
    public void nullValueTest() {
        final ICssDataTypeValidator validator = new CssNumericValueValidator(true, true);
        Assert.assertFalse(validator.isValid(null));
    }

    @Test
    public void initialInheritUnsetValuesTest() {
        final ICssDataTypeValidator validator = new CssNumericValueValidator(true, true);
        Assert.assertTrue(validator.isValid("initial"));
        Assert.assertTrue(validator.isValid("inherit"));
        Assert.assertTrue(validator.isValid("unset"));
    }

    @Test
    public void normalValueTest() {
        final ICssDataTypeValidator normalAllowedValidator = new CssNumericValueValidator(true, true);
        final ICssDataTypeValidator normalNotAllowedValidator = new CssNumericValueValidator(true, false);
        Assert.assertTrue(normalAllowedValidator.isValid("normal"));
        Assert.assertFalse(normalNotAllowedValidator.isValid("normal"));
    }

    @Test
    public void invalidValuesTest() {
        final ICssDataTypeValidator validator = new CssNumericValueValidator(true, true);
        Assert.assertFalse(validator.isValid(""));
        Assert.assertFalse(validator.isValid("dja"));
        Assert.assertFalse(validator.isValid("5pixels"));
    }

    @Test
    public void absoluteValuesTest() {
        final ICssDataTypeValidator validator = new CssNumericValueValidator(true, true);
        Assert.assertTrue(validator.isValid("12"));
        Assert.assertTrue(validator.isValid("12pt"));
        Assert.assertTrue(validator.isValid("12px"));
        Assert.assertTrue(validator.isValid("12in"));
        Assert.assertTrue(validator.isValid("12cm"));
        Assert.assertTrue(validator.isValid("12mm"));
        Assert.assertTrue(validator.isValid("12pc"));
        Assert.assertTrue(validator.isValid("12q"));
        Assert.assertFalse(validator.isValid("12 pt"));
    }

    @Test
    public void relativeValuesTest() {
        final ICssDataTypeValidator validator = new CssNumericValueValidator(true, true);
        Assert.assertTrue(validator.isValid("12em"));
        Assert.assertTrue(validator.isValid("12rem"));
        Assert.assertTrue(validator.isValid("12ex"));
        Assert.assertFalse(validator.isValid("12 em"));
    }

    @Test
    public void percentValueTest() {
        final ICssDataTypeValidator percentAllowedValidator = new CssNumericValueValidator(true, true);
        final ICssDataTypeValidator percentNotAllowedValidator = new CssNumericValueValidator(false, true);
        Assert.assertTrue(percentAllowedValidator.isValid("12%"));
        Assert.assertFalse(percentNotAllowedValidator.isValid("12%"));
    }
}
