package com.itextpdf.signatures;

import java.security.GeneralSecurityException;

/**
 * Interface that needs to be implemented to do the actual signing.
 * For instance: you'll have to implement this interface if you want
 * to sign a PDF using a smart card.
 * @author Paulo Soares
 */
public interface ExternalSignature {

    /**
     * Returns the hash algorithm.
     * @return	The hash algorithm (e.g. "SHA-1", "SHA-256,...").
     */
    String getHashAlgorithm();

    /**
     * Returns the encryption algorithm used for signing.
     * @return The encryption algorithm ("RSA" or "DSA").
     */
    String getEncryptionAlgorithm();

    /**
     * Signs the given message using the encryption algorithm in combination
     * with the hash algorithm.
     * @param message The message you want to be hashed and signed.
     * @return	A signed message digest.
     * @throws GeneralSecurityException
     */
    byte[] sign(byte[] message) throws GeneralSecurityException;
}
