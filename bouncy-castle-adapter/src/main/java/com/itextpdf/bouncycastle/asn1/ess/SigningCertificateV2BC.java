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
import com.itextpdf.commons.bouncycastle.asn1.ess.IESSCertIDv2;
import com.itextpdf.commons.bouncycastle.asn1.ess.ISigningCertificateV2;

import org.bouncycastle.asn1.ess.ESSCertIDv2;
import org.bouncycastle.asn1.ess.SigningCertificateV2;

/**
 * Wrapper class for {@link SigningCertificateV2}.
 */
public class SigningCertificateV2BC extends ASN1EncodableBC implements ISigningCertificateV2 {
    /**
     * Creates new wrapper instance for {@link SigningCertificateV2}.
     *
     * @param signingCertificateV2 {@link SigningCertificateV2} to be wrapped
     */
    public SigningCertificateV2BC(SigningCertificateV2 signingCertificateV2) {
        super(signingCertificateV2);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SigningCertificateV2}.
     */
    public SigningCertificateV2 getSigningCertificateV2() {
        return (SigningCertificateV2) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IESSCertIDv2[] getCerts() {
        ESSCertIDv2[] certs = getSigningCertificateV2().getCerts();
        IESSCertIDv2[] certsBC = new IESSCertIDv2[certs.length];
        for (int i = 0; i < certsBC.length; i++) {
            certsBC[i] = new ESSCertIDv2BC(certs[i]);
        }
        return certsBC;
    }
}
