package com.itextpdf.bouncycastlefips.cert.ocsp;

import com.itextpdf.commons.bouncycastle.cert.ocsp.ICertificateStatus;

import java.util.Objects;
import org.bouncycastle.cert.ocsp.CertificateStatus;

/**
 * Wrapper class for {@link CertificateStatus}.
 */
public class CertificateStatusBCFips implements ICertificateStatus {
    private static final CertificateStatusBCFips INSTANCE = new CertificateStatusBCFips(null);

    private static final CertificateStatusBCFips GOOD = new CertificateStatusBCFips(CertificateStatus.GOOD);

    private final CertificateStatus certificateStatus;

    /**
     * Creates new wrapper instance for {@link CertificateStatus}.
     *
     * @param certificateStatus {@link CertificateStatus} to be wrapped
     */
    public CertificateStatusBCFips(CertificateStatus certificateStatus) {
        this.certificateStatus = certificateStatus;
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link CertificateStatusBCFips} instance.
     */
    public static CertificateStatusBCFips getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link CertificateStatus}.
     */
    public CertificateStatus getCertificateStatus() {
        return certificateStatus;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ICertificateStatus getGood() {
        return GOOD;
    }

    /**
     * Indicates whether some other object is "equal to" this one. Compares wrapped objects.
     */
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

    /**
     * Returns a hash code value based on the wrapped object.
     */
    @Override
    public int hashCode() {
        return Objects.hash(certificateStatus);
    }

    /**
     * Delegates {@code toString} method call to the wrapped object.
     */
    @Override
    public String toString() {
        return certificateStatus.toString();
    }
}
