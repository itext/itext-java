package com.itextpdf.signatures;

import com.itextpdf.io.codec.Base64;
import org.bouncycastle.asn1.DERIA5String;
import org.bouncycastle.asn1.DERObjectIdentifier;
import org.bouncycastle.asn1.DEROctetString;
import org.bouncycastle.asn1.esf.*;
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
        SigPolicyQualifiers qualifiers = new SigPolicyQualifiers(new SigPolicyQualifierInfo[] {spqi});

        signaturePolicyIdentifier = new SignaturePolicyIdentifier(new SignaturePolicyId(DERObjectIdentifier.getInstance(new DERObjectIdentifier(this.policyIdentifier.replace("urn:oid:", ""))),
                new OtherHashAlgAndValue(new AlgorithmIdentifier(algId), new DEROctetString(this.policyHash)), qualifiers));

        return signaturePolicyIdentifier;
    }
}
