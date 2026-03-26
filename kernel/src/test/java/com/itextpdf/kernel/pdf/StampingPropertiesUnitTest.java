/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

@Tag("UnitTest")
public class StampingPropertiesUnitTest extends ExtendedITextTest {

    @Test
    public void basicStampingPropertiesTest() {
        StampingProperties stampingProperties = new StampingProperties();

        Assertions.assertFalse(stampingProperties.appendMode);
        Assertions.assertFalse(stampingProperties.disableMac);
        Assertions.assertFalse(stampingProperties.preserveEncryption);

        stampingProperties.useAppendMode();
        stampingProperties.disableMac();
        stampingProperties.preserveEncryption();

        Assertions.assertTrue(stampingProperties.appendMode);
        Assertions.assertTrue(stampingProperties.disableMac);
        Assertions.assertTrue(stampingProperties.preserveEncryption);
    }

    @Test
    public void copiedStampingPropertiesTest() {
        StampingProperties stampingProperties = new StampingProperties();
        stampingProperties.useAppendMode();
        stampingProperties.disableMac();
        stampingProperties.preserveEncryption();

        StampingProperties copiedProperties = new StampingProperties(stampingProperties);

        Assertions.assertTrue(copiedProperties.appendMode);
        Assertions.assertTrue(copiedProperties.disableMac);
        Assertions.assertTrue(copiedProperties.preserveEncryption);
    }
}
