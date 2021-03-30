/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2021 iText Group NV
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
package com.itextpdf.io.font.otf;

import com.itextpdf.io.source.RandomAccessFileOrArray;
import com.itextpdf.io.source.RandomAccessSourceFactory;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

import java.io.IOException;
import java.util.List;

@Category(UnitTest.class)
public class OtfReadCommonTest extends ExtendedITextTest {
    private static final String RESOURCE_FOLDER = "./src/test/resources/com/itextpdf/io/font/otf/OtfReadCommonTest/";

    @Test
    public void testReadCoverageFormat1() throws IOException {
        // Based on Example 5 from the specification
        // https://docs.microsoft.com/en-us/typography/opentype/spec/chapter2
        // 0001 0005 0038 003B 0041 1042 A04A
        String path = RESOURCE_FOLDER + "coverage-format-1.bin";
        RandomAccessFileOrArray rf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createBestSource(path));
        List<Integer> glyphIds = OtfReadCommon.readCoverageFormat(rf, 0);
        Assert.assertEquals(5, glyphIds.size());
        Assert.assertEquals(0x38, (int) glyphIds.get(0));
        Assert.assertEquals(0x3B, (int) glyphIds.get(1));
        Assert.assertEquals(0x41, (int) glyphIds.get(2));
        Assert.assertEquals(0x1042, (int) glyphIds.get(3));
        Assert.assertEquals(0xA04A, (int) glyphIds.get(4));
    }

    @Test
    public void testReadCoverageFormat2() throws IOException {
        // Based on Example 6 from the specification
        // https://docs.microsoft.com/en-us/typography/opentype/spec/chapter2
        // 0002 0001 A04E A057 0000
        String path = RESOURCE_FOLDER + "coverage-format-2.bin";
        RandomAccessFileOrArray rf = new RandomAccessFileOrArray(new RandomAccessSourceFactory().createBestSource(path));
        List<Integer> glyphIds = OtfReadCommon.readCoverageFormat(rf, 0);
        Assert.assertEquals(10, glyphIds.size());
        Assert.assertEquals(0xA04E, (int) glyphIds.get(0));
        Assert.assertEquals(0xA057, (int) glyphIds.get(9));
    }
}
