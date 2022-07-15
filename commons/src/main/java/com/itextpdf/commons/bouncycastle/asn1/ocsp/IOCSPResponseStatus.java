package com.itextpdf.commons.bouncycastle.asn1.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for OCSPResponseStatus that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IOCSPResponseStatus extends IASN1Encodable {
    /**
     * Gets {@code SUCCESSFUL} constant for the wrapped OCSPResponseStatus.
     *
     * @return OCSPResponseStatus.SUCCESSFUL value.
     */
    int getSuccessful();
}
