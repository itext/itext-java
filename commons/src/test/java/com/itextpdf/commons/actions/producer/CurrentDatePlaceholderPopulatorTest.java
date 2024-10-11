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
package com.itextpdf.commons.actions.producer;

import com.itextpdf.commons.exceptions.CommonsExceptionMessageConstant;
import com.itextpdf.commons.utils.DateTimeUtil;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.test.AssertUtil;
import com.itextpdf.test.ExtendedITextTest;

import java.util.Date;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class CurrentDatePlaceholderPopulatorTest extends ExtendedITextTest {
    private final CurrentDatePlaceholderPopulator populator = new CurrentDatePlaceholderPopulator();

    @Test
    public void nullTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> populator.populate(null, null));
        Assertions.assertEquals(MessageFormatUtil.format(CommonsExceptionMessageConstant.INVALID_USAGE_FORMAT_REQUIRED, "currentDate"),
                exception.getMessage());
    }

    @Test
    public void plainTextTest() {
        String result = populator.populate(null, "'plain text'");
        Assertions.assertEquals("plain text", result);
    }

    @Test
    public void plainTextWithIgnoredBackSlashesTest() {
        String result = populator.populate(null, "'\\p\\l\\a\\i\\n \\t\\e\\x\\t'");
        Assertions.assertEquals("plain text", result);
    }

    @Test
    public void plainTextWithEscapedBackSlashesTest() {
        String result = populator.populate(null, "'plain\\\\text'");
        Assertions.assertEquals("plain\\text", result);
    }

    @Test
    public void plainTextWithEscapedApostrophesTest() {
        String result = populator.populate(null, "'plain\\'text'");
        Assertions.assertEquals("plain'text", result);
    }

    @Test
    public void plainTextSeveralQuotedStringsTest() {
        String result = populator.populate(null, "'plain'' ''text'");
        Assertions.assertEquals("plain text", result);
    }

    @Test
    public void plainTextWithUnquotedCharactersTest() {
        String result = populator.populate(null, "'plain text'$$$");
        Assertions.assertEquals("plain text$$$", result);
    }

    @Test
    public void plainTextEndlessQuotationErrorTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> populator.populate(null, "'plain text"));
        Assertions.assertEquals(CommonsExceptionMessageConstant.PATTERN_CONTAINS_OPEN_QUOTATION,
                exception.getMessage());
    }

    @Test
    public void plainTextMultipleQuotationsEndlessQuotationErrorTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> populator.populate(null, "'plain'' ''text"));
        Assertions.assertEquals(CommonsExceptionMessageConstant.PATTERN_CONTAINS_OPEN_QUOTATION,
                exception.getMessage());
    }

    @Test
    public void plainTextEscapedApostropheEndlessQuotationErrorTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> populator.populate(null, "'plain text\\'"));
        Assertions.assertEquals(CommonsExceptionMessageConstant.PATTERN_CONTAINS_OPEN_QUOTATION,
                exception.getMessage());
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
        Assertions.assertEquals(expectedResult, result);
    }

    @Test
    public void unexpectedLetterComponentTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> populator.populate(null, "dd MM tyy yyyy HH"));
        Assertions.assertEquals(MessageFormatUtil.format(CommonsExceptionMessageConstant.PATTERN_CONTAINS_UNEXPECTED_COMPONENT, "t"),
                exception.getMessage());
    }

    @Test
    public void unexpectedLongComponentTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> populator.populate(null, "dd MMMMM yy yyyy HH"));
        Assertions.assertEquals(MessageFormatUtil.format(CommonsExceptionMessageConstant.PATTERN_CONTAINS_UNEXPECTED_COMPONENT, "MMMMM"),
                exception.getMessage());
    }

    @Test
    public void unexpectedShortComponentTest() {
        Exception exception = Assertions.assertThrows(IllegalArgumentException.class,
                () -> populator.populate(null, "dd MM y yyyy HH"));
        Assertions.assertEquals(MessageFormatUtil.format(CommonsExceptionMessageConstant.PATTERN_CONTAINS_UNEXPECTED_COMPONENT, "y"),
                exception.getMessage());
    }
}
