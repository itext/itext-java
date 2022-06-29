package com.itextpdf.bouncycastle.asn1.esf;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifierInfo;
import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;

public class SigPolicyQualifierInfoBC extends ASN1EncodableBC implements ISigPolicyQualifierInfo {
    public SigPolicyQualifierInfoBC(SigPolicyQualifierInfo qualifierInfo) {
        super(qualifierInfo);
    }
    
    public SigPolicyQualifierInfo getQualifierInfo() {
        return (SigPolicyQualifierInfo) getEncodable();
    }
}
