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

import com.itextpdf.commons.datastructures.ConcurrentHashSet;
import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;
import com.itextpdf.signatures.logs.SignLogMessageConstant;
import com.itextpdf.signatures.validation.lotl.CountrySpecificLotlFetcher.Result;
import com.itextpdf.signatures.validation.report.ReportItem;
import com.itextpdf.signatures.validation.report.ReportItem.ReportItemStatus;
import com.itextpdf.signatures.validation.report.ValidationReport;
import com.itextpdf.signatures.validation.report.ValidationReport.ValidationResult;

import java.io.InputStream;
import java.io.OutputStream;
import java.security.cert.Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.slf4j.Logger;

/**
 * This class provides services for managing the Europian List of Trusted Lists (LOTL) and related resources.
 * It includes methods for fetching, validating, and caching LOTL data, as well as managing the European Resource
 * Fetcher and Country-Specific LOTL Fetcher.
 * It also allows for setting custom resource retrievers and cache timeouts.
 */
public class EuropeanLotlService extends LotlService {

    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(EuropeanLotlService.class);
    private EuropeanLotlFetcher lotlByteFetcher;
    private PivotFetcher pivotFetcher;
    private CountrySpecificLotlFetcher countrySpecificLotlFetcher;
    // Services
    private LotlServiceCache cache;
    private EuropeanResourceFetcher europeanResourceFetcher = new EuropeanResourceFetcher();
    private final ConcurrentHashSet<IServiceContext> nationalTrustedCertificates = new ConcurrentHashSet<>();

