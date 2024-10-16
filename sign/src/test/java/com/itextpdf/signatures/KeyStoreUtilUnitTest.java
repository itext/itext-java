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
package com.itextpdf.signatures;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.test.ExtendedITextTest;

import java.security.KeyStore;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Tag;

@Tag("UnitTest")
public class KeyStoreUtilUnitTest extends ExtendedITextTest {

    @Test
    @org.junit.jupiter.api.Disabled
    public void loadCacertsKeyStoreSUNTest() {
        KeyStore keyStore = KeyStoreUtil.loadCacertsKeyStore("SUN");
        Assertions.assertEquals("JKS", keyStore.getType());
        Assertions.assertEquals("SUN", keyStore.getProvider().getName());
    }

    @Test
    @org.junit.jupiter.api.Disabled
    public void loadCaCertsKeyStoreNoSuchProviderTest() {
        PdfException e = Assertions.assertThrows(PdfException.class,
                () -> KeyStoreUtil.loadCacertsKeyStore("unknown provider"));
        Assertions.assertEquals("no such provider: unknown provider", e.getCause().getMessage());
    }

    @Test
    @org.junit.jupiter.api.Disabled
    public void loadCaCertsKeyStoreJKSNotFoundTest() {
        PdfException e = Assertions.assertThrows(PdfException.class,
                () -> KeyStoreUtil.loadCacertsKeyStore("SunPCSC"));
        Assertions.assertEquals("JKS not found", e.getCause().getMessage());
    }

    @Test
    @org.junit.jupiter.api.Disabled
    public void loadCaCertsKeyStoreNullTest() {
        KeyStore keyStore = KeyStoreUtil.loadCacertsKeyStore(null);
        Assertions.assertEquals("JKS", keyStore.getType());
        Assertions.assertEquals("SUN", keyStore.getProvider().getName());
    }

    @Test
    @org.junit.jupiter.api.Disabled
    public void loadCaCertsKeyStoreEmptyTest() {
        PdfException e = Assertions.assertThrows(PdfException.class,
                () -> KeyStoreUtil.loadCacertsKeyStore(""));
        Assertions.assertEquals("missing provider", e.getCause().getMessage());
    }
}
