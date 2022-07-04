package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.IRevokedStatus;

import org.bouncycastle.cert.ocsp.RevokedStatus;

public class RevokedStatusBC extends CertificateStatusBC implements IRevokedStatus {
    public RevokedStatusBC(RevokedStatus certificateStatus) {
        super(certificateStatus);
    }

    public RevokedStatus getRevokedStatus() {
        return (RevokedStatus) super.getCertificateStatus();
    }
}