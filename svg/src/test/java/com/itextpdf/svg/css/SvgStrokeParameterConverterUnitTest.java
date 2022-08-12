package com.itextpdf.svg.css;

import com.itextpdf.svg.css.SvgStrokeParameterConverter.PdfLineDashParameters;
import com.itextpdf.svg.logs.SvgLogMessageConstant;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.LogMessage;
import com.itextpdf.test.annotations.LogMessages;

import org.junit.Assert;
import org.junit.Test;

public class SvgStrokeParameterConverterUnitTest extends ExtendedITextTest {

    @Test
    @LogMessages(messages = {
            @LogMessage(messageTemplate =
                    SvgLogMessageConstant.PERCENTAGE_VALUES_IN_STROKE_DASHARRAY_ARE_NOT_SUPPORTED)})
    public void testStrokeDashArrayPercentsAreNotSupported() {
        Assert.assertNull(SvgStrokeParameterConverter.convertStrokeDashArray("5,3%"));
    }

    @Test
    public void testStrokeDashArrayOddNumberOfValues() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashArray("5pt");
        Assert.assertNotNull(result);
        Assert.assertEquals(0, result.getPhase(), 0);
        Assert.assertArrayEquals(new float[] {5, 5}, result.getLengths(), 1e-5f);
    }

    @Test
    public void testEmptyStrokeDashArray() {
        PdfLineDashParameters result = SvgStrokeParameterConverter.convertStrokeDashArray("");
        Assert.assertNull(result);
    }

}
