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
package com.itextpdf.commons.bouncycastle;

import java.security.Provider;
import java.security.Security;

/**
 * This class is used to register a security provider at runtime if it is not already registered.
 * But if the provider is already registered, it will return the existing provider.
 * This is useful when building with graalvm, as the provider will be registered at compile time.
 * And using a provider that registered at runtime will cause errors.
 */
public class SecurityProviderProxy {

    private final Provider currentProvider;
    private final String currentProviderName;

    /**
     * Constructor that registers the provider if it is not already registered.
     *
     * @param provider The provider to register or get if it was registered
     */
    public SecurityProviderProxy(Provider provider) {
        String providerName = provider.getName();
        Provider retProvider = Security.getProvider(providerName);
        // Provider will be registered at compile time if graalvm is being used
        // Otherwise fallback to runtime registration
        if (retProvider == null) {
            currentProvider = provider;
            currentProviderName = providerName;
            Security.addProvider(provider);
        } else {
            currentProvider = retProvider;
            currentProviderName = providerName;
        }
    }

    /**
     * Gets the provider that was registered.
     *
     * @return The provider that was registered
     */
    public Provider getProvider() {
        return currentProvider;
    }

    /**
     * Gets the name of the provider that was registered.
     *
     * @return The name of the provider that was registered
     */
    public String getProviderName() {
        return currentProviderName;
    }
}
