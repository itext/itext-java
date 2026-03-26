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

import java.util.Map;

/**
 * Interface for caching LOTL (List of Trusted Lists) service results.
 * It provides methods to set and get various LOTL-related data, including
 * European LOTL, country-specific LOTLs, and pivot results.
 * <p>
 * Notice: If you do your own implementation of this interface,
 * you should ensure that the cache is thread-safe and can handle concurrent access.
 * This is important because LOTL data can be accessed and modified by multiple threads
 * simultaneously.
 * You should also ensure that all the values are set atomically using {@link #setAllValues} method
 * to maintain consistency, So that you are not using outdated pivot results or country-specific LOTLs with a changed
 * European LOTL.
 */
public interface LotlServiceCache {

    /**
     * Sets all values related to LOTL, including European LOTL, EU Journal certificates,
     * pivot results, and country-specific LOTLs. This extra method is used for syncronized
     * updates to the cache, ensuring that all related data is set at once. This is useful
     * in multithreaded environments where you want to ensure that all related data is consistent.
     *
     * @param lotlXml                                      the European LOTL result
     * @param europeanResourceFetcherEUJournalCertificates the EU Journal certificates
     * @param result                                       the pivot fetcher result
     * @param countrySpecificResult                        a map of country-specific LOTL results
     */
    void setAllValues(EuropeanLotlFetcher.Result lotlXml,
            EuropeanResourceFetcher.Result europeanResourceFetcherEUJournalCertificates,
            PivotFetcher.Result result, Map<String, CountrySpecificLotlFetcher.Result> countrySpecificResult);

    /**
     * Gets the European LOTL result.
     *
     * @return the European LOTL result
     */
    PivotFetcher.Result getPivotResult();

    /**
     * Sets the pivot result.
     *
     * @param newResult the new pivot result to set
     */
    void setPivotResult(PivotFetcher.Result newResult);

    /**
     * Gets the country-specific LOTL results.
     *
     * @return a map of country-specific LOTL results
     */
    Map<String, CountrySpecificLotlFetcher.Result> getCountrySpecificLotls();

    /**
     * Sets the country-specific LOTL result for a specific country.
     *
     * @param countrySpecificLotlResult the country-specific LOTL result to set
     */
    void setCountrySpecificLotlResult(CountrySpecificLotlFetcher.Result countrySpecificLotlResult);

    /**
     * Gets the European LOTL result.
     *
     * @return the European LOTL result
     */
    EuropeanLotlFetcher.Result getLotlResult();

    /**
     * Sets the European LOTL result.
     *
     * @param data the European LOTL result to set
     */
    void setLotlResult(EuropeanLotlFetcher.Result data);

    /**
     * Sets the result of the European Resource Fetcher.
     * This method is used to update the cache with the result
     *
     * @param result the result of the European Resource Fetcher
     */
    void setEuropeanResourceFetcherResult(EuropeanResourceFetcher.Result result);

    /**
     * Gets the result of the European Resource Fetcher.
     *
     * @return the result of the European Resource Fetcher
     */
    EuropeanResourceFetcher.Result getEUJournalCertificates();
}

