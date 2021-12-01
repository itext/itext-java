package com.itextpdf.signatures.logs;

/**
 * Class which contains constants to be used in logging inside sign module.
 */
public final class SignLogMessageConstant {

    public static final String EXCEPTION_WITHOUT_MESSAGE =
            "Unexpected exception without message was thrown during keystore processing";

    private SignLogMessageConstant() {
        // Private constructor will prevent the instantiation of this class directly
    }
}
