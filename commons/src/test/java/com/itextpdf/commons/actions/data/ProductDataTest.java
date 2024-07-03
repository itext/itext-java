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
package com.itextpdf.commons.actions.data;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ProductDataTest extends ExtendedITextTest {
    @Test
    public void productDataCreationTest() {
        ProductData productData = new ProductData("publicProductName", "productName", "1.2", 1900, 2100);

        Assertions.assertEquals("publicProductName", productData.getPublicProductName());
        Assertions.assertEquals("productName", productData.getProductName());
        Assertions.assertEquals("1.2", productData.getVersion());
        Assertions.assertEquals(1900, productData.getSinceCopyrightYear());
        Assertions.assertEquals(2100, productData.getToCopyrightYear());
    }

    @Test
    public void productDataAnotherCreationTest() {
        ProductData productData = new ProductData("publicProductName", "productName", "1.2", "4.0.0", 1900, 2100);

        Assertions.assertEquals("publicProductName", productData.getPublicProductName());
        Assertions.assertEquals("productName", productData.getProductName());
        Assertions.assertEquals("1.2", productData.getVersion());
        Assertions.assertEquals("4.0.0", productData.getMinCompatibleLicensingModuleVersion());
        Assertions.assertEquals(1900, productData.getSinceCopyrightYear());
        Assertions.assertEquals(2100, productData.getToCopyrightYear());
    }

    @Test
    public void equalsTest() {
        ProductData a = new ProductData("publicProductName", "productName", "1.2", 1900, 2100);
        ProductData b = new ProductData("publicProductName", "productName", "1.2", 1900, 2100);

        Assertions.assertEquals(a, a);

        Assertions.assertEquals(a, b);
        Assertions.assertEquals(b, a);
    }

    @Test
    public void notEqualsTest() {
        ProductData a = new ProductData("publicProductName", "productName", "1.2", 1900, 2100);
        ProductData d = new ProductData("publicProductName", "productName", "1.2", 1910, 2110);

        Assertions.assertNotEquals(a, d);
    }

    @Test
    public void hashCodeTest() {
        ProductData a = new ProductData("publicProductName", "productName", "1.2", 1900, 2100);
        ProductData b = new ProductData("publicProductName", "productName", "1.2", 1900, 2100);

        Assertions.assertEquals(a, b);
        Assertions.assertEquals(a.hashCode(), b.hashCode());

        int h1 = a.hashCode();
        int h2 = a.hashCode();
        Assertions.assertEquals(h1, h2);
    }
}
