package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAlgorithmIdentifier;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectPublicKeyInfo;

import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;

/**
 * Wrapper class for {@link SubjectPublicKeyInfo}.
 */
public class SubjectPublicKeyInfoBC extends ASN1EncodableBC implements ISubjectPublicKeyInfo {
    /**
     * Creates new wrapper instance for {@link SubjectPublicKeyInfo}.
     *
     * @param subjectPublicKeyInfo {@link SubjectPublicKeyInfo} to be wrapped
     */
    public SubjectPublicKeyInfoBC(SubjectPublicKeyInfo subjectPublicKeyInfo) {
        super(subjectPublicKeyInfo);
    }

    /**
     * Creates new wrapper instance for {@link SubjectPublicKeyInfo}.
     *
     * @param obj to get {@link SubjectPublicKeyInfo} instance to be wrapped
     */
    public SubjectPublicKeyInfoBC(Object obj) {
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
        return new AlgorithmIdentifierBC(getSubjectPublicKeyInfo().getAlgorithm());
    }
}
