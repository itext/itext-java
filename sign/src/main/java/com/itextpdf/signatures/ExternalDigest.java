package com.itextpdf.signatures;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * ExternalDigest allows the use of implementations of {@link java.security.MessageDigest} other than {@link com.itextpdf.signatures.BouncyCastleDigest}.
 */
public interface ExternalDigest {

    /**
     * Returns the MessageDigest associated with the provided hashing algorithm.
     *
     * @param hashAlgorithm String value representing the hashing algorithm
     * @return MessageDigest
     * @throws GeneralSecurityException
     */
    MessageDigest getMessageDigest(String hashAlgorithm) throws GeneralSecurityException;
}
