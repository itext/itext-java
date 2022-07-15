package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.asn1.x509.IExtensions;
import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.operator.IContentSigner;

import java.util.Date;

/**
 * This interface represents the wrapper for BasicOCSPRespBuilder that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IBasicOCSPRespBuilder {
    /**
     * Calls actual {@code setResponseExtensions} method for the wrapped BasicOCSPRespBuilder object.
     *
     * @param extensions response extensions wrapper
     *
     * @return {@link IBasicOCSPRespBuilder} this wrapper object.
     */
    IBasicOCSPRespBuilder setResponseExtensions(IExtensions extensions);

    /**
     * Calls actual {@code addResponse} method for the wrapped BasicOCSPRespBuilder object.
     *
     * @param certID            wrapped certificate ID details
     * @param certificateStatus wrapped status of the certificate - wrapped null if okay
     * @param time              date this response was valid on
     * @param time1             date when next update should be requested
     * @param extensions        optional wrapped extensions
     *
     * @return {@link IBasicOCSPRespBuilder} this wrapper object.
     */
    IBasicOCSPRespBuilder addResponse(ICertificateID certID, ICertificateStatus certificateStatus, Date time,
            Date time1, IExtensions extensions);

    /**
     * Calls actual {@code build} method for the wrapped BasicOCSPRespBuilder object.
     *
     * @param signer ContentSigner wrapper
     * @param chain  list of wrapped X509CertificateHolder objects
     * @param time   produced at
     *
     * @return {@link IBasicOCSPResp} wrapper for built BasicOCSPResp object.
     *
     * @throws AbstractOCSPException wrapped OCSPException.
     */
    IBasicOCSPResp build(IContentSigner signer, IX509CertificateHolder[] chain, Date time) throws AbstractOCSPException;
}
