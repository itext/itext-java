package com.itextpdf.kernel.pdf;

import java.io.Serializable;

public class EncryptionConstants implements Serializable {

    private static final long serialVersionUID = 6234590207803219761L;

    /**
     * Type of encryption.
     */
    public static final int STANDARD_ENCRYPTION_40 = 0;
    /**
     * Type of encryption.
     */
    public static final int STANDARD_ENCRYPTION_128 = 1;
    /**
     * Type of encryption.
     */
    public static final int ENCRYPTION_AES_128 = 2;
    /**
     * Type of encryption.
     */
    public static final int ENCRYPTION_AES_256 = 3;
    /**
     * Mask to separate the encryption type from the encryption mode.
     */
    static final int ENCRYPTION_MASK = 7;
    /**
     * Add this to the mode to keep the metadata in clear text.
     */
    public static final int DO_NOT_ENCRYPT_METADATA = 8;
    /**
     * Add this to the mode to keep encrypt only the embedded files.
     */
    public static final int EMBEDDED_FILES_ONLY = 24;    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_PRINTING = 4 + 2048;

    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_MODIFY_CONTENTS = 8;

    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_COPY = 16;

    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_MODIFY_ANNOTATIONS = 32;

    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_FILL_IN = 256;

    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_SCREENREADERS = 512;

    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_ASSEMBLY = 1024;

    /**
     * The operation permitted when the document is opened with the user password.
     */
    public static final int ALLOW_DEGRADED_PRINTING = 4;
}
