package com.itextpdf.commons.bouncycastle.cert;

import com.itextpdf.commons.bouncycastle.asn1.x509.IAuthorityKeyIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectKeyIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectPublicKeyInfo;

/**
 * This interface represents the wrapper for X509ExtensionUtils that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IX509ExtensionUtils {
    /**
     * Calls actual {@code createAuthorityKeyIdentifier} method for the wrapped X509ExtensionUtils object.
     *
     * @param publicKeyInfo SubjectPublicKeyInfo wrapper
     *
     * @return {@link IAuthorityKeyIdentifier} wrapper for the created AuthorityKeyIdentifier.
     */
    IAuthorityKeyIdentifier createAuthorityKeyIdentifier(ISubjectPublicKeyInfo publicKeyInfo);

    /**
     * Calls actual {@code createSubjectKeyIdentifier} method for the wrapped X509ExtensionUtils object.
     *
     * @param publicKeyInfo SubjectPublicKeyInfo wrapper
     *
     * @return {@link ISubjectKeyIdentifier} wrapper for the created SubjectKeyIdentifier.
     */
    ISubjectKeyIdentifier createSubjectKeyIdentifier(ISubjectPublicKeyInfo publicKeyInfo);
}
