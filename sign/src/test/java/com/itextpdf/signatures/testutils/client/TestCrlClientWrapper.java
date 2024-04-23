package com.itextpdf.signatures.testutils.client;

import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.ICrlClient;

import java.io.ByteArrayInputStream;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class TestCrlClientWrapper implements ICrlClient {

    private final ICrlClient wrappedClient;
    private final List<CrlClientCall> calls = new ArrayList<>();

    public TestCrlClientWrapper(ICrlClient wrappedClient) {
        this.wrappedClient = wrappedClient;
    }

    @Override
    public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) throws CertificateEncodingException {
        Collection<byte[]> crlBytesCollection = wrappedClient.getEncoded(checkCert, url);
        List<X509CRL> crlResponses = new ArrayList<>();
        for (byte[] crlBytes : crlBytesCollection) {
            try {
                crlResponses.add((X509CRL) CertificateUtil.parseCrlFromStream(
                        new ByteArrayInputStream(crlBytes)));
            } catch (Exception e) {
                throw new RuntimeException("Deserializing CRL response failed",e);
            }
        }
        calls.add(new CrlClientCall(checkCert, url, crlResponses));
        return crlBytesCollection;
    }

    public List<CrlClientCall> getCalls() {
        return calls;
    }

    public static class CrlClientCall {
        public final X509Certificate checkCert;
        public final String url;
        public final List<X509CRL> responses;

        public CrlClientCall(X509Certificate checkCert, String url, List<X509CRL> responses) {
            this.checkCert = checkCert;
            this.url = url;
            this.responses = responses;
        }
    }
}
