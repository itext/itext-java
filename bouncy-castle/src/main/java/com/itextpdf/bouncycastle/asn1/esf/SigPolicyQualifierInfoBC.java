package com.itextpdf.bouncycastle.asn1.esf;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.bouncycastle.asn1.DERIA5StringBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.IDERIA5String;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifierInfo;
import org.bouncycastle.asn1.esf.SigPolicyQualifierInfo;

public class SigPolicyQualifierInfoBC extends ASN1EncodableBC implements ISigPolicyQualifierInfo {
    public SigPolicyQualifierInfoBC(SigPolicyQualifierInfo qualifierInfo) {
        super(qualifierInfo);
    }
    
    public SigPolicyQualifierInfoBC(IASN1ObjectIdentifier objectIdentifier, IDERIA5String string) {
        this(new SigPolicyQualifierInfo(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getObjectIdentifier(),
                ((DERIA5StringBC) string).getDerIA5String()));
    }
    
    public SigPolicyQualifierInfo getQualifierInfo() {
        return (SigPolicyQualifierInfo) getEncodable();
    }
}
