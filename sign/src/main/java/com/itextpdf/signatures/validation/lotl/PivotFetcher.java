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
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.validation.SafeCalling;
import com.itextpdf.signatures.validation.TrustedCertificatesStore;
import com.itextpdf.signatures.validation.lotl.xml.XmlSaxProcessor;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static com.itextpdf.signatures.validation.lotl.LotlValidator.LOTL_VALIDATION;
import static com.itextpdf.signatures.validation.lotl.LotlValidator.LOTL_VALIDATION_UNSUCCESSFUL;
import static com.itextpdf.signatures.validation.lotl.LotlValidator.UNABLE_TO_RETRIEVE_PIVOT;

/**
 * This class fetches and validates pivot files from a List of Trusted Lists (Lotl) XML.
 */
public class PivotFetcher {
    private static final Logger LOGGER = LoggerFactory.getLogger(PivotFetcher.class);

    private final LotlService service;
    private String currentJournalUri;
    
    /**
     * Constructs a PivotFetcher with the specified LotlService and ValidatorChainBuilder.
     *
     * @param service the LotlService used to retrieve resources
     */
    public PivotFetcher(LotlService service) {
        this.service = service;
    }

    /**
     * Sets {@link String} constant representing currently used Official Journal publication.
     *
     * @param currentJournalUri {@link String} constant representing currently used Official Journal publication
     */
    public void setCurrentJournalUri(String currentJournalUri) {
        this.currentJournalUri = currentJournalUri;
    }

    /**
     * Fetches and validates pivot files from the provided Lotl XML.
     *
     * @param lotlXml      the byte array of the Lotl XML
     * @param certificates the list of trusted certificates
     *
     * @return a Result object containing the validation result and report items
     */
    public Result downloadAndValidatePivotFiles(byte[] lotlXml, List<Certificate> certificates) {
        if (lotlXml == null) {
            throw new PdfException(LotlValidator.UNABLE_TO_RETRIEVE_LOTL);
        }
        Result result = new Result();

        List<String> pivotsUrlList = getPivotsUrlList(lotlXml);
        List<String> ojUris = pivotsUrlList.stream()
                .filter(url -> XmlPivotsHandler.isOfficialJournal(url)).collect(Collectors.toList());
        if (ojUris.size() > 1) {
            LOGGER.warn(SignLogMessageConstant.OJ_TRANSITION_PERIOD);
        }
        result.setPivotUrls(pivotsUrlList);
        List<byte[]> pivotFiles = new ArrayList<>();

        // If we weren't able to find any OJ links, or current OJ uri is null, we process all the pivots.
        boolean startProcessing = ojUris.isEmpty() || currentJournalUri == null;
        // We need to process pivots backwards.
        for (int i = pivotsUrlList.size() - 1; i >= 0; i--) {
            String pivotUrl = pivotsUrlList.get(i);
            if (pivotUrl.equals(currentJournalUri)) {
                // We only need to process pivots which, were created after OJ entry was added.
                startProcessing = true;
                continue;
            }
            if (startProcessing && !XmlPivotsHandler.isOfficialJournal(pivotUrl)) {
                SafeCalling.onExceptionLog(
                        () -> pivotFiles.add(service.getResourceRetriever().getByteArrayByUrl(new URL(pivotUrl))),
                        result.getLocalReport(),
                        e -> new ReportItem(LOTL_VALIDATION, MessageFormatUtil.format(
                                UNABLE_TO_RETRIEVE_PIVOT, pivotUrl), e, ReportItem.ReportItemStatus.INVALID));
                if (result.getLocalReport().getValidationResult() != ValidationResult.VALID) {
                    return result;
                }
            }
        }

        List<Certificate> trustedCertificates = certificates;
        pivotFiles.add(lotlXml);
        for (byte[] pivotFile : pivotFiles) {
            TrustedCertificatesStore trustedCertificatesStore = new TrustedCertificatesStore();
            trustedCertificatesStore.addGenerallyTrustedCertificates(trustedCertificates);
            if (pivotFile == null) {
                result.getLocalReport().addReportItem(new ReportItem(LOTL_VALIDATION, LOTL_VALIDATION_UNSUCCESSFUL,
                        ReportItem.ReportItemStatus.INVALID));
                return result;
            }
            XmlSignatureValidator xmlSignatureValidator = service.getXmlSignatureValidator(trustedCertificatesStore);
            ValidationReport localReport = xmlSignatureValidator.validate(new ByteArrayInputStream(pivotFile));
            if (localReport.getValidationResult() != ValidationReport.ValidationResult.VALID) {
                result.getLocalReport().addReportItem(new ReportItem(LOTL_VALIDATION, LOTL_VALIDATION_UNSUCCESSFUL,
                        ReportItem.ReportItemStatus.INVALID));
                result.getLocalReport().merge(localReport);
                if (!ojUris.stream().anyMatch(ojUri -> ojUri.equals(currentJournalUri))) {
                    throw new PdfException(SignExceptionMessageConstant.OFFICIAL_JOURNAL_CERTIFICATES_OUTDATED);
                }
                return result;
            }
            XmlCertificateRetriever certificateRetriever = new XmlCertificateRetriever(
                    new XmlDefaultCertificateHandler());
            trustedCertificates = certificateRetriever.getCertificates(new ByteArrayInputStream(pivotFile));
        }
        return result;
    }

    /**
     * Gets list of pivots xml files, including OJ entries.
     *
     * @param lotlXml {@code byte} array representing main LOTL file
     *
     * @return list of pivots xml files, including OJ entries
     */
    protected List<String> getPivotsUrlList(byte[] lotlXml) {
        XmlPivotsHandler pivotsHandler = new XmlPivotsHandler();
        new XmlSaxProcessor().process(new ByteArrayInputStream(lotlXml), pivotsHandler);
        return pivotsHandler.getPivots();
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
