package com.itextpdf.commons.bouncycastle.asn1.cms;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

/**
 * This interface represents the wrapper for AttributeTable that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IAttributeTable {
    /**
     * Calls actual {@code get} method for the wrapped AttributeTable object.
     *
     * @param oid ASN1ObjectIdentifier wrapper
     *
     * @return {@link IAttribute} wrapper for the received Attribute object.
     */
    IAttribute get(IASN1ObjectIdentifier oid);
}
