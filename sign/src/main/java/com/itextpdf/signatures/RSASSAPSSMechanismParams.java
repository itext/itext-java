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

import com.itextpdf.bouncycastleconnector.BouncyCastleFactoryCreator;
import com.itextpdf.commons.bouncycastle.IBouncyCastleFactory;
import com.itextpdf.commons.bouncycastle.asn1.IASN1Encodable;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.kernel.crypto.DigestAlgorithms;
import com.itextpdf.kernel.exceptions.PdfException;

import java.security.InvalidAlgorithmParameterException;
import java.security.Signature;


/**
 * Encode the signer's parameters for producing an RSASSA-PSS signature. Note that this class
 * is intended for use in the signing process only, so it does not need to be able to represent all possible
 * parameter configurations; only the ones we consider reasonable. For the purposes of this class,
 * the mask generation function is always MGF1, and the associated digest function is the same as the digest
 * function used in the signing process.
 */
public class RSASSAPSSMechanismParams implements IApplicableSignatureParams {

    /**
     * Default value of the trailer field parameter.
     */
    public static final int DEFAULT_TRAILER_FIELD = 1;

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();
    private final IASN1ObjectIdentifier digestAlgoOid;
    private final int saltLen;
    private final int trailerField;

    /**
     * Instantiate RSASSA-PSS parameters with MGF1 for a given digest algorithm OID, salt length
     * and trailer field value.
     *
     * @param digestAlgoOid the digest algorithm OID that will be used for both the digest and MGF
     * @param saltLen       the salt length
     * @param trailerField  the trailer field
     */
    public RSASSAPSSMechanismParams(IASN1ObjectIdentifier digestAlgoOid, int saltLen, int trailerField) {
        this.digestAlgoOid = digestAlgoOid;
        this.saltLen = saltLen;
        this.trailerField = trailerField;
    }

    /**
     * Instantiate RSASSA-PSS parameters with MGF1 for the given algorithm name.
     *
     * @param digestAlgorithmName  the name of the digest algorithm
     *
     * @return RSASSA-PSS parameters with MGF1 for the given algorithm name.
     */
    public static RSASSAPSSMechanismParams createForDigestAlgorithm(String digestAlgorithmName) {
        String oid = DigestAlgorithms.getAllowedDigest(digestAlgorithmName);
        IASN1ObjectIdentifier oidWrapper = FACTORY.createASN1ObjectIdentifier(oid);
        int bitLen = DigestAlgorithms.getOutputBitLength(digestAlgorithmName);
        // default saltLen to the digest algorithm's output length in bytes
        return new RSASSAPSSMechanismParams(oidWrapper, bitLen / 8, DEFAULT_TRAILER_FIELD);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IASN1Encodable toEncodable() {
        return FACTORY.createRSASSAPSSParamsWithMGF1(this.digestAlgoOid, this.saltLen, this.trailerField);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void apply(Signature signature) {
        try {
            SignUtils.setRSASSAPSSParamsWithMGF1(
                    signature, DigestAlgorithms.getDigest(this.digestAlgoOid.getId()), this.saltLen, this.trailerField
            );
        } catch (InvalidAlgorithmParameterException e) {
            throw new PdfException(e);
        }
    }
}
