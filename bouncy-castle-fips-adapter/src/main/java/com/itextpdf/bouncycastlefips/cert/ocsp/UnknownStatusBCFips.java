package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IUnknownStatus;

import org.bouncycastle.cert.ocsp.UnknownStatus;

/**
 * Wrapper class for {@link UnknownStatus}.
 */
public class UnknownStatusBCFips extends CertificateStatusBCFips implements IUnknownStatus {
    /**
     * Creates new wrapper instance for {@link UnknownStatus}.
     *
     * @param certificateStatus {@link UnknownStatus} to be wrapped
     */
    public UnknownStatusBCFips(UnknownStatus certificateStatus) {
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