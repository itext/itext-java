package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.CertificateStatus;

public class CertificateStatusBCFips implements ICertificateStatus {
    private static final CertificateStatusBCFips INSTANCE = new CertificateStatusBCFips(null);

    private static final CertificateStatusBCFips GOOD = new CertificateStatusBCFips(CertificateStatus.GOOD);

    private final CertificateStatus certificateStatus;

    public CertificateStatusBCFips(CertificateStatus certificateStatus) {
        this.certificateStatus = certificateStatus;
    }

    public static CertificateStatusBCFips getInstance() {
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
        CertificateStatusBCFips that = (CertificateStatusBCFips) o;
        return Objects.equals(certificateStatus, that.certificateStatus);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificateStatus);
    }

    @Override
    public String toString() {
        return certificateStatus.toString();
    }
}
