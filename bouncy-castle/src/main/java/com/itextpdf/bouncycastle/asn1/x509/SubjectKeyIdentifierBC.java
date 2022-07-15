package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectKeyIdentifier;

import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;

public class SubjectKeyIdentifierBC extends ASN1EncodableBC implements ISubjectKeyIdentifier {
    public SubjectKeyIdentifierBC(SubjectKeyIdentifier keyIdentifier) {
        super(keyIdentifier);
    }

    public SubjectKeyIdentifier getSubjectKeyIdentifier() {
        return (SubjectKeyIdentifier) getEncodable();
    }
}
