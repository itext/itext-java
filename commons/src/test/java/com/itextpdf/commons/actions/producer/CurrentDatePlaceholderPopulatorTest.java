/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

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
package com.itextpdf.commons.actions.producer;

import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import java.util.Date;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.ExpectedException;

@Category(UnitTest.class)
public class CurrentDatePlaceholderPopulatorTest extends ExtendedITextTest {
    private final CurrentDatePlaceholderPopulator populator = new CurrentDatePlaceholderPopulator();

    @Rule
    public ExpectedException junitExpectedException = ExpectedException.none();

    @Test
    public void nullTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(CommonsExceptionMessageConstant.INVALID_USAGE_FORMAT_REQUIRED, "currentDate"));

        populator.populate(null, null);
    }

    @Test
    public void plainTextTest() {
        String result = populator.populate(null, "'plain text'");
        Assert.assertEquals("plain text", result);
    }

    @Test
    public void plainTextWithIgnoredBackSlashesTest() {
        String result = populator.populate(null, "'\\p\\l\\a\\i\\n \\t\\e\\x\\t'");
        Assert.assertEquals("plain text", result);
    }

    @Test
    public void plainTextWithEscapedBackSlashesTest() {
        String result = populator.populate(null, "'plain\\\\text'");
        Assert.assertEquals("plain\\text", result);
    }

    @Test
    public void plainTextWithEscapedApostrophesTest() {
        String result = populator.populate(null, "'plain\\'text'");
        Assert.assertEquals("plain'text", result);
    }

    @Test
    public void plainTextSeveralQuotedStringsTest() {
        String result = populator.populate(null, "'plain'' ''text'");
        Assert.assertEquals("plain text", result);
    }

    @Test
    public void plainTextWithUnquotedCharactersTest() {
        String result = populator.populate(null, "'plain text'$$$");
        Assert.assertEquals("plain text$$$", result);
    }

    @Test
    public void plainTextEndlessQuotationErrorTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(CommonsExceptionMessageConstant.PATTERN_CONTAINS_OPEN_QUOTATION);

        populator.populate(null, "'plain text");
    }

    @Test
    public void plainTextMultipleQuotationsEndlessQuotationErrorTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(CommonsExceptionMessageConstant.PATTERN_CONTAINS_OPEN_QUOTATION);

        populator.populate(null, "'plain'' ''text");
    }

    @Test
    public void plainTextEscapedApostropheEndlessQuotationErrorTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(CommonsExceptionMessageConstant.PATTERN_CONTAINS_OPEN_QUOTATION);

        populator.populate(null, "'plain text\\'");
    }

    @Test
    public void validComponentsTest() {
        AssertUtil.doesNotThrow(() -> populator.populate(null, "dd MM MMM MMMM yy yyyy HH mm ss"));
    }

    @Test
    public void validComponentsComparisonTest() {
        // the test may potentially fail if you started it at HH:59:59 so that expected result will
        // be generated at the beginning of the next hour.
        Date date = DateTimeUtil.getCurrentTimeDate();
        String result = populator.populate(null, "dd MM yy yyyy HH");
        String expectedResult = DateTimeUtil.format(date, "dd MM yy yyyy HH");
        Assert.assertEquals(expectedResult, result);
    }

    @Test
    public void unexpectedLetterComponentTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(CommonsExceptionMessageConstant.PATTERN_CONTAINS_UNEXPECTED_COMPONENT, "t"));

        populator.populate(null, "dd MM tyy yyyy HH");
    }

    @Test
    public void unexpectedLongComponentTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(CommonsExceptionMessageConstant.PATTERN_CONTAINS_UNEXPECTED_COMPONENT, "MMMMM"));

        populator.populate(null, "dd MMMMM yy yyyy HH");
    }

    @Test
    public void unexpectedShortComponentTest() {
        junitExpectedException.expect(IllegalArgumentException.class);
        junitExpectedException.expectMessage(MessageFormatUtil.format(CommonsExceptionMessageConstant.PATTERN_CONTAINS_UNEXPECTED_COMPONENT, "y"));

        populator.populate(null, "dd MM y yyyy HH");
    }
}
