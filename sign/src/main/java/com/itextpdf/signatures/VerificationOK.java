/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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
package com.itextpdf.signatures;

import java.security.cert.X509Certificate;

/**
 * Class that informs you that the verification of a Certificate
 * succeeded using a specific CertificateVerifier and for a specific
 * reason.
 *
 * @deprecated starting from 8.0.5.
 * {@link com.itextpdf.signatures.validation.report.ReportItem} should be used instead.
 */
@Deprecated
public class VerificationOK {

    /** The certificate that was verified successfully. */
    protected X509Certificate certificate;
    /** The CertificateVerifier that was used for verifying. */
    protected Class<? extends CertificateVerifier> verifierClass;
    /** The reason why the certificate verified successfully. */
    protected String message;

    /**
     * Creates a VerificationOK object
     * @param certificate	the certificate that was successfully verified
     * @param verifierClass	the class that was used for verification
     * @param message		the reason why the certificate could be verified
     */
    public VerificationOK(X509Certificate certificate,
            Class<? extends CertificateVerifier> verifierClass, String message) {
        this.certificate = certificate;
        this.verifierClass = verifierClass;
        this.message = message;
    }

    /**
     * Return a single String explaining which certificate was verified, how and why.
     * @see java.lang.Object#toString()
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        if (certificate != null) {
            sb.append(certificate.getSubjectDN().getName());
            sb.append(" verified with ");
        }
        sb.append(verifierClass.getName());
        sb.append(": ");
        sb.append(message);
        return sb.toString();
    }
}
