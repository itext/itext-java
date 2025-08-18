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
import com.itextpdf.commons.utils.TimerUtil;
import com.itextpdf.io.resolver.resource.DefaultResourceRetriever;
import com.itextpdf.io.resolver.resource.IResourceRetriever;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.validation.TrustedCertificatesStore;
import com.itextpdf.signatures.validation.lotl.CountrySpecificLotlFetcher.Result;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;

import java.util.function.Function;
import java.util.function.Supplier;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URL;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Timer;
import java.util.function.LongUnaryOperator;


/**
 * This class provides services for managing the List of Trusted Lists (Lotl) and related resources.
 * It includes methods for fetching, validating, and caching Lotl data, as well as managing the European Resource
 * Fetcher and Country-Specific Lotl Fetcher.
 * It also allows for setting custom resource retrievers and cache timeouts.
 */
public class LotlService implements AutoCloseable {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LotlService.class);

    //Services
    private LotlServiceCache cache;
    private EuropeanResourceFetcher europeanResourceFetcher = new EuropeanResourceFetcher();
    private EuropeanLotlFetcher lotlByteFetcher;
    private PivotFetcher pivotFetcher;
    private CountrySpecificLotlFetcher countrySpecificLotlFetcher;
    private boolean cacheInitialized = false;
    private Timer cacheTimer = null;
    private IResourceRetriever resourceRetriever = new LoggableResourceRetriever();
    private Function<TrustedCertificatesStore, XmlSignatureValidator> xmlSignatureValidatorFactory;
    private Supplier<LotlValidator> lotlValidatorFactory;
    private final LotlFetchingProperties lotlFetchingProperties;
    // Global service
    static LotlService GLOBAL_SERVICE;
    private static final Object GLOBAL_SERVICE_LOCK = new Object();


    /**
     * Creates a new instance of {@link LotlService}.
     *
     * @param lotlFetchingProperties {@link LotlFetchingProperties} to configure the way in which LOTL will be fetched
     */
    public LotlService(LotlFetchingProperties lotlFetchingProperties) {
        this.lotlFetchingProperties = lotlFetchingProperties;
        this.cache = new InMemoryLotlServiceCache(lotlFetchingProperties.getCacheStalenessInMilliseconds(),
                lotlFetchingProperties.getOnCountryFetchFailureStrategy());
        this.lotlByteFetcher = new EuropeanLotlFetcher(this);
        this.pivotFetcher = new PivotFetcher(this);
        this.countrySpecificLotlFetcher = new CountrySpecificLotlFetcher(this);
        this.xmlSignatureValidatorFactory =
                trustedCertificatesStore -> buildXmlSignatureValidator(trustedCertificatesStore);
        this.lotlValidatorFactory = () -> buildLotlValidator();
    }

    /**
     * Initializes the global cache with the provided LotlFetchingProperties.
     * This method must be called before using the LotlService to ensure that the cache is set up.
     * <p>
     * If you are using a custom implementation of {@link LotlService} you can use the instance method.
     *
     * @param lotlFetchingProperties the LotlFetchingProperties to use for initializing the cache
     */
    public static void initializeGlobalCache(LotlFetchingProperties lotlFetchingProperties) {
        synchronized (GLOBAL_SERVICE_LOCK) {
            if (GLOBAL_SERVICE == null) {
                GLOBAL_SERVICE = new LotlService(lotlFetchingProperties);
                GLOBAL_SERVICE.initializeCache();
            } else {
                throw new PdfException(SignExceptionMessageConstant.CACHE_ALREADY_INITIALIZED);
            }
        }
    }

    /**
     * Gets global static instance of {@link LotlService}.
     *
     * @return global static instance of {@link LotlService}
     */
    public static LotlService getGlobalService() {
        return GLOBAL_SERVICE;
    }

    /**
     * Sets the cache for the LotlService.
     * <p>
     * This method allows you to provide a custom implementation of {@link LotlServiceCache} to be used
     * for caching Lotl data, pivot files, and country-specific Lotls.
     *
     * @param cache the custom cache to be used for caching Lotl data
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    public LotlService withLotlServiceCache(LotlServiceCache cache) {
        this.cache = cache;
        return this;
    }

    /**
     * Sets a custom resource retriever for fetching resources.
     * <p>
     * This method allows you to provide a custom implementation of {@link IResourceRetriever} to be used
     * for fetching resources such as the Lotl XML, pivot files, and country-specific Lotls.
     *
     * @param resourceRetriever the custom resource retriever to be used for fetching resources
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    public final LotlService withCustomResourceRetriever(IResourceRetriever resourceRetriever) {
        this.resourceRetriever = resourceRetriever;
        return this;
    }

    /**
     * Initializes the cache with the latest Lotl data and related resources.
     */
    public void initializeCache() {
        setupTimer();
        EuropeanLotlFetcher.Result mainLotlResult = lotlByteFetcher.fetch();
        if (!mainLotlResult.getLocalReport().getFailures().isEmpty()) {
            //We throw on main Lotl fetch failure, so we don't proceed to pivot and country specific LOTL fetches
            final ReportItem reportItem = mainLotlResult.getLocalReport().getFailures().get(0);
            throw new PdfException(reportItem.getMessage(), reportItem.getExceptionCause());
        }

        EuropeanResourceFetcher.Result europeanResourceFetcherEUJournalCertificates =
                europeanResourceFetcher.getEUJournalCertificates();
        PivotFetcher.Result pivotsResult = pivotFetcher.downloadAndValidatePivotFiles(mainLotlResult.getLotlXml(),
                europeanResourceFetcherEUJournalCertificates.getCertificates());

        if (!pivotsResult.getLocalReport().getFailures().isEmpty()){
            ReportItem failure = pivotsResult.getLocalReport().getFailures().get(0);
            throw new PdfException(failure.getMessage(), failure.getExceptionCause());
        }

        Map<String, CountrySpecificLotlFetcher.Result> countrySpecificResults =
                countrySpecificLotlFetcher.getAndValidateCountrySpecificLotlFiles(
                        mainLotlResult.getLotlXml(), this);

        Map<String, CountrySpecificLotlFetcher.Result> resultToAddToCache = new HashMap<>(
                countrySpecificResults.size());
        for (Entry<String, Result> entry : countrySpecificResults.entrySet()) {
            final Result countrySpecificResult = entry.getValue();
            if (countrySpecificResult.getLocalReport().getValidationResult() != ValidationResult.VALID) {
                for (ReportItem log : countrySpecificResult.getLocalReport().getLogs()) {
                    log.setStatus(ReportItemStatus.INFO);
                }
                lotlFetchingProperties.getOnCountryFetchFailureStrategy().onCountryFailure(countrySpecificResult);
            }
            resultToAddToCache.put(entry.getKey(), countrySpecificResult);
        }
        this.cache.setAllValues(mainLotlResult, europeanResourceFetcherEUJournalCertificates, pivotsResult,
                resultToAddToCache);
        cacheInitialized = true;
    }

    /**
     * Sets the pivot fetcher for the Lotl service.
     *
     * @param pivotFetcher the pivot fetcher to be used for fetching and validating pivot files
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    public LotlService withPivotFetcher(PivotFetcher pivotFetcher) {
        this.pivotFetcher = pivotFetcher;
        return this;
    }

    /**
     * Sets the country-specific Lotl fetcher for the Lotl service.
     *
     * @param countrySpecificLotlFetcher the country-specific Lotl fetcher to be used for fetching and validating
     *                                   country-specific Lotls
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    public LotlService withCountrySpecificLotlFetcher(CountrySpecificLotlFetcher countrySpecificLotlFetcher) {
        this.countrySpecificLotlFetcher = countrySpecificLotlFetcher;
        return this;
    }

    /**
     * Sets the European List of Trusted Lists (Lotl) byte fetcher for the Lotl service.
     *
     * @param fetcher the fetcher to be used for fetching the Lotl XML data
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    public LotlService withEuropeanLotlFetcher(EuropeanLotlFetcher fetcher) {
        this.lotlByteFetcher = fetcher;
        return this;
    }

    /**
     * Sets up factory which is responsible for {@link XmlSignatureValidator} creation.
     *
     * @param xmlSignatureValidatorFactory factory responsible for {@link XmlSignatureValidator} creation
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    public LotlService withXmlSignatureValidator(
            Function<TrustedCertificatesStore, XmlSignatureValidator> xmlSignatureValidatorFactory) {
        this.xmlSignatureValidatorFactory = xmlSignatureValidatorFactory;
        return this;
    }

    /**
     * Sets up factory which is responsible for {@link LotlValidator} creation.
     *
     * @param lotlValidatorFactory factory responsible for {@link LotlValidator} creation
     *
     * @return this same instance of {@link LotlService}
     */
    public LotlService withLotlValidator(Supplier<LotlValidator> lotlValidatorFactory) {
        this.lotlValidatorFactory = lotlValidatorFactory;
        return this;
    }

    /**
     * Sets the European Resource Fetcher for the LotlService.
     *
     * @param europeanResourceFetcher the European Resource Fetcher to be used for fetching EU journal certificates
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    public LotlService withEuropeanResourceFetcher(EuropeanResourceFetcher europeanResourceFetcher) {
        this.europeanResourceFetcher = europeanResourceFetcher;
        return this;
    }

    /**
     * {@inheritDoc}.
     */
    @Override
    public void close() {
        cancelTimer();
    }

    /**
     * Sets up a timer to periodically refresh the LOTL cache.
     * <p>
     * The timer will use the refresh interval calculated based on the stale-ness of the cache.
     * If the cache is null, it will create a new instance of {@link InMemoryLotlServiceCache}.
     */
    protected void setupTimer() {
        long staleNessInMillis = lotlFetchingProperties.getCacheStalenessInMilliseconds();
        TimerUtil.stopTimer(cacheTimer);
        LongUnaryOperator cacheRefreshTimer = lotlFetchingProperties.getRefreshIntervalCalculator();
        long refreshInterval = cacheRefreshTimer.applyAsLong(staleNessInMillis);
        cacheTimer = TimerUtil.newTimerWithRecurringTask(() -> tryAndRefreshCache(),
                refreshInterval, refreshInterval);
    }

    /**
     * Cancels timer, if it was already set up.
     */
    protected void cancelTimer() {
        if (cacheTimer != null) {
            TimerUtil.stopTimer(cacheTimer);
        }
    }

    /**
     * This method is intended to refresh the cache, it will try to download the latest LOTL data and update the
     * cache accordingly.
     * <p>
     * The rules taken into account are:
     * Country specific LOTL files will be fetched, validated and updated per country. If country fails to fetch,
     * {@link LotlFetchingProperties#getOnCountryFetchFailureStrategy()} will be used to perform corresponding action.
     * <p>
     * For the main LOTL file, if the fetch fails, the cache will not be updated. Also, we will NOT proceed to update
     * the pivot files.
     * If the main LOTL file is fetched successfully, the pivot files will be fetched, validated and stored in the
     * cache.
     */
    protected void tryAndRefreshCache() {
        EuropeanLotlFetcher.Result mainLotlResult = null;
        boolean mainLotlFetchSuccessful = false;
        Exception mainLotlFetchException = null;

        try {
            EuropeanResourceFetcher.Result europeanResourceFetcherEUJournalCertificates =
                    europeanResourceFetcher.getEUJournalCertificates();
            if (europeanResourceFetcherEUJournalCertificates.getLocalReport().getValidationResult()
                    != ValidationResult.VALID) {
                throw new PdfException(
                        MessageFormatUtil.format(SignExceptionMessageConstant.FAILED_TO_FETCH_EU_JOURNAL_CERTIFICATES,
                                europeanResourceFetcherEUJournalCertificates.getLocalReport().getFailures()
                                        .get(0).getMessage()));
            }
            cache.setEuropeanResourceFetcherResult(europeanResourceFetcherEUJournalCertificates);
        } catch (Exception e) {
            LOGGER.warn(MessageFormatUtil.format(SignLogMessageConstant.FAILED_TO_FETCH_EU_JOURNAL_CERTIFICATES,
                    e.getMessage()));
            return;
        }
        try {
            mainLotlResult = lotlByteFetcher.fetch();
            mainLotlFetchSuccessful = mainLotlResult.hasValidXml() && mainLotlResult
                    .getLocalReport()
                    .getFailures().isEmpty();
        } catch (Exception e) {
            mainLotlFetchException = e;
        }

        boolean fetchPivotFilesSuccessful = false;
        PivotFetcher.Result pivotResult = null;
        Exception pivotFetchException = null;

        if (mainLotlFetchSuccessful) {
            //Only if the main Lotl was fetched successfully, we proceed to re-fetch the new pivot files.
            try {
                pivotResult = pivotFetcher.downloadAndValidatePivotFiles(
                        mainLotlResult.getLotlXml(),
                        europeanResourceFetcher.getEUJournalCertificates().getCertificates());
                fetchPivotFilesSuccessful =
                        pivotResult.getLocalReport().getValidationResult() == ValidationResult.VALID;
            } catch (Exception e) {
                pivotFetchException = e;
            }
        } else {
            LOGGER.warn(MessageFormatUtil.format(SignLogMessageConstant.UPDATING_MAIN_LOTL_TO_CACHE_FAILED,
                    mainLotlFetchException == null ? "" : mainLotlFetchException.getMessage()));
        }

        //Only update main Lotl and pivot result if both are successful.
        if (fetchPivotFilesSuccessful) {
            cache.setLotlResult(mainLotlResult);
            cache.setPivotResult(pivotResult);
        } else {
            LOGGER.warn(MessageFormatUtil.format(
                    SignLogMessageConstant.UPDATING_PIVOT_TO_CACHE_FAILED, pivotFetchException == null ? ""
                            : pivotFetchException.getMessage()));
        }

        mainLotlResult = cache.getLotlResult();
        Map<String, CountrySpecificLotlFetcher.Result> allCountries;
        try {
            //Try updating the country specific Lotl files.
            allCountries = countrySpecificLotlFetcher
                    .getAndValidateCountrySpecificLotlFiles(mainLotlResult.getLotlXml(), this);

        } catch (Exception e) {
            LOGGER.warn(MessageFormatUtil.format(SignLogMessageConstant.FAILED_TO_FETCH_COUNTRY_SPECIFIC_LOTL,
                    e.getMessage()));
            return;
        }
        //If an error happened don't update the cache value, if the warn is too stale we will throw an exception
        if (allCountries == null || allCountries.isEmpty()) {
            LOGGER.warn(SignLogMessageConstant.NO_COUNTRY_SPECIFIC_LOTL_FETCHED);
            return;
        }
        for (Result countrySpecificResult : allCountries.values()) {
            boolean wasCountryFetchedSuccessfully =
                    countrySpecificResult.getLocalReport().getFailures().isEmpty();
            if (!wasCountryFetchedSuccessfully) {
                LOGGER.warn(MessageFormatUtil.format(SignLogMessageConstant.COUNTRY_SPECIFIC_FETCHING_FAILED,
                        countrySpecificResult.getCountrySpecificLotl().getSchemeTerritory(),
                        countrySpecificResult.getLocalReport()));
                continue;
            }
            cache.setCountrySpecificLotlResult(countrySpecificResult);
        }
    }

    PivotFetcher.Result getAndValidatePivotFiles(byte[] lotlXml, List<Certificate> certificates) {
        PivotFetcher.Result result = cache.getPivotResult();
        if (result != null) {
            return result;
        }
        PivotFetcher.Result newResult = pivotFetcher.downloadAndValidatePivotFiles(lotlXml, certificates);
        cache.setPivotResult(newResult);
        return newResult;
    }

    List<CountrySpecificLotlFetcher.Result> getCountrySpecificLotlFiles(byte[] lotlXml) {
        final Map<String, CountrySpecificLotlFetcher.Result> result = cache.getCountrySpecificLotls();
        if (result != null) {
            return new ArrayList<>(result.values());
        }
        final Map<String, CountrySpecificLotlFetcher.Result> countrySpecificLotlResults =
                countrySpecificLotlFetcher.getAndValidateCountrySpecificLotlFiles(
                        lotlXml, this);
        for (Map.Entry<String, CountrySpecificLotlFetcher.Result> s : countrySpecificLotlResults.entrySet()) {
            boolean successful = s.getValue().getLocalReport().getValidationResult() == ValidationResult.VALID;
            if (successful || s.getValue().getLocalReport().getLogs().isEmpty()) {
                cache.setCountrySpecificLotlResult(s.getValue());
            }
        }

        return new ArrayList<>(countrySpecificLotlResults.values());

    }

    boolean isCacheInitialized() {
        return cacheInitialized;
    }

    EuropeanLotlFetcher.Result getLotlBytes() {
        EuropeanLotlFetcher.Result cachedData = cache.getLotlResult();
        if (cachedData != null) {
            return cachedData;
        }
        EuropeanLotlFetcher.Result data = lotlByteFetcher.fetch();
        cache.setLotlResult(data);
        return data;
    }

    EuropeanResourceFetcher.Result getEUJournalCertificates() {
        EuropeanResourceFetcher.Result cachedResult = cache.getEUJournalCertificates();
        if (cachedResult != null) {
            return cachedResult;
        }

        EuropeanResourceFetcher.Result result = europeanResourceFetcher.getEUJournalCertificates();
        cache.setEuropeanResourceFetcherResult(result);
        return result;
    }

    /**
     * Gets the resource retriever used by the Lotl service.
     *
     * @return the {@link IResourceRetriever} instance used for fetching resources
     */
    IResourceRetriever getResourceRetriever() {
        return resourceRetriever;
    }

    /**
     * Retrieves explicitly added or automatically created {@link XmlSignatureValidator} instance.
     *
     * @return explicitly added or automatically created {@link XmlSignatureValidator} instance.
     */
    XmlSignatureValidator getXmlSignatureValidator(TrustedCertificatesStore trustedCertificatesStore) {
        return xmlSignatureValidatorFactory.apply(trustedCertificatesStore);
    }

    LotlFetchingProperties getLotlFetchingProperties() {
        return lotlFetchingProperties;
    }

    /**
     * Retrieves explicitly added or automatically created {@link LotlValidator} instance.
     *
     * @return explicitly added or automatically created {@link LotlValidator} instance
     */
    LotlValidator getLotlValidator() {
        return lotlValidatorFactory.get();
    }

    private static XmlSignatureValidator buildXmlSignatureValidator(TrustedCertificatesStore trustedCertificatesStore) {
        return new XmlSignatureValidator(trustedCertificatesStore);
    }

    private LotlValidator buildLotlValidator() {
        return new LotlValidator(this);
    }

    private static final class LoggableResourceRetriever extends DefaultResourceRetriever {
        private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(LotlService.class);

        public LoggableResourceRetriever() {
            // Default constructor
        }

        @Override
        public byte[] getByteArrayByUrl(URL url) throws IOException {
            LOGGER.info(MessageFormatUtil.format("Fetching resource from URL: {0}", url));
            return super.getByteArrayByUrl(url);
        }
    }
}

