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
package com.itextpdf.test.signutils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

/**
 * This class doesn't support bouncy-castle FIPS so it shall not be used in itextcore.
 */
public final class Pkcs12FileHelper {
    private Pkcs12FileHelper() {
    }

    public static Certificate[] readFirstChain(String p12FileName, char[] ksPass)
            throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        Certificate[] certChain = null;

        KeyStore p12 = KeyStore.getInstance("pkcs12");
        p12.load(Files.newInputStream(Paths.get(p12FileName)), ksPass);

        Enumeration<String> aliases = p12.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (p12.isKeyEntry(alias)) {
                certChain = p12.getCertificateChain(alias);
                break;
            }
        }

        return certChain;
    }

    public static PrivateKey readPrivateKeyFromPKCS12KeyStore(InputStream keyStore, String pkAlias, char[] pkPassword)
            throws GeneralSecurityException, IOException {
        KeyStore keystore = KeyStore.getInstance("PKCS12");
        keystore.load(keyStore, pkPassword);
        return (PrivateKey) keystore.getKey(pkAlias, pkPassword);
    }

    public static PrivateKey readFirstKey(String p12FileName, char[] ksPass, char[] keyPass)
            throws KeyStoreException, IOException, CertificateException,
            NoSuchAlgorithmException, UnrecoverableKeyException {
        PrivateKey pk = null;

        KeyStore p12 = KeyStore.getInstance("pkcs12");
        p12.load(Files.newInputStream(Paths.get(p12FileName)), ksPass);

        Enumeration<String> aliases = p12.aliases();
        while (aliases.hasMoreElements()) {
            String alias = aliases.nextElement();
            if (p12.isKeyEntry(alias)) {
                pk = (PrivateKey) p12.getKey(alias, keyPass);
                break;
            }
        }

        return pk;
    }

    public static KeyStore initStore(String p12FileName, char[] ksPass, Provider provider)
            throws KeyStoreException, IOException, CertificateException,
            NoSuchAlgorithmException, NoSuchProviderException {
        KeyStore p12 = KeyStore.getInstance("PKCS12", provider.getName());
        p12.load(Files.newInputStream(Paths.get(p12FileName)), ksPass);
        return p12;
    }
}
