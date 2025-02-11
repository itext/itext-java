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
package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1IntegerBC;
import com.itextpdf.bouncycastle.asn1.x500.X500NameBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Integer;
import com.itextpdf.commons.bouncycastle.asn1.x500.IX500Name;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectPublicKeyInfo;
import com.itextpdf.commons.bouncycastle.asn1.x509.ITBSCertificate;

import org.bouncycastle.asn1.x509.TBSCertificate;

/**
 * Wrapper class for {@link TBSCertificate}.
 */
public class TBSCertificateBC extends ASN1EncodableBC implements ITBSCertificate {
    /**
     * Creates new wrapper instance for {@link TBSCertificate}.
     *
     * @param tbsCertificate {@link TBSCertificate} to be wrapped
     */
    public TBSCertificateBC(TBSCertificate tbsCertificate) {
        super(tbsCertificate);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link TBSCertificate}.
     */
    public TBSCertificate getTBSCertificate() {
        return (TBSCertificate) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return new SubjectPublicKeyInfoBC(getTBSCertificate().getSubjectPublicKeyInfo());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IX500Name getIssuer() {
        return new X500NameBC(getTBSCertificate().getIssuer());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Integer getSerialNumber() {
        return new ASN1IntegerBC(getTBSCertificate().getSerialNumber());
    }
}
