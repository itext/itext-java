package com.itextpdf.signatures.testutils.client;

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.signatures.IOcspClient;

import java.io.IOException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class TestOcspClientWrapper implements IOcspClient {
    private static final IBouncyCastleFactory BOUNCY_CASTLE_FACTORY = BouncyCastleFactoryCreator.getFactory();
    private final List<OcspClientCall> calls = new ArrayList<>();
    private final IOcspClient wrappedClient;

    public TestOcspClientWrapper(IOcspClient wrappedClient) {
        this.wrappedClient = wrappedClient;
    }

    @Override
    public byte[] getEncoded(X509Certificate checkCert, X509Certificate issuerCert, String url) {
        byte[] response = wrappedClient.getEncoded(checkCert, issuerCert, url);
        try {
            IBasicOCSPResp basicOCSPResp = BOUNCY_CASTLE_FACTORY.createBasicOCSPResp(
                    BOUNCY_CASTLE_FACTORY.createBasicOCSPResponse(
                            BOUNCY_CASTLE_FACTORY.createASN1Primitive(response)));
            calls.add(new OcspClientCall(checkCert, issuerCert, url, basicOCSPResp));
        } catch (IOException e) {
            throw new RuntimeException("deserializing ocsp response failed", e);
        }
        return response;
    }

    public List<OcspClientCall> getCalls() {
        return calls;
    }

    public static class OcspClientCall {
        public final X509Certificate checkCert;
        public final X509Certificate issuerCert;
        public final String url;
        public final IBasicOCSPResp response;

        public OcspClientCall(X509Certificate checkCert, X509Certificate issuerCert, String url, IBasicOCSPResp response) {
            this.checkCert = checkCert;
            this.issuerCert = issuerCert;
            this.url = url;
            this.response = response;
        }
    }
}
