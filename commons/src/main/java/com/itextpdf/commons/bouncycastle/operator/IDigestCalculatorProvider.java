package com.itextpdf.commons.bouncycastle.operator;

import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;

/**
 * This interface represents the wrapper for DigestCalculatorProvider that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IDigestCalculatorProvider {
    /**
     * Calls actual {@code get} method for the wrapped DigestCalculatorProvider object.
     *
     * @param algorithmIdentifier AlgorithmIdentifier wrapper
     *
     * @return {@link IDigestCalculator} the wrapper for received DigestCalculator object.
     *
     * @throws AbstractOperatorCreationException wrapped OperatorCreationException.
     */
    IDigestCalculator get(IAlgorithmIdentifier algorithmIdentifier) throws AbstractOperatorCreationException;
}
