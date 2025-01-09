/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2025 Apryse Group NV
    Authors: Apryse Software.

    This program is offered under a commercial and under the AGPL license.
    For commercial licensing, contact us at https://itextpdf.com/sales.  For AGPL licensing, see below.

    AGPL licensing:
    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU Affero General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU Affero General Public License for more details.

    You should have received a copy of the GNU Affero General Public License
    along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
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
