package com.itextpdf.commons.bouncycastle.asn1.x509;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

/**
 * This interface represents the wrapper for SubjectPublicKeyInfo that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ISubjectPublicKeyInfo extends IASN1Encodable {
    /**
     * Calls actual {@code getAlgorithm} method for the wrapped SubjectPublicKeyInfo object.
     *
     * @return {@link IAlgorithmIdentifier} wrapped AlgorithmIdentifier.
     */
    IAlgorithmIdentifier getAlgorithm();
}
