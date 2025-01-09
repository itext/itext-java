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
package com.itextpdf.bouncycastle.asn1.ocsp;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.ocsp.IOCSPResponseStatus;

import org.bouncycastle.asn1.ocsp.OCSPResponseStatus;

/**
 * Wrapper class for {@link OCSPResponseStatus}.
 */
public class OCSPResponseStatusBC extends ASN1EncodableBC implements IOCSPResponseStatus {
    private static final OCSPResponseStatusBC INSTANCE = new OCSPResponseStatusBC(null);

    private static final int SUCCESSFUL = OCSPResponseStatus.SUCCESSFUL;

    /**
     * Creates new wrapper instance for {@link OCSPResponseStatus}.
     *
     * @param ocspResponseStatus {@link OCSPResponseStatus} to be wrapped
     */
    public OCSPResponseStatusBC(OCSPResponseStatus ocspResponseStatus) {
        super(ocspResponseStatus);
    }

    /**
     * Gets wrapper instance.
     *
     * @return {@link OCSPResponseStatusBC} instance.
     */
    public static OCSPResponseStatusBC getInstance() {
        return INSTANCE;
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link OCSPResponseStatus}.
     */
    public OCSPResponseStatus getOcspResponseStatus() {
        return (OCSPResponseStatus) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getSuccessful() {
        return SUCCESSFUL;
    }
}
