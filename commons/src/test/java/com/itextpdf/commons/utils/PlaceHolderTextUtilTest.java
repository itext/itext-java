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
package com.itextpdf.commons.utils;

import com.itextpdf.commons.utils.PlaceHolderTextUtil.PlaceHolderTextBy;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@Tag("UnitTest")
public class PlaceHolderTextUtilTest extends ExtendedITextTest {

    @Test
    public void GetPlaceHolderByCharacterTextSimple() {
        int amountOfCharacters = 24;
        String result = PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.CHARACTERS, amountOfCharacters);
        assertEquals(amountOfCharacters, result.length());
        assertEquals(result, "Portable Document Format");
    }


    @Test
    public void GetPlaceHolderByCharactersTextOverflow() {
        int amountOfCharacters = 31222 + 24;
        String result = PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.CHARACTERS, amountOfCharacters);
        assertEquals(amountOfCharacters, result.length());
        assertTrue(result.endsWith("Portable Document Format"));
    }

    @Test
    public void GetPlaceHolderByWordsTextSimple() {
        int amountOfWords = 5;
        String result = PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords);
        assertEquals(44, result.length());
    }


    @Test
    public void GetPlaceHolderByWordsTextOverflow() {
        int amountOfCharacters = 4000;
        String result = PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfCharacters);
        assertEquals(25472, result.length());
    }

}
