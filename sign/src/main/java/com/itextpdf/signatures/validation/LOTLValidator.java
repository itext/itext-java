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
package com.itextpdf.signatures.validation;

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.io.resolver.resource.DefaultResourceRetriever;
import com.itextpdf.signatures.CertificateUtil;
import com.itextpdf.signatures.validation.XmlCountryRetriever.CountrySpecificLotl;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;
import com.itextpdf.signatures.validation.xml.XmlSaxProcessor;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

class LOTLValidator {
    static final String LOTL_VALIDATION = "LOTL validation.";
    static final String JOURNAL_CERT_NOT_PARSABLE = "One of EU Journal trusted certificates in not parsable. "
            + "It will be ignored.";
    static final String COUNTRY_SPECIFIC_LOTL_NOT_VALIDATED =
            "Country specific LOTL file: {0}, {1} wasn't successfully validated. It will be ignored.";
    static final String COULD_NOT_RESOLVE_URL = "Couldn't resolve {0} url. This TSL Location will be ignored.";
    static final String LOTL_VALIDATION_UNSUCCESSFUL =
            "LOTL chain validation wasn't successful, trusted certificates were not parsed.";
    static final String UNABLE_TO_RETRIEVE_PIVOT =
            "Unable to retrieve pivot LOTL with {0} url. LOTL validation isn't successful.";
    static final String UNABLE_TO_RETRIEVE_LOTL =
            "Unable to retrieve main LOTL file. LOTL validation isn't successful.";

    private final ValidatorChainBuilder builder;
    private final List<IServiceContext> nationalTrustedCertificates = new ArrayList<>();

    protected LOTLValidator(ValidatorChainBuilder builder) {
        this.builder = builder;
    }

    public List<IServiceContext> getNationalTrustedCertificates() {
        return new ArrayList<>(nationalTrustedCertificates);
    }

    public ValidationReport validate() {
        ValidationReport report = new ValidationReport();
        byte[] lotlXml = getLotlBytes();
        if (lotlXml == null) {
            report.addReportItem(new ReportItem(LOTL_VALIDATION, UNABLE_TO_RETRIEVE_LOTL, ReportItemStatus.INVALID));
            return report;
        }
        if (validatePivotFiles(report, lotlXml)) {
            validateCountrySpecificLotls(report, lotlXml);
        }
        return report;
    }

    protected byte[] getLotlBytes() {
        byte[] lotlXml;
        try {
            lotlXml = new EuropeanListOfTrustedListFetcher(new DefaultResourceRetriever()).getLotlData();
        } catch (Exception e) {
            return null;
        }
        return lotlXml;
    }

    protected List<Certificate> getEUJournalCertificates(ValidationReport report) {
        return new EuropeanTrustedListConfiguration().getCertificates().stream()
                .map(certificateWithHash -> {
                    try {
                        return CertificateUtil.readCertificatesFromPem(
                                new ByteArrayInputStream(certificateWithHash.getPemCertificate().getBytes(
                                        StandardCharsets.UTF_8)))[0];
                    } catch (Exception e) {
                        report.addReportItem(
                                new ReportItem(LOTL_VALIDATION, JOURNAL_CERT_NOT_PARSABLE, e, ReportItemStatus.INFO));
                        return null;
                    }
                }).filter(certificate -> certificate != null).collect(Collectors.toList());
    }

