package com.itextpdf.commons.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.IX509CertificateHolder;
import com.itextpdf.commons.bouncycastle.operator.IContentVerifierProvider;

import java.io.IOException;
import java.util.Date;

/**
 * This interface represents the wrapper for BasicOCSPResp that provides the ability
 * to switch between bouncy-castle and bouncy-castle FIPS implementations.
 */
public interface IBasicOCSPResp {
    /**
     * Calls actual {@code getResponses} method for the wrapped BasicOCSPResp object.
     *
     * @return wrapped SingleResp list.
     */
    ISingleResp[] getResponses();

    /**
     * Calls actual {@code isSignatureValid} method for the wrapped BasicOCSPResp object.
     *
     * @param provider ContentVerifierProvider wrapper
     *
     * @return boolean value.
     *
     * @throws AbstractOCSPException OCSPException wrapper.
     */
    boolean isSignatureValid(IContentVerifierProvider provider) throws AbstractOCSPException;

    /**
     * Calls actual {@code getCerts} method for the wrapped BasicOCSPResp object.
     *
     * @return wrapped certificates list.
     */
    IX509CertificateHolder[] getCerts();

    /**
     * Calls actual {@code getEncoded} method for the wrapped BasicOCSPResp object.
     *
     * @return the default encoding for the wrapped object.
     *
     * @throws IOException on encoding error.
     */
    byte[] getEncoded() throws IOException;

    /**
     * Calls actual {@code getProducedAt} method for the wrapped BasicOCSPResp object.
     *
     * @return produced at date.
     */
    Date getProducedAt();
}
