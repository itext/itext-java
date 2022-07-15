package com.itextpdf.bouncycastle.asn1.esf;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifiers;

import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;
import org.bouncycastle.asn1.esf.SigPolicyQualifiers;

/**
 * Wrapper class for {@link SigPolicyQualifiers}.
 */
public class SigPolicyQualifiersBC extends ASN1EncodableBC implements ISigPolicyQualifiers {
    /**
     * Creates new wrapper instance for {@link SigPolicyQualifiers}.
     *
     * @param policyQualifiers {@link SigPolicyQualifiers} to be wrapped
     */
    public SigPolicyQualifiersBC(SigPolicyQualifiers policyQualifiers) {
        super(policyQualifiers);
    }

    /**
     * Creates new wrapper instance for {@link SigPolicyQualifiers}.
     *
     * @param qualifierInfo SigPolicyQualifierInfo array
     */
    public SigPolicyQualifiersBC(SigPolicyQualifierInfo... qualifierInfo) {
        super(new SigPolicyQualifiers(qualifierInfo));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SigPolicyQualifiers}.
     */
    public SigPolicyQualifiers getSigPolityQualifiers() {
        return (SigPolicyQualifiers) getEncodable();
    }
}
