/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssLengthValueValidator;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssLengthValueValidatorTest extends ExtendedITextTest {

    @Test
    public void zeroValueTest() {
        final ICssDataTypeValidator validator1 = new CssLengthValueValidator(false);
        Assert.assertTrue(validator1.isValid("0"));
        Assert.assertTrue(validator1.isValid("0px"));
        Assert.assertTrue(validator1.isValid("-0px"));

        final ICssDataTypeValidator validator2 = new CssLengthValueValidator(true);
        Assert.assertTrue(validator2.isValid("0"));
        Assert.assertTrue(validator2.isValid("0px"));
        Assert.assertTrue(validator2.isValid("-0"));

    }

    @Test
    public void nullValueTest() {
        final ICssDataTypeValidator validator1 = new CssLengthValueValidator(false);
        Assert.assertFalse(validator1.isValid(null));

        final ICssDataTypeValidator validator2 = new CssLengthValueValidator(true);
        Assert.assertFalse(validator2.isValid(null));
    }

    @Test
    public void initialInheritUnsetValuesTest() {
        final ICssDataTypeValidator validator1 = new CssLengthValueValidator(false);
        Assert.assertTrue(validator1.isValid("initial"));
        Assert.assertTrue(validator1.isValid("inherit"));
        Assert.assertTrue(validator1.isValid("unset"));

        final ICssDataTypeValidator validator2 = new CssLengthValueValidator(true);
        Assert.assertTrue(validator2.isValid("initial"));
        Assert.assertTrue(validator2.isValid("inherit"));
        Assert.assertTrue(validator2.isValid("unset"));

    }

    @Test
    public void normalValueTest() {
        final ICssDataTypeValidator validator1 = new CssLengthValueValidator(false);
        Assert.assertFalse(validator1.isValid("normal"));

        final ICssDataTypeValidator validator2 = new CssLengthValueValidator(true);
        Assert.assertFalse(validator2.isValid("normal"));
    }

    @Test
    public void invalidValuesTest() {
        final ICssDataTypeValidator validator1 = new CssLengthValueValidator(false);
        Assert.assertFalse(validator1.isValid(""));
        Assert.assertFalse(validator1.isValid("dja"));
        Assert.assertFalse(validator1.isValid("5pixels"));

        final ICssDataTypeValidator validator2 = new CssLengthValueValidator(true);
        Assert.assertFalse(validator2.isValid(""));
        Assert.assertFalse(validator2.isValid("dja"));
        Assert.assertFalse(validator2.isValid("5pixels"));
    }

    @Test
    public void absoluteValuesTest() {
        final ICssDataTypeValidator validator1 = new CssLengthValueValidator(false);
        Assert.assertFalse(validator1.isValid("12"));
        Assert.assertTrue(validator1.isValid("12pt"));
        Assert.assertFalse(validator1.isValid("-12pt"));
        Assert.assertTrue(validator1.isValid("12px"));
        Assert.assertTrue(validator1.isValid("12in"));
        Assert.assertTrue(validator1.isValid("12cm"));
        Assert.assertTrue(validator1.isValid("12mm"));
        Assert.assertTrue(validator1.isValid("12pc"));
        Assert.assertTrue(validator1.isValid("12q"));
        Assert.assertFalse(validator1.isValid("12 pt"));

        final ICssDataTypeValidator validator2 = new CssLengthValueValidator(true);
        Assert.assertFalse(validator2.isValid("12"));
        Assert.assertTrue(validator2.isValid("12pt"));
        Assert.assertTrue(validator2.isValid("-12pt"));
        Assert.assertTrue(validator2.isValid("12px"));
        Assert.assertTrue(validator2.isValid("12in"));
        Assert.assertTrue(validator2.isValid("12cm"));
        Assert.assertTrue(validator2.isValid("12mm"));
        Assert.assertTrue(validator2.isValid("12pc"));
        Assert.assertTrue(validator2.isValid("12q"));
        Assert.assertFalse(validator2.isValid("12 pt"));
    }

    @Test
    public void relativeValuesTest() {
        final ICssDataTypeValidator validator1 = new CssLengthValueValidator(false);
        Assert.assertTrue(validator1.isValid("12em"));
        Assert.assertFalse(validator1.isValid("-12em"));
        Assert.assertTrue(validator1.isValid("12rem"));
        Assert.assertTrue(validator1.isValid("12ex"));
        Assert.assertFalse(validator1.isValid("12 em"));

        final ICssDataTypeValidator validator2 = new CssLengthValueValidator(true);
        Assert.assertTrue(validator2.isValid("12em"));
        Assert.assertTrue(validator2.isValid("-12em"));
        Assert.assertTrue(validator2.isValid("12rem"));
        Assert.assertTrue(validator2.isValid("12ex"));
        Assert.assertFalse(validator2.isValid("12 em"));
    }

    @Test
    public void percentValueTest() {
        final ICssDataTypeValidator validator1 = new CssLengthValueValidator(false);
        Assert.assertFalse(validator1.isValid("12%"));
        Assert.assertFalse(validator1.isValid("-12%"));
        final ICssDataTypeValidator validator2 = new CssLengthValueValidator(true);
        Assert.assertFalse(validator2.isValid("12%"));
        Assert.assertFalse(validator2.isValid("-12%"));
    }
}
