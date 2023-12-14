/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
package com.itextpdf.io.util;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.util.regex.Pattern;

/**
 * At the moment there is no StringUtil class in Java, but there is one in C# and we are testing
 */
@Category(UnitTest.class)
public class StringUtilTest extends ExtendedITextTest {

    @Test
    public void patternSplitTest01() {
        // Pattern.split in Java works differently compared to Regex.Split in C#
        // In C#, empty strings are possible at the beginning of the resultant array for non-capturing groups in split regex
        // Thus, in C# we use a separate utility for splitting to align the implementation with Java
        // This test verifies that the resultant behavior is the same
        Pattern pattern = Pattern.compile("(?=[ab])");
        String source = "a01aa78ab89b";
        String[] expected = new String[] {"a01", "a", "a78", "a", "b89", "b"};
        String[] result = pattern.split(source);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void patternSplitTest02() {
        Pattern pattern = Pattern.compile("(?=[ab])");
        String source = "";
        String[] expected = new String[] {""};
        String[] result = pattern.split(source);
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void stringSplitTest01() {
        String source = "a01aa78ab89b";
        String[] expected = new String[] {"a01", "a", "a78", "a", "b89", "b"};
        String[] result = source.split("(?=[ab])");
        Assert.assertArrayEquals(expected, result);
    }

    @Test
    public void stringSplitTest02() {
        String source = "";
        String[] expected = new String[] {""};
        String[] result = source.split("(?=[ab])");
        Assert.assertArrayEquals(expected, result);
    }

}
