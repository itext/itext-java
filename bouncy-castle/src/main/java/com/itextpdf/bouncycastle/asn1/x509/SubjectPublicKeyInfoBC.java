package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectPublicKeyInfo;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class SubjectPublicKeyInfoBC extends ASN1EncodableBC implements ISubjectPublicKeyInfo {
    public SubjectPublicKeyInfoBC(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        super(subjectPublicKeyInfo);
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return (SubjectPublicKeyInfo) getEncodable();
    }

    @Override
    public IAlgorithmIdentifier getAlgorithm() {
        return new AlgorithmIdentifierBC(getSubjectPublicKeyInfo().getAlgorithm());
    }
}
