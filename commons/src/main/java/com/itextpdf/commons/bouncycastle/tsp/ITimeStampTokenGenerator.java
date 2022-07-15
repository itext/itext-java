package com.itextpdf.commons.bouncycastle.tsp;

import com.itextpdf.commons.bouncycastle.cert.jcajce.IJcaCertStore;

import java.math.BigInteger;
import java.util.Date;

/**
 * This interface represents the wrapper for TimeStampTokenGenerator that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ITimeStampTokenGenerator {
    /**
     * Calls actual {@code setAccuracySeconds} method for the wrapped TimeStampTokenGenerator object.
     *
     * @param i accuracy seconds to set
     */
    void setAccuracySeconds(int i);

    /**
     * Calls actual {@code addCertificates} method for the wrapped TimeStampTokenGenerator object.
     *
     * @param jcaCertStore the wrapper for the JcaCertStore to add
     */
    void addCertificates(IJcaCertStore jcaCertStore);

    /**
     * Calls actual {@code generate} method for the wrapped TimeStampTokenGenerator object.
     *
     * @param request    the originating TimeStampRequest wrapper
     * @param bigInteger serial number for the TimeStampToken
     * @param date       token generation time
     *
     * @return {@link ITimeStampToken} the wrapper for the generated TimeStampToken object.
     */
    ITimeStampToken generate(ITimeStampRequest request, BigInteger bigInteger, Date date) throws AbstractTSPException;
}
