package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for GeneralName that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IGeneralName extends IASN1Encodable {
    /**
     * Calls actual {@code getTagNo} method for the wrapped GeneralName object.
     *
     * @return tagNo value.
     */
    int getTagNo();

    /**
     * Gets {@code uniformResourceIdentifier} constant for the wrapped GeneralName.
     *
     * @return GeneralName.uniformResourceIdentifier value.
     */
    int getUniformResourceIdentifier();
}
