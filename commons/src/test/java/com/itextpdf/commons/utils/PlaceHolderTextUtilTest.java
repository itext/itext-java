package com.itextpdf.commons.utils;

import com.itextpdf.commons.utils.PlaceHolderTextUtil.PlaceHolderTextBy;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Test;
import org.junit.experimental.categories.Category;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


@Category(UnitTest.class)
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