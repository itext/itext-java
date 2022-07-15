package com.itextpdf.commons.bouncycastle.asn1.cms;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Set;

/**
 * This interface represents the wrapper for Attribute that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IAttribute extends IASN1Encodable {
    /**
     * Calls actual {@code getAttrValues} method for the wrapped Attribute object.
     *
     * @return {@link IASN1Set} wrapped attribute values.
     */
    IASN1Set getAttrValues();
}
