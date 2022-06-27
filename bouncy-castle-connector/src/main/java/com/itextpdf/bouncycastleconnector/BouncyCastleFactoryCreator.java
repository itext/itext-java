package com.itextpdf.bouncycastleconnector;

import com.itextpdf.bouncycastle.BouncyCastleFactory;
import com.itextpdf.bouncycastleconnector.logs.BouncyCastleLogMessageConstant;
import com.itextpdf.bouncycastlefips.BouncyCastleFipsFactory;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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

    public static IBouncyCastleFactory getFactory() {
        return factory;
    }
}
