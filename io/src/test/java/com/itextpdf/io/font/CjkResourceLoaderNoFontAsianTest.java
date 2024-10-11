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
package com.itextpdf.io.font;

import com.itextpdf.io.exceptions.IOException;
import com.itextpdf.io.font.cmap.CMapLocationResource;
import com.itextpdf.test.ExtendedITextTest;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
// Android-Conversion-Skip-File (TODO DEVSIX-7376 investigate why CjkResourceLoaderNoFontAsianTest is skipped on Android)
public class CjkResourceLoaderNoFontAsianTest extends ExtendedITextTest {

    @BeforeAll
    public static void beforeClass() {
        // Here we mimic the absence of font asian
        CjkResourceLoader.setCmapLocation(new DummyCMapLocationResource());
    }

    @AfterAll
    public static void afterClass() {
        CjkResourceLoader.setCmapLocation(new CMapLocationResource());
    }

    @Test
    public void getCompatibleCidFontNoFontAsian() {
        // Without font-asian module in the class path
        // any value passed into a method is expected to return null.
        Assertions.assertNull(CjkResourceLoader.getCompatibleCidFont("78-RKSJ-V"));
    }

    @Test
    public void isPredefinedCidFontNoFontAsian() {
        // Without font-asian module in the class path
        // any value passed into a method is expected to return false.
        Assertions.assertFalse(CjkResourceLoader.isPredefinedCidFont("KozMinPro-Regular"));
    }

    @Test
    public void getCompatibleCmapsNoFontAsian() {
        // Without font-asian module in the class path
        // any value passed into a method is expected to return null.
        Assertions.assertNull(CjkResourceLoader.getCompatibleCmaps("HeiseiKakuGo-W5"));
    }

    @Test
    public void getRegistryNamesNoFontAsian() {
        // Without font-asian module in the class path
        // registry names collection is expected to be empty.
        Assertions.assertTrue(CjkResourceLoader.getRegistryNames().isEmpty());
    }

    @Test
    public void getCid2UniCMapNoFontAsian() {
        // Without font-asian module in the class path
        // no CMap can be found.
        Assertions.assertThrows(IOException.class, () -> CjkResourceLoader.getCid2UniCmap("UniJIS-UTF16-H"));
    }

    @Test
    public void getUni2CidCMapNoFontAsian() {
        // Without font-asian module in the class path
        // no CMap can be found.
        Assertions.assertThrows(IOException.class, () -> CjkResourceLoader.getUni2CidCmap("UniJIS-UTF16-H"));
    }

    @Test
    public void getByte2CidCMapNoFontAsian() {
        // Without font-asian module in the class path
        // no CMap can be found.
        Assertions.assertThrows(IOException.class, () -> CjkResourceLoader.getByte2CidCmap("78ms-RKSJ-H"));
    }

    @Test
    public void getCid2ByteCMapNoFontAsian() {
        // Without font-asian module in the class path
        // no CMap can be found.
        Assertions.assertThrows(IOException.class, () -> CjkResourceLoader.getCidToCodepointCmap("78ms-RKSJ-H"));
    }

    private static class DummyCMapLocationResource extends CMapLocationResource {
        @Override
        public String getLocationPath() {
            return super.getLocationPath() + "dummy/path/";
        }
    }
}
