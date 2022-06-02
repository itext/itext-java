package com.itextpdf.bouncycastle.asn1.esf;

import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyIdentifier;

import org.bouncycastle.asn1.esf.SignaturePolicyIdentifier;

public class SignaturePolicyIdentifierBC implements ISignaturePolicyIdentifier {
    private final SignaturePolicyIdentifier signaturePolicyIdentifier;

    public SignaturePolicyIdentifierBC(SignaturePolicyIdentifier signaturePolicyIdentifier) {
        this.signaturePolicyIdentifier = signaturePolicyIdentifier;
    }

    public SignaturePolicyIdentifier getSignaturePolicyIdentifier() {
        return signaturePolicyIdentifier;
    }
}
