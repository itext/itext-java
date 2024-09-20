/*
    This file is part of the iText (R) project.
    Copyright (c) 1998-2024 Apryse Group NV
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

import com.itextpdf.commons.utils.MessageFormatUtil;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.crypto.OID;
import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

import java.security.GeneralSecurityException;
import java.security.PrivateKey;
import java.security.Signature;

/**
 * Implementation of the {@link IExternalSignature} interface that
 * can be used when you have a {@link PrivateKey} object.
 */
public class PrivateKeySignature implements IExternalSignature {

    /**
     * The private key object.
     */
    private final PrivateKey pk;

    /**
     * The hash algorithm.
     */
    private final String hashAlgorithm;

    /**
     * The encryption algorithm (obtained from the private key)
     */
    private final String signatureAlgorithm;

    /**
     * The security provider
     */
    private final String provider;

    /**
     * The algorithm parameters.
     */
    private final IApplicableSignatureParams parameters;

    /**
     * Creates a {@link PrivateKeySignature} instance.
     *
     * @param pk            A {@link PrivateKey} object.
     * @param hashAlgorithm A hash algorithm (e.g. "SHA-1", "SHA-256",...).
     * @param provider      A security provider (e.g. "BC").
     */
    public PrivateKeySignature(PrivateKey pk, String hashAlgorithm, String provider) {
        this(pk, hashAlgorithm, null, provider, null);
    }

    /**
     * Creates a {@link PrivateKeySignature} instance.
     *
     * @param pk                 A {@link PrivateKey} object.
     * @param hashAlgorithm      A hash algorithm (e.g. "SHA-1", "SHA-256",...).
     * @param signatureAlgorithm A signiture algorithm (e.g. "RSASSA-PSS", "id-signedData",
     *                           "sha256WithRSAEncryption", ...)
     * @param provider           A security provider (e.g. "BC").
     * @param params             Parameters for using RSASSA-PSS or other algorithms requiring them.
     */
    public PrivateKeySignature(PrivateKey pk, String hashAlgorithm, String signatureAlgorithm, String provider,
                               IApplicableSignatureParams params) {
        this.pk = pk;
        this.provider = provider;
        String digestAlgorithmOid = DigestAlgorithms.getAllowedDigest(hashAlgorithm);
        this.hashAlgorithm = DigestAlgorithms.getDigest(digestAlgorithmOid);

        String adjustedSignatureAlgorithm = signatureAlgorithm == null ?
                SignUtils.getPrivateKeyAlgorithm(pk) : signatureAlgorithm;

        if ("RSA/PSS".equals(adjustedSignatureAlgorithm)) {
            this.signatureAlgorithm = "RSASSA-PSS";
        } else {
            this.signatureAlgorithm = adjustedSignatureAlgorithm;
        }

        switch (this.signatureAlgorithm) {
            case "Ed25519":
                if (!OID.SHA_512.equals(digestAlgorithmOid)) {
                    throw new PdfException(SignExceptionMessageConstant.ALGO_REQUIRES_SPECIFIC_HASH)
                            .setMessageParams("Ed25519", "SHA-512", this.hashAlgorithm);
                }
                this.parameters = null;
                break;
            case "Ed448":
                if (!OID.SHAKE_256.equals(digestAlgorithmOid)) {
                    throw new PdfException(SignExceptionMessageConstant.ALGO_REQUIRES_SPECIFIC_HASH)
                            .setMessageParams("Ed448", "512-bit SHAKE256", this.hashAlgorithm);
                }
                this.parameters = null;
                break;
            case "EdDSA":
                throw new IllegalArgumentException(
                        "Key algorithm of EdDSA PrivateKey instance provided by " + pk.getClass()
                                + " is not clear. Expected Ed25519 or Ed448, but got EdDSA. "
                                + "Try a different security provider.");
            case "RSASSA-PSS":
                if (params != null && !(params instanceof RSASSAPSSMechanismParams)) {
                    throw new IllegalArgumentException("Expected RSASSA-PSS parameters; got " + params);
                }
                if (params == null) {
                    this.parameters = RSASSAPSSMechanismParams.createForDigestAlgorithm(hashAlgorithm);
                } else {
                    this.parameters = params;
                }
                break;
            default:
                this.parameters = null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getDigestAlgorithmName() {
        return hashAlgorithm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getSignatureAlgorithmName() {
        return signatureAlgorithm;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ISignatureMechanismParams getSignatureMechanismParameters() {
        return parameters;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public byte[] sign(byte[] message) throws GeneralSecurityException {
        String algorithm = getSignatureMechanismName();
        Signature sig;
        try {
            sig = SignUtils.getSignatureHelper(algorithm, provider);
            if (parameters != null) {
                parameters.apply(sig);
            }
            sig.initSign(pk);
            sig.update(message);
            return sig.sign();
        } catch (Exception ignored) {
            try {
                sig = SignUtils.getSignatureHelper(getSignatureAlgorithmName(), provider);
                if (parameters != null) {
                    parameters.apply(sig);
                }
                sig.initSign(pk);
                sig.update(message);
                return sig.sign();
            } catch (Exception e) {
                throw new PdfException(MessageFormatUtil.format(
                        SignExceptionMessageConstant.ALGORITHMS_NOT_SUPPORTED, algorithm, getSignatureAlgorithmName()),
                        e);
            }
        }
    }

    private String getSignatureMechanismName() {
        final String signatureAlgo = this.getSignatureAlgorithmName();
        // RSASSA-PSS is parameterised
        if ("RSASSA-PSS".equals(signatureAlgo)) {
            return signatureAlgo;
        } 
        return getDigestAlgorithmName() + "with" + getSignatureAlgorithmName();
    }
}
