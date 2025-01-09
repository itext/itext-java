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
 * Use this implementation when no xml report has to be created
 */
public class NullAdESReportAggregator implements AdESReportAggregator {

    /**
     * Creates a new instance of NullAdESReportAggregator.
     */
    public NullAdESReportAggregator() {
        // Declaring default constructor explicitly to avoid removing it unintentionally
    }

    @Override
    public void startSignatureValidation(byte[] signature, String name, Date signingDate) {
        // No action required
    }

    @Override
    public void proofOfExistenceFound(byte[] timeStampSignature, boolean document) {
        // No action required
    }

    @Override
    public void reportSignatureValidationSuccess() {
        // No action required
    }

    @Override
    public void reportSignatureValidationFailure(boolean isInconclusive, String reason) {
        // No action required
    }
//  code for future use commented out for code coverage
//    @Override
//    public void reportCertificateChainValidationSuccess(X509Certificate certificate) {
//        // No action required
//    }
//
//    @Override
//    public void reportCertificateChainValidationFailure(X509Certificate certificate, boolean isInconclusive,
//            String reason) {
//        // No action required
//    }
//
//    @Override
//    public void reportCRLValidationSuccess(X509Certificate certificate, CRL crl) {
//        // No action required
//    }
//
//    @Override
//    public void reportCRLValidationFailure(X509Certificate certificate, CRL crl, boolean isInconclusive,
//            String reason) {
//        // No action required
//    }
//
//    @Override
//    public void reportOCSPValidationSuccess(X509Certificate certificate, IBasicOCSPResp ocsp) {
//        // No action required
//    }
//
//    @Override
//    public void reportOCSPValidationFailure(X509Certificate certificate, IBasicOCSPResp ocsp, boolean isInconclusive,
//            String reason) {
//        // No action required
//    }

    @Override
    public PadesValidationReport getReport() {
        throw new UnsupportedOperationException(
                "Use another implementation of AdESReportAggregator to create an actual report");
    }

}
