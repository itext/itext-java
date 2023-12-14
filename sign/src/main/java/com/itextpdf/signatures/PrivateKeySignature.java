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
package com.itextpdf.signatures;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;

/**
 * Implementation of the {@link IExternalSignature} interface that
 * can be used when you have a {@link PrivateKey} object.
 *
 * @author Paulo Soares
 */
public class PrivateKeySignature implements IExternalSignature {

    /**
     * The private key object.
     */
    private PrivateKey pk;

    /**
     * The hash algorithm.
     */
    private String hashAlgorithm;

    /**
     * The encryption algorithm (obtained from the private key)
     */
    private String encryptionAlgorithm;

    /**
     * The security provider
     */
    private String provider;

    /**
     * Creates a {@link PrivateKeySignature} instance.
     *
     * @param pk            A {@link PrivateKey} object.
     * @param hashAlgorithm A hash algorithm (e.g. "SHA-1", "SHA-256",...).
     * @param provider      A security provider (e.g. "BC").
     */
    public PrivateKeySignature(PrivateKey pk, String hashAlgorithm, String provider) {
        this.pk = pk;
        this.provider = provider;
        this.hashAlgorithm = DigestAlgorithms.getDigest(DigestAlgorithms.getAllowedDigest(hashAlgorithm));
        this.encryptionAlgorithm = SignUtils.getPrivateKeyAlgorithm(pk);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getHashAlgorithm() {
        return hashAlgorithm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getEncryptionAlgorithm() {
        return encryptionAlgorithm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] sign(byte[] message) throws GeneralSecurityException {
        String algorithm = hashAlgorithm + "with" + encryptionAlgorithm;
        Signature sig = SignUtils.getSignatureHelper(algorithm, provider);
        sig.initSign(pk);
        sig.update(message);
        return sig.sign();
    }
}
