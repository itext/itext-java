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
package com.itextpdf.kernel.colors.gradients;

import com.itextpdf.kernel.colors.gradients.GradientColorStop.HintOffsetType;
import com.itextpdf.kernel.colors.gradients.GradientColorStop.OffsetType;
import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class GradientColorStopTest extends ExtendedITextTest {

    @Test
    public void normalizationTest() {
        GradientColorStop stopToTest = new GradientColorStop(new float[]{-0.5f, 1.5f, 0.5f, 0.5f}, 1.5, OffsetType.AUTO).setHint(1.5, HintOffsetType.NONE);
        Assert.assertArrayEquals(new float[]{0f, 1f, 0.5f}, stopToTest.getRgbArray(), 1e-10f);
        Assert.assertEquals(0, stopToTest.getOffset(), 1e-10);
        Assert.assertEquals(OffsetType.AUTO, stopToTest.getOffsetType());
        Assert.assertEquals(0, stopToTest.getHintOffset(), 1e-10);
        Assert.assertEquals(HintOffsetType.NONE, stopToTest.getHintOffsetType());
    }

    @Test
    public void cornerCasesTest() {
        GradientColorStop stopToTest = new GradientColorStop((float[]) null, 1.5, OffsetType.AUTO).setHint(1.5, HintOffsetType.NONE);
        Assert.assertArrayEquals(new float[]{0f, 0f, 0f}, stopToTest.getRgbArray(), 1e-10f);
        Assert.assertEquals(0, stopToTest.getOffset(), 1e-10);
        Assert.assertEquals(OffsetType.AUTO, stopToTest.getOffsetType());
        Assert.assertEquals(0, stopToTest.getHintOffset(), 1e-10);
        Assert.assertEquals(HintOffsetType.NONE, stopToTest.getHintOffsetType());
    }
}
