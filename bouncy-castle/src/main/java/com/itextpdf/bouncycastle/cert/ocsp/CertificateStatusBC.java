package com.itextpdf.bouncycastle.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.CertificateStatus;

public class CertificateStatusBC implements ICertificateStatus {
    private static final CertificateStatusBC INSTANCE = new CertificateStatusBC(null);

    private static final CertificateStatusBC GOOD = new CertificateStatusBC(CertificateStatus.GOOD);

    private final CertificateStatus certificateStatus;

    public CertificateStatusBC(CertificateStatus certificateStatus) {
        this.certificateStatus = certificateStatus;
    }

    public static CertificateStatusBC getInstance() {
        return INSTANCE;
    }

    public CertificateStatus getCertificateStatus() {
        return certificateStatus;
    }

    @Override
    public ICertificateStatus getGood() {
        return GOOD;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CertificateStatusBC that = (CertificateStatusBC) o;
        return Objects.equals(certificateStatus, that.certificateStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateStatus);
    }
}