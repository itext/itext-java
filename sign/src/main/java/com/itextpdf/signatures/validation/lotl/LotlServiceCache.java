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

import java.util.Map;

/**
 * Interface for caching Lotl (List of Trusted Lists) service results.
 * It provides methods to set and get various Lotl-related data, including
 * European Lotl, country-specific Lotls, and pivot results.
 * <p>
 * Notice: If you do your own implementation of this interface,
 * you should ensure that the cache is thread-safe and can handle concurrent access.
 * This is important because Lotl data can be accessed and modified by multiple threads
 * simultaneously.
 * You should also ensure that all the values are set atomically using {@link #setAllValues} method
 * to maintain consistency, So that you are not using outdated pivot results or country-specific Lotls with a changed
 * European Lotl.
 */
public interface LotlServiceCache {

    /**
     * Sets all values related to Lotl, including European Lotl, EU Journal certificates,
     * pivot results, and country-specific Lotls. This extra method is used for syncronized
     * updates to the cache, ensuring that all related data is set at once. This is useful
     * in multithreaded environments where you want to ensure that all related data is consistent.
     *
     * @param lotlXml                                      the European Lotl result
     * @param europeanResourceFetcherEUJournalCertificates the EU Journal certificates
     * @param result                                       the pivot fetcher result
     * @param countrySpecificResult                        a map of country-specific Lotl results
     */
    void setAllValues(EuropeanLotlFetcher.Result lotlXml,
            EuropeanResourceFetcher.Result europeanResourceFetcherEUJournalCertificates,
            PivotFetcher.Result result, Map<String, CountrySpecificLotlFetcher.Result> countrySpecificResult);

    /**
     * Gets the European Lotl result.
     *
     * @return the European Lotl result
     */
    PivotFetcher.Result getPivotResult();

    /**
     * Sets the pivot result.
     *
     * @param newResult the new pivot result to set
     */
    void setPivotResult(PivotFetcher.Result newResult);

    /**
     * Gets the country-specific Lotl results.
     *
     * @return a map of country-specific Lotl results
     */
    Map<String, CountrySpecificLotlFetcher.Result> getCountrySpecificLotls();

    /**
     * Sets the country-specific Lotl result for a specific country.
     *
     * @param countrySpecificLotlResult the country-specific Lotl result to set
     */
    void setCountrySpecificLotlResult(CountrySpecificLotlFetcher.Result countrySpecificLotlResult);

    /**
     * Gets the European Lotl result.
     *
     * @return the European Lotl result
     */
    EuropeanLotlFetcher.Result getLotlResult();

    /**
     * Sets the European Lotl result.
     *
     * @param data the European Lotl result to set
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

