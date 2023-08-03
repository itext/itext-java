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
package com.itextpdf.io;

import com.itextpdf.io.util.ArrayUtil;
import com.itextpdf.io.util.ResourceUtil;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class UtilitiesTest extends ExtendedITextTest {
    @Test
    public void testShortener() {
        byte[] src = new byte[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10};
        byte[] dest = new byte[]{1, 2, 3, 4, 5};
        byte[] test = ArrayUtil.shortenArray(src, 5);

        Assert.assertArrayEquals(dest, test);
    }

    @Test
    public void invalidResource() {
        Assert.assertNull(ResourceUtil.getResourceStream("some-random-resource.zzz"));
    }
}
