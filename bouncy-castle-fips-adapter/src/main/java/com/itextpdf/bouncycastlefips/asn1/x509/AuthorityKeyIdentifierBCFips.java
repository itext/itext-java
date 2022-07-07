package com.itextpdf.bouncycastlefips.asn1.x509;

import com.itextpdf.bouncycastlefips.asn1.ASN1EncodableBCFips;
import com.itextpdf.commons.bouncycastle.asn1.x509.IAuthorityKeyIdentifier;

import org.bouncycastle.asn1.x509.AuthorityKeyIdentifier;

public class AuthorityKeyIdentifierBCFips extends ASN1EncodableBCFips implements IAuthorityKeyIdentifier {
    public AuthorityKeyIdentifierBCFips(AuthorityKeyIdentifier authorityKeyIdentifier) {
        super(authorityKeyIdentifier);
    }

    public AuthorityKeyIdentifier getAuthorityKeyIdentifier() {
        return (AuthorityKeyIdentifier) getEncodable();
    }
}
