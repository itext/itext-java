package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IRevokedStatus;

import org.bouncycastle.cert.ocsp.RevokedStatus;

/**
 * Wrapper class for {@link RevokedStatus}.
 */
public class RevokedStatusBC extends CertificateStatusBC implements IRevokedStatus {
    /**
     * Creates new wrapper instance for {@link RevokedStatus}.
     *
     * @param certificateStatus {@link RevokedStatus} to be wrapped
     */
    public RevokedStatusBC(RevokedStatus certificateStatus) {
        super(certificateStatus);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link RevokedStatus}.
     */
    public RevokedStatus getRevokedStatus() {
        return (RevokedStatus) super.getCertificateStatus();
    }
}