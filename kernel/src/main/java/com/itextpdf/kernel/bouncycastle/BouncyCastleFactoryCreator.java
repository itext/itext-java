package com.itextpdf.kernel.bouncycastle;

import com.itextpdf.bouncycastle.BouncyCastleFactory;
import com.itextpdf.bouncycastlefips.BouncyCastleFipsFactory;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.kernel.logs.KernelLogMessageConstant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class BouncyCastleFactoryCreator {

    private static IBouncyCastleFactory factory;

    private static final Logger LOGGER = LoggerFactory.getLogger(BouncyCastleFactoryCreator.class);

    static {
        try {
            Class.forName("org.bouncycastle.jce.provider.BouncyCastleProvider");
            // If BouncyCastleProvider exists in classpath assume that default bouncy-castle shall be used.
            factory = new BouncyCastleFactory();
        } catch (ClassNotFoundException ignored) {
            try {
                Class.forName("org.bouncycastle.jcajce.provider.BouncyCastleFipsProvider");
                // If there is no BouncyCastleProvider in classpath, but there is BouncyCastleFipsProvider assume
                // that bouncy-castle-fips shall be used.
                factory = new BouncyCastleFipsFactory();
            } catch (ClassNotFoundException exception) {
                // If there are neither BouncyCastleProvider nor BouncyCastleFipsProvider assume that bouncy-castle
                // related logic mustn't be used at all.
                // TODO Decide whether to use log or exception.
                LOGGER.error(KernelLogMessageConstant.BOUNCY_CASTLE_DEPENDENCY_MUST_PRESENT);
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
