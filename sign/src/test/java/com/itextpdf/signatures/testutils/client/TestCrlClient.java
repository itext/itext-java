package com.itextpdf.signatures.testutils.client;

import com.itextpdf.io.util.DateTimeUtil;
import com.itextpdf.signatures.ICrlClient;
import com.itextpdf.signatures.testutils.builder.TestCrlBuilder;
import java.security.PrivateKey;
import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;

public class TestCrlClient implements ICrlClient {

    private final TestCrlBuilder crlBuilder;
    private final PrivateKey caPrivateKey;

    public TestCrlClient(TestCrlBuilder crlBuilder, PrivateKey caPrivateKey) throws CertificateEncodingException {
        this.crlBuilder = crlBuilder;
        this.caPrivateKey = caPrivateKey;
    }

    public TestCrlClient(X509Certificate caCert, PrivateKey caPrivateKey) throws CertificateEncodingException {
        this.crlBuilder = new TestCrlBuilder(caCert, DateTimeUtil.addDaysToDate(DateTimeUtil.getCurrentTimeDate(), -1));
        this.caPrivateKey = caPrivateKey;
    }

    @Override
    public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) {
        Collection<byte[]> crls = null;
        try {
            byte[] crl = crlBuilder.makeCrl(caPrivateKey);
            crls = Collections.singletonList(crl);
        } catch (Exception ignore) {
        }
        return crls;
    }
}
