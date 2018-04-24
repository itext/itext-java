package com.itextpdf.signatures;

/**
 * Class containing all the OID values used by iText.
 */
public final class OID {

    private OID() {
        // Empty on purpose. Avoiding instantiation of this class.
    }

    /**
     * Contains all OIDs used by iText in the context of Certificate Extensions.
     */
    public static final class X509Extensions {
        public static final String BASIC_CONSTRAINTS = "2.5.29.19";
        public static final String EXTENDED_KEY_USAGE = "2.5.29.37";
        public static final String ID_KP_TIMESTAMPING = "1.3.6.1.5.5.7.3.8";
        public static final String KEY_USAGE = "2.5.29.15";
    }
}