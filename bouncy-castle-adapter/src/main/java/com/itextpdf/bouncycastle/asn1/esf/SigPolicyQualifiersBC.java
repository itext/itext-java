package com.itextpdf.bouncycastle.asn1.esf;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifiers;

import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;
import org.bouncycastle.asn1.esf.SigPolicyQualifiers;

public class SigPolicyQualifiersBC extends ASN1EncodableBC implements ISigPolicyQualifiers {
    public SigPolicyQualifiersBC(SigPolicyQualifiers policyQualifiers) {
        super(policyQualifiers);
    }

    public SigPolicyQualifiersBC(SigPolicyQualifierInfo... qualifierInfo) {
        super(new SigPolicyQualifiers(qualifierInfo));
    }

    public SigPolicyQualifiers getSigPolityQualifiers() {
        return (SigPolicyQualifiers) getEncodable();
    }
}
