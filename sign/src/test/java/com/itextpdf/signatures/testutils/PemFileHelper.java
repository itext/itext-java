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
package com.itextpdf.signatures.testutils;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.pkcs.IPrivateKeyInfo;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaX509CertificateConverter;
import com.itextpdf.commons.bouncycastle.openssl.IPEMParser;
import com.itextpdf.commons.bouncycastle.openssl.jcajce.IJcaPEMKeyConverter;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IInputDecryptorProvider;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.bouncycastle.pkcs.IPKCS8EncryptedPrivateKeyInfo;

import java.io.FileReader;
import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.Provider;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.List;

public final class PemFileHelper {
    
    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    
    private static final String CERTIFICATE_ALIAS_NAME = "cert-alias";
    
    private PemFileHelper() {
        // Empty constructor.
    }

    public static Certificate[] readFirstChain(String pemFileName) throws IOException, CertificateException {
        List<IX509CertificateHolder> certificatesHolders = readCertificates(pemFileName);
        IJcaX509CertificateConverter converter =
                FACTORY.createJcaX509CertificateConverter().setProvider(FACTORY.getProvider());
        Certificate[] certificates = new Certificate[certificatesHolders.size()];
        for (int i = 0; i < certificatesHolders.size(); i++) {
            certificates[i] = converter.getCertificate(certificatesHolders.get(i));
        }
        return certificates;
    }

    public static PrivateKey readFirstKey(String pemFileName, char[] keyPass)
            throws IOException, AbstractOperatorCreationException, AbstractPKCSException {
        IPKCS8EncryptedPrivateKeyInfo pkcs8Key = readPkcs8PrivateKey(pemFileName);
        if (pkcs8Key != null) {
            IInputDecryptorProvider decProv = FACTORY.createJceOpenSSLPKCS8DecryptorProviderBuilder()
                    .setProvider(FACTORY.getProvider()).build(keyPass);
            IJcaPEMKeyConverter keyConverter = FACTORY.createJcaPEMKeyConverter().setProvider(FACTORY.getProvider());
            return keyConverter.getPrivateKey(pkcs8Key.decryptPrivateKeyInfo(decProv));
        }
        IPrivateKeyInfo key = readPrivateKey(pemFileName);
        if (key != null) {
            IJcaPEMKeyConverter keyConverter = FACTORY.createJcaPEMKeyConverter().setProvider(FACTORY.getProvider());
            return keyConverter.getPrivateKey(key);
        }
        return null;
    }

    public static KeyStore initStore(String pemFileName, char[] keyPass, Provider provider)
            throws IOException, GeneralSecurityException, AbstractPKCSException, AbstractOperatorCreationException {
        KeyStore p12 = KeyStore.getInstance("BCFKS", provider.getName());
        p12.load(null, null);
        Certificate[] firstChain = readFirstChain(pemFileName);
        PrivateKey privateKey = readFirstKey(pemFileName, keyPass);
        if (privateKey != null) {
            p12.setKeyEntry(CERTIFICATE_ALIAS_NAME, privateKey, keyPass, firstChain);
        } else if (firstChain.length > 0) {
            p12.setCertificateEntry(CERTIFICATE_ALIAS_NAME, firstChain[0]);
        }
        return p12;
    }

    private static List<IX509CertificateHolder> readCertificates(String pemFileName) throws IOException {
        try (IPEMParser parser = FACTORY.createPEMParser(new FileReader(pemFileName))) {
            Object readObject = parser.readObject();
            List<IX509CertificateHolder> certificateHolders = new ArrayList<>();
            while (readObject != null) {
                if (readObject instanceof IX509CertificateHolder) {
                    certificateHolders.add((IX509CertificateHolder) readObject);
                }
                readObject = parser.readObject();
            }
            return certificateHolders;
        }
    }

    private static IPKCS8EncryptedPrivateKeyInfo readPkcs8PrivateKey(String pemFileName) throws IOException {
        try (IPEMParser parser = FACTORY.createPEMParser(new FileReader(pemFileName))) {
            Object readObject = parser.readObject();
            while (!(readObject instanceof IPKCS8EncryptedPrivateKeyInfo) && readObject != null) {
                readObject = parser.readObject();
            }
            return (IPKCS8EncryptedPrivateKeyInfo) readObject;
        }
    }

    private static IPrivateKeyInfo readPrivateKey(String pemFileName) throws IOException {
        try (IPEMParser parser = FACTORY.createPEMParser(new FileReader(pemFileName))) {
            Object readObject = parser.readObject();
            while (!(readObject instanceof IPrivateKeyInfo) && readObject != null) {
                readObject = parser.readObject();
            }
            return (IPrivateKeyInfo) readObject;
        }
    }
}
