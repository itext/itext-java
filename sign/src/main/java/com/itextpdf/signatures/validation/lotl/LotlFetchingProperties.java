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

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.function.LongUnaryOperator;

/**
 * Class which stores properties related to Lotl (List of Trusted Lists) fetching and validation process.
 */
public class LotlFetchingProperties {
    private final IOnCountryFetchFailureStrategy onCountryFetchFailureStrategy;
    private Set<String> ignoredSchemaNames = new HashSet<>();
    private HashSet<String> serviceTypes = new HashSet<>();
    private HashSet<String> schemaNames = new HashSet<>();
    //default time out for invalidating cache is  24 hours in milliseconds
    private long staleNessInMillis = 24L * 60L * 60L * 1000L;
    private LongUnaryOperator refreshIntervalCalculator = stalenessTime -> {
        // This function can be used to set a custom cache refresh timer based on the staleness time.
        // For now, we take 70% of the staleness time as the refresh interval.
        if (stalenessTime <= 0) {
            throw new PdfException(SignExceptionMessageConstant.STALENESS_MUST_BE_POSITIVE);
        }
        final double PERCENTAGE = 0.7D;
        return (long) (stalenessTime * PERCENTAGE);
    };

    /**
     * Creates an instance of {@link LotlFetchingProperties}.
     *
     * @param countryFetchFailureStrategy strategy to be used when fetching a country specific Lotl fails
     */
    public LotlFetchingProperties(IOnCountryFetchFailureStrategy countryFetchFailureStrategy) {
        this.onCountryFetchFailureStrategy = countryFetchFailureStrategy;
    }

    /**
     * Adds schema name (usually two letters) of a country which shall be used during Lotl fetching.
     * <p>
     * If no schema names are added, all country specific Lotl files will be used.
     *
     * @param countryNames schema names of countries to use
     *
     * @return this same {@link LotlFetchingProperties} instance
     */
    public LotlFetchingProperties setCountryNames(String... countryNames) {
        if (!ignoredSchemaNames.isEmpty()) {
            throw new IllegalArgumentException(
                    SignExceptionMessageConstant.EITHER_USE_SCHEMA_NAME_OR_IGNORE_SCHEMA_NAME);
        }
        this.schemaNames = new HashSet<>(Arrays.asList(countryNames));
        return this;
    }

    /**
     * Adds schema name (usually two letters) of a country which shall be ignored during Lotl fetching.
     * <p>
     *
     * @param countryNamesToIgnore countries to ignore
     *
     * @return this same {@link LotlFetchingProperties} instance
     */
    public LotlFetchingProperties setCountryNamesToIgnore(String... countryNamesToIgnore) {
        if (!schemaNames.isEmpty()) {
            throw new IllegalArgumentException(
                    SignExceptionMessageConstant.EITHER_USE_SCHEMA_NAME_OR_IGNORE_SCHEMA_NAME);
        }
        this.ignoredSchemaNames = new HashSet<>(Arrays.asList(countryNamesToIgnore));
        return this;
    }

    /**
     * Get the cache staleness in milliseconds.
     *
     * @return a set cache staleness in milliseconds.
     */
    public long getCacheStalenessInMilliseconds() {
        return staleNessInMillis;
    }

    /**
     * Sets the cache staleness in milliseconds.
     * <p>
     * This value determines how long the cache will be considered valid before it is refreshed.
     * If the cache is older than this value, it will be refreshed.
     * <p>
     * The default value is 24 hours (24 * 60 * 60 * 1000 milliseconds).
     *
     * @param staleNessInMillis the staleness time in milliseconds
     *
     * @return this same {@link LotlFetchingProperties} instance
     */
    public LotlFetchingProperties setCacheStalenessInMilliseconds(long staleNessInMillis) {
        if (staleNessInMillis <= 0) {
            throw new PdfException(SignExceptionMessageConstant.STALENESS_MUST_BE_POSITIVE);
        }
        this.staleNessInMillis = staleNessInMillis;
        return this;
    }

    /**
     * Gets the calculation function for the cache refresh interval.
     * <p>
     * This function will be used to determine the refresh interval based on the staleness time.
     * By default, it takes 70% of the staleness time as the refresh interval.
     *
     * @return a function that takes the staleness time in milliseconds and returns the refresh interval in
     * milliseconds.
     */
    public LongUnaryOperator getRefreshIntervalCalculator() {
        return refreshIntervalCalculator;
    }

    /**
     * Sets a custom cache refresh timer function. This function will be used to determine the refresh interval
     * based on the staleness time.
     * <p>
     * By default, it takes 70% of the staleness time as the refresh interval.
     * So if the staleness time is 24 hours, the refresh interval will be set to 16.8 hours. Which means the cache will
     * be refreshed every 16.8 hours.
     *
     * @param refreshIntervalCalculator a function that takes the staleness time in milliseconds and returns the refresh
     *                                  interval in milliseconds.
     */
    public void setRefreshIntervalCalculator(LongUnaryOperator refreshIntervalCalculator) {
        this.refreshIntervalCalculator = refreshIntervalCalculator;
    }

    /**
     * Gets the strategy to be used when fetching a country specific Lotl fails.
     *
     * @return the strategy to be used when fetching a country specific Lotl fails
     */
    public IOnCountryFetchFailureStrategy getOnCountryFetchFailureStrategy() {
        return onCountryFetchFailureStrategy;
    }

    Set<String> getServiceTypes() {
        return Collections.unmodifiableSet(serviceTypes);
    }

    /**
     * Adds service type identifier which shall be used during country specific Lotl fetching.
     * <p>
     * If no service type identifiers are added, all certificates in country specific Lotl files will be used.
     *
     * @param serviceType service type identifier as a {@link String}
     *
     * @return this same {@link LotlFetchingProperties} instance
     */
    public LotlFetchingProperties setServiceTypes(String... serviceType) {
        this.serviceTypes = new HashSet<>(Arrays.asList(serviceType));
        return this;
    }

    /**
     * Checks if the schema should be processed based on the current configuration.
     *
     * @param countryName the country name to use
     *
     * @return this instance for method chaining
     */
    boolean shouldProcessCountry(String countryName) {
        if (!schemaNames.isEmpty() && !ignoredSchemaNames.isEmpty()) {
            throw new IllegalStateException(SignExceptionMessageConstant.EITHER_USE_SCHEMA_NAME_OR_IGNORE_SCHEMA_NAME);
        }
        if (schemaNames.isEmpty() && ignoredSchemaNames.isEmpty()) {
            // If no specific schema names are set, process all
            return true;
        }
        if (!ignoredSchemaNames.isEmpty()) {
            // Process if schema is not in ignored list
            return !ignoredSchemaNames.contains(countryName);
        }
        // Process if schema is in the specified list
        return schemaNames.contains(countryName);
    }
}
