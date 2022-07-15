package com.itextpdf.bouncycastle.asn1.x509;

import com.itextpdf.bouncycastle.asn1.ASN1EncodableBC;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAuthorityKeyIdentifier;

import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;

public class AuthorityKeyIdentifierBC extends ASN1EncodableBC implements IAuthorityKeyIdentifier {
    public AuthorityKeyIdentifierBC(AuthorityKeyIdentifier authorityKeyIdentifier) {
        super(authorityKeyIdentifier);
    }

    public AuthorityKeyIdentifier getAuthorityKeyIdentifier() {
        return (AuthorityKeyIdentifier) getEncodable();
    }
}
