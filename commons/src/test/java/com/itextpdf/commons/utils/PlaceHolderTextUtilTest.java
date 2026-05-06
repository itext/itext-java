/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
        assertEquals(getExpectedPlaceHolderTextByCharacters(amountOfCharacters), result);
    }


    @Test
    public void GetPlaceHolderByCharactersTextOverflow() {
        int amountOfCharacters = PlaceHolderTextUtil.TEMPLATE.length() + 24;
        String result = PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.CHARACTERS, amountOfCharacters);
        assertEquals(amountOfCharacters, result.length());
        assertEquals(getExpectedPlaceHolderTextByCharacters(amountOfCharacters), result);
        assertTrue(result.endsWith(getExpectedPlaceHolderTextByCharacters(24)));
    }

    @Test
    public void GetPlaceHolderByWordsTextSimple() {
        int amountOfWords = 5;
        String result = PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords);
        assertEquals(getExpectedPlaceHolderTextByWords(amountOfWords), result);
    }


    @Test
    public void GetPlaceHolderByWordsTextOverflow() {
        int amountOfWords = PlaceHolderTextUtil.TEMPLATE.split(" ").length + 5;
        String result = PlaceHolderTextUtil.getPlaceHolderText(PlaceHolderTextBy.WORDS, amountOfWords);
        assertEquals(getExpectedPlaceHolderTextByWords(amountOfWords), result);
        assertTrue(result.endsWith(getExpectedPlaceHolderTextByWords(5)));
    }

    private static String getExpectedPlaceHolderTextByWords(int amount) {
        final String[] words = PlaceHolderTextUtil.TEMPLATE.split(" ");
        final StringBuilder sb = new StringBuilder(amount * 5);
        for (int i = 0; i < amount; i++) {
            sb.append(words[i % words.length]);
            if (i + 1 == amount) {
                break;
            }
            sb.append(' ');
        }
        return sb.toString();
    }

    private static String getExpectedPlaceHolderTextByCharacters(int amount) {
        final String template = PlaceHolderTextUtil.TEMPLATE;
        final StringBuilder sb = new StringBuilder(amount);
        for (int i = 0; i < amount; i++) {
            sb.append(template.charAt(i % template.length()));
        }
        return sb.toString();
    }


}
