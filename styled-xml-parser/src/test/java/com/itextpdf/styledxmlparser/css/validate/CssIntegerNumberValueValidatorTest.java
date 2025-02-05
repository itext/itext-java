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

import com.itextpdf.styledxmlparser.css.validate.impl.datatype.CssIntegerNumberValueValidator;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssIntegerNumberValueValidatorTest extends ExtendedITextTest {
    @Test
    public void zeroValueTest() {
        final ICssDataTypeValidator validator1 = new CssIntegerNumberValueValidator(false, true);
        Assertions.assertTrue(validator1.isValid("0"));
        Assertions.assertTrue(validator1.isValid("+0"));

        final ICssDataTypeValidator validator2 = new CssIntegerNumberValueValidator(false, false);
        Assertions.assertFalse(validator2.isValid("0"));

        final ICssDataTypeValidator validator3 = new CssIntegerNumberValueValidator(true, true);
        Assertions.assertTrue(validator3.isValid("0"));
        Assertions.assertTrue(validator3.isValid("-0"));

        final ICssDataTypeValidator validator4 = new CssIntegerNumberValueValidator(true, false);
        Assertions.assertFalse(validator4.isValid("0"));
    }

    @Test
    public void correctValueTest() {
        final ICssDataTypeValidator validator1 = new CssIntegerNumberValueValidator(false, true);
        Assertions.assertTrue(validator1.isValid("123"));
        Assertions.assertTrue(validator1.isValid("+123"));
        Assertions.assertFalse(validator1.isValid("1.23"));

        final ICssDataTypeValidator validator2 = new CssIntegerNumberValueValidator(false, false);
        Assertions.assertFalse(validator2.isValid("-123"));
        Assertions.assertFalse(validator2.isValid("-1.23"));

        final ICssDataTypeValidator validator3 = new CssIntegerNumberValueValidator(true, true);
        Assertions.assertTrue(validator3.isValid("-123"));
        Assertions.assertTrue(validator3.isValid("-123"));
        Assertions.assertFalse(validator3.isValid("-1.23"));

        final ICssDataTypeValidator validator4 = new CssIntegerNumberValueValidator(true, false);
        Assertions.assertFalse(validator4.isValid("0"));
    }

    @Test
    public void nullValueTest() {
        final ICssDataTypeValidator validator1 = new CssIntegerNumberValueValidator(false, false);
        Assertions.assertFalse(validator1.isValid(null));

        final ICssDataTypeValidator validator2 = new CssIntegerNumberValueValidator(true, true);
        Assertions.assertFalse(validator2.isValid(null));

        final ICssDataTypeValidator validator3 = new CssIntegerNumberValueValidator(false, true);
        Assertions.assertFalse(validator3.isValid(null));

        final ICssDataTypeValidator validator4 = new CssIntegerNumberValueValidator(false, true);
        Assertions.assertFalse(validator4.isValid(null));
    }

    @Test
    public void initialInheritUnsetValuesTest() {
        final ICssDataTypeValidator validator1 = new CssIntegerNumberValueValidator(false, true);
        Assertions.assertTrue(validator1.isValid("initial"));
        Assertions.assertTrue(validator1.isValid("inherit"));
        Assertions.assertTrue(validator1.isValid("unset"));

        final ICssDataTypeValidator validator2 = new CssIntegerNumberValueValidator(true, false);
        Assertions.assertTrue(validator2.isValid("initial"));
        Assertions.assertTrue(validator2.isValid("inherit"));
        Assertions.assertTrue(validator2.isValid("unset"));
    }

    @Test
    public void normalValueTest() {
        final ICssDataTypeValidator validator1 = new CssIntegerNumberValueValidator(false, true);
        Assertions.assertFalse(validator1.isValid("normal"));

        final ICssDataTypeValidator validator2 = new CssIntegerNumberValueValidator(true, false);
        Assertions.assertFalse(validator2.isValid("normal"));
    }

    @Test
    public void invalidValuesTest() {
        final ICssDataTypeValidator validator1 = new CssIntegerNumberValueValidator(false, true);
        Assertions.assertFalse(validator1.isValid(""));
        Assertions.assertFalse(validator1.isValid("dja"));
        Assertions.assertFalse(validator1.isValid("5pixels"));

        final ICssDataTypeValidator validator2 = new CssIntegerNumberValueValidator(true, false);
        Assertions.assertFalse(validator2.isValid(""));
        Assertions.assertFalse(validator2.isValid("dja"));
        Assertions.assertFalse(validator2.isValid("5pixels"));
    }

    @Test
    public void absoluteValuesTest() {
        final ICssDataTypeValidator validator1 = new CssIntegerNumberValueValidator(false, true);
        Assertions.assertTrue(validator1.isValid("12"));
        Assertions.assertFalse(validator1.isValid("12pt"));
        Assertions.assertFalse(validator1.isValid("-12pt"));
        Assertions.assertFalse(validator1.isValid("12px"));
        Assertions.assertFalse(validator1.isValid("12in"));
        Assertions.assertFalse(validator1.isValid("12cm"));
        Assertions.assertFalse(validator1.isValid("12mm"));
        Assertions.assertFalse(validator1.isValid("12pc"));
        Assertions.assertFalse(validator1.isValid("12q"));
        Assertions.assertFalse(validator1.isValid("12 pt"));

        final ICssDataTypeValidator validator2 = new CssIntegerNumberValueValidator(true, false);
        Assertions.assertTrue(validator2.isValid("12"));
        Assertions.assertFalse(validator2.isValid("12pt"));
        Assertions.assertFalse(validator2.isValid("-12pt"));
        Assertions.assertFalse(validator2.isValid("12px"));
        Assertions.assertFalse(validator2.isValid("12in"));
        Assertions.assertFalse(validator2.isValid("12cm"));
        Assertions.assertFalse(validator2.isValid("12mm"));
        Assertions.assertFalse(validator2.isValid("12pc"));
        Assertions.assertFalse(validator2.isValid("12q"));
        Assertions.assertFalse(validator2.isValid("12 pt"));

        final ICssDataTypeValidator validator3 = new CssIntegerNumberValueValidator(true, true);
        Assertions.assertTrue(validator3.isValid("12"));
        Assertions.assertFalse(validator3.isValid("12pt"));
        Assertions.assertFalse(validator3.isValid("-12pt"));
        Assertions.assertFalse(validator3.isValid("12px"));
        Assertions.assertFalse(validator3.isValid("12in"));
        Assertions.assertFalse(validator3.isValid("12cm"));
        Assertions.assertFalse(validator3.isValid("12mm"));
        Assertions.assertFalse(validator3.isValid("12pc"));
        Assertions.assertFalse(validator3.isValid("12q"));
        Assertions.assertFalse(validator3.isValid("12 pt"));
    }

    @Test
    public void relativeValuesTest() {
        final ICssDataTypeValidator validator1 = new CssIntegerNumberValueValidator(false, true);
        Assertions.assertFalse(validator1.isValid("12em"));
        Assertions.assertFalse(validator1.isValid("-12em"));
        Assertions.assertFalse(validator1.isValid("12rem"));
        Assertions.assertFalse(validator1.isValid("12ex"));
        Assertions.assertFalse(validator1.isValid("12 em"));

        final ICssDataTypeValidator validator2 = new CssIntegerNumberValueValidator(true, false);
        Assertions.assertFalse(validator2.isValid("12em"));
        Assertions.assertFalse(validator2.isValid("-12em"));
        Assertions.assertFalse(validator2.isValid("12rem"));
        Assertions.assertFalse(validator2.isValid("12ex"));
        Assertions.assertFalse(validator2.isValid("12 em"));
    }

    @Test
    public void percentValueTest() {
        final ICssDataTypeValidator validator1 = new CssIntegerNumberValueValidator(false, true);
        Assertions.assertFalse(validator1.isValid("12%"));
        Assertions.assertFalse(validator1.isValid("-12%"));
        final ICssDataTypeValidator validator2 = new CssIntegerNumberValueValidator(true, false);
        Assertions.assertFalse(validator2.isValid("12%"));
        Assertions.assertFalse(validator2.isValid("-12%"));
    }
}
