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
package com.itextpdf.pdfua.checkers.utils;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class BCP47ValidatorTest extends ExtendedITextTest {

    @Test
    public void simpleLanguageSubtagTest() {
        Assertions.assertTrue(BCP47Validator.validate("de"));
        Assertions.assertTrue(BCP47Validator.validate("fr"));
        //example of a grandfathered tag
        Assertions.assertTrue(BCP47Validator.validate("i-enochian"));
    }

    @Test
    public void languageSubtagAndScriptSubtagTest() {
        //Chinese written using the Traditional Chinese script
        Assertions.assertTrue(BCP47Validator.validate("zh-Hant"));
        //Chinese written using the Simplified Chinese script
        Assertions.assertTrue(BCP47Validator.validate("zh-Hans"));
        //Serbian written using the Cyrillic script
        Assertions.assertTrue(BCP47Validator.validate("sr-Cyrl"));
        //Serbian written using the Latin script
        Assertions.assertTrue(BCP47Validator.validate("sr-Latn"));
    }

    @Test
    public void extLangSubtagsAndPrimaryLangSubtagsTest() {
        //Chinese, Mandarin, Simplified script, as used in China
        Assertions.assertTrue(BCP47Validator.validate("zh-cmn-Hans-CN"));
        //Mandarin Chinese, Simplified script, as used in China
        Assertions.assertTrue(BCP47Validator.validate("cmn-Hans-CN"));
        //Chinese, Cantonese, as used in Hong Kong SAR
        Assertions.assertTrue(BCP47Validator.validate("zh-yue-HK"));
        Assertions.assertTrue(BCP47Validator.validate("sr-Latn"));
    }

    @Test
    public void languageScriptRegionsTest() {
        //Chinese written using the Simplified script as used in mainland China
        Assertions.assertTrue(BCP47Validator.validate("zh-Hans-CN"));
        //Serbian written using the Latin script as used in Serbia
        Assertions.assertTrue(BCP47Validator.validate("sr-Latn-RS"));
    }

    @Test
    public void languageVariantTest() {
        //Resian dialect of Slovenian
        Assertions.assertTrue(BCP47Validator.validate("sl-rozaj"));
        //San Giorgio dialect of Resian dialect of Slovenian
        Assertions.assertTrue(BCP47Validator.validate("sl-rozaj-biske"));
        //Nadiza dialect of Slovenian
        Assertions.assertTrue(BCP47Validator.validate("sl-nedis"));
    }

    @Test
    public void languageRegionVariantTest() {
        //German as used in Switzerland using the 1901 variant [orthography]
        Assertions.assertTrue(BCP47Validator.validate("de-CH-1901"));
        //Slovenian as used in Italy, Nadiza dialect
        Assertions.assertTrue(BCP47Validator.validate("sl-IT-nedis"));
    }

    @Test
    public void languageScriptRegionVariantTest() {
        //Eastern Armenian written in Latin script, as used in Italy
        Assertions.assertTrue(BCP47Validator.validate("hy-Latn-IT-arevela"));
    }

    @Test
    public void languageRegionTest() {
        //German for Germany
        Assertions.assertTrue(BCP47Validator.validate("de-DE"));
        //English as used in the United States
        Assertions.assertTrue(BCP47Validator.validate("en-US"));
        //Spanish appropriate for the Latin America and Caribbean region using the UN region code
        Assertions.assertTrue(BCP47Validator.validate("es-419"));
        //Invalid, two region tags
        Assertions.assertFalse(BCP47Validator.validate("de-419-DE"));
        //use of a single-character subtag in primary position; note
        //that there are a few grandfathered tags that start with "i-" that
        //are valid
        Assertions.assertFalse(BCP47Validator.validate("a-DE"));
    }

    @Test
    public void privateUseSubtagsTest() {
        Assertions.assertTrue(BCP47Validator.validate("de-CH-x-phonebk"));
        Assertions.assertTrue(BCP47Validator.validate("az-Arab-x-AZE-derbend"));
    }

    @Test
    public void privateUseRegistryValuesTest() {
        //private use using the singleton 'x'
        Assertions.assertTrue(BCP47Validator.validate("x-whatever"));
        //all private tags
        Assertions.assertTrue(BCP47Validator.validate("qaa-Qaaa-QM-x-southern"));
        //German, with a private script
        Assertions.assertTrue(BCP47Validator.validate("de-Qaaa"));
        //Serbian, Latin script, private region
        Assertions.assertTrue(BCP47Validator.validate("sr-Latn-QM"));
        //Serbian, private script, for Serbia
        Assertions.assertTrue(BCP47Validator.validate("sr-Qaaa-RS"));
    }

    @Test
    public void tagsWithExtensions() {
        Assertions.assertTrue(BCP47Validator.validate("en-US-u-islamcal"));
        Assertions.assertTrue(BCP47Validator.validate("zh-CN-a-myext-x-private"));
        Assertions.assertTrue(BCP47Validator.validate("en-a-myext-b-another"));
    }
}
