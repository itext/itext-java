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


}

