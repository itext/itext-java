package com.itextpdf.signatures.testutils.client;

import com.itextpdf.signatures.IOcspClient;
import com.itextpdf.signatures.testutils.SignTestPortUtil;
import com.itextpdf.signatures.testutils.builder.TestOcspResponseBuilder;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.ocsp.CertificateID;

public class TestOcspClient implements IOcspClient {

    private final TestOcspResponseBuilder builder;
    private final PrivateKey caPrivateKey;

    public TestOcspClient(TestOcspResponseBuilder builder, PrivateKey caPrivateKey) {
        this.builder = builder;
        this.caPrivateKey = caPrivateKey;
    }

    public TestOcspClient(X509Certificate caCert, PrivateKey caPrivateKey) throws CertificateEncodingException {
        this.builder = new TestOcspResponseBuilder(caCert);
        this.caPrivateKey = caPrivateKey;
    }

    @Override
    public byte[] getEncoded(X509Certificate checkCert, X509Certificate issuerCert, String url) {
        byte[] bytes = null;
        try {
            CertificateID id = SignTestPortUtil.generateCertificateId(issuerCert, checkCert.getSerialNumber(), CertificateID.HASH_SHA1);
            bytes = builder.makeOcspResponse(SignTestPortUtil.generateOcspRequestWithNonce(id).getEncoded(), caPrivateKey);
        } catch (Exception ignored) {
        }

        return bytes;
    }
}
