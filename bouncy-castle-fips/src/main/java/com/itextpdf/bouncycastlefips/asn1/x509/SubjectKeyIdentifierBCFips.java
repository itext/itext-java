package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectKeyIdentifier;

import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;

public class SubjectKeyIdentifierBCFips extends ASN1EncodableBCFips implements ISubjectKeyIdentifier {
    public SubjectKeyIdentifierBCFips(SubjectKeyIdentifier keyIdentifier) {
        super(keyIdentifier);
    }

    public SubjectKeyIdentifier getSubjectKeyIdentifier() {
        return (SubjectKeyIdentifier) getEncodable();
    }
}
