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
package com.itextpdf.signatures.validation;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.security.cert.Certificate;
import java.util.List;
import java.util.function.Supplier;

/**
 * Abstract factory class for configuring and retrieving European Trusted List configurations.
 * This class provides methods to get and set the factory implementation, as well as abstract
 * methods to retrieve trusted list-related information.
 */
public abstract class EuropeanTrustedListConfigurationFactory {

    /**
     * Supplier for the current factory implementation.
     * By default, it uses {@link LoadFromModuleEuropeanTrustedListConfigurationFactory}.
     */
    private static Supplier<EuropeanTrustedListConfigurationFactory> configuration =
            () -> {
                try {
                    return new LoadFromModuleEuropeanTrustedListConfigurationFactory();
                } catch (Exception e) {
                    throw new PdfException(SignExceptionMessageConstant.EU_RESOURCES_NOT_LOADED);
                }
            };

    /**
     * Retrieves the current factory supplier.
     *
     * @return the current factory supplier
     */
    public static Supplier<EuropeanTrustedListConfigurationFactory> getFactory() {
        return configuration;
    }

    /**
     * Sets the factory supplier.
     *
     * @param factory the new factory supplier to set
     * @throws IllegalArgumentException if the provided factory is null
     */
    public static void setFactory(Supplier<EuropeanTrustedListConfigurationFactory> factory) {
        if (factory == null) {
            throw new IllegalArgumentException("Factory cannot be null");
        }
        configuration = factory;
    }

    /**
     * Retrieves the URI of the trusted list.
     *
     * @return the trusted list URI
     */
    public abstract String getTrustedListUri();

    /**
     * Retrieves the currently supported publication of the trusted list.
     *
     * @return the currently supported publication
     */
    public abstract String getCurrentlySupportedPublication();

    /**
     * Retrieves the list of certificates from the trusted list.
     *
     * @return a list of certificates
     */
    public abstract List<Certificate> getCertificates();
}
