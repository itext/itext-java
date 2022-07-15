package com.itextpdf.commons.bouncycastle.asn1.ess;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for SigningCertificateV2 that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ISigningCertificateV2 extends IASN1Encodable {
    /**
     * Calls actual {@code getCerts} method for the wrapped SigningCertificateV2 object.
     *
     * @return array of wrapped certificates {@link IESSCertIDv2}.
     */
    IESSCertIDv2[] getCerts();
}
