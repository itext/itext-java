/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2026 Apryse Group NV
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
import com.itextpdf.commons.utils.MultiThreadingUtil;
import com.itextpdf.io.resolver.resource.IResourceRetriever;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.validation.SafeCalling;
import com.itextpdf.signatures.validation.TrustedCertificatesStore;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ValidationReport;

import java.io.ByteArrayInputStream;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import static com.itextpdf.signatures.validation.lotl.LotlValidator.LOTL_VALIDATION;

/**
 * This class fetches and validates country-specific List of Trusted Lists (Lotls).
 */
public class CountrySpecificLotlFetcher {
    private final LotlService service;

    /**
     * Creates a new instance of {@link CountrySpecificLotlFetcher}.
     *
     * @param service the LotlService used to retrieve resources
     */
    public CountrySpecificLotlFetcher(LotlService service) {
        this.service = service;
    }


    /**
     * Fetches and validates country-specific Lotls from the provided Lotl XML.
     *
     * @param lotlXml the byte array of the Lotl XML
     * @param lotlService the {@link LotlService} used to build this fetcher
     *
     * @return a map of results containing validated country-specific Lotls and their contexts
     */
    public Map<String, Result> getAndValidateCountrySpecificLotlFiles(byte[] lotlXml, LotlService lotlService) {
        XmlCertificateRetriever certificateRetriever = new XmlCertificateRetriever(new XmlDefaultCertificateHandler());
        List<Certificate> lotlTrustedCertificates = certificateRetriever.getCertificates(
                new ByteArrayInputStream(lotlXml));
        XmlCountryRetriever countryRetriever = new XmlCountryRetriever();
        List<CountrySpecificLotl> countrySpecificLotl = countryRetriever
                .getAllCountriesLotlFilesLocation(new ByteArrayInputStream(lotlXml),
                        lotlService.getLotlFetchingProperties());

        final TrustedCertificatesStore certificatesStore = new TrustedCertificatesStore();
        certificatesStore.addGenerallyTrustedCertificates(lotlTrustedCertificates);
        final XmlSignatureValidator validator = lotlService.getXmlSignatureValidator(certificatesStore);

        final List<Callable<Result>> tasks = getTasks(lotlService, countrySpecificLotl, validator);

        HashMap<String, Result> countrySpecificCacheEntries = new HashMap<>();
        for (Result result : executeTasks(tasks)) {
            countrySpecificCacheEntries.put(result.createUniqueIdentifier(), result);
        }
        return countrySpecificCacheEntries;
    }


    /**
     * Creates an ExecutorService for downloading country-specific Lotls.
     * By default, it creates a fixed thread pool with a number of threads equal to the number of available
     * processors or the number of files to download, whichever is smaller.
     * If you require a different configuration with other executor services, you can override this method.
     *
     * @param tasks the list of tasks to be executed
     *
     * @return an ExecutorService instance configured for downloading Lotls
     */
    protected List<Result> executeTasks(List<Callable<Result>> tasks) {
        return MultiThreadingUtil.<Result>runActionsParallel(tasks, tasks.size());
    }

    private List<Callable<Result>> getTasks(LotlService lotlService, List<CountrySpecificLotl> countrySpecificLotl,
            XmlSignatureValidator validator) {
        final List<Callable<Result>> tasks = new ArrayList<>(countrySpecificLotl.size());
        for (CountrySpecificLotl f : countrySpecificLotl) {
            Callable<Result> resultCallable = () -> {
                ValidationReport report = new ValidationReport();
                Result result = SafeCalling.onExceptionLog(
                        () -> new CountryFetcher(service.getResourceRetriever(), validator, f,
                                lotlService.getLotlFetchingProperties()).getCountrySpecificLotl(),
                        new Result().setCountrySpecificLotl(f),
                        report,
                        e -> new ReportItem(LOTL_VALIDATION, MessageFormatUtil.format(
                                SignExceptionMessageConstant.FAILED_TO_FETCH_LOTL_FOR_COUNTRY, f.getSchemeTerritory(),
                                f.getTslLocation(), e.getMessage()), e,
                                ReportItem.ReportItemStatus.INVALID));
                result.getLocalReport().merge(report);
                return result;
            };
            tasks.add(resultCallable);
        }
        return tasks;
    }

    /**
     * Represents the result of fetching and validating country-specific Lotls.
     */
    public static class Result {
        private ValidationReport localReport;
        private List<IServiceContext> contexts;
        private CountrySpecificLotl countrySpecificLotl;

        /**
         * Constructs a new Result object.
         * Initializes the report items list, local report, and contexts list.
         */
        public Result() {
            this.localReport = new ValidationReport();
            this.contexts = new ArrayList<>();
        }


