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
package com.itextpdf.signatures.validation;

import com.itextpdf.test.ExtendedITextTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.*;

@Tag("IntegrationTest")
public class EuropeanTrustedListConfigurationFactoryTest extends ExtendedITextTest {

    @Test
    public void getFactory() {
        EuropeanTrustedListConfigurationFactory factory = EuropeanTrustedListConfigurationFactory.getFactory().get();
        assertNotNull(factory, "Factory should not be null");
        assertTrue(factory instanceof LoadFromModuleEuropeanTrustedListConfigurationFactory,
                "Factory should be an instance of LoadFromModuleEuropeanTrustedListConfigurationFactory");
    }

    @Test
    public void setFactoryNull() {
        Exception e = assertThrows(IllegalArgumentException.class, () -> {
            EuropeanTrustedListConfigurationFactory.setFactory(null);
        });
        Assertions.assertNotNull(e.getMessage());
    }

    @Test
    public void setFactory() {
        EuropeanTrustedListConfigurationFactory factory = EuropeanTrustedListConfigurationFactory.getFactory().get();
        Supplier<EuropeanTrustedListConfigurationFactory> supplier = () -> factory;
        EuropeanTrustedListConfigurationFactory.setFactory(supplier);
        Assertions.assertNotNull(EuropeanTrustedListConfigurationFactory.getFactory(),
                "Factory should not be null after setting it with a supplier");
    }
}