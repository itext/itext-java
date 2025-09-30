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

import com.itextpdf.commons.utils.SystemUtil;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

final class InMemoryLotlServiceCache implements LotlServiceCache {

    private static final String CACHE_KEY_LOTL = "lotlCache";
    private static final String CACHE_KEY_EU_JOURNAL_CERTIFICATES = "europeanResourceFetcherCache";
    private final Object lock = new Object();
    private final long maxAllowedStalenessInMillis;
    private final HashMap<String, Long> timeStamps = new HashMap<>();
    private final IOnFailingCountryLotlData strategy;
    private EuropeanLotlFetcher.Result lotlCache = null;
    private PivotFetcher.Result pivotCache = null;
    private EuropeanResourceFetcher.Result europeanResourceFetcherCache = null;
    private Map<String, CountrySpecificLotlFetcher.Result> countrySpecificLotlCache = null;

    InMemoryLotlServiceCache(long maxAllowedStalenessInMillis, IOnFailingCountryLotlData strategy) {
        this.maxAllowedStalenessInMillis = maxAllowedStalenessInMillis;
        this.strategy = strategy;
    }


    @Override
    public void setAllValues(EuropeanLotlFetcher.Result lotlXml,
            EuropeanResourceFetcher.Result europeanResourceFetcherEUJournalCertificates,
            PivotFetcher.Result result,
            Map<String, CountrySpecificLotlFetcher.Result> countrySpecificResult) {
        synchronized (lock) {
            lotlCache = lotlXml;
            addToStaleTracker(CACHE_KEY_LOTL);
            europeanResourceFetcherCache = europeanResourceFetcherEUJournalCertificates;
            addToStaleTracker(CACHE_KEY_EU_JOURNAL_CERTIFICATES);
            pivotCache = result;
            addToStaleTracker(pivotCache.generateUniqueIdentifier());
            for (Map.Entry<String, CountrySpecificLotlFetcher.Result> entry : countrySpecificResult.entrySet()) {
                setSpecificCountryInternally(entry.getValue());
            }
        }
    }

    @Override
    public PivotFetcher.Result getPivotResult() {
        synchronized (lock) {
            if (isObjectStale(pivotCache.generateUniqueIdentifier())) {
                throw new InvalidLotlDataException(SignExceptionMessageConstant.STALE_DATA_IS_USED);
            }
            return pivotCache;
        }
    }

    @Override
    public void setPivotResult(PivotFetcher.Result newResult) {
        synchronized (lock) {
            pivotCache = newResult;
            addToStaleTracker(newResult.generateUniqueIdentifier());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, CountrySpecificLotlFetcher.Result> getCountrySpecificLotls() {
        synchronized (lock) {
            if (countrySpecificLotlCache == null) {
                countrySpecificLotlCache = new HashMap<>();
            }
            HashMap<String, CountrySpecificLotlFetcher.Result> newValues = new HashMap<>(this.countrySpecificLotlCache);
            // We need to do the stale check, so we loop and call getCountrySpecificLotl
            for (String s : this.countrySpecificLotlCache.keySet()) {
                CountrySpecificLotlFetcher.Result result = getCountrySpecificLotl(s);
                newValues.put(s, result);
            }
            countrySpecificLotlCache = newValues;
            return Collections.unmodifiableMap(countrySpecificLotlCache);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setCountrySpecificLotlResult(CountrySpecificLotlFetcher.Result countrySpecificLotlResult) {
        synchronized (lock) {
            setSpecificCountryInternally(countrySpecificLotlResult);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EuropeanLotlFetcher.Result getLotlResult() {
        synchronized (lock) {
            if (isObjectStale(CACHE_KEY_LOTL)) {
                throw new InvalidLotlDataException(SignExceptionMessageConstant.STALE_DATA_IS_USED);
            }
            return lotlCache;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setLotlResult(EuropeanLotlFetcher.Result data) {
        synchronized (lock) {
            lotlCache = data;
            addToStaleTracker(CACHE_KEY_LOTL);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEuropeanResourceFetcherResult(EuropeanResourceFetcher.Result result) {
        synchronized (lock) {
            europeanResourceFetcherCache = result;
            addToStaleTracker(CACHE_KEY_EU_JOURNAL_CERTIFICATES);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EuropeanResourceFetcher.Result getEUJournalCertificates() {
        synchronized (lock) {
            if (isObjectStale(CACHE_KEY_EU_JOURNAL_CERTIFICATES)) {
                throw new InvalidLotlDataException(SignExceptionMessageConstant.STALE_DATA_IS_USED);
            }
            return europeanResourceFetcherCache;
        }
    }

    HashMap<String, Long> getTimeStamps() {
        synchronized (lock) {
            return new HashMap<>(timeStamps);
        }
    }

    void setTimeStamps(Map<String, Long> timeStamps) {
        synchronized (lock) {
            this.timeStamps.clear();
            this.timeStamps.putAll(timeStamps);
        }
    }

    LotlCacheDataV1 getAllData() {
        synchronized (lock) {
            return new LotlCacheDataV1(lotlCache, pivotCache, europeanResourceFetcherCache, countrySpecificLotlCache,
                    (HashMap<String, Long>) timeStamps);
        }
    }

    private boolean isObjectStale(String key) {
        final Long lastUpdated = timeStamps.get(key);
        if (lastUpdated == null) {
            return true;
        }
        final long lastUpdatedInMillis = (long) lastUpdated;
        final long currentTime = SystemUtil.currentTimeMillis();
        final long timeElapsed = currentTime - lastUpdatedInMillis;
        return timeElapsed > maxAllowedStalenessInMillis;
    }

    private void addToStaleTracker(String key) {
        timeStamps.put(key, SystemUtil.currentTimeMillis());
    }

    private CountrySpecificLotlFetcher.Result getCountrySpecificLotl(String country) {
        CountrySpecificLotlFetcher.Result result = this.countrySpecificLotlCache.get(country);
        if (isObjectStale(result.createUniqueIdentifier())) {
            strategy.onCountryFailure(result);
        }
        return result;
    }

    private void setSpecificCountryInternally(CountrySpecificLotlFetcher.Result countrySpecificLotlCache) {
        if (this.countrySpecificLotlCache == null) {
            this.countrySpecificLotlCache = new HashMap<>();
        }
        final String cacheId = countrySpecificLotlCache.createUniqueIdentifier();
        this.countrySpecificLotlCache.put(cacheId, countrySpecificLotlCache);
        addToStaleTracker(cacheId);
    }
}

