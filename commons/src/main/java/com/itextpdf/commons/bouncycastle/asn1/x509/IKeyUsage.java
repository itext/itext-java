package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for KeyUsage that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IKeyUsage extends IASN1Encodable {
    /**
     * Gets {@code digitalSignature} constant for the wrapped KeyUsage.
     *
     * @return KeyUsage.digitalSignature value.
     */
    int getDigitalSignature();

    /**
     * Gets {@code nonRepudiation} constant for the wrapped KeyUsage.
     *
     * @return KeyUsage.nonRepudiation value.
     */
    int getNonRepudiation();
}
