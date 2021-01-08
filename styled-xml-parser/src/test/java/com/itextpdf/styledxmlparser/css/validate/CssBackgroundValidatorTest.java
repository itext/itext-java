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
        final ICssDataTypeValidator positionValidator = new CssBackgroundValidator("background-position-x");
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
    public void checkMultiValuePositionXYTest() {
        ICssDataTypeValidator positionValidator = new CssBackgroundValidator("background-position-x");
        Assert.assertFalse(positionValidator.isValid("50px left"));
        Assert.assertFalse(positionValidator.isValid("50px bottom"));
        Assert.assertFalse(positionValidator.isValid("center 50pt"));
        Assert.assertFalse(positionValidator.isValid("50px 50pt"));
        Assert.assertFalse(positionValidator.isValid("left right"));
        Assert.assertFalse(positionValidator.isValid("bottom"));

        Assert.assertTrue(positionValidator.isValid("left 10pt"));
        Assert.assertTrue(positionValidator.isValid("center"));

        positionValidator = new CssBackgroundValidator("background-position-y");
        Assert.assertTrue(positionValidator.isValid("bottom 10pt"));
        Assert.assertTrue(positionValidator.isValid("10pt"));

        Assert.assertFalse(positionValidator.isValid("right"));

        final ICssDataTypeValidator notPositionValidator = new CssBackgroundValidator("background-size");
        Assert.assertTrue(notPositionValidator.isValid("10px 15pt"));
    }

    @Test
    public void multiValuesAllowedForThisTypeTest() {
        ICssDataTypeValidator validator = new CssBackgroundValidator("background-size");
        Assert.assertTrue(validator.isValid("5px 10%"));

        validator = new CssBackgroundValidator("background-position-x");
        Assert.assertTrue(validator.isValid("left 10px"));
        Assert.assertFalse(validator.isValid("5px 10%"));
        Assert.assertFalse(validator.isValid("left left left left left"));

        validator = new CssBackgroundValidator("background-position-y");
        Assert.assertTrue(validator.isValid("bottom 10px"));
        Assert.assertFalse(validator.isValid("5px 10%"));
        Assert.assertFalse(validator.isValid("bottom bottom bottom bottom"));

        validator = new CssBackgroundValidator("background-repeat");
        Assert.assertTrue(validator.isValid("repeat round"));
        Assert.assertFalse(validator.isValid("repeat-x repeat"));

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
        Assert.assertTrue(validator.isValid("round space"));
        Assert.assertTrue(validator.isValid("no-repeat space"));
        Assert.assertTrue(validator.isValid("round repeat"));
        Assert.assertTrue(validator.isValid("space repeat"));

        Assert.assertFalse(validator.isValid("repeat-x repeat"));
        Assert.assertFalse(validator.isValid("repeat-y no-repeat"));
        Assert.assertFalse(validator.isValid("round repeat-x"));
        Assert.assertFalse(validator.isValid("space repeat-x"));

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
