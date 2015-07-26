package com.itextpdf.signatures;

import java.security.cert.X509Certificate;

/**
 * Interface for the Online Certificate Status Protocol (OCSP) Client.
 */
public interface OcspClient {

    /**
     * Gets an encoded byte array with OCSP validation. The method should not throw an exception.
     * @param checkCert Certificate to check.
     * @param issuerCert The parent certificate.
     * @param url The url to get the verification. It it's null it will be taken.
     * from the check cert or from other implementation specific source
     * @return A byte array with the validation or null if the validation could not be obtained
     */
    byte[] getEncoded(X509Certificate checkCert, X509Certificate issuerCert, String url); // TODO: issuer?
}
