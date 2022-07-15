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
