/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2019 iText Group NV
    Authors: iText Software.

    This program is free software; you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License version 3
    as published by the Free Software Foundation with the addition of the
    following permission added to Section 15 as permitted in Section 7(a):
    FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
    ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
    OF THIRD PARTY RIGHTS

    This program is distributed in the hope that it will be useful, but
    WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
    or FITNESS FOR A PARTICULAR PURPOSE.
    See the GNU Affero General Public License for more details.
    You should have received a copy of the GNU Affero General Public License
    along with this program; if not, see http://www.gnu.org/licenses or write to
    the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
    Boston, MA, 02110-1301 USA, or download the license from the following URL:
    http://itextpdf.com/terms-of-use/

    The interactive user interfaces in modified source and object code versions
    of this program must display Appropriate Legal Notices, as required under
    Section 5 of the GNU Affero General Public License.

    In accordance with Section 7(b) of the GNU Affero General Public License,
    a covered work must retain the producer line in every PDF that is created
    or manipulated using iText.

    You can be released from the requirements of the license by purchasing
    a commercial license. Buying such a license is mandatory as soon as you
    develop commercial activities involving the iText software without
    disclosing the source code of your own applications.
    These activities include: offering paid services to customers as an ASP,
    serving PDFs on the fly in a web application, shipping iText with a closed
    source product.

    For more information, please contact iText Software Corp. at this
    address: sales@itextpdf.com
 */
package com.itextpdf.test.signutils;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.Enumeration;

public final class Pkcs12FileHelper {
    private Pkcs12FileHelper() {
    }

    public static Certificate[] readFirstChain(String p12FileName, char[] ksPass) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException {
        Certificate[] certChain = null;

        KeyStore p12 = KeyStore.getInstance("pkcs12");
        p12.load(new FileInputStream(p12FileName), ksPass);

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

    public static PrivateKey readFirstKey(String p12FileName, char[] ksPass, char[] keyPass) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException {
        PrivateKey pk = null;

        KeyStore p12 = KeyStore.getInstance("pkcs12");
        p12.load(new FileInputStream(p12FileName), ksPass);

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

    public static KeyStore initStore(String p12FileName, char[] ksPass) throws KeyStoreException, IOException, CertificateException, NoSuchAlgorithmException, UnrecoverableKeyException, NoSuchProviderException {
        KeyStore p12 = KeyStore.getInstance("PKCS12", "BC");
        p12.load(new FileInputStream(p12FileName), ksPass);
        return p12;
    }
}
