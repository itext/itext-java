package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAuthorityKeyIdentifier;

import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;

/**
 * Wrapper class for {@link AuthorityKeyIdentifier}.
 */
public class AuthorityKeyIdentifierBCFips extends ASN1EncodableBCFips implements IAuthorityKeyIdentifier {
    /**
     * Creates new wrapper instance for {@link AuthorityKeyIdentifier}.
     *
     * @param authorityKeyIdentifier {@link AuthorityKeyIdentifier} to be wrapped
     */
    public AuthorityKeyIdentifierBCFips(AuthorityKeyIdentifier authorityKeyIdentifier) {
        super(authorityKeyIdentifier);
    }

    /**
     * Gets actual org.bouncycastle object being wrapped.
     *
     * @return wrapped {@link AuthorityKeyIdentifier}.
     */
    public AuthorityKeyIdentifier getAuthorityKeyIdentifier() {
        return (AuthorityKeyIdentifier) getEncodable();
    }
}
