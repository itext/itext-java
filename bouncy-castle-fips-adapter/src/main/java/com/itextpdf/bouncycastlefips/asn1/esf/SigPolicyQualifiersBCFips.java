package com.itextpdf.bouncycastlefips.asn1.esf;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifiers;

import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;
import org.bouncycastle.asn1.esf.SigPolicyQualifiers;

/**
 * Wrapper class for {@link SigPolicyQualifiers}.
 */
public class SigPolicyQualifiersBCFips extends ASN1EncodableBCFips implements ISigPolicyQualifiers {
    /**
     * Creates new wrapper instance for {@link SigPolicyQualifiers}.
     *
     * @param policyQualifiers {@link SigPolicyQualifiers} to be wrapped
     */
    public SigPolicyQualifiersBCFips(SigPolicyQualifiers policyQualifiers) {
        super(policyQualifiers);
    }

    /**
     * Creates new wrapper instance for {@link SigPolicyQualifiers}.
     *
     * @param qualifierInfo SigPolicyQualifierInfo array
     */
    public SigPolicyQualifiersBCFips(SigPolicyQualifierInfo... qualifierInfo) {
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
