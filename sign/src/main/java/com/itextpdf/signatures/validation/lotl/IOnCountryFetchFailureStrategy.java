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

/**
 * Interface for handling the failure of fetching a country-specific List of Trusted Lists (Lotl).
 * Implementations can define custom strategies for dealing with such failures.
 * <p>
 * //TODO mention default implementations
 */
public interface IOnCountryFetchFailureStrategy {

    //TODO add javadoc for doing stuff related  certificates and validation report and their items will be added to
    // the main report
    //so if invalid is added main report will be invalidated

    /**
     * This method is called when the fetching of a country-specific Lotl fails.
     * It allows for custom handling of the failure.
     *
     * @param fetchResult the result of the fetch attempt, which may contain error details
     */
    void onCountryFetchFailure(CountrySpecificLotlFetcher.Result fetchResult);


}

