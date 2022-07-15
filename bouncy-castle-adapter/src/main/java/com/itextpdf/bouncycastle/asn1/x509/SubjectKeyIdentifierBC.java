package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.ISubjectKeyIdentifier;

import org.bouncycastle.asn1.x509.SubjectKeyIdentifier;

/**
 * Wrapper class for {@link SubjectKeyIdentifier}.
 */
public class SubjectKeyIdentifierBC extends ASN1EncodableBC implements ISubjectKeyIdentifier {
    /**
     * Creates new wrapper instance for {@link SubjectKeyIdentifier}.
     *
     * @param keyIdentifier {@link SubjectKeyIdentifier} to be wrapped
     */
    public SubjectKeyIdentifierBC(SubjectKeyIdentifier keyIdentifier) {
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
