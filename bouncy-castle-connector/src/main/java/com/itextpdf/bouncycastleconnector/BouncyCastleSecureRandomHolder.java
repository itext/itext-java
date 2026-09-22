package com.itextpdf.bouncycastleconnector;

import java.security.SecureRandom;

/**
 * A utility class for exception-free initialization of bouncy-castle-dependent
 * secure random number generator. It is meant to be used specifically for
 * instances initialized as a static field value, because exceptions thrown
 * during class initialization causes troubles.
 */
public class BouncyCastleSecureRandomHolder {
    private volatile SecureRandom RNG = null;
    private Object lock = new Object();

    /**
     * Creates a new {@link BouncyCastleSecureRandomHolder} instance.
     */
    public BouncyCastleSecureRandomHolder() {
        // empty constructor
    }

    /**
     * Gets a secure random number generator instance.
     *
     * <p>
     * If bouncy-castle dependency is missing it will throw an exception.
     *
     * @return the lazily initialized {@link SecureRandom}
     */
    public SecureRandom getSecureRandom() {
        if (RNG == null) {
            synchronized (lock) {
                if (RNG == null) {
                    // can throw an exception if BC is missing
                    RNG = BouncyCastleFactoryCreator.getFactory().getSecureRandom();
                }
            }
        }
        return RNG;
    }
}
