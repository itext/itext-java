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
package com.itextpdf.commons.utils;

import com.itextpdf.test.ExtendedITextTest;

import java.awt.SystemTray;
import java.io.Console;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Stack;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class MessageFormatUtilTest extends ExtendedITextTest {

    private String expectedResult;
    private String pattern;
    private Object[] arguments;


    public MessageFormatUtilTest(Object expectedResult, Object pattern, Object arguments, Object name) {
        this.expectedResult = (String)expectedResult;
        this.pattern = (String)pattern;
        this.arguments = (Object[]) arguments;
    }


    @Parameterized.Parameters(name = "{index}: {3} format: {1}; {0}")
    public static Iterable<Object[]> dataSource() {
        return Arrays.asList(new Object[][]{
                {"Plain message with params 1 test", "Plain message with params {0} {1}", new Object[] {1, "test"},"test with simple params"},
                {"Message with 'single quotes'", "Message with 'single quotes'", new Object[0],"test with single quotes"},
                {"Message with ''doubled single quotes''", "Message with ''doubled single quotes''", new Object[0],"test with doubled single quotes"},
                {"Message with {curly braces} and a parameter {I'm between curly braces too}", "Message with {{curly braces}} and a parameter {{{0}}}", new Object[]{"I'm between curly braces too"},"Test with curly braces"},
                {"Message with {{multiple curly braces}}", "Message with {{{{multiple curly braces}}}}", new Object[]{},"Test with multiple curly braces"},
                {"Message with {Value between brackets} and {{Value between double brackets}}", "Message with {{{0}}} and {{{{{1}}}}}", new Object[]{"Value between brackets", "Value between double brackets"},"Test with multiple curly braces"},
                {"Lets go wild 'value 1', {value 2}, '{value 3}', {'{value 4}'}", "Lets go wild '{0}', {{{1}}}, '{{{2}}}', {{'{{{3}}}'}}", new Object[]{"value 1", "value 2","value 3","value 4"},"Some of all"},
                {"{'{value}'}", "{{'{{{0}}}'}}", new Object[]{"value"},"Mix om multiple brackets and quotes 1"},
                {"'{value}'", "'{{{0}}}'", new Object[]{"value"},"Mix om multiple brackets and quotes 1"},
                {"a '{'{123}'}''' b", "a '{{'{{{0}}}'}}''' b",  new Object[]{123},"Mix om multiple brackets and quotes 1"},
        });
    }

    @Test
    public void testFormatting() {
        Assert.assertEquals(expectedResult, MessageFormatUtil.format(pattern, arguments));
    }

}