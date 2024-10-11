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
package com.itextpdf.commons.utils;

import com.itextpdf.test.ExtendedITextTest;

import java.util.Arrays;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

@Tag("UnitTest")
public class MessageFormatUtilTest extends ExtendedITextTest {

    public static Iterable<Object[]> dataSource() {
        return Arrays.asList(new Object[][]{
                {"Plain message with params 1 test", "Plain message with params {0} {1}", new Object[]{1, "test"}, "test with simple params"},
                {"Message with 'single quotes'", "Message with 'single quotes'", new Object[0], "test with single quotes"},
                {"Message with ''doubled single quotes''", "Message with ''doubled single quotes''", new Object[0], "test with doubled single quotes"},
                {"Message with {curly braces} and a parameter {I'm between curly braces too}", "Message with {{curly braces}} and a parameter {{{0}}}", new Object[]{"I'm between curly braces too"}, "Test with curly braces"},
                {"'{value}'", "'{{{0}}}'", new Object[]{"value"}, "Mix om multiple brackets and quotes 1"},
                {"'value'", "'{0}'", new Object[]{"value"}, "Mix of brackets and quotes"},
                {"{'0'}", "{{'0'}}", new Object[0], "Mix of multiple brackets and quotes 2"},
                {"single opening brace {0 test", "single opening brace {{0 test", new Object[0], "Test single opening brace"},
                {"single closing  brace 0} test", "single closing  brace 0}} test", new Object[0], "Test single closing brace"},
                {"single opening + closing  brace {  test  }", "single opening + closing  brace {{  {0}  }}", new Object[]{"test"}, "Test single opening and closing brace"},
        });
    }

    @ParameterizedTest(name = "{index}: {3} format: {1}; {0}")
    @MethodSource("dataSource")
    public void testFormatting(String expectedResult, String pattern, Object[] arguments, String name) {
        Assertions.assertEquals(expectedResult, MessageFormatUtil.format(pattern, arguments));
    }
}
