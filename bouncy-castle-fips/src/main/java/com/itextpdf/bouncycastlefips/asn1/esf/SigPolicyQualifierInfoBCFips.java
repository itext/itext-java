package com.itextpdf.bouncycastlefips.asn1.esf;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.bouncycastlefips.asn1.DERIA5StringBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IDERIA5String;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifierInfo;

import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;

public class SigPolicyQualifierInfoBCFips extends ASN1EncodableBCFips implements ISigPolicyQualifierInfo {
    public SigPolicyQualifierInfoBCFips(SigPolicyQualifierInfo qualifierInfo) {
        super(qualifierInfo);
    }

    public SigPolicyQualifierInfoBCFips(IASN1ObjectIdentifier objectIdentifier, IDERIA5String string) {
        this(new SigPolicyQualifierInfo(
                ((ASN1ObjectIdentifierBCFips) objectIdentifier).getASN1ObjectIdentifier(),
                ((DERIA5StringBCFips) string).getDerIA5String()));
    }

    public SigPolicyQualifierInfo getQualifierInfo() {
        return (SigPolicyQualifierInfo) getEncodable();
    }
}
