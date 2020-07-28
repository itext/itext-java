/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2020 iText Group NV
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
package com.itextpdf.test;

import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.text.MessageFormat;

@Category(UnitTest.class)
public class LoggerHelperTest extends ExtendedITextTest {

    @Test
    public void equalsMessageByTemplate() {
        String pattern = "There might be a message: {0}";
        String example = MessageFormat.format(pattern, "message");
        Assert.assertTrue(LoggerHelper.equalsMessageByTemplate(example, pattern));
    }

    @Test
    public void equalsMessageByTemplateWithEmptyParameter() {
        String pattern = "There might be a message: {0}";
        String example = MessageFormat.format(pattern, "message");
        Assert.assertTrue(LoggerHelper.equalsMessageByTemplate(example, pattern));
    }

    @Test
    public void equalsMessageByTemplateWithMultipleParameters() {
        String pattern = "There might be messages: {0} {1}";
        String example = MessageFormat.format(pattern, "message1", "message2");
        Assert.assertTrue(LoggerHelper.equalsMessageByTemplate(example, pattern));
    }

    @Test
    public void equalsMessageByTemplateWithQuotes() {
        String pattern = "There might be a message '': {0}";
        String example = "There might be a message ': message";
        Assert.assertTrue(LoggerHelper.equalsMessageByTemplate(example, pattern));
    }

    @Test
    public void equalsMessageByTemplateWithCyrillic() {
        String pattern = "There might be a cyrillic message: {0}";
        String example = MessageFormat.format(pattern, "сообщение");
        Assert.assertTrue(LoggerHelper.equalsMessageByTemplate(example, pattern));
    }

    @Test
    public void equalsMessageByTemplateWithAsterisks() {
        String pattern = "some text * *** {0}";
        String example = MessageFormat.format(pattern, "message");
        Assert.assertTrue(LoggerHelper.equalsMessageByTemplate(example, pattern));
    }

    @Test
    public void equalsMessageByTemplateWithBrackets() {
        String pattern = "some text ( ) (0) ( {0}";
        String example = MessageFormat.format(pattern, "message");
        Assert.assertTrue(LoggerHelper.equalsMessageByTemplate(example, pattern));
    }

    @Test
    public void equalsMessageByTemplateWithSquareBrackets() {
        String pattern = "some text [ ] [0] [ {0}";
        String example = MessageFormat.format(pattern, "message");
        Assert.assertTrue(LoggerHelper.equalsMessageByTemplate(example, pattern));
    }

    @Test
    public void equalsMessageByTemplateWithQuestionSign() {
        String pattern = "some text ? ??? .*? {0}";
        String example = MessageFormat.format(pattern, "message");
        Assert.assertTrue(LoggerHelper.equalsMessageByTemplate(example, pattern));
    }

    @Test
    public void equalsMessageByTemplateWithDot() {
        String pattern = "some text . ... .* {0}";
        String example = MessageFormat.format(pattern, "message");
        Assert.assertTrue(LoggerHelper.equalsMessageByTemplate(example, pattern));
    }

    @Test
    public void equalsMessageByTemplateWithBraces() {
        String pattern = "some text {} {a} { {0}";
        String example = "some text {} {a} { message";
        Assert.assertTrue(LoggerHelper.equalsMessageByTemplate(example, pattern));
    }

    @Test
    public void equalsComplicatedMessageByTemplate() {
        String pattern = "Not supported list style type ? {a} [b] . * (not working) {0}";
        String example = "Not supported list style type ? {a} [b] . * (not working) *some phrase instead of template*";
        Assert.assertTrue(LoggerHelper.equalsMessageByTemplate(example, pattern));
    }

    @Test
    public void notEqualsMessageByTemplate() {
        String pattern = "There might be a message: {0}";
        String example = "There should be a message: message";
        Assert.assertFalse(LoggerHelper.equalsMessageByTemplate(example, pattern));
    }
}
