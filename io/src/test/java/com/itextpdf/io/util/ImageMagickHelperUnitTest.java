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

@Category(UnitTest.class)
public class ImageMagickHelperUnitTest extends ExtendedITextTest {

    @Test
    public void verifyValidIntegerFuzzValue() {
        String testFuzzValue = "10";

        Assert.assertTrue(ImageMagickHelper.validateFuzziness(testFuzzValue));
    }

    @Test
    public void verifyValidDecimalFuzzValue() {
        String testFuzzValue = "10.5";

        Assert.assertTrue(ImageMagickHelper.validateFuzziness(testFuzzValue));
    }

    @Test
    public void verifyFuzzIntegerValueGT100() {
        String testFuzzValue = "200";

        Assert.assertTrue(ImageMagickHelper.validateFuzziness(testFuzzValue));
    }

    @Test
    public void verifyFuzzDecimalValueGT100() {
        String testFuzzValue = "200.5";

        Assert.assertTrue(ImageMagickHelper.validateFuzziness(testFuzzValue));
    }

    @Test
    public void verifyNegativeIntegerFuzzValue() {
        String testFuzzValue = "-10";

        Assert.assertFalse(ImageMagickHelper.validateFuzziness(testFuzzValue));
    }

    @Test
    public void verifyNegativeDecimalFuzzValue() {
        String testFuzzValue = "-10.5";

        Assert.assertFalse(ImageMagickHelper.validateFuzziness(testFuzzValue));
    }

    @Test
    public void verifyEmptyFuzzValue() {
        String testFuzzValue = "";

        Assert.assertFalse(ImageMagickHelper.validateFuzziness(testFuzzValue));
    }

    @Test
    public void verifyNullFuzzValue() {
        String testFuzzValue = null;

        Assert.assertTrue(ImageMagickHelper.validateFuzziness(testFuzzValue));
    }

    @Test
    public void verifySomeTextInFuzzValue() {
        String testFuzzValue = "10hello";

        Assert.assertFalse(ImageMagickHelper.validateFuzziness(testFuzzValue));
    }
}
