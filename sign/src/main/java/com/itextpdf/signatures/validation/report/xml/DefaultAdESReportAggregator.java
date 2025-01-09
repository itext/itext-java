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

import com.itextpdf.signatures.cms.CMSContainer;
import com.itextpdf.signatures.validation.report.xml.SignatureValidationStatus.MainIndication;
import com.itextpdf.signatures.validation.report.xml.SignatureValidationStatus.MessageType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Date;
import java.util.Stack;

/**
 * Use this implementation when an xml report has to be created
 */
public class DefaultAdESReportAggregator implements AdESReportAggregator {

    private static final Logger LOGGER = LoggerFactory.getLogger(DefaultAdESReportAggregator.class);

    private final ValidationObjects validationObjects = new ValidationObjects();
    private final PadesValidationReport report = new PadesValidationReport(validationObjects);
    private final Stack<SubValidationReport> validationReportStack = new Stack<SubValidationReport>();

    /**
     * Instantiates a new DefaultAdESReportAggregator instance
     */
    public DefaultAdESReportAggregator() {
        // Declaring default constructor explicitly to avoid removing it unintentionally
    }

    @Override
    public void startSignatureValidation(byte[] signature, String name, Date signingDate) {
        try {
            SignatureValidationReport currentSignatureValidationReport = new SignatureValidationReport(
                    validationObjects, new CMSContainer(signature), name, signingDate);
            validationReportStack.push(currentSignatureValidationReport);
            report.addSignatureValidationReport(currentSignatureValidationReport);
        } catch (Exception e) { // catching generic Exception here for portability
            LOGGER.error("Unable to parse signature container.", e);
            throw new IllegalArgumentException("Signature is not parsable", e);
        }
    }

    @Override
    public void proofOfExistenceFound(byte[] timeStampSignature, boolean document) {
        try {
            POEValidationReport currentValidationReport = new POEValidationReport(
                    validationObjects, new CMSContainer(timeStampSignature), document);
            validationReportStack.push(currentValidationReport);
            validationObjects.addObject(currentValidationReport);
        } catch (Exception e) { // catching generic Exception here for portability
            LOGGER.error("Unable to parse timestamp signature container.", e);
            throw new IllegalArgumentException("Timestamp signature is not parsable", e);
        }
    }

    @Override
    public void reportSignatureValidationSuccess() {
        SignatureValidationStatus status = new SignatureValidationStatus();
        status.setMainIndication(MainIndication.TOTAL_PASSED);
        SubValidationReport currentValidationReport = validationReportStack.pop();
        currentValidationReport.setSignatureValidationStatus(status);
    }

    @Override
    public void reportSignatureValidationFailure(boolean isInconclusive, String reason) {
        SignatureValidationStatus status = new SignatureValidationStatus();
        if (isInconclusive) {
            status.setMainIndication(MainIndication.INDETERMINATE);
        } else {
            status.setMainIndication(MainIndication.TOTAL_FAILED);
        }
        status.addMessage(reason, MessageType.ERROR);
        SubValidationReport currentValidationReport = validationReportStack.pop();
        currentValidationReport.setSignatureValidationStatus(status);

    }
//  code for future use commented out for code coverage
//    @Override
//    public void reportCertificateChainValidationSuccess(X509Certificate certificate) {
//        //will be completed later
//    }
//
//    @Override
//    public void reportCertificateChainValidationFailure(X509Certificate certificate, boolean isInconclusive,
//            String reason) {
//        //will be completed later
//    }
//
//    @Override
//    public void reportCRLValidationSuccess(X509Certificate certificate, CRL crl) {
//        //will be completed later
//    }
//
//    @Override
//    public void reportCRLValidationFailure(X509Certificate certificate, CRL crl, boolean isInconclusive,
//            String reason) {
//        //will be completed later
//    }
//
//    @Override
//    public void reportOCSPValidationSuccess(X509Certificate certificate, IBasicOCSPResp ocsp) {
//        //will be completed later
//    }
//
//    @Override
//    public void reportOCSPValidationFailure(X509Certificate certificate, IBasicOCSPResp ocsp, boolean isInconclusive,
//            String reason) {
//        //will be completed later
//    }

    @Override
    public PadesValidationReport getReport() {
        return report;
    }

}
