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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("UnitTest")
class LotlFetchingPropertiesTest extends ExtendedITextTest {

    @Test
    public void testAddCountryName() {
        LotlFetchingProperties properties = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        properties.setCountryNames("BE");
        assertTrue(properties.shouldProcessCountry("BE"));
        assertFalse(properties.shouldProcessCountry("NL"));
    }

    @Test
    public void addIgnoredCountryName() {
        LotlFetchingProperties properties = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        properties.setCountryNamesToIgnore("BE");
        assertFalse(properties.shouldProcessCountry("BE"));
        assertTrue(properties.shouldProcessCountry("NL"));
    }

    @Test
    public void byDefaultShouldProcessCountryReturnsTrue() {
        LotlFetchingProperties properties = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        assertTrue(properties.shouldProcessCountry("BE"));
        assertTrue(properties.shouldProcessCountry("NL"));
    }

    @Test
    public void byDefaultShouldProcessCountryReturnsTrueEvenIfItsNotACountry() {
        LotlFetchingProperties properties = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        assertTrue(properties.shouldProcessCountry("INVALID"));
    }


    @Test
    public void tryAddingBothCountryAndIgnoredCountryThrowsException() {
        LotlFetchingProperties properties = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        properties.setCountryNames("BE");
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            properties.setCountryNamesToIgnore("NL");
        });
        Assertions.assertEquals(SignExceptionMessageConstant.EITHER_USE_SCHEMA_NAME_OR_IGNORE_SCHEMA_NAME,
                e.getMessage());
    }

    @Test
    public void tryAddingCountryNameToIgnoreAndCountryNameThrowsException() {
        LotlFetchingProperties properties = new LotlFetchingProperties(new RemoveOnFailingCountryData());
        properties.setCountryNamesToIgnore("BE");
        Exception e = Assertions.assertThrows(IllegalArgumentException.class, () -> {
            properties.setCountryNames("NL");
        });
        Assertions.assertEquals(SignExceptionMessageConstant.EITHER_USE_SCHEMA_NAME_OR_IGNORE_SCHEMA_NAME,
                e.getMessage());
    }

}