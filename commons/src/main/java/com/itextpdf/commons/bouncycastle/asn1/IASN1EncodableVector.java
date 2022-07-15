package com.itextpdf.commons.bouncycastle.asn1;

import com.itextpdf.commons.bouncycastle.asn1.cms.IAttribute;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

/**
 * This interface represents the wrapper for ASN1EncodableVector that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IASN1EncodableVector {
    /**
     * Calls actual {@code add} method for the wrapped ASN1EncodableVector object.
     *
     * @param primitive ASN1Primitive wrapper.
     */
    void add(IASN1Primitive primitive);

    /**
     * Calls actual {@code add} method for the wrapped ASN1EncodableVector object.
     *
     * @param attribute Attribute wrapper.
     */
    void add(IAttribute attribute);

    /**
     * Calls actual {@code add} method for the wrapped ASN1EncodableVector object.
     *
     * @param element AlgorithmIdentifier wrapper.
     */
    void add(IAlgorithmIdentifier element);
}
