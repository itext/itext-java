package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;

/**
 * This interface represents the wrapper for OCSPReqBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IOCSPReqBuilder {
    /**
     * Calls actual {@code setRequestExtensions} method for the wrapped OCSPReqBuilder object.
     *
     * @param extensions wrapper for extensions to set
     *
     * @return {@link IOCSPReqBuilder} this wrapper object.
     */
    IOCSPReqBuilder setRequestExtensions(IExtensions extensions);

    /**
     * Calls actual {@code addRequest} method for the wrapped OCSPReqBuilder object.
     *
     * @param certificateID CertificateID wrapper
     *
     * @return {@link IOCSPReqBuilder} this wrapper object.
     */
    IOCSPReqBuilder addRequest(ICertificateID certificateID);

    /**
     * Calls actual {@code build} method for the wrapped OCSPReqBuilder object.
     *
     * @return {@link IOCSPReq} wrapper for built OCSPReq object.
     *
     * @throws AbstractOCSPException wrapped OCSPException.
     */
    IOCSPReq build() throws AbstractOCSPException;
}
