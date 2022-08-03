/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2022 iText Group NV
    Authors: iText Software.

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

import com.itextpdf.bouncycastle.BouncyCastleFactory;
import com.itextpdf.bouncycastleconnector.logs.BouncyCastleLogMessageConstant;
import com.itextpdf.bouncycastlefips.BouncyCastleFipsFactory;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides the ability to create {@link IBouncyCastleFactory} instance
 * to create bouncy-castle or bouncy-castle FIPS classes instances. User chooses which
 * bouncy-castle will be used by specifying dependency, so either bouncy-castle or
 * bouncy-castle-fips dependency must be added in order to use this class.
 */
public final class BouncyCastleFactoryCreator {

    private static IBouncyCastleFactory factory;

    private static final Logger LOGGER = LoggerFactory.getLogger(BouncyCastleFactoryCreator.class);

    static {
        try {
            factory = new BouncyCastleFactory();
        } catch (NoClassDefFoundError error) {
            try {
                factory = new BouncyCastleFipsFactory();
            } catch (NoClassDefFoundError ignored) {
                LOGGER.error(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
            }
        }
    }

    private BouncyCastleFactoryCreator() {
        // Empty constructor.
    }

    /**
     * Returns {@link IBouncyCastleFactory} instance to create bouncy-castle or bouncy-castle FIPS
     * classes instances depending on specified dependency.
     *
     * @return {@link IBouncyCastleFactory} appropriate implementation.
     */
    public static IBouncyCastleFactory getFactory() {
        return factory;
    }
}
