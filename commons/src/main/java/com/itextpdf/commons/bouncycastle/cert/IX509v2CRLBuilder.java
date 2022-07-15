package com.itextpdf.commons.bouncycastle.cert;

import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.math.BigInteger;
import java.util.Date;

/**
 * This interface represents the wrapper for X509v2CRLBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IX509v2CRLBuilder {
    /**
     * Calls actual {@code addCRLEntry} method for the wrapped X509v2CRLBuilder object.
     *
     * @param bigInteger serial number of revoked certificate
     * @param date       date of certificate revocation
     * @param i          the reason code, as indicated in CRLReason, i.e CRLReason.keyCompromise, or 0 if not to be used
     *
     * @return {@link IX509v2CRLBuilder} the current wrapper object.
     */
    IX509v2CRLBuilder addCRLEntry(BigInteger bigInteger, Date date, int i);

    /**
     * Calls actual {@code setNextUpdate} method for the wrapped X509v2CRLBuilder object.
     *
     * @param nextUpdate date of next CRL update
     *
     * @return {@link IX509v2CRLBuilder} the current wrapper object.
     */
    IX509v2CRLBuilder setNextUpdate(Date nextUpdate);

    /**
     * Calls actual {@code build} method for the wrapped X509v2CRLBuilder object.
     *
     * @param signer ContentSigner wrapper
     *
     * @return {@link IX509CRLHolder} the wrapper for built X509CRLHolder object.
     */
    IX509CRLHolder build(IContentSigner signer);
}
