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
