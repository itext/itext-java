package com.itextpdf.styledxmlparser.css.util;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class CssPropertyNormalizerTest {

    @Test
    public void testUrlNormalizationSimple() {
        test("url('data:image/png;base64,iVBORw0K')", "url('data:image/png;base64,iVBORw0K')");
    }

    @Test
    // Test is initially added to ensure equal behavior between Java and C#.
    // The behavior itself might be reconsidered in the future. Browsers do not forgive newlines in base64 expressions
    public void testUrlNormalizationLineTerminators() {
        test("url(data:image/png;base64,iVBOR\nw0K)", "url(data:image/png;base64,iVBOR\nw0K)");
        test("url(data:image/png;base64,iVBOR\rw0K)", "url(data:image/png;base64,iVBOR\rw0K)");
        test("url(data:image/png;base64,iVBOR\r\nw0K)", "url(data:image/png;base64,iVBOR\r\nw0K)");
    }

    private void test(String input, String expectedOutput) {
        String result = CssPropertyNormalizer.normalize(input);
        Assert.assertEquals(expectedOutput, result);
    }

}
