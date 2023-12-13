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

import com.itextpdf.io.codec.Base64;
import org.bouncycastle.asn1.ASN1ObjectIdentifier;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.esf.OtherHashAlgAndValue;
import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;
import org.bouncycastle.asn1.esf.SignaturePolicyId;
import org.bouncycastle.asn1.esf.SignaturePolicyIdentifier;
import org.bouncycastle.asn1.pkcs.PKCSObjectIdentifiers;
import org.bouncycastle.asn1.x509.AlgorithmIdentifier;

/**
 * Class that encapsulates the signature policy information
 *
 * Sample:
 *
 *      SignaturePolicyInfo spi = new SignaturePolicyInfo("2.16.724.1.3.1.1.2.1.9",
 *      "G7roucf600+f03r/o0bAOQ6WAs0=", "SHA-1", "https://sede.060.gob.es/politica_de_firma_anexo_1.pdf");
 */
public class SignaturePolicyInfo {
    private String policyIdentifier;
    private byte[] policyHash;
    private String policyDigestAlgorithm;
    private String policyUri;

    /**
     * Constructs a new {@link SignaturePolicyInfo} instance
     * @param policyIdentifier the id of the signature policy
     * @param policyHash the hash of the signature policy
     * @param policyDigestAlgorithm the digestion algorithm of the signature policy
     * @param policyUri the uri of the full policy description
     */
    public SignaturePolicyInfo(String policyIdentifier, byte[] policyHash, String policyDigestAlgorithm, String policyUri) {
        if (policyIdentifier == null || policyIdentifier.length() == 0) {
            throw new IllegalArgumentException("Policy identifier cannot be null");
        }
        if (policyHash == null) {
            throw new IllegalArgumentException("Policy hash cannot be null");
        }
        if (policyDigestAlgorithm == null || policyDigestAlgorithm.length() == 0) {
            throw new IllegalArgumentException("Policy digest algorithm cannot be null");
        }

        this.policyIdentifier = policyIdentifier;
        this.policyHash = policyHash;
        this.policyDigestAlgorithm = policyDigestAlgorithm;
        this.policyUri = policyUri;
    }

    /**
     * Constructs a new {@link SignaturePolicyInfo} instance
     * @param policyIdentifier the id of the signature policy
     * @param policyHashBase64 the Base64 presentation of the hash of the signature policy
     * @param policyDigestAlgorithm the digestion algorithm of the signature policy
     * @param policyUri the uri of the full policy description
     */
    public SignaturePolicyInfo(String policyIdentifier, String policyHashBase64, String policyDigestAlgorithm, String policyUri) {
        this(policyIdentifier, policyHashBase64 != null ? Base64.decode(policyHashBase64) : null, policyDigestAlgorithm, policyUri);
    }

    public String getPolicyIdentifier() {
        return policyIdentifier;
    }

    public byte[] getPolicyHash() {
        return policyHash;
    }

    public String getPolicyDigestAlgorithm() {
        return policyDigestAlgorithm;
    }

    public String getPolicyUri() {
        return policyUri;
    }

    SignaturePolicyIdentifier toSignaturePolicyIdentifier() {
        String algId = DigestAlgorithms.getAllowedDigest(this.policyDigestAlgorithm);

        if (algId == null || algId.length() == 0) {
            throw new IllegalArgumentException("Invalid policy hash algorithm");
        }

        SignaturePolicyIdentifier signaturePolicyIdentifier = null;
        SigPolicyQualifierInfo spqi = null;

        if (this.policyUri != null && this.policyUri.length() > 0) {
            spqi = new SigPolicyQualifierInfo(PKCSObjectIdentifiers.id_spq_ets_uri, new DERIA5String(this.policyUri));
        }

        signaturePolicyIdentifier = new SignaturePolicyIdentifier(new SignaturePolicyId(ASN1ObjectIdentifier
                .getInstance(new ASN1ObjectIdentifier(this.policyIdentifier.replace("urn:oid:", ""))),
                new OtherHashAlgAndValue(new AlgorithmIdentifier(new ASN1ObjectIdentifier(algId)),
                        new DEROctetString(this.policyHash)), SignUtils.createSigPolicyQualifiers(spqi)));

        return signaturePolicyIdentifier;
    }
}
