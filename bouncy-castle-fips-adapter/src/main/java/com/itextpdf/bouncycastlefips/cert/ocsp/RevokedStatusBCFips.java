package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IRevokedStatus;

import org.bouncycastle.cert.ocsp.RevokedStatus;

/**
 * Wrapper class for {@link RevokedStatus}.
 */
public class RevokedStatusBCFips extends CertificateStatusBCFips implements IRevokedStatus {
    /**
     * Creates new wrapper instance for {@link RevokedStatus}.
     *
     * @param certificateStatus {@link RevokedStatus} to be wrapped
     */
    public RevokedStatusBCFips(RevokedStatus certificateStatus) {
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
