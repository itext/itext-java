package com.itextpdf.commons.bouncycastle.tsp;

import java.math.BigInteger;
import java.util.Date;

/**
 * This interface represents the wrapper for TimeStampResponseGenerator that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ITimeStampResponseGenerator {
    /**
     * Calls actual {@code generate} method for the wrapped TimeStampResponseGenerator object.
     *
     * @param request    the wrapper for request this response is for
     * @param bigInteger serial number for the response token
     * @param date       generation time for the response token
     *
     * @return {@link ITimeStampResponse} the wrapper for the generated TimeStampResponse object.
     */
    ITimeStampResponse generate(ITimeStampRequest request, BigInteger bigInteger, Date date)
            throws AbstractTSPException;
}
