package com.itextpdf.commons.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;

import java.math.BigInteger;

/**
 * This interface represents the wrapper for TimeStampRequestGenerator that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ITimeStampRequestGenerator {
    /**
     * Calls actual {@code setCertReq} method for the wrapped TimeStampRequestGenerator object.
     *
     * @param var1 the value to be set
     */
    void setCertReq(boolean var1);

    /**
     * Calls actual {@code setReqPolicy} method for the wrapped TimeStampRequestGenerator object.
     *
     * @param reqPolicy the value to be set
     */
    void setReqPolicy(String reqPolicy);

    /**
     * Calls actual {@code generate} method for the wrapped TimeStampRequestGenerator object.
     *
     * @param objectIdentifier ASN1ObjectIdentifier wrapper
     * @param imprint          byte array
     * @param nonce            BigInteger
     *
     * @return {@link ITimeStampRequest} the wrapper for generated TimeStampRequest object.
     */
    ITimeStampRequest generate(IASN1ObjectIdentifier objectIdentifier, byte[] imprint, BigInteger nonce);
}
