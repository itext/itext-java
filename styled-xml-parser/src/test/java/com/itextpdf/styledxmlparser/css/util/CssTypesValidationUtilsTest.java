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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssTypesValidationUtilsTest extends ExtendedITextTest {
    @Test
    public void testIsAngleCorrectValues() {
        Assert.assertTrue(CssTypesValidationUtils.isAngleValue("10deg"));
        Assert.assertTrue(CssTypesValidationUtils.isAngleValue("-20grad"));
        Assert.assertTrue(CssTypesValidationUtils.isAngleValue("30.5rad"));
        Assert.assertTrue(CssTypesValidationUtils.isAngleValue("0rad"));
    }

    @Test
    public void testIsAngleNullValue() {
        Assert.assertFalse(CssTypesValidationUtils.isAngleValue(null));
    }

    @Test
    public void testIsAngleIncorrectValues() {
        Assert.assertFalse(CssTypesValidationUtils.isAngleValue("deg"));
        Assert.assertFalse(CssTypesValidationUtils.isAngleValue("-20,6grad"));
        Assert.assertFalse(CssTypesValidationUtils.isAngleValue("0"));
        Assert.assertFalse(CssTypesValidationUtils.isAngleValue("10in"));
        Assert.assertFalse(CssTypesValidationUtils.isAngleValue("10px"));
    }

    @Test
    public void validateMetricValue() {
        Assert.assertTrue(CssTypesValidationUtils.isMetricValue("1px"));
        Assert.assertTrue(CssTypesValidationUtils.isMetricValue("1in"));
        Assert.assertTrue(CssTypesValidationUtils.isMetricValue("1cm"));
        Assert.assertTrue(CssTypesValidationUtils.isMetricValue("1mm"));
        Assert.assertTrue(CssTypesValidationUtils.isMetricValue("1pc"));
        Assert.assertFalse(CssTypesValidationUtils.isMetricValue("1em"));
        Assert.assertFalse(CssTypesValidationUtils.isMetricValue("1rem"));
        Assert.assertFalse(CssTypesValidationUtils.isMetricValue("1ex"));
        Assert.assertTrue(CssTypesValidationUtils.isMetricValue("1pt"));
        Assert.assertFalse(CssTypesValidationUtils.isMetricValue("1inch"));
        Assert.assertFalse(CssTypesValidationUtils.isMetricValue("+1m"));
    }

    @Test
    public void isNegativeValueTest() {
        // Invalid values
        Assert.assertFalse(CssTypesValidationUtils.isNegativeValue(null));
        Assert.assertFalse(CssTypesValidationUtils.isNegativeValue("-..23"));
        Assert.assertFalse(CssTypesValidationUtils.isNegativeValue("12 34"));
        Assert.assertFalse(CssTypesValidationUtils.isNegativeValue("12reeem"));

        // Valid not negative values
        Assert.assertFalse(CssTypesValidationUtils.isNegativeValue(".23"));
        Assert.assertFalse(CssTypesValidationUtils.isNegativeValue("+123"));
        Assert.assertFalse(CssTypesValidationUtils.isNegativeValue("57%"));
        Assert.assertFalse(CssTypesValidationUtils.isNegativeValue("3.7em"));

        // Valid negative values
        Assert.assertTrue(CssTypesValidationUtils.isNegativeValue("-1.7rem"));
        Assert.assertTrue(CssTypesValidationUtils.isNegativeValue("-43.56%"));
        Assert.assertTrue(CssTypesValidationUtils.isNegativeValue("-12"));
        Assert.assertTrue(CssTypesValidationUtils.isNegativeValue("-0.123"));
        Assert.assertTrue(CssTypesValidationUtils.isNegativeValue("-.34"));
    }

    @Test
    public void validateNumericValue() {
        Assert.assertTrue(CssTypesValidationUtils.isNumericValue("1"));
        Assert.assertTrue(CssTypesValidationUtils.isNumericValue("12"));
        Assert.assertTrue(CssTypesValidationUtils.isNumericValue("1.2"));
        Assert.assertTrue(CssTypesValidationUtils.isNumericValue(".12"));
        Assert.assertFalse(CssTypesValidationUtils.isNumericValue("12f"));
        Assert.assertFalse(CssTypesValidationUtils.isNumericValue("f1.2"));
        Assert.assertFalse(CssTypesValidationUtils.isNumericValue(".12f"));
    }

    @Test
    public void testSpacesBeforeUnitTypes() {
        Assert.assertFalse(CssTypesValidationUtils.isAngleValue("10 deg"));
        Assert.assertFalse(CssTypesValidationUtils.isEmValue("10 em"));
        Assert.assertFalse(CssTypesValidationUtils.isExValue("10 ex"));
        Assert.assertFalse(CssTypesValidationUtils.isRelativeValue("10 %"));
        Assert.assertFalse(CssTypesValidationUtils.isRemValue("10 rem"));
        Assert.assertFalse(CssTypesValidationUtils.isMetricValue("10 px"));
        Assert.assertFalse(CssTypesValidationUtils.isPercentageValue("10 %"));
    }

    @Test
    public void testSpacesAfterUnitTypes() {
        Assert.assertTrue(CssTypesValidationUtils.isAngleValue("10deg "));
        Assert.assertTrue(CssTypesValidationUtils.isEmValue("10em "));
        Assert.assertTrue(CssTypesValidationUtils.isExValue("10ex "));
        Assert.assertTrue(CssTypesValidationUtils.isRelativeValue("10% "));
        Assert.assertTrue(CssTypesValidationUtils.isRemValue("10rem "));
        Assert.assertTrue(CssTypesValidationUtils.isMetricValue("10px "));
        Assert.assertTrue(CssTypesValidationUtils.isPercentageValue("10% "));
    }
}
