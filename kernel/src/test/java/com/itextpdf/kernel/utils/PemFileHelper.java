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
package com.itextpdf.kernel.utils;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.openssl.IPEMParser;
import com.itextpdf.commons.bouncycastle.openssl.jcajce.IJcaPEMKeyConverter;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IInputDecryptorProvider;
import com.itextpdf.commons.bouncycastle.pkcs.AbstractPKCSException;
import com.itextpdf.commons.bouncycastle.pkcs.IPKCS8EncryptedPrivateKeyInfo;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.PrivateKey;

public final class PemFileHelper {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private PemFileHelper() {
        // Empty constructor.
    }
    
    public static PrivateKey readPrivateKeyFromPemFile(InputStream pemFile, char[] pkPassword)
            throws IOException, AbstractPKCSException, AbstractOperatorCreationException {
        IPKCS8EncryptedPrivateKeyInfo key = readPrivateKey(pemFile);
        if (key != null) {
            IInputDecryptorProvider decProv = BOUNCY_CASTLE_FACTORY.createJceOpenSSLPKCS8DecryptorProviderBuilder()
                    .setProvider(BOUNCY_CASTLE_FACTORY.getProvider()).build(pkPassword);
            IJcaPEMKeyConverter keyConverter = BOUNCY_CASTLE_FACTORY
                    .createJcaPEMKeyConverter().setProvider(BOUNCY_CASTLE_FACTORY.getProvider());
            return keyConverter.getPrivateKey(key.decryptPrivateKeyInfo(decProv));
        }
        return null;
    }

    private static IPKCS8EncryptedPrivateKeyInfo readPrivateKey(InputStream pemFile) throws IOException {
        try (IPEMParser parser = BOUNCY_CASTLE_FACTORY.createPEMParser(new InputStreamReader(pemFile))) {
            Object readObject = parser.readObject();
            while (!(readObject instanceof IPKCS8EncryptedPrivateKeyInfo) && readObject != null) {
                readObject = parser.readObject();
            }
            return (IPKCS8EncryptedPrivateKeyInfo) readObject;
        }
    }
}
