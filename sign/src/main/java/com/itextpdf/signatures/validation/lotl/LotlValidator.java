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

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.validation.ValidatorChainBuilder;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;

import java.util.ArrayList;
import java.util.List;

/**
 * LotlValidator is responsible for validating the List of Trusted Lists (Lotl) and managing the trusted certificates.
 * It fetches the Lotl, validates it, and retrieves country-specific entries.
 */
public class LotlValidator {
    public static final String LOTL_VALIDATION = "Lotl validation.";
    static final String JOURNAL_CERT_NOT_PARSABLE = "One of EU Journal trusted certificates in not parsable. " + "It "
            + "will be ignored.";
    static final String LOTL_FETCHING_PROPERTIES_NOT_PROVIDED = "Lotl fetching properties have to be provided in " +
            "order to use Lotl Validator. " + "See \"ValidationChainBuilder#withLotlFetchingProperties\"";
    static final String LOTL_VALIDATION_UNSUCCESSFUL = "Lotl chain validation wasn't successful, trusted " +
            "certificates" + " were not parsed.";
    static final String UNABLE_TO_RETRIEVE_PIVOT = "Unable to retrieve pivot Lotl with {0} url. Lotl validation " +
            "isn't" + " successful.";
    static final String UNABLE_TO_RETRIEVE_Lotl = "Unable to retrieve main Lotl file. Lotl validation isn't " +
            "successful.";
    static final Object GLOBAL_SERVICE_LOCK = new Object();
    static boolean WAS_INITIALIZED = false;
    static LotlService GLOBAL_SERVICE;
    private final ValidatorChainBuilder builder;
    private final List<CountryServiceContext> nationalTrustedCertificates = new ArrayList<>();
    private LotlService service = GLOBAL_SERVICE;

    /**
     * Constructs a LotlValidator with the specified ValidatorChainBuilder.
     *
     * @param builder the ValidatorChainBuilder to use for validation
     */
    public LotlValidator(ValidatorChainBuilder builder) {
        this.builder = builder;
    }


    /**
     * Initializes the global cache with the provided properties.
     * This method must be called before using the LotlValidator to ensure that the cache is set up.
     * <p>
     * If you are using a custom implementation of {@link LotlService} you can use the instance method.
     *
     * @param properties the LotlFetchingProperties to use for initializing the cache
     */
    public static void initializeGlobalCache(ValidatorChainBuilder properties) {
        synchronized (GLOBAL_SERVICE_LOCK) {
            if (WAS_INITIALIZED) {
                throw new PdfException(SignExceptionMessageConstant.CACHE_ALREADY_INITIALIZED);
            }
            if (GLOBAL_SERVICE == null) {
                GLOBAL_SERVICE = new LotlService(properties);
                GLOBAL_SERVICE.initializeCache();
                WAS_INITIALIZED = true;
            }
        }
    }

    /**
     * Sets the cache for the LotlValidator.
     *
     * @param service the LotlService instance to use as a cache for Lotl data
     *
     * @return the current instance of LotlValidator for method chaining
     */
    public LotlValidator withService(LotlService service) {
        this.service = service;
        return this;
    }

    /**
     * Validates the List of Trusted Lists (Lotl) and retrieves national trusted certificates.
     *
     * @return a ValidationReport containing the results of the validation
     */
    public ValidationReport validate() {
        if (service == null || !this.service.isCacheInitialized()) {
            throw new PdfException(SignExceptionMessageConstant.CACHE_NOT_INITIALIZED);
        }
        ValidationReport report = new ValidationReport();
        if (builder.getLotlFetchingProperties() == null) {
            report.addReportItem(
                    new ReportItem(LOTL_VALIDATION, LOTL_FETCHING_PROPERTIES_NOT_PROVIDED, ReportItemStatus.INVALID));
            return report;
        }
        EuropeanLotlFetcher.Result lotl = service.getLotlBytes();
        if (!lotl.getLocalReport().getLogs().isEmpty()) {
            report.merge(lotl.getLocalReport());
            return report;
        }

        EuropeanResourceFetcher.Result europeanResult = service.getEUJournalCertificates();
        report.merge(europeanResult.getLocalReport());

        PivotFetcher.Result result = service.getAndValidatePivotFiles(lotl.getLotlXml(),
                europeanResult.getCertificates(), builder.getProperties());

        report.merge(result.getLocalReport());
        if (result.getLocalReport().getValidationResult() != ValidationResult.VALID) {
            return report;
        }

        try {
            List<CountrySpecificLotlFetcher.Result> entries = service.getCountrySpecificLotlFiles(lotl.getLotlXml(),
                    this.builder);

            for (CountrySpecificLotlFetcher.Result countrySpecificResult : entries) {
                // When cache was loaded without config it still contains all country specific Lotl files.
                // So we need to filter them out if schema names were provided.
                if (!this.builder.getLotlFetchingProperties()
                        .shouldProcessCountry(countrySpecificResult.getCountrySpecificLotl().getSchemeTerritory())) {
                    continue;
                }
                report.merge(countrySpecificResult.getLocalReport());
                this.nationalTrustedCertificates.addAll(
                        LotlTrustedStore.mapIServiceContextToCountry(countrySpecificResult.getContexts()));
            }
        } catch (Exception e) {
            report.addReportItem(new ReportItem(LOTL_VALIDATION, "Country specific Lotl files validation failed.", e,
                    ReportItemStatus.INVALID));
        }
        return report;
    }

    /**
     * Retrieves national trusted certificates.
     *
     * @return the list of national trusted certificates
     */
    List<IServiceContext> getNationalTrustedCertificates() {
        return new ArrayList<>(nationalTrustedCertificates);
    }
}

