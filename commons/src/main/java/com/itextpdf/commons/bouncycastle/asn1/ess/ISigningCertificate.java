package com.itextpdf.commons.bouncycastle.asn1.ess;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for SigningCertificate that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ISigningCertificate extends IASN1Encodable {
    /**
     * Calls actual {@code getCerts} method for the wrapped SigningCertificate object.
     *
     * @return array of wrapped certificates {@link IESSCertID}.
     */
    IESSCertID[] getCerts();
}
