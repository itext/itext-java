package com.itextpdf.io.font.cmap;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


public final class StandardCMapCharsets {
    private static final Map<String, CMapCharsetEncoder> encoders = new HashMap<>();
    private static final CMapCharsetEncoder UTF16_ENCODER = new CMapCharsetEncoder(StandardCharsets.UTF_16BE);
    private static final CMapCharsetEncoder UCS2_ENCODER = new CMapCharsetEncoder(StandardCharsets.UTF_16BE, true);

    private StandardCMapCharsets() {

    }

    private static void registerHV(String cmapPrefix, CMapCharsetEncoder encoder) {
        encoders.put(cmapPrefix + "-H", encoder);
        encoders.put(cmapPrefix + "-V", encoder);
    }

    static {
        registerEncoder();
    }

    private static void registerEncoder() {
        // Register encoders for all standard non-identity CMaps in PDF

        // Simplified Chinese
        registerHV("UniGB-UCS2", UCS2_ENCODER);
        registerHV("UniGB-UTF16", UTF16_ENCODER);

        // Traditional Chinese
        registerHV("UniCNS-UCS2", UCS2_ENCODER);
        registerHV("UniCNS-UTF16", UTF16_ENCODER);

        // Japanese
        registerHV("UniJIS-UCS2", UCS2_ENCODER);
        registerHV("UniJIS-UTF16", UTF16_ENCODER);

        // Korean
        registerHV("UniKS-UCS2", UCS2_ENCODER);
        registerHV("UniKS-UTF16", UTF16_ENCODER);
    }

    public static CMapCharsetEncoder getEncoder(String stdCmapName) {
        return encoders.get(stdCmapName);
    }

    /**
     * Charset encoders are disabled.
     */
    public static void disableCharsetEncoders() {
        encoders.clear();
    }

    /**
     * Charset encoders are enabled (default).
     */
    public static void enableCharsetEncoders() {
        if ( encoders.size() == 0 ) {
            registerEncoder();
        }
    }

}
