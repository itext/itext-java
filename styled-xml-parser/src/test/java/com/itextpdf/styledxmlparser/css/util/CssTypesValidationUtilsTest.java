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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssTypesValidationUtilsTest extends ExtendedITextTest {
    @Test
    public void testIsAngleCorrectValues() {
        Assertions.assertTrue(CssTypesValidationUtils.isAngleValue("10deg"));
        Assertions.assertTrue(CssTypesValidationUtils.isAngleValue("-20grad"));
        Assertions.assertTrue(CssTypesValidationUtils.isAngleValue("30.5rad"));
        Assertions.assertTrue(CssTypesValidationUtils.isAngleValue("0rad"));
    }

    @Test
    public void testIsAngleNullValue() {
        Assertions.assertFalse(CssTypesValidationUtils.isAngleValue(null));
    }

    @Test
    public void testIsAngleIncorrectValues() {
        Assertions.assertFalse(CssTypesValidationUtils.isAngleValue("deg"));
        Assertions.assertFalse(CssTypesValidationUtils.isAngleValue("-20,6grad"));
        Assertions.assertFalse(CssTypesValidationUtils.isAngleValue("0"));
        Assertions.assertFalse(CssTypesValidationUtils.isAngleValue("10in"));
        Assertions.assertFalse(CssTypesValidationUtils.isAngleValue("10px"));
    }

    @Test
    public void validateMetricValue() {
        Assertions.assertTrue(CssTypesValidationUtils.isMetricValue("1px"));
        Assertions.assertTrue(CssTypesValidationUtils.isMetricValue("1in"));
        Assertions.assertTrue(CssTypesValidationUtils.isMetricValue("1cm"));
        Assertions.assertTrue(CssTypesValidationUtils.isMetricValue("1mm"));
        Assertions.assertTrue(CssTypesValidationUtils.isMetricValue("1pc"));
        Assertions.assertFalse(CssTypesValidationUtils.isMetricValue("1em"));
        Assertions.assertFalse(CssTypesValidationUtils.isMetricValue("1rem"));
        Assertions.assertFalse(CssTypesValidationUtils.isMetricValue("1ex"));
        Assertions.assertTrue(CssTypesValidationUtils.isMetricValue("1pt"));
        Assertions.assertFalse(CssTypesValidationUtils.isMetricValue("1inch"));
        Assertions.assertFalse(CssTypesValidationUtils.isMetricValue("+1m"));
    }

    @Test
    public void isNegativeValueTest() {
        // Invalid values
        Assertions.assertFalse(CssTypesValidationUtils.isNegativeValue(null));
        Assertions.assertFalse(CssTypesValidationUtils.isNegativeValue("-..23"));
        Assertions.assertFalse(CssTypesValidationUtils.isNegativeValue("12 34"));
        Assertions.assertFalse(CssTypesValidationUtils.isNegativeValue("12reeem"));

        // Valid not negative values
        Assertions.assertFalse(CssTypesValidationUtils.isNegativeValue(".23"));
        Assertions.assertFalse(CssTypesValidationUtils.isNegativeValue("+123"));
        Assertions.assertFalse(CssTypesValidationUtils.isNegativeValue("57%"));
        Assertions.assertFalse(CssTypesValidationUtils.isNegativeValue("3.7em"));

        // Valid negative values
        Assertions.assertTrue(CssTypesValidationUtils.isNegativeValue("-1.7rem"));
        Assertions.assertTrue(CssTypesValidationUtils.isNegativeValue("-43.56%"));
        Assertions.assertTrue(CssTypesValidationUtils.isNegativeValue("-12"));
        Assertions.assertTrue(CssTypesValidationUtils.isNegativeValue("-0.123"));
        Assertions.assertTrue(CssTypesValidationUtils.isNegativeValue("-.34"));
    }

    @Test
    public void validateNumericValue() {
        Assertions.assertTrue(CssTypesValidationUtils.isNumber("1"));
        Assertions.assertTrue(CssTypesValidationUtils.isNumber("12"));
        Assertions.assertTrue(CssTypesValidationUtils.isNumber("1.2"));
        Assertions.assertTrue(CssTypesValidationUtils.isNumber(".12"));
        Assertions.assertFalse(CssTypesValidationUtils.isNumber("12f"));
        Assertions.assertFalse(CssTypesValidationUtils.isNumber("f1.2"));
        Assertions.assertFalse(CssTypesValidationUtils.isNumber(".12f"));
    }

    @Test
    public void validateIntegerNumericValue() {
        Assertions.assertTrue(CssTypesValidationUtils.isIntegerNumber("1"));
        Assertions.assertTrue(CssTypesValidationUtils.isIntegerNumber("+12"));
        Assertions.assertTrue(CssTypesValidationUtils.isIntegerNumber("-12"));
        Assertions.assertFalse(CssTypesValidationUtils.isIntegerNumber(".12"));
        Assertions.assertFalse(CssTypesValidationUtils.isIntegerNumber("1.2"));
        Assertions.assertFalse(CssTypesValidationUtils.isIntegerNumber("1,2"));
        Assertions.assertFalse(CssTypesValidationUtils.isIntegerNumber("12f"));
        Assertions.assertFalse(CssTypesValidationUtils.isIntegerNumber("f1.2"));
        Assertions.assertFalse(CssTypesValidationUtils.isIntegerNumber(".12f"));
    }

    @Test
    public void testSpacesBeforeUnitTypes() {
        Assertions.assertFalse(CssTypesValidationUtils.isAngleValue("10 deg"));
        Assertions.assertFalse(CssTypesValidationUtils.isEmValue("10 em"));
        Assertions.assertFalse(CssTypesValidationUtils.isExValue("10 ex"));
        Assertions.assertFalse(CssTypesValidationUtils.isRelativeValue("10 %"));
        Assertions.assertFalse(CssTypesValidationUtils.isRemValue("10 rem"));
        Assertions.assertFalse(CssTypesValidationUtils.isMetricValue("10 px"));
        Assertions.assertFalse(CssTypesValidationUtils.isPercentageValue("10 %"));
    }

    @Test
    public void testSpacesAfterUnitTypes() {
        Assertions.assertTrue(CssTypesValidationUtils.isAngleValue("10deg "));
        Assertions.assertTrue(CssTypesValidationUtils.isEmValue("10em "));
        Assertions.assertTrue(CssTypesValidationUtils.isExValue("10ex "));
        Assertions.assertTrue(CssTypesValidationUtils.isRelativeValue("10% "));
        Assertions.assertTrue(CssTypesValidationUtils.isRemValue("10rem "));
        Assertions.assertTrue(CssTypesValidationUtils.isMetricValue("10px "));
        Assertions.assertTrue(CssTypesValidationUtils.isPercentageValue("10% "));
    }

    @Test
    public void isBase64Test() {
        String base64String = "data:image/jpeg;base64,/9j/aGVsbG8gd29ybGQ=";
        boolean isBase64Data = CssTypesValidationUtils.isBase64Data(base64String);
        boolean isInlineData = CssTypesValidationUtils.isInlineData(base64String);
        Assertions.assertTrue(isBase64Data);
        Assertions.assertTrue(isInlineData);
    }
}
