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
