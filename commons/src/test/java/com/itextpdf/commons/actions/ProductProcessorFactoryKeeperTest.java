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
package com.itextpdf.commons.actions;

import com.itextpdf.commons.actions.processors.DefaultProductProcessorFactory;
import com.itextpdf.commons.actions.processors.IProductProcessorFactory;
import com.itextpdf.commons.actions.processors.UnderAgplProductProcessorFactory;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class ProductProcessorFactoryKeeperTest extends ExtendedITextTest {

    @AfterEach
    public void afterEach() {
        ProductProcessorFactoryKeeper.restoreDefaultProductProcessorFactory();
    }

    @Test
    public void gettingDefaultFactoryFromKeeper() {
        IProductProcessorFactory productProcessorFactory = ProductProcessorFactoryKeeper.getProductProcessorFactory();
        Assertions.assertTrue(productProcessorFactory instanceof DefaultProductProcessorFactory);
    }

    @Test
    public void restoringDefaultFactory() {
        ProductProcessorFactoryKeeper.setProductProcessorFactory(new UnderAgplProductProcessorFactory());

        Assertions.assertTrue(ProductProcessorFactoryKeeper.getProductProcessorFactory()
                instanceof UnderAgplProductProcessorFactory);
        ProductProcessorFactoryKeeper.restoreDefaultProductProcessorFactory();
        Assertions.assertTrue(ProductProcessorFactoryKeeper.getProductProcessorFactory()
                instanceof DefaultProductProcessorFactory);
    }
}
