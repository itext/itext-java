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
package com.itextpdf.commons.actions.data;

import com.itextpdf.test.ExtendedITextTest;
import com.itextpdf.test.annotations.type.UnitTest;

import org.junit.Assert;
import org.junit.Test;
import org.junit.experimental.categories.Category;

@Category(UnitTest.class)
public class ProductDataTest extends ExtendedITextTest {
    @Test
    public void productDataCreationTest() {
        ProductData productData = new ProductData("publicProductName", "productName", "1.2", 1900, 2100);

        Assert.assertEquals("publicProductName", productData.getPublicProductName());
        Assert.assertEquals("productName", productData.getProductName());
        Assert.assertEquals("1.2", productData.getVersion());
        Assert.assertEquals(1900, productData.getSinceCopyrightYear());
        Assert.assertEquals(2100, productData.getToCopyrightYear());
    }

    @Test
    public void equalsTest() {
        ProductData a = new ProductData("publicProductName", "productName", "1.2", 1900, 2100);
        ProductData b = new ProductData("publicProductName", "productName", "1.2", 1900, 2100);

        Assert.assertEquals(a, a);

        Assert.assertEquals(a, b);
        Assert.assertEquals(b, a);
    }

    @Test
    public void notEqualsTest() {
        ProductData a = new ProductData("publicProductName", "productName", "1.2", 1900, 2100);
        ProductData d = new ProductData("publicProductName", "productName", "1.2", 1910, 2110);

        Assert.assertNotEquals(a, d);
    }

    @Test
    public void hashCodeTest() {
        ProductData a = new ProductData("publicProductName", "productName", "1.2", 1900, 2100);
        ProductData b = new ProductData("publicProductName", "productName", "1.2", 1900, 2100);

        Assert.assertEquals(a, b);
        Assert.assertEquals(a.hashCode(), b.hashCode());

        int h1 = a.hashCode();
        int h2 = a.hashCode();
        Assert.assertEquals(h1, h2);
    }
}
