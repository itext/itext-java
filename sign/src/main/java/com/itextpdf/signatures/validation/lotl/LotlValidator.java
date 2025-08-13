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
    static final String UNABLE_TO_RETRIEVE_LOTL = "Unable to retrieve main Lotl file. Lotl validation isn't " +
            "successful.";
    private final List<CountryServiceContext> nationalTrustedCertificates = new ArrayList<>();
    private final LotlService service;

    /**
     * Constructs a LotlValidator with the specified ValidatorChainBuilder.
     *
     * @param service {@link LotlService} from which this instance was created.
     */
    public LotlValidator(LotlService service) {
        this.service = service;
    }

    /**
     * Validates the List of Trusted Lists (Lotl) and retrieves national trusted certificates.
     *
     * @return a ValidationReport containing the results of the validation
     */
    public ValidationReport validate() {
        ValidationReport report = new ValidationReport();
        EuropeanLotlFetcher.Result lotl = service.getLotlBytes();
        if (!lotl.getLocalReport().getLogs().isEmpty()) {
            report.merge(lotl.getLocalReport());
            return report;
        }

        EuropeanResourceFetcher.Result europeanResult = service.getEUJournalCertificates();
        report.merge(europeanResult.getLocalReport());

        PivotFetcher.Result result = service.getAndValidatePivotFiles(lotl.getLotlXml(),
                europeanResult.getCertificates());

        report.merge(result.getLocalReport());
        if (result.getLocalReport().getValidationResult() != ValidationResult.VALID) {
            return report;
        }

        List<CountrySpecificLotlFetcher.Result> entries = service.getCountrySpecificLotlFiles(lotl.getLotlXml());

        for (CountrySpecificLotlFetcher.Result countrySpecificResult : entries) {
            // When cache was loaded without config it still contains all country specific Lotl files.
            // So we need to filter them out if schema names were provided.
            if (!this.service.getLotlFetchingProperties()
                    .shouldProcessCountry(countrySpecificResult.getCountrySpecificLotl().getSchemeTerritory())) {
                continue;
            }
            report.merge(countrySpecificResult.getLocalReport());
            this.nationalTrustedCertificates.addAll(
                    LotlTrustedStore.mapIServiceContextToCountry(countrySpecificResult.getContexts()));
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

