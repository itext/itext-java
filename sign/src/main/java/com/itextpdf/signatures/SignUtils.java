package com.itextpdf.signatures;

import java.io.InputStream;
import java.security.cert.CRL;
import java.security.cert.CRLException;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;

class SignUtils {
    static CRL parseCrlFromStream(InputStream input) throws CertificateException, CRLException {
        return CertificateFactory.getInstance("X.509").generateCRL(input);
    }

    static byte[] getExtensionValueByOid(X509Certificate certificate, String oid) {
        return certificate.getExtensionValue(oid);
    }
}
