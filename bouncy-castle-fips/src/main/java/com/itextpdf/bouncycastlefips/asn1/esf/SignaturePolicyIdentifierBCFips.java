package com.itextpdf.bouncycastlefips.asn1.esf;

import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyIdentifier;

import org.bouncycastle.asn1.esf.SignaturePolicyIdentifier;

public class SignaturePolicyIdentifierBCFips implements ISignaturePolicyIdentifier {
    private final SignaturePolicyIdentifier signaturePolicyIdentifier;

    public SignaturePolicyIdentifierBCFips(SignaturePolicyIdentifier signaturePolicyIdentifier) {
        this.signaturePolicyIdentifier = signaturePolicyIdentifier;
    }

    public SignaturePolicyIdentifier getSignaturePolicyIdentifier() {
        return signaturePolicyIdentifier;
    }
}
