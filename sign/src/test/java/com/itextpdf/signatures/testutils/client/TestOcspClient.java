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
package com.itextpdf.signatures.testutils.client;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateID;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestOcspClient implements IOcspClient {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final Map<String, TestOcspResponseBuilder> issuerIdToResponseBuilder = new LinkedHashMap<>();

    public TestOcspClient addBuilderForCertIssuer(X509Certificate cert, PrivateKey privateKey)
            throws CertificateEncodingException, IOException {
        issuerIdToResponseBuilder.put(cert.getSerialNumber().toString(16), new TestOcspResponseBuilder(cert, privateKey));
        return this;
    }

    public TestOcspClient addBuilderForCertIssuer(X509Certificate cert, TestOcspResponseBuilder builder) {
        issuerIdToResponseBuilder.put(cert.getSerialNumber().toString(16), builder);
        return this;
    }

    @Override
    public byte[] getEncoded(X509Certificate checkCert, X509Certificate issuerCert, String url) {
        if (url != null && !url.isEmpty()) {
            // Treat as file path
            try {
                return Files.readAllBytes(Paths.get(url));
            } catch (Exception e) {
                // Sometimes we pass http url here in tests (though it's not used) so skipping any errors
            }
        }

        byte[] bytes = null;
        try {
            ICertificateID id = SignTestPortUtil.generateCertificateId(issuerCert, checkCert.getSerialNumber(), BOUNCY_CASTLE_FACTORY.createCertificateID().getHashSha1());
            TestOcspResponseBuilder builder = issuerIdToResponseBuilder.get(issuerCert.getSerialNumber().toString(16));
            if (builder == null) {
                throw new IllegalArgumentException("This TestOcspClient instance is not capable of providing OCSP response for the given issuerCert:" + issuerCert.getSubjectDN().toString());
            }
            bytes = builder.makeOcspResponse(SignTestPortUtil.generateOcspRequestWithNonce(id).getEncoded());
        } catch (Exception e) {
            if (e instanceof RuntimeException) {
                throw (RuntimeException) e;
            }
        }

        return bytes;
    }
}
