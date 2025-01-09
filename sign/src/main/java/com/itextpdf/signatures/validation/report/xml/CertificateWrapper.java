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
package com.itextpdf.signatures.validation.report.xml;

import com.itextpdf.commons.utils.Base64;

import java.security.cert.CertificateEncodingException;
import java.security.cert.X509Certificate;
import java.util.Objects;

class CertificateWrapper extends AbstractCollectableObject {
    private final X509Certificate certificate;

    public CertificateWrapper(X509Certificate signingCertificate) {
        super("C");
        this.certificate = signingCertificate;
    }

    @Override
    public void accept(CollectableObjectVisitor visitor) {
        visitor.visit(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        CertificateWrapper that = (CertificateWrapper) o;
        return certificate.equals(that.certificate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(certificate);
    }

    public String getBase64ASN1Structure() {
        try {
            return Base64.encodeBytes(certificate.getEncoded());
        } catch (CertificateEncodingException e) {
            throw new RuntimeException("Error encoding certificate.", e);
        }
    }
}
