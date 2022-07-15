package com.itextpdf.commons.bouncycastle.cms.jcajce;

import com.itextpdf.commons.bouncycastle.cms.ISignerInfoGenerator;
import com.itextpdf.commons.bouncycastle.operator.AbstractOperatorCreationException;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;

/**
 * This interface represents the wrapper for JcaSignerInfoGeneratorBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IJcaSignerInfoGeneratorBuilder {
    /**
     * Calls actual {@code build} method for the wrapped JcaSignerInfoGeneratorBuilder object.
     *
     * @param signer ContentSigner wrapper
     * @param cert   X509Certificate
     *
     * @return {@link ISignerInfoGenerator} the wrapper for built SignerInfoGenerator object.
     *
     * @throws AbstractOperatorCreationException wrapped OperatorCreationException.
     * @throws CertificateEncodingException      if an error occurs while attempting to encode a certificate.
     */
    ISignerInfoGenerator build(IContentSigner signer, X509Certificate cert)
            throws AbstractOperatorCreationException, CertificateEncodingException;
}
