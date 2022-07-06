package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectPublicKeyInfo;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

public class SubjectPublicKeyInfoBCFips extends ASN1EncodableBCFips implements ISubjectPublicKeyInfo {
    public SubjectPublicKeyInfoBCFips(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        super(subjectPublicKeyInfo);
    }

    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return (SubjectPublicKeyInfo) getEncodable();
    }

    @Override
    public IAlgorithmIdentifier getAlgorithm() {
        return new AlgorithmIdentifierBCFips(getSubjectPublicKeyInfo().getAlgorithm());
    }
}
