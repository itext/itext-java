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
import com.itextpdf.io.resolver.resource.DefaultResourceRetriever;
import com.itextpdf.signatures.validation.EuropeanTrustedListConfigurationFactory;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.lotl.xml.XmlSaxProcessor;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static com.itextpdf.signatures.validation.lotl.XmlCountryRetriever.CountrySpecificLotl;
import static com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;

/**
 * Class responsible for complete LOTL validation.
 */
public class LOTLValidator {
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
    static final String LOTL_FETCHING_PROPERTIES_NOT_PROVIDED =
            "LOTL fetching properties have to be provided in order to use LOTL Validator. "
                    + "See \"ValidationChainBuilder#withLOTLFetchingProperties\"";

    private final ValidatorChainBuilder builder;
    private final List<CountryServiceContext> nationalTrustedCertificates = new ArrayList<>();

    /**
     * Creates new {@link LOTLValidator} instance. This constructor shall not be used directly.
     * Instead, in order to create such instance {@link ValidatorChainBuilder#getLotlValidator()} shall be used.
     *
     * @param builder {@link ValidatorChainBuilder} which was responsible for creation
     */
    public LOTLValidator(ValidatorChainBuilder builder) {
        this.builder = builder;
    }

    private static List<CountryServiceContext> mapIServiceContextToCountry(List<IServiceContext> serviceContexts) {
        return serviceContexts.stream()
                .map(serviceContext -> serviceContext instanceof CountryServiceContext ?
                        (CountryServiceContext) serviceContext : null)
                .filter(countryServiceContext -> countryServiceContext != null).collect(Collectors.toList());
    }

    /**
     * Validates the List of Trusted Lists (LOTL) and country-specific LOTLs.
     *
     * @return a {@link ValidationReport} containing the results of the validation
     */
    public ValidationReport validate() {
        ValidationReport report = new ValidationReport();
        if (builder.getLotlFetchingProperties() == null) {
            report.addReportItem(new ReportItem(
                    LOTL_VALIDATION, LOTL_FETCHING_PROPERTIES_NOT_PROVIDED, ReportItemStatus.INVALID));
            return report;
        }
        byte[] lotlXml = null;
        try {
            lotlXml = getLotlBytes();
            if (lotlXml == null) {
                report.addReportItem(
                        new ReportItem(LOTL_VALIDATION, UNABLE_TO_RETRIEVE_LOTL, ReportItemStatus.INVALID));
                return report;
            }
        } catch (Exception e) {
            report.addReportItem(new ReportItem(LOTL_VALIDATION, MessageFormatUtil.format(
                    UNABLE_TO_RETRIEVE_LOTL, e.getMessage()), e, ReportItemStatus.INVALID));
            return report;
        }
        if (validatePivotFiles(report, lotlXml)) {
            validateCountrySpecificLotls(report, lotlXml);
        }
        return report;
    }

    /**
     * Gets the bytes of a main LOTL file.
     *
     * @return {@code byte[]} array representing main LOTL file
     * @throws IOException if there is an error retrieving the LOTL file
     */
    protected byte[] getLotlBytes() throws IOException {
        byte[] lotlXml;
        lotlXml = new EuropeanListOfTrustedListFetcher(new DefaultResourceRetriever()).getLotlData();
        return lotlXml;
    }

    /**
     * Gets EU Journal Certificates. These certificates are essential for main LOTL file validation.
     * The certificates in here are intended to be unconditionally trusted.
     * <p>
     * By default, this method retrieves the certificates from itext-lotl-resources repository.
     * However, it is possible to override this method and provide certificates manually.
     *
     * @param report {@link ValidationReport} to report validation related information
     * @return list of {@link Certificate} objects representing EU Journal certificates
     */
    protected List<Certificate> getEUJournalCertificates(ValidationReport report) {
        EuropeanTrustedListConfigurationFactory factory = EuropeanTrustedListConfigurationFactory.getFactory().get();
        try {
            return factory.getCertificates();
        } catch (Exception e) {
            report.addReportItem(
                    new ReportItem(LOTL_VALIDATION, JOURNAL_CERT_NOT_PARSABLE, e, ReportItemStatus.INFO));
            return new ArrayList<>();
        }
    }

    List<CountryServiceContext> getNationalTrustedCertificates() {
        return new ArrayList<>(nationalTrustedCertificates);
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

    private void validateCountrySpecificLotls(ValidationReport report, byte[] lotlXml) {
        List<CountrySpecificLotl> countrySpecificLotls = getCountrySpecificLotls(lotlXml);
        ValidatorChainBuilder newValidatorChainBuilder = getNewValidatorChainBuilder(lotlXml);
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
                XmlCertificateRetriever countryCertificateRetriever = new XmlCertificateRetriever(
                        new XmlCountryCertificateHandler(builder.getLotlFetchingProperties().getServiceTypes()));
                countryCertificateRetriever.getCertificates(new ByteArrayInputStream(countryLotlBytes));
                nationalTrustedCertificates.addAll(
                        mapIServiceContextToCountry(countryCertificateRetriever.getServiceContexts()));
            } else {
                report.addReportItem(new ReportItem(LOTL_VALIDATION, MessageFormatUtil.format(
                        COUNTRY_SPECIFIC_LOTL_NOT_VALIDATED,
                        countrySpecificLotl.getSchemeTerritory(),
                        countrySpecificLotl.getTslLocation()), ReportItemStatus.INFO));
                report.mergeWithDifferentStatus(localReport, ReportItemStatus.INFO);
            }
        }
    }

    private ValidatorChainBuilder getNewValidatorChainBuilder(byte[] lotlXml) {
        XmlCertificateRetriever certificateRetriever = new XmlCertificateRetriever(new XmlDefaultCertificateHandler());
        List<Certificate> lotlTrustedCertificates =
                certificateRetriever.getCertificates(new ByteArrayInputStream(lotlXml));
        ValidatorChainBuilder newValidatorChainBuilder = new ValidatorChainBuilder()
                .withSignatureValidationProperties(builder.getProperties());
        newValidatorChainBuilder.withTrustedCertificates(lotlTrustedCertificates);
        return newValidatorChainBuilder;
    }

    private List<CountrySpecificLotl> getCountrySpecificLotls(byte[] lotlXml) {
        XmlCountryRetriever countryRetriever = new XmlCountryRetriever();
        List<CountrySpecificLotl> countrySpecificLotls =
                countryRetriever.getAllCountriesLotlFilesLocation(new ByteArrayInputStream(lotlXml));
        Set<String> schemaNames = builder.getLotlFetchingProperties().getSchemaNames();
        if (!schemaNames.isEmpty()) {
            // Ignored country specific LOTL files which were not requested.
            return countrySpecificLotls.stream().filter(countrySpecificLotl ->
                    schemaNames.contains(countrySpecificLotl.getSchemeTerritory())).collect(Collectors.toList());
        }
        return countrySpecificLotls;
    }
}
