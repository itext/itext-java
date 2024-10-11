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

import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssBlendModeValidator;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssBlendModeValidatorTest extends ExtendedITextTest {

    @Test
    public void nullValueTest() {
        final ICssDataTypeValidator validator = new CssBlendModeValidator();
        Assertions.assertFalse(validator.isValid(null));
    }

    @Test
    public void normalValueTest() {
        final ICssDataTypeValidator validator = new CssBlendModeValidator();
        Assertions.assertTrue(validator.isValid("normal"));
        Assertions.assertTrue(validator.isValid("multiply"));
        Assertions.assertTrue(validator.isValid("screen"));
        Assertions.assertTrue(validator.isValid("overlay"));
        Assertions.assertTrue(validator.isValid("darken"));
        Assertions.assertTrue(validator.isValid("lighten"));
        Assertions.assertTrue(validator.isValid("color-dodge"));
        Assertions.assertTrue(validator.isValid("color-burn"));
        Assertions.assertTrue(validator.isValid("hard-light"));
        Assertions.assertTrue(validator.isValid("soft-light"));
        Assertions.assertTrue(validator.isValid("difference"));
        Assertions.assertTrue(validator.isValid("exclusion"));
        Assertions.assertTrue(validator.isValid("hue"));
        Assertions.assertTrue(validator.isValid("saturation"));
        Assertions.assertTrue(validator.isValid("color"));
        Assertions.assertTrue(validator.isValid("luminosity"));
    }

    @Test
    public void invalidValuesTest() {
        final ICssDataTypeValidator validator = new CssBlendModeValidator();
        Assertions.assertFalse(validator.isValid(""));
        Assertions.assertFalse(validator.isValid("norma"));
        Assertions.assertFalse(validator.isValid("NORMAL"));
    }
}
