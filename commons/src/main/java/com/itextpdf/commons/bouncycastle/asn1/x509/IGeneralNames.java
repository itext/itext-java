package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for GeneralNames that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IGeneralNames extends IASN1Encodable {
    /**
     * Calls actual {@code getNames} method for the wrapped GeneralNames object.
     *
     * @return array of wrapped names {@link IGeneralName}.
     */
    IGeneralName[] getNames();
}
