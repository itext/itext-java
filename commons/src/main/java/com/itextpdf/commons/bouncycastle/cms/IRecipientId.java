package com.itextpdf.commons.bouncycastle.cms;

import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;

/**
 * This interface represents the wrapper for RecipientId that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IRecipientId {
    /**
     * Calls actual {@code match} method for the wrapped RecipientId object.
     *
     * @param holder X509CertificateHolder wrapper
     *
     * @return boolean value.
     */
    boolean match(IX509CertificateHolder holder);
}
