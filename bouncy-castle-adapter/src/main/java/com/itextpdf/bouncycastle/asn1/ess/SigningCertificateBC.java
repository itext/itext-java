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
package com.itextpdf.bouncycastle.asn1.ess;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertID;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificate;

import org.bouncycastle.asn1.ess.ESSCertID;
import org.bouncycastle.asn1.ess.SigningCertificate;

/**
 * Wrapper class for {@link SigningCertificate}.
 */
public class SigningCertificateBC extends ASN1EncodableBC implements ISigningCertificate {
    /**
     * Creates new wrapper instance for {@link SigningCertificate}.
     *
     * @param signingCertificate {@link SigningCertificate} to be wrapped
     */
    public SigningCertificateBC(SigningCertificate signingCertificate) {
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
        IESSCertID[] certsBC = new IESSCertID[certs.length];
        for (int i = 0; i < certsBC.length; i++) {
            certsBC[i] = new ESSCertIDBC(certs[i]);
        }
        return certsBC;
    }
}
