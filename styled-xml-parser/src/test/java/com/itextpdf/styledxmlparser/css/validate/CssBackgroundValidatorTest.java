package com.itextpdf.styledxmlparser.css.validate;

import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssBackgroundValidator;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssBackgroundValidatorTest extends ExtendedITextTest {

    @Test
    public void nullValueTest() {
        final ICssDataTypeValidator validator = new CssBackgroundValidator("any property");
        Assert.assertFalse(validator.isValid(null));
    }

    @Test
    public void undefinedValueTest() {
        final ICssDataTypeValidator validator = new CssBackgroundValidator("undefined");
        Assert.assertFalse(validator.isValid("ja"));
    }

    @Test
    public void initialInheritUnsetValueTest() {
        final ICssDataTypeValidator validator = new CssBackgroundValidator("any property");
        Assert.assertTrue(validator.isValid("initial"));
        Assert.assertTrue(validator.isValid("inherit"));
        Assert.assertTrue(validator.isValid("unset"));
    }

    @Test
    public void emptyValueTest() {
        final ICssDataTypeValidator validator = new CssBackgroundValidator("any property");
        Assert.assertFalse(validator.isValid(""));
    }

    @Test
    public void propertyValueCorrespondsPropertyTypeTest() {
        ICssDataTypeValidator validator = new CssBackgroundValidator("background-repeat");
        Assert.assertTrue(validator.isValid("repeat-x"));
        Assert.assertFalse(validator.isValid("cover"));

        validator = new CssBackgroundValidator("background-image");
        Assert.assertTrue(validator.isValid("url(something.png)"));
        Assert.assertFalse(validator.isValid("5px"));

        validator = new CssBackgroundValidator("background-attachment");
        Assert.assertTrue(validator.isValid("fixed"));
        Assert.assertFalse(validator.isValid("5px"));
    }

    @Test
    public void propertyValueWithMultiTypesCorrespondsPropertyTypeTest() {
        final ICssDataTypeValidator positionValidator = new CssBackgroundValidator("background-position");
        final ICssDataTypeValidator sizeValidator = new CssBackgroundValidator("background-size");
        Assert.assertTrue(positionValidator.isValid("5px"));
        Assert.assertTrue(sizeValidator.isValid("5px"));
        Assert.assertTrue(positionValidator.isValid("5%"));
        Assert.assertTrue(sizeValidator.isValid("5%"));
        Assert.assertTrue(positionValidator.isValid("left"));
        Assert.assertFalse(sizeValidator.isValid("left"));
        Assert.assertFalse(positionValidator.isValid("contain"));
        Assert.assertTrue(sizeValidator.isValid("contain"));

        final ICssDataTypeValidator originValidator = new CssBackgroundValidator("background-origin");
        final ICssDataTypeValidator clipValidator = new CssBackgroundValidator("background-clip");
        Assert.assertTrue(originValidator.isValid("border-box"));
        Assert.assertTrue(clipValidator.isValid("border-box"));
        Assert.assertTrue(originValidator.isValid("padding-box"));
        Assert.assertTrue(clipValidator.isValid("padding-box"));
        Assert.assertTrue(originValidator.isValid("content-box"));
        Assert.assertTrue(clipValidator.isValid("content-box"));
    }

    @Test
    public void multiValuesAllowedForThisTypeTest() {
        ICssDataTypeValidator validator = new CssBackgroundValidator("background-size");
        Assert.assertTrue(validator.isValid("5px 10%"));

        validator = new CssBackgroundValidator("background-position");
        Assert.assertTrue(validator.isValid("5px 10%"));
        // TODO DEVSIX-1457 change to assertFalse when background-position property will be supported.
        Assert.assertTrue(validator.isValid("5px 5px 5px 5px 5px 5px 5px 5px 5px 5px 5px 5px 5px"));

        validator = new CssBackgroundValidator("background-repeat");
        Assert.assertTrue(validator.isValid("repeat no-repeat"));
        // TODO DEVSIX-4370 change to assertFalse when background-repeat property will be fully supported.
        Assert.assertTrue(validator.isValid("repeat repeat repeat repeat repeat repeat repeat repeat"));

        validator = new CssBackgroundValidator("background-image");
        Assert.assertFalse(validator.isValid("url(something.png) url(something2.png)"));

        validator = new CssBackgroundValidator("background-clip");
        Assert.assertFalse(validator.isValid("content-box padding-box"));

        validator = new CssBackgroundValidator("background-origin");
        Assert.assertFalse(validator.isValid("content-box padding-box"));

        validator = new CssBackgroundValidator("background-attachment");
        Assert.assertFalse(validator.isValid("fixed scroll"));
    }

    @Test
    public void multiValuesAllowedForThisValueTest() {
        ICssDataTypeValidator validator = new CssBackgroundValidator("background-repeat");
        Assert.assertTrue(validator.isValid("repeat no-repeat"));

        Assert.assertFalse(validator.isValid("repeat-x repeat"));
        Assert.assertFalse(validator.isValid("repeat-y no-repeat"));

        validator = new CssBackgroundValidator("background-size");
        Assert.assertTrue(validator.isValid("5px 5px"));

        Assert.assertFalse(validator.isValid("contain 5px"));
        Assert.assertFalse(validator.isValid("cover 10%"));
    }

    @Test
    public void severalValuesTest() {
        final ICssDataTypeValidator validator = new CssBackgroundValidator("background-image");
        Assert.assertTrue(validator.isValid("url(img.png),url(img2.png),url(img3.jpg)"));
        Assert.assertTrue(validator.isValid("url(img.png),none,url(img3.jpg)"));
        Assert.assertTrue(validator.isValid("linear-gradient(red, red, red),url(img2.png),url(img3.jpg)"));
    }
}
