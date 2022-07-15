package com.itextpdf.commons.bouncycastle.asn1.ess;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for ESSCertID that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IESSCertID extends IASN1Encodable {
    /**
     * Calls actual {@code getCertHash} method for the wrapped ESSCertID object.
     *
     * @return certificate hash byte array.
     */
    byte[] getCertHash();
}
