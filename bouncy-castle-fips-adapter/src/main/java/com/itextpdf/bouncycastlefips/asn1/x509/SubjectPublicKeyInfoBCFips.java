package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectPublicKeyInfo;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

/**
 * Wrapper class for {@link SubjectPublicKeyInfo}.
 */
public class SubjectPublicKeyInfoBCFips extends ASN1EncodableBCFips implements ISubjectPublicKeyInfo {
    /**
     * Creates new wrapper instance for {@link SubjectPublicKeyInfo}.
     *
     * @param subjectPublicKeyInfo {@link SubjectPublicKeyInfo} to be wrapped
     */
    public SubjectPublicKeyInfoBCFips(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        super(subjectPublicKeyInfo);
    }

    /**
     * Creates new wrapper instance for {@link SubjectPublicKeyInfo}.
     *
     * @param obj to get {@link SubjectPublicKeyInfo} instance to be wrapped
     */
    public SubjectPublicKeyInfoBCFips(Object obj) {
        super(SubjectPublicKeyInfo.getInstance(obj));
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SubjectPublicKeyInfo}.
     */
    public SubjectPublicKeyInfo getSubjectPublicKeyInfo() {
        return (SubjectPublicKeyInfo) getEncodable();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IAlgorithmIdentifier getAlgorithm() {
        return new AlgorithmIdentifierBCFips(getSubjectPublicKeyInfo().getAlgorithm());
    }
}
