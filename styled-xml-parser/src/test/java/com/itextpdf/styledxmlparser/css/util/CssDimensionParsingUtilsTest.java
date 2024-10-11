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
package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.colors.DeviceCmyk;
import com.itextpdf.kernel.colors.DeviceRgb;
import com.itextpdf.layout.properties.TransparentColor;
import com.itextpdf.styledxmlparser.css.CommonCssConstants;
import com.itextpdf.styledxmlparser.exceptions.StyledXMLParserException;
import com.itextpdf.styledxmlparser.logs.StyledXmlParserLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CssDimensionParsingUtilsTest extends ExtendedITextTest {
    private static final float EPS = 0.0001f;

    @Test
    public void parseAbsoluteFontSizeTest() {
        Assertions.assertEquals(75, CssDimensionParsingUtils.parseAbsoluteFontSize("100", CommonCssConstants.PX), EPS);
        Assertions.assertEquals(75, CssDimensionParsingUtils.parseAbsoluteFontSize("100px"), EPS);
        Assertions.assertEquals(12, CssDimensionParsingUtils.parseAbsoluteFontSize(CommonCssConstants.MEDIUM), EPS);
        Assertions.assertEquals(0, CssDimensionParsingUtils.parseAbsoluteFontSize("", ""), EPS);
    }

    @Test
    public void parseRelativeFontSizeTest() {
        Assertions.assertEquals(120, CssDimensionParsingUtils.parseRelativeFontSize("10em", 12), EPS);
        Assertions.assertEquals(12.5f, CssDimensionParsingUtils.parseRelativeFontSize(CommonCssConstants.SMALLER, 15), EPS);
    }

    @Test
    public void parseResolutionValidDpiUnit() {
        Assertions.assertEquals(10f, CssDimensionParsingUtils.parseResolution("10dpi"), 0);
    }

    @Test
    public void parseResolutionValidDpcmUnit() {
        Assertions.assertEquals(25.4f, CssDimensionParsingUtils.parseResolution("10dpcm"), 0);
    }

    @Test
    public void parseResolutionValidDppxUnit() {
        Assertions.assertEquals(960f, CssDimensionParsingUtils.parseResolution("10dppx"), 0);
    }

    @Test
    public void parseResolutionInvalidUnit() {
        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssDimensionParsingUtils.parseResolution("10incorrectUnit")
        );
        Assertions.assertEquals(StyledXmlParserLogMessageConstant.INCORRECT_RESOLUTION_UNIT_VALUE, e.getMessage());
    }

    @Test
    public void parseInvalidFloat() {
        String value = "invalidFloat";
        try {
            Assertions.assertNull(CssDimensionParsingUtils.parseFloat(value));
        } catch (Exception e){
            Assertions.fail();
        }
    }

    @Test
    public void parseAbsoluteLengthFrom10px() {
        String value = "10px";
        float actual = CssDimensionParsingUtils.parseAbsoluteLength(value, CommonCssConstants.PX);
        float expected = 7.5f;

        Assertions.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthFrom10cm() {
        String value = "10cm";
        float actual = CssDimensionParsingUtils.parseAbsoluteLength(value, CommonCssConstants.CM);
        float expected = 283.46457f;

        Assertions.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthFrom10in() {
        String value = "10in";
        float actual = CssDimensionParsingUtils.parseAbsoluteLength(value, CommonCssConstants.IN);
        float expected = 720.0f;

        Assertions.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthFrom10pt() {
        String value = "10pt";
        float actual = CssDimensionParsingUtils.parseAbsoluteLength(value, CommonCssConstants.PT);
        float expected = 10.0f;

        Assertions.assertEquals(expected, actual, 0);
    }

    @Test
    @LogMessages(messages = {@LogMessage(messageTemplate = StyledXmlParserLogMessageConstant.UNKNOWN_ABSOLUTE_METRIC_LENGTH_PARSED, count = 1)})
    public void parseAbsoluteLengthFromUnknownType() {
        String value = "10pateekes";
        float actual = CssDimensionParsingUtils.parseAbsoluteLength(value, "pateekes");
        float expected = 10.0f;

        Assertions.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseLength() {
        Assertions.assertEquals(9, CssDimensionParsingUtils.parseAbsoluteLength("12"), 0);
        Assertions.assertEquals(576, CssDimensionParsingUtils.parseAbsoluteLength("8inch"), 0);
        Assertions.assertEquals(576, CssDimensionParsingUtils.parseAbsoluteLength("8", CommonCssConstants.IN), 0);
    }

    @Test
    public void parseAbsoluteLengthTest() {
        Assertions.assertEquals(75, CssDimensionParsingUtils.parseAbsoluteLength("100", CommonCssConstants.PX), EPS);
        Assertions.assertEquals(75, CssDimensionParsingUtils.parseAbsoluteLength("100px"), EPS);
    }

    @Test
    public void parseAbsoluteLengthFromNAN() {
        String value = "Definitely not a number";

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssDimensionParsingUtils.parseAbsoluteLength(value)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.NAN, "Definitely not a number"),
                e.getMessage());
    }

    @Test
    public void parseAbsoluteLengthFromNull() {
        String value = null;

        Exception e = Assertions.assertThrows(StyledXMLParserException.class,
                () -> CssDimensionParsingUtils.parseAbsoluteLength(value)
        );
        Assertions.assertEquals(MessageFormatUtil.format(StyledXMLParserException.NAN, "null"), e.getMessage());
    }

    @Test
    public void parseAbsoluteLengthExponentialPtTest() {
        String value = "1e2pt";
        float actual = CssDimensionParsingUtils.parseAbsoluteLength(value);
        float expected = 1e2f;

        Assertions.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthExponentialPxTest() {
        String value = "1e2px";
        float actual = CssDimensionParsingUtils.parseAbsoluteLength(value);
        float expected = 1e2f * 0.75f;

        Assertions.assertEquals(expected, actual, 0);
    }

    @Test
    public void parseAbsoluteLengthExponentialCapitalTest() {
        String value = "1E-4";
        float actual = CssDimensionParsingUtils.parseAbsoluteLength(value);
        float expected = 1e-4f * 0.75f;

        Assertions.assertEquals(expected, actual, 1e-9);
    }

    @Test
    public void parseAbsoluteLength12cmTest() {
        // Calculations in CssUtils#parseAbsoluteLength were changed to work
        // with double values instead of float to improve precision and eliminate
        // the difference between java and .net. So the test verifies this fix.
        Assertions.assertEquals(340.15747f, CssDimensionParsingUtils.parseAbsoluteLength("12cm"), 0f);
    }


    @Test
    public void parseAbsoluteLength12qTest() {
        // Calculations in CssUtils#parseAbsoluteLength were changed to work
        // with double values instead of float to improve precision and eliminate
        // the difference between java and .net. So the test verifies this fix
        Assertions.assertEquals(8.503937f, CssDimensionParsingUtils.parseAbsoluteLength("12q"), 0f);
    }

    @Test
    public void parseDoubleIntegerValueTest(){
        Double expectedString = 5.0;
        Double actualString = CssDimensionParsingUtils.parseDouble("5");

        Assertions.assertEquals(expectedString, actualString);
    }

    @Test
    public void parseDoubleManyCharsAfterDotTest(){
        Double expectedString = 5.123456789;
        Double actualString = CssDimensionParsingUtils.parseDouble("5.123456789");

        Assertions.assertEquals(expectedString, actualString);
    }

    @Test
    public void parseDoubleManyCharsAfterDotNegativeTest(){
        Double expectedString = -5.123456789;
        Double actualString = CssDimensionParsingUtils.parseDouble("-5.123456789");

        Assertions.assertEquals(expectedString, actualString);
    }

    @Test
    public void parseDoubleNullValueTest(){
        Double expectedString = null;
        Double actualString = CssDimensionParsingUtils.parseDouble(null);

        Assertions.assertEquals(expectedString, actualString);
    }

    @Test
    public void parseDoubleNegativeTextTest(){
        Double expectedString = null;
        Double actualString = CssDimensionParsingUtils.parseDouble("text");

        Assertions.assertEquals(expectedString, actualString);
    }

    @Test
    public void parseSimpleDeviceCmykTest(){
        TransparentColor expected = new TransparentColor(new DeviceCmyk(0f, 0.4f, 0.6f, 1f), 1);
        TransparentColor actual = CssDimensionParsingUtils.parseColor("device-cmyk(0 40% 60% 100%)");

        Assertions.assertEquals(expected.getColor(), actual.getColor());
        Assertions.assertEquals(expected.getOpacity(), actual.getOpacity(), 0.0001f);
    }

    @Test
    public void parseDeviceCmykWithOpacityTest(){
        TransparentColor expected = new TransparentColor(new DeviceCmyk(0f, 0.4f, 0.6f, 1f), 0.5f);
        TransparentColor actual = CssDimensionParsingUtils.parseColor("device-cmyk(0 40% 60% 100% / .5)");

        Assertions.assertEquals(expected.getColor(), actual.getColor());
        Assertions.assertEquals(expected.getOpacity(), actual.getOpacity(), 0.0001f);
    }

    @Test
    public void parseDeviceCmykWithFallbackAndOpacityTest(){
        TransparentColor expected = new TransparentColor(new DeviceCmyk(0f, 0.4f, 0.6f, 1f), 0.5f);
        TransparentColor actual = CssDimensionParsingUtils.parseColor("device-cmyk(0 40% 60% 100% / .5 rgb(178 34 34))");

        Assertions.assertEquals(expected.getColor(), actual.getColor());
        Assertions.assertEquals(expected.getOpacity(), actual.getOpacity(), 0.0001f);
    }

    @Test
    public void parseRgbTest(){
        TransparentColor expected = new TransparentColor(new DeviceRgb(255, 255, 128), 1f);
        TransparentColor actual = CssDimensionParsingUtils.parseColor("rgb(255, 255, 128)");

        Assertions.assertEquals(expected.getColor(), actual.getColor());
        Assertions.assertEquals(expected.getOpacity(), actual.getOpacity(), 0.0001f);
    }

    @Test
    public void parseInvalidColorTest(){
        TransparentColor expected = new TransparentColor(new DeviceRgb(0, 0, 0), 1f);
        TransparentColor actual = CssDimensionParsingUtils.parseColor("currentcolor");

        Assertions.assertEquals(expected.getColor(), actual.getColor());
        Assertions.assertEquals(expected.getOpacity(), actual.getOpacity(), 0.0001f);
    }

    @Test
    public void parseLengthAbsoluteTest() {
        float result = CssDimensionParsingUtils.parseLength("10pt", 1, 2, 1, 1);
        Assertions.assertEquals(10, result, 0.0001f);

        result = CssDimensionParsingUtils.parseLength("10px", 1, 1, 2, 1);
        Assertions.assertEquals(7.5, result, 0.0001f);

        result = CssDimensionParsingUtils.parseLength("10in", 1, 1, 2, 1);
        Assertions.assertEquals(720, result, 0.0001f);
    }

    @Test
    public void parseLengthPercentTest() {
        final float result = CssDimensionParsingUtils.parseLength("10%", 10, 2, 1, 1);
        Assertions.assertEquals(1, result, 0.0001f);
    }

    @Test
    public void parseLengthFontTest() {
        float result = CssDimensionParsingUtils.parseLength("10em", 10, 2, 8, 9);
        Assertions.assertEquals(80, result, 0.0001f);

        result = CssDimensionParsingUtils.parseLength("10rem", 10, 2, 8, 9);
        Assertions.assertEquals(90, result, 0.0001f);
    }

    @Test
    public void parseLengthInvalidTest() {
        final float result = CssDimensionParsingUtils.parseLength("10cmm", 10, 2, 8, 9);
        Assertions.assertEquals(2, result, 0.0001f);
    }

    @Test
    public void parseFlexTest() {
        Assertions.assertEquals(13.3f, CssDimensionParsingUtils.parseFlex("13.3fr"), 0.0001);
        Assertions.assertEquals(13.3f, CssDimensionParsingUtils.parseFlex("13.3fr "), 0.0001);
        Assertions.assertEquals(13.3f, CssDimensionParsingUtils.parseFlex(" 13.3fr "), 0.0001);
        Assertions.assertNull(CssDimensionParsingUtils.parseFlex("13.3 fr"));
        Assertions.assertNull(CssDimensionParsingUtils.parseFlex("13.3f"));
        Assertions.assertNull(CssDimensionParsingUtils.parseFlex("13.3"));
        Assertions.assertNull(CssDimensionParsingUtils.parseFlex(null));
    }
}
