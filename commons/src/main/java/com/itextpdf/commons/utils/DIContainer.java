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
package com.itextpdf.commons.utils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Supplier;

/**
 * A simple dependency injection container.
 * <p>
 * The container is thread-safe.
 */
public class DIContainer {

    private static final ConcurrentHashMap<Class<?>, Supplier<Object>> instances = new ConcurrentHashMap<>();

    private final ConcurrentHashMap<Class<?>, Object> localInstances = new ConcurrentHashMap<>();

    static {
        DIContainerConfigurations.loadDefaultConfigurations();
    }


    /**
     * Creates a new instance of {@link DIContainer}.
     */
    public DIContainer() {
        // Empty constructor
    }

    /**
     * Registers a default instance for a class.
     *
     * @param clazz    the class
     * @param supplier supplier of the instance
     */
    public static void registerDefault(Class<?> clazz, Supplier<Object> supplier) {
        instances.put(clazz, supplier);
    }

    /**
     * Registers an instance for a class.
     *
     * @param clazz the class
     * @param inst  the instance
     */
    public void register(Class<?> clazz, Object inst) {
        localInstances.put(clazz, inst);
    }

    /**
     * Gets an instance of a class.
     *
     * @param clazz the class
     * @param <T>   the type of the class
     *
     * @return the instance
     */
    public <T> T getInstance(Class<T> clazz) {
        Object supplier = localInstances.get(clazz);
        if (supplier == null) {
            supplier = instances.get(clazz).get();
        }
        if (supplier == null) {
            throw new RuntimeException("No instance registered for class " + clazz.getName());
        }
        return (T) supplier;
    }

    /**
     * Checks if an instance is registered for a class.
     * If the class is registered but the value is null, it will still return {@code true}.
     *
     * @param clazz the class
     *
     * @return {@code true} if an instance is registered, {@code false} otherwise
     */
    public boolean isRegistered(Class<?> clazz) {
        return localInstances.containsKey(clazz) || instances.containsKey(clazz);
    }
}
