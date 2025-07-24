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
 * This class implements the {@link IOnCountryFetchFailureStrategy} interface and provides a strategy
 * for handling failures when fetching country-specific Lotl (List of Trusted Lists) files.
 * It ignores the failure of the specific country, and converts all report items to INFO status.
 * This way the country-specific Lotl is not used, the validation report is not invalid, but the items are still
 * preserved.
 */
public class IgnoreCountrySpecificCertificates implements IOnCountryFetchFailureStrategy {

    /**
     * Constructs an instance of {@link IgnoreCountrySpecificCertificates}.
     */
    public IgnoreCountrySpecificCertificates() {
        //Empty constructor
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onCountryFetchFailure(CountrySpecificLotlFetcher.Result fetchResult) {
        // we do nothing here, as we ignore the failure of the specific country
    }
}
