package com.itextpdf.signatures;

import com.itextpdf.core.PdfException;
import java.security.cert.CRL;
import java.security.cert.X509CRL;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * An implementation of the CrlClient that handles offline
 * Certificate Revocation Lists.
 *
 * @author Paulo Soares
 */
public class CrlClientOffline implements CrlClient {

    /**
     * The CRL as a byte array.
     */
    private List<byte[]> crls = new ArrayList<>();

    /**
     * Creates an instance of a CrlClient in case you
     * have a local cache of the Certificate Revocation List.
     *
     * @param crlEncoded the CRL bytes
     */
    public CrlClientOffline(byte[] crlEncoded) {
        crls.add(crlEncoded);
    }

    /**
     * Creates an instance of a CrlClient in case you
     * have a local cache of the Certificate Revocation List.
     *
     * @param crl a CRL object
     */
    public CrlClientOffline(CRL crl) {
        try {
            crls.add(((X509CRL) crl).getEncoded());
        } catch (Exception ex) {
            throw new PdfException(ex);
        }
    }

    /**
     * Returns the CRL bytes (the parameters are ignored).
     *
     * @see com.itextpdf.signatures.CrlClient#getEncoded(java.security.cert.X509Certificate, java.lang.String)
     */
    public Collection<byte[]> getEncoded(X509Certificate checkCert, String url) {
        return crls;
    }

}
