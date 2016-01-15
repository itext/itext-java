package com.itextpdf.signatures;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 * This class will return the {@link java.security.MessageDigest} associated with
 * a certain hashing algorithm returned by the specified provider.
 *
 * @author psoares
 */
public class ProviderDigest implements ExternalDigest {
    private String provider;

    /**
     * Creates a ProviderDigest.
     *
     * @param provider String name of the provider that you want to use to create the hash
     */
    public ProviderDigest(String provider) {
        this.provider = provider;
    }

    @Override
    public MessageDigest getMessageDigest(String hashAlgorithm) throws GeneralSecurityException{
        return DigestAlgorithms.getMessageDigest(hashAlgorithm, provider);
    }
}
