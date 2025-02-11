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
package com.itextpdf.kernel.pdf;

/**
 * Encryption constants for {@link WriterProperties#setStandardEncryption(byte[], byte[], int, int)}.
 */
public final class EncryptionConstants {
    private EncryptionConstants() {
        // Empty constructor
    }

    /**
     * Type of encryption. RC4 encryption algorithm will be used with the key length of 40 bits.
     */
    public static final int STANDARD_ENCRYPTION_40 = 0;
    /**
     * Type of encryption. RC4 encryption algorithm will be used with the key length of 128 bits.
     */
    public static final int STANDARD_ENCRYPTION_128 = 1;
    /**
     * Type of encryption. AES encryption algorithm will be used with the key length of 128 bits.
     */
    public static final int ENCRYPTION_AES_128 = 2;
    /**
     * Type of encryption. AES encryption algorithm will be used with the key length of 256 bits.
     */
    public static final int ENCRYPTION_AES_256 = 3;
    /**
     * Type of encryption. Advanced Encryption Standard-Galois/Counter Mode (AES-GCM) encryption algorithm.
     */
    public static final int ENCRYPTION_AES_GCM = 4;
    /**
     * Add this to the mode to keep the metadata in clear text.
     */
    public static final int DO_NOT_ENCRYPT_METADATA = 8;
    /**
     * Add this to the mode to encrypt only the embedded files.
     */
    public static final int EMBEDDED_FILES_ONLY = 24;
    /**
     * The operation is permitted when the document is opened with the user password.
     */
    public static final int ALLOW_PRINTING = 4 + 2048;
    /**
     * The operation is permitted when the document is opened with the user password.
     */
    public static final int ALLOW_MODIFY_CONTENTS = 8;
    /**
     * The operation is permitted when the document is opened with the user password.
     */
    public static final int ALLOW_COPY = 16;
    /**
     * The operation is permitted when the document is opened with the user password.
     */
    public static final int ALLOW_MODIFY_ANNOTATIONS = 32;
    /**
     * The operation is permitted when the document is opened with the user password.
     */
    public static final int ALLOW_FILL_IN = 256;
    /**
     * The operation is permitted when the document is opened with the user password.
     */
    public static final int ALLOW_SCREENREADERS = 512;
    /**
     * The operation is permitted when the document is opened with the user password.
     */
    public static final int ALLOW_ASSEMBLY = 1024;
    /**
     * The operation is permitted when the document is opened with the user password.
     */
    public static final int ALLOW_DEGRADED_PRINTING = 4;


    /**
     * Mask to separate the encryption type from the encryption mode.
     */
    static final int ENCRYPTION_MASK = 7;
}
