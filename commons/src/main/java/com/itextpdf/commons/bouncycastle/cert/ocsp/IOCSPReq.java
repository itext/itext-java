package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.IExtension;

import java.io.IOException;

/**
 * This interface represents the wrapper for OCSPReq that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IOCSPReq {
    /**
     * Calls actual {@code getEncoded} method for the wrapped OCSPReq object.
     *
     * @return the default encoding for the wrapped object.
     *
     * @throws IOException on encoding error.
     */
    byte[] getEncoded() throws IOException;

    /**
     * Calls actual {@code getRequestList} method for the wrapped OCSPReq object.
     *
     * @return {@link IReq} request wrappers list.
     */
    IReq[] getRequestList();

    /**
     * Calls actual {@code getExtension} method for the wrapped OCSPReq object.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     *
     * @return {@link IExtension} wrapper for received Extension.
     */
    IExtension getExtension(IASN1ObjectIdentifier objectIdentifier);
}
