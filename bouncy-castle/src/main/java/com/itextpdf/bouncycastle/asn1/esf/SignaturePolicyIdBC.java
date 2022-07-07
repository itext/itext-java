package com.itextpdf.bouncycastle.asn1.esf;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.bouncycastle.asn1.ASN1ObjectIdentifierBC;
import com.itextpdf.commons.bouncycastle.asn1.IASN1ObjectIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.esf.IOtherHashAlgAndValue;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISigPolicyQualifiers;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyId;

import org.bouncycastle.asn1.esf.SignaturePolicyId;

public class SignaturePolicyIdBC extends ASN1EncodableBC implements ISignaturePolicyId {
    public SignaturePolicyIdBC(SignaturePolicyId signaturePolicyId) {
        super(signaturePolicyId);
    }

    public SignaturePolicyIdBC(IASN1ObjectIdentifier objectIdentifier,
            IOtherHashAlgAndValue algAndValue, ISigPolicyQualifiers policyQualifiers) {
        this(new SignaturePolicyId(
                ((ASN1ObjectIdentifierBC) objectIdentifier).getASN1ObjectIdentifier(),
                ((OtherHashAlgAndValueBC) algAndValue).getOtherHashAlgAndValue(),
                ((SigPolicyQualifiersBC) policyQualifiers).getSigPolityQualifiers()));
    }

    public SignaturePolicyId getSignaturePolicyId() {
        return (SignaturePolicyId) getEncodable();
    }
}
