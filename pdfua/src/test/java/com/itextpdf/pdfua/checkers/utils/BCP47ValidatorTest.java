package com.itextpdf.pdfua.checkers.utils;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class BCP47ValidatorTest extends ExtendedITextTest {

    @Test
    public void simpleLanguageSubtagTest() {
        Assert.assertTrue(BCP47Validator.validate("de"));
        Assert.assertTrue(BCP47Validator.validate("fr"));
        //example of a grandfathered tag
        Assert.assertTrue(BCP47Validator.validate("i-enochian"));
    }

    @Test
    public void languageSubtagAndScriptSubtagTest() {
        //Chinese written using the Traditional Chinese script
        Assert.assertTrue(BCP47Validator.validate("zh-Hant"));
        //Chinese written using the Simplified Chinese script
        Assert.assertTrue(BCP47Validator.validate("zh-Hans"));
        //Serbian written using the Cyrillic script
        Assert.assertTrue(BCP47Validator.validate("sr-Cyrl"));
        //Serbian written using the Latin script
        Assert.assertTrue(BCP47Validator.validate("sr-Latn"));
    }

    @Test
    public void extLangSubtagsAndPrimaryLangSubtagsTest() {
        //Chinese, Mandarin, Simplified script, as used in China
        Assert.assertTrue(BCP47Validator.validate("zh-cmn-Hans-CN"));
        //Mandarin Chinese, Simplified script, as used in China
        Assert.assertTrue(BCP47Validator.validate("cmn-Hans-CN"));
        //Chinese, Cantonese, as used in Hong Kong SAR
        Assert.assertTrue(BCP47Validator.validate("zh-yue-HK"));
        Assert.assertTrue(BCP47Validator.validate("sr-Latn"));
    }

    @Test
    public void languageScriptRegionsTest() {
        //Chinese written using the Simplified script as used in mainland China
        Assert.assertTrue(BCP47Validator.validate("zh-Hans-CN"));
        //Serbian written using the Latin script as used in Serbia
        Assert.assertTrue(BCP47Validator.validate("sr-Latn-RS"));
    }

    @Test
    public void languageVariantTest() {
        //Resian dialect of Slovenian
        Assert.assertTrue(BCP47Validator.validate("sl-rozaj"));
        //San Giorgio dialect of Resian dialect of Slovenian
        Assert.assertTrue(BCP47Validator.validate("sl-rozaj-biske"));
        //Nadiza dialect of Slovenian
        Assert.assertTrue(BCP47Validator.validate("sl-nedis"));
    }

    @Test
    public void languageRegionVariantTest() {
        //German as used in Switzerland using the 1901 variant [orthography]
        Assert.assertTrue(BCP47Validator.validate("de-CH-1901"));
        //Slovenian as used in Italy, Nadiza dialect
        Assert.assertTrue(BCP47Validator.validate("sl-IT-nedis"));
    }

    @Test
    public void languageScriptRegionVariantTest() {
        //Eastern Armenian written in Latin script, as used in Italy
        Assert.assertTrue(BCP47Validator.validate("hy-Latn-IT-arevela"));
    }

    @Test
    public void languageRegionTest() {
        //German for Germany
        Assert.assertTrue(BCP47Validator.validate("de-DE"));
        //English as used in the United States
        Assert.assertTrue(BCP47Validator.validate("en-US"));
        //Spanish appropriate for the Latin America and Caribbean region using the UN region code
        Assert.assertTrue(BCP47Validator.validate("es-419"));
        //Invalid, two region tags
        Assert.assertFalse(BCP47Validator.validate("de-419-DE"));
        //use of a single-character subtag in primary position; note
        //that there are a few grandfathered tags that start with "i-" that
        //are valid
        Assert.assertFalse(BCP47Validator.validate("a-DE"));
    }

    @Test
    public void privateUseSubtagsTest() {
        Assert.assertTrue(BCP47Validator.validate("de-CH-x-phonebk"));
        Assert.assertTrue(BCP47Validator.validate("az-Arab-x-AZE-derbend"));
    }

    @Test
    public void privateUseRegistryValuesTest() {
        //private use using the singleton 'x'
        Assert.assertTrue(BCP47Validator.validate("x-whatever"));
        //all private tags
        Assert.assertTrue(BCP47Validator.validate("qaa-Qaaa-QM-x-southern"));
        //German, with a private script
        Assert.assertTrue(BCP47Validator.validate("de-Qaaa"));
        //Serbian, Latin script, private region
        Assert.assertTrue(BCP47Validator.validate("sr-Latn-QM"));
        //Serbian, private script, for Serbia
        Assert.assertTrue(BCP47Validator.validate("sr-Qaaa-RS"));
    }

    @Test
    public void tagsWithExtensions() {
        Assert.assertTrue(BCP47Validator.validate("en-US-u-islamcal"));
        Assert.assertTrue(BCP47Validator.validate("zh-CN-a-myext-x-private"));
        Assert.assertTrue(BCP47Validator.validate("en-a-myext-b-another"));
    }
}
