package com.itextpdf.bouncycastlefips.asn1.esf;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifiers;

import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;
import org.bouncycastle.asn1.esf.SigPolicyQualifiers;

public class SigPolicyQualifiersBCFips extends ASN1EncodableBCFips implements ISigPolicyQualifiers {
    public SigPolicyQualifiersBCFips(SigPolicyQualifiers policyQualifiers) {
        super(policyQualifiers);
    }

    public SigPolicyQualifiersBCFips(SigPolicyQualifierInfo... qualifierInfo) {
        super(new SigPolicyQualifiers(qualifierInfo));
    }

    public SigPolicyQualifiers getSigPolityQualifiers() {
        return (SigPolicyQualifiers) getEncodable();
    }
}
