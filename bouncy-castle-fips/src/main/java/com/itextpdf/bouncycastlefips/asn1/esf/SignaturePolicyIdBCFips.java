package com.itextpdf.bouncycastlefips.asn1.esf;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.bouncycastlefips.asn1.ASN1ObjectIdentifierBCFips;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.esf.IOtherHashAlgAndValue;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifiers;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyId;

import org.bouncycastle.asn1.esf.SignaturePolicyId;

public class SignaturePolicyIdBCFips extends ASN1EncodableBCFips implements ISignaturePolicyId {
    public SignaturePolicyIdBCFips(SignaturePolicyId signaturePolicyId) {
        super(signaturePolicyId);
    }

    public SignaturePolicyIdBCFips(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue, ISigPolicyQualifiers policyQualifiers) {
        this(new SignaturePolicyId(
                ((ASN1ObjectIdentifierBCFips) objectIdentifier).getASN1ObjectIdentifier(),
                ((OtherHashAlgAndValueBCFips) algAndValue).getOtherHashAlgAndValue(),
                ((SigPolicyQualifiersBCFips) policyQualifiers).getSigPolityQualifiers()));
    }

    public SignaturePolicyId getSignaturePolicyId() {
        return (SignaturePolicyId) getEncodable();
    }
}
