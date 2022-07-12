package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IUnknownStatus;

import org.bouncycastle.cert.ocsp.UnknownStatus;

public class UnknownStatusBC extends CertificateStatusBC implements IUnknownStatus {
    public UnknownStatusBC(UnknownStatus certificateStatus) {
        super(certificateStatus);
    }

    public UnknownStatus getUnknownStatus() {
        return (UnknownStatus) super.getCertificateStatus();
    }
}