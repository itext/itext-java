/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
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
package com.itextpdf.kernel.pdf;

import com.itextpdf.commons.actions.data.ProductData;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("IntegrationTest")
public class FingerPrintTest extends ExtendedITextTest {

    private ProductData productData;
    private ProductData productData2;
    private ProductData duplicateProductData;

    @BeforeEach
    public void beforeTest() {
        this.productData = new ProductData("pdfProduct", "pdfProduct", "7.0.0", 1900, 2000);
        this.productData2 = new ProductData("pdfProduct2", "pdfProduct2", "7.0.0", 1900, 2000);
        this.duplicateProductData =new ProductData("pdfProduct", "pdfProduct", "7.0.0", 1900, 2000);
    }

    @Test
    public void normalAddTest() {
        FingerPrint fingerPrint = new FingerPrint();
        Assertions.assertTrue(fingerPrint.registerProduct(productData));
        Assertions.assertTrue(fingerPrint.registerProduct(productData2));
        Assertions.assertEquals(2, fingerPrint.getProducts().size());
    }

    @Test
    public void duplicateTest() {
        FingerPrint fingerPrint = new FingerPrint();
        fingerPrint.registerProduct(productData);
        Assertions.assertFalse(fingerPrint.registerProduct(duplicateProductData));
    }

    @Test
    public void disableFingerPrintTest() {
        FingerPrint fingerPrint = new FingerPrint();
        fingerPrint.disableFingerPrint();
        Assertions.assertFalse(fingerPrint.isFingerPrintEnabled());
    }
}