        /**
         * Gets the local validation report.
         *
         * @return the ValidationReport object containing the results of local validation
         */
        public ValidationReport getLocalReport() {
            return localReport;
        }

        /**
         * Sets the local validation report.
         *
         * @param localReport the ValidationReport object to set
         */
        public void setLocalReport(ValidationReport localReport) {
            this.localReport = localReport;
        }

        /**
         * Gets the list of service contexts associated with the country-specific Lotl.
         *
         * @return a list of IServiceContext objects representing the service contexts
         */
        public List<IServiceContext> getContexts() {
            return contexts;
        }

        /**
         * Sets the list of service contexts associated with the country-specific Lotl.
         *
         * @param contexts a list of IServiceContext objects to set
         */
        public void setContexts(List<IServiceContext> contexts) {
            this.contexts = contexts;
        }

        /**
         * Gets the country-specific Lotl that was fetched and validated.
         *
         * @return the CountrySpecificLotl object representing the country-specific Lotl
         */
        public CountrySpecificLotl getCountrySpecificLotl() {
            return countrySpecificLotl;
        }

        /**
         * Sets the country-specific Lotl that was fetched and validated.
         *
         * @param countrySpecificLotl the CountrySpecificLotl object to set
         *
         * @return same result instance.
         */
        public Result setCountrySpecificLotl(CountrySpecificLotl countrySpecificLotl) {
            this.countrySpecificLotl = countrySpecificLotl;
            return this;
        }

        /**
         * Creates a unique identifier for the country-specific Lotl based on its scheme territory and TSL location.
         *
         * @return a string representing the unique identifier for the country-specific Lotl
         */
        public String createUniqueIdentifier() {
            return countrySpecificLotl.getSchemeTerritory() + "_" + countrySpecificLotl.getTslLocation();
        }
    }

    private static final class CountryFetcher {
        static final String COUNTRY_SPECIFIC_LOTL_NOT_VALIDATED = "Country specific Lotl file: {0}, {1} wasn't " +
                "successfully validated. It will be ignored.";
        static final String COULD_NOT_RESOLVE_URL = "Couldn't resolve {0} url. This TSL Location will be ignored.";

        private final IResourceRetriever resourceRetriever;
        private final XmlSignatureValidator xmlSignatureValidator;
        private final CountrySpecificLotl countrySpecificLotl;
        private final LotlFetchingProperties properties;

        CountryFetcher(IResourceRetriever resourceRetriever, XmlSignatureValidator xmlSignatureValidator,
                CountrySpecificLotl countrySpecificLotl, LotlFetchingProperties properties) {
            this.resourceRetriever = resourceRetriever;
            this.xmlSignatureValidator = xmlSignatureValidator;
            this.countrySpecificLotl = countrySpecificLotl;
            this.properties = properties;
        }

        public Result getCountrySpecificLotl() {
            final Result countryResult = new Result();
            countryResult.setCountrySpecificLotl(countrySpecificLotl);
            byte[] countryLotlBytes;
            countryLotlBytes = SafeCalling.onExceptionLog(
                    () -> resourceRetriever.getByteArrayByUrl(new URL(countrySpecificLotl.getTslLocation())),
                    null,
                    countryResult.getLocalReport(),
                    e -> new ReportItem(LOTL_VALIDATION, MessageFormatUtil.format(
                            COULD_NOT_RESOLVE_URL, countrySpecificLotl.getTslLocation()), e,
                            ReportItem.ReportItemStatus.INVALID));
            if (countryLotlBytes == null) {
                return countryResult;
            }
            ValidationReport localReport = xmlSignatureValidator.validate(new ByteArrayInputStream(countryLotlBytes));
            countryResult.setLocalReport(localReport);
            if (localReport.getValidationResult() == ValidationReport.ValidationResult.VALID) {
                XmlCertificateRetriever countryCertificateRetriever = new XmlCertificateRetriever(
                        new XmlCountryCertificateHandler(properties.getServiceTypes()));
                countryCertificateRetriever.getCertificates(new ByteArrayInputStream(countryLotlBytes));
                countryResult.getContexts().addAll(countryCertificateRetriever.getServiceContexts());
            } else {
                countryResult.getLocalReport().addReportItem(new ReportItem(LOTL_VALIDATION,
                        MessageFormatUtil.format(COUNTRY_SPECIFIC_LOTL_NOT_VALIDATED,
                                countrySpecificLotl.getSchemeTerritory(), countrySpecificLotl.getTslLocation()),
                        ReportItem.ReportItemStatus.INVALID));
            }
            return countryResult;
        }
    }
}
