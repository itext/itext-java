package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

/**
 * This interface represents the wrapper for AlgorithmIdentifier that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IAlgorithmIdentifier extends IASN1Encodable {
    /**
     * Calls actual {@code getAlgorithm} method for the wrapped AlgorithmIdentifier object.
     *
     * @return {@link IASN1ObjectIdentifier} wrapped algorithm ASN1ObjectIdentifier.
     */
    IASN1ObjectIdentifier getAlgorithm();
}
