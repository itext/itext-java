package com.itextpdf.commons.bouncycastle.cert.jcajce;

import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.cert.AbstractCertIOException;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

/**
 * This interface represents the wrapper for JcaX509v3CertificateBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJcaX509v3CertificateBuilder {
    /**
     * Calls actual {@code build} method for the wrapped JcaX509v3CertificateBuilder object.
     *
     * @param contentSigner ContentSigner wrapper
     *
     * @return {IX509CertificateHolder} wrapper for built X509CertificateHolder object.
     */
    IX509CertificateHolder build(IContentSigner contentSigner);

    /**
     * Calls actual {@code addExtension} method for the wrapped JcaX509v3CertificateBuilder object.
     *
     * @param extensionOID   wrapper for the OID defining the extension type
     * @param critical       true if the extension is critical, false otherwise
     * @param extensionValue wrapped ASN.1 structure that forms the extension's value
     *
     * @return {@link IJcaX509v3CertificateBuilder} this wrapper object.
     *
     * @throws AbstractCertIOException CertIOException wrapper.
     */
    IJcaX509v3CertificateBuilder addExtension(IASN1ObjectIdentifier extensionOID, boolean critical,
            IASN1Encodable extensionValue) throws AbstractCertIOException;
}
