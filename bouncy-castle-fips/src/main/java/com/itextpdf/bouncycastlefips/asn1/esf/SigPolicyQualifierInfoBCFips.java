package com.itextpdf.bouncycastlefips.asn1.esf;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifierInfo;
import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;

public class SigPolicyQualifierInfoBCFips extends ASN1EncodableBCFips implements ISigPolicyQualifierInfo {
    public SigPolicyQualifierInfoBCFips(SigPolicyQualifierInfo qualifierInfo) {
        super(qualifierInfo);
    }

    public SigPolicyQualifierInfo getQualifierInfo() {
        return (SigPolicyQualifierInfo) getEncodable();
    }
}
