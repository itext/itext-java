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
