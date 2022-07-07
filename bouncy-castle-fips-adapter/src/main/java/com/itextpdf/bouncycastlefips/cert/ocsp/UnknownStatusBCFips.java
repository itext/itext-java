package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IUnknownStatus;

import org.bouncycastle.cert.ocsp.UnknownStatus;

public class UnknownStatusBCFips extends CertificateStatusBCFips implements IUnknownStatus {
    public UnknownStatusBCFips(UnknownStatus certificateStatus) {
        super(certificateStatus);
    }

    public UnknownStatus getUnknownStatus() {
        return (UnknownStatus) super.getCertificateStatus();
    }
}