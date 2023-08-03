/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2023 Apryse Group NV
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

import com.itextpdf.kernel.security.IExternalDecryptionProcess;

import java.security.Key;
import java.security.cert.Certificate;

public class ReaderProperties {


    //added by ujihara for decryption
    protected byte[] password;

    //added by Aiken Sam for certificate decryption
    protected Key certificateKey;
    //added by Aiken Sam for certificate decryption
    protected Certificate certificate;
    //added by Aiken Sam for certificate decryption
    protected String certificateKeyProvider;
    protected IExternalDecryptionProcess externalDecryptionProcess;

    protected MemoryLimitsAwareHandler memoryLimitsAwareHandler;

    /**
     * Defines the password which will be used if the document is encrypted with standard encryption.
     * This could be either user or owner password.
     *
     * @param password the password to use in order to open the document
     * @return this {@link ReaderProperties} instance
     */
    public ReaderProperties setPassword(byte[] password) {
        clearEncryptionParams();
        this.password = password;
        return this;
    }

    /**
     * Defines the certificate which will be used if the document is encrypted with public key
     * encryption (see Pdf 1.7 specification, 7.6.4. Public-Key Security Handlers)
     *
     * @param certificate               the recipient {@link Certificate},
     *                                  serves as recipient identifier
     * @param certificateKey            the recipient private {@link Key} to the certificate
     * @param certificateKeyProvider    the certificate key provider id
     *                                  for {@link java.security.Security#getProvider(String)}
     * @param externalDecryptionProcess the external decryption process to be used
     * @return this {@link ReaderProperties} instance
     */
    public ReaderProperties setPublicKeySecurityParams(Certificate certificate, Key certificateKey,
            String certificateKeyProvider, IExternalDecryptionProcess externalDecryptionProcess) {
        clearEncryptionParams();
        this.certificate = certificate;
        this.certificateKey = certificateKey;
        this.certificateKeyProvider = certificateKeyProvider;
        this.externalDecryptionProcess = externalDecryptionProcess;

        return this;
    }

    /**
     * Defines the certificate which will be used if the document is encrypted with public key
     * encryption (see Pdf 1.7 specification, 7.6.4. Public-Key Security Handlers)
     *
     * @param certificate               the recipient {@link Certificate},
     *                                  serves as recipient identifier
     * @param externalDecryptionProcess the external decryption process to be used
     * @return this {@link ReaderProperties} instance
     */
    public ReaderProperties setPublicKeySecurityParams(Certificate certificate, IExternalDecryptionProcess externalDecryptionProcess) {
        clearEncryptionParams();
        this.certificate = certificate;
        this.externalDecryptionProcess = externalDecryptionProcess;
        return this;
    }

    private void clearEncryptionParams() {
        this.password = null;
        this.certificate = null;
        this.certificateKey = null;
        this.certificateKeyProvider = null;
        this.externalDecryptionProcess = null;
    }

    /**
     * Sets the memory handler which will be used to handle decompressed PDF streams.
     *
     * @param memoryLimitsAwareHandler the memory handler which will be used to handle decompressed PDF streams
     * @return this {@link ReaderProperties} instance
     */
    public ReaderProperties setMemoryLimitsAwareHandler(MemoryLimitsAwareHandler memoryLimitsAwareHandler) {
        this.memoryLimitsAwareHandler = memoryLimitsAwareHandler;
        return this;
    }

}
