package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for KeyPurposeId that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IKeyPurposeId extends IASN1Encodable {
    /**
     * Gets {@code id_kp_OCSPSigning} constant for the wrapped KeyPurposeId.
     *
     * @return KeyPurposeId.id_kp_OCSPSigning value.
     */
    IKeyPurposeId getIdKpOCSPSigning();
}
