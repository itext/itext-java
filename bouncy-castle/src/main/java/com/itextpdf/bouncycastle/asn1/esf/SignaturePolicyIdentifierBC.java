package com.itextpdf.bouncycastle.asn1.esf;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyId;
import com.itextpdf.commons.bouncycastle.asn1.esf.ISignaturePolicyIdentifier;

import org.bouncycastle.asn1.esf.SignaturePolicyIdentifier;

public class SignaturePolicyIdentifierBC extends ASN1EncodableBC implements ISignaturePolicyIdentifier {
    public SignaturePolicyIdentifierBC(SignaturePolicyIdentifier signaturePolicyIdentifier) {
        super(signaturePolicyIdentifier);
    }
    
    public SignaturePolicyIdentifierBC(ISignaturePolicyId signaturePolicyId) {
        this(new SignaturePolicyIdentifier(((SignaturePolicyIdBC) signaturePolicyId).getSignaturePolicyId()));
    }

    public SignaturePolicyIdentifier getSignaturePolicyIdentifier() {
        return (SignaturePolicyIdentifier) getEncodable();
    }
}
