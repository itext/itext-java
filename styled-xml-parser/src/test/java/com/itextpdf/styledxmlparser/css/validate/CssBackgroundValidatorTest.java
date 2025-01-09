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

import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssBackgroundValidator;
import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssBackgroundValidatorTest extends ExtendedITextTest {

    @Test
    public void nullValueTest() {
        final ICssDataTypeValidator validator = new CssBackgroundValidator("any property");
        Assertions.assertFalse(validator.isValid(null));
    }

    @Test
    public void undefinedValueTest() {
        final ICssDataTypeValidator validator = new CssBackgroundValidator("undefined");
        Assertions.assertFalse(validator.isValid("ja"));
    }

    @Test
    public void initialInheritUnsetValueTest() {
        final ICssDataTypeValidator validator = new CssBackgroundValidator("any property");
        Assertions.assertTrue(validator.isValid("initial"));
        Assertions.assertTrue(validator.isValid("inherit"));
        Assertions.assertTrue(validator.isValid("unset"));
    }

    @Test
    public void emptyValueTest() {
        final ICssDataTypeValidator validator = new CssBackgroundValidator("any property");
        Assertions.assertFalse(validator.isValid(""));
    }

    @Test
    public void propertyValueCorrespondsPropertyTypeTest() {
        ICssDataTypeValidator validator = new CssBackgroundValidator("background-repeat");
        Assertions.assertTrue(validator.isValid("repeat-x"));
        Assertions.assertFalse(validator.isValid("cover"));

        validator = new CssBackgroundValidator("background-image");
        Assertions.assertTrue(validator.isValid("url(something.png)"));
        Assertions.assertFalse(validator.isValid("5px"));

        validator = new CssBackgroundValidator("background-attachment");
        Assertions.assertTrue(validator.isValid("fixed"));
        Assertions.assertFalse(validator.isValid("5px"));
    }

    @Test
    public void propertyValueWithMultiTypesCorrespondsPropertyTypeTest() {
        final ICssDataTypeValidator positionValidator = new CssBackgroundValidator("background-position-x");
        final ICssDataTypeValidator sizeValidator = new CssBackgroundValidator("background-size");
        Assertions.assertTrue(positionValidator.isValid("5px"));
        Assertions.assertTrue(sizeValidator.isValid("5px"));
        Assertions.assertTrue(positionValidator.isValid("5%"));
        Assertions.assertTrue(sizeValidator.isValid("5%"));
        Assertions.assertTrue(positionValidator.isValid("left"));
        Assertions.assertFalse(sizeValidator.isValid("left"));
        Assertions.assertFalse(positionValidator.isValid("contain"));
        Assertions.assertTrue(sizeValidator.isValid("contain"));

        final ICssDataTypeValidator originValidator = new CssBackgroundValidator("background-origin");
        final ICssDataTypeValidator clipValidator = new CssBackgroundValidator("background-clip");
        Assertions.assertTrue(originValidator.isValid("border-box"));
        Assertions.assertTrue(clipValidator.isValid("border-box"));
        Assertions.assertTrue(originValidator.isValid("padding-box"));
        Assertions.assertTrue(clipValidator.isValid("padding-box"));
        Assertions.assertTrue(originValidator.isValid("content-box"));
        Assertions.assertTrue(clipValidator.isValid("content-box"));
    }

    @Test
    public void checkMultiValuePositionXYTest() {
        ICssDataTypeValidator positionValidator = new CssBackgroundValidator("background-position-x");
        Assertions.assertFalse(positionValidator.isValid("50px left"));
        Assertions.assertFalse(positionValidator.isValid("50px bottom"));
        Assertions.assertFalse(positionValidator.isValid("center 50pt"));
        Assertions.assertFalse(positionValidator.isValid("50px 50pt"));
        Assertions.assertFalse(positionValidator.isValid("left right"));
        Assertions.assertFalse(positionValidator.isValid("bottom"));

        Assertions.assertTrue(positionValidator.isValid("left 10pt"));
        Assertions.assertTrue(positionValidator.isValid("center"));

        positionValidator = new CssBackgroundValidator("background-position-y");
        Assertions.assertTrue(positionValidator.isValid("bottom 10pt"));
        Assertions.assertTrue(positionValidator.isValid("10pt"));

        Assertions.assertFalse(positionValidator.isValid("right"));

        final ICssDataTypeValidator notPositionValidator = new CssBackgroundValidator("background-size");
        Assertions.assertTrue(notPositionValidator.isValid("10px 15pt"));
    }

    @Test
    public void multiValuesAllowedForThisTypeTest() {
        ICssDataTypeValidator validator = new CssBackgroundValidator("background-size");
        Assertions.assertTrue(validator.isValid("5px 10%"));

        validator = new CssBackgroundValidator("background-position-x");
        Assertions.assertTrue(validator.isValid("left 10px"));
        Assertions.assertFalse(validator.isValid("5px 10%"));
        Assertions.assertFalse(validator.isValid("left left left left left"));

        validator = new CssBackgroundValidator("background-position-y");
        Assertions.assertTrue(validator.isValid("bottom 10px"));
        Assertions.assertFalse(validator.isValid("5px 10%"));
        Assertions.assertFalse(validator.isValid("bottom bottom bottom bottom"));

        validator = new CssBackgroundValidator("background-repeat");
        Assertions.assertTrue(validator.isValid("repeat round"));
        Assertions.assertFalse(validator.isValid("repeat-x repeat"));

        validator = new CssBackgroundValidator("background-image");
        Assertions.assertFalse(validator.isValid("url(something.png) url(something2.png)"));

        validator = new CssBackgroundValidator("background-clip");
        Assertions.assertFalse(validator.isValid("content-box padding-box"));

        validator = new CssBackgroundValidator("background-origin");
        Assertions.assertFalse(validator.isValid("content-box padding-box"));

        validator = new CssBackgroundValidator("background-attachment");
        Assertions.assertFalse(validator.isValid("fixed scroll"));
    }

    @Test
    public void multiValuesAllowedForThisValueTest() {
        ICssDataTypeValidator validator = new CssBackgroundValidator("background-repeat");
        Assertions.assertTrue(validator.isValid("repeat no-repeat"));
        Assertions.assertTrue(validator.isValid("round space"));
        Assertions.assertTrue(validator.isValid("no-repeat space"));
        Assertions.assertTrue(validator.isValid("round repeat"));
        Assertions.assertTrue(validator.isValid("space repeat"));

        Assertions.assertFalse(validator.isValid("repeat-x repeat"));
        Assertions.assertFalse(validator.isValid("repeat-y no-repeat"));
        Assertions.assertFalse(validator.isValid("round repeat-x"));
        Assertions.assertFalse(validator.isValid("space repeat-x"));

        validator = new CssBackgroundValidator("background-size");
        Assertions.assertTrue(validator.isValid("5px 5px"));

        Assertions.assertFalse(validator.isValid("contain 5px"));
        Assertions.assertFalse(validator.isValid("cover 10%"));
    }

    @Test
    public void severalValuesTest() {
        final ICssDataTypeValidator validator = new CssBackgroundValidator("background-image");
        Assertions.assertTrue(validator.isValid("url(img.png),url(img2.png),url(img3.jpg)"));
        Assertions.assertTrue(validator.isValid("url(img.png),none,url(img3.jpg)"));
        Assertions.assertTrue(validator.isValid("linear-gradient(red, red, red),url(img2.png),url(img3.jpg)"));
    }
}
