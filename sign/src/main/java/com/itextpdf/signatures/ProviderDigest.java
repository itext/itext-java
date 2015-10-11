package com.itextpdf.signatures;

import com.itextpdf.signatures.ExternalDigest;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;

/**
 *
 * @author psoares
 */
public class ProviderDigest implements ExternalDigest {
    private String provider;

    public ProviderDigest(String provider) {
        this.provider = provider;
    }

    public MessageDigest getMessageDigest(String hashAlgorithm) throws GeneralSecurityException{
        return DigestAlgorithms.getMessageDigest(hashAlgorithm, provider);
    }
}
