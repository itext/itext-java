package com.itextpdf.signatures;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IBasicOCSPResp;
import com.itextpdf.signatures.validation.OCSPValidator;

import java.security.cert.X509Certificate;

/**
 * Interface for the Online Certificate Status Protocol (OCSP) Client.
 * With a method returning parsed IBasicOCSPResp instead of encoded response.
 */
public interface IOcspClientBouncyCastle extends IOcspClient{
    /**
     * Gets OCSP response.
     *
     * <p>
     * If required, {@link IBasicOCSPResp} can be checked using {@link OCSPValidator} class.
     *
     * @param checkCert the certificate to check
     * @param rootCert  parent certificate
     * @param url       to get the verification
     *
     * @return {@link IBasicOCSPResp} an OCSP response wrapper
     */
    IBasicOCSPResp getBasicOCSPResp(X509Certificate checkCert, X509Certificate rootCert, String url);
}
