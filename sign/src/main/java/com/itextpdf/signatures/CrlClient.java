package com.itextpdf.signatures;

import java.security.cert.X509Certificate;
import java.util.Collection;

/**
 * Interface that needs to be implemented if you want to embed
 * Certificate Revocation Lists (CRL) into your PDF.
 * @author Paulo Soares
 */
public interface CrlClient {

    /**
     * Gets an encoded byte array.
     * @param	checkCert The certificate which a CRL URL can be obtained from.
     * @param	url	A CRL url if you don't want to obtain it from the certificate.
     * @return	A collection of byte array each representing a crl. It may return null or an empty collection.
     */
    Collection<byte[]> getEncoded(X509Certificate checkCert, String url);
}