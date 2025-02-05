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
package com.itextpdf.bouncycastleconnector;

import com.itextpdf.bouncycastle.BouncyCastleFactory;
import com.itextpdf.bouncycastleconnector.logs.BouncyCastleLogMessageConstant;
import com.itextpdf.bouncycastlefips.BouncyCastleFipsFactory; // Android-Conversion-Skip-Line (BC FIPS isn't supported on Android)
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.utils.SystemUtil;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class provides the ability to create {@link IBouncyCastleFactory} instance.
 * User chooses which bouncy-castle will be created by specifying dependency.
 * Bouncy-castle dependency must be added in order to use this class.
 */
public final class BouncyCastleFactoryCreator {

    private static IBouncyCastleFactory factory;
    
    private static final Map<String, Supplier<IBouncyCastleFactory>> FACTORIES = new LinkedHashMap<>();
    
    private static final String FACTORY_ENVIRONMENT_VARIABLE_NAME = "ITEXT_BOUNCY_CASTLE_FACTORY_NAME";

    private static final Logger LOGGER = LoggerFactory.getLogger(BouncyCastleFactoryCreator.class);

    static {
        populateFactoriesMap();
        
        String factoryName = SystemUtil.getPropertyOrEnvironmentVariable(FACTORY_ENVIRONMENT_VARIABLE_NAME);
        Supplier<IBouncyCastleFactory> systemVariableFactoryCreator = FACTORIES.get(factoryName);
        if (systemVariableFactoryCreator != null) {
            tryCreateFactory(systemVariableFactoryCreator);
        }
        
        for (Supplier<IBouncyCastleFactory> factorySupplier : FACTORIES.values()) {
            if (factory != null) {
                break;
            }
            tryCreateFactory(factorySupplier);
        }
        
        if (factory == null) {
            LOGGER.error(BouncyCastleLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
            factory = new BouncyCastleDefaultFactory();
        }
    }

    private BouncyCastleFactoryCreator() {
        // Empty constructor.
    }

    /**
     * Sets {@link IBouncyCastleFactory} instance, which will be used for bouncy-castle classes creation.
     * 
     * @param newFactory {@link IBouncyCastleFactory} instance to be set.
     */
    public static void setFactory(IBouncyCastleFactory newFactory) {
        factory = newFactory;
    }

    /**
     * Returns {@link IBouncyCastleFactory} instance for bouncy-castle classes creation.
     *
     * @return {@link IBouncyCastleFactory} implementation.
     */
    public static IBouncyCastleFactory getFactory() {
        return factory;
    }
    
    private static void tryCreateFactory(Supplier<IBouncyCastleFactory> factoryCreator) {
        try {
            createFactory(factoryCreator);
        } catch (NoClassDefFoundError ignored) {
            // Do nothing if factory cannot be created.
        }
    }
    
    private static void createFactory(Supplier<IBouncyCastleFactory> factoryCreator) {
        factory = factoryCreator.get();
    }

    private static void populateFactoriesMap() {
        FACTORIES.put("bouncy-castle", () -> new BouncyCastleFactory());
        FACTORIES.put("bouncy-castle-fips", () -> new BouncyCastleFipsFactory()); // Android-Conversion-Skip-Line (BC FIPS isn't supported on Android)
    }
}
