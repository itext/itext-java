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
package com.itextpdf.signatures.validation.context;

/**
 * This enum lists all possible contexts related to the certificate origin in which a validation may take place
 */
public enum CertificateSource {
    /**
     * The context while validating a CRL issuer certificate.
     */
    CRL_ISSUER,
    /**
     * The context while validating a OCSP issuer certificate that is neither trusted nor CA.
     */
    OCSP_ISSUER,
    /**
     * The context while validating a certificate issuer certificate.
     */
    CERT_ISSUER,
    /**
     * The context while validating a signer certificate.
     */
    SIGNER_CERT,
    /**
     * A certificate that is on a trusted list.
     */
    TRUSTED,
    /**
     * The context while validating a timestamp issuer certificate.
     */
    TIMESTAMP
}
