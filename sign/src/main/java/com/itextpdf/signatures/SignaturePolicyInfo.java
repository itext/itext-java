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
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.esf.IOtherHashAlgAndValue;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifierInfo;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyId;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyIdentifier;
import com.itextpdf.commons.utils.Base64;

/**
 * Class that encapsulates the signature policy information
 * 
 * <p>
 * Sample:
 * 
 * <p>
 * SignaturePolicyInfo spi = new SignaturePolicyInfo("2.16.724.1.3.1.1.2.1.9",
 * "G7roucf600+f03r/o0bAOQ6WAs0=", "SHA-1", "https://sede.060.gob.es/politica_de_firma_anexo_1.pdf");
 */
public class SignaturePolicyInfo {

    private static final IBouncyCastleFactory FACTORY = BouncyCastleFactoryCreator.getFactory();

    private final String policyIdentifier;
    private final byte[] policyHash;
    private final String policyDigestAlgorithm;
    private final String policyUri;

    /**
     * Constructs a new {@link SignaturePolicyInfo} instance
     *
     * @param policyIdentifier      the id of the signature policy
     * @param policyHash            the hash of the signature policy
     * @param policyDigestAlgorithm the digestion algorithm of the signature policy
     * @param policyUri             the uri of the full policy description
     */
    public SignaturePolicyInfo(String policyIdentifier, byte[] policyHash, String policyDigestAlgorithm,
            String policyUri) {
        if (policyIdentifier == null || policyIdentifier.isEmpty()) {
            throw new IllegalArgumentException("Policy identifier cannot be null");
        }
        if (policyHash == null) {
            throw new IllegalArgumentException("Policy hash cannot be null");
        }
        if (policyDigestAlgorithm == null || policyDigestAlgorithm.isEmpty()) {
            throw new IllegalArgumentException("Policy digest algorithm cannot be null");
        }

        this.policyIdentifier = policyIdentifier;
        this.policyHash = policyHash;
        this.policyDigestAlgorithm = policyDigestAlgorithm;
        this.policyUri = policyUri;
    }

    /**
     * Constructs a new {@link SignaturePolicyInfo} instance
     *
     * @param policyIdentifier      the id of the signature policy
     * @param policyHashBase64      the Base64 presentation of the hash of the signature policy
     * @param policyDigestAlgorithm the digestion algorithm of the signature policy
     * @param policyUri             the uri of the full policy description
     */
    public SignaturePolicyInfo(String policyIdentifier, String policyHashBase64, String policyDigestAlgorithm,
            String policyUri) {
        this(policyIdentifier, policyHashBase64 != null ? Base64.decode(policyHashBase64) : null,
                policyDigestAlgorithm, policyUri);
    }

    /**
     * Get the ID of the signature policy.
     *
     * @return the ID of the signature policy
     */
    public String getPolicyIdentifier() {
        return policyIdentifier;
    }

    /**
     * Get the hash of the signature policy.
     *
     * @return the hash of the signature policy
     */
    public byte[] getPolicyHash() {
        return policyHash;
    }

    /**
     * Get the digestion algorithm of the signature policy.
     *
     * @return the digestion algorithm of the signature policy
     */
    public String getPolicyDigestAlgorithm() {
        return policyDigestAlgorithm;
    }

    /**
     * Get the uri of the full policy description.
     *
     * @return the uri of the full policy description
     */
    public String getPolicyUri() {
        return policyUri;
    }

    ISignaturePolicyIdentifier toSignaturePolicyIdentifier() {
        String algId = DigestAlgorithms.getAllowedDigest(this.policyDigestAlgorithm);

        if (algId == null || algId.isEmpty()) {
            throw new IllegalArgumentException("Invalid policy hash algorithm");
        }
        ISigPolicyQualifierInfo spqi = null;

        if (this.policyUri != null && !this.policyUri.isEmpty()) {
            spqi = FACTORY.createSigPolicyQualifierInfo(FACTORY.createPKCSObjectIdentifiers().getIdSpqEtsUri(),
                    FACTORY.createDERIA5String(this.policyUri));
        }

        IASN1ObjectIdentifier identifier = FACTORY.createASN1ObjectIdentifierInstance(
                FACTORY.createASN1ObjectIdentifier(this.policyIdentifier.replace("urn:oid:", "")));
        IOtherHashAlgAndValue otherHashAlgAndValue = FACTORY.createOtherHashAlgAndValue(
                FACTORY.createAlgorithmIdentifier(FACTORY.createASN1ObjectIdentifier(algId)),
                FACTORY.createDEROctetString(this.policyHash));
        ISignaturePolicyId signaturePolicyId = FACTORY.createSignaturePolicyId(identifier, otherHashAlgAndValue, spqi);

        return FACTORY.createSignaturePolicyIdentifier(signaturePolicyId);
    }
}
