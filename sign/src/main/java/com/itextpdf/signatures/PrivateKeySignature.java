/*
 *
 * This file is part of the iText (R) project.
    Copyright (c) 1998-2023 iText Group NV
 * Authors: Bruno Lowagie, Paulo Soares, et al.
 *
 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License version 3
 * as published by the Free Software Foundation with the addition of the
 * following permission added to Section 15 as permitted in Section 7(a):
 * FOR ANY PART OF THE COVERED WORK IN WHICH THE COPYRIGHT IS OWNED BY
 * ITEXT GROUP. ITEXT GROUP DISCLAIMS THE WARRANTY OF NON INFRINGEMENT
 * OF THIRD PARTY RIGHTS
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU Affero General Public License for more details.
 * You should have received a copy of the GNU Affero General Public License
 * along with this program; if not, see http://www.gnu.org/licenses or write to
 * the Free Software Foundation, Inc., 51 Franklin Street, Fifth Floor,
 * Boston, MA, 02110-1301 USA, or download the license from the following URL:
 * http://itextpdf.com/terms-of-use/
 *
 * The interactive user interfaces in modified source and object code versions
 * of this program must display Appropriate Legal Notices, as required under
 * Section 5 of the GNU Affero General Public License.
 *
 * In accordance with Section 7(b) of the GNU Affero General Public License,
 * a covered work must retain the producer line in every PDF that is created
 * or manipulated using iText.
 *
 * You can be released from the requirements of the license by purchasing
 * a commercial license. Buying such a license is mandatory as soon as you
 * develop commercial activities involving the iText software without
 * disclosing the source code of your own applications.
 * These activities include: offering paid services to customers as an ASP,
 * serving PDFs on the fly in a web application, shipping iText with a closed
 * source product.
 *
 * For more information, please contact iText Software Corp. at this
 * address: sales@itextpdf.com
 */
package com.itextpdf.signatures;

import com.itextpdf.kernel.exceptions.PdfException;
import com.itextpdf.signatures.exceptions.SignExceptionMessageConstant;

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
                if (!SecurityIDs.ID_SHA512.equals(digestAlgorithmOid)) {
                    throw new PdfException(SignExceptionMessageConstant.ALGO_REQUIRES_SPECIFIC_HASH)
                            .setMessageParams("Ed25519", "SHA-512", this.hashAlgorithm);
                }
                this.parameters = null;
                break;
            case "Ed448":
                if (!SecurityIDs.ID_SHAKE256.equals(digestAlgorithmOid)) {
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
        Signature sig = SignUtils.getSignatureHelper(algorithm, provider);
        if (parameters != null) {
            parameters.apply(sig);
        }
        sig.initSign(pk);
        sig.update(message);
        return sig.sign();
    }

    private String getSignatureMechanismName() {
        final String signatureAlgo = this.getSignatureAlgorithmName();
        // Ed25519 and Ed448 do not involve a choice of hashing algorithm
        // and RSASSA-PSS is parameterised
        if ("Ed25519".equals(signatureAlgo) || "Ed448".equals(signatureAlgo)
                || "RSASSA-PSS".equals(signatureAlgo)) {
            return signatureAlgo;
        } else {
            return getDigestAlgorithmName() + "with" + getSignatureAlgorithmName();
        }
    }
}