    private void validateCountrySpecificLotls(ValidationReport report, byte[] lotlXml) {
        XmlCertificateRetriever certificateRetriever = new XmlCertificateRetriever(new XmlDefaultCertificateHandler());
        List<Certificate> lotlTrustedCertificates =
                certificateRetriever.getCertificates(new ByteArrayInputStream(lotlXml));

        XmlCountryRetriever countryRetriever = new XmlCountryRetriever();
        List<CountrySpecificLotl> countrySpecificLotls =
                countryRetriever.getAllCountriesLotlFilesLocation(new ByteArrayInputStream(lotlXml));
        ValidatorChainBuilder newValidatorChainBuilder = new ValidatorChainBuilder()
                .withSignatureValidationProperties(builder.getProperties());
        newValidatorChainBuilder.withTrustedCertificates(lotlTrustedCertificates);
        DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever();

        for (CountrySpecificLotl countrySpecificLotl : countrySpecificLotls) {
            XmlSignatureValidator xmlSignatureValidator = newValidatorChainBuilder.getXmlSignatureValidator();
            byte[] countryLotlBytes;
            try {
                countryLotlBytes = resourceRetriever.getByteArrayByUrl(
                        new URL(countrySpecificLotl.getTslLocation()));
            } catch (Exception e) {
                report.addReportItem(new ReportItem(LOTL_VALIDATION, MessageFormatUtil.format(
                        COULD_NOT_RESOLVE_URL, countrySpecificLotl.getTslLocation()), e, ReportItemStatus.INFO));
                continue;
            }
            ValidationReport localReport = xmlSignatureValidator.validate(new ByteArrayInputStream(countryLotlBytes));
            if (localReport.getValidationResult() == ValidationResult.VALID) {
                XmlCertificateRetriever countryCertificateRetriever =
                        new XmlCertificateRetriever(new XmlCountryCertificateHandler());
                countryCertificateRetriever.getCertificates(new ByteArrayInputStream(countryLotlBytes));
                nationalTrustedCertificates.addAll(countryCertificateRetriever.getServiceContexts());
            } else {
                report.addReportItem(new ReportItem(LOTL_VALIDATION, MessageFormatUtil.format(
                        COUNTRY_SPECIFIC_LOTL_NOT_VALIDATED,
                        countrySpecificLotl.getSchemeTerritory(),
                        countrySpecificLotl.getTslLocation()), ReportItemStatus.INFO));
                report.mergeWithDifferentStatus(localReport, ReportItemStatus.INFO);
            }
        }
    }

    private boolean validatePivotFiles(ValidationReport report, byte[] lotlXml) {
        List<byte[]> pivotFiles = getPivotsFiles(report, lotlXml);
        if (pivotFiles == null) {
            return false;
        }
        List<Certificate> trustedCertificates = getEUJournalCertificates(report);
        pivotFiles.add(lotlXml);
        for (byte[] pivotFile : pivotFiles) {
            ValidatorChainBuilder newValidatorChainBuilder = new ValidatorChainBuilder()
                    .withSignatureValidationProperties(builder.getProperties());
            newValidatorChainBuilder.withTrustedCertificates(trustedCertificates);
            XmlSignatureValidator xmlSignatureValidator = newValidatorChainBuilder.getXmlSignatureValidator();
            ValidationReport localReport = xmlSignatureValidator.validate(new ByteArrayInputStream(pivotFile));
            if (localReport.getValidationResult() != ValidationResult.VALID) {
                report.addReportItem(
                        new ReportItem(LOTL_VALIDATION, LOTL_VALIDATION_UNSUCCESSFUL, ReportItemStatus.INVALID));
                report.merge(localReport);
                return false;
            }
            XmlCertificateRetriever certificateRetriever =
                    new XmlCertificateRetriever(new XmlDefaultCertificateHandler());
            trustedCertificates = certificateRetriever.getCertificates(new ByteArrayInputStream(pivotFile));
        }
        return true;
    }

    private List<byte[]> getPivotsFiles(ValidationReport report, byte[] lotlXml) {
        XmlPivotsHandler pivotsHandler = new XmlPivotsHandler();
        new XmlSaxProcessor().process(new ByteArrayInputStream(lotlXml), pivotsHandler);

        List<String> pivots = pivotsHandler.getPivots();
        DefaultResourceRetriever resourceRetriever = new DefaultResourceRetriever();
        List<byte[]> pivotFiles = new ArrayList<>();
        // We need to process pivots backwards.
        for (int i = pivots.size() - 1; i >= 0; i--) {
            String pivot = pivots.get(i);
            try {
                pivotFiles.add(resourceRetriever.getByteArrayByUrl(new URL(pivot)));
            } catch (Exception e) {
                report.addReportItem(new ReportItem(LOTL_VALIDATION,
                        MessageFormatUtil.format(UNABLE_TO_RETRIEVE_PIVOT, pivot), e, ReportItemStatus.INVALID));
                return null;
            }
        }
        return pivotFiles;
    }
}
