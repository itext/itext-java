package com.itextpdf.commons.bouncycastle.cert.jcajce;

import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;

import java.security.Provider;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

/**
 * This interface represents the wrapper for JcaX509CertificateConverter that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJcaX509CertificateConverter {
    /**
     * Calls actual {@code getCertificate} method for the wrapped JcaX509CertificateConverter object.
     *
     * @param certificateHolder X509CertificateHolder wrapper
     *
     * @return received X509Certificate.
     *
     * @throws CertificateException indicates certificate problems.
     */
    X509Certificate getCertificate(IX509CertificateHolder certificateHolder) throws CertificateException;

    /**
     * Calls actual {@code setProvider} method for the wrapped JcaX509CertificateConverter object.
     *
     * @param provider provider to set
     *
     * @return {@link IJcaX509CertificateConverter} this wrapped object.
     */
    IJcaX509CertificateConverter setProvider(Provider provider);
}
