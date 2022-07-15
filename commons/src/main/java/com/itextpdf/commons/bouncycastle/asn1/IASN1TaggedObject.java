package com.itextpdf.commons.bouncycastle.asn1;

/**
 * This interface represents the wrapper for ASN1TaggedObject that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1TaggedObject extends IASN1Primitive {
    /**
     * Calls actual {@code getObject} method for the wrapped ASN1TaggedObject object.
     *
     * @return {@link IASN1Primitive} wrapped ASN1Primitive object.
     */
    IASN1Primitive getObject();

    /**
     * Calls actual {@code getTagNo} method for the wrapped ASN1TaggedObject object.
     *
     * @return tagNo value.
     */
    int getTagNo();
}
