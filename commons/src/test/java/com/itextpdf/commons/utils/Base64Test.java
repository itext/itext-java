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
package com.itextpdf.commons.utils;

import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class Base64Test extends ExtendedITextTest {

    @Test
    public void testEncodeObjectForNullObject() {
        Assertions.assertEquals("rO0ABXA=", Base64.encodeObject(null));
    }

    @Test
    public void testDecodeForSourceIsEmptyArray() {
        Assertions.assertEquals("", Base64.encodeBytes(new byte[] {}));
    }

    @Test
    public void testDecodeForSourceIsEmptyArrayGzip() {
        //HEADER        FOOTER          example of signatures that can be generated
        //H4sIAAAAAAAAAAMAAAAAAAAAAAA=	1f 8b 08 00 00 00 00 00 00 00 03 00 00 00 00 00 00 00 00 00 // windows
        //H4sIAAAAAAAA/wMAAAAAAAAAAAA=  1f 8b 08 00 00 00 00 00 00 ff 03 00 00 00 00 00 00 00 00 00 // unknown
        //H4sIAAAAAAAAAwMAAAAAAAAAAAA=	1f 8b 08 00 00 00 00 00 00 03 03 00 00 00 00 00 00 00 00 00 // linux
        //H4sIAAAAAAAAAgMAAAAAAAAAAAA=	1f 8b 08 00 00 00 00 00 00 02 03 00 00 00 00 00 00 00 00 00 // vms
        final String expectedHeaderWithoutOsFlag = "H4sIAAAAAAAA";
        final String expectedFooter = "MAAAAAAAAAAAA=";
        final int startIndexHeader = 0;
        final int endIndexHeaderWithoutOsFlag = 12;
        final int startIndexFooter = 14;
        final int endIndexFooter = 28;

        String generatedBase64 = Base64.encodeBytes(new byte[] {}, Base64.GZIP);

        String generatedHeader = generatedBase64.substring(startIndexHeader, endIndexHeaderWithoutOsFlag);
        String generatedFooter = generatedBase64.substring(startIndexFooter, endIndexFooter);

        Assertions.assertEquals(expectedHeaderWithoutOsFlag, generatedHeader);
        Assertions.assertEquals(expectedFooter, generatedFooter);
    }

}
