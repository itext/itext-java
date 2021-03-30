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

import com.itextpdf.styledxmlparser.css.validate.impl.datatype.ArrayDataTypeValidator;
import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssBlendModeValidator;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ArrayDataTypeValidatorTest extends ExtendedITextTest {

    @Test
    public void nullValueTest() {
        final ICssDataTypeValidator validator = new ArrayDataTypeValidator(new TestValidator());
        Assert.assertFalse(validator.isValid(null));
    }

    @Test
    public void normalValueTest() {
        final ICssDataTypeValidator validator = new ArrayDataTypeValidator(new TestValidator());
        Assert.assertTrue(validator.isValid(""));
        Assert.assertTrue(validator.isValid("test"));
        Assert.assertTrue(validator.isValid("test is, test, ttest"));
        Assert.assertTrue(validator.isValid("(te,st)"));
        Assert.assertTrue(validator.isValid("(te,st), test,"));
    }

    @Test
    public void invalidValuesTest() {
        final ICssDataTypeValidator validator = new ArrayDataTypeValidator(new TestValidator());
        Assert.assertFalse(validator.isValid("norma"));
        Assert.assertFalse(validator.isValid("te,st"));
        Assert.assertFalse(validator.isValid("(te,st),, test,"));
    }

    private static class TestValidator implements ICssDataTypeValidator {

        @Override
        public boolean isValid(String objectString) {
            return objectString.contains("test") || objectString.contains("te,st");
        }
    }
}
