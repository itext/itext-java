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

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Class which stores properties related to LOTL (List of Trusted Lists) fetching and validation process.
 */
public class LOTLFetchingProperties {
    private final HashSet<String> schemaNames = new HashSet<>();
    private final HashSet<String> serviceTypes = new HashSet<>();

    /**
     * Creates an instance of {@link LOTLFetchingProperties}.
     */
    public LOTLFetchingProperties() {
        // Empty constructor.
    }

    /**
     * Adds schema name (usually two letters) of a country which shall be used during LOTL fetching.
     * <p>
     * If no schema names are added, all country specific LOTL files will be used.
     *
     * @param schemaName country schema name as a {@link String}
     *
     * @return this same {@link LOTLFetchingProperties} instance
     */
    public LOTLFetchingProperties addSchemaName(String schemaName) {
        schemaNames.add(schemaName);
        return this;
    }

    /**
     * Adds service type identifier which shall be used during country specific LOTL fetching.
     * <p>
     * If no service type identifiers are added, all certificates in country specific LOTL files will be used.
     *
     * @param serviceType service type identifier as a {@link String}
     *
     * @return this same {@link LOTLFetchingProperties} instance
     */
    public LOTLFetchingProperties addServiceType(String serviceType) {
        serviceTypes.add(serviceType);
        return this;
    }

    Set<String> getSchemaNames() {
        return Collections.<String>unmodifiableSet(schemaNames);
    }

    Set<String> getServiceTypes() {
        return Collections.<String>unmodifiableSet(serviceTypes);
    }
}
