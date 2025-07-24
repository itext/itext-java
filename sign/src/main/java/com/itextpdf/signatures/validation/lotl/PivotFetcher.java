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
package com.itextpdf.signatures.validation.lotl;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.validation.SignatureValidationProperties;
import com.itextpdf.signatures.validation.TrustedCertificatesStore;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.lotl.xml.XmlSaxProcessor;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.itextpdf.signatures.validation.lotl.LotlValidator.LOTL_VALIDATION;
import static com.itextpdf.signatures.validation.lotl.LotlValidator.LOTL_VALIDATION_UNSUCCESSFUL;
import static com.itextpdf.signatures.validation.lotl.LotlValidator.UNABLE_TO_RETRIEVE_PIVOT;

/**
 * This class fetches and validates pivot files from a List of Trusted Lists (Lotl) XML.
 */
public class PivotFetcher {

    private final LotlService service;
    private final ValidatorChainBuilder builder;

    public PivotFetcher(LotlService service, ValidatorChainBuilder builder) {
        this.service = service;
        this.builder = builder;
    }

    /**
     * Fetches and validates pivot files from the provided Lotl XML.
     *
     * @param lotlXml      the byte array of the Lotl XML
     * @param certificates the list of trusted certificates
     * @param properties   the signature validation properties
     *
     * @return a Result object containing the validation result and report items
     */
    public Result downloadAndValidatePivotFiles(byte[] lotlXml, List<Certificate> certificates,
            SignatureValidationProperties properties) {
        if (lotlXml == null || lotlXml.length == 0) {
            throw new PdfException(LotlValidator.UNABLE_TO_RETRIEVE_Lotl);
        }
        XmlPivotsHandler pivotsHandler = new XmlPivotsHandler();
        new XmlSaxProcessor().process(new ByteArrayInputStream(lotlXml), pivotsHandler);
        Result result = new Result();

        List<String> pivotsUrlList = pivotsHandler.getPivots();
        result.setPivotUrls(pivotsUrlList);
        List<byte[]> pivotFiles = new ArrayList<>();

        // We need to process pivots backwards.
        for (int i = pivotsUrlList.size() - 1; i >= 0; i--) {
            String pivotUrl = pivotsUrlList.get(i);
            try {
                pivotFiles.add(service.getResourceRetriever().getByteArrayByUrl(new URL(pivotUrl)));
            } catch (Exception e) {
                result.getLocalReport().addReportItem(
                        new ReportItem(LOTL_VALIDATION, MessageFormatUtil.format(UNABLE_TO_RETRIEVE_PIVOT, pivotUrl), e,
                                ReportItem.ReportItemStatus.INVALID));
                return result;
            }
        }

        List<Certificate> trustedCertificates = certificates;
        pivotFiles.add(lotlXml);
        for (byte[] pivotFile : pivotFiles) {
            TrustedCertificatesStore trustedCertificatesStore = new TrustedCertificatesStore();
            trustedCertificatesStore.addGenerallyTrustedCertificates(trustedCertificates);
            XmlSignatureValidator xmlSignatureValidator = this.builder.buildXmlSignatureValidator(
                    trustedCertificatesStore);
            if (pivotFile == null || pivotFile.length == 0) {
                result.getLocalReport().addReportItem(new ReportItem(LOTL_VALIDATION, LOTL_VALIDATION_UNSUCCESSFUL,
                        ReportItem.ReportItemStatus.INVALID));
                return result;
            }
            ValidationReport localReport = xmlSignatureValidator.validate(new ByteArrayInputStream(pivotFile));
            if (localReport.getValidationResult() != ValidationReport.ValidationResult.VALID) {
                result.getLocalReport().addReportItem(new ReportItem(LOTL_VALIDATION, LOTL_VALIDATION_UNSUCCESSFUL,
                        ReportItem.ReportItemStatus.INVALID));
                result.getLocalReport().merge(localReport);
                return result;
            }
            XmlCertificateRetriever certificateRetriever = new XmlCertificateRetriever(
                    new XmlDefaultCertificateHandler());
            trustedCertificates = certificateRetriever.getCertificates(new ByteArrayInputStream(pivotFile));
        }
        return result;
    }

    /**
     * Result class encapsulates the result of the pivot fetching and validation process.
     */
    public static class Result {
        private ValidationReport localReport = new ValidationReport();
        private List<String> pivotsUrlList = new ArrayList<>();

        /**
         * Creates a new instance of {@link Result} for {@link PivotFetcher}.
         */
        public Result() {
            // Default constructor.
        }

        /**
         * Gets the local validation report.
         *
         * @return the local ValidationReport
         */
        public ValidationReport getLocalReport() {
            return localReport;
        }

        /**
         * Sets the local validation report.
         *
         * @param localReport the ValidationReport to set
         */
        public void setLocalReport(ValidationReport localReport) {
            this.localReport = localReport;
        }

        /**
         * Gets the list of pivot URLs.
         *
         * @return a list of pivot URLs
         */
        public List<String> getPivotUrls() {
            if (pivotsUrlList == null) {
                return Collections.<String>emptyList();
            }
            return Collections.unmodifiableList(pivotsUrlList);
        }

        /**
         * Gets the list of pivot URLs.
         *
         * @param pivotsUrlList a list of pivot URLs
         */
        public void setPivotUrls(List<String> pivotsUrlList) {
            this.pivotsUrlList = pivotsUrlList;
        }

        /**
         * Generates a unique identifier based on the pivot URLs.
         *
         * @return a string representing the unique identifier
         */
        public String generateUniqueIdentifier() {
            return String.join("_", pivotsUrlList);
        }
    }
}
