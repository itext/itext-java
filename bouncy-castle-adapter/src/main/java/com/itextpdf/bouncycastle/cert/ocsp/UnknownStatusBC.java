package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IUnknownStatus;

import org.bouncycastle.cert.ocsp.UnknownStatus;

/**
 * Wrapper class for {@link UnknownStatus}.
 */
public class UnknownStatusBC extends CertificateStatusBC implements IUnknownStatus {
    /**
     * Creates new wrapper instance for {@link UnknownStatus}.
     *
     * @param certificateStatus {@link UnknownStatus} to be wrapped
     */
    public UnknownStatusBC(UnknownStatus certificateStatus) {
        super(certificateStatus);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link UnknownStatus}.
     */
    public UnknownStatus getUnknownStatus() {
        return (UnknownStatus) super.getCertificateStatus();
    }
}