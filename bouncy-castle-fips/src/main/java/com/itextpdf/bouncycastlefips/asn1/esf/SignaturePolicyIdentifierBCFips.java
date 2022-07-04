package com.itextpdf.bouncycastlefips.asn1.esf;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyId;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyIdentifier;

import org.bouncycastle.asn1.esf.SignaturePolicyIdentifier;

public class SignaturePolicyIdentifierBCFips extends ASN1EncodableBCFips implements ISignaturePolicyIdentifier {
    public SignaturePolicyIdentifierBCFips(SignaturePolicyIdentifier signaturePolicyIdentifier) {
        super(signaturePolicyIdentifier);
    }

    public SignaturePolicyIdentifierBCFips(ISignaturePolicyId signaturePolicyId) {
        this(new SignaturePolicyIdentifier(((SignaturePolicyIdBCFips) signaturePolicyId).getSignaturePolicyId()));
    }

    public SignaturePolicyIdentifier getSignaturePolicyIdentifier() {
        return (SignaturePolicyIdentifier) getEncodable();
    }
}
