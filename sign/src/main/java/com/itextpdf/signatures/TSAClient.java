package com.itextpdf.signatures;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * Time Stamp Authority client (caller) interface.
 * <p>
 * Interface used by the PdfPKCS7 digital signature builder to call
 * Time Stamp Authority providing RFC 3161 compliant time stamp token.
 * @author Martin Brunecky, 07/17/2007
 * @since 2.1.6
 */
public interface TSAClient { // TODO: refactor docs

    /**
     * Get the time stamp estimated token size.
     * Implementation must return value large enough to accommodate the
     * entire token returned by {@link #getTimeStampToken(byte[])} prior
     * to actual {@link #getTimeStampToken(byte[])} call.
     * @return	an estimate of the token size
     */
    int getTokenSizeEstimate();

    /**
     * Returns the {@link MessageDigest} to digest the data imprint
     * @return The {@link MessageDigest} object.
     */
    MessageDigest getMessageDigest() throws GeneralSecurityException;

    /**
     * Returns RFC 3161 timeStampToken.
     * Method may return null indicating that timestamp should be skipped.
     * @param imprint byte[] - data imprint to be time-stamped
     * @return byte[] - encoded, TSA signed data of the timeStampToken
     * @throws Exception - TSA request failed
     */
    byte[] getTimeStampToken(byte[] imprint) throws Exception;
}