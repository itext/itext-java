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

import java.util.Date;

/**
 * The interface for AdES reports aggregator implementations.
 */
public interface AdESReportAggregator {
    /**
     * Called at the start of a signature validation
     *
     * @param signature   signature container as a byte[]
     * @param name        signature name
     * @param signingDate the signing date
     */
    void startSignatureValidation(byte[] signature, String name, Date signingDate);

    /**
     * Called when a timestamp is encountered
     *
     * @param timeStampSignature timestamp container as a byte[]
     * @param document           true when the timestamp is document level, false for a signature timestamp
     */
    void proofOfExistenceFound(byte[] timeStampSignature, boolean document);

    /**
     * Called after a successful validation of the current signature
     */
    void reportSignatureValidationSuccess();

    /**
     * Called after signature validation failed for the current signature
     *
     * @param isInconclusive {@code true} when validation is neither valid nor invalid, {@code false} when it is invalid
     * @param reason         the failure reason
     */
    void reportSignatureValidationFailure(boolean isInconclusive, String reason);
//  code for future use commented out for code coverage
//    /**
//     * Called after certificate chain validation success for the current signature
//     *
//     * @param certificate the certificate that was tested
//     */
//    void reportCertificateChainValidationSuccess(X509Certificate certificate);
//
//    /**
//     * Called after certificate chain validation failed for the current signature
//     *
//     * @param certificate    the validated certificate
//     * @param isInconclusive false when validation is neither valid or invalid
//     * @param reason         the failure reason
//     */
//    void reportCertificateChainValidationFailure(X509Certificate certificate, boolean isInconclusive, String reason);
//
//    /**
//     * Called after crl validation success for the current signature
//     *
//     * @param certificate    the validated certificate
//     * @param crl            the crl being tested
//     */
//    void reportCRLValidationSuccess(X509Certificate certificate, CRL crl);
//
//    /**
//     * Called after crl validation failed for the current signature
//     *
//     * @param certificate    the validated certificate
//     * @param crl            the crl being tested
//     * @param isInconclusive false when validation is neither valid or invalid
//     * @param reason         the failure reason
//     */
//    void reportCRLValidationFailure(X509Certificate certificate, CRL crl, boolean isInconclusive, String reason);
//
//    /**
//     * Called after ocsp validation success for the current signature
//     *
//     * @param certificate the validated certificate
//     * @param ocsp the
//     */
//    void reportOCSPValidationSuccess(X509Certificate certificate, IBasicOCSPResp ocsp);
//
//    /**
//     * Called after ocsp validation failed for the current signature
//     *
//     * @param certificate the validated certificate
//     * @param ocsp the
//     * @param isInconclusive false when validation is neither valid or invalid
//     * @param reason the failure reason
//     */
//    void reportOCSPValidationFailure(X509Certificate certificate, IBasicOCSPResp ocsp, boolean isInconclusive,
//            String reason);

    /**
     * Retrieves the generated report
     *
     * @return the generated report
     */
    PadesValidationReport getReport();
}
