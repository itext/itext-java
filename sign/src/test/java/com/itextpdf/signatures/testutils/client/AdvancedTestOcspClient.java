/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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
import com.itextpdf.commons.bouncycastle.cert.ocsp.AbstractOCSPException;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPReq;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IOCSPResp;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.signatures.OCSPVerifier;
import com.itextpdf.signatures.OcspClientBouncyCastle;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.LinkedHashMap;
import java.util.Map;

public class AdvancedTestOcspClient extends OcspClientBouncyCastle {

    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final Map<String, TestOcspResponseBuilder> issuerIdToResponseBuilder = new LinkedHashMap<>();
    
    public AdvancedTestOcspClient(OCSPVerifier verifier) {
        super(verifier);
    }

    @Override
    protected InputStream createRequestAndResponse(X509Certificate checkCert, X509Certificate rootCert, String url)
            throws IOException, AbstractOperatorCreationException, AbstractOCSPException, CertificateEncodingException {
        IOCSPReq request = generateOCSPRequest(rootCert, checkCert.getSerialNumber());
        byte[] array = request.getEncoded();
        TestOcspResponseBuilder builder = issuerIdToResponseBuilder.get(checkCert.getSerialNumber().toString(16));
        try {
            IOCSPResp resp = BOUNCY_CASTLE_FACTORY.createOCSPRespBuilder().build(
                    BOUNCY_CASTLE_FACTORY.createOCSPRespBuilderInstance().getSuccessful(),
                    builder.makeOcspResponseObject(array));
            return new ByteArrayInputStream(resp.getEncoded());
        } catch (GeneralSecurityException e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    public AdvancedTestOcspClient addBuilderForCertIssuer(X509Certificate cert, X509Certificate signingCert,
            PrivateKey privateKey)
            throws CertificateEncodingException, IOException {
        issuerIdToResponseBuilder.put(cert.getSerialNumber().toString(16),
                new TestOcspResponseBuilder(signingCert, privateKey));
        return this;
    }

    public AdvancedTestOcspClient addBuilderForCertIssuer(X509Certificate cert, TestOcspResponseBuilder builder) {
        issuerIdToResponseBuilder.put(cert.getSerialNumber().toString(16), builder);
        return this;
    }
}
