package com.itextpdf.commons.bouncycastle.asn1.tsp;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;

import java.text.ParseException;
import java.util.Date;

/**
 * This interface represents the wrapper for TSTInfo that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface ITSTInfo extends IASN1Encodable {
    /**
     * Calls actual {@code getMessageImprint} method for the wrapped TSTInfo object.
     *
     * @return {@link IMessageImprint} wrapper for the received MessageImprint object.
     */
    IMessageImprint getMessageImprint();

    /**
     * Calls actual {@code getGenTime} method for the wrapped TSTInfo object and gets date.
     *
     * @return the received {@link Date} object.
     */
    Date getGenTime() throws ParseException;
}
