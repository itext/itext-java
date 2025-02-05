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
package com.itextpdf.bouncycastlefips.asn1.ess;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificate;

import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.SigningCertificate;

/**
 * Wrapper class for {@link SigningCertificate}.
 */
public class SigningCertificateBCFips extends ASN1EncodableBCFips implements ISigningCertificate {
    /**
     * Creates new wrapper instance for {@link SigningCertificate}.
     *
     * @param signingCertificate {@link SigningCertificate} to be wrapped
     */
    public SigningCertificateBCFips(SigningCertificate signingCertificate) {
        super(signingCertificate);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SigningCertificate}.
     */
    public SigningCertificate getSigningCertificate() {
        return (SigningCertificate) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IESSCertID[] getCerts() {
        ESSCertID[] certs = getSigningCertificate().getCerts();
        IESSCertID[] certsBCFips = new IESSCertID[certs.length];
        for (int i = 0; i < certsBCFips.length; i++) {
            certsBCFips[i] = new ESSCertIDBCFips(certs[i]);
        }
        return certsBCFips;
    }
}
