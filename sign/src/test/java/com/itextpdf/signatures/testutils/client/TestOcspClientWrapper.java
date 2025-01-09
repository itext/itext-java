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
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.forms.form.element.Button;
import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.IOcspClientBouncyCastle;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class TestOcspClientWrapper implements IOcspClient , IOcspClientBouncyCastle {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();
    private final List<OcspClientCall> calls = new ArrayList<>();
    private final List<BasicOCSPCall> basicCalls = new ArrayList<>();
    private final IOcspClient wrappedClient;
    private Function<OcspClientCall, byte[]> onGetEncoded;
    private Function<BasicOCSPCall, IBasicOCSPResp> onGetBasicPOcspResponse;


    public TestOcspClientWrapper(IOcspClient wrappedClient) {
        this.wrappedClient = wrappedClient;
    }

    @Override
    public byte[] getEncoded(X509Certificate checkCert, X509Certificate issuerCert, String url) {
        OcspClientCall call = new OcspClientCall(checkCert, issuerCert, url);
        byte[] response;
        if (onGetEncoded != null) {
            response = onGetEncoded.apply(call);
        } else {
            response = wrappedClient.getEncoded(checkCert, issuerCert, url);
        }
        try {
            IBasicOCSPResp basicOCSPResp = BOUNCY_CASTLE_FACTORY.createBasicOCSPResp(
                    BOUNCY_CASTLE_FACTORY.createBasicOCSPResponse(
                            BOUNCY_CASTLE_FACTORY.createASN1Primitive(response)));
            call.setResponce(basicOCSPResp);
            calls.add(call);
        } catch (IOException e) {
            throw new RuntimeException("deserializing ocsp response failed", e);
        }
        return response;
    }

    public List<OcspClientCall> getCalls() {
        return calls;
    }

    public List<BasicOCSPCall> getBasicResponceCalls() {
        return basicCalls;
    }

    public TestOcspClientWrapper onGetEncodedDo(Function<OcspClientCall, byte[]> callBack) {
        onGetEncoded = callBack;
        return this;
    }

    @Override
    public IBasicOCSPResp getBasicOCSPResp(X509Certificate checkCert, X509Certificate issuerCert, String url) {
        BasicOCSPCall call = new BasicOCSPCall(checkCert, issuerCert, url);
        basicCalls.add(call);
        if (onGetBasicPOcspResponse != null) {
            return onGetBasicPOcspResponse.apply(call);
        }
        if (wrappedClient instanceof IOcspClientBouncyCastle) {
            return ((IOcspClientBouncyCastle) wrappedClient).getBasicOCSPResp(checkCert, issuerCert, url);
        }
        throw new RuntimeException("TestOcspClientWrapper for IOcspClientBouncyCastle was expected here.");
    }

    public TestOcspClientWrapper onGetBasicOCSPRespDo(Function<BasicOCSPCall, IBasicOCSPResp> callback) {
        onGetBasicPOcspResponse = callback;
        return this;
    }

    public static class OcspClientCall {
        public final X509Certificate checkCert;
        public final X509Certificate issuerCert;
        public final String url;
        public IBasicOCSPResp response;

        public OcspClientCall(X509Certificate checkCert, X509Certificate issuerCert, String url) {
            this.checkCert = checkCert;
            this.issuerCert = issuerCert;
            this.url = url;
        }

        public void setResponce(IBasicOCSPResp basicOCSPResp) {
            response = basicOCSPResp;
        }
    }

    public static class BasicOCSPCall {
        public final X509Certificate checkCert;
        public final X509Certificate issuerCert;
        public final String url;

        public BasicOCSPCall(X509Certificate checkCert, X509Certificate issuerCert, String url) {
            this.checkCert = checkCert;
            this.issuerCert = issuerCert;
            this.url = url;

        }
    }
}
