package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IRevokedStatus;

import org.bouncycastle.cert.ocsp.RevokedStatus;

public class RevokedStatusBCFips extends CertificateStatusBCFips implements IRevokedStatus {
    public RevokedStatusBCFips(RevokedStatus certificateStatus) {
        super(certificateStatus);
    }

    public RevokedStatus getRevokedStatus() {
        return (RevokedStatus) super.getCertificateStatus();
    }
}
