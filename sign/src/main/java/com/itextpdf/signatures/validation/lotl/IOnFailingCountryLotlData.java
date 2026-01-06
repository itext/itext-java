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

/**
 * Interface for handling the failure of fetching a country-specific trusted list.
 * Implementations can define custom strategies for dealing with such failures.
 * <p>
 * This strategy is used for the handling of the cases where the connection to the third-party endpoint for fetching
 * the trusted list for a specific country fails. This can happen for various reasons,
 * such as network issues, server downtime, or invalid responses. This strategy will be called per each country-specific
 * EU trusted list that is not available on initialization or when the certificates staleness threshold is reached
 * at the moment of digital signatures validation attempt if it relies on EU trusted lists. See
 * {@link LotlFetchingProperties#setCacheStalenessInMilliseconds(long)} for details about the staleness threshold.
 * <p>
 * We provide 2 default implementations out of the box:
 * <p>
 * - {@link ThrowExceptionOnFailingCountryData} - which will throw an exception
 * if the fetching of a country-specific trusted list fails.
 * In cache initialization this means that initialization will be halted.
 * In cache update this means that unavailable country-specific trusted certificates will not be updated,
 * the validation will continue until the certificate staleness threshold will be reached,
 * but when staleness threshold is reached this strategy will cause the validation attempts to fail with exception
 * if they rely on EU trusted lists.
 * <p>
 * - {@link RemoveOnFailingCountryData} - which just silently removes not available country-specific certificates from
 * the trust store, thus the validation results might change depending on success of certificates fetching.
 * In cache initialization this means that the country-specific trusted certificates will not be added to the trust
 * store.
 * In cache update this means that unavailable country-specific trusted certificates will not be updated,
 * the validation process will continue until the certificate staleness threshold will be reached,
 * but when the staleness threshold is reached this strategy will silently remove the outdated certificates.
 */
public interface IOnFailingCountryLotlData {


    /**
     * This method is called when the fetching of a country-specific Lotl fails.
     * It allows for custom handling of the failure.
     * <p>
     * If the implementation does not throw an exception, the validation process will continue, and the certificates
     * from the {@code CountrySpecificLotlFetcher.Result } will not be added to the trust store.
     *
     * @param fetchResult the result of the fetch attempt, which may contain error details
     */
    void onCountryFailure(CountrySpecificLotlFetcher.Result fetchResult);


}