    /**
     * Creates a new instance of {@link EuropeanLotlService}.
     *
     * @param lotlFetchingProperties {@link LotlFetchingProperties} to configure the way in which LOTL will be fetched
     */
    public EuropeanLotlService(LotlFetchingProperties lotlFetchingProperties) {
        super(lotlFetchingProperties);
        this.cache = new InMemoryLotlServiceCache(lotlFetchingProperties.getCacheStalenessInMilliseconds(),
                lotlFetchingProperties.getOnCountryFetchFailureStrategy());
        this.lotlByteFetcher = new EuropeanLotlFetcher(this);
        this.pivotFetcher = new PivotFetcher(this);
        this.countrySpecificLotlFetcher = new CountrySpecificLotlFetcher(this);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public LotlService withLotlServiceCache(LotlServiceCache cache) {
        this.cache = cache;
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void loadFromCache(InputStream stream) {
        try {
            LotlCacheDataV1 container = LotlCacheDataV1.deserialize(stream);
            if (cache instanceof InMemoryLotlServiceCache) {
                // Check if the data we have in the cache is older than the new one
                Map<String, Long> newTimeStamps = container.getTimeStamps();
                InMemoryLotlServiceCache inMemoryCache = (InMemoryLotlServiceCache) this.cache;
                Map<String, Long> currentTimeStamps = inMemoryCache.getTimeStamps();

                for (Entry<String, Long> entry : newTimeStamps.entrySet()) {
                    Long newTimeStamp = entry.getValue();
                    Long timeStampFromCache = currentTimeStamps.get(entry.getKey());
                    if (timeStampFromCache != null && newTimeStamp <= timeStampFromCache) {
                        throw new PdfException(SignExceptionMessageConstant.CACHE_INCOMING_DATA_IS_STALER);
                    }
                }
                inMemoryCache.setTimeStamps(container.getTimeStamps());
            }

            EuropeanLotlFetcher.Result mainLotlResult = container.getLotlCache();
            EuropeanResourceFetcher.Result europeanResourceFetcherEUJournalCertificates =
                    container.getEuropeanResourceFetcherCache();
            PivotFetcher.Result pivotsResult = container.getPivotCache();
            Map<String, Result> resultToAddToCache = container.getCountrySpecificLotlCache();
            if (mainLotlResult == null || europeanResourceFetcherEUJournalCertificates == null
                    || pivotsResult == null || resultToAddToCache == null) {
                throw new PdfException(SignExceptionMessageConstant.COULD_NOT_INITIALIZE_FROM_FILE);
            }

            Set<String> countriesInCache = new HashSet<>();
            for (String key : new ArrayList<>(resultToAddToCache.keySet())) {
                Result value = resultToAddToCache.get(key);
                String countryCode = value.getCountrySpecificLotl().getSchemeTerritory();
                if (lotlFetchingProperties.getSchemaNames().contains(countryCode) ||
                        lotlFetchingProperties.getSchemaNames().isEmpty()) {
                    countriesInCache.add(countryCode);
                } else {
                    resultToAddToCache.remove(key);
                    LOGGER.warn(MessageFormatUtil.format(
                            SignLogMessageConstant.COUNTRY_NOT_REQUIRED_BY_CONFIGURATION,
                            countryCode));
                }
            }

            for (String schemaName : lotlFetchingProperties.getSchemaNames()) {
                if (countriesInCache.contains(schemaName)) {
                    continue;
                }
                throw new PdfException(MessageFormatUtil.format(
                        SignExceptionMessageConstant.INITIALIZED_CACHE_DOES_NOT_CONTAIN_REQUIRED_COUNTRY,
                        schemaName));
            }
            this.cache.setAllValues(mainLotlResult, europeanResourceFetcherEUJournalCertificates, pivotsResult,
                    resultToAddToCache);
        } catch (PdfException e) {
            throw e;
        } catch (Exception e) {
            throw new PdfException(SignExceptionMessageConstant.COULD_NOT_INITIALIZE_FROM_FILE, e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ValidationReport getValidationResult() {
        ValidationReport report = new ValidationReport();
        EuropeanLotlFetcher.Result lotl = getLotlBytes();
        if (!lotl.getLocalReport().getLogs().isEmpty()) {
            report.merge(lotl.getLocalReport());
            return report;
        }

        EuropeanResourceFetcher.Result europeanResult = getEUJournalCertificates();
        report.merge(europeanResult.getLocalReport());

        // get all the data from cache, if it is stale, exception will be thrown
        // locked and pass to methods
        PivotFetcher.Result result = getAndValidatePivotFiles(lotl.getLotlXml(),
                europeanResult.getCertificates(), europeanResult.getCurrentlySupportedPublication());

        report.merge(result.getLocalReport());
        if (result.getLocalReport().getValidationResult() != ValidationResult.VALID) {
            return report;
        }

        List<CountrySpecificLotlFetcher.Result> entries = getCountrySpecificLotlFiles(lotl.getLotlXml());

        this.nationalTrustedCertificates.clear();
        for (CountrySpecificLotlFetcher.Result countrySpecificResult : entries) {
            // When cache was loaded without config it still contains all country specific LOTL files.
            // So we need to filter them out if schema names were provided.
            if (!this.getLotlFetchingProperties()
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
     * {@inheritDoc}
     */
    @Override
    public List<IServiceContext> getNationalTrustedCertificates() {
        // Add all values to a new list
        return new ArrayList<>(this.nationalTrustedCertificates);
    }

    /**
     * Sets the pivot fetcher for the LOTL service.
     *
     * @param pivotFetcher the pivot fetcher to be used for fetching and validating pivot files
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    @Override
    public LotlService withPivotFetcher(PivotFetcher pivotFetcher) {
        this.pivotFetcher = pivotFetcher;
        return this;
    }

    /**
     * Sets the country-specific LOTL fetcher for the LOTL service.
     *
     * @param countrySpecificLotlFetcher the country-specific LOTL fetcher to be used for fetching and validating
     *                                   country-specific LOTLs
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    @Override
    public LotlService withCountrySpecificLotlFetcher(CountrySpecificLotlFetcher countrySpecificLotlFetcher) {
        this.countrySpecificLotlFetcher = countrySpecificLotlFetcher;
        return this;
    }

    /**
     * Sets the European List of Trusted Lists (LOTL) byte fetcher for the LOTL service.
     *
     * @param fetcher the fetcher to be used for fetching the LOTL XML data
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    @Override
    public LotlService withEuropeanLotlFetcher(EuropeanLotlFetcher fetcher) {
        this.lotlByteFetcher = fetcher;
        return this;
    }

    /**
     * Sets the European Resource Fetcher for the {@link LotlService}.
     *
     * @param europeanResourceFetcher the European Resource Fetcher to be used for fetching EU journal certificates
     *
     * @return the current instance of {@link LotlService} for method chaining
     */
    @Override
    public LotlService withEuropeanResourceFetcher(EuropeanResourceFetcher europeanResourceFetcher) {
        this.europeanResourceFetcher = europeanResourceFetcher;
        return this;
    }

    /**
     * Serializes the current state of the cache to the provided output stream.
     *
     * @param outputStream the output stream to which the cache will be serialized.
     */
    @Override
    public void serializeCache(OutputStream outputStream) {
        try {
            InMemoryLotlServiceCache inMemoryCache = (InMemoryLotlServiceCache) cache;
            inMemoryCache.getAllData().serialize(outputStream);
        } catch (Exception e) {
            throw new PdfException(SignExceptionMessageConstant.CACHE_CANNOT_BE_SERIALIZED, e);
        }
    }

    /**
     * Loads the cache from the network by fetching the latest LOTL data and related resources.
     * <p>
     * This method fetches the main LOTL file, EU journal certificates, pivot files, and country-specific LOTLs,
     * validates them, and stores them in the cache.
     * <p>
     * If the main LOTL fetch fails, the method will throw a {@link PdfException} and will not proceed to fetch
     * pivot files or country-specific LOTLs. If a country-specific LOTL fetch fails, the
     * {@link LotlFetchingProperties#getOnCountryFetchFailureStrategy()} will be used to handle the failure.
     * <p>
     * Note: This method is called during cache initialization and should not be called directly in normal
     * operation.
     */
    @Override
    protected void loadFromNetwork() {
        EuropeanLotlFetcher.Result mainLotlResult = lotlByteFetcher.fetch();
        if (!mainLotlResult.getLocalReport().getFailures().isEmpty()) {
            // We throw on main LOTL fetch failure, so we don't proceed to pivot and country specific LOTL fetches
            final ReportItem reportItem = mainLotlResult.getLocalReport().getFailures().get(0);
            throw new PdfException(reportItem.getMessage(), reportItem.getExceptionCause());
        }

        EuropeanResourceFetcher.Result europeanResourceFetcherEUJournalCertificates =
                europeanResourceFetcher.getEUJournalCertificates();
        pivotFetcher.setCurrentJournalUri(
                europeanResourceFetcherEUJournalCertificates.getCurrentlySupportedPublication());
        PivotFetcher.Result pivotsResult = pivotFetcher.downloadAndValidatePivotFiles(mainLotlResult.getLotlXml(),
                europeanResourceFetcherEUJournalCertificates.getCertificates());

        if (!pivotsResult.getLocalReport().getFailures().isEmpty()) {
            ReportItem failure = pivotsResult.getLocalReport().getFailures().get(0);
            throw new PdfException(failure.getMessage(), failure.getExceptionCause());
        }

        Map<String, CountrySpecificLotlFetcher.Result> countrySpecificResults =
                countrySpecificLotlFetcher.getAndValidateCountrySpecificLotlFiles(
                        mainLotlResult.getLotlXml(), this);

        Map<String, CountrySpecificLotlFetcher.Result> resultToAddToCache
                = new HashMap<>(countrySpecificResults.size());
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
    @Override
    protected void tryAndRefreshCache() {
        String currentJournalUri;

        EuropeanResourceFetcher.Result europeanResourceFetcherEUJournalCertificatesToUse;
        try {
            EuropeanResourceFetcher.Result europeanResourceFetcherEUJournalCertificates =
                    europeanResourceFetcher.getEUJournalCertificates();
            currentJournalUri = europeanResourceFetcherEUJournalCertificates.getCurrentlySupportedPublication();
            if (europeanResourceFetcherEUJournalCertificates.getLocalReport().getValidationResult()
                    != ValidationResult.VALID) {
                LOGGER.warn(MessageFormatUtil.format(
                        SignLogMessageConstant.FAILED_TO_FETCH_EU_JOURNAL_CERTIFICATES,
                        europeanResourceFetcherEUJournalCertificates.getLocalReport().getFailures().get(0)
                                .getMessage()));
                return;
            }
            europeanResourceFetcherEUJournalCertificatesToUse = europeanResourceFetcherEUJournalCertificates;
        } catch (Exception e) {
            LOGGER.warn(MessageFormatUtil.format(SignLogMessageConstant.FAILED_TO_FETCH_EU_JOURNAL_CERTIFICATES,
                    e.getMessage()));
            return;
        }

        boolean mainLotlFetchSuccessful = false;
        Exception mainLotlFetchException = null;
        EuropeanLotlFetcher.Result mainLotlResultToUse = null;
        try {
            mainLotlResultToUse = lotlByteFetcher.fetch();
            mainLotlFetchSuccessful =
                    mainLotlResultToUse.hasValidXml() && mainLotlResultToUse.getLocalReport().getFailures().isEmpty();
        } catch (Exception e) {
            mainLotlFetchException = e;
        }

        boolean fetchPivotFilesSuccessful = false;
        Exception pivotFetchException = null;
        PivotFetcher.Result pivotResultToUse = null;
        if (mainLotlFetchSuccessful) {
            // Only if the main LOTL was fetched successfully, we proceed to re-fetch the new pivot files.
            try {
                pivotFetcher.setCurrentJournalUri(currentJournalUri);
                pivotResultToUse = pivotFetcher.downloadAndValidatePivotFiles(mainLotlResultToUse.getLotlXml(),
                        europeanResourceFetcher.getEUJournalCertificates().getCertificates());
                fetchPivotFilesSuccessful =
                        pivotResultToUse.getLocalReport().getValidationResult() == ValidationResult.VALID;
            } catch (Exception e) {
                pivotFetchException = e;
            }
        } else {
            LOGGER.warn(MessageFormatUtil.format(SignLogMessageConstant.UPDATING_MAIN_LOTL_TO_CACHE_FAILED,
                    mainLotlFetchException == null ? "" : mainLotlFetchException.getMessage()));
        }

        // Only update main LOTL and pivot result if both are successful.
        if (!fetchPivotFilesSuccessful) {
            LOGGER.warn(MessageFormatUtil.format(SignLogMessageConstant.UPDATING_PIVOT_TO_CACHE_FAILED,
                    pivotFetchException == null ? "" : pivotFetchException.getMessage()));
        }
        if (!mainLotlFetchSuccessful) {
            // if main LOTL is null we do not proceed with country specific LOTL fetch because it depends on main LOTL
            return;
        }

        Map<String, CountrySpecificLotlFetcher.Result> allCountries;
        try {
            //Try updating the country specific LOTL files.
            allCountries = countrySpecificLotlFetcher.getAndValidateCountrySpecificLotlFiles(
                    mainLotlResultToUse.getLotlXml(), this);
        } catch (Exception e) {
            LOGGER.warn(MessageFormatUtil.format(SignLogMessageConstant.FAILED_TO_FETCH_COUNTRY_SPECIFIC_LOTL,
                    e.getMessage()));
            return;
        }
        // If an error happened don't update the cache value, if the warning is too stale we will throw an exception
        if (allCountries == null || allCountries.isEmpty()) {
            LOGGER.warn(SignLogMessageConstant.NO_COUNTRY_SPECIFIC_LOTL_FETCHED);
            return;
        }
        Map<String, CountrySpecificLotlFetcher.Result> countrySpecificLotlResultsToUse =
                new HashMap<>(allCountries.size());
        for (Result countrySpecificResult : allCountries.values()) {
            boolean wasCountryFetchedSuccessfully = countrySpecificResult.getLocalReport().getFailures().isEmpty();
            if (!wasCountryFetchedSuccessfully) {
                LOGGER.warn(MessageFormatUtil.format(SignLogMessageConstant.COUNTRY_SPECIFIC_FETCHING_FAILED,
                        countrySpecificResult.getCountrySpecificLotl().getSchemeTerritory(),
                        countrySpecificResult.getLocalReport()));
                continue;
            }
            countrySpecificLotlResultsToUse.put(countrySpecificResult.createUniqueIdentifier(), countrySpecificResult);
        }
        if (pivotResultToUse == null) {
            // nothing to update
            return;
        }
        cache.setAllValues(mainLotlResultToUse, europeanResourceFetcherEUJournalCertificatesToUse,
                pivotResultToUse, countrySpecificLotlResultsToUse);

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

    HashMap<String, Result> getCachedCountrySpecificLotls() {
        return new HashMap<>(cache.getCountrySpecificLotls());
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

    PivotFetcher.Result getAndValidatePivotFiles(byte[] lotlXml, List<Certificate> certificates,
            String currentJournalUri) {
        PivotFetcher.Result result = cache.getPivotResult();
        if (result != null) {
            return result;
        }
        pivotFetcher.setCurrentJournalUri(currentJournalUri);
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
}
