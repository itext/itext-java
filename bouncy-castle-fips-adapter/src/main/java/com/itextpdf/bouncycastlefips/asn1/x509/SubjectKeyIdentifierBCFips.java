package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectKeyIdentifier;

import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;

/**
 * Wrapper class for {@link SubjectKeyIdentifier}.
 */
public class SubjectKeyIdentifierBCFips extends ASN1EncodableBCFips implements ISubjectKeyIdentifier {
    /**
     * Creates new wrapper instance for {@link SubjectKeyIdentifier}.
     *
     * @param keyIdentifier {@link SubjectKeyIdentifier} to be wrapped
     */
    public SubjectKeyIdentifierBCFips(SubjectKeyIdentifier keyIdentifier) {
        super(keyIdentifier);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link SubjectKeyIdentifier}.
     */
    public SubjectKeyIdentifier getSubjectKeyIdentifier() {
        return (SubjectKeyIdentifier) getEncodable();
    }
}
